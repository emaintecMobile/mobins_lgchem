package com.emaintec.lib.device

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings

import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.emaintec.lib.base.Emaintec

object Device {

	enum class ActiveNetwork {
		NONE, WIFI, MOBILE
	}
	var user : String = ""
	val model: String
		get() = Build.MODEL
	var isFingerprint = true
	val osVersion: String
		get() = Build.VERSION.RELEASE

	val packageName: String
		get() {
			assert(Emaintec.application != null)

			return Emaintec.application!!.applicationContext.packageName
		}

	val versionName: String
		get() {
			assert(Emaintec.application != null)

			var versionName = "???"
			try {
				val packageInfo = Emaintec.application!!.applicationContext.packageManager.getPackageInfo(
					Emaintec.application!!.applicationContext.packageName,
					PackageManager.GET_META_DATA
				)
				versionName = packageInfo.versionName
			} catch (e: PackageManager.NameNotFoundException) {
				e.printStackTrace()
			}
			return versionName
		}

	val versionCode: Int
		get() {
			assert(Emaintec.application != null)

			var versionCode = 0
			try {
				val packageInfo = Emaintec.application!!.applicationContext.packageManager.getPackageInfo(
					Emaintec.application!!.applicationContext.packageName,
					PackageManager.GET_META_DATA
				)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
					versionCode = packageInfo.longVersionCode.toInt()
				} else {
					versionCode = packageInfo.versionCode
				}
			} catch (e: PackageManager.NameNotFoundException) {
				e.printStackTrace()
			}

			return versionCode
		}

	// TODO: Consider calling
	//    ActivityCompat#requestPermissions
	// here to request the missing permissions, and then overriding
	//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
	//                                          int[] grantResults)
	// to handle the case where the user grants the permission. See the documentation
	// for ActivityCompat#requestPermissions for more details.
	val phoneNumber: String?
		get() {
			assert(Emaintec.application != null)

			var phoneNumber: String? = null
			if (ActivityCompat.checkSelfPermission(
					Emaintec.application!!.applicationContext,
					Manifest.permission.READ_SMS
				) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(
					Emaintec.application!!.applicationContext,
					Manifest.permission.READ_PHONE_NUMBERS
				) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(
					Emaintec.application!!.applicationContext,
					Manifest.permission.READ_PHONE_STATE
				) != PackageManager.PERMISSION_GRANTED
			) {
			} else {
				val telephonyManager = Emaintec.application!!.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
				phoneNumber = telephonyManager.line1Number
			}
			return phoneNumber?.replace("-", "")?.replace("+82", "0")
		}
	val ANDROID_ID:String
		get() {
			return Settings.Secure.getString(Emaintec.application!!.contentResolver, Settings.Secure.ANDROID_ID)
		}
	val deviceDpi: Int
		get() {
			assert(Emaintec.application != null)

			val displayMetrics = DisplayMetrics()
			val windowManager = Emaintec.application!!.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
			windowManager.defaultDisplay.getMetrics(displayMetrics)

			return displayMetrics.densityDpi
		}
	val deviceWidth: Int
		get() {
			assert(Emaintec.application != null)

			val displayMetrics = DisplayMetrics()
			val windowManager = Emaintec.application!!.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
			windowManager.defaultDisplay.getMetrics(displayMetrics)

			return displayMetrics.widthPixels
		}
	val deviceHeight: Int
		get() {
			assert(Emaintec.application != null)

			val displayMetrics = DisplayMetrics()
			val windowManager = Emaintec.application!!.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
			windowManager.defaultDisplay.getMetrics(displayMetrics)

			return displayMetrics.heightPixels
		}
	val statusBarHeight: Int
		get() {
			assert(Emaintec.application != null)

			var pixelSize = 0
			val resourceId = Emaintec.application!!.applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android")
			if (resourceId > 0) {
				pixelSize = Emaintec.application!!.applicationContext.resources.getDimensionPixelSize(resourceId)
			}
			return pixelSize
		}

	fun getActiveNetwork(checkConnected: Boolean): ActiveNetwork {
		assert(Emaintec.application != null)
		var returnValue = ActiveNetwork.NONE
		val connectivityManager = Emaintec.application!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		if(connectivityManager.activeNetworkInfo ==null) return returnValue
		if (checkConnected && connectivityManager.activeNetworkInfo!!.isConnected) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				val network = connectivityManager.activeNetwork
				val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
				if (networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
					returnValue = ActiveNetwork.WIFI
				} else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
					returnValue = ActiveNetwork.MOBILE
				}
			} else {
				val networkInfo = connectivityManager.activeNetworkInfo
				if (networkInfo!!.type == ConnectivityManager.TYPE_WIFI) {
					returnValue = ActiveNetwork.WIFI
				} else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
					returnValue = ActiveNetwork.MOBILE
				}
			}
		}
		return returnValue
	}

	fun convertDpToPixel(dp: Float, context: Context): Float {
		return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
	}

	fun convertPixelsToDp(px: Float, context: Context): Float {
		return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
	}
	fun restartApp(context : Context) {
		val packageManager = context.packageManager
		val intent = packageManager.getLaunchIntentForPackage(context.packageName)
		val componentName = intent!!.getComponent()
		val mainIntent: Intent = Intent.makeRestartActivityTask(componentName)
		context.startActivity(mainIntent)
		System.exit(0)
	}
}
