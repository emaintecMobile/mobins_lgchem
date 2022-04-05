package com.emaintec.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.HypherText

class CustomLayout_TextView_HypherText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var hypherText: HypherText
        private set

    init {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_hyphertext, this, false)
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_HypherText, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_HypherText_leftTextView_Text)
            view.findViewById<HypherText>(R.id.hypherText).text = getString(R.styleable.CustomLayout_TextView_HypherText_rightHypherText_Text)
            hypherText = view.findViewById<HypherText>(R.id.hypherText)

            recycle()
        }
    }
}
