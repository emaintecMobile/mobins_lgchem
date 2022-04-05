package com.emaintec.common.model

import android.database.Cursor
import android.graphics.Color
import android.view.Gravity
import com.emaintec.Functions
import com.emaintec.lib.db.CursorHelper
import com.emaintec.lib.db.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class gridViewModel {
    var IS_EXPAND: Boolean = true
    var IS_TITLE: Boolean = false
    var TYPE: String = "TXT"
    var COLUMN_NAME: String = ""
    var KEY: String = ""
    var GRAVITY: Int = Gravity.CENTER
    var COLOR: Int = Color.BLACK
    var TEXT_SIZE: Float = 16f
    var TEXT_LINE: Int = 0
    var COLOR_1: Int = Color.RED
    var COLOR_2: Int = Color.BLUE
    var Select_color_1: Array<String>? = null
    var Select_color_2: Array<String>? = null
    var Select_color_std: Float? = null
    var listModel : ArrayList<gridViewModel>? = null
    constructor() {
    }


    constructor(
        colum_name: String,
        key: String,
        is_title: Boolean = false
    ) {
        COLUMN_NAME = colum_name
        KEY = key
        IS_TITLE = is_title
    }

    override fun toString(): String {
        return KEY!!
    }
}