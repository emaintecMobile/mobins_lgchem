package com.emaintec.ckdaily

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.emaintec.common.model.gridModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.base.Model
import com.emaintec.lib.ctrl.recycleview.RecyclerViewAdapter
import com.emaintec.lib.db.JsonHelper
import com.emaintec.lib.device.Device
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.databinding.CommonGridListBinding
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.Double.parseDouble
import java.text.DecimalFormat

class CkDayItemGridAdapter<T>() : RecyclerViewAdapter<T, CkDayItemGridAdapter.MyViewHolder>() {
    var gridSetting : ArrayList<gridModel> ?= null
    private var _onCellClickListener: OnCellClickListener? = null
    private var _onChangeListener: OnChangeListener? = null
    //var isSelection : Boolean = false
    var ClassItem: Class<T> ? = null
    var minHeight: Float = 35f

    interface OnCellClickListener {
        fun onCellClick(position: Int,columnName : String)
    }
    interface OnChangeListener {
        fun onChange()
    }
    fun setOnCellClickListener(listener: OnCellClickListener?) {
        _onCellClickListener = listener
    }
    fun setOnChangeListener(listener: OnChangeListener?) {
        _onChangeListener = listener
    }
    class MyViewHolder(val binding: CommonGridListBinding) :  RecyclerView.ViewHolder(binding.root) {
        var layout_grid: LinearLayout = binding.layoutGrid
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CommonGridListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = _arrayList.get(position)
        var i = 0
        holder.layout_grid.removeAllViews()
//        if(isSelection)
        holder.layout_grid.isSelected = position == selection
        val p1 = LinearLayout.LayoutParams(
            Device.convertDpToPixel(1f, Emaintec.activity!!).toInt(),
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val jsonObject = JSONObject(Gson().toJson(item))
        //각 model에 STATUS 값을 갖고와 해당 값에 따라.. 색깔및 속성을 변경한다.. TextView에 한해서...
        val STATUS = JsonHelper.getStringFromJson(jsonObject,"STATUS","")!!//jsonObject.getString(it.KEY)
        val VISIBILITY  = JsonHelper.getStringFromJson(jsonObject,"VISIBILITY","")!!
        if(VISIBILITY.equals("H")){
            holder.binding.layoutRoot.visibility = View.GONE
            holder.binding.layoutGrid.visibility = View.GONE
            holder.binding.layoutRow.visibility = View.GONE
        }else{
            holder.binding.layoutRoot.visibility = View.VISIBLE
            holder.binding.layoutGrid.visibility = View.VISIBLE
            holder.binding.layoutRow.visibility = View.VISIBLE
        }

        val line = LinearLayout(Emaintec.activity)
        line.setLayoutParams(p1)
        line.minimumHeight =  Device.convertDpToPixel(minHeight, Emaintec.activity!!).toInt()
        line.setBackgroundColor(Color.parseColor("#92b7eb"))
        holder.layout_grid.addView(line)

//        if(/*holder.layout_grid.isSelected && */(item as Model).LINE_BG.isNotBlank())
//        {
//        holder.layout_grid.backgroundTintList = ColorStateList.valueOf(R.drawable.selector_list_item_only_stroke)
//        }
//        holder.layout_grid.background = ColorStateList.valueOf(R.drawable.selector_list_item_only_stroke)

        gridSetting?.forEach {
            var objContainer = TextView(Emaintec.activity)
            if((item as Model).LINE_BG.isNotBlank()) // 배경색을 지정한 행만 색이 변경되도록
            {
                objContainer.setBackgroundColor(Color.parseColor((item as Model).LINE_BG))
            }

            if(it.TYPE.equals("CHK")) {
                objContainer = CheckBox(Emaintec.activity)
                objContainer.isClickable = false
            }else if(it.TYPE.equals("BTN")) {
                objContainer = Button(Emaintec.activity)
                objContainer.isClickable = false
            }else if(it.TYPE.equals("EDT")) {
                objContainer = com.emaintec.lib.ctrl.EditText(Emaintec.activity!!)
                objContainer.isClickable = false
            }
            val p = LinearLayout.LayoutParams(
                Device.convertDpToPixel(it.WIDTH, Emaintec.activity!!).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT,
                0f
            )
            p.topMargin =  Device.convertDpToPixel(1f, Emaintec.activity!!).toInt()
            p.bottomMargin = Device.convertDpToPixel(1f, Emaintec.activity!!).toInt()
            objContainer.setLayoutParams(p);
//        p.setMargins(30, 30, 30, 30);
//            text.setBackgroundResource(R.drawable.bg_label_item)
            objContainer.setPadding(10,0,10,0)
//            text.setTypeface(null, Typeface.BOLD);

            objContainer.textSize = it.TEXT_SIZE
            objContainer.gravity = it.GRAVITY
            if(it.TEXT_LINE != 0)
                objContainer.maxLines = it.TEXT_LINE
            else if(it.TEXT_LINE == 1)
                objContainer.setSingleLine(true)

            // 2020-04-14 TaeHyun 그리드 어댑터의 기본 텍스트 컬러 세팅 위치 변경
            objContainer.setTextColor(it.COLOR)
            if(it.TYPE.equals("TXT")) {
                if(it.KEY.equals("CHK_RESULT")) {
                    var is_num = JsonHelper.getStringFromJson(jsonObject, "CHK_CHAR", "")!!
                    if (is_num.isNotEmpty()) {
                        objContainer.setBackgroundColor(Color.YELLOW)
                    }
                }
                objContainer.text = JsonHelper.getStringFromJson(jsonObject, it.KEY,"")!!// jsonObject.getString(it.KEY)
                if(it.NUMBER_FORMAT.isNotEmpty() && objContainer.text.isNotEmpty())
                {
                    try {
                        val df = DecimalFormat(it.NUMBER_FORMAT)
                        objContainer.text = df.format(parseDouble(objContainer.text.toString()))
                    }catch(e: NumberFormatException){}
                }
                // 2020-04-14 TaeHyun STATUS 상태에 따라 해당 라인에 글자색 및 상태 변경
                if(STATUS.equals("D")){                                         // 삭제 버튼을 클릭시
                    objContainer.setTextColor(Color.RED)
                    objContainer.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else if (STATUS.equals("U")) {                                // 저장 버튼을 클릭시
                    objContainer.setTextColor(Color.BLUE)
                }
            }else if(it.TYPE.equals("CHK")) {
                (objContainer as CheckBox).isChecked =  JsonHelper.getBooleanFromJson(jsonObject,it.KEY,false)!! //jsonObject.getBoolean(it.KEY)
            }else if(it.TYPE.equals("BTN")) {
                objContainer.text = it.COLUMN_NAME
            }else if(it.TYPE.equals("EDT")) {
                objContainer.text = JsonHelper.getStringFromJson(jsonObject, it.KEY,"")!!
                if(it.NUMBER_FORMAT.isNotEmpty()) {
                    objContainer.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                (objContainer as EditText).addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(ss: CharSequence, start: Int, before: Int, count: Int) {}
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun afterTextChanged(ss: Editable?) {
                        var text:String ?
                        if(ss.isNullOrBlank()) text = null else text = ss.toString()
                        jsonObject.put(it.KEY, text)
                        _arrayList[position] = Gson().fromJson(jsonObject.toString(), ClassItem)

                    }
                })
            }

            if(it.IS_CLICK ) {
                objContainer.setOneClickListener { view ->
                    _onCellClickListener?.let { cell ->
                        cell.onCellClick(position, it.KEY)
                    }
                }
            }
            if(it.Select_color_1 != null)
            {
                if(it.Select_color_1!!.filter { it.equals(objContainer.text.toString()) }.isNotEmpty())
                {
                    objContainer.setTextColor(it.COLOR_1)
                }
            }
            if(it.Select_color_2 != null)
            {
                if(it.Select_color_2!!.filter { it.equals(objContainer.text.toString()) }.isNotEmpty())
                {
                    objContainer.setTextColor(it.COLOR_2)
                }
            }
            if(it.Select_color_std != null)
            {
                if(it.Select_color_std!!.toFloat()> objContainer.text.toString().toFloat())
                {
                    objContainer.setTextColor(it.COLOR_2)
                }else{
                    objContainer.setTextColor(it.COLOR_1)
                }
            }

            holder.layout_grid.addView(objContainer)
            if(i < gridSetting!!.size ) {
                var line = LinearLayout(Emaintec.activity)
                line.setLayoutParams(p1)
                line.minimumHeight = Device.convertDpToPixel(minHeight, Emaintec.activity!!).toInt()
                line.setBackgroundColor(Color.parseColor("#92b7eb"))
                holder.layout_grid.addView(line)
            }
            i++
        }
        _onChangeListener?.let {
            it.onChange()
        }
    }

}

