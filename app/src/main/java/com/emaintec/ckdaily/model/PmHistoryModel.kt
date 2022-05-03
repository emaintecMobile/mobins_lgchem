package com.emaintec.ckdaily.model

import com.emaintec.lib.base.Model
import com.emaintec.lib.db.JsonHelper
import org.json.JSONException
import org.json.JSONObject

class PmHistoryModel: Model {
    var CHK_DATE: String? = null                        //
    var CHK_RESULT: String? = ""
    var CHK_OKNOK: String? = ""

    constructor() {}
    constructor(jobject: JSONObject) {
        init(jobject)
    }
    constructor(CHK_DATE: String?, CHK_OKNOK: String?, CHK_RESULT: String?) {
        this.CHK_DATE = CHK_DATE
        this.CHK_OKNOK = CHK_OKNOK
        this.CHK_RESULT = CHK_RESULT
    }

    fun init(`object`: JSONObject) {
        try {
            CHK_DATE= JsonHelper.getStringFromJson(`object`, "CHK_DATE","")!!
            CHK_RESULT= JsonHelper.getStringFromJson(`object`, "CHK_RESULT","")!!
            CHK_OKNOK= JsonHelper.getStringFromJson(`object`, "CHK_OKNOK","")!!
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}