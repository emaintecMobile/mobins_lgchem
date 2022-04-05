package com.emaintec.lib.base

import com.emaintec.lib.db.JsonHelper
import com.google.gson.Gson
import org.json.JSONObject

open class Model {
    var IS_EXPANDER : Boolean = false
    var STATUS: String = "" // 'D' 삭제 ,'U' 수정 ,'I' 입력
    var VISIBILITY: String = "S" // 'H' 숨김
    var LINE_BG = "" // 조건에 따라 설정할 행의 색을 넣을 변수 ex)#FFFFFF
    constructor() {
    }
    fun  getValue(columnName : String) : String {
        val json = JSONObject(Gson().toJson(this))
        return JsonHelper.getStringFromJson(json, columnName,"")!!
    }
}