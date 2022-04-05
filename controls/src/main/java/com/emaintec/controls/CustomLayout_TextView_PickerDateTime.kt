package com.emaintec.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.PickerDate
import com.emaintec.lib.ctrl.PickerTime


class CustomLayout_TextView_PickerDateTime @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var pickerDate: PickerDate
        private set
    var pickerTime: PickerTime
        private set

    init {

        var view : View
        when (context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_PickerDateTime, defStyleAttr, 0).getString(
            R.styleable.CustomLayout_TextView_PickerDateTime_CustomStyle
        ))
        {
            "1" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdatetime1, this, false)
            "2" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdatetime2, this, false)
            "3" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdatetime3, this, false)
            else -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdatetime, this, false)
        }
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_PickerDateTime, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_PickerDateTime_leftTextView_Text)

            pickerDate = view.findViewById(R.id.pickerWorkDate)
            pickerTime = view.findViewById(R.id.pickerTime)

            recycle()
        }
    }
}
