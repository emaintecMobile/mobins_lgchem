package com.emaintec.controls

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import com.emaintec.lib.ctrl.EditTextDialog

class CustomLayout_TextView_EditText_Backup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var editTextDialog: EditTextDialog
        private set

    init {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_layout_textview_edittext_backup, this, false)
        addView(view)

        context.obtainStyledAttributes(attrs, R.styleable.CustomLayout_TextView_EditText_Backup, defStyleAttr, 0).apply {

            view.findViewById<TextView>(R.id.textView).text = getString(R.styleable.CustomLayout_TextView_EditText_Backup_leftTextView_Text)

            view.findViewById<EditTextDialog>(R.id.editTextDialog).setText(getString(R.styleable.CustomLayout_TextView_EditText_Backup_rightEditText_Text))
            view.findViewById<EditTextDialog>(R.id.editTextDialog).imeOptions = getInt(R.styleable.CustomLayout_TextView_EditText_Backup_rightEditText_ImeOptions, EditorInfo.IME_ACTION_DONE)
            view.findViewById<EditTextDialog>(R.id.editTextDialog).inputType = getInt(R.styleable.CustomLayout_TextView_EditText_Backup_rightEditText_InputType, InputType.TYPE_CLASS_TEXT)
            view.findViewById<EditTextDialog>(R.id.editTextDialog).maxLines = getInteger(R.styleable.CustomLayout_TextView_EditText_Backup_rightEditText_MaxLines, 1)
            view.findViewById<EditTextDialog>(R.id.editTextDialog).filters = arrayOf<InputFilter>(InputFilter.LengthFilter(getInteger(R.styleable.CustomLayout_TextView_EditText_Backup_rightEditText_MaxLength, -1)))
            view.findViewById<EditTextDialog>(R.id.editTextDialog).privateImeOptions = getString(R.styleable.CustomLayout_TextView_EditText_Backup_rightEditText_PrivateImeOptions)
            editTextDialog = view.findViewById<EditTextDialog>(R.id.editTextDialog)

            recycle()
        }
    }
}
