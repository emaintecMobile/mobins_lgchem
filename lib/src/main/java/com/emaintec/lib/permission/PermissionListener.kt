package com.emaintec.lib.permission

interface PermissionListener {

	fun onPermissionGranted()

	fun onPermissionDenied(listDeniedPermission: List<String>)

}
