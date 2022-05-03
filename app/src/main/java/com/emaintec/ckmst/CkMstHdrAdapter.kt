package com.emaintec.ckmst

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emaintec.ckmst.model.PmMstrModel
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.mobins.databinding.CkMstHdrListBinding


class CkMstHdrAdapter private constructor() : RecyclerViewAdapter<PmMstrModel, CkMstHdrAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: CkMstHdrListBinding) :  RecyclerView.ViewHolder(binding.root) {
        var textView_01: TextView = binding.textView01
        var textView_02: TextView = binding.textView02
        var textView_03: TextView = binding.textView03
        var textView_04: TextView = binding.textView04
        var textView_05: TextView = binding.textView05

        var layout_Frame : LinearLayout = binding.layoutRoot
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CkMstHdrListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = _arrayList.get(position)
        holder.textView_01.text = "${item.PM_TAG_NO} (${item.PM_EQP_NO})"
        holder.textView_02.text = "${item.PM_CYCLE}"
        holder.textView_03.text = "${item.PM_WKCNTR}"
        holder.textView_04.text = "2020-01-03"//"${item.PM_LDATE}"
        holder.textView_05.text = "${item.PM_EQP_NM}"
        //list 선택시 해더의 색깔을 변경한다.
        holder.layout_Frame.isSelected = position == selection
    }

    private object SingletonHolder {
        val INSTANCE = CkMstHdrAdapter()
    }

    companion object {
        val instance: CkMstHdrAdapter
            get() = SingletonHolder.INSTANCE
    }
}
