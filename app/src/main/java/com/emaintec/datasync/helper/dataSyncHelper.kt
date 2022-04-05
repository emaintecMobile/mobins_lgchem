package com.emaintec.datasync.helper

import androidx.annotation.WorkerThread
import com.emaintec.Data
import com.emaintec.Define
import com.emaintec.lib.db.DBSwitcher
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.network.*
import org.json.JSONObject


object dataSyncHelper {
    object BasicSync {


        @WorkerThread
        suspend fun getDnPlant(
            action: suspend (Boolean, String) -> Unit,
            jsondata: String? = null,
            result: String? = null
        ): Boolean {
            var bResult = true;
            NetworkSync(
                TransmitterJson(
                    url = Data.instance.url + "/ct_broker.jsp",
                    action = "ct_biz.dn_wkcenter_code.IGetDataEx"
                ), ReceiverJson()
            ).get(onSuccessed = {
                val nCount =
                    SQLiteQueryUtil.insert_Table(it.receiver.resultData!!, "TB_PM_WKCENTER", true)
                action(
                    true,
                    "플랜트 정보 ${nCount}/${it.receiver.resultData!!.getJSONArray("data").length()} 건 성공"
                )
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, jsonObject.get("message").toString())
                bResult = false;
            })
            return bResult;
        }
    }


    object Check {
        @WorkerThread
        suspend fun GetCkMst(
            action: suspend (Boolean, String) -> Unit,
            jsondata: String? = null,
            result: String? = null
        ): Boolean {
            var bResult = true;
            NetworkSync(
                TransmitterJson(
                    url = Data.instance.url + "/ct_broker.jsp",
                    action = "ct_biz.dn_pm_master.IGetDataEx",
                    jsondata = jsondata,
                    timeout =  300
                ), ReceiverJson()
            ).get(onSuccessed = {
                val nCount = SQLiteQueryUtil.insert_Table(it.receiver.resultData!!,"TB_PM_MSTCP",true)
                SQLiteQueryUtil.replace_Table(it.receiver.resultData!!,"TB_PM_MASTER",true)
                action(true, "${nCount}건 다운")
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, jsonObject.get("message").toString())
                bResult = false;
            })
            return bResult;
        }

        @WorkerThread
        suspend fun GetCkDaily(
            action: suspend (Boolean, String) -> Unit,
            jsondata: String? = null,
            result: String? = null
        ) {
            NetworkSync(
                TransmitterJson(
                    url = Data.instance.url + "/ct_broker.jsp",
                    action = "ct_biz.dn_pm_daily.IGetDataEx",
                    jsondata = jsondata
                ), ReceiverJson()
            ).get(onSuccessed = {

                val nCount = SQLiteQueryUtil.insert_Table(it.receiver.resultData!!,"TB_PM_DAYCP",true)
                SQLiteQueryUtil.replace_Table(it.receiver.resultData!!,"TB_PM_DAYMST",true)
                action(
                    true,
                    "점검아이템  ${nCount}/${it.receiver.resultData!!.getJSONArray("data").length()} 건 성공"
                )
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, jsonObject.get("message").toString())
            })
        }

        @WorkerThread
        suspend fun GetCkSchHstList(
            action: suspend (Boolean, String) -> Unit,
            jsondata: String? = null,
            result: String? = null
        ) {
            NetworkSync(
                TransmitterJson(
                    url = Data.instance.url + "/Mobile",
                    action = "ct_off.GetCkSchHstList.IGetData",
                    jsondata = jsondata

                ), ReceiverJson()
            ).get(onSuccessed = {
                val nCount = SQLiteQueryUtil.insert_Table(
                    it.receiver.resultData!!,
                    "TM_CHECK_HISTORY",
                    true
                )
                action(
                    true,
                    "점검이력  ${nCount}/${it.receiver.resultData!!.getJSONArray("data").length()} 건 성공"
                )
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, jsonObject.get("message").toString())
            })
        }

        fun updateTM_CHECK_HDR(CHECK_SCHEDULE_NO: String, IS_UPLOAD: String): Int {
            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(Define.DB_NAME_1)!!

            val sqLiteStatement =
                sqLiteDatabase.compileStatement("UPDATE TM_CHECK_HDR SET IS_UPLOAD=? WHERE (CHECK_SCHEDULE_NO = ?)")
            sqLiteStatement!!.bindObject(1, IS_UPLOAD)
            sqLiteStatement!!.bindObject(2, CHECK_SCHEDULE_NO)

            sqLiteStatement!!.executeUpdateDelete()
            val result = sqLiteDatabase.changes
            sqLiteStatement!!.close()
            sqLiteDatabase.close()

            return result
        }
    }

}
