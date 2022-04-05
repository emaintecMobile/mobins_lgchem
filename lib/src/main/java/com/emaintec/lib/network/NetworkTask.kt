package com.emaintec.lib.network

import android.os.AsyncTask
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class NetworkTask : AsyncTask<ConnInfo<out TransmitterBase, out ReceiverBase<*>>, ConnInfo<out TransmitterBase, out ReceiverBase<*>>, Boolean>() {

	private var onPreExecuteListener: OnPreExecuteListener? = null
	private var onProgressUpdateListener: OnProgressUpdateListener? = null
	private var onPostExecuteListener: OnPostExecuteListener? = null
	private var onCancelledListener: OnCancelledListener? = null

	interface OnPreExecuteListener {
		fun onPreExecute()
	}

	interface OnProgressUpdateListener {
		fun onProgressUpdate(progress: ConnInfo<out TransmitterBase, out ReceiverBase<*>>)
	}

	interface OnPostExecuteListener {
		fun onPostExecute(completed: Boolean)
	}

	interface OnCancelledListener {
		fun onCancelled()
	}

	fun addOnPreExecuteListener(listener: OnPreExecuteListener): NetworkTask {
		onPreExecuteListener = listener
		return this
	}

	fun addOnProgressUpdateListener(listener: OnProgressUpdateListener): NetworkTask {
		onProgressUpdateListener = listener
		return this
	}

	fun addOnPostExecuteListener(listener: OnPostExecuteListener): NetworkTask {
		onPostExecuteListener = listener
		return this
	}

	fun addOnCancelledListener(listener: OnCancelledListener): NetworkTask {
		onCancelledListener = listener
		return this
	}

	override fun doInBackground(vararg connInfos: ConnInfo<out TransmitterBase, out ReceiverBase<*>>): Boolean? {

		for (connInfo in connInfos) {
			if (connInfo.transmitter.url.startsWith("https://")) {
				NetworkSSL.trustAllHosts()
				break
			}
		}

		for (connInfo in connInfos) {
			var httpURLConnection: HttpURLConnection? = null
			var inputStream: InputStream? = null
			try {
				if (connInfo.transmitter.url.startsWith("https://")) {
					val httpsURLConnection = URL(connInfo.transmitter.url).openConnection() as HttpsURLConnection
					httpsURLConnection.setHostnameVerifier { hostname, session -> true }
					httpURLConnection = httpsURLConnection
				} else {
					httpURLConnection = URL(connInfo.transmitter.url).openConnection() as HttpURLConnection
				}

				connInfo.receiver.errorData = connInfo.transmitter.process(httpURLConnection)
				if (connInfo.receiver.errorData != null) {
					publishProgress(connInfo)
					return false
				}

				inputStream = httpURLConnection.inputStream
				connInfo.receiver.errorData = connInfo.receiver.process(this, inputStream!!, httpURLConnection.contentLength)
				publishProgress(connInfo)
				if (connInfo.receiver.errorData != null) {
					return false
				}
			} catch (e: Exception) {
				connInfo.receiver.errorData = "{\"success\":false, \"message\":\"" + e.localizedMessage + "\"}"
				publishProgress(connInfo)
				return false
			} finally {
				try {
					inputStream?.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}

				httpURLConnection?.disconnect()
			}
		}

		return true
	}

	override fun onPreExecute() {
		onPreExecuteListener?.onPreExecute()
	}

	override fun onProgressUpdate(vararg connInfos: ConnInfo<out TransmitterBase, out ReceiverBase<*>>) {
		connInfos[0].receiver.onProgressUpdateListener?.onProgressUpdate(connInfos[0])
		onProgressUpdateListener?.onProgressUpdate(connInfos[0])
	}

	override fun onPostExecute(result: Boolean?) {
		onPostExecuteListener?.onPostExecute(result!!)
	}

	override fun onCancelled() {
		onCancelledListener?.onCancelled()
	}
}
