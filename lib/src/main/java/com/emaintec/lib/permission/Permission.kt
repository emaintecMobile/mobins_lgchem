package com.emaintec.lib.permission

/**
 * 박상권님의 블로그에서 발췌하여 제작했습니다.
 * http://gun0912.tistory.com
 * https://github.com/ParkSangGwon/TedPermission
 */

import android.content.Context
import io.reactivex.Single

object Permission {

	fun create(context: Context): Builder {
		return Builder(context)
	}

	class Builder internal constructor(context: Context) : PermissionBuilder<Builder>(context) {

		fun request(): Single<PermissionResult> {
			return Single.create { emitter ->
				val listener = object : PermissionListener {
					override fun onPermissionGranted() {
						emitter.onSuccess(PermissionResult(null))
					}

					override fun onPermissionDenied(listDeniedPermission: List<String>) {
						emitter.onSuccess(PermissionResult(listDeniedPermission))
					}
				}

				try {
					setPermissionListener(listener)
					checkPermissions()
				} catch (exception: Exception) {
					emitter.onError(exception)
				}
			}
		}
	}
}
