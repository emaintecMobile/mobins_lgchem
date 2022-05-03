package com.emaintec.ckdaily

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emaintec.ckdaily.model.PmDayMstModel
import com.emaintec.ckmst.model.PmMstrModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.CkDayHdrListBinding
import com.emaintec.mobins.databinding.CkMstHdrListBinding


class CkDayHdrAdapter private constructor() : RecyclerViewAdapter<PmDayMstModel, CkDayHdrAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: CkDayHdrListBinding) :  RecyclerView.ViewHolder(binding.root) {
        var textView_01: TextView = binding.textView01
        var textView_02: TextView = binding.textView02
        var textView_03: TextView = binding.textView03
        var layout_Frame : LinearLayout = binding.layoutRoot
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CkDayHdrListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = _arrayList.get(position)
        holder.textView_01.text = "${item.PM_TAG_NO} (${item.PM_EQP_NO})"
        holder.textView_02.text = "${item.PM_PLN_DT}"
        holder.textView_03.text = "${item.PM_EQP_NM}"
        holder.textView_02.setTextColor(ContextCompat.getColor(Emaintec.activity!!,R.color.colorGridText))
        holder.textView_03.setTextColor(ContextCompat.getColor(Emaintec.activity!!,R.color.colorGridText))
        if(item.PM_CHECK.equals("Y"))
        {
            holder.textView_02.setTextColor(ContextCompat.getColor(Emaintec.activity!!,R.color.colorGridTextCheck))
            holder.textView_03.setTextColor(ContextCompat.getColor(Emaintec.activity!!,R.color.colorGridTextCheck))
        }
        if(item.PM_STRANGE.equals("Y"))
        {
            holder.textView_02.setTextColor(ContextCompat.getColor(Emaintec.activity!!,R.color.colorGridTextStrange))
            holder.textView_03.setTextColor(ContextCompat.getColor(Emaintec.activity!!,R.color.colorGridTextStrange))
        }
        //list 선택시 해더의 색깔을 변경한다.
        holder.layout_Frame.isSelected = position == selection
    }

    private object SingletonHolder {
        val INSTANCE = CkDayHdrAdapter()
    }

    companion object {
        val instance: CkDayHdrAdapter
            get() = SingletonHolder.INSTANCE
    }
}
