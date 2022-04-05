package com.emaintec.lib.permission

class PermissionResult internal constructor(deniedPermissions: List<String>?) {

	val deniedPermissions: List<String>? = null

	val isGranted: Boolean
		get() = null == deniedPermissions || deniedPermissions.isEmpty()
}
