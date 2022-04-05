package com.emaintec

import android.os.Environment

import java.io.File

object Define {
    val PLANT = "6000"
    val DB_NAME_1 = "mobins_off.db"
    //	val DB_NAME_1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "betools.db";
    //val DB_NAME_1 = Environment.getExternalStoragePublicDirectory("Others").absolutePath + File.separator + "betools.db"
//    val DB_NAME_1 = Environment.getExternalStoragePublicDirectory(File.separator + "mobins_multi.db").path
    val DB_PASSWORD_1 = "emaintec"
    var SOUND = false
    var USB_ONLY = true
    val IS_USB = "http://127.0.0.1:9090"
    val URL_DEFAULT = "http://nis.lgchem.com/mobins" //http://nis.lgchem.com/mobins
    var LANG = "ko"
    var OFFLINE = true
}
