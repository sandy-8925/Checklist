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

import android.databinding.DataBindingUtil
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.apache.commons.collections4.ListUtils
import org.sanpra.checklist.R
import org.sanpra.checklist.databinding.ItemRowBinding

internal class ChecklistItemRecyclerAdapter @UiThread
constructor() : RecyclerView.Adapter<ChecklistItemViewHolder>() {
    private val DEFAULT_LIST = emptyList<ChecklistItem>()
    private var itemClickListener: View.OnClickListener? = null
    private var itemLongClickListener: View.OnLongClickListener? = null
    private var items = DEFAULT_LIST

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRowBinding>(inflater, R.layout.item_row, parent, false)
        return ChecklistItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.item = item
        holder.itemView.setTag(VIEWHOLDER_TAG, getItemId(position))
        holder.itemView.setOnClickListener(itemClickListener)
        holder.itemView.setOnLongClickListener(itemLongClickListener)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @UiThread
    fun setOnItemClickListener(onClickListener: ItemClickListener) {
        this.itemClickListener = onClickListener
    }

    fun setItems(items: List<ChecklistItem>?) {
        this.items = ListUtils.defaultIfNull(items, DEFAULT_LIST)
        notifyDataSetChanged()
    }

    fun setItemLongClickListener(itemLongClickListener: View.OnLongClickListener) {
        this.itemLongClickListener = itemLongClickListener
    }

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
        private val VIEWHOLDER_TAG = R.id.CursorItemId
    }
}

/**
 * Used to hold a reference to the TextView containing the text in a row
 */
internal class ChecklistItemViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root)
