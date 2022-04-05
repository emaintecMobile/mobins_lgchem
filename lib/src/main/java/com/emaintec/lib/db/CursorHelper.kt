package com.emaintec.lib.db

import android.database.Cursor
import android.util.Log
import org.json.JSONObject

object CursorHelper {

	fun getBooleanFromCursor(cursor: Cursor, columnName: String, defaultValue: Boolean): Boolean {

		val index = cursor.getColumnIndex(columnName)

		return if (0 <= index) "Y" == cursor.getString(index) else defaultValue
	}

	fun getIntFromCursor(cursor: Cursor, columnName: String, defaultValue: Int): Int {

		val index = cursor.getColumnIndex(columnName)

		return if (0 <= index) cursor.getInt(index) else defaultValue
	}

	fun getLongFromCursor(cursor: Cursor, columnName: String, defaultValue: Long): Long {

		val index = cursor.getColumnIndex(columnName)

		return if (0 <= index) cursor.getLong(index) else defaultValue
	}

	fun getFloatFromCursor(cursor: Cursor, columnName: String, defaultValue: Float): Float {

		val index = cursor.getColumnIndex(columnName)

		return if (0 <= index) cursor.getFloat(index) else defaultValue
	}

	fun getDoubleFromCursor(cursor: Cursor, columnName: String, defaultValue: Double): Double {

		val index = cursor.getColumnIndex(columnName)

		return if (0 <= index) cursor.getDouble(index) else defaultValue
	}

	fun getBlobFromCursor(cursor: Cursor, columnName: String, defaultValue: ByteArray?): ByteArray? {

		val index = cursor.getColumnIndex(columnName)

		return if (0 <= index) cursor.getBlob(index) else defaultValue
	}

	fun getStringFromCursor(cursor: Cursor, columnName: String, defaultValue: String?): String? {

		val index = cursor.getColumnIndex(columnName)

		if (0 > index)
			return defaultValue

		val returnValue = cursor.getString(index)

		return returnValue ?: defaultValue
	}
	fun cursorToJson(c: Cursor): JSONObject? {
		val retVal = JSONObject()
		for(i in 0 until c.columnCount) {
			val cName = c.getColumnName(i)
			try {
				when(c.getType(i)) {
					Cursor.FIELD_TYPE_INTEGER -> retVal.put(cName, c.getInt(i))
					Cursor.FIELD_TYPE_FLOAT -> retVal.put(cName, c.getFloat(i).toDouble())
					Cursor.FIELD_TYPE_STRING -> retVal.put(cName, c.getString(i))
					Cursor.FIELD_TYPE_BLOB -> retVal.put(cName, c.getBlob(i))
				}
			} catch(ex: Exception) {
				Log.e("CursorHelper", "Exception converting cursor column to json field: $cName")
			}
		}
		return retVal
	}
}
