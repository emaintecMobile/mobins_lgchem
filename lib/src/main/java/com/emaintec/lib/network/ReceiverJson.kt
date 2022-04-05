package com.emaintec.lib.network

import org.json.JSONException
import org.json.JSONObject
import java.io.*

class ReceiverJson(listener: NetworkTask.OnProgressUpdateListener? = null) : ReceiverBase<JSONObject>(listener) {

	override fun process(connectionTask: NetworkTask?, inputStream: InputStream, dataLength: Int): String? {
		var errMessage: String? = null
		try {
			val bufferedReader = BufferedReader(InputStreamReader(inputStream) as Reader?)
			var line: String? = ""
			val stringBuilder = StringBuilder()
			while (true) {
				line = bufferedReader.readLine()
				if (line == null) {
					break;
				}
				stringBuilder.append(line)
			}
			resultData = JSONObject(stringBuilder.toString())
		} catch (e: IOException) {
			errMessage = e.localizedMessage
		} catch (e: JSONException) {
			errMessage = e.localizedMessage
		}

		return errMessage
	}
}
