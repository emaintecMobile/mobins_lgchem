package com.emaintec.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*

class CustomLayout_TextView_Spinner_Button @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var spinner: Spinner
        private set
    var button: Button
        private set

    init {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_spinner_button, this, false)
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_Spinner_Button, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_Spinner_Button_leftTextView_Text)

            spinner = view.findViewById(R.id.spinner)

            view.findViewById<Button>(R.id.button).text = getString(R.styleable.CustomLayout_TextView_Spinner_Button_rightButton_Text)
            button =  view.findViewById<Button>(R.id.button)

            recycle()
        }
    }
}
