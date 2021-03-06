package com.emaintec.mobins


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.emaintec.Activity_Base
import com.emaintec.Data
import com.emaintec.Define
import com.emaintec.Functions
import com.emaintec.common.model.WorkCenterModel
import com.emaintec.datasync.helper.dataSyncHelper
import com.emaintec.db.QueryHelper_Setup
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.device.Device
import com.emaintec.lib.network.NetworkProgress
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.databinding.ActivityLoginBinding
import com.emaintec.mobins.databinding.ActivityMainMenuBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Activity_Login : Activity_Base() {
    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    lateinit var bindingMenu: ActivityMainMenuBinding
    private var strPath = ""
    private var strSavePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Emaintec.application = this.application
        Emaintec.activity = this
        Emaintec.fragment = null
        initButton()
        initSpinner()
        initVersion()


        //        DBSwitcher.instance.sendMessage(Define.DB_NAME_1, "T1PT_ISSUE")
        //        DBSwitcher.instance.sendMessage(Define.DB_NAME_1, "T1PART_STOCK_SURVEY")
        //        DBSwitcher.instance.sendMessage(Define.DB_NAME_1, "T1PART_SURVEY_RESULT")

    }

    private fun initSpinner() {
        NetworkProgress.start(this)
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
            select * from TB_PM_WKCENTER
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<WorkCenterModel>::class.java)
        val adapter = ArrayAdapter(this, R.layout.custom_spinner_item_lg, list)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerWorkCenter.adapter = adapter
        NetworkProgress.end()

        var select = list.map {it.WKCNTR_NO}.indexOf(Data.instance._workCenter)
        if(select<0) select = 0
        binding.spinnerWorkCenter.setSelection(select)

    }


    private fun initVersion() {

        //////////////////////////////////////////////////////////////////////////////////
        // ?????? ??????
        //////////////////////////////////////////////////////////////////////////////////
        binding.textViewVersion.text = "Mobile Version:" + Device.versionName
    }

    private fun initButton() {
        binding.buttonLogin.setOneClickListener {
            LoginOffline()
        }

        binding.imageButtonData.setOneClickListener {
            val wrapper: Context = ContextThemeWrapper(this@Activity_Login, R.style.PopupMenuTheme)
            val popup = PopupMenu(wrapper, it)  //v??? ????????? ?????? ??????
            var setting = SpannableString("????????????" + "   ").also {
                it.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, it.length, 0)
            }
            var exit = SpannableString("??????" + "   ").also {
                it.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, it.length, 0)

            }
            popup.menu.add(0, 0, 0, setting)
            popup.menu.add(0, 1, 1, exit)
            popup.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    0 -> {
                        startActivity(Intent().setClassName(this@Activity_Login.baseContext, "com.emaintec.mobins" + "." + "Activity_Setup"))
                        overridePendingTransition(0, 0)
//                                finish()
                    }
                    1 -> {
                        this@Activity_Login.finish()
                        closeBarcode()
                        Handler().postDelayed( // 2 ??? ?????? ??????
                            {
                                android.os.Process.killProcess(android.os.Process.myPid())
                                //mHandler.sendEmptyMessage(0);
                            }, 1000
                        )
                    }

                } //
                false
            }
            popup.show() //Popup Menu ?????????
        }

        binding.buttonBasicDown.setOneClickListener {

            download_Basic()

        }

    }

    private fun download_Basic() {
        CoroutineScope(Dispatchers.Default).launch {
            launch(Dispatchers.Main) {
                Functions.UpdateApp.Autoupdate()
            }.join()
        }
        val jsonobject = JsonObject()
        var lang = "${Define.LANG}"
        jsonobject.addProperty("PLANT", Data.instance._plant)
        val jsonData = Gson().toJson(arrayOf(jsonobject))
        NetworkProgress.start(this)
        CoroutineScope(Dispatchers.Default).launch {
            val bResult = dataSyncHelper.BasicSync.getDnPlant({ success: Boolean, msg: String ->
                launch(Dispatchers.Main) {
                    Functions.toast(applicationContext, msg)
                    initSpinner()
                }.join()
            })
            if (!bResult) {
                NetworkProgress.end()
                return@launch;
            }
            NetworkProgress.end()
        }
    }

    private fun LoginOffline() {
        //startActivity(new Intent(Activity_Login.this.getBaseContext(), Activity_Main.class));
//        DBSwitcher.instance.sendMessage(Define.DB_NAME_1, "TM_NOTICE")

        run {
            if(binding.spinnerWorkCenter.selectedItem==null){
                Functions.MessageBox(this,"??????????????? ???????????? ?????????.")
                return
            }
            var strWkCenter = (binding.spinnerWorkCenter.selectedItem as WorkCenterModel).WKCNTR_NO
            if(strWkCenter.isNullOrBlank()){
                Functions.MessageBox(this,"???????????? ????????? ?????????.")
                return
            }
            Data.instance._workCenterNm =(binding.spinnerWorkCenter.selectedItem as WorkCenterModel).WKCNTR_NM
            Data.instance._workCenter = strWkCenter
            QueryHelper_Setup.instance.mapSetting["WORKCENTER"] = Data.instance._workCenter
            QueryHelper_Setup.instance.querySettingSave()
            startActivity(Intent(this@Activity_Login.baseContext, Activity_Main::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    override fun onScanMsg(strQrCode: String) {
        if (Functions.ContainsCount(strQrCode, " ") == 1) {
            binding.buttonLogin.performClick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Emaintec.activity = this
    }

    private var backKeyPressedTime: Long = 0
    override fun onBackPressed() {
/*
        val alertDialogBuilder = AlertDialog.Builder(this@Activity_Login)
        // ????????????
        alertDialogBuilder.setTitle(this@Activity_Login.applicationInfo.loadLabel(this@Activity_Login.packageManager).toString())

        alertDialogBuilder.setMessage("??????????????? ?????????????????????????").setCancelable(true).setPositiveButton("??????") { dialog, which ->
            closeBarcode()
            this@Activity_Login.finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        }.setNegativeButton("??????", null)

        // ??????????????? ??????
        val alertDialog = alertDialogBuilder.create()

        // ??????????????? ????????????
        alertDialog.show()
 */
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'??????\' ????????? ?????? ??? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
            return
        }
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ?????? ??????????????? ?????? ???
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ????????? ???????????? ??????
        // ?????? ????????? Toast ??????
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            closeBarcode()
            this@Activity_Login.finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
}




