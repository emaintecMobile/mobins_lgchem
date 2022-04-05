package com.emaintec.lib.device

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build

import com.emaintec.lib.base.Emaintec

class Flash private constructor() {
	private var camera: android.hardware.Camera? = null
	var isFlashOn = false
		private set

	/*
	public Intent getCameraIntent(Context context, String fileName) {
		Intent chooserIntent = null;
		List<Intent> intentList = new ArrayList<>();
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePhotoIntent.putExtra("return-data", true);
		takePhotoIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, Betools.getApplication().getResources().getConfiguration().orientation);
		takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Betools.getApplication().getExternalCacheDir(), fileName)));
		intentList = addIntentsToList(Betools.getApplication(), intentList, Collections.singletonList(takePhotoIntent));

		if (intentList.size() > 0) {
			String title = Betools.getApplication().getResources().getString(R.string.choose);
			chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), title);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
		}
		return chooserIntent;
	}
*/
	fun flashOn() {
		assert(Emaintec.application != null)

		if (!isFlashOn) {
			isFlashOn = true

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				val cameraManager = Emaintec.application!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
				try {
					val cameraId = cameraManager.cameraIdList[0]        // Usually front camera is at 0 position.
					cameraManager.setTorchMode(cameraId, true)
				} catch (e: CameraAccessException) {
					isFlashOn = false
					e.printStackTrace()
				}
			} else {
				camera = android.hardware.Camera.open()
				val parameters = camera!!.parameters
				parameters.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_TORCH
				camera!!.parameters = parameters
				camera!!.startPreview()
			}
		}
	}

	fun flashOff() {
		assert(Emaintec.application != null)

		if (isFlashOn) {
			isFlashOn = false

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				val cameraManager = Emaintec.application!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
				try {
					val cameraId = cameraManager.cameraIdList[0]        // Usually front camera is at 0 position.
					cameraManager.setTorchMode(cameraId, false)
				} catch (e: CameraAccessException) {
					isFlashOn = true
					e.printStackTrace()
				}
			} else {
				val parameters = camera!!.parameters
				parameters.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_OFF
				camera!!.parameters = parameters
				camera!!.stopPreview()
				camera!!.release()
				camera = null
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private object SingletonHolder {
		internal val INSTANCE = Flash()
	}

	companion object {
		val instance: Flash
			get() = SingletonHolder.INSTANCE
	}
}
