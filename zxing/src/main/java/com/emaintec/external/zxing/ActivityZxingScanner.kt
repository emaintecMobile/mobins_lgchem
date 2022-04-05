package com.emaintec.external.zxing

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView


class ActivityZxingScanner : Activity(), DecoratedBarcodeView.TorchListener {

	private var _switchFlashlightButtonCheck: Boolean = true
	private lateinit var _capture: CaptureManager
	private lateinit var _barcodeScannerView: DecoratedBarcodeView
	private lateinit var _switchFlashlightButton: ImageButton
	//	private lateinit var _settingButton: ImageButton

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_zxing_scanner)

//		_settingButton = setting_btn
		_switchFlashlightButton = findViewById(R.id.switch_flashlight)

		if (!hasFlash()) {
			_switchFlashlightButton.visibility = View.GONE
		}

		_barcodeScannerView =  findViewById(R.id.zxing_barcode_scanner)
		_barcodeScannerView.setTorchListener(this)
		_capture = CaptureManager(this, _barcodeScannerView)
		_capture.initializeFromIntent(intent, savedInstanceState)
		_capture.decode()
	}

	override fun onResume() {
		super.onResume()
		_capture.onResume()
	}

	override fun onPause() {
		super.onPause()
		_capture.onPause()
	}

	override fun onDestroy() {
		super.onDestroy()
		_capture.onDestroy()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		_capture.onSaveInstanceState(outState)
	}

	override fun onBackPressed() {
		finish()
	}

	fun switchFlashlight(view: View) {
		if (_switchFlashlightButtonCheck) {
			_barcodeScannerView.setTorchOn()
		} else {
			_barcodeScannerView.setTorchOff()
		}
	}

	private fun hasFlash(): Boolean {
		return applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
	}

	override fun onTorchOn() {
		_switchFlashlightButton.setImageResource(R.drawable.ic_flash_on_white_36dp)
		_switchFlashlightButtonCheck = false
	}

	override fun onTorchOff() {
		_switchFlashlightButton.setImageResource(R.drawable.ic_flash_off_white_36dp)
		_switchFlashlightButtonCheck = true
	}
}