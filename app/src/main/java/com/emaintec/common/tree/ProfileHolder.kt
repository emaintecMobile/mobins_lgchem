package com.ibetools.lscable.tree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.emaintec.mobins.R
import com.github.johnkil.print.PrintView
import com.unnamed.b.atv.model.TreeNode


/**
 * Created by Bogdan Melnychuk on 2/13/15.
 */
class ProfileHolder(context: Context) : TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem>(context) {

    override fun createNodeView(node: TreeNode, value: IconTreeItemHolder.IconTreeItem): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_profile_node, null, false)
        val tvValue = view.findViewById<View>(R.id.node_value) as TextView
        tvValue.text = value.dataitem.DESCRIPTION

        val iconView = view.findViewById<View>(R.id.icon) as PrintView
        iconView.iconText = context.resources.getString(value.icon)

        return view
    }

    override fun toggle(active: Boolean) {}

    override fun getContainerStyle(): Int {
        return R.style.TreeNodeStyleCustom
    }
}
