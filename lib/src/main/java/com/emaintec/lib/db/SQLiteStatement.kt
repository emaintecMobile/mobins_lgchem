package com.emaintec.lib.db

class SQLiteStatement {

	private var _sqLiteStatement_Cipher: net.sqlcipher.database.SQLiteStatement? = null
	private var _sqLiteStatement_Normal: android.database.sqlite.SQLiteStatement? = null

	fun set(sqLiteStatement: android.database.sqlite.SQLiteStatement) {
		_sqLiteStatement_Normal = sqLiteStatement
	}

	fun set(sqLiteStatement: net.sqlcipher.database.SQLiteStatement) {
		_sqLiteStatement_Cipher = sqLiteStatement
	}

	fun close() {
		_sqLiteStatement_Cipher?.close()
		_sqLiteStatement_Normal?.close()
	}

	fun bindObject(index: Int, jobject: Any?) {
		if (null != _sqLiteStatement_Cipher) {
			if (null == jobject) {
				_sqLiteStatement_Cipher!!.bindNull(index)
			} else if (jobject is String) {
				_sqLiteStatement_Cipher!!.bindString(index, (jobject as String?)!!)
			} else if (jobject is Int) {
				_sqLiteStatement_Cipher!!.bindLong(index, jobject.toLong())
			} else if (jobject is Long) {
				_sqLiteStatement_Cipher!!.bindLong(index, jobject)
			} else if (jobject is Float) {
				_sqLiteStatement_Cipher!!.bindDouble(index, jobject as Double)
			} else if (jobject is Double) {
				_sqLiteStatement_Cipher!!.bindDouble(index, jobject)
			} else if (jobject is ByteArray) {
				_sqLiteStatement_Cipher!!.bindBlob(index, (jobject as ByteArray?)!!)
			}
		} else if (null != _sqLiteStatement_Normal) {
			if (null == jobject) {
				_sqLiteStatement_Normal!!.bindNull(index)
			} else if (jobject is String) {
				_sqLiteStatement_Normal!!.bindString(index, jobject as String?)
			} else if (jobject is Int) {
				_sqLiteStatement_Normal!!.bindLong(index, jobject.toLong())
			} else if (jobject is Long) {
				_sqLiteStatement_Normal!!.bindLong(index, jobject)
			} else if (jobject is Float) {
				_sqLiteStatement_Normal!!.bindDouble(index, jobject as Double)
			} else if (jobject is Double) {
				_sqLiteStatement_Normal!!.bindDouble(index, jobject)
			} else if (jobject is ByteArray) {
				_sqLiteStatement_Normal!!.bindBlob(index, jobject as ByteArray?)
			}
		}
	}

	fun execute() {
		_sqLiteStatement_Cipher?.execute()
		_sqLiteStatement_Normal?.execute()
	}

	fun executeInsert() {
		_sqLiteStatement_Cipher?.executeInsert()
		_sqLiteStatement_Normal?.executeInsert()
	}

	fun executeUpdateDelete() {
		_sqLiteStatement_Cipher?.executeUpdateDelete()
		_sqLiteStatement_Normal?.executeUpdateDelete()
	}
}
