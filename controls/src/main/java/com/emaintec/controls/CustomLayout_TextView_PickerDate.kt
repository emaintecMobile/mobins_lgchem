package com.emaintec.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.PickerDate

class CustomLayout_TextView_PickerDate @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var pickerDate_Start: PickerDate
        private set
    var pickerDate_End: PickerDate
        private set

     override fun setEnabled(isEnabled: Boolean) {
        pickerDate_Start.isEnabled = isEnabled
        pickerDate_End.isEnabled = isEnabled
//         super.setEnabled(isEnabled)
    }
    init {

        var view : View = when (context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_PickerDate, defStyleAttr, 0).getString(
            R.styleable.CustomLayout_TextView_PickerDate_CustomStyle
        )) {
            "3" -> (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdate3, this, false)
            else -> (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdate, this, false)
        }

        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_PickerDate, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_PickerDate_leftTextView_Text)

            pickerDate_Start = view.findViewById(R.id.pickerDate_Start)
            pickerDate_End = view.findViewById(R.id.pickerDate_End)

            recycle()
        }

        object: PickerDate.OnDialogListener {
            override fun afterTextChanged(date: String) {
            }
        }.let {
            pickerDate_Start.setOnDialogListener(it)
            pickerDate_End.setOnDialogListener(it)
        }

    }
    fun getStartDateTime() :String {
        return "${pickerDate_End.text}"
    }

    fun getEndDateTime() :String {
        return "${pickerDate_Start.text}"
    }
}
