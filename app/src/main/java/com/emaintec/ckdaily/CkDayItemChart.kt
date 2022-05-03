package com.emaintec.ckdaily

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
import com.emaintec.ckdaily.model.PmDayCpModel
import com.emaintec.ckdaily.model.PmHistoryModel
import com.emaintec.common.commonGridAdapter
import com.emaintec.common.model.gridModel
import com.emaintec.datasync.helper.dataSyncHelper
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.device.Device
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.version.Html
import com.emaintec.mobins.databinding.CkResultDtlChartBinding
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CkDayItemChart : Fragment_Base() {
    var adapterView = commonGridAdapter<PmHistoryModel>()
    var item : PmDayCpModel ?= null
    private var _listener: OnDialogEventListener? = null
    lateinit var binding : CkResultDtlChartBinding
    var loading: Boolean = true
    var pastVisiblesItems: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0

    interface OnDialogEventListener {
        fun onEvent(message: String)
    }

    fun setOnDialogEventListener(listener: OnDialogEventListener?): CkDayItemChart {
        _listener = listener
        return this
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CkResultDtlChartBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButton()
        initListView()
    }
    // 적용설비 그리드의 헤더를 설정
    private fun initListView() {
        val recyclerView = binding.grid.listView
        adapterView.gridSetting = arrayListOf(
            gridModel("점검일", 100, "CHK_DATE"),
            gridModel("결과", 90, "CHK_RESULT"),
            gridModel("상태", 90, "CHK_OKNOK")
        )

        val mLayoutManager: LinearLayoutManager
        mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.setInitialPrefetchItemCount(50)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemViewCacheSize(50)
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


    private fun initButton() {

        binding.buttonClose!!.setOnClickListener {
            if (null != _listener) {
                _listener!!.onEvent("close")
            }
            this@CkDayItemChart.dismiss()
        }
        binding.customLayoutTitle.imageButton.setOnClickListener {
            if (null != _listener) {
                _listener!!.onEvent("close")
            }
            this@CkDayItemChart.dismiss()
        }
    }

    override fun updateUI() {
        val gsonObj = Gson()
        val jsonobj = JsonObject()
        val array = JsonArray()
        jsonobj.addProperty("PM_EQP_NO", item!!.PM_EQP_NO)
        jsonobj.addProperty("CHECK_DETAIL_NO", item!!.CHK_NO)
        array.add(jsonobj)

        val jsonData = gsonObj.toJson(array)
        NetworkProgress.start(activity!!)
        CoroutineScope(Dispatchers.Default).launch {
            dataSyncHelper.Check.GetCkSchHstList({ success, list, msg ->
                if (success) {
                    launch(Dispatchers.Main) {
                        adapterView.clear()
                        for (item in list!!) {
                            adapterView.addItem(item)
                        }
                        adapterView.front()
                    }.join()
                    launch(Dispatchers.Main) {
                        updateChart()
                    }.join()
                } else {
                    launch(Dispatchers.Main) {
                        adapterView.clear()
                        val map =   SQLiteQueryUtil.selectMap(""" SELECT  CHK_LDATE1 , CHK_LDATE2, CHK_OKNOK1 , CHK_OKNOK2, CHK_LRSLT1 , CHK_LRSLT2 FROM TB_PM_MSTCP
                                 |WHERE PM_EQP_NO = '${item!!.PM_EQP_NO}' AND CHK_NO = '${item!!.CHK_NO}'""".trimMargin())
                        adapterView.addItem(PmHistoryModel(map["CHK_LDATE1"], map["CHK_OKNOK1"], map["CHK_LRSLT1"]));
                        adapterView.addItem(PmHistoryModel(map["CHK_LDATE2"], map["CHK_OKNOK2"], map["CHK_LRSLT2"]));
                    }.join()
                    launch(Dispatchers.Main) {
                        updateChart()
                    }.join()
                }
            }, jsonData)
            NetworkProgress.end()
        }
    }
    fun updateChart() {
        binding.textView01.text = item!!.CHK_POS
        binding.textView02.text = item!!.CHK_DESC
        if (item!!.CHK_CHAR.isNullOrBlank()) {
            binding.textView03.visibility = View.GONE
            binding.chart.visibility = View.GONE
            return

        }else{
            binding.textView03.visibility = View.VISIBLE
            binding.chart.visibility = View.VISIBLE
            binding.textView03.text = Html.fromHtml(
                "<font color=#FF0000>Min: ${item!!.CHK_MIN} </font>--<font color=#00FF00> Std:${item!!.CHK_DEST}</font> -- <font color=#FF0000>Max:${item!!.CHK_MAX} </font>".replace(
                    "null",
                    "    "
                )
            )
        }
        if(adapterView.getCount() == 1) return
        binding.chart.clear()
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()
        for (idx in 0 until adapterView.getCount()) {
            var value = adapterView.getItem(idx).CHK_RESULT!!
            if(value.isNullOrBlank())value ="0"
            entries.add(Entry(idx.toFloat(), value.toFloat()))
            labels.add(adapterView.getItem(idx).CHK_DATE!!)
        }

        //		LineDataSet dataset = new LineDataSet(entries, "# of Calls");
        val dataset = LineDataSet(entries, "Check Result")
        dataset.setAxisDependency(YAxis.AxisDependency.LEFT)


        val data = LineData(dataset)
        dataset.setColors(Color.GREEN) //
        /* dataset.setDrawCubic(true); //선 둥글게 만들기
         dataset.setDrawFilled(true); //그래프 밑부분 색칠*/

        binding.chart.setData(data)
        binding.chart.invalidate()
//        chart.setVisibleXRangeMinimum((7).toFloat());


        // chart는 widget과 연결된 변수
//        val xAxis = lineChart.getXAxis()
        // setValueFormatter메소드에 인자로 객체를 넘겨주면 됩니다.
//        xAxis.setValueFormatter(labels)
        val xAxis = binding.chart.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        //xAxis.setLabelCount(7,true)

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return labels.get(value.toInt())
            }
        }
        val yAxis: YAxis
        run {
            // // Y-Axis Style // //
            yAxis = binding.chart.axisLeft
            // disable dual axis (only use LEFT axis)
            binding.chart.axisRight.isEnabled = false
            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f)
            yAxis.removeAllLimitLines()
            // axis range
        }
        run {
            // // Create Limit Lines // //
//            val llXAxis = LimitLine(9f, "Index 10")
//            llXAxis.lineWidth = 4f
//            llXAxis.enableDashedLine(10f, 10f, 0f)
//            llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
//            llXAxis.textSize = 10f
//            llXAxis.typeface = tfRegular
            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true)
            xAxis.setDrawLimitLinesBehindData(true)
            // add limit lines
            if (!(item!!.CHK_MAX.isBlank() || item!!.CHK_MAX == "0")) {
                val ll1 = LimitLine(item!!.CHK_MAX.toFloat(), "Max")
                ll1.lineWidth = 4f
                ll1.enableDashedLine(10f, 10f, 0f)
                ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                ll1.textSize = 10f
//            ll1.typeface = tfRegular
                yAxis.addLimitLine(ll1)
            }
            if (!(item!!.CHK_MIN.toString().isBlank() || item!!.CHK_MIN.toString() == "0")) {
                val ll2 = LimitLine(item!!.CHK_MIN.toFloat(), "Min")
                ll2.lineWidth = 4f
                ll2.enableDashedLine(10f, 10f, 0f)
                ll2.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                ll2.textSize = 10f
//            ll2.typeface = tfRegular
                yAxis.addLimitLine(ll2)
            }
            if (!(item!!.CHK_DEST.isBlank() || item!!.CHK_DEST == "0")){
                val ll3 = LimitLine(item!!.CHK_DEST.toFloat(), "Std")
                ll3.lineWidth = 4f
                ll3.enableDashedLine(10f, 10f, 0f)
                ll3.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                ll3.textSize = 10f
                ll3.lineColor =Color.BLUE
//            ll2.typeface = tfRegular
                yAxis.addLimitLine(ll3)
            }
            //xAxis.addLimitLine(llXAxis);
        }
        binding.chart.setScaleMinima(adapterView.getCount()/10f,1f)
        binding.chart.description = null
        binding.chart.animateY(1000)
    }


}


