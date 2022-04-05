package com.emaintec.common


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.emaintec.Fragment_Base
import com.emaintec.common.helper.commonHelper
import com.emaintec.common.model.codeModel
import com.emaintec.common.model.commonTreeModel
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.CommonOfflineTreeBinding
import com.ibetools.lscable.tree.IconTreeItemHolder
import com.ibetools.lscable.tree.ProfileHolder
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.model.TreeNode.TreeNodeClickListener
import com.unnamed.b.atv.view.AndroidTreeView



/**
 * Created by yyc on 2/12/20.
 */
class commonOfflineTree : Fragment_Base() {
    lateinit var binding: CommonOfflineTreeBinding
    private var tView: AndroidTreeView? = null
    private var selectionModeEnabled = true
    private var _listener: OnDialogEventListener? = null
    private var _Table = "TM_EQUIPMENT"
    var treeItem: commonTreeModel = commonTreeModel()

    interface OnDialogEventListener {
        fun onEvent(message: commonTreeModel)
    }

    fun setOnDialogEventListener(listener: OnDialogEventListener): commonOfflineTree {
        _listener = listener
        return this
    }

    fun setTable(table: String): commonOfflineTree {
        _Table = table
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CommonOfflineTreeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val containerView = rootView.findViewById<View>(R.id.containerTree) as ViewGroup
        val check = rootView.findViewById<View>(R.id.btn_checkSelection)


        check.setOneClickListener {
            if (!selectionModeEnabled) {
                Toast.makeText(activity, "Enable selection mode first", Toast.LENGTH_SHORT).show()
            } else {
                binding.textViewDeptNm.text = "";
                for (item in tView!!.selected) {
//                    Log.d("yyc",(item.value as IconTreeItemHolder.IconTreeItem).dataitem.CD)
                    treeItem.KEY = (item.value as IconTreeItemHolder.IconTreeItem).dataitem.CODE
                    treeItem.DESCRIPTION =
                        (item.value as IconTreeItemHolder.IconTreeItem).dataitem.DESCRIPTION

                    binding.textViewDeptNm.setText(binding.textViewDeptNm.text.toString() + (item.value as IconTreeItemHolder.IconTreeItem).dataitem.DESCRIPTION + "\n")
                }
            }
        }

        val root = TreeNode.root()

        for (loc in commonHelper.instance.getLocList("", _Table)) {
            val dataitem = codeModel(loc.KEY, loc.DESCRIPTION)
            val node =
                TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_home, dataitem)).setViewHolder(
                    ProfileHolder(activity!!)
                )
            node.isSelectable = true
            root.addChildren(node)
        }

        tView = AndroidTreeView(activity, root)
        tView!!.setDefaultAnimation(true)
        tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
        tView!!.setDefaultNodeClickListener(nodeClickListener)
        containerView.addView(tView!!.view)
        if (savedInstanceState != null) {
            val state = savedInstanceState.getString("tState")
            if (!TextUtils.isEmpty(state)) {
                tView!!.restoreState(state)
            }
        }
        tView!!.isSelectionModeEnabled = selectionModeEnabled;
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButton()
    }

    fun initButton() {

        binding.btnToggleSelection.setOneClickListener {
            selectionModeEnabled = !selectionModeEnabled
            tView!!.isSelectionModeEnabled = selectionModeEnabled
        }


        binding.btnSelectAll.setOneClickListener {
            if (!selectionModeEnabled) {
                Toast.makeText(activity, "Enable selection mode first", Toast.LENGTH_SHORT).show()
            }
            tView!!.selectAll(true)
        }


        binding.btnDeselectAll.setOneClickListener {
            if (!selectionModeEnabled) {
                Toast.makeText(activity, "Enable selection mode first", Toast.LENGTH_SHORT).show()
            }
            tView!!.deselectAll()
        }
        val button_Ok = view!!.findViewById<View>(R.id.button_Ok)
        button_Ok.setOneClickListener {
            if (null != _listener) {
                treeItem.ORG_DESCRIPTION = binding.textViewDeptNm.text.toString()
                _listener!!.onEvent(treeItem)
                this@commonOfflineTree.dismiss()
            }
            this@commonOfflineTree.dismiss()
        }


    }

    private val nodeClickListener = TreeNodeClickListener { node, value ->
        val item = value as IconTreeItemHolder.IconTreeItem

        var path = ""
        var nodepath = node
        if (_Table.equals("TM_EQUIPMENT")) {
            while (nodepath.parent != null) {
                path =
                    "${((nodepath.value as IconTreeItemHolder.IconTreeItem).dataitem.DESCRIPTION)} > " + path
                nodepath = nodepath.parent
            }
            binding.textViewDeptNm.text = path.substring(0, path.length - 3);
        }
        else{
            path = item.dataitem.CODE
            treeItem.KEY = item.dataitem.CODE
            treeItem.DESCRIPTION = item.dataitem.DESCRIPTION
            binding.textViewDeptNm.text = item.dataitem.DESCRIPTION
        }

        if (node.children.isEmpty()) {
            for (loc in commonHelper.instance.getLocList(path, _Table)) {
                val dataitem = codeModel(loc.KEY, loc.DESCRIPTION)
                var chilinode: TreeNode
                chilinode =
                    TreeNode(
                        IconTreeItemHolder.IconTreeItem(
                            R.string.ic_folder,
                            dataitem
                        )
                    ).setViewHolder(
                        ProfileHolder(activity!!)
                    )
                node.longClickListener
                node.isSelectable = true
                node.addChildren(chilinode)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tState", tView!!.saveState)
    }
}
