
package com.emaintec.common

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.emaintec.common.model.commonTreeModel
import com.emaintec.mobins.R
import com.github.johnkil.print.PrintView
import moe.leer.tree2view.TreeUtils
import moe.leer.tree2view.adapter.TreeAdapter
import moe.leer.tree2view.module.DefaultTreeNode

class commonTreeAdapter(context: Context?, root: DefaultTreeNode<commonTreeModel?>?, resourceId: Int) :
    TreeAdapter<commonTreeModel?>(context, root, resourceId) {
    override fun getView(position: Int, convertView : View?, parent: ViewGroup): View? {
        var convertView = convertView
        if (mNodesList == null) {
            mNodesList = TreeUtils.getVisibleNodesD(super.mRoot)
            notifyDataSetChanged()
        }
        val node = mNodesList[position]
        val holder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false)
            holder = ViewHolder()
            holder.arrowIcon = convertView.findViewById<View>(R.id.arrow_icon) as PrintView
            holder.itemIcon = convertView.findViewById<View>(R.id.item_icon) as PrintView
            holder.fileText = convertView.findViewById<View>(R.id.ft_name) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        val fileItem = node.element as commonTreeModel
        //get the file name(not the full name)
        holder.fileText!!.text = fileItem.DESCRIPTION
        if (fileItem.CHILD == "0") { //set dir icon
//set file icon
            holder.arrowIcon!!.visibility = View.INVISIBLE
            holder.itemIcon!!.iconText = getStringResource(R.string.ic_drive_document)
        } else {
            holder.arrowIcon!!.visibility = View.VISIBLE
            holder.itemIcon!!.iconText = getStringResource(R.string.ic_folder)
        }
        val depth = node.depth
        if (position == getPosition()) {
            holder.fileText!!.setTextColor(Color.BLUE)
            holder.fileText!!.typeface = Typeface.DEFAULT_BOLD
        } else {
            holder.fileText!!.setTextColor(Color.BLACK)
            holder.fileText!!.typeface = Typeface.DEFAULT
        }
        // holder.fileText
// convertView.findViewById<View>(R.id.layout_Frame).isSelected = position == selection
        setPadding(holder.arrowIcon, depth , -1)
        toggle(node, holder)
      //  if (node.isRoot) convertView!!.findViewById<View>(R.id.relative_Layout).setBackgroundColor(Color.RED)
        return convertView
    }

    override fun toggle(vararg objects: Any) {
        var node: DefaultTreeNode<*>? = null
        var holder: ViewHolder? = null
        try {
            node = objects[0] as DefaultTreeNode<*>
            holder = objects[1] as ViewHolder
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
        //only toggle for dir
        if (node!!.isExpandable) {
            if (!node.isExpanded) { //set right arrowIcon
                holder!!.arrowIcon!!.iconText = getStringResource(R.string.ic_keyboard_arrow_right)
            } else if (node.isExpanded) { //set down arrowIcon
                holder!!.arrowIcon!!.iconText = getStringResource(R.string.ic_keyboard_arrow_down)
            }
        } else { //holder.arrowIcon.setVisibility(View.GONE);
            holder!!.arrowIcon!!.iconText = getStringResource(R.string.ic_keyboard_arrow_right)
        }
    }

    private fun getStringResource(id: Int): String {
        checkNotNull(mContext) { "Context is null" }
        return mContext.resources.getString(id)
    }

    internal inner class ViewHolder {
        var arrowIcon: PrintView? = null
        var itemIcon: PrintView? = null
        var fileText: TextView? = null
    }
}