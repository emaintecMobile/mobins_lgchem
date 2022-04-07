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
import com.emaintec.Data
import com.emaintec.Fragment_Base
import com.emaintec.Functions
import com.emaintec.ckdaily.model.PmDayCpModel
import com.emaintec.ckdaily.model.PmDayMstModel
import com.emaintec.common.model.gridModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.device.Device
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.CkDayItemBinding
import com.google.gson.Gson

class CkDayItem : Fragment_Base() {

    lateinit var binding: CkDayItemBinding
    var adapterView = CkDayItemGridAdapter<PmDayCpModel>()
    var standBy = "Y"
    var stranger = "N"
    var PM_EQP_NO = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CkDayItemBinding.inflate(inflater, container, false)
        return binding.root//inflater.inflate(R.layout.ck_result_hdr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRadioButton()
        initViewList()
        initButton()

    }

    private fun initRadioButton() {
        binding.radioStandByYes.isChecked = true
        binding.radioStandByYes.tag = "Y"
        binding.radioStandByNo.tag = "N"
        binding.radioStrangerNo.isChecked = true
        binding.radioStrangerYes.tag = "Y"
        binding.radioStrangerNo.tag = "N"

        binding.radioGroupStandBy.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioStandByYes -> standBy = "Y"
                R.id.radioStandByNo -> {
                    standBy = "N"
                    adapterView._arrayList.forEach {
                        it.CHK_RESULT = ""
                        it.CHK_OKNOK = "OK"
                    }
                    adapterView.notifyDataSetChanged()
                }

            }
        }
        binding.radioGroupStranger.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioStrangerYes -> stranger = "Y"
                R.id.radioStrangerNo -> stranger = "N"
            }
        }
    }

    // 적용설비 그리드의 헤더를 설정
    private fun initViewList() {
        val recyclerView = binding.grid.listView
        adapterView.gridSetting = arrayListOf(
            gridModel("작업내용", 180, "CHK_DESC").also { it.IS_CLICK = true },
            gridModel("결과", 70, "CHK_RESULT").also { it.IS_CLICK = true },
            gridModel("상태", 70, "CHK_OKNOK").also {
                it.Select_color_1 = arrayOf("NOK")
                it.Select_color_2 = arrayOf("OK")
                it.IS_CLICK = true
            },
            gridModel("MIN", 70, "CHK_MIN"),
            gridModel("STD", 70, "CHK_DEST"),
            gridModel("MAX", 70, "CHK_MAX"),
            gridModel("단위", 70, "CHK_UNIT")
//            gridModel("메모", 270, "CHK_MEMO").also { it.IS_CLICK = true }
        )

        val mLayoutManager: LinearLayoutManager
        mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.setInitialPrefetchItemCount(50)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemViewCacheSize(50)
        adapterView.setOnCellClickListener(object : CkDayItemGridAdapter.OnCellClickListener {
            override fun onCellClick(position: Int, columnName: String) {
                if(standBy.equals("N")) return
                adapterView.selection = position
                when(columnName){
                    "CHK_DESC" ->{
                        showInputDtl()
                    }
                    "CHK_RESULT" ->{
                        if(adapterView.currentItem?.CHK_IN_TYP.equals("X"))
                            showInputDtl()
                    }
                    "CHK_OKNOK" ->{
                        var CHECK_STATUS = "OK"
                        if (adapterView.getItem(position).CHK_OKNOK.equals("OK")) CHECK_STATUS = "NOK"
                        else CHECK_STATUS = "OK"
                        adapterView.getItem(position).STATUS = "U"
                        adapterView.getItem(position).CHK_OKNOK = CHECK_STATUS
                        adapterView.notifyItemChanged(position)
                    }
                }
//                if (columnName.equals("CHK_MEMO")) {
//                    val et = EditText(context!!)
//                    et.setText(adapterView.getItem(position).CHK_MEMO)
//                    val alt_bld = AlertDialog.Builder(context!!)
//
//                    alt_bld.setTitle("비고입력").setMessage("입력하세요").setCancelable(false).setView(et).setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
//                        val value = et.text.toString()
//                        adapterView.getItem(position).STATUS = "U"
//                        adapterView.getItem(position).CHK_MEMO = value
//                        adapterView.selection = position
//                        adapterView.notifyItemChanged(position)
//                    }).setNegativeButton("취소") { alt_bld, which -> alt_bld.dismiss() }
//
//                    val alert = alt_bld.create()
//
//                    alert.show()
//                }
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
        adapterView.setOnChangeListener(object : CkDayItemGridAdapter.OnChangeListener {
            override fun onChange() {
                if(adapterView._arrayList.filter { it.CHK_OKNOK.equals("NOK") }.size > 0)
                {
                    binding.radioStrangerYes.isChecked = true
                }else{
                    binding.radioStrangerNo.isChecked = true
                }
            }
        }
        )
    }

    private fun updateList(strQrCode: String="") {
        NetworkProgress.start(requireContext())
        if(strQrCode.isNotEmpty()) PM_EQP_NO = strQrCode
        adapterView.clear()
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            SELECT A.* 
                  , B.CHK_MIN , B.CHK_DEST, B.CHK_MAX ,B.CHK_UNIT,B.CHK_CHAR,B.CHK_UNIT 
                  , (SELECT PM_TAG_NO FROM TB_PM_DAYMST WHERE PM_EQP_NO = '$PM_EQP_NO') PM_TAG_NO
              FROM TB_PM_DAYCP A, TB_PM_MSTCP B 
             WHERE 1=1
              AND A.PM_PLAN = B.PM_PLAN
              AND A.PM_GROUP = B.PM_GROUP
              AND A.PM_EQP_NO = B.PM_EQP_NO
              AND A.CHK_NO = B.CHK_NO
            ${
                if (PM_EQP_NO.isBlank()) {
                    "AND A.PM_EQP_NO  = (SELECT PM_EQP_NO FROM TB_PM_DAYMST WHERE PM_TAG_NO = '${binding.editTextSearch.text.toString()}')"
                } else {
                    "AND A.PM_EQP_NO  = '$PM_EQP_NO'"
                }
            }
            order by A.PM_NOTI_NO,A.CHK_SEQ,A.CHK_NO
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<PmDayCpModel>::class.java)
        for (item in list!!) {
            if (item.CHK_OKNOK.isNullOrBlank()) item.CHK_OKNOK = "OK"
            adapterView.addItem(item)
        }

        if(adapterView._arrayList.filter { it.CHK_DESC.contains(Data.instance.m_strChkDmyIden) }.size==0){
            binding.layoutSpecial.visibility = View.GONE
        }else{
            binding.layoutSpecial.visibility = View.VISIBLE
            binding.editTextSpecial.editTextDialog.setText(adapterView._arrayList.filter { it.CHK_DESC.contains(Data.instance.m_strChkDmyIden)}[0].CHK_MEMO)
        }
        if(list.isNotEmpty()) {
            PM_EQP_NO = adapterView._arrayList[0].PM_EQP_NO
            var itemHdr = getPmMstInfo()
            binding.editTextSearch.setText(itemHdr?.PM_TAG_NO)
            adapterView.selection = 0
            if(adapterView._arrayList[0].PM_STANDBY.equals("N")){
                binding.radioStandByNo.isChecked = true
            }
        }
        NetworkProgress.end()
    }
    private fun updateListNew(strQrCode: String="") {
        NetworkProgress.start(requireContext())
        if(strQrCode.isNotEmpty()) PM_EQP_NO = strQrCode
        adapterView.clear()
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            SELECT  *
                  , (SELECT PM_TAG_NO FROM TB_PM_DAYMST WHERE PM_EQP_NO = '$PM_EQP_NO') PM_TAG_NO
                  , (SELECT MAX(PM_NOTI_NO) FROM TB_PM_DAYCP)  PM_NOTI_NO
                   , '${Functions.DateUtil.getDate("yyyyMMdd")}'  PM_PLN_DT
              FROM  TB_PM_MSTCP 
             WHERE 1=1
             AND PM_EQP_NO  = '$PM_EQP_NO'
            order by CHK_NO
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<PmDayCpModel>::class.java)
        for (item in list!!) {
            if (item.CHK_OKNOK.isNullOrBlank()) item.CHK_OKNOK = "OK"
            adapterView.addItem(item)
        }

        if(adapterView._arrayList.filter { it.CHK_DESC.contains(Data.instance.m_strChkDmyIden) }.size==0){
            binding.layoutSpecial.visibility = View.GONE
        }else{
            binding.layoutSpecial.visibility = View.VISIBLE
            binding.editTextSpecial.editTextDialog.setText(adapterView._arrayList.filter { it.CHK_DESC.contains(Data.instance.m_strChkDmyIden)}[0].CHK_MEMO)
        }
        if(list.isNotEmpty()) {
            PM_EQP_NO = adapterView._arrayList[0].PM_EQP_NO
            var itemHdr = getPmMstInfo()
            binding.editTextSearch.setText(itemHdr?.PM_TAG_NO)
            adapterView.selection = 0
            if(adapterView._arrayList[0].PM_STANDBY.equals("N")){
                binding.radioStandByNo.isChecked = true
            }
        }
        NetworkProgress.end()
    }
    private fun initButton() {
        binding.customLayoutTitle.imageButton.setOneClickListener {
            dialog?.dismiss()
            dismiss()
        }
        binding.buttonInquery.setOneClickListener {
            PM_EQP_NO = ""
            updateList()
        }

        binding.buttonSave.setOneClickListener {
            if(standBy.equals("Y")){
                adapterView._arrayList.filter { it.CHK_IN_TYP.equals("X") && it.CHK_CHAR.isNotEmpty() && it.CHK_RESULT.isNullOrEmpty() }.forEach {
                    val idx = adapterView._arrayList.indexOf(it)
                    adapterView.selection = idx
                    adapterView.listView?.scrollToPosition(idx)
                    adapterView.listView?.findViewHolderForLayoutPosition(idx)
                        ?.let { Functions.highlightGrid("점검결과입력이 완료되지 않았습니다.", it) }
                    return@setOneClickListener
                }
            }
            save()
        }
    }

    private fun save() {
        val date = Functions.DateUtil.getDate()
        val time =  Functions.DateUtil.getDate("HHmmss")
        //해더정보 조회
        var itemHdr = getPmMstInfo()
        if(itemHdr == null){
            Functions.MessageBox(requireContext(),"해더 정보가 없습니다.")
            return
        }
        SQLiteQueryUtil.replace_table_Model(itemHdr as Object,"TB_PM_DAYMST",false)
        adapterView._arrayList.forEach {
            it.CHK_DATE = date
            it.CHK_TIME = time
            it.PM_CHECK = "Y"
            it.PM_STANDBY = standBy
            it.PM_STRANGE = stranger
            SQLiteQueryUtil.replace_table_Model(it as Object,"TB_PM_DAYCP",false)
        }
        Functions.MessageBox(requireContext(),"성공적으로 저장되었습니다")
        dialog?.dismiss()
        dismiss()

    }
    private fun getPmMstInfo() : PmDayMstModel? {
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            SELECT PM_EQP_NO,PM_TAG_NO,PM_EQP_NM,PM_PLN_DT,
                  'Y' 'PM_CHECK',
                   '${stranger}' 'PM_STRANGE',
                   'N' CHK_NOTI,CHK_PART,FLEX1,FLEX2,FLEX3
              FROM TB_PM_DAYMST  
             WHERE 1=1
               AND PM_EQP_NO  = '$PM_EQP_NO'
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<PmDayMstModel>::class.java)
        if(list.isEmpty()) {
            val jArr = SQLiteQueryUtil.selectJsonArray(
                """
            SELECT PM_EQP_NO,PM_TAG_NO,PM_EQP_NM,
                    '${Functions.DateUtil.getDate("yyyyMMdd")}' PM_PLN_DT ,'Y' PM_CHECK,
                    '${stranger}' PM_STRANGE,
                    'N' CHK_NOTI
              FROM TB_PM_MASTER  
             WHERE 1=1
               AND PM_EQP_NO  = '$PM_EQP_NO'
        """.trimIndent()
            )
            val list = Gson().fromJson(jArr.toString(), Array<PmDayMstModel>::class.java)
            if(list.isNotEmpty())
                return list[0]
            else return null
        }else{
            return list[0]
        }
    }
    private fun showInputDtl() {
        adapterView.currentItem?.let { item ->
            CkDayItemInput().let {
                it.adapterView = adapterView
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

                it.setOnDialogEventListener(object : CkDayItemInput.OnDialogEventListener {
                    override fun onEvent(message: String) {
                        if ("changed" == message) {
                            binding.buttonSave!!.isEnabled = true
                        }
                    }
                })
                it.dialog!!.setOnDismissListener {
                    Emaintec.fragment = this
                }
                it.updateUI()
            }
        }
    }

    override fun updateUI() {
        super.updateUI()
        if( Data.instance._mode.equals("NEW")){
            PM_EQP_NO =  Data.instance._modeData
            updateListNew()
        }else {
            updateList()
        }
        Data.instance._mode = ""
        Data.instance._modeData = ""
    }

    override fun onScanMsg(strQrCode: String) {
        super.onScanMsg(strQrCode)
        updateList(strQrCode)
    }
}