package com.emaintec.common.helper

import android.util.Log
import androidx.annotation.WorkerThread
import com.emaintec.Data
import com.emaintec.Define
import com.emaintec.common.model.codeModel
import com.emaintec.common.model.commonTreeModel
import com.emaintec.lib.db.DBSwitcher
import com.emaintec.lib.db.JsonHelper
import com.emaintec.lib.network.NetworkSync
import com.emaintec.lib.network.ReceiverJson
import com.emaintec.lib.network.TransmitterJson
import com.google.gson.JsonParser
import org.json.JSONObject
import java.util.ArrayList

class commonHelper {

    fun getLocList(LOC: String, Table: String = "TM_EQUIPMENT"): ArrayList<commonTreeModel> {
        val list = ArrayList<commonTreeModel>()
        val sqLiteDatabase = DBSwitcher.instance.getReadableDatabase(Define.DB_NAME_1)!!
        var query = ""
        when(Table){
            "TM_EQUIPMENT" ->{
                query ="""
                        SELECT distinct case trim(substr(LOC,0,instr(LOC,'>'))) when  '' then LOC
                            else trim(substr(LOC,0,instr(LOC,'>'))) end AS DESCRIPTION,'' KEY
                              FROM
                                    (
                                    SELECT distinct trim(substr(LOC,length('${LOC}'))) AS LOC
                                      FROM ${Table}
                                     WHERE LOC LIKE '${LOC}%'
                                    ) ;
                    """.trimIndent()
            }
            "T1DEPT" ->{

                query ="""
                        SELECT DEPT_NO KEY,DEPT_NM DESCRIPTION FROM  ${Table}
                        WHERE P_DEPT_NO = '${if(LOC.isNullOrBlank())"!" else LOC}'
                    """.trimIndent()
            }
        }
        val cursor = sqLiteDatabase.rawQuery(query, null)
        if(cursor != null) {
            cursor!!.moveToFirst()
            while(!cursor!!.isAfterLast()) {

                list.add(commonTreeModel(cursor))
                cursor!!.moveToNext()
            }
            cursor!!.close()
        }
        sqLiteDatabase.close()
        return list
    }

    @WorkerThread
    suspend fun getCommonTreeList(action: suspend (Boolean, List<commonTreeModel>?, String) -> Unit, jsondata: String? = null, result: String? = null) {
        NetworkSync(TransmitterJson(url = Data.instance.url + "/Mobile", action = "ct_biz.GetCommonTreeList.IGetData", jsondata = jsondata), ReceiverJson()).get(onSuccessed = {
            val item: commonTreeModel? = null
            val list = ArrayList<commonTreeModel>()
            val jsonObject = it.receiver.resultData!!
            if(!jsonObject.getBoolean("success")) {
                action(false, null, jsonObject.get("message").toString())
            }
            val jsonArray = jsonObject.getJSONArray("data")

            for(n in 0 until jsonArray.length()) {
                val `object` = jsonArray.getJSONObject(n)
                list.add(commonTreeModel(`object`))
            }
            action(true, list, jsonObject.get("message").toString())
        }, onFailed = {
            val jsonObject = JSONObject(it.receiver.errorData!!)
            action(false, null, jsonObject.get("message").toString())

        })
    }

