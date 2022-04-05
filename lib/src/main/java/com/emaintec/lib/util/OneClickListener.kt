package com.emaintec.lib.util

import android.os.SystemClock
import android.view.View


class OneClickListener(private var interval: Int=1000, private var onOneClick: (View) -> Unit) : View.OnClickListener {
    private var lastClick : Long = 0

    override fun onClick(p0: View?) {
        if(SystemClock.elapsedRealtime() - lastClick < interval){
            return
        }
        lastClick = SystemClock.elapsedRealtime()
        onOneClick(p0!!)
    }
}

//extension method
fun View.setOneClickListener(onClick: (View) -> Unit){
    val oneClick = OneClickListener{
        onClick(it)
    }
    setOnClickListener(oneClick)
}

