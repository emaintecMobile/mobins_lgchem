package com.emaintec.lib.version

import android.text.Spanned

object Html {

	fun fromHtml(source: String): Spanned {
		return if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
			android.text.Html.fromHtml(source)
		} else android.text.Html.fromHtml(source, android.text.Html.FROM_HTML_MODE_LEGACY)
	}
}
