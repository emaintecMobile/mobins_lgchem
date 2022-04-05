package com.emaintec.common

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emaintec.common.model.codeModel
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.mobins.databinding.CommonCodeListBinding

class userFindAdapter private constructor() : RecyclerViewAdapter<codeModel, userFindAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: CommonCodeListBinding) :  RecyclerView.ViewHolder(binding.root) {
        var textView_01: TextView = binding.textView01
        var textView_02: TextView = binding.textView02
        var textView_03: TextView = binding.textView03
        var layout_Frame : LinearLayout = binding.layoutFrame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CommonCodeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = _arrayList.get(position)
        holder.textView_01.text = "${item.CODE}"
        holder.textView_02.text = "${item.DESCRIPTION}"
        holder.textView_03.text = "${item.TYPE_DESC}"
        holder.layout_Frame.isSelected = position == selection
    }

    private object SingletonHolder {
        val INSTANCE = userFindAdapter()
    }

    companion object {
        val instance: userFindAdapter
            get() = SingletonHolder.INSTANCE
    }
}
