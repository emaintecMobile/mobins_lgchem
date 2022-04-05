package com.emaintec.controls

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.controls.R.styleable.CustomLayout_TextView_TextView_CustomStyle
import com.emaintec.controls.R.styleable.CustomLayout_TextView_TextView_leftTextView_textStyle


class CustomLayout_TextView_TextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var textView: TextView
        private set


    init {
        var view :View
        when (context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_TextView, defStyleAttr, 0).getString(CustomLayout_TextView_TextView_CustomStyle))
        {
            "1" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_textview1, this, false)
            "2" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_textview2, this, false)
            "3" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_textview3, this, false)
            else -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_textview, this, false)
        }
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_TextView, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).tag = getString(R.styleable.CustomLayout_TextView_TextView_leftTextView_Tag)
            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_TextView_leftTextView_Text)
            if("bold".equals(getString(CustomLayout_TextView_TextView_leftTextView_textStyle)))
                {
                    view.findViewById<TextView>(R.id.textView).setTypeface(null, Typeface.BOLD)
                }

            view.findViewById<TextView>(R.id.textView_Right).setTextColor(getColor(R.styleable.CustomLayout_TextView_TextView_rightTextView_TextColor, 0))
            view.findViewById<TextView>(R.id.textView_Right).text = getString(R.styleable.CustomLayout_TextView_TextView_rightTextView_Text)
            view.findViewById<TextView>(R.id.textView_Right).setLines( getInt(R.styleable.CustomLayout_TextView_TextView_rightTextView_Lines,1))
            textView = view.findViewById<TextView>(R.id.textView_Right)

            recycle()
        }
    }
}
