package com.emaintec.lib.network

import android.net.Uri
import com.google.gson.Gson
import java.io.*
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URLConnection

class TransmitterFile(
	url: String,
	private val action: String? = null,
	private val jsondata: String? = null,
	private val file: File
) : TransmitterBase(url) {

	override fun process(httpURLConnection: HttpURLConnection): String? {

		var outputStream: OutputStream? = null
		var writer: PrintWriter? = null
		var errMessage: String? = null

		try {
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary=$BOUNDARY")
			httpURLConnection.requestMethod = "POST"
			httpURLConnection.doInput = true
			httpURLConnection.doOutput = true
			httpURLConnection.useCaches = false
			httpURLConnection.connectTimeout = 15000
		
			outputStream = httpURLConnection.outputStream
			writer = PrintWriter(OutputStreamWriter(outputStream!!, CHARSET), true)

			/* Body에 데이터를 넣어줘야 할경우 없으면 Pass */
//			writer.append("--$BOUNDARY").append(LINE_FEED)
//			writer.append("Content-Disposition: form-data; name=\"action\"").append(LINE_FEED)
//			writer.append("Content-Type: text/plain; charset=$CHARSET").append(LINE_FEED)
//			writer.append(LINE_FEED)
//			writer.append(action).append(LINE_FEED)
//			writer.flush()
			
			writer.append("--$BOUNDARY").append(LINE_FEED)
			writer.append("Content-Disposition: form-data; name=\"jsondata\"").append(LINE_FEED)
			writer.append("Content-Type: text/plain; charset=$CHARSET").append(LINE_FEED)
			writer.append(LINE_FEED)
			writer.append(jsondata).append(LINE_FEED)
			writer.flush()
			/* 파일 데이터를 넣는 부분 */
			writer.append("--$BOUNDARY").append(LINE_FEED)
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.name).append("\"")
				.append(LINE_FEED)
			writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.name)).append(LINE_FEED)
			writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
			writer.append(LINE_FEED)
			writer.flush()

			val inputStream = FileInputStream(file)
			val buffer = ByteArray(file.length().toInt())
			var bytesRead = -1
			while (true) {
				bytesRead = inputStream.read(buffer)
				if (bytesRead == -1) {
					break
				}
				outputStream.write(buffer, 0, bytesRead)
			}
			outputStream.flush()
			inputStream.close()
			writer.append(LINE_FEED)
			writer.flush()

			writer.append("--$BOUNDARY--").append(LINE_FEED)
			writer.close()

			val responseCode = httpURLConnection.responseCode
			if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
				val bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.errorStream))
				var inputLine: String
				val stringBuilder = StringBuilder()
				while (true) {
					inputLine = bufferedReader.readLine()
					if (inputLine == null) {
						break;
					}
					stringBuilder.append(inputLine)
				}
				bufferedReader.close()
				errMessage = "{\"success\":false, \"message\":\"$stringBuilder\"}"
			}
		} catch (e: UnsupportedEncodingException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage + "\"}"
		} catch (e: FileNotFoundException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage + "\"}"
		} catch (e: ProtocolException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage + "\"}"
		} catch (e: IOException) {
			errMessage = "{\"success\":false, \"message\":\"" + e.localizedMessage + "\"}"
		}

		return errMessage
	}

	companion object {
		private val BOUNDARY = "^-----^"
		private val LINE_FEED = "\r\n"
		private val CHARSET = "UTF-8"
	}
}
