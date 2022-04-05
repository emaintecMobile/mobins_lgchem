package com.emaintec.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.HypherText

class CustomLayout_TextView_HypherText_TextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var hypherText: HypherText
        private set
    var textView: TextView
        private set

    init {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_hyphertext_textview, this, false)
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_HypherText_TextView, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_HypherText_TextView_leftTextView_Text)

            view.findViewById<HypherText>(R.id.hypherText).text = getString(R.styleable.CustomLayout_TextView_HypherText_TextView_rightHypherText_Text)
            hypherText = view.findViewById<HypherText>(R.id.hypherText)

            view.findViewById<TextView>(R.id.textView_Bottom).setTextColor(getColor(R.styleable.CustomLayout_TextView_HypherText_TextView_bottomTextView_TextColor, 0))
            view.findViewById<TextView>(R.id.textView_Bottom).text = getString(R.styleable.CustomLayout_TextView_HypherText_TextView_bottomTextView_Text)
            textView = view.findViewById<TextView>(R.id.textView_Bottom)

            recycle()

        }
    }
}
