package com.emaintec.lib.file

import android.os.Environment

import java.io.File
import java.util.ArrayList
import java.util.Arrays

class FileFinder private constructor() {

	private var onFileFinderListener: OnFileFinderListener? = null
	private var isReadRecurcevly = false
	private var extEntries = ArrayList<String>()

	class FileInfo(
		var fileName: String,
		var filePath: String,
		var fileSize: Long,
		var canRead: Boolean,
		var canWrite: Boolean
	)

	interface OnFileFinderListener {
		fun onFound(fileInfo: FileInfo)
		fun onCompleted()
	}

	fun setOnFileLoaderListener(listener: OnFileFinderListener?) {
		onFileFinderListener = listener
	}

	fun setReadRecursive(rec: Boolean) {
		isReadRecurcevly = rec
	}

	fun setEnabledExtensions(arr: ArrayList<String>) {
		extEntries = arr
	}

	fun getFileInfo(filePath: String): FileInfo? {

		val file = File(filePath)

		return if (!file.exists() || !file.isFile) null else FileInfo(
			file.name,
			file.absolutePath,
			file.length(),
			file.canRead(),
			file.canWrite()
		)

	}

	fun build() {
		Thread(Runnable {
			rebuildSync()
			onFileFinderListener?.onCompleted()
		}).start()
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private object SingletonHolder {
		internal val INSTANCE = FileFinder()
	}

	private fun readDir(dir: File) {
		val files = dir.listFiles { pathname ->
			var bShowFile = false
			for (iter in extEntries) {
				bShowFile = bShowFile or pathname.name.toLowerCase().endsWith(iter)
			}
			bShowFile || pathname.isDirectory && isReadRecurcevly
		}
		if (files != null) {
			//이름별로 정렬한다.
			Arrays.sort(files)
			for (file in files) {
				if (file.isDirectory) {
					readDir(file)
				} else if (file.isFile && null != onFileFinderListener) {
					onFileFinderListener!!.onFound(FileInfo(file.name, file.absolutePath, file.length(), file.canRead(), file.canWrite()))
				}
			}
		}
	}

	private fun rebuildSync() {
		val sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
		//File sdcard = Environment.getExternalStorageDirectory();
		//File sdcard = new File(String.valueOf(Betools.getApplication().getApplicationContext().getFilesDir().getAbsoluteFile()));
		if (sdcard.isDirectory) {
			readDir(sdcard)
		}
	}

	companion object {
		val instance: FileFinder
			get() = SingletonHolder.INSTANCE
	}
}
