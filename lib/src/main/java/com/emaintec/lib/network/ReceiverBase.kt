package com.emaintec.lib.network

import java.io.InputStream

abstract class ReceiverBase<T> internal constructor() {

	var errorData: String? = null
		internal set
	var resultData: T? = null
		internal set


	abstract fun process( inputStream: InputStream, dataLength: Int): String?
}
