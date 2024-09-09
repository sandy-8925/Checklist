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


import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.TypedValue
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.UiThread
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.sanpra.checklist.R
import org.sanpra.checklist.application.SystemObjects
import org.sanpra.checklist.databinding.FragmentItemsListBinding
import org.sanpra.checklist.dbhelper.ChecklistItem

private fun ClipData.getTextItems() : List<String> {
    val result = mutableListOf<String>()
    for(pos in 0 until itemCount)  result.add(getItemAt(pos).text.toString())
    return result
}

/**
 * Displays checklist items
 */
class ItemsListFragment : Fragment() {
    private val itemsController = SystemObjects.itemsController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_items_list, container, false)

    private val viewModel: ItemsListFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentItemsListBinding.bind(view)
        setupItemsListUI(binding)
        registerForContextMenu(binding.itemsList)
        viewModel.itemsList.observe(viewLifecycleOwner) { itemListAdapter.submitList(it) }

        dragEventLiveData.observe(viewLifecycleOwner) {
            when(it) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    binding.itemsList.visibility = View.INVISIBLE
                    binding.dropLayout.visibility = View.VISIBLE
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    binding.itemsList.visibility = View.VISIBLE
                    binding.dropLayout.visibility = View.INVISIBLE
                }
                DragEvent.ACTION_DRAG_ENTERED -> binding.dropLayout.setBackgroundColor(getColour(R.attr.dropInProgressColourActive, android.R.color.holo_green_light))
                DragEvent.ACTION_DRAG_EXITED ->  binding.dropLayout.setBackgroundColor(getColour(R.attr.dropInProgressColourInactive, android.R.color.darker_gray))
            }
        }
        textDropListener = TextDropListener()
        binding.itemsList.setOnDragListener(textDropListener)
    }

    private lateinit var textDropListener: TextDropListener

    private val dragEventLiveData = MutableLiveData<Int>()

    private inner class TextDropListener : View.OnDragListener {
        override fun onDrag(view: View, event: DragEvent): Boolean {
            when(event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    val isText = event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    if(isText) dragEventLiveData.postValue(event.action)
                    return isText
                }
                DragEvent.ACTION_DRAG_ENDED -> dragEventLiveData.postValue(event.action)
                DragEvent.ACTION_DROP -> {
                    if(!event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) return false
                    val items = event.clipData.getTextItems()
                    for(item in items) itemsController.addItem(item)
                    return true
                }
                DragEvent.ACTION_DRAG_ENTERED -> dragEventLiveData.postValue(event.action)
                DragEvent.ACTION_DRAG_EXITED -> dragEventLiveData.postValue(event.action)
            }
            return false
        }
    }

    private fun getColour(attrId: Int, defaultValue: Int): Int {
        val typedValue = TypedValue()
        val found = requireContext().theme.resolveAttribute(attrId, typedValue, true)
        if(found) return typedValue.data
        return defaultValue
    }

    private val itemListAdapter = ChecklistItemRecyclerAdapter()

    @UiThread
    private fun setupItemsListUI(binding: FragmentItemsListBinding) {
        itemListAdapter.itemClickListener = object : ChecklistItemRecyclerAdapter.ItemClickListener() {
            override fun onClick(view: View, itemId: Long) {
                itemsController.flipStatus(itemId)
            }
        }
        itemListAdapter.itemLongClickListener = object : ChecklistItemRecyclerAdapter.ItemLongClickListener() {
            override fun onLongClick(view: View, itemId: Long) {
                showItemPopupMenu(view, itemId)
            }
        }
        binding.itemsList.adapter = itemListAdapter
    }

    @UiThread
    private fun showItemPopupMenu(view: View, itemId: Long) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.checklist_context_menu)
        popupMenu.setOnMenuItemClickListener(ItemPopupMenuClickListener(itemId))
        popupMenu.show()
    }

    private inner class ItemPopupMenuClickListener(private val itemId: Long) : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.context_menu_delete -> {
                    itemsController.deleteItem(itemId)
                    return true
                }

                R.id.context_menu_edit -> {
                    val dialog : DialogFragment = ItemEditDialog()
                    dialog.arguments = ItemEditDialog.getArgs(itemId)
                    dialog.show(parentFragmentManager, ItemEditDialog.TAG)
                    return true
                }
            }
            return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.checklist_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delcheckeditems -> {
                itemsController.deleteCheckedItems()
                return true
            }

            R.id.menu_checkall -> {
                itemsController.checkAllItems()
                return true
            }

            R.id.menu_uncheckall -> {
                itemsController.uncheckAllItems()
                return true
            }

            R.id.menu_reverseall -> {
                itemsController.reverseAllItemStatus()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

internal class ItemsListFragmentViewModel : ViewModel() {
    val itemsList : LiveData<List<ChecklistItem>> = SystemObjects.itemsController().itemListLiveData()
}
