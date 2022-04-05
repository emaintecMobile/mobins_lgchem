package com.emaintec

import android.content.DialogInterface
import android.os.Handler
import android.text.format.DateUtils
import android.util.Log
import com.emaintec.common.helper.commonHelper
import com.emaintec.db.QueryHelper_Setup
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.device.Device
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class Data private constructor() {
    var url = Define.URL_DEFAULT
    var _plant = Define.PLANT
    var _workCenter = ""
    var _workCenterNm = ""
    var m_strChkDmyIden = "특기사항"
    var _downDate = ""
    private var timer: Timer? = null
    private var handler: Handler? = null
    val LOGOUT_TIME_MIN = 30
    /*** 30분 후 자동 로그아웃 ***/
    var  refreshHandler:Handler? = null
    var  runnable:Runnable? = null

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private object SingletonHolder {
        val INSTANCE = Data()
    }

    companion object {
        val instance: Data
            get() = SingletonHolder.INSTANCE
    }
}
