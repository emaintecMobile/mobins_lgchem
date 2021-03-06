package com.emaintec.datasync

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emaintec.Data
import com.emaintec.Fragment_Base
import com.emaintec.Functions
import com.emaintec.datasync.helper.dataSyncHelper
import com.emaintec.db.QueryHelper_Setup
import com.emaintec.lib.adapter.AdapterMutiSpinner
import com.emaintec.lib.adapter.ModelMutilSpinner
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.DatasyncBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class dataSync : Fragment_Base() {
    lateinit var binding: DatasyncBinding
    private var workCenters = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DatasyncBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        binding.dtpCkScheduleDateFrom.text = Functions.DateUtil.getDate("yyyy-MM-dd", -6)
        Functions.UpdateApp.Autoupdate()
        initSpinner()
        initButton()
    }

    private fun initSpinner() {
        NetworkProgress.start(requireContext())
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            select WKCNTR_NO CODE,WKCNTR_NM NAME from TB_PM_WKCENTER
        """.trimIndent()
        )

        val list = Gson().fromJson(jArr.toString(), Array<ModelMutilSpinner>::class.java)
        val arrayList = list.toCollection(ArrayList<ModelMutilSpinner>())
        arrayList.filter { it.CODE.equals(Data.instance._workCenter) }.forEach {
            it.selected = true
        }
        arrayList.add(0, ModelMutilSpinner().also { it.NAME = "${Data.instance._workCenterNm}" })
        val adapter = AdapterMutiSpinner(requireContext(), R.layout.custom_spinner_check_item, arrayList)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerWorkCenter.adapter = adapter
        NetworkProgress.end()

        binding.spinnerWorkCenter.setOnFocusChangeListener { v, hasFocus ->
            binding.spinnerWorkCenter.adapter = adapter
        }
    }


    private fun initButton() {
        binding.customLayoutTitle.imageButton.setOneClickListener {
            dialog!!.dismiss()
            dismiss()
        }
        binding.buttonDownload.setOneClickListener {
            if (binding.switchCkDaily.isChecked) {
                val map = SQLiteQueryUtil.selectMap("""SELECT COUNT(*) CNT FROM TB_PM_DAYMST WHERE PM_CHECK = 'Y'""")
                if (!map["CNT"].equals("0")) {
                    Functions.MessageBox(requireContext(), "??????????????? ???????????????. ???????????? ?????? ?????????????????????????", "??????",
                        DialogInterface.OnClickListener { dialog, which ->
                            download()
                        })
                }else{
                    download()
                }
            } else {
                download()
            }

        }
        binding.buttonUpload.setOneClickListener {
            upload()
        }
    }

    private fun upload() {
        NetworkProgress.start(activity!!)
        CoroutineScope(Dispatchers.Default).launch {
            var bResult =
                dataSyncHelper.Check.uploadCheckResult(action = { success: Boolean, msg: String ->
                    launch(Dispatchers.Main) {
                        binding.textViewCkDaily.text = msg
                    }.join()
                })
            NetworkProgress.end()
        }
    }

    private fun download() {

        workCenters = ""
        (binding.spinnerWorkCenter.adapter as AdapterMutiSpinner).listState.filter { it.selected }.forEach {
            workCenters += it.CODE+"!"
        }
        workCenters = "!"+workCenters

        NetworkProgress.start(activity!!)
        CoroutineScope(Dispatchers.IO).launch{
            if (binding.switchCkMst.isChecked) downloadChMst()
            if (binding.switchCkDaily.isChecked) downloadCkDaily()

            NetworkProgress.end()
        }
    }

    suspend fun downloadChMst() {
        val gson = Gson()
        val jsonobject = JsonObject()
        val array = JsonArray()
        jsonobject.addProperty("PLANT", Data.instance._plant)
        jsonobject.addProperty("WKCENTER", workCenters)
        array.add(jsonobject)
        val jsonData = gson.toJson(array)
        CoroutineScope(Dispatchers.IO).launch {
            launch(Dispatchers.Main) {
                binding.textViewCkResult.text = "?????????"
            }.join()
            dataSyncHelper.Check.GetCkMst({ success: Boolean, msg: String ->
                launch(Dispatchers.Main) {
                    binding.textViewCkResult.text = msg
                }.join()
            }, jsonData)
        }.join()
    }


    suspend fun downloadCkDaily() {
        val gson = Gson()
        val jsonobject = JsonObject()
        val array = JsonArray()
        var selectDeptNo = "'${Data.instance._plant}'"

        jsonobject.addProperty("PLANT", Data.instance._plant)
        jsonobject.addProperty("WKCENTER", workCenters)
        jsonobject.addProperty("FRDATE", binding.dtpCkScheduleDateFrom.text.toString())
        jsonobject.addProperty("TODATE", binding.dtpCkScheduleDateFrom.text.toString())
        array.add(jsonobject)
        val jsonData = gson.toJson(array)

        CoroutineScope(Dispatchers.IO).launch {
            launch(Dispatchers.Main) {
                binding.textViewCkDaily.text = "?????????"
            }.join()
           dataSyncHelper.Check.GetCkDaily({ success: Boolean, msg: String ->
                launch(Dispatchers.Main) {
                    binding.textViewCkDaily.text = msg
                }.join()
            }, jsonData)
        }.join()
//        if (binding.dtpCkScheduleDateFrom.text.toString().equals(binding.dtpCkScheduleDateTo.text.toString())) {
//            QueryHelper_Setup.instance.mapSetting["DOWN_DATE"] = binding.dtpCkScheduleDateFrom.text.toString()
//            Data.instance._downDate = QueryHelper_Setup.instance.mapSetting["DOWN_DATE"].toString()
//        } else {
//            QueryHelper_Setup.instance.mapSetting["DOWN_DATE"] = binding.dtpCkScheduleDateFrom.text.toString() + " ~ " + binding.dtpCkScheduleDateTo.text.toString()
//            Data.instance._downDate = QueryHelper_Setup.instance.mapSetting["DOWN_DATE"].toString()
//        }
        QueryHelper_Setup.instance.mapSetting["DOWN_DATE"] = binding.dtpCkScheduleDateFrom.text.toString()
        Data.instance._downDate = QueryHelper_Setup.instance.mapSetting["DOWN_DATE"].toString()
        QueryHelper_Setup.instance.querySettingSave()
    }
}


