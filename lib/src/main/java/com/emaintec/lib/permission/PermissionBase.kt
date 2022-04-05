package com.emaintec.lib.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


import com.emaintec.lib.base.Emaintec

import java.util.ArrayList

object PermissionBase {
	val REQ_CODE_REQUEST_SETTING = 2000
	private val PREFS_NAME_PERMISSION = "PREFS_NAME_PERMISSION"
	private val PREFS_IS_FIRST_REQUEST = "IS_FIRST_REQUEST"

	fun isGranted(context: Context, vararg permissions: String): Boolean {
		assert(Emaintec.application != null)
		for (permission in permissions) {
			if (isDenied(Emaintec.application!!.applicationContext, permission)) {
				return false
			}
		}
		return true
	}

	fun isDenied(context: Context, permission: String): Boolean {
		return !isGranted(context, permission)
	}

	private fun isGranted(context: Context, permission: String): Boolean {
		return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
	}

	fun getDeniedPermissions(context: Context, vararg permissions: String): List<String> {
		val deniedPermissions = ArrayList<String>()
		for (permission in permissions) {
			if (isDenied(context, permission)) {
				deniedPermissions.add(permission)
			}
		}
		return deniedPermissions
	}

	fun canRequestPermission(activity: Activity, vararg permissions: String): Boolean {

		if (isFirstRequest(activity, arrayOf(*permissions))) {
			return true
		}

		for (permission in permissions) {
			val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
			if (isDenied(activity, permission) && !showRationale) {
				return false
			}
		}
		return true
	}

	private fun isFirstRequest(context: Context, permissions: Array<String>): Boolean {
		for (permission in permissions) {
			if (!isFirstRequest(context, permission)) {
				return false
			}
		}
		return true
	}

	private fun isFirstRequest(context: Context, permission: String): Boolean {
		return getSharedPreferences(context).getBoolean(getPrefsNamePermission(permission), true)
	}

	private fun getPrefsNamePermission(permission: String): String {
		return PREFS_IS_FIRST_REQUEST + "_" + permission
	}

	private fun getSharedPreferences(context: Context): SharedPreferences {
		return context.getSharedPreferences(PREFS_NAME_PERMISSION, Context.MODE_PRIVATE)
	}

	@JvmOverloads
	fun startSettingActivityForResult(activity: Activity, requestCode: Int = REQ_CODE_REQUEST_SETTING) {
		activity.startActivityForResult(getSettingIntent(activity), requestCode)
	}

	fun getSettingIntent(context: Context): Intent {
		return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:$context.packageName"))
	}

	@JvmOverloads
	fun startSettingActivityForResult(fragment: Fragment, requestCode: Int = REQ_CODE_REQUEST_SETTING) {
		fragment.startActivityForResult(getSettingIntent(fragment.activity!!), requestCode)
	}

	internal fun setFirstRequest(context: Context, permissions: Array<String>) {
		for (permission in permissions) {
			setFirstRequest(context, permission)
		}
	}

	private fun setFirstRequest(context: Context, permission: String) {
		getSharedPreferences(context).edit().putBoolean(getPrefsNamePermission(permission), false).apply()
	}


}
