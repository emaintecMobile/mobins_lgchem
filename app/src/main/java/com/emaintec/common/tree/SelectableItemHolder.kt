package com.ibetools.lscable.tree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.emaintec.mobins.R
import com.unnamed.b.atv.model.TreeNode


/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
class SelectableItemHolder(context: Context) : TreeNode.BaseNodeViewHolder<String>(context) {
    private var tvValue: TextView? = null
    private var nodeSelector: CheckBox? = null

    override fun createNodeView(node: TreeNode, value: String): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_selectable_item, null, false)

        tvValue = view.findViewById<View>(R.id.node_value) as TextView
        tvValue!!.text = value


        nodeSelector = view.findViewById<View>(R.id.node_selector) as CheckBox
        nodeSelector!!.setOnCheckedChangeListener { buttonView, isChecked -> node.isSelected = isChecked }
        nodeSelector!!.isChecked = node.isSelected

        if (node.isLastChild) {
            view.findViewById<View>(R.id.bot_line).visibility = View.INVISIBLE
        }

        return view
    }


    override fun toggleSelectionMode(editModeEnabled: Boolean) {
        nodeSelector!!.visibility = if (editModeEnabled) View.VISIBLE else View.GONE
        nodeSelector!!.isChecked = mNode.isSelected
    }
}
