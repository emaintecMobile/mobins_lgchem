package com.emaintec.lib.ctrl

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface

import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText

class EditTextDialog : AppCompatEditText, View.OnTouchListener {

	private var _onDialogListener: OnDialogListener? = null
	private var _onTouchedListener: OnTouchedListener? = null
//	private var _onNumberPressedListener: EditText.OnNumberPressedListener? = null
	private var _tooltipText: String? = null
	private var _textWatcher: TextWatcher? = null

	interface OnTouchedListener {
		fun onTouched(var1: View)
	}

	interface OnDialogListener {
		fun afterTextChanged(editTextDialog: EditTextDialog)
	}

	fun setOnTouchedListener(listener: OnTouchedListener?) {
		_onTouchedListener = listener
	}

	fun setOnDialogListener(listener: OnDialogListener?) {
		_onDialogListener = listener
	}
	fun getOnDialogListener(): OnDialogListener? {
		return _onDialogListener
	}
//	fun setOnNumberPressedListener(listener: EditText.OnNumberPressedListener?) {
//		_onNumberPressedListener = listener
//	}

	fun setTextWatcher(textWatcher: TextWatcher) {
		_textWatcher = textWatcher

		addTextChangedListener(_textWatcher)
	}

	@SuppressLint("ClickableViewAccessibility")
	constructor(context: Context) : super(context) {
		this.isFocusable = false
		this.setOnTouchListener(this)
	}

	@SuppressLint("ClickableViewAccessibility")
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		this.isFocusable = false
		this.setOnTouchListener(this)
	}

	@SuppressLint("ClickableViewAccessibility")
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		this.isFocusable = false
		this.setOnTouchListener(this)
	}

	override fun setTooltipText(tooltipText: CharSequence?) {
		_tooltipText = tooltipText?.toString()
	}

	override fun getTooltipText(): CharSequence? {
		return _tooltipText
	}

	//	@RequiresApi(api = Build.VERSION_CODES.O)
	override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

		if (MotionEvent.ACTION_UP != motionEvent.action)
			return false

		if (!this.isClickable)
			return false

		_onTouchedListener?.onTouched(view)

		val builder = AlertDialog.Builder(context)

		val editText = EditText(builder.context)
		editText.inputType = this.inputType
		editText.maxLines = this.maxLines
		editText.setLines(this.maxLines)
		editText.imeOptions = this.imeOptions
		editText.privateImeOptions = this.privateImeOptions
		editText.hint = this.hint
		editText.text = this.text
		editText.filters = this.filters
		if (editText.text != null) {
			editText.setSelection(editText.text!!.length)
		}
//		if(_onNumberPressedListener != null)
//			editText.setOnNumberPressedListener(_onNumberPressedListener!!)

		if (null != _textWatcher)
			editText.addTextChangedListener(_textWatcher)

		if (1 == this.maxLines) {
			editText.gravity = Gravity.CENTER
			editText.textAlignment = View.TEXT_ALIGNMENT_CENTER
			editText.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
		} else {
			editText.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
		}

		val linearLayout = LinearLayout(builder.context)
		linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
		linearLayout.orientation = LinearLayout.HORIZONTAL
		linearLayout.gravity = Gravity.CENTER
		linearLayout.isFocusable = true
		linearLayout.isFocusableInTouchMode = true
		linearLayout.addView(editText)

		editText.requestFocus()

		builder.setMessage(this.tooltipText)
		builder.setView(linearLayout)
		builder.setPositiveButton("입력") { dialog, which ->
			this@EditTextDialog.setText(editText.text!!.toString())
			dialog.dismiss()
			this@EditTextDialog._onDialogListener?.afterTextChanged(this@EditTextDialog)
		}
		builder.setNegativeButton("취소") { dialog, which -> dialog.dismiss() }

		val alertDialog = builder.create()
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
		alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
		alertDialog.setCancelable(false)

		//완료버튼 클릭시 자동으로 입력 버튼을 클릭한다.
		editText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
			when (actionId) {
				EditorInfo.IME_ACTION_DONE ->
					//Toast.makeText(getApplicationContext(), "검색", Toast.LENGTH_LONG).show();
					alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
				else ->
					//Toast.makeText(getApplicationContext(), "기본", Toast.LENGTH_LONG).show();
					return@OnEditorActionListener false
			}
			true
		})

		alertDialog.show()

		return true
	}
}
