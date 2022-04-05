package com.emaintec.lib.db

import android.content.Context

import com.emaintec.lib.base.Emaintec

import java.util.HashMap

class DBSwitcher private constructor() {

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private val _mapDBHelper: HashMap<String, IDBHelper>

	interface DBListener {
		fun onCreate(sqLiteDatabase: SQLiteDatabase)
		fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int)
		fun onMessage(message: String)
	}

	fun openDatabase(name: String, version: Int, password: String?, listener: DBListener?) {
		assert(Emaintec.application != null)

		if (!_mapDBHelper.containsKey(name)) {
			if (null == password) {
				_mapDBHelper[name] = DBDefaultHelper(Emaintec.application!!.applicationContext, name, null, version, listener)
			} else {
				_mapDBHelper[name] = DBCipherHelper(Emaintec.application!!.applicationContext, name, null, version, password, listener)
			}
		}
	}

	//	public boolean openDatabase(@NonNull Context context, @NonNull String name, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version, @Nullable DBListener listener) {
	//		if (!_mapDBHelper.containsKey(name)) {
	//			_mapDBHelper.put(name, new DBDefaultHelper(context, name, factory, version, listener));
	//			return true;
	//		}
	//		return false;
	//	}
	//
	//	public boolean openDatabase(@NonNull Context context, @NonNull String name, @Nullable net.sqlcipher.database.SQLiteDatabase.CursorFactory factory, int version, @NonNull String password, @Nullable DBListener listener) {
	//		if (!_mapDBHelper.containsKey(name)) {
	//			_mapDBHelper.put(name, new DBCipherHelper(context, name, factory, version, password, listener));
	//			return true;
	//		}
	//		return false;
	//	}

	fun closeDatabase(name: String): Boolean {
		val idbHelper = _mapDBHelper.remove(name)
		if (null != idbHelper) {
			idbHelper.closeDB()
			return true
		}
		return false
	}

	fun setDBListener(name: String, listener: DBListener?): Boolean {
		val idbHelper = _mapDBHelper[name]
		if (null != idbHelper) {
			idbHelper.setDBListener(listener)
			return true
		}
		return false
	}

	fun sendMessage(name: String?, message: String): Boolean {
		if (name.isNullOrEmpty()) {
			for (idbHelper in _mapDBHelper.values) {
				idbHelper.sendMessage(message)
			}
			return !_mapDBHelper.isEmpty()
		} else {
			val idbHelper = _mapDBHelper[name]
			if (null != idbHelper) {
				idbHelper.sendMessage(message)
				return true
			}
		}
		return false
	}

	fun getReadableDatabase(name: String): SQLiteDatabase? {
		val idbHelper = _mapDBHelper[name]
		return idbHelper?.readableDB
	}

	fun getWritableDatabase(name: String): SQLiteDatabase? {
		val idbHelper = _mapDBHelper[name]
		return idbHelper?.writableDB
	}

	private object SingletonHolder {
		internal val INSTANCE = DBSwitcher()
	}

	init {
		_mapDBHelper = HashMap()
	}

	private interface IDBHelper {
		val readableDB: SQLiteDatabase
		val writableDB: SQLiteDatabase
		fun closeDB()
		fun setDBListener(listener: DBListener?)
		fun sendMessage(message: String)
	}

	private inner class DBDefaultHelper internal constructor(
		context: Context,
		name: String,
		factory: android.database.sqlite.SQLiteDatabase.CursorFactory?,
		version: Int,
		private var _listener: DBListener?
	) : android.database.sqlite.SQLiteOpenHelper(context, name, factory, version), IDBHelper {

		private val _sqLiteDatabase: SQLiteDatabase

		override val readableDB: SQLiteDatabase
			get() {
				_sqLiteDatabase.set(readableDatabase)
				return _sqLiteDatabase
			}

		override val writableDB: SQLiteDatabase
			get() {
				_sqLiteDatabase.set(writableDatabase)
				return _sqLiteDatabase
			}

		init {
			_sqLiteDatabase = SQLiteDatabase()
		}

		override fun onCreate(db: android.database.sqlite.SQLiteDatabase) {
			if (null != _listener) {
				_sqLiteDatabase.set(db)
				_listener!!.onCreate(_sqLiteDatabase)
				_sqLiteDatabase.reset()
			}
		}

		override fun onUpgrade(db: android.database.sqlite.SQLiteDatabase, oldVersion: Int, newVersion: Int) {
			if (null != _listener) {
				_sqLiteDatabase.set(db)
				_listener!!.onUpgrade(_sqLiteDatabase, oldVersion, newVersion)
				_sqLiteDatabase.reset()
			}
		}

		override fun closeDB() {
			_sqLiteDatabase.close()
			close()
		}

		override fun setDBListener(listener: DBListener?) {
			_listener = listener
		}

		override fun sendMessage(message: String) {
			_listener?.onMessage(message)
		}
	}

	private inner class DBCipherHelper internal constructor(
		context: Context,
		name: String,
		factory: net.sqlcipher.database.SQLiteDatabase.CursorFactory?,
		version: Int,
		private val _password: String?,
		private var _listener: DBListener?
	) : net.sqlcipher.database.SQLiteOpenHelper(context, name, factory, version), IDBHelper {

		private val _sqLiteDatabase: SQLiteDatabase

		override val readableDB: SQLiteDatabase
			get() {
				_sqLiteDatabase.set(getReadableDatabase(_password))
				return _sqLiteDatabase
			}

		override val writableDB: SQLiteDatabase
			get() {
				_sqLiteDatabase.set(getWritableDatabase(_password))
				return _sqLiteDatabase
			}

		init {
			_sqLiteDatabase = SQLiteDatabase()
			net.sqlcipher.database.SQLiteDatabase.loadLibs(context)                // loadLibs 를 여러번 호출할 경우 오류가 있는지 확인해볼 것
		}

		override fun onCreate(db: net.sqlcipher.database.SQLiteDatabase) {
			if (null != _listener) {
				_sqLiteDatabase.set(db)
				_listener!!.onCreate(_sqLiteDatabase)
				_sqLiteDatabase.reset()
			}
		}

		override fun onUpgrade(db: net.sqlcipher.database.SQLiteDatabase, oldVersion: Int, newVersion: Int) {
			if (null != _listener) {
				_sqLiteDatabase.set(db)
				_listener!!.onUpgrade(_sqLiteDatabase, oldVersion, newVersion)
				_sqLiteDatabase.reset()
			}
		}

		override fun closeDB() {
			_sqLiteDatabase.close()
			close()
		}

		override fun setDBListener(listener: DBListener?) {
			_listener = listener
		}

		override fun sendMessage(message: String) {
			_listener?.onMessage(message)
		}
	}

	companion object {
		val instance: DBSwitcher
			 get() = SingletonHolder.INSTANCE

	}
}
