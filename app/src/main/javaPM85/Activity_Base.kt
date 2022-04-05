package com.emaintec

//PM85
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.emaintec.external.zxing.IntentIntegrator
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.ViewPager
import com.emaintec.mobins.R
import device.common.DecodeResult
import device.common.DecodeStateCallback
import device.common.ScanConst
import device.sdk.ScanManager

open class Activity_Base : AppCompatActivity() {
    //PM85스캔
    companion object {
        private val TAG = "tScanner"
        private var mScanner: ScanManager? = null
        private var mDecodeResult: DecodeResult? = null
        private var mKeyLock = false

        private var mDialog: android.app.AlertDialog? = null
        private var mBackupResultType = ScanConst.ResultType.DCD_RESULT_COPYPASTE
        private var mContext: Context? = null

        //        private var mWaitDialog: ProgressDialog? = null
        private val mHandler = Handler()
        private var mScanResultReceiver: ScanResultReceiver? = null
    }

    class ScanResultReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (mScanner != null) {
                try {
                    if (ScanConst.INTENT_USERMSG == intent.action) {
                        mScanner!!.aDecodeGetResult(mDecodeResult!!.recycle())
                        Log.d("barcode", mDecodeResult!!.toString())
                        if (mDecodeResult!!.toString().equals("READ_FAIL")) return
//                        Toast.makeText(Emaintec.activity,mDecodeResult.toString(),Toast.LENGTH_LONG).show();
                        //viewPager.setCurrentItem(2);
                        if (Emaintec.fragment == null) {
                            (Emaintec.activity as Activity_Base).onScanMsg(mDecodeResult!!.toString())
                        } else {
                            (Emaintec.fragment as Fragment_Base).onScanMsg(mDecodeResult!!.toString())
                        }
                    } else if (ScanConst.INTENT_EVENT == intent.action) {
                        val result = intent.getBooleanExtra(ScanConst.EXTRA_EVENT_DECODE_RESULT, false)
                        val decodeBytesLength = intent.getIntExtra(ScanConst.EXTRA_EVENT_DECODE_LENGTH, 0)
                        val decodeBytesValue = intent.getByteArrayExtra(ScanConst.EXTRA_EVENT_DECODE_VALUE)
                        val decodeValue = String(decodeBytesValue!!, 0, decodeBytesLength)
                        val decodeLength = decodeValue.length
                        val symbolName = intent.getStringExtra(ScanConst.EXTRA_EVENT_SYMBOL_NAME)
                        val symbolId = intent.getByteExtra(ScanConst.EXTRA_EVENT_SYMBOL_ID, 0.toByte())
                        val symbolType = intent.getIntExtra(ScanConst.EXTRA_EVENT_SYMBOL_TYPE, 0)
                        val letter = intent.getByteExtra(ScanConst.EXTRA_EVENT_DECODE_LETTER, 0.toByte())
                        val modifier = intent.getByteExtra(ScanConst.EXTRA_EVENT_DECODE_MODIFIER, 0.toByte())
                        val decodingTime = intent.getIntExtra(ScanConst.EXTRA_EVENT_DECODE_TIME, 0)
                        Log.d(TAG, "1. result: $result")
                        Log.d(TAG, "2. bytes length: $decodeBytesLength")
                        Log.d(TAG, "3. bytes value: $decodeBytesValue")
                        Log.d(TAG, "4. decoding length: $decodeLength")
                        Log.d(TAG, "5. decoding value: $decodeValue")
                        Log.d(TAG, "6. symbol name: $symbolName")
                        Log.d(TAG, "7. symbol id: $symbolId")
                        Log.d(TAG, "8. symbol type: $symbolType")
                        Log.d(TAG, "9. decoding letter: $letter")
                        Log.d(TAG, "10.decoding modifier: $modifier")
                        Log.d(TAG, "11.decoding time: $decodingTime")
//                        mBarType.setText(symbolName)
//                        mResult.setText(decodeValue)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
    //PM85
    private val mStateCallback = if (Build.MODEL == "PM85") {
        object : DecodeStateCallback(mHandler) {
            override fun onChangedState(state: Int) {
                when (state) {
                    ScanConst.STATE_ON, ScanConst.STATE_TURNING_ON -> if (getEnableDialog()!!.isShowing()) {
                        getEnableDialog()!!.dismiss()
                    }
                    ScanConst.STATE_OFF, ScanConst.STATE_TURNING_OFF -> if (!getEnableDialog()!!.isShowing()) {
                        getEnableDialog()!!.show()
                    }
                }
            }
        }
    } else null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Emaintec.activity = this
//PM85
        mContext = this
        if (Build.MODEL == "PM85") {
            mScanner = ScanManager()
            mDecodeResult = DecodeResult()
            mScanResultReceiver = ScanResultReceiver()
            if (mScanner != null) {
                mScanner!!.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_ONESHOT)
                mScanner!!.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG)
                mScanner!!.aDecodeSetBeepEnable(1)
                mScanner!!.aDecodeSymSetEnable(ScanConst.SymbologyID.DCD_SYM_UPCA, 0)
            }
        }
    }

