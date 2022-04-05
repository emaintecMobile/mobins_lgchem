package com.emaintec.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView

class CustomLayout_TextView_Spinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var spinner: Spinner
        private set

    init {

        var view : View
        when (context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_Spinner, defStyleAttr, 0).getString(
            R.styleable.CustomLayout_TextView_Spinner_CustomStyle
        ))
        {
            "1" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_spinner1, this, false)
            "2" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_spinner2, this, false)
            "3" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_spinner3, this, false)
            else -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_spinner, this, false)
        }
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_Spinner, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_Spinner_leftTextView_Text)
            spinner = view.findViewById(R.id.spinner)
            recycle()
        }
    }
}
