package com.emaintec.controls

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.EditTextDialog

class CustomLayout_TextView_EditText_ImageButton_EditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var editTextDialog_Top: EditTextDialog
        private set
    var editTextDialog_Bottom: EditTextDialog
        private set
    var imageButton: ImageButton
        private set

    init {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_edittext_imagebutton_edittext, this, false)
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText, defStyleAttr, 0).apply {
            val textView1 = view.findViewById<TextView>(R.id.textView)
            val editTextDialog1 = view.findViewById<EditTextDialog>(R.id.editTextDialog_Top)
            val editTextDialog2 = view.findViewById<EditTextDialog>(R.id.editTextDialog_Bottom)
            val imageButton1 = view.findViewById<ImageButton>(R.id.imageButton)
            textView1.text = getString(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_leftTextView_Text)

            editTextDialog1.setText(getString(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightEditText_Text))
            editTextDialog1.imeOptions = getInt(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightEditText_ImeOptions, EditorInfo.IME_ACTION_DONE)
            editTextDialog1.inputType = getInt(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightEditText_InputType, InputType.TYPE_CLASS_TEXT)
            editTextDialog1.maxLines = getInteger(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightEditText_MaxLines, 1)
            editTextDialog1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(getInteger(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightEditText_MaxLength, -1)))
            editTextDialog1.privateImeOptions = getString(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightEditText_PrivateImeOptions)
            editTextDialog1.textAlignment = getInteger(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightEditText_TextAlignment, View.TEXT_ALIGNMENT_INHERIT)
            editTextDialog1.layoutParams.height = getDimensionPixelSize(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightEditText_Height, ViewGroup.LayoutParams.WRAP_CONTENT)
            editTextDialog_Top = editTextDialog1

            imageButton1.setImageResource(getResourceId(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_rightImageButton_Resource, R.drawable.ic_phone_black))
            imageButton = imageButton1

            editTextDialog2.setText(getString(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_bottomEditText_Text))
            editTextDialog2.imeOptions = getInt(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_bottomEditText_ImeOptions, EditorInfo.IME_ACTION_DONE)
            editTextDialog2.inputType = getInt(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_bottomEditText_InputType, InputType.TYPE_CLASS_TEXT)
            editTextDialog2.maxLines = getInteger(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_bottomEditText_MaxLines, 1)
            editTextDialog2.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(getInteger(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_bottomEditText_MaxLength, -1)))
            editTextDialog2.privateImeOptions = getString(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_bottomEditText_PrivateImeOptions)
            editTextDialog2.textAlignment = getInteger(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_bottomEditText_TextAlignment, View.TEXT_ALIGNMENT_INHERIT)
            editTextDialog2.layoutParams.height = getDimensionPixelSize(R.styleable.CustomLayout_TextView_EditText_ImageButton_EditText_bottomEditText_Height, ViewGroup.LayoutParams.WRAP_CONTENT)
            editTextDialog_Bottom = editTextDialog2

            recycle()
        }
    }
}
