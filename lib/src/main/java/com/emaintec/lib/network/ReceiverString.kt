package com.emaintec.lib.network

import java.io.*

class ReceiverString() : ReceiverBase<String>() {

	override fun process(inputStream: InputStream, dataLength: Int): String? {
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
			resultData = stringBuilder.toString()
		} catch (e: IOException) {
			errMessage = e.localizedMessage
		}

		return errMessage
	}
}
