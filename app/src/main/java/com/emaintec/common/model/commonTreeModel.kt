package com.emaintec.common.model

import android.database.Cursor
import com.emaintec.lib.db.CursorHelper
import com.emaintec.lib.db.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class commonTreeModel {
    var P_KEY:String = ""
    var KEY:String = ""
    var CHILD:String = ""
    var DESCRIPTION:String = ""
    var ORG_DESCRIPTION:String = ""
    var EQ_ORG_DESC:String = ""
    var EQ_ORG_NO:String = ""
    var SORT_NO:String = ""
    var SHOW_LEVEL:String = ""

    constructor() {
    }
    constructor(key:String,description:String) {
        KEY = key
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
            P_KEY= JsonHelper.getStringFromJson(`object`, "P_KEY","")!!
            KEY= JsonHelper.getStringFromJson(`object`, "KEY","")!!
            CHILD= JsonHelper.getStringFromJson(`object`, "CHILD","")!!
            DESCRIPTION= JsonHelper.getStringFromJson(`object`, "DESCRIPTION","")!!
            ORG_DESCRIPTION= JsonHelper.getStringFromJson(`object`, "ORG_DESCRIPTION","")!!
            EQ_ORG_DESC= JsonHelper.getStringFromJson(`object`, "EQ_ORG_DESC","")!!
            EQ_ORG_NO= JsonHelper.getStringFromJson(`object`, "EQ_ORG_NO","")!!
            SORT_NO= JsonHelper.getStringFromJson(`object`, "SORT_NO","")!!
            SHOW_LEVEL= JsonHelper.getStringFromJson(`object`, "SHOW_LEVEL","")!!

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun init(cursor: Cursor) {
        P_KEY = CursorHelper.getStringFromCursor(cursor, "P_KEY","")!!
        KEY = CursorHelper.getStringFromCursor(cursor, "KEY","")!!
        CHILD = CursorHelper.getStringFromCursor(cursor, "CHILD","")!!
        DESCRIPTION = CursorHelper.getStringFromCursor(cursor, "DESCRIPTION","")!!
        ORG_DESCRIPTION = CursorHelper.getStringFromCursor(cursor, "ORG_DESCRIPTION","")!!
        EQ_ORG_DESC = CursorHelper.getStringFromCursor(cursor, "EQ_ORG_DESC","")!!
        EQ_ORG_NO = CursorHelper.getStringFromCursor(cursor, "EQ_ORG_NO","")!!
        SORT_NO = CursorHelper.getStringFromCursor(cursor, "SORT_NO","")!!
        SHOW_LEVEL = CursorHelper.getStringFromCursor(cursor, "SHOW_LEVEL","")!!

    }

}