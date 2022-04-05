package com.emaintec.common.model

import android.database.Cursor
import com.emaintec.lib.db.CursorHelper
import com.emaintec.lib.db.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class codeModel {

    var CODE:String = ""
    var P_CODE:String = ""
    var DESCRIPTION:String = ""
    var FLEXFIELD1:String = ""
    var TYPE:String = ""
    var TYPE_DESC:String = ""
    constructor() {
    }
    constructor(key:String,description:String) {
        CODE = key
        DESCRIPTION = description
    }
    constructor(cursor: Cursor) {
        init(cursor)
    }

    constructor(jobject: JSONObject) {
        init(jobject)
    }

    fun init(`object`: JSONObject) {
        try {
            CODE= JsonHelper.getStringFromJson(`object`, "CODE","")!!
            DESCRIPTION= JsonHelper.getStringFromJson(`object`, "DESCRIPTION","")!!
            FLEXFIELD1= JsonHelper.getStringFromJson(`object`, "FLEXFIELD1","")!!
            TYPE= JsonHelper.getStringFromJson(`object`, "TYPE","")!!
            TYPE_DESC= JsonHelper.getStringFromJson(`object`, "TYPE_DESC","")!!
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun init(cursor: Cursor) {
        CODE = CursorHelper.getStringFromCursor(cursor, "CODE","")!!
        DESCRIPTION = CursorHelper.getStringFromCursor(cursor, "DESCRIPTION","")!!
        FLEXFIELD1 = CursorHelper.getStringFromCursor(cursor, "FLEXFIELD1","")!!
        TYPE = CursorHelper.getStringFromCursor(cursor, "TYPE","")!!
        TYPE_DESC= CursorHelper.getStringFromCursor(cursor, "TYPE_DESC","")!!
   }
    override fun toString(): String {
        return DESCRIPTION!!
    }
}