package com.emaintec.lib.file

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.emaintec.lib.base.Emaintec
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

	fun getPathFromUri(uri: Uri): String? {
		assert(Emaintec.application != null)

		var path: String? = null
		val cursor = Emaintec.application!!.contentResolver.query(uri, null, null, null, null)
		if (cursor != null) {
			cursor.moveToNext()
			path = cursor.getString(cursor.getColumnIndex("_data"))
			cursor.close()
		}
		return path
	}

	fun getUriFromPath(path: String): Uri? {
		assert(Emaintec.application != null)

		var uri: Uri? = null
		/*
		String fileName= "file:///sdcard/DCIM/Camera/2013_07_07_12345.jpg";
		Uri fileUri = Uri.parse( fileName );
		path = fileUri.getPath();
*/
		val cursor = Emaintec.application!!.contentResolver.query(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			null,
			"_data = '$path'",
			null,
			null
		)

		if (cursor != null) {
			cursor.moveToNext()
			val id = cursor.getInt(cursor.getColumnIndex("_id"))
			uri = ContentUris.withAppendedId(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				id.toLong()
			)
			cursor.close()
		}

		return uri
	}

	@SuppressLint("SimpleDateFormat")
	fun backupFile(
		srcFilePath: String,
		destDirectory: String,
		dateFormat: String? = "yyyyMMddhhmmss"
	) : Boolean {
		try {
			val srcFile = File(srcFilePath)
			val suffix = if (dateFormat != null) "_" + SimpleDateFormat(dateFormat).format(Date()) else ""
			val destFilePath = destDirectory + File.separator + srcFile.name.replace(
				srcFile.extension,
				""
			) + suffix + "." + srcFile.extension
			srcFile.copyTo(File(destFilePath))
		} catch (e: Exception) {
			e.stackTrace
			return false
		}
		return true
	}
	fun trimCache(context: Context) {
		try {
			val dir: File = context.getCacheDir()
			if (dir != null && dir.isDirectory) {
				deleteDir(dir)
			}
		} catch (e: Exception) {
			// TODO: handle exception
		}
	}

	fun deleteDir(dir: File?): Boolean {
		if (dir != null && dir.isDirectory) {
			val children = dir.list()
			for (i in children.indices) {
				val success = deleteDir(File(dir, children[i]))
				if (!success) {
					return false
				}
			}
		}

		// The directory is now empty so delete it
		return dir!!.delete()
	}

}
