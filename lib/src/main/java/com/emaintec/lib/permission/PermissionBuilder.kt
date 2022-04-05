package com.emaintec.lib.permission

import android.content.Context
import android.content.Intent
import android.os.Build

abstract class PermissionBuilder<T : PermissionBuilder<T>>(private val _context: Context) {
	private var _listener: PermissionListener? = null
	private var _permissions: Array<String>? = null
	private var _rationaleTitle: CharSequence? = null
	private var _rationaleMessage: CharSequence? = null
	private var _deniedTitle: CharSequence? = null
	private var _deniedMessage: CharSequence? = null
	private var _settingButtonText: CharSequence? = null
	private var _deniedCloseButtonText: CharSequence? = null
	private var _rationaleConfirmText: CharSequence? = null
	private var _hasSettingBtn = false
	private var _requestedOrientation: Int = 0

	protected fun checkPermissions() {
		if (null == _listener) {
			throw IllegalArgumentException("You must setPermissionListener() on Permission")
		} else if (null == _permissions || 0 == _permissions!!.size) {
			throw IllegalArgumentException("You must setPermissions() on Permission")
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			_listener!!.onPermissionGranted()
			return
		}

		val intent = Intent(_context, PermissionActivity::class.java)
		intent.putExtra(PermissionActivity.EXTRA_PERMISSIONS, _permissions)
		intent.putExtra(PermissionActivity.EXTRA_RATIONALE_TITLE, _rationaleTitle)
		intent.putExtra(PermissionActivity.EXTRA_RATIONALE_MESSAGE, _rationaleMessage)
		intent.putExtra(PermissionActivity.EXTRA_DENY_TITLE, _deniedTitle)
		intent.putExtra(PermissionActivity.EXTRA_DENY_MESSAGE, _deniedMessage)
		intent.putExtra(PermissionActivity.EXTRA_PACKAGE_NAME, _context.packageName)
		intent.putExtra(PermissionActivity.EXTRA_SETTING_BUTTON, _hasSettingBtn)
		intent.putExtra(PermissionActivity.EXTRA_DENIED_DIALOG_CLOSE_TEXT, _deniedCloseButtonText)
		intent.putExtra(PermissionActivity.EXTRA_RATIONALE_CONFIRM_TEXT, _rationaleConfirmText)
		intent.putExtra(PermissionActivity.EXTRA_SETTING_BUTTON_TEXT, _settingButtonText)
		intent.putExtra(PermissionActivity.EXTRA_SCREEN_ORIENTATION, _requestedOrientation)

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
		PermissionActivity.startActivity(_context, intent, _listener!!)
		PermissionBase.setFirstRequest(_context, _permissions!!)
	}

	protected fun setPermissionListener(listener: PermissionListener): T {
		_listener = listener
		return this as T
	}

	fun setPermissions(vararg permissions: String): T {
		_permissions = arrayOf(*permissions)
		return this as T
	}

	fun setRationaleTitle(rationaleTitle: CharSequence): T {
		_rationaleTitle = rationaleTitle
		return this as T
	}

	fun setRationaleMessage(rationaleMessage: CharSequence): T {
		_rationaleMessage = rationaleMessage
		return this as T
	}

	fun setDeniedTitle(deniedTitle: CharSequence): T {
		_deniedTitle = deniedTitle
		return this as T
	}

	fun setDeniedMessage(deniedMessage: CharSequence): T {
		_deniedMessage = deniedMessage
		return this as T
	}

	fun setGotoSettingButton(hasSettingBtn: Boolean): T {
		this._hasSettingBtn = hasSettingBtn
		return this as T
	}

	fun setGotoSettingButtonText(rationaleConfirmText: CharSequence): T {
		this._settingButtonText = rationaleConfirmText
		return this as T
	}

	fun setRationaleConfirmText(rationaleConfirmText: CharSequence): T {
		this._rationaleConfirmText = rationaleConfirmText
		return this as T
	}

	fun setDeniedCloseButtonText(deniedCloseButtonText: CharSequence): T {
		this._deniedCloseButtonText = deniedCloseButtonText
		return this as T
	}

	fun setScreenOrientation(requestedOrientation: Int): T {
		this._requestedOrientation = requestedOrientation
		return this as T
	}
}