    open fun onScanMsg(strQrCode: String) {

    }

    open fun movePage(position: Int) {

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            val re = scanResult!!.contents
            val message = re
            val viewPager = findViewById<View>(R.id.pager) as ViewPager
            (Emaintec.fragment as Fragment_Base).onScanMsg(re)
            //            ((Fragment_Base)((Adapter_TabPager) viewPager.getAdapter()).getItem(viewPager.getCurrentItem())).onScanMsg(re);
        }
    }

    //PM85
    private fun initScanner() {
        if (Build.MODEL == "PM85") {
            if (mScanner != null) {
                if (mScanner!!.aDecodeGetDecodeEnable() == 1) {
                    if (getEnableDialog()!!.isShowing) {
                        getEnableDialog()!!.dismiss()
                    }
                } else {
                    if (!getEnableDialog()!!.isShowing) {
                        getEnableDialog()!!.show()
                    }
                }
                mBackupResultType = mScanner!!.aDecodeGetResultType()
                mScanner!!.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG)

            }
        }

    }

    private val mStartOnResume = Runnable {
        if (Build.MODEL == "PM85") {
            runOnUiThread {
                initScanner()
            }
        }
    }

    //PM85
    private fun getEnableDialog(): android.app.AlertDialog? {
        if (mDialog == null) {
            val dialog = android.app.AlertDialog.Builder(this).create()
            dialog.setTitle(R.string.app_name)
            dialog.setMessage("Your scanner is disabled. Do you want to enable it?")

            dialog.setButton(
                android.app.AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel)
            ) { dialog, which -> finish() }
            dialog.setButton(
                android.app.AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok)
            ) { dialog, which ->
                val intent = Intent(ScanConst.LAUNCH_SCAN_SETTING_ACITON)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            mDialog = dialog
        }
        return mDialog
    }

    //PM85
    override fun onResume() {
        super.onResume()
        if (Build.MODEL == "PM85") {

            mHandler.postDelayed(mStartOnResume, 1000)
            val filter = IntentFilter()
            filter.addAction(ScanConst.INTENT_USERMSG)
            filter.addAction(ScanConst.INTENT_EVENT)
            mContext!!.registerReceiver(mScanResultReceiver, filter)
        }
    }

    //PM85
    override fun onPause() {
        if (Build.MODEL == "PM85") {
            if (mScanner != null) {
                mScanner!!.aDecodeSetResultType(mBackupResultType)
            }
            mContext!!.unregisterReceiver(mScanResultReceiver)

        }
        super.onPause()
    }

    //PM85
    override fun onDestroy() {
        super.onDestroy()
    }

    //PM85
    fun closeBarcode() {
        if (Build.MODEL == "PM85") {
            if (mScanner != null) {
                mScanner!!.aDecodeSetResultType(mBackupResultType)
            }
            mScanner = null
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}




