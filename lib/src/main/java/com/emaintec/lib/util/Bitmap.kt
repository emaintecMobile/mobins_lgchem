package com.emaintec.lib.util

//import androidx.exifinterface.media.ExifInterface;

import android.R
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64OutputStream
import android.widget.ImageView
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object Bitmap {
	/**
	 * 파일을  BASE64이미지로 인코딩.
	 * @param bm : 원본 비트맵 이미지
	 * @return : BASE64로 인코딩 된 값
	 */
	fun convertFileToBase64(imageFile: File): String {
		return FileInputStream(imageFile).use { inputStream ->
			ByteArrayOutputStream().use { outputStream ->
				Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
					inputStream.copyTo(base64FilterStream)
					base64FilterStream.close() // This line is required, see comments
					outputStream.toString()
				}
			}
		}
	}
	/**
	 * 비트맵을 BASE64이미지로 인코딩.
	 * @param bm : 원본 비트맵 이미지
	 * @return : BASE64로 인코딩 된 값
	 */
	fun bitmapToBase64(bm: android.graphics.Bitmap): String {

		val baos = ByteArrayOutputStream()
		bm.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, baos)
		val byteArray = baos.toByteArray()
		return Base64.encodeToString(byteArray, Base64.NO_WRAP)
	}

	/**
	 * 저장된 파일을 size의 비율로 비트맵 생성
	 * @param src : 원본 비트맵
	 * @param maxSize : 이미지 사이즈 지정(maxSize 맞는 스케일로 수정됨)
	 * @return
	 */
	fun resizeBitmap(src: android.graphics.Bitmap?, maxSize: Int): android.graphics.Bitmap? {

		if (0 < maxSize && null != src) {
			var bitmap: android.graphics.Bitmap? = null
			var imgWidth = src.width
			var imgHeight = src.height

			if (src.width < src.height) {
				imgWidth = (imgWidth * (maxSize.toFloat() / imgHeight.toFloat())).toInt()
				imgHeight = maxSize
			} else {
				imgHeight = (imgHeight * (maxSize.toFloat() / imgWidth.toFloat())).toInt()
				imgWidth = maxSize
			}

			try {
				bitmap = android.graphics.Bitmap.createScaledBitmap(src, imgWidth, imgHeight, true) // 이미지 사이즈 조정
			} catch (e: Exception) {
				return null
			}
			return bitmap
		}

		return src
	}

	/**
	 * 저장된 파일을 size의 비율로 비트맵 생성
	 * @param strFilePath : 저장된 파일 경로
	 * @param maxSize : 이미지 사이즈 지정(maxSize 맞는 스케일로 수정됨)
	 * @return
	 */
	fun resizeBitmap(strFilePath: String, maxSize: Int): android.graphics.Bitmap? {
		var bitmap: android.graphics.Bitmap? = BitmapFactory.decodeFile(strFilePath)
		val degrees = rotateImageRequired(strFilePath)
		var imgWidth: Int
		var imgHeight: Int

		if (0 < maxSize && null != bitmap) {
			imgWidth = bitmap.width
			imgHeight = bitmap.height

			if (bitmap.width < bitmap.height) {
				imgWidth = (imgWidth * (maxSize.toFloat() / imgHeight.toFloat())).toInt()
				imgHeight = maxSize
			} else {
				imgHeight = (imgHeight * (maxSize.toFloat() / imgWidth.toFloat())).toInt()
				imgWidth = maxSize
			}

			try {
				bitmap = android.graphics.Bitmap.createScaledBitmap(
					bitmap,
					imgWidth,
					imgHeight,
					true
				) // 이미지 사이즈 조정
			} catch (e: Exception) {
				return null
			}

		}

		if (degrees > 0 && null != bitmap) {
			val matrix = Matrix()
			matrix.setRotate(
				degrees.toFloat(),
				bitmap.width.toFloat() / 2,
				bitmap.height.toFloat() / 2
			)
			bitmap = android.graphics.Bitmap.createBitmap(
				bitmap,
				0,
				0,
				bitmap.width,
				bitmap.height,
				matrix,
				true
			)
		}

		return bitmap
	}

	/**
	 * 저장된 이미지의 회전도를 가져온다
	 * @param strFilePath : 이미지 경로
	 * @return : 회전 각도
	 */
	fun rotateImageRequired(strFilePath: String): Int {
		var ei: ExifInterface? = null
		var degrees = 0
//		Log.d("Bitmap.kt",strFilePath)

		try {
			ei = ExifInterface(strFilePath)
		} catch (e: IOException) {
			e.printStackTrace()
		}

		if (ei == null) {
			throw AssertionError()
		}

		val orientation = ei.getAttributeInt(
			ExifInterface.TAG_ORIENTATION,
			ExifInterface.ORIENTATION_UNDEFINED
		)

		when (orientation) {
			ExifInterface.ORIENTATION_ROTATE_90 -> degrees = 90
			ExifInterface.ORIENTATION_ROTATE_180 -> degrees = 180
			ExifInterface.ORIENTATION_ROTATE_270 -> degrees = 270
			ExifInterface.ORIENTATION_NORMAL -> {
			}
			else -> {
			}
		}

		return degrees
	}

	/**
	 * @param uri
	 * @param activity
	 * @return : url경로를 파일경로로 가져온다
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	fun uriToFile(uri: Uri, activity: Activity): File {

		var filePath: String? = ""

		if (uri.path!!.contains(":")) {
			//:이 존재하는 경우
			val wholeID = DocumentsContract.getDocumentId(uri)
			val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
			val column = arrayOf(MediaStore.Images.Media.DATA)
			val sel = MediaStore.Images.Media._ID + "=?"
			val cursor = activity.contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				column,
				sel,
				arrayOf(id),
				null
			)
			if (cursor != null) {
				val columnIndex = cursor.getColumnIndex(column[0])
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(columnIndex)
				}
				cursor.close()
			}
		} else {
			//:이 존재하지 않을경우
			//			String id					= uri.getLastPathSegment();
			val imageColumns = arrayOf(MediaStore.Images.Media.DATA)
			//			final String imageOrderBy	= null;
			//			String selectedImagePath	= "path";
			val scheme = uri.scheme
			if (null != scheme && scheme.equals("content", ignoreCase = true)) {
				val cursor = activity.contentResolver.query(uri, imageColumns, null, null, null)
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
					}
					cursor.close()
				}
			} else {
				filePath = uri.path
			}
		}

		return File(filePath!!)
	}

	/**
	 * dirPath에 시간값으로 파일명을 설정하고 저장
	 * @param bitmap
	 * @param dirPath : 저장할 디렉토리
	 * @return : 저장된 파일의 경로
	 */
	fun createImageFile(bitmap: android.graphics.Bitmap, dirPath: String): String? {

		var returnValue: String? = null
		val fileName: String

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			fileName = SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(Date())
		} else {
			fileName = String.format("%d", System.currentTimeMillis())
		}

		//String dirPath					= Functions.FilePath.getPath(Environment.DIRECTORY_PICTURES).getAbsoluteFile() + Define.PHOTO_SAVE_DIRECTORY;

		val dirFile = File(dirPath)
		if (!dirFile.exists() && !dirFile.mkdir()) {
			return returnValue
		}

		val file = File(dirFile.toString() + File.separator + fileName + ".png")

		/*
		File storageDir = Functions.FilePath.getPath(Environment.DIRECTORY_PICTURES).getAbsoluteFile();

		try {
			//file						= File.createTempFile(fileName, ".jpg", storageDir);
		} catch (IOException e) {

		}
		*/

		var out: OutputStream? = null
		try {
			out = FileOutputStream(file)
			bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
			returnValue = file.absolutePath
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			if (out != null) {
				try {
					out.close()
				} catch (e: Exception) {
					e.printStackTrace()
				}

			}
		}
		return returnValue
	}

	fun createImageFileData(imageView: ImageView?): ByteArray? {

		if (null == imageView)
			return null

		val drawable = imageView.drawable ?: return null

		val bitmap = (drawable as BitmapDrawable).bitmap ?: return null

		val byteArrayOutputStream = ByteArrayOutputStream()
		bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

		return byteArrayOutputStream.toByteArray()
	}

	fun createImageFileData(bitmap: android.graphics.Bitmap?): ByteArray? {

		if (null == bitmap)
			return null

		val byteArrayOutputStream = ByteArrayOutputStream()
		bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

		return byteArrayOutputStream.toByteArray()
	}



}
