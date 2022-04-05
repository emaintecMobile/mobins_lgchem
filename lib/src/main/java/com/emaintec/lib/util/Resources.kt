package com.emaintec.lib.util

import android.content.res.AssetManager
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics

import com.emaintec.lib.base.Emaintec

import java.util.Locale

class Resources(private val _defaultLocale: Locale, private val _targetLocale: Locale) {
	private val assetManager: AssetManager
	private val metrics: DisplayMetrics
	private val configuration: Configuration

	init {

		assert(Emaintec.application != null)

		val resources = Emaintec.application!!.resources
		assetManager = resources.assets
		metrics = resources.displayMetrics
		configuration = Configuration(resources.configuration)
	}

	fun getStringArray(resourceId: Int): Array<String> {

		assert(Emaintec.application != null)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			configuration.setLocale(_targetLocale)
			return Emaintec.application!!.createConfigurationContext(configuration).resources.getStringArray(resourceId)
		} else {
			configuration.locale = _targetLocale
			val resourceArray = ResourceManager(assetManager, metrics, configuration).getStringArray(resourceId)
			configuration.locale = _defaultLocale                            // reset
			ResourceManager(assetManager, metrics, configuration)            // reset
			return resourceArray
		}
	}

	fun getString(resourceId: Int): String {

		assert(Emaintec.application != null)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			configuration.setLocale(_targetLocale)
			return Emaintec.application!!.createConfigurationContext(configuration).resources.getString(resourceId)
		} else {
			configuration.locale = _targetLocale
			val resource = ResourceManager(assetManager, metrics, configuration).getString(resourceId)
			configuration.locale = _defaultLocale                            // reset
			ResourceManager(assetManager, metrics, configuration)            // reset
			return resource
		}
	}

	private inner class ResourceManager internal constructor(
		assets: AssetManager,
		metrics: DisplayMetrics,
		config: Configuration
	) : android.content.res.Resources(assets, metrics, config) {

		@Throws(NotFoundException::class)
		override fun getStringArray(id: Int): Array<String> {
			return super.getStringArray(id)
		}

		@Throws(NotFoundException::class)
		override fun getString(id: Int, vararg formatArgs: Any): String {
			return super.getString(id, *formatArgs)
		}

		@Throws(NotFoundException::class)
		override fun getString(id: Int): String {
			return super.getString(id)
		}
	}
}
