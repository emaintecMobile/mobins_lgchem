package com.emaintec.lib.ctrl

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.graphics.drawable.DrawableCompat
import com.emaintec.lib.R

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class EditTextSearch :  AppCompatEditText, TextWatcher, View.OnTouchListener, View.OnFocusChangeListener {
	private var _sercheDrawable: Drawable? = null
	private var _clearDrawable: Drawable? = null
	private var _onFocusChangeListener: OnFocusChangeListener? = null
	private var _onBtnClickListener: OnBtnClickListener? = null

	fun setOnBtnClickListener(listener: OnBtnClickListener) {
		_onBtnClickListener = listener
	}

	interface OnBtnClickListener {
		fun onClicked(view:EditTextSearch)
	}

	constructor(context: Context) : super(context) {}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

	init {
		val tempDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			resources.getDrawable( android.R.drawable.ic_menu_search, null)
		} else {
			resources.getDrawable( android.R.drawable.ic_menu_search)
		}
		_sercheDrawable = DrawableCompat.wrap(tempDrawable)
		DrawableCompat.setTintList(_sercheDrawable!!, hintTextColors)
		_sercheDrawable!!.setBounds(0, 0, _sercheDrawable!!.intrinsicWidth, _sercheDrawable!!.intrinsicHeight)
		val tempDrawable2 =	resources.getDrawable( R.drawable.ic_cancel_black, null)

		_clearDrawable = DrawableCompat.wrap(tempDrawable2)
		DrawableCompat.setTintList(_clearDrawable!!, hintTextColors)
		_clearDrawable!!.setBounds(0, 0, _clearDrawable!!.intrinsicWidth, _clearDrawable!!.intrinsicHeight)
		setIconVisible(false)
		super.setOnTouchListener(this)
		addTextChangedListener(this)
	}
	private fun setIconVisible(visibleClear: Boolean) {
		_sercheDrawable!!.setVisible(true, false)
		_clearDrawable!!.setVisible(visibleClear, false)
		setCompoundDrawables( _sercheDrawable, null, if (visibleClear) _clearDrawable else null, null)
	}

	override fun afterTextChanged(p0: Editable?) {	}

	override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {	}

	override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
		_clearDrawable?.let{
			setIconVisible(s.isNotEmpty())
		}
	}
	override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

		val x = motionEvent.x.toInt()
		if(motionEvent.action == MotionEvent.ACTION_DOWN) {
			if(_sercheDrawable!!.isVisible && x >= (width  - paddingRight - _clearDrawable!!.intrinsicWidth)) {
				// your action here
				error = null
				setText("")
				tag = ""
				return true
			}
			if(x <= ( paddingLeft + _sercheDrawable!!.intrinsicWidth)) {
				// your action here
				_onBtnClickListener?.let {
					it.onClicked(this)
				}

				return true
			}
		}
		return this.onTouchEvent(motionEvent)
	}

	override fun onFocusChange(view: View, hasFocus: Boolean) {
		if (hasFocus) {
			setIconVisible(text!!.isNotEmpty())
		} else {
			setIconVisible(false)
		}

		if (null != _onFocusChangeListener) {
			_onFocusChangeListener!!.onFocusChange(view, hasFocus)
		} else if (!hasFocus) {
			val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			imm.hideSoftInputFromWindow(view.windowToken, 0)
		}
	}
	override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {

		if (KeyEvent.KEYCODE_BACK == keyCode) {
			clearFocus()
			return super.dispatchKeyEvent(event)
		}

		return super.onKeyPreIme(keyCode, event)
	}
}
