

package com.emaintec

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.emaintec.external.zxing.IntentIntegrator
import com.emaintec.lib.adapter.Adapter_Pager
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.ViewPager
import com.emaintec.mobins.R


open class Activity_Base : AppCompatActivity() {
    companion object {
        private var mContext: Context? = null
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Emaintec.activity = this
        mContext = this

    }
    open fun onScanMsg(strQrCode: String) {

    }
    open fun movePage(position: Int) {

    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            val re = scanResult?.contents
            val viewPager = findViewById<View>(R.id.pager) as ViewPager
            Log.d("scan",((viewPager.getAdapter() as Adapter_Pager).getItem(viewPager.getCurrentItem()) as Fragment_Base).toString())
            re?.let { (Emaintec.fragment as Fragment_Base).onScanMsg(it) }
            //            ((Fragment_Base)((Adapter_TabPager) viewPager.getAdapter()).getItem(viewPager.getCurrentItem())).onScanMsg(re);
//            re?.let {((viewPager.getAdapter() as Adapter_Pager).getItem(viewPager.getCurrentItem()) as Fragment_Base).onScanMsg(re)}
        }
    }
    override fun onUserInteraction() {
        super.onUserInteraction()
    }
    fun closeBarcode(){

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}

