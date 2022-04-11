package com.emaintec.ckdaily.model

import com.emaintec.lib.base.Model

class PmHistoryModel: Model {
    constructor() {}
    constructor(CHK_DATE: String?, CHK_OKNOK: String?, CHK_RESULT: String?) {
        this.CHK_DATE = CHK_DATE
        this.CHK_OKNOK = CHK_OKNOK
        this.CHK_RESULT = CHK_RESULT
    }
    var CHK_DATE: String? = null                        //
    var CHK_RESULT: String? = ""
    var CHK_OKNOK: String? = ""

}