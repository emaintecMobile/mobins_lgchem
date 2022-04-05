package com.emaintec.lib.ctrl

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import com.emaintec.lib.base.Emaintec
import java.text.SimpleDateFormat
import java.util.*

class PickerDate : AppCompatTextView,  View.OnClickListener {

	private val defaultFormat = "yyyy-MM-dd"
	private var _onDialogListener: OnDialogListener? = null
	var year: Int = 0
	var month: Int = 0
	var day: Int = 0
	interface OnDialogListener {
		fun afterTextChanged(date: String)
	}
	fun setOnDialogListener(listener: PickerDate.OnDialogListener?) {
		_onDialogListener = listener
	}

	override fun onClick(v: View?) {

		if(text.isNullOrEmpty()) {
			text = setCurrentTime()
		}

		val dialog = DatePickerDialog(context, object : DatePickerDialog.OnDateSetListener {
			override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, date: Int) {

				this@PickerDate.year = year
				this@PickerDate.month = month
				this@PickerDate.day = date

				val calendar = Calendar.getInstance()
				calendar.set(this@PickerDate.year, this@PickerDate.month, this@PickerDate.day)
				text = SimpleDateFormat(defaultFormat).format(calendar.time)
				this@PickerDate._onDialogListener?.afterTextChanged(text.toString())
			}
		}, this@PickerDate.year, this@PickerDate.month, this@PickerDate.day)
        if(maxDate != null) {
            dialog.datePicker.maxDate = maxDate!!.time    //입력한 날짜 이후로 클릭 안되게 옵션
        }
        if(minDate!=null) {
            dialog.datePicker.minDate = minDate!!.time    //입력한 날짜 이후로 클릭 안되게 옵션
        }
        dialog.show()
	}

	private var _calrendarDrawable: Drawable? = null
	private var _onClickListener: View.OnClickListener? = null
	

    var maxDate : Date? = null
    var minDate : Date? = null

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
			resources.getDrawable( android.R.drawable.ic_menu_my_calendar, null)
		} else {
			resources.getDrawable( android.R.drawable.ic_menu_my_calendar)
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
		year = calendar.get(Calendar.YEAR)
		month = calendar.get(Calendar.MONTH)
		day = calendar.get(Calendar.DAY_OF_MONTH)
		calendar.set(year, month, day)

		return SimpleDateFormat(defaultFormat).format(calendar.time)
	}

	fun setDate(date: String) {

		if (date.length != 8) {
			setCurrentTime()
		} else {
			year = date.substring(0, 4).toInt()
			month = date.substring(4, 6).toInt() - 1
			day = date.substring(6).toInt()
		}

		val calendar = Calendar.getInstance()
		calendar.set(year, month, day)

		text = SimpleDateFormat(defaultFormat).format(calendar.time)
	}

	fun getDate(): String {
		return text.toString().replace("-", "")
	}
}
