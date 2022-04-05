package com.emaintec.controls

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

class CustomLayout_TextView_TextView_Button @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var textView: TextView
        private set
    var textView_Name: TextView
        private set
    var imageButton: ImageButton
        private set

    init {
       val view  : View
        when (context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_TextView_Button, defStyleAttr, 0).getString(
            R.styleable.CustomLayout_TextView_TextView_Button_CustomStyle
        ))
        {
            "1" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_textview_button1, this, false)
            "2" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_textview_button2, this, false)
            "3" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_textview_button3, this, false)
            else -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_textview_button, this, false)
        }
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_TextView_Button, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_TextView_Button_leftTextView_Text)
            if("bold".equals(getString(R.styleable.CustomLayout_TextView_TextView_Button_leftTextView_textStyle)))
                {
                    view.findViewById<TextView>(R.id.textView).setTypeface(null, Typeface.BOLD)
                }

            view.findViewById<TextView>(R.id.textView_Right).setTextColor(getColor(R.styleable.CustomLayout_TextView_TextView_Button_rightTextView_TextColor, Color.BLACK))
            view.findViewById<TextView>(R.id.textView_Right).text = getString(R.styleable.CustomLayout_TextView_TextView_Button_rightTextView_Text)
            view.findViewById<TextView>(R.id.textView_Right).setLines( getInt(R.styleable.CustomLayout_TextView_TextView_Button_rightTextView_Lines,1))
            textView = view.findViewById<TextView>(R.id.textView_Right)

            view.findViewById<TextView>(R.id.textView_Name).setTextColor(getColor(R.styleable.CustomLayout_TextView_TextView_Button_rightTextView_TextColor, Color.BLACK))
            view.findViewById<TextView>(R.id.textView_Name).text = getString(R.styleable.CustomLayout_TextView_TextView_Button_rightTextView_Text2)
            view.findViewById<TextView>(R.id.textView_Name).setLines( getInt(R.styleable.CustomLayout_TextView_TextView_Button_rightTextView_Lines,1))
            textView_Name = view.findViewById<TextView>(R.id.textView_Name)

            view.findViewById<ImageButton>(R.id.imageButton).setImageResource(getResourceId(R.styleable.CustomLayout_TextView_TextView_Button_rightImageButton_Resource, R.drawable.ic_phone_black))
            imageButton =view.findViewById<ImageButton>(R.id.imageButton)
            recycle()
        }
    }
}
