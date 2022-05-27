package com.emaintec.lib.network

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class ReceiverBytes() : ReceiverBase<ByteArray>() {

	private var fileLength = 0
	private var downloadBytes = 0

	override fun process(inputStream: InputStream, dataLength: Int): String? {
		var errMessage: String? = null
		var byteArrayOutputStream: ByteArrayOutputStream? = null
		try {
			fileLength = dataLength
			byteArrayOutputStream = ByteArrayOutputStream(dataLength)
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
					byteArrayOutputStream.write(data, 0, readBytes)
					//					sender.publishProgress(connectionInfo);
				}
			}
			resultData = byteArrayOutputStream.toByteArray()
		} catch (e: Exception) {
			errMessage = e.localizedMessage
		} finally {
			try {
				byteArrayOutputStream?.close()
			} catch (ignored: IOException) {
			}

		}
		return errMessage
	}
}
