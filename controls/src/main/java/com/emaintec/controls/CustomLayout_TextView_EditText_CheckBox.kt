package com.emaintec.controls

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.EditTextDialog

class CustomLayout_TextView_EditText_CheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var editTextDialog: EditTextDialog
        private set
    var checkBox: CheckBox
        private set

    init {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_edittext_checkbox, this, false)
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_EditText_CheckBox, defStyleAttr, 0).apply {
            val textView1 = view.findViewById<TextView>(R.id.textView)
            val editTextDialog1 = view.findViewById<EditTextDialog>(R.id.editTextDialog)
            val checkBox1 = view.findViewById<CheckBox>(R.id.checkBox)
            textView1.text = getString(R.styleable.CustomLayout_TextView_EditText_CheckBox_leftTextView_Text)

            editTextDialog1.setText(getString(R.styleable.CustomLayout_TextView_EditText_CheckBox_rightEditText_Text))
            editTextDialog1.imeOptions = getInt(R.styleable.CustomLayout_TextView_EditText_CheckBox_rightEditText_ImeOptions, EditorInfo.IME_ACTION_DONE)
            editTextDialog1.inputType = getInt(R.styleable.CustomLayout_TextView_EditText_CheckBox_rightEditText_InputType, InputType.TYPE_CLASS_TEXT)
            editTextDialog1.maxLines = getInteger(R.styleable.CustomLayout_TextView_EditText_CheckBox_rightEditText_MaxLines, 1)
            editTextDialog1.setLines(editTextDialog1.maxLines)
            editTextDialog1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(getInteger(R.styleable.CustomLayout_TextView_EditText_CheckBox_rightEditText_MaxLength, -1)))
            editTextDialog1.privateImeOptions = getString(R.styleable.CustomLayout_TextView_EditText_CheckBox_rightEditText_PrivateImeOptions)
            editTextDialog1.textAlignment = getInteger(R.styleable.CustomLayout_TextView_EditText_CheckBox_rightEditText_TextAlignment, View.TEXT_ALIGNMENT_INHERIT)
            editTextDialog1.layoutParams.height = getDimensionPixelSize(R.styleable.CustomLayout_TextView_EditText_CheckBox_rightEditText_Height, ViewGroup.LayoutParams.WRAP_CONTENT)
            editTextDialog = editTextDialog1

            checkBox1.text = getString(R.styleable.CustomLayout_TextView_EditText_CheckBox_bottomCheckBox_Text)
            checkBox = checkBox1

            recycle()
        }
    }
}
