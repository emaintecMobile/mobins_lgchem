package com.emaintec.ckdaily

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.emaintec.Data
import com.emaintec.Fragment_Base
import com.emaintec.Functions
import com.emaintec.ckdaily.model.PmDayMstModel
import com.emaintec.ckmst.CkMstHdrAdapter
import com.emaintec.ckmst.model.PmMstrModel
import com.emaintec.common.commonViewAdapter
import com.emaintec.common.model.gridViewModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.main.main_Fragment
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.CkDayHdrBinding
import com.emaintec.mobins.databinding.CkMstHdrBinding
import com.google.gson.Gson
import org.json.JSONObject

class CkDayHdr: Fragment_Base()  {
    lateinit var binding : CkDayHdrBinding
//    var adapterView = commonViewAdapter<PmDayMstModel>()
//        .apply { this.ClassItem = PmDayMstModel::class.java }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CkDayHdrBinding.inflate(inflater,container,false)
        return binding.root//inflater.inflate(R.layout.ck_result_hdr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initList()
        initButton()
        initRadioButton()
        updateUI()
    }
    private fun initRadioButton() {
        binding.radioTotal.isChecked = true
        binding.radioGroupCk.setOnCheckedChangeListener { group, checkedId ->
            updateList()
        }

    }
    override fun onStart() {
        super.onStart()

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
//                showckResultDtl()
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
    private fun updateList(strQrCode: String ="") {
        val jStatus = SQLiteQueryUtil.selectJsonArray(
            """
                   SELECT (SELECT count(*) from TB_PM_MASTER ) MASTER_CNT
                       ,COUNT(*)	   AS DAY_CNT
                       ,SUM(CASE WHEN PM_CHECK = 'Y' THEN 1 ELSE 0 END) AS CHECKED
                       ,SUM(CASE WHEN PM_STRANGE = 'Y' THEN 1 ELSE 0 END) AS STRANGE
                FROM TB_PM_DAYMST
 
        """.trimIndent()
        )
        val list2 = Gson().fromJson(jStatus.toString(), Array<main_Fragment.StatusModel>::class.java)
        if(list2.isNotEmpty()){
            binding.radioCkY.text = "점검 ${list2[0].CHECKED} "
            binding.radioCkN.text = "미점검 ${list2[0].DAY_CNT.toInt() - list2[0].CHECKED.toInt()}"
        }
        NetworkProgress.start(requireContext())
        CkDayHdrAdapter.instance.clear()
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            select * from TB_PM_DAYMST where 1=1
            ${
                if(binding.editTextSearch.text.toString().isNotBlank()) {
                    "and (PM_TAG_NO like '%${binding.editTextSearch.text.toString()}%' " +
                            "or PM_EQP_NO like '%${binding.editTextSearch.text.toString()}%' " +
                            "or PM_EQP_NM like '%${binding.editTextSearch.text.toString()}%')"
                }else{""}
            }
            ${
                if(strQrCode.isNotBlank()) {
                    "and PM_EQP_NO = '$strQrCode'"
                }else{
                    when(binding.radioGroupCk.checkedRadioButtonId) {
                        R.id.radioCkY -> "and PM_CHECK = 'Y'"
                        R.id.radioCkN -> "and PM_CHECK IS NULL"
                        R.id.radioCkS ->" and PM_STRANGE = 'Y'"
                        else ->""
                    }
                }
            }
            order by PM_PLN_DT,PM_EQP_NO
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<PmDayMstModel>::class.java)
        for (item in list!!) {
            CkDayHdrAdapter.instance.addItem(item)
        }
        NetworkProgress.end()
        if(list.isNotEmpty()) {
            CkDayHdrAdapter.instance.selection = 0
        }else{
            if(strQrCode.isNotBlank()) {
                Functions.MessageBox(requireContext(), "없는 설비번호 입니다.")
                return
            }
        }

        if(strQrCode.isNotBlank()) {
            binding.buttonCheckPoint.post {
                binding.buttonCheckPoint.performClick()
            }
        }
    }

    override fun updateUI() {
        super.updateUI()
        if(Data.instance._mode.equals("NEW")){
            binding.buttonCheckPoint.post {
                CkDayItem().let {
                    it.setStyle(STYLE_NORMAL, R.style.FullDialogTheme) // 전체 화면
                    it.showNow(parentFragmentManager, "")
                    it.dialog?.window?.let { window ->
                        val params = window.attributes
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT
                        window.attributes = params as android.view.WindowManager.LayoutParams
                        window.setGravity(Gravity.TOP)
                        window.setWindowAnimations(android.R.style.Animation_Translucent) // 화면 표시 애니메이션
                    }
                    it.dialog!!.setOnDismissListener {
                        Emaintec.fragment = this
                        updateList()
                    }
                    it.updateUI()
                }
            }
        }else {
            updateList(Data.instance.scanData)
        }
        Data.instance.scanData = ""

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
                it.dialog!!.setOnDismissListener {
                    Emaintec.fragment = this
                    updateList()
                }
                it.updateUI()
            }
        }
    }
    override fun onScanMsg(strQrCode: String) {
        Data.instance.scanData = strQrCode
        updateList(Data.instance.scanData)
        Data.instance.scanData = ""
    }
}