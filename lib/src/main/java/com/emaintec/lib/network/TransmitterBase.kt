package com.emaintec.lib.network

import java.net.HttpURLConnection

abstract class TransmitterBase internal constructor(val url: String) {

	abstract fun process(httpURLConnection: HttpURLConnection): String?
}
