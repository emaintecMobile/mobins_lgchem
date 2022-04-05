package com.emaintec.common.model

import android.database.Cursor
import android.graphics.Color
import android.view.Gravity
import com.emaintec.lib.db.CursorHelper
import com.emaintec.lib.db.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class gridModel {
    var IS_TITLE: Boolean = false
    var IS_MERGE: Boolean = false
    var TYPE: String = "TXT"
    var NUMBER_FORMAT: String = ""
    var COLUMN_NAME: String = ""
    var WIDTH: Float = 0f
    var KEY: String = ""
    var GRAVITY: Int = Gravity.CENTER
    var COLOR: Int = Color.BLACK
    var TEXT_SIZE: Float = 14f
    var TEXT_LINE: Int = 0
    var COLOR_1: Int = Color.RED
    var COLOR_2: Int = Color.BLUE
    var Select_color_1: Array<String>? = null
    var Select_color_2: Array<String>? = null
    var Select_color_std: Float? = null
    var IS_CLICK: Boolean = false
    constructor() {
    }

    constructor(
        colum_name: String, width: Int, key: String, gravity: Int = Gravity.CENTER
        , textSize: Float = 14f, textColor: Int = Color.BLACK, textLine: Int = 0
    ) {
        COLUMN_NAME = colum_name
        WIDTH = width.toFloat()
        KEY = key
        GRAVITY = gravity
        TEXT_SIZE = textSize
        COLOR = textColor
        TEXT_LINE = textLine
    }

    constructor(
        colum_name: String,
        width: Int,
        key: String,
        is_title: Boolean = false,
        is_merge: Boolean = false
    ) {
        COLUMN_NAME = colum_name
        WIDTH = width.toFloat()
        KEY = key
        IS_TITLE = is_title
        IS_MERGE = is_merge

    }

    override fun toString(): String {
        return KEY!!
    }
}