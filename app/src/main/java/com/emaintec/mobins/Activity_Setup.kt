package com.emaintec.mobins

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emaintec.Data
import com.emaintec.Define
import com.emaintec.Functions
import com.emaintec.db.QueryHelper_Setup
import com.emaintec.lib.db.DBSwitcher
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.databinding.ActivitySetupBinding

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by YUN on 2018-03-20.
 */

class Activity_Setup : AppCompatActivity() {
    val binding by lazy { ActivitySetupBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initButton()
        //editText_URL.setText(Data.getInstance().getUrl());
        if (Data.instance.url == "") {
            binding.editTextURL.setText(Define.URL_DEFAULT)
        } else {
            binding.editTextURL.setText(Data.instance.url)
        }
        binding.editTextPlant.setText(Data.instance._plant)
//        editText_URL.setLines(5)
        binding.editTextLang.setText(Define.LANG)
        binding.checkboxSound.isChecked = Define.SOUND
        binding.checkboxOffline.isChecked = Define.OFFLINE
        binding.checkBoxUsb.isChecked = Define.USB_ONLY
        if ((BuildConfig.DEBUG)) {
            binding.textInfo.visibility = View.VISIBLE
        } else {
            binding.textInfo.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
//        val intent = Intent(this@Activity_Setup, Activity_Login::class.java)
//        startActivity(intent)
//        overridePendingTransition(0, 0)
//        finish()
    }

    private fun initButton() {
        run {
            binding.imageButtonClose.setOneClickListener {
//            val intent = Intent(this@Activity_Setup, Activity_Login::class.java)
//            startActivity(intent)
//            overridePendingTransition(0, 0)
                finish()
            }
        }

        run {
            binding.btnSetupSave.setOneClickListener {

                val strUrl = binding.editTextURL.text.toString()
                QueryHelper_Setup.instance.mapSetting["URL"] = strUrl
                QueryHelper_Setup.instance.mapSetting["PLANT"] = binding.editTextPlant.text.toString()
                QueryHelper_Setup.instance.mapSetting["LANG"] = binding.editTextLang.text.toString()
                QueryHelper_Setup.instance.mapSetting["IS_USB"] = if (binding.checkBoxUsb.isChecked) "Y" else "N"
                QueryHelper_Setup.instance.mapSetting["SOUND"] = if (binding.checkboxSound.isChecked) "Y" else "N"
                QueryHelper_Setup.instance.mapSetting["OFFLINE"] = if (binding.checkboxOffline.isChecked) "Y" else "N"
                QueryHelper_Setup.instance.querySettingSave()

                Data.instance.url = strUrl
                Data.instance._plant = binding.editTextPlant.text.toString()
                Define.USB_ONLY = binding.checkBoxUsb.isChecked
                Define.SOUND = binding.checkboxSound.isChecked
                Define.LANG = binding.editTextLang.text.toString()
                Define.OFFLINE = binding.checkboxOffline.isChecked
                //Toast.makeText(applicationContext, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                Functions.MessageBoxOk(this@Activity_Setup, "환경설정 수정후 프로그램을 다시 시작해야 합니다.", null, null, DialogInterface.OnClickListener { dialog, which ->
                    Functions.restartApp(this@Activity_Setup)
//                        finish()
//                        Handler().postDelayed( // 2 초 후에 실행
//                        {
//                                android.os.Process.killProcess(android.os.Process.myPid())
//                                //mHandler.sendEmptyMessage(0);
//                        }, 1000)
                })

            }
        }

        run {
            binding.btnBackupDb.setOneClickListener {
                DBSwitcher.instance.getReadableDatabase(Define.DB_NAME_1)!!.close()
//                    val file = File(Define.DB_NAME_1)
                val file = File(getDatabasePath(Define.DB_NAME_1).path)

                var destName = Functions.FilePath.getPath("emaintec.db").path        // "/mnt/sdcard/" + fileName;
                val strTodate = Functions.DateUtil.getDate("yyyyMMddhhmmss")
                val ext = Environment.getExternalStorageState()
                if (ext == Environment.MEDIA_MOUNTED) {
                    destName = Functions.FilePath.getPath()
                        .absolutePath + "/Emaintec/emt" + strTodate + ".bak"
                }

                val dir = File(
                    Functions.FilePath.getPath()
                        .absolutePath + "/Emaintec"
                )
                // 폴더가 존재하지 않을 경우 폴더를 만듦
                if (!dir.exists()) {
                    dir.mkdir()
                }

                val result = false

                if (null != file && file.exists()) {
                    try {
                        val fis = FileInputStream(file)
                        val fos = FileOutputStream(destName)
                        var readcount = 0
                        val buffer = ByteArray(1024)
                        do {
                            readcount = fis.read(buffer, 0, buffer.size)
                            if (readcount < 0) {
                                break;
                            }
                            fos.write(buffer, 0, readcount)
                        } while (true)
                        fos.close()
                        fis.close()
                        applicationContext.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$destName")))
                        Toast.makeText(applicationContext, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
        run {
            binding.btnInitDb.setOneClickListener {
                DBSwitcher.instance.sendMessage(Define.DB_NAME_1, "")
            }
        }

    }
}
