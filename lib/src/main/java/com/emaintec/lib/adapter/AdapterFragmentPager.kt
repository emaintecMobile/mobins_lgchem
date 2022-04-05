package com.emaintec.lib.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import java.util.ArrayList

class AdapterFragmentPager(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	private val fragmentArrayList = ArrayList<Fragment>()

	override fun getItem(position: Int): Fragment {
		return fragmentArrayList[position]
	}

	override fun getCount(): Int {
		return fragmentArrayList.size
	}

	fun addItem(fragment: Fragment) {
		fragmentArrayList.add(fragment)
		notifyDataSetChanged()
	}

	fun clear() {
		fragmentArrayList.clear()
		notifyDataSetChanged()
	}
}
