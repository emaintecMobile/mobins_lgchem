package com.emaintec.ckdaily

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.emaintec.Fragment_Base
import com.emaintec.ckdaily.model.PmDayCpModel
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.ctrl.EditTextDialog
import com.emaintec.lib.db.SQLiteQueryUtil
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.databinding.CkResultDtlInputBinding


class CkDayItemInput : Fragment_Base() {
    lateinit var binding : CkResultDtlInputBinding
    lateinit var adapterView : CkDayItemGridAdapter<PmDayCpModel>
    private var modelCkDtl: PmDayCpModel? = null
    private var _listener: OnDialogEventListener? = null
    interface OnDialogEventListener {
        fun onEvent(message: String)
    }
    fun setOnDialogEventListener(listener: OnDialogEventListener): CkDayItemInput {
        _listener = listener
        return this
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CkResultDtlInputBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButton()
        initEditText()
//        updateUI()
    }

    private fun initEditText() {

        binding.editTextNumValue!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if( modelCkDtl!!.CHK_RESULT != s.toString()) {
                    modelCkDtl!!.CHK_RESULT = s.toString()
                    changeStatus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }
    private fun initButton() {
        binding.customLayoutTitle.imageButton.setOnClickListener {
            dismiss()
        }
        binding.buttonPrev!!.setOnClickListener {
            if(adapterView.prev()) {
                updateUI()
            }else{
                binding.customLayoutTitle.imageButton.performClick()
            }
        }

        binding.buttonNext!!.setOnClickListener {
            if(adapterView.next()) {
                updateUI()
            }else{
                binding.customLayoutTitle.imageButton.performClick()
            }
        }

        binding.buttonStatusO.setOnClickListener {
//            adapterView.currentItem?.STATUS = "U"
            adapterView.currentItem?.CHK_OKNOK = "OK"
            adapterView.notifyItemChanged(adapterView.selection)
            SQLiteQueryUtil.update_table_Model(adapterView.currentItem as Object,"TB_PM_DAYCP", arrayOf("CHK_NO","PM_NOTI_NO","PM_EQP_NO","PM_PLAN"))
        }
        binding.buttonStatusX.setOnClickListener {
//            adapterView.currentItem?.STATUS = "U"
            adapterView.currentItem?.CHK_OKNOK = "NOK"
            adapterView.notifyItemChanged(adapterView.selection)
            SQLiteQueryUtil.update_table_Model(adapterView.currentItem as Object,"TB_PM_DAYCP", arrayOf("CHK_NO","PM_NOTI_NO","PM_EQP_NO","PM_PLAN"))
        }
        binding.textEditRemark.setOnDialogListener(object : EditTextDialog.OnDialogListener {
            override fun afterTextChanged(editTextDialog: EditTextDialog) {
//                adapterView.currentItem?.STATUS = "U"
                adapterView.currentItem?.CHK_MEMO = editTextDialog.text.toString()
                adapterView.notifyItemChanged(adapterView.selection)
            }
        })
        binding.buttonHst.setOneClickListener {
            showResultHst()
        }
    }
    private fun showResultHst(){
        CkDayItemChart().let {
            it.item = modelCkDtl
            it.showNow(parentFragmentManager, "")
            it.dialog?.setCanceledOnTouchOutside(false);
            it.dialog?.window?.let { window ->
                val params = window.attributes
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                window.attributes = params as android.view.WindowManager.LayoutParams
                window.setGravity(Gravity.TOP)
                window.setWindowAnimations(android.R.style.Animation_InputMethod)
            }
            it.dialog!!.setOnDismissListener {
                Emaintec.fragment = this
            }
            it.updateUI()
        }
    }
    override fun updateUI() {
        modelCkDtl = adapterView.currentItem ?: PmDayCpModel()

        binding.textViewMinValue.text = modelCkDtl?.CHK_MIN
        binding.textViewMaxValue.text = modelCkDtl?.CHK_MAX
        binding.textViewStdValue.text = modelCkDtl?.CHK_DEST
        binding.editTextNumValue.setText(modelCkDtl?.CHK_RESULT)
        binding.textEditRemark.setText(modelCkDtl?.CHK_MEMO)
        binding.textViewCkType.text = "내용: ${modelCkDtl?.CHK_DESC}"
        binding.textViewCkPos.text = "측정위치: ${modelCkDtl?.CHK_POS}"

        if(modelCkDtl?.CHK_OKNOK.equals("NOK"))
        {
            binding.buttonStatusX.performClick()
        }else{
            binding.buttonStatusO.performClick()
        }
        if(modelCkDtl?.CHK_CHAR!!.isNotEmpty()){
            binding.layoutNum.visibility = View.VISIBLE
            binding.editTextNumValue.setSelectAllOnFocus(true);
            binding.editTextNumValue.requestFocus()
            binding.editTextNumValue.post {
                val inputMethodManager: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(binding.editTextNumValue, InputMethodManager.SHOW_IMPLICIT)
            }
        }else{
            binding.layoutNum.visibility = View.GONE
            val inputMethodManager: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.editTextNumValue.windowToken, 0)
        }
    }

    private fun changeStatus() {
       if( modelCkDtl!!.CHK_RESULT.isNullOrBlank() || modelCkDtl!!.CHK_RESULT.equals("-")|| modelCkDtl!!.CHK_RESULT.equals(".")) return
        var max = modelCkDtl!!.CHK_MAX
        var std = modelCkDtl!!.CHK_DEST
        var min = modelCkDtl!!.CHK_MIN
        if (max.isNullOrBlank() && std.isNullOrBlank() && min.isNullOrBlank()) return

        if (max.isNotEmpty() && std.isNotEmpty() && min.isNullOrBlank()) min = std

        if (max.isNullOrBlank() && std.isNotEmpty() && min.isNotEmpty()) max = std

        if (max.isNullOrBlank() && std.isNotEmpty()  && min.isNullOrBlank() ) {
            max = std
            min = std
        }

        if (max.isNotEmpty()) {
            if (modelCkDtl!!.CHK_RESULT!!.toDouble() > max.toDouble()) {
                binding.buttonStatusX.performClick()
                return
            } else {
                binding.buttonStatusO.performClick()
            }
        }
        if (min.isNotEmpty()) {
            if (modelCkDtl!!.CHK_RESULT!!.toDouble() < min.toDouble()) {
                binding.buttonStatusX.performClick()
                return
            } else {
                binding.buttonStatusO.performClick()
            }
        }

    }
}
