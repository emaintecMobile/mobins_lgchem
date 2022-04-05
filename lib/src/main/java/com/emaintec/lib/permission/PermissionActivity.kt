package com.emaintec.lib.permission

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class PermissionActivity : AppCompatActivity() {
	internal var rationaleTitle: CharSequence? = null
	internal var rationale_message: CharSequence? = null
	internal var denyTitle: CharSequence? = null
	internal var denyMessage: CharSequence? = null
	internal var permissions: Array<String>? = null
	internal var packageName: String? = null
	internal var hasSettingButton: Boolean = false
	internal var settingButtonText: String? = null
	internal var deniedCloseButtonText: String? = null
	internal var rationaleConfirmText: String? = null
	internal var isShownRationaleDialog: Boolean = false
	internal var requestedOrientation: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		overridePendingTransition(0, 0)
		super.onCreate(savedInstanceState)
		window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
		setupFromSavedInstanceState(savedInstanceState)
		// check windows
		if (needWindowPermission()) {
			requestWindowPermission()
		} else {
			checkPermissions(false)
		}

		setRequestedOrientation(requestedOrientation)//ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
	}

	private fun setupFromSavedInstanceState(savedInstanceState: Bundle?) {
		if (savedInstanceState != null) {
			permissions = savedInstanceState.getStringArray(EXTRA_PERMISSIONS)
			rationaleTitle = savedInstanceState.getCharSequence(EXTRA_RATIONALE_TITLE)
			rationale_message = savedInstanceState.getCharSequence(EXTRA_RATIONALE_MESSAGE)
			denyTitle = savedInstanceState.getCharSequence(EXTRA_DENY_TITLE)
			denyMessage = savedInstanceState.getCharSequence(EXTRA_DENY_MESSAGE)
			packageName = savedInstanceState.getString(EXTRA_PACKAGE_NAME)

			hasSettingButton = savedInstanceState.getBoolean(EXTRA_SETTING_BUTTON, true)

			rationaleConfirmText = savedInstanceState.getString(EXTRA_RATIONALE_CONFIRM_TEXT)
			deniedCloseButtonText = savedInstanceState.getString(EXTRA_DENIED_DIALOG_CLOSE_TEXT)

			settingButtonText = savedInstanceState.getString(EXTRA_SETTING_BUTTON_TEXT)
			requestedOrientation = savedInstanceState.getInt(EXTRA_SCREEN_ORIENTATION)
		} else {
			val intent = intent
			permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS)
			rationaleTitle = intent.getCharSequenceExtra(EXTRA_RATIONALE_TITLE)
			rationale_message = intent.getCharSequenceExtra(EXTRA_RATIONALE_MESSAGE)
			denyTitle = intent.getCharSequenceExtra(EXTRA_DENY_TITLE)
			denyMessage = intent.getCharSequenceExtra(EXTRA_DENY_MESSAGE)
			packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
			hasSettingButton = intent.getBooleanExtra(EXTRA_SETTING_BUTTON, true)
			rationaleConfirmText = intent.getStringExtra(EXTRA_RATIONALE_CONFIRM_TEXT)
			deniedCloseButtonText = intent.getStringExtra(EXTRA_DENIED_DIALOG_CLOSE_TEXT)
			settingButtonText = intent.getStringExtra(EXTRA_SETTING_BUTTON_TEXT)
			requestedOrientation =
				intent.getIntExtra(EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
		}
	}

	private fun needWindowPermission(): Boolean {
		for (permission in permissions!!) {
			if (permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
				return !hasWindowPermission()
			}
		}
		return false
	}

	@TargetApi(Build.VERSION_CODES.M)
	private fun hasWindowPermission(): Boolean {
		return Settings.canDrawOverlays(applicationContext)
	}

	@TargetApi(Build.VERSION_CODES.M)
	private fun requestWindowPermission() {
		val uri = Uri.fromParts("package", packageName, null)
		val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)

		if (!TextUtils.isEmpty(rationale_message)) {
			AlertDialog.Builder(this/*, R.style.Theme_AppCompat_Light_Dialog_Alert*/)
				.setMessage(rationale_message)
				.setCancelable(false)
				.setNegativeButton(rationaleConfirmText) { dialogInterface, i ->
					startActivityForResult(
						intent,
						REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST
					)
				}
				.show()
			isShownRationaleDialog = true
		} else {
			startActivityForResult(intent, REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST)
		}
	}

	private fun checkPermissions(fromOnActivityResult: Boolean) {

		val needPermissions = ArrayList<String>()

		for (permission in permissions!!) {
			if (permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
				if (!hasWindowPermission()) {
					needPermissions.add(permission)
				}
			} else {
				if (PermissionBase.isDenied(this, permission)) {
					needPermissions.add(permission)
				}
			}
		}

		if (needPermissions.isEmpty()) {
			permissionResult(null)
		} else if (fromOnActivityResult) {                                                                                        // From Setting Activity
			permissionResult(needPermissions)
		} else if (needPermissions.size == 1 && needPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {        // window permission deny
			permissionResult(needPermissions)
		} else if (!isShownRationaleDialog && !TextUtils.isEmpty(rationale_message)) {                                    // Need Show Rationale
			showRationaleDialog(needPermissions)
		} else {                                                                                                                // Need Request Permissions
			requestPermissions(needPermissions)
		}
	}

	private fun permissionResult(deniedPermissions: List<String>?) {
		//		Log.v(Permission.TAG, "permissionResult(): " + deniedPermissions);
		if (permissionListenerStack != null) {
			val listener = permissionListenerStack!!.pop()

			if (null == deniedPermissions || deniedPermissions.isEmpty()) {
				listener.onPermissionGranted()
			} else {
				listener.onPermissionDenied(deniedPermissions)
			}
			if (permissionListenerStack!!.size == 0) {
				permissionListenerStack = null
			}
		}

		finish()
		overridePendingTransition(0, 0)
	}

	override fun finish() {
		super.finish()
		overridePendingTransition(0, 0)
	}

	private fun showRationaleDialog(needPermissions: List<String>) {

		AlertDialog.Builder(this/*, R.style.Theme_AppCompat_Light_Dialog_Alert*/)
			.setTitle(rationaleTitle)
			.setMessage(rationale_message)
			.setCancelable(false)
			.setNegativeButton(rationaleConfirmText) { dialogInterface, i -> requestPermissions(needPermissions) }
			.show()
		isShownRationaleDialog = true
	}

	fun requestPermissions(needPermissions: List<String>) {
		ActivityCompat.requestPermissions(this, needPermissions.toTypedArray(), REQ_CODE_PERMISSION_REQUEST)
	}

	public override fun onSaveInstanceState(outState: Bundle) {
		outState.putStringArray(EXTRA_PERMISSIONS, permissions)
		outState.putCharSequence(EXTRA_RATIONALE_TITLE, rationaleTitle)
		outState.putCharSequence(EXTRA_RATIONALE_MESSAGE, rationale_message)
		outState.putCharSequence(EXTRA_DENY_TITLE, denyTitle)
		outState.putCharSequence(EXTRA_DENY_MESSAGE, denyMessage)
		outState.putString(EXTRA_PACKAGE_NAME, packageName)
		outState.putBoolean(EXTRA_SETTING_BUTTON, hasSettingButton)
		outState.putString(EXTRA_DENIED_DIALOG_CLOSE_TEXT, deniedCloseButtonText)
		outState.putString(EXTRA_RATIONALE_CONFIRM_TEXT, rationaleConfirmText)
		outState.putString(EXTRA_SETTING_BUTTON_TEXT, settingButtonText)

		super.onSaveInstanceState(outState)
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

		val deniedPermissions = PermissionBase.getDeniedPermissions(this, *permissions)

		if (deniedPermissions.isEmpty()) {
			permissionResult(null)
		} else {
			showPermissionDenyDialog(deniedPermissions)
		}
	}

	fun showPermissionDenyDialog(deniedPermissions: List<String>) {

		if (TextUtils.isEmpty(denyMessage)) {
			// denyMessage 설정 안함
			permissionResult(deniedPermissions)
			return
		}

		val builder = AlertDialog.Builder(this/*, R.style.Theme_AppCompat_Light_Dialog_Alert*/)

		builder.setTitle(denyTitle)
			.setMessage(denyMessage)
			.setCancelable(false)
			.setNegativeButton(deniedCloseButtonText) { dialogInterface, i -> permissionResult(deniedPermissions) }

		if (hasSettingButton) {
			builder.setPositiveButton(settingButtonText) { dialog, which ->
				PermissionBase.startSettingActivityForResult(
					this@PermissionActivity
				)
			}
		}
		builder.show()
	}

	fun shouldShowRequestPermissionRationale(needPermissions: List<String>?): Boolean {

		if (needPermissions == null) {
			return false
		}

		for (permission in needPermissions) {
			if (!ActivityCompat.shouldShowRequestPermissionRationale(this@PermissionActivity, permission)) {
				return false
			}
		}

		return true
	}

	fun showWindowPermissionDenyDialog() {

		val builder = AlertDialog.Builder(this/*, R.style.Theme_AppCompat_Light_Dialog_Alert*/)
		builder.setMessage(denyMessage)
			.setCancelable(false)
			.setNegativeButton(deniedCloseButtonText) { dialogInterface, i -> checkPermissions(false) }

		if (hasSettingButton) {
			builder.setPositiveButton(settingButtonText) { dialog, which ->
				val uri = Uri.fromParts("package", packageName, null)
				val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
				startActivityForResult(intent, REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING)
			}
		}
		builder.show()
	}

	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			PermissionBase.REQ_CODE_REQUEST_SETTING -> checkPermissions(true)
			REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST            // 최초 ALERT WINDOW 요청에 대한 결과
			-> if (!hasWindowPermission() && !TextUtils.isEmpty(denyMessage)) {        // 권한이 거부되고 denyMessage 가 있는 경우
				showWindowPermissionDenyDialog()
			} else {                                                                // 권한있거나 또는 denyMessage가 없는 경우는 일반 permission 을 확인한다.
				checkPermissions(false)
			}
			REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING        //  ALERT WINDOW 권한 설정 실패후 재 요청에 대한 결과
			-> checkPermissions(false)
			else -> super.onActivityResult(requestCode, resultCode, data)
		}
	}

	companion object {

		val REQ_CODE_PERMISSION_REQUEST = 10

		val REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST = 30
		val REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING = 31

		val EXTRA_PERMISSIONS = "permissions"
		val EXTRA_RATIONALE_TITLE = "rationale_title"
		val EXTRA_RATIONALE_MESSAGE = "rationale_message"
		val EXTRA_DENY_TITLE = "deny_title"
		val EXTRA_DENY_MESSAGE = "deny_message"
		val EXTRA_PACKAGE_NAME = "package_name"
		val EXTRA_SETTING_BUTTON = "setting_button"
		val EXTRA_SETTING_BUTTON_TEXT = "setting_button_text"
		val EXTRA_RATIONALE_CONFIRM_TEXT = "rationale_confirm_text"
		val EXTRA_DENIED_DIALOG_CLOSE_TEXT = "denied_dialog_close_text"
		val EXTRA_SCREEN_ORIENTATION = "screen_orientation"

		private var permissionListenerStack: Deque<PermissionListener>? = null

		fun startActivity(context: Context, intent: Intent, listener: PermissionListener) {
			if (permissionListenerStack == null) {
				permissionListenerStack = ArrayDeque()
			}
			permissionListenerStack!!.push(listener)
			context.startActivity(intent)
		}
	}
}