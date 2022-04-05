package com.emaintec.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.emaintec.Activity_ViewPager
import com.emaintec.Data
import com.emaintec.Fragment_Base
import com.emaintec.ckdaily.model.PmDayMstModel
import com.emaintec.datasync.dataSync
import com.emaintec.external.zxing.IntentIntegrator
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.Activity_Main
import com.emaintec.mobins.Menu
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.MainFragmentBinding
import com.google.gson.Gson


class main_Fragment : com.emaintec.Fragment_Base() {
    lateinit var binding: MainFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1000){
            updateUI()
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButton()
        updateUI()
    }

    private fun initButton() {
        binding.btnCkMst.setOneClickListener {
            val intent = Intent(activity, Activity_ViewPager::class.java)
            intent.putExtra("menu", "ckmst.CkMstHdr")
            intent.putExtra("title", "점검설비관리")
            startActivityForResult(intent, 1000)
        }
        binding.btnCkDaily.setOneClickListener {
            val intent = Intent(activity, Activity_ViewPager::class.java)
            intent.putExtra("menu", "ckdaily.CkDayHdr")
            intent.putExtra("title", "일일점검관리")
            startActivityForResult(intent, 1000)
        }
        binding.btnDataSync.setOneClickListener {
            val intent = Intent(activity, Activity_ViewPager::class.java)
            intent.putExtra("menu", "datasync.dataSync")
            intent.putExtra("title", "자료주고받기")
            startActivityForResult(intent, 1000)
        }
    }

    override fun updateUI() {
        if (view == null) return
        Log.d("main_fragement", "updateui---")
        binding.textViewLoginMsg.text =
            "${Data.instance._workCenterNm} 작업장으로 로그인 했습니다."
        binding.textDnDate.text = "작업현황 다운로드 일자: ${Data.instance._downDate}"
        val jArr = SQLiteQueryUtil.selectJsonArray(
            """
                SELECT (SELECT count(*) from TB_PM_DAYMST ) MASTER_CNT
                       ,COUNT(*)	   AS DAY_CNT
                       ,SUM(CASE WHEN PM_CHECK = 'Y' THEN 1 ELSE 0 END) AS CHECKED
                       ,SUM(CASE WHEN PM_STRANGE = 'Y' THEN 1 ELSE 0 END) AS STRANGE
                FROM TB_PM_DAYMST
 
        """.trimIndent()
        )
        val list = Gson().fromJson(jArr.toString(), Array<StatusModel>::class.java)
        if(list.isNotEmpty()){
            binding.textMasterCnt.text = list[0].MASTER_CNT
            binding.textDayCnt.text = "${list[0].CHECKED}/${list[0].DAY_CNT}"
            binding.textStrangeCnt.text = list[0].STRANGE
        }
    }

    override fun onScanMsg(strQrCode: String) {

    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        //나의메뉴
    }
    class StatusModel {
        var MASTER_CNT: String = ""
        var DAY_CNT: String = ""
        var CHECKED: String = ""
        var STRANGE: String = ""
    }
}

