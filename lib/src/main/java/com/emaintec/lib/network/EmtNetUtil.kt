package com.emaintec.lib.network

import androidx.annotation.WorkerThread
import org.json.JSONObject
import java.util.*


object EmtNetUtil {
    @WorkerThread
    suspend fun  getNetworkJson(
        SEVICE_URL:String,
        RESULT_DATA: String? = null,
        RESULT_LIST: String? = null,
        action: suspend (Boolean, String, String) -> Unit
        
    ) {
        NetworkSync(TransmitterJsonPlus(url =  SEVICE_URL, RESULT_DATA = RESULT_DATA,RESULT_LIST = RESULT_LIST), ReceiverJson()).get(onSuccessed = {
            val jsonObject = it.receiver.resultData!!
            if ("F".equals(jsonObject.getString("resultSts"))) {
                action(false, "", jsonObject.get("resultMsg").toString())
                return@get
            }
            val jsonArray = jsonObject.get("resultList").toString()
            var msg = ""
            if(jsonObject.has("resultMsg"))
            {
                msg =jsonObject.get("resultMsg").toString()
            }
            action(true, jsonArray,msg)
        }, onFailed = {
            val jsonObject = JSONObject(it.receiver.errorData!!)
            var msg = ""
            if(jsonObject.has("resultMsg"))
            {
                msg =jsonObject.get("resultMsg").toString()
            }
            action(false, "",msg)
        })
    }

}


