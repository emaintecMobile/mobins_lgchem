package com.emaintec.lib.ctrl

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class HypherText : AppCompatTextView {

	constructor(context: Context) : super(context) {}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

	fun setHypherText(text: CharSequence, l: OnClickListener? = null) {
		val spannableString = SpannableString(text)
		spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
		super.setText(spannableString)
		super.setOnClickListener(l)
	}
}
