package com.emaintec.lib.db

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.sql.SQLException

object SQLiteQueryUtil{
    var DB_NAME = ""
    fun selectJsonArray(strQuery: String): JSONArray {
        val sqLiteDatabase = DBSwitcher.instance.getReadableDatabase(DB_NAME)!!
        var crs = sqLiteDatabase.rawQuery(strQuery, null)!!
        val arr = JSONArray()
        crs.moveToFirst()
        while (!crs.isAfterLast()) {
            val nColumns: Int = crs.getColumnCount()
            val row = JSONObject()
            for (i in 0 until nColumns) {
                val colName: String = crs.getColumnName(i)
                if (colName != null) {
                    val `val` = ""
                    try {
                        when (crs.getType(i)) {
                            Cursor.FIELD_TYPE_BLOB -> row.put(colName, crs.getBlob(i).toString())
                            Cursor.FIELD_TYPE_FLOAT -> row.put(colName, crs.getDouble(i))
                            Cursor.FIELD_TYPE_INTEGER -> row.put(colName, crs.getLong(i))
                            Cursor.FIELD_TYPE_NULL -> row.put(colName, null)
                            Cursor.FIELD_TYPE_STRING -> row.put(colName, crs.getString(i))
                        }
                    } catch (e: JSONException) {
                    }
                }
            }
            arr.put(row)
            if (!crs.moveToNext()) break
        }
        crs.close() // close the cursor
        sqLiteDatabase.close()
        return arr
    }
    fun update_table_Model(dto : Object,strTableName: String, keys: Array<String?>): String {
        var newRowId: Long = 0
        try {
            val result = Gson().toJson(dto)
            val jsonObject = JSONObject(result)

            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!
            sqLiteDatabase.beginTransaction()
            val values = ContentValues()
            var where = ""
            val temp = jsonObject.keys()
            val arField = arTableField(strTableName)
            while (temp.hasNext()) {
                val strColNmkey = temp.next()
                val strValue = jsonObject.get(strColNmkey).toString()

                if(arField.filter { it == strColNmkey }.isEmpty()) {
                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                    continue
                }
                //                if (!isFieldExist(strTableName, strColNmkey)) {
                //                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                //                    continue
                //                }
                for (key in keys) {
                    if (strColNmkey == key) {
                        where += (if (where.isEmpty()) "" else " AND ") + "$key = '$strValue'"
                    }
                }
                values.put(strColNmkey, strValue)
            }
            // Insert the new row, returning the primary key value of the new row
            //public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
            newRowId = sqLiteDatabase.update(strTableName, values, where, null).toLong()

            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()
        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: android.database.SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {

        }
        return "" + newRowId
    }
    fun insert_table_Model(dto : Object,strTableName: String): String {
        var newRowId: Int = 0
        try {
            val result = Gson().toJson(dto)
            val jsonObject = JSONObject(result)

            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!
            sqLiteDatabase.beginTransaction()
            val values = ContentValues()
            var where = ""
            val temp = jsonObject.keys()
            val arField = arTableField(strTableName)
            while (temp.hasNext()) {
                val strColNmkey = temp.next()
                val strValue = jsonObject.get(strColNmkey).toString()

                if(arField.filter { it == strColNmkey }.isEmpty()) {
                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                    continue
                }
                //                if (!isFieldExist(strTableName, strColNmkey)) {
                //                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                //                    continue
                //                }
                if (strValue == "null" || "" == strValue) continue
                values.put(strColNmkey, strValue)
            }
            // Insert the new row, returning the primary key value of the new row
            sqLiteDatabase.insert(strTableName, null, values)
            newRowId = sqLiteDatabase.lastInsertRowId

            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()
        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: android.database.SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {

        }
        return "" + newRowId
    }
    fun replace_table_Model(dto : Object,strTableName: String,bDel: Boolean?): String {
        var newRowId: Int = 0
        if (bDel!!) {
            DBSwitcher.instance.sendMessage(DB_NAME, strTableName)
        }
        try {
            val result = Gson().toJson(dto)
            val jsonObject = JSONObject(result)

            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!
            sqLiteDatabase.beginTransaction()
            val values = ContentValues()
            var where = ""
            val temp = jsonObject.keys()
            val arField = arTableField(strTableName)
            while (temp.hasNext()) {
                val strColNmkey = temp.next()
                val strValue = jsonObject.get(strColNmkey).toString()

                if(arField.filter { it == strColNmkey }.isEmpty()) {
                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                    continue
                }
                //                if (!isFieldExist(strTableName, strColNmkey)) {
                //                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                //                    continue
                //                }
                if (strValue == "null" || "" == strValue) continue
                values.put(strColNmkey, strValue)
            }
            // Insert the new row, returning the primary key value of the new row
            sqLiteDatabase.replace(strTableName, null, values)
            newRowId = sqLiteDatabase.lastInsertRowId

            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()
        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: android.database.SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {

        }
        return "" + newRowId
    }

     fun insert_Table(jsonObject: JSONObject, strTableName: String, bDel: Boolean?): String {
        var newRowId: Long = 0
        try {
            var resultData =""
            if (jsonObject.has("success")){
                resultData ="data"
                if (!jsonObject.getBoolean("success")) {
                    return "error:" + jsonObject.getString("message")
                }
             }
            if (jsonObject.has("resultSts")) {
                resultData ="resultList"
                if ("F".equals(jsonObject.getString("resultSts"))) {
                    return "error:" + jsonObject.getString("resultMsg")
                }
            }
            val jsonArray = jsonObject.getJSONArray(resultData)

            if (bDel!!) {
                DBSwitcher.instance.sendMessage(DB_NAME, strTableName)
            }

            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!

            sqLiteDatabase.beginTransaction()
            val arField = arTableField(strTableName)
            for (n in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(n)
                val values = ContentValues()
                val temp = obj.keys()

                while (temp.hasNext()) {
                    val strColNmkey = temp.next()
                    val strValue = obj.get(strColNmkey).toString()
                    if(arField.filter { it == strColNmkey }.isEmpty()) {
                        Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                        continue
                    }
                    if (strValue == "null" || "" == strValue) continue
                    values.put(strColNmkey, strValue)
                }
                // Insert the new row, returning the primary key value of the new row
                newRowId = sqLiteDatabase.insert(strTableName, null, values)
            }
            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()

        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {
        }
        return "" + newRowId
    }
    fun insert_Table(jsonArray: JSONArray, strTableName: String, bDel: Boolean?): String {
        var newRowId: Long = 0
        try {
            if (bDel!!) {
                DBSwitcher.instance.sendMessage(DB_NAME, strTableName)
            }
            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!

            sqLiteDatabase.beginTransaction()
            val arField = arTableField(strTableName)
            for (n in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(n)
                val values = ContentValues()
                val temp = obj.keys()

                while (temp.hasNext()) {
                    val strColNmkey = temp.next()
                    val strValue = obj.get(strColNmkey).toString()
                    if(arField.filter { it == strColNmkey }.isEmpty()) {
                        Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                        continue
                    }
                    //                if (!isFieldExist(strTableName, strColNmkey)) {
                    //                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                    //                    continue
                    //                }
                    if (strValue == "null" || "" == strValue) continue
                    values.put(strColNmkey, strValue)
                }
                // Insert the new row, returning the primary key value of the new row
                newRowId = sqLiteDatabase.insert(strTableName, null, values)
            }
            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()

        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {
        }
        return "" + newRowId
    }
    fun replace_Table(jsonObject: JSONObject, strTableName: String, bDel: Boolean?): String {
        var newRowId: Long = 0
        try {
            var resultData =""
            if (jsonObject.has("success")){
                resultData ="data"
                if (!jsonObject.getBoolean("success")) {
                    return "error:" + jsonObject.getString("message")
                }
            }
            if (jsonObject.has("resultSts")) {
                resultData ="resultList"
                if ("F".equals(jsonObject.getString("resultSts"))) {
                    return "error:" + jsonObject.getString("resultMsg")
                }
            }
            if (bDel!!) {
                DBSwitcher.instance.sendMessage(DB_NAME, strTableName)
            }
            val jsonArray = jsonObject.getJSONArray(resultData)
            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!
            sqLiteDatabase.beginTransaction()
            val arField = arTableField(strTableName)
            for (n in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(n)
                val values = ContentValues()
                val temp = obj.keys()

                while (temp.hasNext()) {
                    val strColNmkey = temp.next()
                    val strValue = obj.get(strColNmkey).toString()
                    if(arField.filter { it == strColNmkey }.isEmpty()) {
                        Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                        continue
                    }
                    //                if (!isFieldExist(strTableName, strColNmkey)) {
                    //                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                    //                    continue
                    //                }
                    if (strValue == "null" || "" == strValue) continue
                    values.put(strColNmkey, strValue)
                }
                // Insert the new row, returning the primary key value of the new row
                newRowId = sqLiteDatabase.replace(strTableName, null, values)
            }
            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()

        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {
        }
        return "" + newRowId
    }
    fun replace_Table(jsonArray: JSONArray, strTableName: String, bDel: Boolean?): String {
        var newRowId: Long = 0
        try {
            if (bDel!!) {
                DBSwitcher.instance.sendMessage(DB_NAME, strTableName)
            }
            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!

            sqLiteDatabase.beginTransaction()
            val arField = arTableField(strTableName)
            for (n in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(n)
                val values = ContentValues()
                val temp = obj.keys()

                while (temp.hasNext()) {
                    val strColNmkey = temp.next()
                    val strValue = obj.get(strColNmkey).toString()
                    if(arField.filter { it == strColNmkey }.isEmpty()) {
                        Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                        continue
                    }
                    //                if (!isFieldExist(strTableName, strColNmkey)) {
                    //                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                    //                    continue
                    //                }
                    if (strValue == "null" || "" == strValue) continue
                    values.put(strColNmkey, strValue)
                }
                // Insert the new row, returning the primary key value of the new row
                newRowId = sqLiteDatabase.replace(strTableName, null, values)
            }
            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()

        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {
        }
        return "" + newRowId
    }

    fun update_table(result: String, strTableName: String, keys: Array<String?>): String {
        var newRowId: Long = 0
        //JSONObject jobj = new JSONObject();
        try {
            val jsonObject = JSONObject(result)

            var resultData =""
            if (jsonObject.has("success")){
                resultData ="data"
                if (!jsonObject.getBoolean("success")) {
                    return "error:" + jsonObject.getString("message")
                }
            }
            if (jsonObject.has("resultSts")) {
                resultData ="resultList"
                if ("F".equals(jsonObject.getString("resultSts"))) {
                    return "error:" + jsonObject.getString("resultMsg")
                }
            }
            val jsonArray = jsonObject.getJSONArray(resultData)

            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!
            sqLiteDatabase.beginTransaction()
            val arField = arTableField(strTableName)
            for (n in 0 until jsonArray.length()) {
                val jObject = jsonArray.getJSONObject(n)
                val values = ContentValues()
                var where = ""
                val temp = jObject.keys()
                while (temp.hasNext()) {
                    val strColNmkey = temp.next()
                    val strValue = jObject.get(strColNmkey).toString()

                    if(arField.filter { it == strColNmkey }.isEmpty()) {
                        Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                        continue
                    }
                    //                if (!isFieldExist(strTableName, strColNmkey)) {
                    //                    Log.d("LOG_noCol", "$strTableName - $strColNmkey")
                    //                    continue
                    //                }
                    for (key in keys) {
                        if (strColNmkey == key) {
                            where += (if (where.isEmpty()) "" else " AND ") + "$key = '$strValue'"
                        }
                    }
                    values.put(strColNmkey, strValue)

                }
                // Insert the new row, returning the primary key value of the new row
                //public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
                newRowId = sqLiteDatabase.update(strTableName, values, where, null).toLong()
            }

            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()
        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {

        }
        return "" + newRowId
    }
    fun delete_table(result: String, strTableName: String, keys: Array<String?>): String {
        var newRowId: Long = 0
        //JSONObject jobj = new JSONObject();
        try {
            val jsonObject = JSONObject(result)
            var resultData =""
            if (jsonObject.has("success")){
                resultData ="data"
                if (!jsonObject.getBoolean("success")) {
                    return "error:" + jsonObject.getString("message")
                }
            }
            if (jsonObject.has("resultSts")) {
                resultData ="resultList"
                if ("F".equals(jsonObject.getString("resultSts"))) {
                    return "error:" + jsonObject.getString("resultMsg")
                }
            }
            val jsonArray = jsonObject.getJSONArray(resultData)
            val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!
            sqLiteDatabase.beginTransaction()

            for (n in 0 until jsonArray.length()) {
                val jObject = jsonArray.getJSONObject(n)
                var where = ""
                val temp = jObject.keys()
                while (temp.hasNext()) {
                    val strColNmkey = temp.next()
                    val strValue = jObject.get(strColNmkey).toString()
                    for (key in keys) {
                        if (strColNmkey == key) {
                            where += (if (where.isEmpty()) "" else " AND ") + "$key = '$strValue'"
                        }
                    }
                }

                newRowId = sqLiteDatabase.delete(strTableName,  where, null).toLong()
            }

            sqLiteDatabase.setTransactionSuccessful()
            sqLiteDatabase.endTransaction()
            sqLiteDatabase.close()
        } catch (e: JSONException) {
            e.printStackTrace()
            return "error:$e"
        } catch (e: SQLException) {
            e.printStackTrace()
            return "error:$e"
        } finally {

        }
        return "" + newRowId
    }
    // This method will check if column exists in your table
    fun isFieldExist(tableName: String, fieldName: String): Boolean {
        var isExist = false
        val db = DBSwitcher.instance.getReadableDatabase(DB_NAME)!!
        val res = db.rawQuery("PRAGMA table_info($tableName)", null)
        res!!.moveToFirst()
        do {
            val currentColumn = res.getString(1)
            if (currentColumn == fieldName) {
                isExist = true
                res.close()
                return isExist
            }
        } while (res.moveToNext())
        res.close()
        return isExist
    }

    fun arTableField(tableName: String): ArrayList<String> {
        var arExist:ArrayList<String> = ArrayList()
        val db = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!
        val res = db.rawQuery("PRAGMA table_info($tableName)", null)!!
        res.moveToFirst()
        do {
            val currentColumn = res.getString(1)
            arExist.add(currentColumn)
        } while (res.moveToNext())
        res.close();
        return arExist
    }
    fun executeSql(sql:String)
    {
        val sqLiteDatabase = DBSwitcher.instance.getWritableDatabase(DB_NAME)!!
        try {
            sqLiteDatabase.execSQL(sql)
        } catch (e: SQLException) {
            Log.d("LOG_SQLException", e.toString())
        }
    }
}