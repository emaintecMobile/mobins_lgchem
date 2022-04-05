package com.emaintec

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.emaintec.mobins.BuildConfig
import com.emaintec.lib.base.Emaintec.activity
import java.io.*
import java.net.*

@Suppress("DEPRECATION")
class FileHelper private constructor() {
    private var progressBar: ProgressDialog? = null
    fun DownloadFile2(FilePath: String?, DATA_NO: String?) {
        progressBar = ProgressDialog(activity)
        progressBar!!.setMessage("다운로드중")
        progressBar!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressBar!!.isIndeterminate = true
        progressBar!!.setCancelable(true)
        val downloadTask = DownloadFilesTask(activity)
        downloadTask.execute(DATA_NO, FilePath)
    }

    private fun showDownloadFile(Save_Path: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val file = File(Save_Path)
        if (Save_Path.lastIndexOf(".") < 0) {
            return
        }
        var File_extend = Save_Path.substring(Save_Path.lastIndexOf(".") + 1)
        File_extend = File_extend.toLowerCase()
        var uri: Uri? = null
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                activity!!,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
        // 파일 확장자 별로 mime type 지정해 준다.
        if (File_extend == "mp3") {
            intent.setDataAndType(uri, "audio/*")
        } else if (File_extend == "mp4") {
            intent.setDataAndType(uri, "video/*")
        } else if (File_extend == "avi") {
            intent.setDataAndType(uri, "video/*")
        } else if (File_extend == "jpg" || File_extend == "jpeg" || File_extend == "JPG" || File_extend == "gif" || File_extend == "png" || File_extend == "bmp") {
            intent.setDataAndType(uri, "image/*")
            //            Uri photoURI = Uri.fromFile(createImageFile());
        } else if (File_extend == "txt") {
            intent.setDataAndType(uri, "text/*")
        } else if (File_extend == "doc" || File_extend == "docx") {
            intent.setDataAndType(uri, "application/msword")
        } else if (File_extend == "xls" || File_extend == "xlsx") {
            intent.setDataAndType(
                uri, "application/vnd.ms-excel"
            )
        } else if (File_extend == "ppt" || File_extend == "pptx") {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        } else if (File_extend == "pdf") {
            intent.setDataAndType(uri, "application/pdf")
        } else if (File_extend == "apk") {
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            if (Build.VERSION.SDK_INT < 24) {
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK // without this flag android returned a intent error!
            } else {
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
            }
        } else {
            intent.setDataAndType(uri, "application/pdf")
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity!!.startActivity(Intent.createChooser(intent, null))
    }

    inner class DownloadFilesTask(private val context: Context?) :
        AsyncTask<String?, String?, Long>() {
        private var mWakeLock: PowerManager.WakeLock? = null
        private var outputFile //파일명까지 포함한 경로
                : File? = null
        private val path //디렉토리경로
                : File? = null

        //파일 다운로드를 시작하기 전에 프로그레스바를 화면에 보여줍니다.
        override fun onPreExecute() { //2
            super.onPreExecute()
            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            val pm = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name)
            mWakeLock?.acquire()
            progressBar!!.show()
        }

        //파일 다운로드를 진행합니다.
        override fun doInBackground(vararg string_name: String?): Long {
            var count: Int
            var FileSize: Long = -1
            val input: InputStream? = null
            val output: OutputStream? = null
            var Read: Int
            // URLConnection connection = null;
            var strSendData = ""
            try {
                val strUrl = Data.instance.url + "/downloadFile" //"/ct_filedownload.jsp"
                val url = URL(strUrl)
                val httpCon = url.openConnection() as HttpURLConnection
                httpCon.requestMethod = "POST"
                httpCon.doOutput = true
                httpCon.doInput = true
                val builder = Uri.Builder()
                builder.appendQueryParameter("DATA_NO", string_name[0])
                strSendData = builder.build().encodedQuery!!
                val os = httpCon.outputStream
                val writer = BufferedWriter(
                    OutputStreamWriter(os, "UTF-8")
                )
                writer.write(strSendData)
                writer.flush()
                writer.close()
                os.close()
                httpCon.connect()

                //파일 크기를 가져옴
                FileSize = httpCon.contentLength.toLong()
                val LocalPath =
                    string_name[1] //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + string_name[0];
                val tmpByte = ByteArray(1024)
                val inpStr = httpCon.inputStream
                outputFile = File(LocalPath)
                val fos = FileOutputStream(outputFile)
                var downloadedSize: Long = 0
                while (true) {
                    if (isCancelled) {
                        input!!.close()
                        return java.lang.Long.valueOf(-1)
                    }
                    Read = inpStr.read(tmpByte)
                    downloadedSize += Read.toLong()
                    if (FileSize > 0) {
                        val per = downloadedSize.toLong() / FileSize * 100L
                        val str =
                            "Downloaded " + downloadedSize + "KB / " + FileSize + "KB (" + per.toLong() + "%)"
                        publishProgress("" + (downloadedSize * 100L / FileSize), str)
                    }
                    if (Read <= 0) {
                        break
                    }
                    fos.write(tmpByte, 0, Read)
                }
                inpStr.close()
                fos.close()
                httpCon.disconnect()
            } catch (e: Exception) {
                Log.e("Error: ", e.message.toString())
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (ignored: IOException) {
                }
                mWakeLock!!.release()
            }
            return FileSize
        }

        override fun onProgressUpdate(vararg progress: String?) {
            super.onProgressUpdate(*progress)

            // if we get here, length is known, now set indeterminate to false
            progressBar!!.isIndeterminate = false
            progressBar!!.max = 100
            progressBar!!.progress = progress[0]!!.toInt()
            progressBar!!.setMessage(progress[1])
        }

        //파일 다운로드 완료 후
        override fun onPostExecute(size: Long) { //5
            super.onPostExecute(size)
            progressBar!!.dismiss()
            if (size > 0) {
                Toast.makeText(
                    context!!.applicationContext,
                    "다운로드 완료되었습니다. 파일 크기=$size",
                    Toast.LENGTH_LONG
                ).show()

//                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                mediaScanIntent.setData(Uri.fromFile(outputFile));
//                context.sendBroadcast(mediaScanIntent);
//                applicationContext.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$destName")))
                showDownloadFile(outputFile!!.path)
            } else Toast.makeText(context!!.applicationContext, "다운로드 에러", Toast.LENGTH_LONG).show()
        }


    }

    companion object {
        val instance = FileHelper()
    }
}