package com.emaintec.lib.network

import android.net.Uri
import com.google.gson.Gson
import com.emaintec.lib.device.Device

import java.io.*
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.nio.charset.StandardCharsets
import java.util.HashMap

class TransmitterJson : TransmitterBase {

	private val action: String?
	private val jsondata: String?
	private val result: String?
	private var timeout = 60
	private val fileName: String?

	constructor(url: String, action: String?=null, jsondata: String?=null, result: String?=null, timeout:Int = 60) : super(url) {
		this.action = action
		this.jsondata = jsondata
		this.result = result
		this.timeout = timeout
		fileName = null
	}

	constructor(url: String, fileName: String) : super(url) {
		action = null
		jsondata = null
		result = null
		this.fileName = fileName
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

		try {
			httpURLConnection.connectTimeout = 30 * 1000
			httpURLConnection.readTimeout = timeout * 1000
			httpURLConnection.requestMethod = "POST"
			httpURLConnection.doOutput = true                // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
			httpURLConnection.doInput = true                    // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

			val builder = Uri.Builder()
			if (action != null) {
				builder.appendQueryParameter("device", Gson().toJson(arrayListOf(device)))
				builder.appendQueryParameter("action", action)
				if (jsondata != null) {
					builder.appendQueryParameter("jsondata", jsondata)
				}
				if (result != null) {
					builder.appendQueryParameter("result", result)
				}

			} else if (fileName != null) {
				builder.appendQueryParameter("fileName", fileName)
			}
			builder.build().encodedQuery?.let {
				outputStream = httpURLConnection.outputStream
				writer = BufferedWriter(OutputStreamWriter(outputStream!!, StandardCharsets.UTF_8), it.length)
				writer!!.write(it)
				writer!!.flush()
			}

			httpURLConnection.connect()

			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (httpURLConnection.responseCode != HttpURLConnection.HTTP_OK) {
				errMessage = "{\"success\":false, \"message\":\"주어진 서버의 정보가 잘못 되었습니다.\"}"
			}
		} catch (e: UnsupportedEncodingException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage.replace("\"","") + "\"}"
		} catch (e: ProtocolException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage.replace("\"","") + "\"}"
		} catch (e: IOException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage.replace("\"","") + "\"}"
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
