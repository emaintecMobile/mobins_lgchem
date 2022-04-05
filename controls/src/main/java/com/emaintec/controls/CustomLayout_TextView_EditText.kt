package com.emaintec.controls

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.EditTextDialog

class
CustomLayout_TextView_EditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var editTextDialog: EditTextDialog
        private set

    init {
        var view :View
        when (context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_EditText, defStyleAttr, 0).getString(
            R.styleable.CustomLayout_TextView_EditText_CustomStyle
        ))
        {
            "1" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_edittext1, this, false)
            "2" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_edittext2, this, false)
            "3" -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_edittext3, this, false)
            else -> view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_edittext, this, false)
        }
        addView(view)
        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_EditText, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_EditText_leftTextView_Text)
            var editText = view.findViewById<EditTextDialog>(R.id.editTextDialog)
            editText.setText(getString(R.styleable.CustomLayout_TextView_EditText_rightEditText_Text))
            editText.imeOptions = getInt(R.styleable.CustomLayout_TextView_EditText_rightEditText_ImeOptions, EditorInfo.IME_ACTION_DONE)
            editText.inputType = getInt(R.styleable.CustomLayout_TextView_EditText_rightEditText_InputType, InputType.TYPE_CLASS_TEXT)
            editText.maxLines = getInteger(R.styleable.CustomLayout_TextView_EditText_rightEditText_MaxLines, 1)
            editText.setLines(editText.maxLines)
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(getInteger(R.styleable.CustomLayout_TextView_EditText_rightEditText_MaxLength, -1)))
            editText.privateImeOptions = getString(R.styleable.CustomLayout_TextView_EditText_rightEditText_PrivateImeOptions)
            editText.textAlignment = getInteger(R.styleable.CustomLayout_TextView_EditText_rightEditText_TextAlignment, View.TEXT_ALIGNMENT_INHERIT)
            editText.layoutParams.height = getDimensionPixelSize(R.styleable.CustomLayout_TextView_EditText_rightEditText_Height, ViewGroup.LayoutParams.WRAP_CONTENT)
            editTextDialog = editText

            recycle()
        }
    }
}
