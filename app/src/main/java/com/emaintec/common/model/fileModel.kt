package com.emaintec.common.model

import android.database.Cursor
import com.emaintec.lib.db.CursorHelper
import com.emaintec.lib.db.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class fileModel {
    var FILE_ID: Int = 0
    var DATA_RAW: ByteArray? = null
    var DATA_NO: String? = null
    var DESCRIPTION:String?= null
    var DATA_TYPE: String? = null
    var FILE_NO: String? = null
    var FILE_PATH: String? = null
    var PHOTO_LOC: String? = null

    constructor() {
        FILE_ID = 0
    }

    constructor(cursor: Cursor) {
        init(cursor)
    }

    fun init(cursor: Cursor) {
        FILE_ID = CursorHelper.getIntFromCursor(cursor, "FILE_ID", 0)
        DATA_RAW = CursorHelper.getBlobFromCursor(cursor, "DATA_RAW", null)
        DATA_NO = CursorHelper.getStringFromCursor(cursor, "DATA_NO", "")
        DESCRIPTION= CursorHelper.getStringFromCursor(cursor, "DESCRIPTION", "")
        DATA_TYPE = CursorHelper.getStringFromCursor(cursor, "DATA_TYPE", "")
        FILE_NO = CursorHelper.getStringFromCursor(cursor, "FILE_NO", null)
        FILE_PATH = CursorHelper.getStringFromCursor(cursor, "FILE_PATH", "")
        PHOTO_LOC = CursorHelper.getStringFromCursor(cursor, "PHOTO_LOC", "")
    }
}