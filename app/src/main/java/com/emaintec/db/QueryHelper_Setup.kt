package com.emaintec.db

import android.database.SQLException
import android.util.Log
import com.emaintec.lib.db.DBSwitcher
import com.emaintec.Define
import java.util.*

class QueryHelper_Setup private constructor() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    var mapSetting = HashMap<String, String>()
    fun querySettingSave(): Boolean {
        val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(Define.DB_NAME_1)
        try {
            for (key in mapSetting.keys) {
                val sql =
                    "INSERT OR REPLACE INTO PDASETTING ( CODE, NAME ) VALUES ( '" + key + "', '" + mapSetting[key] + "')"
                sqLiteDatabase!!.execSQL(sql)
            }
            sqLiteDatabase!!.close()
        } catch (e: SQLException) {
            Log.d("LOG_SQLException", e.toString())
            return false
        }

        return true
    }

//    fun queryResourceSave(model: langModel): Boolean {
//        val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(Define.DB_NAME_1)
//        try {
//            val sql =
//                """INSERT OR REPLACE INTO TS_RESOURCE ( KEY_ID, KEY_NAME,KEY_TYPE,LANG )
//                    |VALUES ( '${model.KEY_ID}', '${model.KEY_NAME}','${model.KEY_TYPE}','${model.LANG}')""".trimMargin()
//            sqLiteDatabase!!.execSQL(sql)
//            sqLiteDatabase!!.close()
//        } catch (e: SQLException) {
//            Log.d("LOG_SQLException", e.toString())
//            return false
//        }
//        return true
//    }
//
//    fun getLangName(key_id: String, is_hint: Boolean = false) : String? {
//        val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(Define.DB_NAME_1)
//        val sql =
//            """SELECT KEY_NAME,KEY_HINT FROM TS_RESOURCE  WHERE  KEY_ID ='$key_id'"""
//        var KEY_NAME :String ?= null
//        var KEY_HINT :String ?= null
//        val cursor = sqLiteDatabase!!.rawQuery(sql, null)
//        if (cursor != null) {
//
//            cursor.moveToFirst()
//            while (!cursor.isAfterLast) {
//                KEY_NAME = cursor.getString(cursor.getColumnIndex("KEY_NAME"))
//                KEY_HINT = cursor.getString(cursor.getColumnIndex("KEY_HINT"))
//                cursor.moveToNext()
//            }
//            cursor.close()
//        }
//        sqLiteDatabase!!.close()
//        var retunValue:String ? = KEY_NAME
//        if (is_hint) retunValue = KEY_HINT
//        return retunValue
//    }

    private object SingletonHolder {
        val INSTANCE = QueryHelper_Setup()
    }

    init {

        val sqLiteDatabase = DBSwitcher.instance.getReadableDatabase(Define.DB_NAME_1)
        val strSelectSql = "SELECT CODE, NAME FROM PDASETTING"

        val cursor = sqLiteDatabase!!.rawQuery(strSelectSql, null)

        if (cursor != null) {

            cursor.moveToFirst()

            while (!cursor.isAfterLast) {
                mapSetting[cursor.getString(cursor.getColumnIndex("CODE"))] =
                    cursor.getString(cursor.getColumnIndex("NAME"))
                cursor.moveToNext()
            }
            cursor.close()
        }

        sqLiteDatabase.close()
    }

    companion object {

        val instance: QueryHelper_Setup
            get() = SingletonHolder.INSTANCE
    }

}
