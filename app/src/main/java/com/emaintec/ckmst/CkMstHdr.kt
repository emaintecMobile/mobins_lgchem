package com.emaintec.ckmst

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emaintec.Data
import com.emaintec.Fragment_Base
import com.emaintec.Functions
import com.emaintec.ckmst.model.PmMstrModel
import com.emaintec.common.commonViewAdapter
import com.emaintec.common.model.gridViewModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.CkMstHdrBinding
import com.google.gson.Gson

class CkMstHdr : Fragment_Base() {
    lateinit var binding: CkMstHdrBinding
//    var CkMstHdrAdapter.instance = commonViewAdapter<PmMstrModel>()
//        .apply { this.ClassItem = PmMstrModel::class.java }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CkMstHdrBinding.inflate(inflater, container, false)
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
//        CkMstHdrAdapter.instance.gridSetting = arrayListOf(
//            gridViewModel("TAG_NO", "PM_TAG_NO").also { it.IS_EXPAND = false },
//            gridViewModel("주기", "PM_CYCLE").also { it.IS_EXPAND = false },
//            gridViewModel("작업장", "PM_WKCNTR").also { it.IS_EXPAND = false },
//            gridViewModel("점검일", "PM_LDATE").also { it.IS_EXPAND = false },
//            gridViewModel("설비명", "PM_EQP_NM").also { it.IS_EXPAND = false }
//        )
//
//        val mLayoutManager: LinearLayoutManager
//        mLayoutManager = LinearLayoutManager(activity)
//        mLayoutManager.setInitialPrefetchItemCount(50)
//        recyclerView.setLayoutManager(mLayoutManager)
//        recyclerView.setItemViewCacheSize(50);
//
//        CkMstHdrAdapter.instance.setOnItemTapListener(object :
//            RecyclerViewAdapter.OnItemTapListener {
//            override fun onDoubleTap(position: Int) {
//                showckResultDtl()
//            }
//
//            override fun onSingleTap(position: Int) {
//            }
//
//            override fun onLongTap(position: Int): Boolean {
//                return true
//            }
//        })
//        CkMstHdrAdapter.instance.listView = recyclerView
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



    CkMstHdrAdapter.instance.setOnItemTapListener(object : RecyclerViewAdapter.OnItemTapListener {
        override fun onDoubleTap(position: Int) {
            showckResultDtl()
        }

        override fun onSingleTap(position: Int) {

        }

        override fun onLongTap(position: Int): Boolean {
            return true

        }
    })
    CkMstHdrAdapter.instance.listView = recyclerView

}
    private fun updateList(strQrCode: String ="") {
        NetworkProgress.start(requireContext())
        CkMstHdrAdapter.instance.clear()
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            select * from TB_PM_MASTER where 1=1
            ${
                if (binding.editTextSearch.text.toString().isNotBlank()) {
                    "and (PM_TAG_NO like '%${binding.editTextSearch.text.toString()}%' " +
                            "or PM_EQP_NO like '%${binding.editTextSearch.text.toString()}%' " +
                            "or PM_EQP_NM like '%${binding.editTextSearch.text.toString()}%')"
                } else {
                    ""
                }

            }
            ${
                if (strQrCode.isNotBlank()) {
                    "and PM_EQP_NO = '$strQrCode'"
                } else {
                    ""
                }

            }
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<PmMstrModel>::class.java)
        for (item in list!!) {
            CkMstHdrAdapter.instance.addItem(item)
        }
        if (list.isNotEmpty()) {
            CkMstHdrAdapter.instance.selection = 0
        }
        NetworkProgress.end()
    }

    private fun initButton() {

        binding.buttonInquery.setOneClickListener {
            updateList()
        }
        binding.buttonCheckPoint.setOneClickListener {
            showckResultDtl()
        }
        binding.buttonDailyNew.setOneClickListener {
            newDailyCk()
        }
    }

    private fun newDailyCk() {
        CkMstHdrAdapter.instance.currentItem?.let { item ->

           val map  = SQLiteQueryUtil.selectMap("""
                SELECT COUNT(*) CNT FROM TB_PM_DAYMST WHERE PM_EQP_NO = '${item.PM_EQP_NO}'""".trimIndent())
            if(map["CNT"].toString().equals("0"))
            {
                val intentR = Intent()
                intentR.putExtra("PM_EQP_NO", item.PM_EQP_NO) //사용자에게 입력받은값 넣기
                requireActivity().setResult(RESULT_OK, intentR) //결과를 저장
                requireActivity().finish()
            }else{
                Functions.MessageBox(requireContext(),"일일점검대상 설비입니다.추가할수 없습니다")
            }
        }
    }

    private fun showckResultDtl() {
        CkMstHdrAdapter.instance.currentItem?.let { item ->
            CkMstItem().let {
                it.setStyle(STYLE_NORMAL, R.style.FullDialogTheme) // 전체 화면
                it.PM_EQP_NO = item.PM_EQP_NO
                it.PM_GROUP = item.PM_GROUP
                it.PM_PLAN = item.PM_PLAN
                it.PM_TAG_NO = item.PM_TAG_NO
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
                }
                it.updateUI()
            }
        }
    }
    override fun onScanMsg(strQrCode: String) {
        updateList(strQrCode)

    }
}