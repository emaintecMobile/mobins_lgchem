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
        // 버전 표시
        //////////////////////////////////////////////////////////////////////////////////
        binding.textViewVersion.text = "Mobile Version:" + Device.versionName
    }

    private fun initButton() {
        binding.buttonLogin.setOneClickListener {
            LoginOffline()
        }

        binding.imageButtonData.setOneClickListener {
            val wrapper: Context = ContextThemeWrapper(this@Activity_Login, R.style.PopupMenuTheme)
            val popup = PopupMenu(wrapper, it)  //v는 클릭된 뷰를 의미
            var setting = SpannableString("환경설정" + "   ").also {
                it.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, it.length, 0)
            }
            var exit = SpannableString("종료" + "   ").also {
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
                        Handler().postDelayed( // 2 초 후에 실행
                            {
                                android.os.Process.killProcess(android.os.Process.myPid())
                                //mHandler.sendEmptyMessage(0);
                            }, 1000
                        )
                    }

                } //
                false
            }
            popup.show() //Popup Menu 보이기
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
                Functions.MessageBox(this,"기본코드를 다운로드 하세요.")
                return
            }
            var strWkCenter = (binding.spinnerWorkCenter.selectedItem as WorkCenterModel).WKCNTR_NO
            if(strWkCenter.isNullOrBlank()){
                Functions.MessageBox(this,"작업장을 선택해 주세요.")
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
        // 제목셋팅
        alertDialogBuilder.setTitle(this@Activity_Login.applicationInfo.loadLabel(this@Activity_Login.packageManager).toString())

        alertDialogBuilder.setMessage("프로그램을 종료하시겠습니까?").setCancelable(true).setPositiveButton("종료") { dialog, which ->
            closeBarcode()
            this@Activity_Login.finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        }.setNegativeButton("취소", null)

        // 다이얼로그 생성
        val alertDialog = alertDialogBuilder.create()

        // 다이얼로그 보여주기
        alertDialog.show()
 */
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            closeBarcode()
            this@Activity_Login.finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
}




