package com.emaintec.ckfault

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.emaintec.Fragment_Base
import com.emaintec.ckdaily.CkDayHdrAdapter
import com.emaintec.ckdaily.CkDayItem
import com.emaintec.ckdaily.model.PmDayMstModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.CkFaultBinding
import com.google.gson.Gson

class CkFault: Fragment_Base()  {
    lateinit var binding : CkFaultBinding
//    var adapterView = commonViewAdapter<PmDayMstModel>()
//        .apply { this.ClassItem = PmDayMstModel::class.java }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CkFaultBinding.inflate(inflater,container,false)
        return binding.root//inflater.inflate(R.layout.ck_result_hdr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initList()
        initButton()
        updateList()
    }
//    private fun initViewList() {
//        val recyclerView = binding.listView
//        adapterView.gridSetting = arrayListOf(
//            gridViewModel("TAG No", "PM_TAG_NO").also { it.IS_EXPAND = false },
//            gridViewModel("예정일", "PM_PLN_DT").also { it.IS_EXPAND = false },
//            gridViewModel("설비명", "PM_EQP_NM").also { it.IS_EXPAND = false }
//        )
//
//        val mLayoutManager: LinearLayoutManager
//        mLayoutManager = LinearLayoutManager(activity)
//        mLayoutManager.setInitialPrefetchItemCount(50)
//        recyclerView.setLayoutManager(mLayoutManager)
//        recyclerView.setItemViewCacheSize(50);
//
//        adapterView.setOnItemTapListener(object :
//            RecyclerViewAdapter.OnItemTapListener {
//            override fun onDoubleTap(position: Int) {
//                adapterView.currentItem?.let { item ->
//                }
//            }
//            override fun onSingleTap(position: Int) {
//            }
//            override fun onLongTap(position: Int): Boolean {
//                return true
//            }
//        })
//        adapterView.listView = recyclerView
//
//    }
    private fun initList() {
//        val recyclerView = requireView().findViewById<View>(R.id.listView) as RecyclerView
        val recyclerView = binding.listView
        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        //        val mLayoutManager: RecyclerView.LayoutManager =
        //            LinearLayoutManager(Emaintec.application)

        val mLayoutManager: LinearLayoutManager
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView.setLayoutManager(mLayoutManager)

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager)



        CkDayHdrAdapter.instance.setOnItemTapListener(object : RecyclerViewAdapter.OnItemTapListener {
            override fun onDoubleTap(position: Int) {
                showckResultDtl()
            }

            override fun onSingleTap(position: Int) {

            }

            override fun onLongTap(position: Int): Boolean {
                return true

            }
        })
        CkDayHdrAdapter.instance.listView = recyclerView

    }
    private fun updateList() {
        NetworkProgress.start(requireContext())
        CkDayHdrAdapter.instance.clear()
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            SELECT * FROM TB_PM_DAYMST WHERE 1=1
            ${
                if(binding.editTextSearch.text.toString().isNotBlank()) {
                    "and (PM_TAG_NO like '%${binding.editTextSearch.text.toString()}%' " +
                            "or PM_EQP_NO like '%${binding.editTextSearch.text.toString()}%' " +
                            "or PM_EQP_NM like '%${binding.editTextSearch.text.toString()}%')"
                }else{""}

            }
            AND PM_STRANGE = 'Y'
            ORDER BY PM_PLN_DT,PM_EQP_NO
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<PmDayMstModel>::class.java)
        for (item in list!!) {
            CkDayHdrAdapter.instance.addItem(item)
        }
        if(list.isNotEmpty()) {
            CkDayHdrAdapter.instance.selection = 0
        }
        NetworkProgress.end()
    }

    private fun initButton() {

        binding.buttonInquery.setOneClickListener {
            updateList()
        }
        binding.buttonCheckPoint.setOneClickListener {
            CkDayHdrAdapter.instance.currentItem?.let { item ->
                showckResultDtl()
            }
        }
    }
    private fun showckResultDtl() {
        CkDayHdrAdapter.instance.currentItem?.let { item->
            CkDayItem().let {
                it.setStyle(STYLE_NORMAL, R.style.FullDialogTheme) // 전체 화면
                it.PM_EQP_NO = item.PM_EQP_NO
                it.showNow(parentFragmentManager, "")
                it.dialog?.window?.let { window ->
                    val params = window.attributes
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT
                    window.attributes = params as android.view.WindowManager.LayoutParams
                    window.setGravity(Gravity.TOP)
                    window.setWindowAnimations(android.R.style.Animation_Translucent) // 화면 표시 애니메이션
                }
                it.dialog!!.setOnDismissListener { it2->
                    it.dialog!!.dismiss()
                    it.dismiss()
                    Emaintec.fragment = this
                    updateList()
                }
                it.updateUI()
            }
        }
    }
}