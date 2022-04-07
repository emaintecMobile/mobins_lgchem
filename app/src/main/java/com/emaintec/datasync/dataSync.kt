package com.emaintec.datasync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.emaintec.Data
import com.emaintec.Define
import com.emaintec.Fragment_Base
import com.emaintec.Functions
import com.emaintec.common.helper.commonHelper
import com.emaintec.datasync.helper.dataSyncHelper
import com.emaintec.db.QueryHelper_Setup
import com.emaintec.lib.db.DBSwitcher
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

class dataSync : Fragment_Base() {
    lateinit var binding: DatasyncBinding
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
        initSpinner()
        initButton()
    }

    private fun initSpinner() {
        val jsonobj = JsonObject()
        jsonobj.addProperty("DIR_TYPE", "WAREHOUSE")
        val jsonWareHouse = Gson().toJson(arrayOf(jsonobj))
        CoroutineScope(Dispatchers.Default).launch {
            commonHelper.instance.getDirDtlList({ success, list, msg ->
                if (success) {
                    launch(Dispatchers.Main) {
                        if (list != null) {
                            val adapter = ArrayAdapter(
                                context!!,
                                R.layout.custom_spinner_item_left_black,
                                list
                            )
                            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
                            binding.spinnerWAREHOUSE.adapter = adapter
                        }
                    }.join()
                }
            }, jsonWareHouse)
        }
    }

    private fun initButton() {
        binding.customLayoutTitle.imageButton.setOneClickListener {
//            dismiss() 는 Activice_Main.kt 에 it.dialog!!.setOnDismissListener 리스너를 통하지 않고 닫는다.. 그래서 dialog.dismiss()를 사용한다.
            dialog!!.dismiss()
        }
        binding.buttonDownload.setOneClickListener {
            download()
        }
        binding.buttonUpload.setOneClickListener {
            upload()
        }
    }

    private fun upload() {

        NetworkProgress.start(activity!!)
        NetworkProgress.end()
    }

    private fun download() {
        NetworkProgress.start(activity!!)
        CoroutineScope(Dispatchers.Default).launch {
            if (Functions.UpdateApp.Autoupdate()) return@launch
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
        jsonobject.addProperty("WKCENTER", Data.instance._workCenter)
        array.add(jsonobject)
        val jsonData = gson.toJson(array)
        CoroutineScope(Dispatchers.Default).launch {
            launch(Dispatchers.Main) {
                binding.textViewCkResult.text = "다운중"
            }.join()
            val bResult = dataSyncHelper.Check.GetCkMst({ success: Boolean, msg: String ->
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
        jsonobject.addProperty("WKCENTER", Data.instance._workCenter)
        jsonobject.addProperty("FRDATE", binding.dtpCkScheduleDateFrom.text.toString())
        jsonobject.addProperty("TODATE", binding.dtpCkScheduleDateTo.text.toString())
        array.add(jsonobject)
        val jsonData = gson.toJson(array)

        CoroutineScope(Dispatchers.Default).launch {
            launch(Dispatchers.Main) {
                binding.textViewCkDaily.text = "다운중"
            }.join()
            val bResult = dataSyncHelper.Check.GetCkDaily({ success: Boolean, msg: String ->
                launch(Dispatchers.Main) {
                    binding.textViewCkDaily.text = msg
                }.join()
            }, jsonData)
        }.join()

        if(binding.dtpCkScheduleDateFrom.text.toString().equals(binding.dtpCkScheduleDateTo.text.toString())) {
            QueryHelper_Setup.instance.mapSetting["DOWN_DATE"] = binding.dtpCkScheduleDateFrom.text.toString()
            Data.instance._downDate =  QueryHelper_Setup.instance.mapSetting["DOWN_DATE"].toString()
        }else{
            QueryHelper_Setup.instance.mapSetting["DOWN_DATE"] = binding.dtpCkScheduleDateFrom.text.toString()+ " ~ " +binding.dtpCkScheduleDateTo.text.toString()
            Data.instance._downDate =   QueryHelper_Setup.instance.mapSetting["DOWN_DATE"].toString()
        }
    }
}


