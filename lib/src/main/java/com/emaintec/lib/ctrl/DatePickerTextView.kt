package com.emaintec.lib.ctrl

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build

import android.util.AttributeSet

import android.view.View
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import java.text.ParseException


class DatePickerTextView : AppCompatTextView,  View.OnClickListener {
	override fun onClick(v: View?) {

		if(text==null || text.toString().equals("")) {
			text = SimpleDateFormat( "yyyy-MM-dd").format(Date())
		}
		val cal = Calendar.getInstance()
		val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
		var parsedDate: Date? = null
		try {
			parsedDate = simpleDateFormat.parse(text.toString())
		} catch (e: ParseException) {
			e.printStackTrace()
		}

		cal.time = parsedDate
		val dialog = DatePickerDialog(context, object : DatePickerDialog.OnDateSetListener {
			override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, date: Int) {
				text = DatePickerFormat(datePicker, "yyyy-MM-dd")
//				Toast.makeText(this@PickerActivity, msg, Toast.LENGTH_SHORT).show()
				this@DatePickerTextView._onDialogListener?.afterTextChanged(text.toString())
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
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
	interface OnDialogListener {
		fun afterTextChanged(date: String)
	}
	fun setOnDialogListener(listener: OnDialogListener?) {
		_onDialogListener = listener
	}

	private var _onDialogListener: OnDialogListener? = null

    var maxDate : Date? = null
    var minDate : Date? = null


    constructor(context: Context) : super(context) {
		if(text==null || text == "") text = SimpleDateFormat( "yyyy-MM-dd").format(Date())
		invalidate()
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		if(text==null || text == "") text = SimpleDateFormat( "yyyy-MM-dd").format(Date())
		invalidate()
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		if(text==null || text == "") text = SimpleDateFormat( "yyyy-MM-dd").format(Date())
		invalidate()
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
	}
	override fun performClick(): Boolean {
		return super.performClick()
	}



    private fun DatePickerFormat(datePicker: DatePicker, format: String?): String {
		val day = datePicker.dayOfMonth
		val month = datePicker.month
		val year = datePicker.year

		val calendar = Calendar.getInstance()
		calendar.set(year, month, day)
		val date = calendar.time

		return SimpleDateFormat(if (format.isNullOrEmpty()) "yyyy-MM-dd" else format).format(date)
	}
	private fun setCalrendarconVisible(visible: Boolean) {
		_calrendarDrawable!!.setVisible(visible, false)
		setCompoundDrawables(null, null, if (visible) _calrendarDrawable else null, null)
	}
}
