package com.emaintec.lib.ctrl

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView

import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min

open class ListViewAdapter<T> : BaseAdapter() {

	val currentItem: T?
		get() = if (0 > _position || _arrayList.size <= _position) {
			null
		} else _arrayList[_position]

	// 아이템 위치의 차이가 10칸 미만이면 부드럽게 이동하고 차이가 크면 바로 이동한다.
	var selection: Int
		get() = _position
		set(position) {
			if (0 > position || _arrayList.size <= position) {
				return
			}

			val prevPosition = _position
			_position = position
			this.notifyDataSetChanged()

			if (null != listView) {
				if (10 > Math.abs(_position - prevPosition))
					listView!!.smoothScrollToPosition(_position)
				else
					listView!!.setSelection(_position)
			}
		}

	val isFirstElement: Boolean
		get() = _arrayList.isEmpty() || _position <= 0

	val isLastElement: Boolean
		get() = _arrayList.isEmpty() || _position >= _arrayList.size - 1

	private val _handler = Handler(Handler.Callback { msg ->
		when (msg.what) {
			SINGLE_TAP -> {
				_countTap = 0
				_onItemTapListener?.onSingleTap(msg.arg1)
			}
			DOUBLE_TAP -> {
				_countTap = 0
				_onItemTapListener?.onDoubleTap(msg.arg1)
			}
			LONG_TAP -> {
				_countTap = 0
				_onItemTapListener?.onDoubleTap(msg.arg1)
			}
		}
		true
	})

	private var _massageTap = Message()
	private var _positionHolder = -1
	private var _countTap = 0

	private var _onItemTapListener: OnItemTapListener? = null
	private var _position = -1

