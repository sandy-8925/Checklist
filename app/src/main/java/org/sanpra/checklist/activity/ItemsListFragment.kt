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


import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.PopupMenu
import org.sanpra.checklist.R
import org.sanpra.checklist.databinding.FragmentItemsListBinding
import org.sanpra.checklist.dbhelper.ItemsDatabase
import org.sanpra.checklist.dbhelper.ItemsDbThreadHelper

/**
 * Displays checklist items
 */
class ItemsListFragment : Fragment(), Observer<List<ChecklistItem>> {
    override fun onChanged(t: List<ChecklistItem>?) {
        itemListAdapter.setItems(t)
    }

    private lateinit var binding : FragmentItemsListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_list, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    private lateinit var itemsDao: ItemsDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // create database helper object and fetch all checklist items from
        // database
        itemsDao = ItemsDatabase.getInstance(requireContext()).itemsDao()
        setupItemsListUI()
        registerForContextMenu(binding.itemsList)
        itemsDao.fetchAllItems().observe(this, this)
    }

    private lateinit var itemListAdapter: ChecklistItemRecyclerAdapter

    @UiThread
    private fun setupItemsListUI() {
        val context = requireContext()
        binding.itemsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        itemListAdapter = ChecklistItemRecyclerAdapter()
        itemListAdapter.setOnItemClickListener(object : ChecklistItemRecyclerAdapter.ItemClickListener() {
            override fun onClick(view: View, itemId: Long) {
                ItemsDbThreadHelper.dbOpsHandler.post { itemsDao.flipStatus(itemId) }
            }
        })
        itemListAdapter.setItemLongClickListener(object : ChecklistItemRecyclerAdapter.ItemLongClickListener() {
            override fun onLongClick(view: View, itemId: Long) {
                showItemPopupMenu(view, itemId)
            }
        })
        binding.itemsList.adapter = itemListAdapter
    }

    @UiThread
    private fun showItemPopupMenu(view: View, itemId: Long) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.checklist_context_menu)
        popupMenu.setOnMenuItemClickListener(ItemPopupMenuClickListener(itemId))
        popupMenu.show()
    }

    private inner class ItemPopupMenuClickListener internal constructor(private val itemId: Long) : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.context_menu_delete -> {
                    ItemsDbThreadHelper.dbOpsHandler.post { itemsDao.deleteItem(itemId) }
                    return true
                }

                R.id.context_menu_edit -> {arguments
                    val dialog : DialogFragment = ItemEditDialog()
                    dialog.arguments = ItemEditDialog.getArgs(itemId)
                    dialog.show(fragmentManager, ItemEditDialog.TAG)
                    return true
                }
            }
            return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.checklist_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delcheckeditems -> {
                ItemsDbThreadHelper.dbOpsHandler.post {  itemsDao.deleteCheckedItems() }
                return true
            }

            R.id.menu_checkall -> {
                ItemsDbThreadHelper.dbOpsHandler.post { itemsDao.checkAllItems() }
                return true
            }

            R.id.menu_uncheckall -> {
                ItemsDbThreadHelper.dbOpsHandler.post { itemsDao.uncheckAllItems() }
                return true
            }

            R.id.menu_reverseall -> {
                ItemsDbThreadHelper.dbOpsHandler.post { itemsDao.flipAllItems() }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
