package com.emaintec.db

import com.emaintec.lib.db.DBSwitcher
import com.emaintec.lib.db.SQLiteDatabase
import com.emaintec.Data
import com.emaintec.Define

class DBInterface_1 : DBSwitcher.DBListener {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(
            """CREATE TABLE IF NOT EXISTS PDASETTING (
            CODE VARCHAR2 ( 20 ) NOT NULL
            , NAME VARCHAR2 ( 250 ) NOT NULL
            , DESCRIPTION VARCHAR2 ( 250 )
            , PRIMARY KEY(CODE) )"""
        )
        sqLiteDatabase.execSQL("INSERT OR REPLACE INTO PDASETTING (CODE,NAME,DESCRIPTION) VALUES ('URL','${Data.instance.url}','서버주소')")
        resetTable(sqLiteDatabase, "")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when {
//            oldVersion <= 2 -> {
//                 resetTable_T1PART_SURVEY_RESULT(sqLiteDatabase)
//             }
        }
    }


    override fun onMessage(message: String) {
        val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(Define.DB_NAME_1)
        resetTable(sqLiteDatabase!!, message)
        sqLiteDatabase.close()
    }

    fun resetTable(sqLiteDatabase: SQLiteDatabase, tableName: String) {

        when (tableName) {
            "", null -> {
                T9MENU(sqLiteDatabase)
                TB_PM_WKCENTER(sqLiteDatabase)
                TB_MCD(sqLiteDatabase)
                TB_PM_DAYCP(sqLiteDatabase)
                TB_PM_DAYMST(sqLiteDatabase)
                TB_PM_MASTER(sqLiteDatabase)
                TB_PM_MSTCP(sqLiteDatabase)
                T9MYMENU(sqLiteDatabase)

            }
            "T9MENU" -> T9MENU(sqLiteDatabase)
            "TB_PM_WKCENTER" ->TB_PM_WKCENTER(sqLiteDatabase)
            "TB_MCD" -> TB_MCD(sqLiteDatabase)
            "TB_PM_DAYCP" -> TB_PM_DAYCP(sqLiteDatabase)
            "TB_PM_DAYMST" -> TB_PM_DAYMST(sqLiteDatabase)
            "TB_PM_MASTER" -> TB_PM_MASTER(sqLiteDatabase)
            "TB_PM_MSTCP" -> TB_PM_MSTCP(sqLiteDatabase)
            "T9MYMENU" -> T9MYMENU(sqLiteDatabase)
        }


    }

    //메뉴:
    private fun T9MENU(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS T9MENU")
        sqLiteDatabase.execSQL(
            """CREATE TABLE IF NOT EXISTS T9MENU (
                  MENU_SEQ VARCHAR2 ( 20 ) NOT NULL
            , MENU_ID VARCHAR2 ( 20 ) NOT NULL
            , MENU_NAME VARCHAR2 ( 50 ) NOT NULL
            , P_MENU_ID VARCHAR2 ( 20 )
            , MENU_TYPE VARCHAR2 ( 20 ) NOT NULL
            , MENU_CLASS VARCHAR2 ( 20 )
            , MENU_SUB_CLASS VARCHAR2 ( 50 )
            , SORT_NO INTEGER NOT NULL DEFAULT 0
            , IS_USE VARCHAR2 ( 1 ) NOT NULL DEFAULT 'N'
            , PRIMARY KEY(MENU_ID) )"""
        )
        sqLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS IDX_T9MENU ON T9MENU ( P_MENU_ID , SORT_NO  )")
    }

    //메뉴:
    private fun T9MYMENU(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS T9MYMENU")
        sqLiteDatabase.execSQL(
            """CREATE TABLE IF NOT EXISTS T9MYMENU (
              MENU_SEQ VARCHAR2 ( 20 ) NOT NULL
            , MY_MENU_SORT INTEGER NOT NULL DEFAULT 0
            , PRIMARY KEY(MENU_SEQ) )"""
        )
    }

    private fun TB_MCD(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_MCD")
        sqLiteDatabase.execSQL(
            """
            CREATE TABLE IF NOT EXISTS TB_MCD(
            KEYWORD TEXT 
            ,MCD_COLOR TEXT 
            ,MCD_GROUP TEXT 
            ,PRIMARY KEY(KEYWORD,MCD_GROUP))
            """.trimIndent()
        )
    }

    private fun TB_PM_DAYCP(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_PM_DAYCP")
        sqLiteDatabase.execSQL(
            """
            CREATE TABLE IF NOT EXISTS TB_PM_DAYCP(
            PM_NOTI_NO TEXT 
            ,CHK_NO TEXT 
            ,CHK_SEQ TEXT 
            ,PM_EQP_NO TEXT 
            ,PM_PLAN TEXT 
            ,PM_GROUP TEXT 
            ,PM_PLN_DT TEXT 
            ,CHK_POS TEXT 
            ,CHK_DESC TEXT 
            ,CHK_IN_TYP TEXT 
            ,PM_STANDBY TEXT 
            ,CHK_DATE TEXT 
            ,CHK_RESULT TEXT 
            ,CHK_OKNOK TEXT 
            ,CHK_NOTI TEXT 
            ,PM_STRANGE TEXT 
            ,CHK_MEMO TEXT 
            ,PM_WKCNTR TEXT 
            ,PM_CHECK TEXT 
            ,PM_INST TEXT 
            ,CHK_TIME TEXT 
            ,CHK_FLEX1 TEXT 
            ,CHK_FLEX2 TEXT 
            ,CHK_FLEX3 TEXT 
            ,PRIMARY KEY(PM_NOTI_NO,CHK_NO,PM_EQP_NO,PM_PLAN))
            """.trimIndent()
        )
        sqLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS IDX_TB_PM_DAYCP ON TB_PM_DAYCP(PM_NOTI_NO,CHK_NO,PM_EQP_NO,PM_PLAN )")
    }

    private fun TB_PM_DAYMST(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_PM_DAYMST")
        sqLiteDatabase.execSQL(
            """
            CREATE TABLE IF NOT EXISTS TB_PM_DAYMST(
            PM_EQP_NO TEXT 
            ,PM_TAG_NO TEXT 
            ,PM_EQP_NM TEXT 
            ,PM_PLN_DT TEXT 
            ,PM_CHECK TEXT 
            ,PM_STRANGE TEXT 
            ,CHK_NOTI TEXT 
            ,CHK_PART TEXT 
            ,FLEX1 TEXT 
            ,FLEX2 TEXT 
            ,FLEX3 TEXT 
            ,PRIMARY KEY(PM_EQP_NO,PM_PLN_DT))
            """.trimIndent()
        )
        sqLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS IDX_TB_PM_DAYMST ON TB_PM_DAYMST(PM_EQP_NO,PM_PLN_DT )")
    }

    //코드:
    private fun TB_PM_MASTER(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_PM_MASTER")
        sqLiteDatabase.execSQL(
            """
            CREATE TABLE IF NOT EXISTS TB_PM_MASTER(
            PM_PLAN TEXT 
            ,PM_GROUP TEXT 
            ,PM_EQP_NO TEXT 
            ,PM_TAG_NO TEXT 
            ,PM_EQP_NM TEXT 
            ,PM_CYCLE TEXT 
            ,PM_WKCNTR TEXT 
            ,PM_LDATE TEXT 
            ,FLEX1 TEXT 
            ,FLEX2 TEXT 
            ,FLEX3 TEXT 
            ,PRIMARY KEY(PM_PLAN,PM_GROUP,PM_EQP_NO))
            """.trimIndent()
        )
        sqLiteDatabase.execSQL(" CREATE INDEX IF NOT EXISTS IDX_TB_PM_MASTER ON TB_PM_MASTER(PM_PLAN,PM_GROUP,PM_EQP_NO )")
    }

    private fun TB_PM_MSTCP(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_PM_MSTCP")
        sqLiteDatabase.execSQL(
          """
              CREATE TABLE IF NOT EXISTS TB_PM_MSTCP(
              PM_PLAN TEXT 
              ,PM_GROUP TEXT 
              ,PM_EQP_NO TEXT 
              ,CHK_NO TEXT 
              ,CHK_SEQ TEXT 
              ,CHK_POS TEXT 
              ,CHK_DESC TEXT 
              ,PM_WKCNTR TEXT 
              ,CHK_CHAR TEXT 
              ,CHK_CH_DE TEXT 
              ,CHK_UNIT TEXT 
              ,CHK_MIN TEXT 
              ,CHK_DEST TEXT 
              ,CHK_IN_TYP TEXT 
              ,CHK_MAX TEXT 
              ,CHK_LDATE1 TEXT 
              ,CHK_LRSLT1 TEXT 
              ,CHK_OKNOK1 TEXT 
              ,CHK_LDATE2 TEXT 
              ,CHK_LRSLT2 TEXT 
              ,CHK_OKNOK2 TEXT 
              ,CHK_FLEX1 TEXT 
              ,CHK_FLEX2 TEXT 
              ,CHK_FLEX3 TEXT 
              ,PRIMARY KEY(PM_PLAN,CHK_NO))
          """.trimIndent()
        )
        sqLiteDatabase.execSQL(" CREATE INDEX IF NOT EXISTS IDX_TB_PM_MSTCP ON TB_PM_MSTCP(PM_PLAN,CHK_NO)")
    }

    //사용자:
    private fun TB_PM_WKCENTER(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_PM_WKCENTER")
        sqLiteDatabase.execSQL(
            """
                CREATE TABLE IF NOT EXISTS TB_PM_WKCENTER(
                WKCNTR_NO TEXT 
                ,WKCNTR_NM TEXT 
                ,WKCNTR_PDA TEXT 
                ,PRIMARY KEY(WKCNTR_NO))     
            """.trimIndent()

        )
    }
}
