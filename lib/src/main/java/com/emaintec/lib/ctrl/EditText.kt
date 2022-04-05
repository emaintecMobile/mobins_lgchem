package com.emaintec.lib.ctrl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.graphics.drawable.DrawableCompat
import com.emaintec.lib.R

class EditText : AppCompatEditText, View.OnTouchListener, View.OnFocusChangeListener {

	private var _clearDrawable: Drawable? = null
	private var _onFocusChangeListener: OnFocusChangeListener? = null
	private var _onTouchListener: OnTouchListener? = null
	private var _beforeText = ""
//	private var _onNumberPressedListener: OnNumberPressedListener? = null

	constructor(context: Context) : super(context) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		init()
	}

	override fun setOnFocusChangeListener(onFocusChangeListener: OnFocusChangeListener?) {
		_onFocusChangeListener = onFocusChangeListener
	}

	override fun setOnTouchListener(onTouchListener: OnTouchListener) {
		_onTouchListener = onTouchListener
	}

	@SuppressLint("RestrictedApi", "ClickableViewAccessibility")
	private fun init() {
		val tempDrawable = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_cancel_black)
		_clearDrawable = DrawableCompat.wrap(tempDrawable)
		DrawableCompat.setTintList(_clearDrawable!!, hintTextColors)
		_clearDrawable!!.setBounds(0, 0, _clearDrawable!!.intrinsicWidth, _clearDrawable!!.intrinsicHeight)
		setClearIconVisible(false)
		super.setOnTouchListener(this)
		super.setOnFocusChangeListener(this)
//		addTextChangedListener(this)
		//포커스를 잃으면 키보드를 내린다.(키보드에 NEXT BUTTON 눌려도 다음 에딕박스가서 키보드가 사라져 주석처리(결과 입력창)
//		setOnEditorActionListener { v, _, _ ->
//			v.clearFocus()
//			false
//		}
	}

	private fun setClearIconVisible(visible: Boolean) {
		_clearDrawable!!.setVisible(visible, false)
		setCompoundDrawables(null, null, if (visible) _clearDrawable else null, null)
	}

	override fun onFocusChange(view: View, hasFocus: Boolean) {
		if (hasFocus) {
			setClearIconVisible(text!!.isNotEmpty())
		} else {
			setClearIconVisible(false)
		}

		if (null != _onFocusChangeListener) {
			_onFocusChangeListener!!.onFocusChange(view, hasFocus)
		} else if (!hasFocus) {
			//포커스를 잃으면 키보드를 내린다.(키보드에 NEXT BUTTON 눌려도 다음 에딕박스가서 키보드가 사라져 주석처리(결과 입력창)
//			val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//			imm.hideSoftInputFromWindow(view.windowToken, 0)
		}
	}

	override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

		val x = motionEvent.x.toInt()
		if (_clearDrawable!!.isVisible && x > width - paddingRight - _clearDrawable!!.intrinsicWidth) {
			if (motionEvent.action == MotionEvent.ACTION_UP) {
				error = null
				text = null
			}

			return true
		}

		return if (null != _onTouchListener) {
			_onTouchListener!!.onTouch(view, motionEvent)
		} else {
			false
		}
	}

	override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
		if (isFocused) {
			setClearIconVisible(s.isNotEmpty())
		}
	}

//	override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//	override fun afterTextChanged(s: Editable) {
//
//		if (null != _onNumberPressedListener) {
//			val afterText = s.toString()
//
//			if (_beforeText.length < afterText.length) {
//				val ch = afterText[afterText.length - 1]
//
//				if (ch in '0'..'9') {
//					_onNumberPressedListener!!.onPressed(ch)
//				}
//			}
//
//			_beforeText = afterText
//		}
//	}

//	override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
//
//		if (KeyEvent.KEYCODE_BACK == keyCode) {
//			clearFocus()
//			return true//super.dispatchKeyEvent(event)
//		}
//
//		return super.onKeyPreIme(keyCode, event)
//	}

//	fun setOnNumberPressedListener(listener: OnNumberPressedListener) {
//		_onNumberPressedListener = listener
//	}

//	interface OnNumberPressedListener {
//		fun onPressed(number: Char)
//	}
}
