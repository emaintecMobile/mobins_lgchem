package com.emaintec.common.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.emaintec.lib.device.Device
import com.emaintec.lib.device.Device.getActiveNetwork
import java.lang.NullPointerException
//n버전이상에서는 작동이 안됨...
class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            when (getActiveNetwork(true)){
                Device.ActiveNetwork.NONE -> {
                Log.e("keshav", "Conectivity Failure !!! ")
                }
                Device.ActiveNetwork.WIFI -> {
                    Log.e("keshav", "Conectivity WIFI !!! ")
                }
                Device.ActiveNetwork.MOBILE -> {
                    Log.e("keshav", "Conectivity MOBILE !!! ")
                }
                else -> {
                    Log.e("keshav", "else ")
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
}