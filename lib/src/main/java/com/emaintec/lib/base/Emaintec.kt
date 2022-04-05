package com.emaintec.lib.base


import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.os.SystemClock
import android.view.View
import androidx.fragment.app.Fragment
import com.emaintec.lib.util.OneClickListener
import java.util.*


object Emaintec {

    var application: Application? = null
        set(value) {
            value?.let {
//                if (!Device.model.equals("PM85")) {
//                    Timer(true).schedule(object : TimerTask() {
//                        override fun run() {
//                            activity?.runOnUiThread {
//                                //val message = it.packageName ?: getRandomString(20)
//                                val message = getRandomString(20)
//                                Toast.makeText(
//                                    it.applicationContext,
//                                    message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }, 0, 5000)
//                }
            }

            field = value
        }
    var activity: Activity? = null
    var activityOrientation : Int =-1
    var fragment: Fragment? = null
    var dialog: Dialog? = null
    var jobject: Any? = null

    private fun getRandomString(length: Int): String {
        val buffer = StringBuffer()
        val random = Random()
        val chars =
            "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,0,1,2,3,4,5,6,7,8,9".split(
                ","
            )

        for (idx in 0..length) {
            buffer.append(chars[random.nextInt(chars.size)]);
        }
        return buffer.toString();
    }

}

