package com.emaintec.mobins

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emaintec.Data
import com.emaintec.Define
import com.emaintec.Functions
import com.emaintec.db.DBInterface_1
import com.emaintec.db.QueryHelper_Setup
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.db.DBSwitcher
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.permission.Permission
import java.io.File


class Activity_Splash : AppCompatActivity() {
    private var _isPermissionGranted = false

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Emaintec.application = this.application
        Emaintec.activity = this
        
        Permission.create(this)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .setDeniedMessage("If you reject permission, you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButton(true)
                .setGotoSettingButtonText("Settings")
                .request()
                .subscribe({ permissionResult ->
                    if (permissionResult.isGranted) {
                        val dir = File(
                            Functions.FilePath.getPath()
                                        .absolutePath + "/Emaintec")
                        // 폴더가 존재하지 않을 경우 폴더를 만듦
                        if (!dir.exists()) {
                            dir.mkdir()
                        }
                        _isPermissionGranted = true
                        SQLiteQueryUtil.DB_NAME = Define.DB_NAME_1
                        DBSwitcher.instance.openDatabase(Define.DB_NAME_1, 1, null, DBInterface_1())
//                        DBSwitcher.instance.sendMessage(Define.DB_NAME_1, "TS_RESOURCE")
                        val _mapSetting = QueryHelper_Setup.instance.mapSetting
                        Data.instance.url = _mapSetting["URL"] ?: Define.URL_DEFAULT
                        Define.SOUND = "Y" == _mapSetting["SOUND"]
                        Data.instance._workCenter = _mapSetting["WORKCENTER"].toString()
                        Data.instance._plant = _mapSetting["PLANT"]?: Define.PLANT
                        Data.instance._downDate = _mapSetting["DOWN_DATE"].toString()
                        Functions


//                        QueryHelper_Menu.instance.InitMenu()
                        startActivity(Intent(this@Activity_Splash.baseContext, Activity_Login::class.java))
                        finish()



                    } else {
                        //						Toast.makeText(Activity_Splash.this, "Permission Denied\n" + permissionResult.getDeniedPermissions().toString(), Toast.LENGTH_SHORT).show();
                    }
                }, { throwable ->
                    //					Toast.makeText(Activity_Splash.this, "Permission Exception\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                })
    }

    override fun onBackPressed() {
        this@Activity_Splash.finish()
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}
