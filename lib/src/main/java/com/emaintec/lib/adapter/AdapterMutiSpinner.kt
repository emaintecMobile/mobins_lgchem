package com.emaintec.lib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.emaintec.lib.R

class AdapterMutiSpinner(context: Context, resource: Int, objects: ArrayList<ModelMutilSpinner>) : ArrayAdapter<ModelMutilSpinner?>(context, resource, objects as List<ModelMutilSpinner?>) {
    private val mContext: Context
    var listState: ArrayList<ModelMutilSpinner>
    private val myAdapter: AdapterMutiSpinner
    private var isFromView = false
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View? {
        return getCustomView(position, convertView, parent)
    }
//
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    fun getCustomView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView: View? = convertView
        val holder: ViewHolder
        if (convertView == null) {
            val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
            convertView = layoutInflator.inflate(R.layout.custom_spinner_check_item, null)
            holder = ViewHolder()
            holder.mTextView = convertView
                .findViewById(R.id.text) as TextView
            holder.mCheckBox = convertView
                .findViewById(R.id.checkbox) as CheckBox
            convertView.setTag(holder)
        } else {
            holder = convertView!!.getTag() as ViewHolder
        }
        holder.mTextView!!.setText(listState[position].NAME)

        // To check weather checked event fire from getview() or user input
        isFromView = true
        holder.mCheckBox!!.setChecked(listState[position].selected)
        isFromView = false
        if (position == 0) {
            holder.mCheckBox!!.setVisibility(View.INVISIBLE)

        } else {
            holder.mCheckBox!!.setVisibility(View.VISIBLE)
        }
        holder.mCheckBox!!.setTag(position)
        holder.mCheckBox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isFromView) {
                listState[position].selected = isChecked
                var items = ""
                listState.filter { it.selected }.forEach {
                    items += it.NAME + ","
                }
                if(items.isBlank()){
                    listState[0].NAME = items
                    notifyDataSetChanged()
                    return@setOnCheckedChangeListener
                }
                items = items.substring(0,items.length-1)
                if(!listState[0].NAME.equals(items)) {
                    listState[0].NAME = items
                    notifyDataSetChanged()
                }
            }
        }


        return convertView!!
    }

    private inner class ViewHolder {
        var mTextView: TextView? = null
        var mCheckBox: CheckBox? = null
    }

    init {
        mContext = context
        listState = objects
        myAdapter = this
    }
}