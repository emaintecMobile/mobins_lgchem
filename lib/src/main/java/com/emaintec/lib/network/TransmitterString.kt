package com.emaintec.lib.network

import android.net.Uri

import java.io.*
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.nio.charset.StandardCharsets

class TransmitterString(
	url: String,
	private val data: String? = null
) : TransmitterBase(url) {

	override fun process(httpURLConnection: HttpURLConnection): String? {

		var outputStream: OutputStream? = null
		var writer: BufferedWriter? = null
		var errMessage: String? = null

		try {
			httpURLConnection.connectTimeout = 30 * 1000
			httpURLConnection.readTimeout = 60 * 1000
			httpURLConnection.requestMethod = "POST"
			httpURLConnection.doOutput = true                // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
			httpURLConnection.doInput = true                    // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

			val builder = Uri.Builder()
			if (data != null) {
				builder.appendQueryParameter("data", data)
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
				errMessage = "{\"success\":false, \"message\":\"The server information is incorrect.\"}"
			}
		} catch (e: UnsupportedEncodingException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage + "\"}"
		} catch (e: ProtocolException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage + "\"}"
		} catch (e: IOException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage + "\"}"
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
