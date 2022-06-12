package com.emaintec.datasync.helper

import androidx.annotation.WorkerThread
import com.emaintec.Data
import com.emaintec.Define
import com.emaintec.ckdaily.model.PmDayMstModel
import com.emaintec.ckdaily.model.PmHistoryModel
import com.emaintec.datasync.model.UpDayCpModel
import com.emaintec.lib.db.DBSwitcher
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.network.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.ArrayList


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
                    action = "ct_biz.dn_wkcenter_code.IGetData"
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
        ) {
            NetworkSync(
                TransmitterJson(
                    url = Data.instance.url + "/ct_broker.jsp",
                    action = "ct_biz.dn_pm_master_sql.IGetData",
                    jsondata = jsondata,
                    timeout =  600
                ), ReceiverJson()
            ).get(onSuccessed = {
                val nCount = SQLiteQueryUtil.insert_Table(it.receiver.resultData!!,"TB_PM_MSTCP",true)
                SQLiteQueryUtil.replace_Table(it.receiver.resultData!!,"TB_PM_MASTER",true)
                action(true, "${nCount}건 다운")
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, jsonObject.get("message").toString())
            })
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
                    action = "ct_biz.dn_pm_daily.IGetData",
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
            action: suspend (Boolean, List<PmHistoryModel>?, String) -> Unit,
            jsondata: String? = null,
            result: String? = null
        ) {
            NetworkSync(
                TransmitterJson(
                    url = Data.instance.url + "/ct_broker.jsp",
                    action = "ct_biz.dn_ckday_hst.IGetData",
                    jsondata = jsondata
                ), ReceiverJson()
            ).get(onSuccessed = {
                val list = ArrayList<PmHistoryModel>()
                val jsonObject = it.receiver.resultData!!
                if (!jsonObject.getBoolean("success")) {
                    action(false, null, jsonObject.get("message").toString())
                    return@get
                }
                val jsonArray = jsonObject.getJSONArray("data")

                for (n in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(n)
                    list.add(PmHistoryModel(`object`))
                }
                action(true, list, jsonObject.get("message").toString())
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, null, jsonObject.get("message").toString())
            })
        }
        @WorkerThread
        suspend fun uploadCheckResult(action: suspend (Boolean, String) -> Unit): Boolean {
            var bResult = true;
            val result = SQLiteQueryUtil.selectJsonArray("""
                SELECT PM_NOTI_NO,CHK_NO,PM_GROUP,PM_PLN_DT,CHK_RESULT,CHK_OKNOK,CHK_MEMO,CHK_DATE,CHK_NOTI,PM_WKCNTR,PM_INST,CHK_TIME,PM_STANDBY
                  FROM TB_PM_DAYCP
                 WHERE PM_CHECK = 'Y' 
            """.trimIndent())
            val list = Gson().fromJson(result.toString(), Array<UpDayCpModel>::class.java)
            if(list.isEmpty()){
                action(false, "데이타가 없습니다.")
                return false
            }
            NetworkSync(
                TransmitterJson(
                    url = Data.instance.url + "/ct_broker.jsp",
                    action = "ct_biz.up_result.IGetData",
                    jsondata = "[]",
                    result = result.toString(),
                    timeout = 300
                ), ReceiverJson()
            ).get(
                onSuccessed = {
                    if ((it.receiver.resultData as JSONObject).getBoolean("success")) {
                        SQLiteQueryUtil.executeSql("""
                           UPDATE  TB_PM_MASTER  SET  PM_LDATE = (SELECT  MAX(CHK_DATE) FROM  TB_PM_DAYCP WHERE PM_EQP_NO = TB_PM_MASTER.PM_EQP_NO AND PM_CHECK = 'Y' )
                            WHERE PM_EQP_NO IN (SELECT PM_EQP_NO FROM TB_PM_DAYMST WHERE PM_CHECK = 'Y' )
                        """.trimIndent())
                        SQLiteQueryUtil.executeSql("""
                            UPDATE  TB_PM_MSTCP  SET  CHK_LDATE1 = CHK_LDATE2, CHK_OKNOK1 = CHK_OKNOK2, CHK_LRSLT1 = CHK_LRSLT2,
                                CHK_LDATE2 = (SELECT CHK_DATE FROM TB_PM_DAYCP WHERE  CHK_NO = TB_PM_MSTCP.CHK_NO AND PM_CHECK = 'Y'),
                                CHK_OKNOK2 =  (SELECT CHK_OKNOK FROM TB_PM_DAYCP WHERE  CHK_NO = TB_PM_MSTCP.CHK_NO AND PM_CHECK = 'Y'),
                                CHK_LRSLT2 =  (SELECT CHK_RESULT FROM TB_PM_DAYCP WHERE  CHK_NO = TB_PM_MSTCP.CHK_NO AND PM_CHECK = 'Y')
                            WHERE CHK_NO IN (SELECT CHK_NO FROM TB_PM_DAYCP WHERE PM_CHECK = 'Y' )
                        """.trimIndent())
                        DBSwitcher.instance.sendMessage(SQLiteQueryUtil.DB_NAME, "TB_PM_DAYMST")
                        DBSwitcher.instance.sendMessage(SQLiteQueryUtil.DB_NAME, "TB_PM_DAYCP")
                        action(true, "업로드 성공")
                    } else {
                        action(false, (it.receiver.resultData as JSONObject).getString("message"))
                    }
                }, onFailed = {
                    val jsonObject = JSONObject(it.receiver.errorData!!)
                    bResult = false;
                }
            )
            return bResult;
        }

    }

}
