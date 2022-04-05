package com.emaintec.lib.ctrl

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ScrollAwareFABBehavior(context: Context, attributeSet: AttributeSet) :
	CoordinatorLayout.Behavior<FloatingActionButton>(context, attributeSet) {

	private var _checkConsumed = false
	private var _dyConsumed: Int = 0
	private var _dyUnconsumed: Int = 0
	private val _handler = Handler()

	override fun onNestedScroll(
		coordinatorLayout: CoordinatorLayout,
		child: FloatingActionButton,
		target: View,
		dxConsumed: Int,
		dyConsumed: Int,
		dxUnconsumed: Int,
		dyUnconsumed: Int,
		type: Int
	) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)

		if (!_checkConsumed) {
			return
		}

		_dyConsumed += dyConsumed
		_dyUnconsumed += dyUnconsumed

		if (View.VISIBLE == child.visibility && (40 <= Math.abs(_dyConsumed) || 40 <= Math.abs(_dyUnconsumed))) {

			_checkConsumed = false
			val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
			val fab_bottomMargin = layoutParams.bottomMargin
			child.animate().setDuration(100).setStartDelay(0).translationY((child.height + fab_bottomMargin).toFloat())
				.setInterpolator(LinearInterpolator()).start()
		}
	}

	override fun onStartNestedScroll(
		coordinatorLayout: CoordinatorLayout,
		child: FloatingActionButton,
		directTargetChild: View,
		target: View,
		nestedScrollAxes: Int,
		type: Int
	): Boolean {

		_checkConsumed = true
		_dyConsumed = 0
		_dyUnconsumed = 0
		_handler.removeCallbacksAndMessages(null)

		return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
			coordinatorLayout,
			child,
			directTargetChild,
			target,
			nestedScrollAxes,
			type
		)
	}

	override fun onStopNestedScroll(
		coordinatorLayout: CoordinatorLayout,
		child: FloatingActionButton,
		target: View,
		type: Int
	) {

		_checkConsumed = false

		// 2 초 후에 실행
		_handler.postDelayed({
			child.animate().setDuration(100).setStartDelay(0).translationY(0f).setInterpolator(LinearInterpolator())
				.start()
			//mHandler.sendEmptyMessage(0);
		}, 2000)

		super.onStopNestedScroll(coordinatorLayout, child, target, type)
	}
}
