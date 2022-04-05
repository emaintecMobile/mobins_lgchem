package com.emaintec.external.zxing

import android.app.Activity
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator


object IntentIntegrator {

    const val REQUEST_CODE = IntentIntegrator.REQUEST_CODE

    fun createScanIntent(activity: Activity): Intent {
        val integrator = IntentIntegrator(activity)
        integrator.captureActivity = ActivityZxingScanner::class.java
        integrator.initiateScan()
        return integrator.createScanIntent()
    }

    fun parseActivityResult(requestCode: Int, resultCode: Int, intent: Intent?): IntentResult? {
        return if (requestCode == REQUEST_CODE) {
            IntentResult(
                IntentIntegrator.parseActivityResult(
                    resultCode,
                    intent
                )
            )
        } else null
    }

}