package com.ibetools.lscable.tree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.github.johnkil.print.PrintView
import com.emaintec.mobins.R
import com.unnamed.b.atv.model.TreeNode


/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
class SelectableHeaderHolder(context: Context) : TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem>(context) {
    private var tvValue: TextView? = null
    private var arrowView: PrintView? = null
    private var nodeSelector: CheckBox? = null
    private var mCheckListener: TreeNodeCheckListener? = null


    interface TreeNodeCheckListener {
        fun onCheck(node: TreeNode, value: Boolean)
    }

    fun setTreeNodeCheckListener(listener: TreeNodeCheckListener?): SelectableHeaderHolder {
        mCheckListener = listener
        return this
    }

    fun getTreeNodeCheckListener(): TreeNodeCheckListener? {
        return mCheckListener
    }

    override fun createNodeView(node: TreeNode, value: IconTreeItemHolder.IconTreeItem): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_selectable_header, null, false)

        tvValue = view.findViewById<View>(R.id.node_value) as TextView
        tvValue!!.text = value.dataitem.DESCRIPTION

        val iconView = view.findViewById<View>(R.id.icon) as PrintView
        iconView.iconText = context.resources.getString(value.icon)

        arrowView = view.findViewById<View>(R.id.arrow_icon) as PrintView
        if (false == node.isbChild()) {
            arrowView!!.visibility = View.GONE
        }

        nodeSelector = view.findViewById<View>(R.id.node_selector) as CheckBox
        nodeSelector!!.setOnCheckedChangeListener { buttonView, isChecked ->
            node.isSelected = isChecked
            for (n in node.children) {
                treeView.selectNode(n, isChecked)
            }
            mCheckListener!!.onCheck(node, isChecked)
        }
        nodeSelector!!.isChecked = node.isSelected

        return view
    }

    override fun toggle(active: Boolean) {
        arrowView!!.iconText =
            context.resources.getString(if (active) R.string.ic_keyboard_arrow_down else R.string.ic_keyboard_arrow_right)
    }

    override fun toggleSelectionMode(editModeEnabled: Boolean) {
        nodeSelector!!.visibility = if (editModeEnabled) View.VISIBLE else View.GONE
        nodeSelector!!.isChecked = mNode.isSelected
    }
}
