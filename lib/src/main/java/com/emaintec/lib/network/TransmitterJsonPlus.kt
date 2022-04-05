package com.emaintec.lib.network

import android.net.Uri
import com.google.gson.Gson
import com.emaintec.lib.device.Device
import com.google.gson.JsonObject

import java.io.*
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.nio.charset.StandardCharsets
import java.util.HashMap

class TransmitterJsonPlus : TransmitterBase {

    private val RESULT_DATA: String?
    private val RESULT_LIST: String?


    constructor(url: String, RESULT_DATA: String? = null, RESULT_LIST: String? = null) : super(url) {
        this.RESULT_DATA = RESULT_DATA
        this.RESULT_LIST = RESULT_LIST
    }

    override fun process(httpURLConnection: HttpURLConnection): String? {


        var outputStream: OutputStream? = null
        var writer: BufferedWriter? = null
        var errMessage: String? = null
        val device = HashMap<String, String>()
        device["ID"] = Device.ANDROID_ID
        device["MODEL"] = Device.model
        device["OS"] = Device.osVersion
        device["VERSION"] = Device.versionName
        device["PACKAGE"] = Device.packageName
        device["USER"] = Device.user
        device["resultData"] = RESULT_DATA?:""
        device["resultList"] = RESULT_LIST?:""
        var jData = JsonObject()
        try {
//            val builder = Uri.Builder()
//            builder.appendQueryParameter("device", Gson().toJson(arrayListOf(device)))
//            if (RESULT_DATA != null) {
//                builder.appendQueryParameter("resultData", RESULT_DATA)
//            }
//            if (RESULT_LIST != null) {
//                builder.appendQueryParameter("resultList", RESULT_LIST)
//            }

//            jData.addProperty("device", Gson().toJson(device).toString())
//            jData.addProperty("resultData", RESULT_DATA?:"")
//            jData.addProperty("resultList", RESULT_LIST?:"")
/////////////
            val jsonData = Gson().toJson(jData)
            httpURLConnection.setRequestProperty("Accept", "application/json")
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
///////////////
            httpURLConnection.connectTimeout = 30 * 1000
            httpURLConnection.readTimeout = 60 * 1000
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true                // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpURLConnection.doInput = true                    // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

//            builder.build().encodedQuery?.let {
//                outputStream = httpURLConnection.outputStream
//                writer = BufferedWriter(OutputStreamWriter(outputStream!!, StandardCharsets.UTF_8), it.length)
//                writer!!.write(it)
//                writer!!.flush()
//            }
            Gson().toJson(device).toString()?.let {
                    outputStream = httpURLConnection.outputStream
                    writer = BufferedWriter(OutputStreamWriter(outputStream!!, StandardCharsets.UTF_8), it.length)
                    writer!!.write(it)
                    writer!!.flush()
                }

            httpURLConnection.connect()

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (httpURLConnection.responseCode != HttpURLConnection.HTTP_OK) {
                errMessage = "{\"resultSts\":\"F\", \"resultMsg\":\"주어진 서버의 정보가 잘못 되었습니다.\"}"
            }
        } catch (e: UnsupportedEncodingException) {
            errMessage = "{\"resultSts\":\"F\", \"resultMsg\":\"" + e.localizedMessage + "\"}"
        } catch (e: ProtocolException) {
            errMessage = "{\"resultSts\":\"F\", \"resultMsg\":\"" + e.localizedMessage + "\"}"
        } catch (e: IOException) {
            errMessage = "{\"resultSts\":\"F\", \"resultMsg\":\"" + e.localizedMessage + "\"}"
        } finally {
            try {
                writer?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return errMessage
    }
}
