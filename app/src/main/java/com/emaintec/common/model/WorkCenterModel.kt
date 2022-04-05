package com.emaintec.common.model

import android.database.Cursor
import com.emaintec.lib.db.CursorHelper
import com.emaintec.lib.db.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class WorkCenterModel {

    var WKCNTR_NO:String = ""
    var WKCNTR_NM:String = ""
    var WKCNTR_PDA:String = ""

    constructor() {
    }
    constructor(key:String,description:String) {
        WKCNTR_NO = key
        WKCNTR_NM = description
    }
    constructor(cursor: Cursor) {
        init(cursor)
    }

    constructor(jobject: JSONObject) {
        init(jobject)
    }

    fun init(`object`: JSONObject) {
        try {
            WKCNTR_NO= JsonHelper.getStringFromJson(`object`, "WKCNTR_NO","")!!
            WKCNTR_NM= JsonHelper.getStringFromJson(`object`, "WKCNTR_NM","")!!
            WKCNTR_PDA= JsonHelper.getStringFromJson(`object`, "WKCNTR_PDA","")!!
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun init(cursor: Cursor) {
        WKCNTR_NO = CursorHelper.getStringFromCursor(cursor, "WKCNTR_NO","")!!
        WKCNTR_NM = CursorHelper.getStringFromCursor(cursor, "WKCNTR_NM","")!!
        WKCNTR_PDA = CursorHelper.getStringFromCursor(cursor, "WKCNTR_PDA","")!!
   }
    override fun toString(): String {
        return WKCNTR_NM!!
    }
}