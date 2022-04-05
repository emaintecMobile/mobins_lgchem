package com.emaintec.common

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emaintec.Functions
import com.emaintec.common.model.gridViewModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.lib.db.JsonHelper
import com.emaintec.mobins.R
import com.google.gson.Gson
import org.json.JSONObject


class commonViewAdapter<T>() : RecyclerViewAdapter<T, commonViewAdapter.MyViewHolder>() {
    var gridSetting: ArrayList<gridViewModel>? = null
    var ClassItem: Class<T> ? = null

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView_01: TextView = view.findViewById<View>(R.id.textView_01) as TextView
        var layout_list: LinearLayout = view.findViewById<View>(R.id.layout_list) as LinearLayout
        var button_Expander: TextView = view.findViewById<View>(R.id.button_Expander) as TextView
        var layout_Frame: LinearLayout = view.findViewById<View>(R.id.layout_Frame) as LinearLayout
        var layoutTitle: LinearLayout = view.findViewById<View>(R.id.layoutTitle) as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.common_view_list, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item = _arrayList.get(position)
        holder.layout_list.removeAllViews()
        holder.layout_Frame.isSelected = position == selection
        if(position == selection){
            holder.textView_01.setTextColor(ContextCompat.getColor(Emaintec.activity!!,R.color.colorListHeaderSel))
        }
        else{
            holder.textView_01.setTextColor(ContextCompat.getColor(Emaintec.activity!!,R.color.colorListHeaderTxt))
        }
        val jsonObject = JSONObject(Gson().toJson(item))

        var IS_EXPANDER = JsonHelper.getBooleanFromJson(jsonObject,"IS_EXPANDER",false)!!
        if(IS_EXPANDER)
            holder.button_Expander.text = "︿"
        else
            holder.button_Expander.text = "﹀"
        holder.button_Expander.setOnClickListener {
            IS_EXPANDER = !IS_EXPANDER
            jsonObject.put("IS_EXPANDER",IS_EXPANDER)
            _arrayList[position] = Gson().fromJson(jsonObject.toString(), ClassItem )
            notifyItemChanged(position)
        }
        holder.layoutTitle.visibility = View.GONE
        holder.textView_01.text = ""

        var PM_CHECK = JsonHelper.getStringFromJson(jsonObject, "PM_CHECK", "")!!

        var type = 0
        if(PM_CHECK.equals("Y")){
            type = 1
            var PM_STRANGE = JsonHelper.getStringFromJson(jsonObject, "PM_STRANGE", "")!!
            if(PM_STRANGE.equals("Y")){
                type = 2
            }
        }

        gridSetting?.forEach {
            var layout_line = LinearLayout(Emaintec.activity)
            layout_line.orientation = LinearLayout.HORIZONTAL
            if(it.listModel == null) {
                if (it.IS_TITLE) {
                    holder.layoutTitle.visibility = View.VISIBLE
                    holder.textView_01.text = holder.textView_01.text.toString() + "[" + JsonHelper.getStringFromJson(jsonObject, it.KEY, "")!! + "]"
                }
                else
                    Functions.addTextLabel(
                        layout_line,
                        it.COLUMN_NAME,
                        JsonHelper.getStringFromJson(jsonObject, it.KEY, "")!!,type
                    )

            }else{
                it.listModel!!.forEach {
                    Functions.addTextLabel2(
                        layout_line,
                        it.COLUMN_NAME,
                        JsonHelper.getStringFromJson(jsonObject, it.KEY, "")!!,type
                    )
                }
            }


            holder.layout_list.addView(layout_line)

            if(it.IS_EXPAND)
                layout_line.visibility = if(IS_EXPANDER) View.VISIBLE else View.GONE

        }
    }

}
