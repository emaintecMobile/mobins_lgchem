package com.emaintec.lib.network

import java.io.InputStream

abstract class ReceiverBase<T> internal constructor(listener: NetworkTask.OnProgressUpdateListener?) {

	var errorData: String? = null
		internal set
	var resultData: T? = null
		internal set
	internal var onProgressUpdateListener: NetworkTask.OnProgressUpdateListener? = null

	init {
		onProgressUpdateListener = listener
	}

	abstract fun process(connectionTask: NetworkTask?, inputStream: InputStream, dataLength: Int): String?
}
