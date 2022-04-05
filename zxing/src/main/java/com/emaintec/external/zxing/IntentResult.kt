package com.emaintec.external.zxing

import com.google.zxing.integration.android.IntentResult

class IntentResult constructor(private val intentResult: IntentResult) {

	val contents: String
		get() = intentResult.contents

	val formatName: String
		get() = intentResult.formatName

	val rawBytes: ByteArray
		get() = intentResult.rawBytes

	val orientation: Int
		get() = intentResult.orientation

	val errorCorrectionLevel: String
		get() = intentResult.errorCorrectionLevel

	val barcodeImagePath: String
		get() = intentResult.barcodeImagePath
}