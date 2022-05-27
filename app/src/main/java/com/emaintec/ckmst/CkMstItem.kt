package com.emaintec.ckmst

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import com.emaintec.Fragment_Base
import com.emaintec.ckdaily.CkDayItemChart
import com.emaintec.ckdaily.model.PmDayCpModel
import com.emaintec.ckmst.model.PmMstCpModel
import com.emaintec.ckmst.model.PmMstrModel
import com.emaintec.common.commonGridAdapter
import com.emaintec.common.commonViewAdapter
import com.emaintec.common.model.gridModel
import com.emaintec.common.model.gridViewModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.device.Device
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.databinding.CkMstHdrBinding
import com.emaintec.mobins.databinding.CkMstItemBinding
import com.google.gson.Gson

class CkMstItem: Fragment_Base()  {

    lateinit var binding : CkMstItemBinding
    var adapterView = commonGridAdapter<PmMstCpModel>()
    var PM_EQP_NO = ""
    var PM_TAG_NO = ""
    var PM_GROUP = ""
    var PM_PLAN = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CkMstItemBinding.inflate(inflater,container,false)
        return binding.root//inflater.inflate(R.layout.ck_result_hdr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewList()
        initButton()
        updateList()
    }
    // 적용설비 그리드의 헤더를 설정
    private fun initViewList() {
        val recyclerView = binding.grid.listView
        adapterView.gridSetting = arrayListOf(
            gridModel("점검항목", 150, "CHK_DESC"),
            gridModel("단위", 60, "CHK_UNIT"),
            gridModel("MIN", 70, "CHK_MIN"),
            gridModel("STD", 70, "CHK_DEST"),
            gridModel("MAX", 70, "CHK_MAX")
//            gridModel("입력일", 100, "CHK_LDATE1"),
//            gridModel("결과", 100, "CHK_LRSLT1"),
//            gridModel("상태", 100, "CHK_OKNOK1"),
//            gridModel("입력일", 100, "CHK_LDATE2"),
//            gridModel("결과", 100, "CHK_LRSLT2"),
//            gridModel("상태", 100, "CHK_OKNOK2")

        )

        val mLayoutManager: LinearLayoutManager
        mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.setInitialPrefetchItemCount(50)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemViewCacheSize(50)

        adapterView.setOnItemTapListener(object : RecyclerViewAdapter.OnItemTapListener {
            override fun onDoubleTap(position: Int) {
                adapterView.currentItem?.let {
//                    commonPopup.init(this@ptDtl, parentFragmentManager).showEqMstrDtl(it.EQ_NO)
                }
            }

            override fun onSingleTap(position: Int) {
                    binding.textViewChkPos.text = adapterView.currentItem!!.CHK_POS
            }
            override fun onLongTap(position: Int): Boolean {
                return true
            }
        })

        adapterView.listView = recyclerView

        var i = 0
        //표의 해더를 만듬
        adapterView.gridSetting?.forEach { item ->
            var textColumnHeader = TextView(Emaintec.activity)
            val p = LinearLayout.LayoutParams(
                Device.convertDpToPixel(item.WIDTH, requireActivity()).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT,
                0f
            )
            textColumnHeader.setLayoutParams(p)
            textColumnHeader.setTextColor(Color.BLACK)
            textColumnHeader.gravity = Gravity.CENTER
            textColumnHeader.text = item.COLUMN_NAME + " "
            val KEY = item.KEY
            val grid_header = binding.grid.gridHeader
            grid_header.addView(textColumnHeader)
            textColumnHeader.tag = true
            //그리드 정렬 해더를 클릭 하면 정렬 한다.
            textColumnHeader.setOnClickListener { view ->
                grid_header.forEach { header ->
                    if (header is TextView)
                        header.text =
                            header.text.toString().substring(0, header.text.length - 1) + " "
                }
                adapterView._arrayList.sortWith(Comparator { lhs, rhs ->
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    if (textColumnHeader.tag as Boolean) {
                        if (lhs.getValue(KEY) > rhs.getValue(KEY)) -1
                        else if (lhs.getValue(KEY) < rhs.getValue(KEY)) 1
                        else 0
                    } else {
                        if (lhs.getValue(KEY) < rhs.getValue(KEY)) -1
                        else if (lhs.getValue(KEY) > rhs.getValue(KEY)) 1
                        else 0
                    }
                })

                if ((view.tag as Boolean)) {
                    (view as TextView).text = (view as TextView).text.toString()
                        .substring(0, (view as TextView).text.length - 1) + "▼"
                } else {
                    (view as TextView).text = (view as TextView).text.toString()
                        .substring(0, (view as TextView).text.length - 1) + "▲"
                }
                view.tag = !(view.tag as Boolean)
                adapterView.notifyDataSetChanged()
            }

            if (i < adapterView.gridSetting!!.size - 1) {
                var line = LinearLayout(Emaintec.activity)
                val p1 = LinearLayout.LayoutParams(
                    Device.convertDpToPixel(1f, requireActivity()).toInt(),
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                line.setLayoutParams(p1)
                line.setBackgroundColor(Color.parseColor("#92b7eb"))
                grid_header.addView(line)
            }
            i++
        }
    }

    private fun updateList() {
        NetworkProgress.start(requireContext())
        adapterView.clear()
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            select * from TB_PM_MSTCP where 1=1
            ${
                if (PM_EQP_NO.isBlank()) {
                    "and 111"
                } else {
                    "and PM_PLAN  = '$PM_PLAN' and PM_GROUP = '$PM_GROUP' and PM_EQP_NO ='$PM_EQP_NO'"
                }   
            }
            order by PM_GROUP,PM_EQP_NO,CHK_SEQ,CHK_NO
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<PmMstCpModel>::class.java)
        for (item in list!!) {
            adapterView.addItem(item)
        }
        NetworkProgress.end()
    }

    private fun initButton() {
        binding.customLayoutTitle.imageButton.setOneClickListener {
            dialog?.dismiss()
            this.dismiss()
        }
        binding.buttonInquery.setOneClickListener {
            updateList()
        }
        binding.buttonHst.setOneClickListener {
            showResultHst()
        }
    }
    private fun showResultHst(){
        adapterView.currentItem?.let { item ->
            CkDayItemChart().let {
                it.item = Gson().fromJson( Gson().toJson(item).toString(),PmDayCpModel::class.java)
                it.showNow(parentFragmentManager, "")
                it.dialog?.setCanceledOnTouchOutside(false);
                it.dialog?.window?.let { window ->
                    val params = window.attributes
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    window.attributes = params as android.view.WindowManager.LayoutParams
                    window.setGravity(Gravity.TOP)
                    window.setWindowAnimations(android.R.style.Animation_InputMethod)
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
    override fun updateUI() {
        super.updateUI()
        binding.editTextSearch.setText(PM_TAG_NO)
    }
}