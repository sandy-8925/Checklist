/*
 * Copyright (C) 2011-2018 Sandeep Raghuraman (sandy.8925@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sanpra.checklist.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.lang3.StringUtils
import org.sanpra.checklist.R
import org.sanpra.checklist.databinding.ItemRowBinding
import org.sanpra.checklist.dbhelper.ChecklistItem

internal class ChecklistItemRecyclerAdapter @UiThread
constructor() : ListAdapter<ChecklistItem, ChecklistItemViewHolder>(ChecklistDiffCallback()) {
    internal var itemClickListener: View.OnClickListener? = null
    internal var itemLongClickListener: View.OnLongClickListener? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowBinding.inflate(inflater, parent, false)
        return ChecklistItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.item = item
        holder.itemView.apply {
            setTag(VIEWHOLDER_TAG, getItemId(position))
            setOnClickListener(itemClickListener)
            setOnLongClickListener(itemLongClickListener)
        }
    }

    override fun getItemId(position: Int) = getItem(position).id

    internal abstract class ItemClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            val itemId = v.getTag(VIEWHOLDER_TAG) as Long
            onClick(v, itemId)
        }

        internal abstract fun onClick(view: View, itemId: Long)
    }

    internal abstract class ItemLongClickListener : View.OnLongClickListener {
        internal abstract fun onLongClick(view: View, itemId: Long)

        override fun onLongClick(v: View): Boolean {
            val itemId = v.getTag(VIEWHOLDER_TAG) as Long
            onLongClick(v, itemId)
            return true
        }
    }

    companion object {
        private const val VIEWHOLDER_TAG = R.id.CursorItemId
    }
}

/**
 * Used to hold a reference to the TextView containing the text in a row
 */
internal class ChecklistItemViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root)

private class ChecklistDiffCallback : DiffUtil.ItemCallback<ChecklistItem>() {
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ChecklistItem, newItem: ChecklistItem): Boolean {
        if(oldItem == newItem) return true
        return oldItem.isChecked == newItem.isChecked && StringUtils.equals(oldItem.description, newItem.description)
    }

    override fun areItemsTheSame(oldItem: ChecklistItem, newItem: ChecklistItem): Boolean {
        if(oldItem == newItem) return true
        return oldItem.id == newItem.id
    }
}