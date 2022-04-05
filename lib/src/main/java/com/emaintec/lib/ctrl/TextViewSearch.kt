package com.emaintec.lib.ctrl

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import com.emaintec.lib.R

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class TextViewSearch : AppCompatTextView , View.OnTouchListener{
	private var _sercheDrawable: Drawable? = null
	private var _clearDrawable: Drawable? = null
	private var _onBtnClickListener: OnBtnClickListener? = null

	fun setOnBtnClickListener(listener: OnBtnClickListener) {
		_onBtnClickListener = listener
	}

	interface OnBtnClickListener {
		fun onClicked(view:TextViewSearch)
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
//		addTextChangedListener(this)
	}
	private fun setIconVisible(visibleClear: Boolean) {
		_sercheDrawable!!.setVisible(true, false)
		_clearDrawable!!.setVisible(visibleClear, false)
		setCompoundDrawables( _sercheDrawable, null, if (visibleClear) _clearDrawable else null, null)
	}
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
				text = ""
				tag = ""
				return true
			}
			if(x <= ( paddingLeft + _sercheDrawable!!.intrinsicWidth)) {
				// your action here
				_onBtnClickListener?.let {
					it.onClicked(this)
				}
				return false
			}
		}
		return this.onTouchEvent(motionEvent)
	}

}
