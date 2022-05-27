package com.emaintec.lib.network

import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ReceiverFile(savePath: String) : ReceiverBase<String>() {

	private val savePath: String? = savePath
	private var fileLength = 0
	private var downloadBytes = 0

	override fun process(inputStream: InputStream, dataLength: Int): String? {
		var errMessage: String? = null
		var outputStream: OutputStream? = null
		try {
			fileLength = dataLength
			outputStream = FileOutputStream(savePath!!)
			val data = ByteArray(4096)
			var readBytes: Int
			while (true) {
				readBytes = inputStream.read(data)
				if (readBytes == -1) {
					break;
				}
				// publishing the progress....
				if (dataLength > 0) {
					downloadBytes += readBytes
					// only if total length is known
					outputStream.write(data, 0, readBytes)
					//					sender.publishProgress(connectionInfo);
				}
			}
			resultData = "{\"success\":true, \"message\":\"$savePath\"}"
		} catch (e: Exception) {
			errMessage = e.localizedMessage
		} finally {
			try {
				outputStream?.close()
			} catch (ignored: IOException) {
			}

		}
		return errMessage
	}
}
