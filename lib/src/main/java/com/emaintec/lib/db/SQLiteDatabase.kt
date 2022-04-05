package com.emaintec.lib.db

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import java.lang.Exception

class SQLiteDatabase {

	private var _sqLiteDatabase_Normal: android.database.sqlite.SQLiteDatabase? = null
	private var _sqLiteDatabase_Cipher: net.sqlcipher.database.SQLiteDatabase? = null

	fun set(sqLiteDatabase: android.database.sqlite.SQLiteDatabase) {
		_sqLiteDatabase_Normal = sqLiteDatabase
	}

	fun set(sqLiteDatabase: net.sqlcipher.database.SQLiteDatabase) {
		_sqLiteDatabase_Cipher = sqLiteDatabase
	}

	fun reset() {
		_sqLiteDatabase_Normal = null
		_sqLiteDatabase_Cipher = null
	}

	fun close() {
//20200820 yyc 닫고 다시열때 에러가 빈번함.. 안닫고 써도 이상없음		
//		_sqLiteDatabase_Cipher?.close()
//		_sqLiteDatabase_Normal?.close()
	}

	fun beginTransaction() {
		_sqLiteDatabase_Cipher?.beginTransaction()
		_sqLiteDatabase_Normal?.beginTransaction()
	}

	fun endTransaction() {
		_sqLiteDatabase_Cipher?.endTransaction()
		_sqLiteDatabase_Normal?.endTransaction()
	}

	fun setTransactionSuccessful() {
		_sqLiteDatabase_Cipher?.setTransactionSuccessful()
		_sqLiteDatabase_Normal?.setTransactionSuccessful()
	}

	fun insert(table: String, nullColumnHack: String?, values: ContentValues): Long {
		if (null != _sqLiteDatabase_Cipher) {
			return _sqLiteDatabase_Cipher!!.insert(table, nullColumnHack, values)
		} else if (null != _sqLiteDatabase_Normal) {
			return _sqLiteDatabase_Normal!!.insert(table, nullColumnHack, values)
		}
		return -1
	}
	fun replace(table: String, nullColumnHack: String?, values: ContentValues): Long {
		if (null != _sqLiteDatabase_Cipher) {
			return _sqLiteDatabase_Cipher!!.replace(table, nullColumnHack, values)
		} else if (null != _sqLiteDatabase_Normal) {
			return _sqLiteDatabase_Normal!!.replace(table, nullColumnHack, values)
		}
		return -1
	}

	fun update(table: String, values: ContentValues, whereClause: String, whereArgs: Array<String>?): Int {
		if (null != _sqLiteDatabase_Cipher) {
			return _sqLiteDatabase_Cipher!!.update(table, values, whereClause, whereArgs)
		} else if (null != _sqLiteDatabase_Normal) {
			return _sqLiteDatabase_Normal!!.update(table, values, whereClause, whereArgs)
		}
		return -1
	}

	fun delete(table: String, whereClause: String, whereArgs: Array<String>?): Int {
		if (null != _sqLiteDatabase_Cipher) {
			return _sqLiteDatabase_Cipher!!.delete(table,  whereClause, whereArgs)
		} else if (null != _sqLiteDatabase_Normal) {
			return _sqLiteDatabase_Normal!!.delete(table,  whereClause, whereArgs)
		}
		return -1
	}
	fun execSQL(sql: String): Boolean {
		try {
			Log.d("sqlite",sql)
			_sqLiteDatabase_Cipher?.execSQL(sql)
			_sqLiteDatabase_Normal?.execSQL(sql)
		} catch (e: Exception) {
			e.stackTrace
			return false
		}
		return true
	}

	fun rawQuery(sql: String, selectionArgs: Array<String>?): Cursor? {
		try {
			Log.d("sqlite",sql)
			if (null != _sqLiteDatabase_Cipher) {
				return _sqLiteDatabase_Cipher!!.rawQuery(sql, selectionArgs)
			} else if (null != _sqLiteDatabase_Normal) {
				return _sqLiteDatabase_Normal!!.rawQuery(sql, selectionArgs)
			}
		} catch (e: Exception) {
			e.stackTrace
			return null
		}
		return null
	}

	fun compileStatement(sql: String): SQLiteStatement? {
		if (null != _sqLiteDatabase_Cipher) {
			val sqLiteStatement = SQLiteStatement()
			sqLiteStatement.set(_sqLiteDatabase_Cipher!!.compileStatement(sql))
			return sqLiteStatement
		} else if (null != _sqLiteDatabase_Normal) {
			val sqLiteStatement = SQLiteStatement()
			sqLiteStatement.set(_sqLiteDatabase_Normal!!.compileStatement(sql))
			return sqLiteStatement
		}
		return null
	}

	val lastInsertRowId: Int
		get() {
			var row_id = 0
			rawQuery("SELECT last_insert_rowid()", null)?.let {
				it.moveToFirst()
				if (!it.isAfterLast) {
					row_id = it.getInt(0)
				}
				it.close()
			}

			return row_id
		}

	val changes: Int
		get() {
			var changes = 0
			rawQuery("SELECT changes()", null)?.let {
				it.moveToFirst()
				if (!it.isAfterLast) {
					changes = it.getInt(0)
				}
				it.close()
			}
			return changes
		}
}