    @WorkerThread
    suspend fun getDirDtlList(action: suspend (Boolean, ArrayList<codeModel>?, String) -> Unit, jsondata: String? = null, result: String? = null) {
        val list = ArrayList<codeModel>()
        if(!Define.OFFLINE) {
            NetworkSync(TransmitterJson(url = Data.instance.url + "/Mobile", action = "ct_biz.GetDirDtl.IGetData", jsondata = jsondata), ReceiverJson()).get(onSuccessed = {
                val jsonObject = it.receiver.resultData!!
                if(!jsonObject.getBoolean("success")) {
                    action(false, null, jsonObject.get("message").toString())
                }
                val jsonArray = jsonObject.getJSONArray("data")
                for(n in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(n)
                    list.add(codeModel(`object`))
                }
                action(true, list, jsonObject.get("message").toString())
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, null, jsonObject.get("message").toString())

            })
        } else {

            val parser = JsonParser().parse(jsondata)
            val DIR_TYPE = parser.asJsonArray.get(0).asJsonObject.get("DIR_TYPE").let { if(it == null) "" else it.asString }

            val sqLiteDatabase = DBSwitcher.instance.getReadableDatabase(Define.DB_NAME_1)!!
            val strQuery = """SELECT CODE_NO CODE,CODE_NM DESCRIPTION,CODE_TYPE TYPE FROM TS_DIR_DTL
                | WHERE 1=1 AND CODE_TYPE = '$DIR_TYPE'
                | ORDER  BY DESCRIPTION
            """.trimMargin()

            val cursor = sqLiteDatabase.rawQuery(strQuery, null)
            if(cursor != null) {
                cursor!!.moveToFirst()
                while(!cursor!!.isAfterLast()) {
                    list.add(codeModel(cursor!!))
                    cursor!!.moveToNext()
                }
                cursor!!.close()
            }
            sqLiteDatabase.close()
            action(true, list, list.count().toString())
        }
    }

    @WorkerThread
    suspend fun getFailureList(action: suspend (Boolean, ArrayList<codeModel>?, String) -> Unit, jsondata: String? = null, result: String? = null) {
        val list = ArrayList<codeModel>()
        if(!Define.OFFLINE) {
            NetworkSync(TransmitterJson(url = Data.instance.url + "/Mobile", action = "ct_biz.GetFailure.IGetData", jsondata = jsondata), ReceiverJson()).get(onSuccessed = {

                val jsonObject = it.receiver.resultData!!
                if(!jsonObject.getBoolean("success")) {
                    action(false, null, jsonObject.get("message").toString())
                }
                val jsonArray = jsonObject.getJSONArray("data")

                for(n in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(n)
                    list.add(codeModel(`object`))
                }
                action(true, list, jsonObject.get("message").toString())
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, null, jsonObject.get("message").toString())

            })
        }else{
            val sqLiteDatabase = DBSwitcher.instance.getReadableDatabase(Define.DB_NAME_1)!!
            val strQuery = """SELECT CODE_NO CODE,CODE_NM DESCRIPTION,CODE_TYPE TYPE FROM TM_FAILURE              
            """.trimMargin()
            val cursor = sqLiteDatabase.rawQuery(strQuery, null)
            if(cursor != null) {
                cursor!!.moveToFirst()
                while(!cursor!!.isAfterLast()) {
                    list.add(codeModel(cursor!!))
                    cursor!!.moveToNext()
                }
                cursor!!.close()
            }
            sqLiteDatabase.close()
            action(true, list, list.count().toString())
        }
    }

    @WorkerThread
    suspend fun getUserList(action: suspend (Boolean, ArrayList<codeModel>?, String) -> Unit, jsondata: String? = null, result: String? = null) {
        val list = ArrayList<codeModel>()
        if(!Define.OFFLINE) {
            NetworkSync(TransmitterJson(url = Data.instance.url + "/Mobile", action = "ct_biz.GetUser.IGetData", jsondata = jsondata), ReceiverJson()).get(onSuccessed = {

                val jsonObject = it.receiver.resultData!!
                if(!jsonObject.getBoolean("success")) {
                    action(false, null, jsonObject.get("message").toString())
                }
                val jsonArray = jsonObject.getJSONArray("data")

                for(n in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(n)
                    list.add(codeModel(`object`))
                }
                action(true, list, jsonObject.get("message").toString())
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, null, jsonObject.get("message").toString())

            })
        }else{
            val parser = JsonParser().parse(jsondata)
            val DEPT_NO = parser.asJsonArray.get(0).asJsonObject.get("DEPT_NO").let { if(it == null) "" else it.asString }
            val sqLiteDatabase = DBSwitcher.instance.getReadableDatabase(Define.DB_NAME_1)!!
            val strQuery = """
                |SELECT USER_NO CODE,USER_NM DESCRIPTION  
                |  FROM TS_USER WHERE 1=1
                | ${if(DEPT_NO.isNotEmpty()) " AND DEPT_NO = '$DEPT_NO'" else ""}             
            """.trimMargin()
            val cursor = sqLiteDatabase.rawQuery(strQuery, null)
            if(cursor != null) {
                cursor!!.moveToFirst()
                while(!cursor!!.isAfterLast()) {
                    list.add(codeModel(cursor!!))
                    cursor!!.moveToNext()
                }
                cursor!!.close()
            }
            sqLiteDatabase.close()
            action(true, list, list.count().toString())
        }
    }


    @WorkerThread
    suspend fun getSequence(action: suspend (Boolean, String, String) -> Unit, jsondata: String? = null, result: String? = null) {
        if(!Define.OFFLINE) {
            NetworkSync(TransmitterJson(url = Data.instance.url + "/Mobile", action = "ct_biz.GetSequence.IGetData", jsondata = jsondata), ReceiverJson()).get(onSuccessed = {
                var Seq: String = ""

                val jsonObject = it.receiver.resultData!!
                if(!jsonObject.getBoolean("success")) {
                    action(false, "", jsonObject.get("message").toString())
                }
                val jsonArray = jsonObject.getJSONArray("data")

                for(n in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(n)
                    Seq = JsonHelper.getStringFromJson(`object`, "SEQUENCE", "")!!
                }
                action(true, Seq, jsonObject.get("message").toString())
            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, "", jsonObject.get("message").toString())

            })
        } else {
            val parser = JsonParser().parse(jsondata)
            val SEQUENCE = parser.asJsonArray.get(0).asJsonObject.get("SEQUENCE").let { if(it == null) "" else it.asString }
            when(SEQUENCE) {
                "SQ_NOTICE_NO" -> {
                    val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(Define.DB_NAME_1)!!
                    val sqLiteStatement = sqLiteDatabase.compileStatement("INSERT INTO TM_NOTICE (DESCRIPTION) VALUES ('')")
                    sqLiteStatement!!.executeInsert()
                    val no = sqLiteDatabase.lastInsertRowId.toString()
                    Log.d("LOG_NOTICE_NO", no)
                    action(true, no, "")
                    sqLiteStatement!!.close()
                    sqLiteDatabase.close()
                }
            }

        }
    }

    private object SingletonHolder {
        val INSTANCE = commonHelper()
    }

    companion object {
        val instance: commonHelper
            get() = SingletonHolder.INSTANCE
    }
}