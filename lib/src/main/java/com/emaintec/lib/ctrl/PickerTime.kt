package com.emaintec.lib.ctrl

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import java.text.SimpleDateFormat
import java.util.*

class PickerTime : AppCompatTextView,  View.OnClickListener {

	private val defaultFormat = "HH:mm"
	private var _onDialogListener: OnDialogListener? = null
	var hour: Int = 0
	var minute: Int = 0
	interface OnDialogListener {
		fun afterTextChanged(date: String)
	}
	fun setOnDialogListener(listener: PickerTime.OnDialogListener?) {
		_onDialogListener = listener
	}
	override fun onClick(v: View?) {

		if(text.isNullOrEmpty()) {
			text = setCurrentTime()
		}

		TimePickerDialog(context, object: TimePickerDialog.OnTimeSetListener {
			override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
				this@PickerTime.hour = hourOfDay
				this@PickerTime.minute = minute
				val calendar = Calendar.getInstance()
				calendar.set(1900, 1, 1, this@PickerTime.hour, this@PickerTime.minute)
				text = SimpleDateFormat(defaultFormat).format(calendar.time)
				this@PickerTime._onDialogListener?.afterTextChanged(text.toString())
			}
		}, this@PickerTime.hour, this@PickerTime.minute, false).show()
	}

	private var _calrendarDrawable: Drawable? = null
	private var _onClickListener: View.OnClickListener? = null

    constructor(context: Context) : super(context) {
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
	}

	override fun setOnClickListener(onClickListener: View.OnClickListener?) {
		_onClickListener = onClickListener
	}

	init {
//		Drawable tempDrawable = AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_cancel_black);
		val tempDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			resources.getDrawable( android.R.drawable.ic_menu_recent_history, null)
		} else {
			resources.getDrawable( android.R.drawable.ic_menu_recent_history)
		}

		_calrendarDrawable = DrawableCompat.wrap(tempDrawable)
		DrawableCompat.setTintList(_calrendarDrawable!!, hintTextColors)
		_calrendarDrawable!!.setBounds(0, 0, _calrendarDrawable!!.intrinsicWidth, _calrendarDrawable!!.intrinsicHeight)
		setCalrendarconVisible(true)
		super.setOnClickListener(this)

		text = setCurrentTime()
	}

	private fun setCalrendarconVisible(visible: Boolean) {
		_calrendarDrawable!!.setVisible(visible, false)
		//setCompoundDrawables(null, null, if (visible) _calrendarDrawable else null, null)
		setCompoundDrawables(if (visible) _calrendarDrawable else null, null, null, null)
	}

	private fun setCurrentTime(): String {
		val calendar = Calendar.getInstance()
		hour = calendar.get(Calendar.HOUR_OF_DAY)
		minute = calendar.get(Calendar.MINUTE)
		calendar.set(1900, 1, 1, hour, minute)

		return SimpleDateFormat(defaultFormat).format(calendar.time)
	}

	fun setTime(time: String) {

		if (time.length != 4) {
			setCurrentTime()
		} else {
			hour = time.substring(0, 2).toInt()
			minute = time.substring(2).toInt()
		}

		val calendar = Calendar.getInstance()
		calendar.set(1900, 1, 1, hour, minute)

		text = SimpleDateFormat(defaultFormat).format(calendar.time)
	}
	
	fun getTime(): String {
		return text.toString().replace(":", "")
	}
}
