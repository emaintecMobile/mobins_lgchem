package com.emaintec.lib.ctrl

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class ViewPager : androidx.viewpager.widget.ViewPager {

	private var _enabled = true

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
		if (_enabled) {
			return super.onInterceptTouchEvent(ev)
		} else {
			if (MotionEvent.ACTION_MOVE == ev.actionMasked) {
				// ignore move action
				return false
			} else {
				if (super.onInterceptTouchEvent(ev)) {
					super.onTouchEvent(ev)
				}
			}
			return false
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(ev: MotionEvent): Boolean {
		return if (_enabled) {
			super.onTouchEvent(ev)
		} else {
			MotionEvent.ACTION_MOVE != ev.actionMasked && super.onTouchEvent(ev)
		}
	}

	fun setPagingEnabled(enabled: Boolean) {
		_enabled = enabled
	}
}
