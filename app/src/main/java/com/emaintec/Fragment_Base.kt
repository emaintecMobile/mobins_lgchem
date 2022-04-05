package com.emaintec

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.emaintec.lib.base.Emaintec

open class Fragment_Base : DialogFragment() {

    open fun updateUI() {

    }

    open fun onScanMsg(strQrCode: String) {

    }

    fun onTabLayoutSelected(position: Int) {
        (activity as Activity_Base).movePage(position)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Emaintec.fragment = this
    }



    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(isVisibleToUser) {
            Emaintec.fragment = this
        }
    }
}
