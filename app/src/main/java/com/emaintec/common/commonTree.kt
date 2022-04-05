package com.emaintec.common


import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import com.emaintec.Define
import com.emaintec.Fragment_Base
import com.emaintec.Functions
import com.emaintec.common.helper.commonHelper
import com.emaintec.common.model.commonTreeModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.DeptTreeBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.leer.tree2view.TreeUtils
import moe.leer.tree2view.TreeView
import moe.leer.tree2view.module.DefaultTreeNode


/**
 * Created by yyc on 2/12/15.
 */
class commonTree : Fragment_Base()  {
    lateinit var binding: DeptTreeBinding
    var _defaultRoot :String ? = null
    var TREE_TYPE : String  = "DEPT"
    var TYPE_DESC : String  = ""
    private var lastSelectedNode: commonTreeModel? = null
    private var adapter: commonTreeAdapter? = null
    private var root: DefaultTreeNode<commonTreeModel?>? = null
    //<hash, count>
    private var clickCount = HashMap<Int, Int>()

    private var lastPress: Long = 0
    private var _listener: OnDialogEventListener? = null

    interface OnDialogEventListener {
        fun onEvent(message: commonTreeModel?)
    }
    fun setOnDialogEventListener(listener: OnDialogEventListener): commonTree {
        _listener = listener
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DeptTreeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState:Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initButton()
        initRoot()
        Emaintec.fragment = this
        dialog?.setOnKeyListener { _, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.action == KeyEvent.ACTION_UP)) {
                binding.buttonOk.performClick()
            }
            false
        }
    }
    private fun initRoot() {
        //expand root
        root =  DefaultTreeNode(commonTreeModel("",TYPE_DESC))
        root!!.removeAllChildren()
        root!!.isExpanded = true
        root!!.isSelectable = true

        //do not rm root node's children or add children
        clickCount.put(root.hashCode(), 1)

        val gson = Gson()
        val jobject = JsonObject()
        val array = JsonArray()
        jobject.addProperty("TREE_TYPE", TREE_TYPE)
        jobject.addProperty("SETTREE", if(_defaultRoot.isNullOrBlank()) "N" else "Y")
        jobject.addProperty("KEY",  if(_defaultRoot.isNullOrBlank()) "!" else _defaultRoot)
        jobject.addProperty("EQ_ORG_NO", "2000") //
        jobject.addProperty("LANG", Define.LANG)
        array.add(jobject)

        val jsonData = gson.toJson(array)
        NetworkProgress.start(activity!!)

        fun selfAdd(key:String, node: DefaultTreeNode<commonTreeModel>, dept:commonTreeModel)
        {
            val n = DefaultTreeNode<commonTreeModel>(dept)
            for (j in node.getChildren().indices) {
                val nextNode = node.getChildren().get(j) as DefaultTreeNode<commonTreeModel>
                //sb.append(nextNode.getElement().toString());
                if (nextNode.element.KEY == dept.P_KEY) {
                    nextNode.addChild(n)
                    nextNode.isExpanded = true//dept.CHILD!="0"
                    clickCount.put(nextNode.hashCode(), 1)
                }
                if (nextNode.isHasChildren) { //search next node
                    selfAdd(key, nextNode,dept)
                }
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            commonHelper.instance.getCommonTreeList({ success, list, msg ->
                if (success) {
                    launch(Dispatchers.Main) {
                        for (item in list!!) {
                            val n = DefaultTreeNode<commonTreeModel>(item)
                            n.isExpandable = item.CHILD != "0"
                            when (item.SHOW_LEVEL) {
                                "","1"   -> {
                                    root?.addChild(n)
                                    root?.isExpanded = true
                                }
                                else ->{
                                    selfAdd(item.P_KEY,root as DefaultTreeNode<commonTreeModel>,item)
                                }
                            }
                        }
                        initView()
                    }.join()
                    launch(Dispatchers.Main) {
//                        tree_view.performItemClick(tree_view,0,tree_view.getItemIdAtPosition(0))
                        onItemTreeClick( binding.treeView,null,0,binding.treeView.getItemIdAtPosition(0))
                        //선택된 아이템을 찾고 선택한다.
                        var nSel = 0
                        for(node in binding.treeView.treeAdapter.nodesList)
                        {
                            if((node.element as commonTreeModel).KEY.equals(_defaultRoot))  break
                            nSel++
                        }
                        if(!_defaultRoot.isNullOrBlank())
//                        tree_view.performItemClick(tree_view,nSel,tree_view.getItemIdAtPosition(nSel))
                        onItemTreeClick( binding.treeView,null,nSel,binding.treeView.getItemIdAtPosition(nSel))
                    }.join()

                } else {
                    launch(Dispatchers.Main) {
                        Functions.MessageBox(activity!!, msg)
                    }
                }
            }, jsonData)
            NetworkProgress.end()
        }

    }
    val TAG = "MOBINS_tree"

    fun onItemTreeClick(parent: AdapterView<*>, view: View?, position: Int, id: Long)
    {
        run {
            val nodes = adapter!!.nodesList
            //for (i in 0 until nodes.size) Log.i(TAG, "index: $i,node:" + (nodes[i].element as FileItem).name)
            //the click item
            val node: DefaultTreeNode<commonTreeModel> = nodes[position] as DefaultTreeNode<commonTreeModel>
            lastSelectedNode = node.element
            binding.treeView.treeAdapter.position = position
            val c = if (clickCount[node.hashCode()] == null) 0 else clickCount[node.hashCode()]
            clickCount.put(node.hashCode(), c!! + 1)
            //remove children
            if (node.isExpanded) {
                //count visible child before remove
                //-1 for discount itself
                val visibleCount = TreeUtils.getVisibleNodesD(node).size - 1
                node.isExpanded = false
                //only remove and add node when first time click.
                if (c == 0) {
                    node.removeAllChildren()
                }
                val offset = parent.firstVisiblePosition
                val start = position - offset + 1
                for (i in start..if (start + visibleCount < binding.treeView.childCount) start + visibleCount - 1 else binding.treeView.childCount) {
                    rmItemAnim(parent, i)
                }
            } else {
                //add children for this node
                node.isExpanded = true
                if (c == 0 && !node.element.CHILD.equals("0")) {
                    val gson = Gson()
                    val jsonobject = JsonObject()
                    val array = JsonArray()
                    jsonobject.addProperty("TREE_TYPE", TREE_TYPE)
                    jsonobject.addProperty("SETDEPT", "N")
                    jsonobject.addProperty("KEY", node.element.KEY) //Adapter_PartStockList.instance.count.toString()
                    jsonobject.addProperty("EQ_ORG_NO", "2000") //
                    jsonobject.addProperty("LANG", Define.LANG)
                    array.add(jsonobject)

                    val jsonData = gson.toJson(array)
                    NetworkProgress.start(activity!!)
                    CoroutineScope(Dispatchers.Default).launch {
                        commonHelper.instance.getCommonTreeList({ success, list, msg ->
                            if (success) {
                                launch(Dispatchers.Main) {
                                    for (item in list!!) {
                                        val n = DefaultTreeNode<commonTreeModel>(item)
                                        n.isExpandable = !item.CHILD.toString().equals("0")
                                        node.addChild(n)
                                    }
                                    //update view
                                    adapter!!.nodesList = TreeUtils.getVisibleNodesD(root)
                                    //tree_view.refresh(null)
                                    adapter!!.notifyDataSetChanged()
                                    //start animation
                                    val offset = parent.firstVisiblePosition
                                    val start = position - offset + 1
                                    val visibleCount = TreeUtils.getVisibleNodesD(node).size - 1
                                    for (i in start..if (start + visibleCount <= binding.treeView.childCount) (start + visibleCount - 1) else binding.treeView.childCount) {
                                        addItemAnim(parent, i)
                                    }
                                }.join()

                            } else {
                                launch(Dispatchers.Main) {
                                    Functions.MessageBox(activity!!, msg)
                                }
                            }
                        }, jsonData)
                        NetworkProgress.end()
                    }
                }else {
                    //refresh nodes
                    //nodes = TreeUtils.getVisibleNodesD(root)
                    //for (i in 0 until nodes.size) Log.i(TAG, "index: $i,node:" + (nodes[i].element as FileItem).name)
                    Log.d(TAG, "LOG_TreeView children: ${binding.treeView.childCount}")
                    //update view
                    adapter!!.nodesList = TreeUtils.getVisibleNodesD(root)
                    //tree_view.refresh(null)
                    adapter!!.notifyDataSetChanged()
                    //start animation
                    val offset = parent.firstVisiblePosition
                    val start = position - offset + 1
                    val visibleCount = TreeUtils.getVisibleNodesD(node).size - 1
                    for (i in start..if (start + visibleCount <= binding.treeView.childCount) (start + visibleCount - 1) else binding.treeView.childCount) {
                        addItemAnim(parent, i)
                    }
                    //adapter!!.root = root
                }
            }
        }
    }



    private fun  initView() {
        binding.treeView.requestLayout()
        adapter = commonTreeAdapter(activity, root, R.layout.dept_tree_item)
        binding.treeView.treeAdapter = adapter

        binding.treeView.setOnItemTabListener(object : TreeView.OnItemTabListener{
            override fun onDoubleTap(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val list = TreeUtils.getVisibleNodesD(root)
                lastSelectedNode = list[position].element as commonTreeModel
//            Functions.MessageBox(activity!!,"You selected " + lastSelectedNode!!.DESCRIPTION)
                _listener!!.onEvent(lastSelectedNode)
                dismiss()
            }

            override fun onSingleTap(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onItemTreeClick(parent!!,view,position,id)
            }

        });
        binding.treeView.setTreeItemSelectedListener { _, node, pos ->
            val list = TreeUtils.getVisibleNodesD(root)
            lastSelectedNode = list[pos].element as commonTreeModel
//            Functions.MessageBox(activity!!,"You selected " + lastSelectedNode!!.DESCRIPTION)
            _listener!!.onEvent(lastSelectedNode)
            dismiss()
            false
        }
        binding.treeView.visibility = View.VISIBLE
        binding.refreshView.isRefreshing = false
        binding.refreshView.isEnabled = false

    }

    private fun addItemAnim(parent: ViewGroup, index: Int) {
        val anim = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left)
        anim.duration = 500
        //if (tree_view.getChildAt(index) != null) {
        //  tree_view.getChildAt(index).startAnimation(anim)
        //}
        try {
            parent.getChildAt(index).startAnimation(anim)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Log.e(TAG, "null index: $index")
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                adapter!!.nodesList = TreeUtils.getVisibleNodesD(root)
                binding.treeView.refresh(null)
            }
            override fun onAnimationRepeat(animation: Animation) {
            }
        })
    }
    private fun refreshTree() {
        binding.treeView.visibility = View.GONE
        binding.treeView.treeAdapter.nodesList.clear()
        //adapter!!.notifyDataSetChanged()
        binding.treeView.refresh(null)
        initView()

    }
    private fun rmItemAnim(parent: ViewGroup, index: Int) {
        val anim = AnimationUtils.loadAnimation(activity, android.R.anim.slide_out_right)
        anim.duration = 500
        try {
            parent.getChildAt(index).startAnimation(anim)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Log.e(TAG, "null index: $index")
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                adapter!!.nodesList = TreeUtils.getVisibleNodesD(root)
                binding.treeView.refresh(null)
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
    }
    fun initButton(){
        val button_Ok = view!!.findViewById<View>(R.id.button_Ok)
        button_Ok.setOneClickListener {
            if (null != _listener) {
                _listener!!.onEvent(lastSelectedNode)
//                this@deptTreeFragment.dialog.hide()
            }
//            this@deptTreeFragment.dialog.hide() //
            dismiss()
        }
        binding.customLayoutTitle.imageButton.setOneClickListener {
            this.dismiss()
        }



    }



}
