package com.emaintec.ckmst

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.emaintec.Fragment_Base
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

class CkMstHdr: Fragment_Base()  {
    lateinit var binding : CkMstHdrBinding
    var adapterView = commonViewAdapter<PmMstrModel>()
        .apply { this.ClassItem = PmMstrModel::class.java }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CkMstHdrBinding.inflate(inflater,container,false)
        return binding.root//inflater.inflate(R.layout.ck_result_hdr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewList()
        initButton()
        updateList()
    }
    private fun initViewList() {
        val recyclerView = binding.listView
        adapterView.gridSetting = arrayListOf(
            gridViewModel("TAG_NO", "PM_TAG_NO").also { it.IS_EXPAND = false },
            gridViewModel("주기", "PM_CYCLE").also { it.IS_EXPAND = false },
            gridViewModel("작업장", "PM_WKCNTR").also { it.IS_EXPAND = false },
            gridViewModel("점검일", "PM_LDATE").also { it.IS_EXPAND = false },
            gridViewModel("설비명", "PM_EQP_NM").also { it.IS_EXPAND = false }
        )

        val mLayoutManager: LinearLayoutManager
        mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.setInitialPrefetchItemCount(50)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemViewCacheSize(50);

        adapterView.setOnItemTapListener(object :
            RecyclerViewAdapter.OnItemTapListener {
            override fun onDoubleTap(position: Int) {
                adapterView.currentItem?.let { item ->
                }
            }
            override fun onSingleTap(position: Int) {
            }
            override fun onLongTap(position: Int): Boolean {
                return true
            }
        })
        adapterView.listView = recyclerView

    }

    private fun updateList() {
        NetworkProgress.start(requireContext())
        adapterView.clear()
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            select * from TB_PM_MASTER where 1=1
            ${
                if(binding.editTextSearch.text.toString().isNotBlank()) {
                    "and PM_TAG_NO like '%${binding.editTextSearch.text.toString()}%'"
                }else{""}
                
            }
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<PmMstrModel>::class.java)
        for (item in list!!) {
            adapterView.addItem(item)
        }
        if(list.isNotEmpty()) {
            adapterView.selection = 0
        }
        NetworkProgress.end()
    }

    private fun initButton() {

        binding.buttonInquery.setOneClickListener {
            updateList()
        }
        binding.buttonCheckPoint.setOneClickListener {
            adapterView.currentItem?.let { item ->
                showckResultDtl()
            }
        }
    }
    private fun showckResultDtl() {
        adapterView.currentItem?.let { item->
            CkMstItem().let {
                it.setStyle(STYLE_NORMAL, R.style.FullDialogTheme) // 전체 화면
                it.PM_EQP_NO = item.PM_EQP_NO
                it.PM_GROUP =  item.PM_GROUP
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
                it.dialog!!.setOnDismissListener {
                    Emaintec.fragment = this
                }
                it.updateUI()
            }
        }
    }
}