	private var _onGetViewListener: OnGetViewListener<T>? = null
	private var _listenerOnButtonClick: OnButtonClickListener? = null
	interface OnButtonClickListener {
		fun onClick(message: String,position: Int)
	}
	fun setOnButtonClickListener(listener: OnButtonClickListener?) {
		_listenerOnButtonClick = listener
	}
	fun getOnButtonClickListener() : OnButtonClickListener? {
		return _listenerOnButtonClick
	}
	var _arrayList = ArrayList<T>()
	var listView: ListView? = null
		@SuppressLint("ClickableViewAccessibility")
		set(listView) {
			field = listView
			field?.let {
				it.setOnTouchListener { v, event -> false }
				it.adapter = this
				it.onItemLongClickListener = object : AdapterView.OnItemLongClickListener {
					override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
						_positionHolder = position
						_massageTap = _handler.obtainMessage()
						_massageTap.what = LONG_TAP
						_massageTap.arg1 = position
						_handler.removeMessages(SINGLE_TAP)
						_handler.removeMessages(DOUBLE_TAP)
						_handler.sendMessageDelayed(_massageTap, SEND_MESSAGE_DELAY.toLong())

						_position = position
						notifyDataSetChanged()

						this@ListViewAdapter.listView!!.smoothScrollToPosition(_position)

						return true
					}
				}
				it.onItemClickListener = object : AdapterView.OnItemClickListener {
					override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
						if (null != _onItemTapListener) {
							when (_countTap) {
								0 -> {
									_countTap++
									_positionHolder = position
									_massageTap = _handler.obtainMessage()
									_massageTap.what = SINGLE_TAP
									_massageTap.arg1 = position
									_handler.removeMessages(SINGLE_TAP)
									_handler.removeMessages(DOUBLE_TAP)
									_handler.sendMessageDelayed(_massageTap, DOUBLE_TAP_DELAY.toLong())
								}
								1 -> if (_positionHolder == position) {
										_countTap++
										_massageTap = _handler.obtainMessage()
										_massageTap.what = DOUBLE_TAP
										_massageTap.arg1 = position
										_handler.removeMessages(SINGLE_TAP)
										_handler.removeMessages(DOUBLE_TAP)
										_handler.sendMessageDelayed(_massageTap, DOUBLE_TAP_DELAY.toLong())
									} else {
										_countTap++
										_positionHolder = position
										_massageTap = _handler.obtainMessage()
										_massageTap.what = SINGLE_TAP
										_massageTap.arg1 = position
										_handler.removeMessages(SINGLE_TAP)
										_handler.removeMessages(DOUBLE_TAP)
										_handler.sendMessageDelayed(_massageTap, DOUBLE_TAP_DELAY.toLong())
									}
								else -> {
									_countTap++
									_positionHolder = position
									_massageTap = _handler.obtainMessage()
									_massageTap.what = SINGLE_TAP
									_massageTap.arg1 = position
									_handler.removeMessages(SINGLE_TAP)
									_handler.removeMessages(DOUBLE_TAP)
									_handler.sendMessageDelayed(_massageTap, DOUBLE_TAP_DELAY.toLong())
								}
							}
						}

						_position = position
						notifyDataSetChanged()

						this@ListViewAdapter.listView!!.smoothScrollToPosition(_position)
					}
				}
			}
		}

	interface OnItemTapListener {
		fun onDoubleTap(position: Int)
		fun onSingleTap(position: Int)
	}

	interface OnGetViewListener<T> {
		fun getView(position: Int, convertView: View?, parent: ViewGroup, item: T, focused: Boolean): View?
	}

	fun setOnItemTapListener(listener: OnItemTapListener?) {
		_onItemTapListener = listener
	}

	fun setOnGetViewListener(listener: OnGetViewListener<T>?) {
		_onGetViewListener = listener
	}

	fun isExist(key: T): Boolean {
		//int i = _arrayList.indexOf(key);
		return _arrayList.contains(key)//i>0? true:false;
	}

	override fun getCount(): Int {
		return _arrayList.size
	}

	override fun getItem(position: Int): T {
		return _arrayList[position]
	}

	override fun getItemId(position: Int): Long {
		return 0
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
		return _onGetViewListener?.getView(position, convertView, parent, getItem(position), position == _position )

	}

	fun clear() {
		_position = -1
		_arrayList.clear()
		this.notifyDataSetChanged()
	}

	fun addItem(jobject: T) {
		_arrayList.add(jobject)
		this.notifyDataSetChanged()
	}

	fun addItem(position: Int, jobject: T) {
		_arrayList.add(position, jobject)
		this.notifyDataSetChanged()
	}

	fun removeItem(position: Int): T? {
		val item = _arrayList.removeAt(position)
		if (null != item) {
			this.notifyDataSetChanged()
		}
		_position = -1
		return item
	}

	fun replaceItem(position: Int, item: T) {
		if (null != _arrayList.removeAt(position)) {
			_arrayList.add(position, item)
			this.notifyDataSetChanged()
		}
	}

	fun refreshItem() {
		this.notifyDataSetChanged()
	}

	fun front(): T? {
		if (_arrayList.isNotEmpty()) {
			_position = 0
			this.notifyDataSetChanged()

			listView?.setSelection(_position)

			return _arrayList[_position]
		}

		return null
	}

	fun back(): T? {
		if (_arrayList.isNotEmpty()) {
			_position = _arrayList.size - 1
			this.notifyDataSetChanged()

			listView?.setSelection(_position)
		}

		return null
	}

	fun prev(): Boolean {
		if (_arrayList.isNotEmpty() && _position > 0) {
			_position--
			this.notifyDataSetChanged()

			listView?.smoothScrollToPosition(max(_position - 1, 0))
		}
		return _position > 0
	}

	operator fun next(): Boolean {
		if (_arrayList.isNotEmpty() && _position < _arrayList.size - 1) {
			_position++
			this.notifyDataSetChanged()

			listView?.smoothScrollToPosition(min(_position + 1, _arrayList.size - 1))
		}
		return _position < _arrayList.size - 1
	}

	companion object {
		private const val SINGLE_TAP = 1
		private const val DOUBLE_TAP = 2
		private const val LONG_TAP = 3
		private val DOUBLE_TAP_DELAY = ViewConfiguration.getDoubleTapTimeout()
		private val SEND_MESSAGE_DELAY = ViewConfiguration.getTapTimeout()
	}
}
