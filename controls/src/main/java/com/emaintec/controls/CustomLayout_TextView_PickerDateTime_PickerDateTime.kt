package com.emaintec.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.EditTextDialog
import com.emaintec.lib.ctrl.PickerDate
import com.emaintec.lib.ctrl.PickerTime
import java.text.ParseException
import java.text.SimpleDateFormat

class CustomLayout_TextView_PickerDateTime_PickerDateTime @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var pickerDate_Start: PickerDate
        private set
    var pickerTime_Start: PickerTime
        private set
    var pickerDate_End: PickerDate
        private set
    var pickerTime_End: PickerTime
        private set
    var editTextDialog: EditTextDialog
        private set

     override fun setEnabled(isEnabled: Boolean) {
        pickerDate_Start.isEnabled = isEnabled
        pickerTime_Start.isEnabled = isEnabled
        pickerTime_End.isEnabled = isEnabled
        pickerDate_End.isEnabled = isEnabled
        editTextDialog.isEnabled = isEnabled
//         super.setEnabled(isEnabled)
    }
    init {

        var view : View
        when (context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_PickerDateTime_PickerDateTime, defStyleAttr, 0).getString(
            R.styleable.CustomLayout_TextView_PickerDateTime_PickerDateTime_CustomStyle
        ))
        {
            "1" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdatetime_pickerdatetime1, this, false)
            "2" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdatetime_pickerdatetime2, this, false)
            "3" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdatetime_pickerdatetime3, this, false)
            else -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_pickerdatetime_pickerdatetime, this, false)
        }

        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_PickerDateTime_PickerDateTime, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_PickerDateTime_PickerDateTime_leftTextView_Text)

            pickerDate_Start = view.findViewById(R.id.pickerDate_Start)
            pickerTime_Start = view.findViewById(R.id.pickerTime_Start)

            pickerDate_End = view.findViewById(R.id.pickerDate_End)
            pickerTime_End = view.findViewById(R.id.pickerTime_End)

            editTextDialog = view.findViewById(R.id.editTextDialog)

            recycle()
        }

        object: PickerDate.OnDialogListener {
            override fun afterTextChanged(date: String) {
                editTextDialog.setText(calcWorkingTime())
            }
        }.let {
            pickerDate_Start.setOnDialogListener(it)
            pickerDate_End.setOnDialogListener(it)
        }

        object: PickerTime.OnDialogListener {
            override fun afterTextChanged(date: String) {
                editTextDialog.setText(calcWorkingTime())

            }
        }.let {
            pickerTime_Start.setOnDialogListener(it)
            pickerTime_End.setOnDialogListener(it)
        }
    }
    fun getStartDateTime() :String {
        return "${pickerDate_End.text} ${pickerTime_End.text}"
    }

    fun getEndDateTime() :String {
        return "${pickerDate_Start.text} ${pickerTime_Start.text}"
    }
    private fun calcWorkingTime(): String {
        if (pickerDate_Start.text.isNullOrBlank() || pickerTime_Start.text.isNullOrBlank() || pickerDate_End.text.isNullOrBlank() || pickerTime_End.text.isNullOrBlank()) {
            return "0"
        }

        var hour = 0.0
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val diff = (dateFormat.parse("${pickerDate_End.text} ${pickerTime_End.text}").time - dateFormat.parse("${pickerDate_Start.text} ${pickerTime_Start.text}").time).toDouble()
            hour = diff / (1000 * 60 * 60)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Math.ceil(hour * 100).div(100).toString()
    }
}
