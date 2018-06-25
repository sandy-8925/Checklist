package org.sanpra.checklist.activity


import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.AdapterView
import android.widget.PopupMenu
import org.sanpra.checklist.R
import org.sanpra.checklist.activity.ChecklistActivity.EDIT_ITEM_ACTION
import org.sanpra.checklist.activity.ChecklistActivity.actionTag
import org.sanpra.checklist.databinding.FragmentItemsListBinding
import org.sanpra.checklist.dbhelper.ItemsDatabase

/**
 * Displays checklist items
 */
class ItemsListFragment : Fragment(),  LoaderManager.LoaderCallbacks<List<ChecklistItem>> {
    private lateinit var binding : FragmentItemsListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_list, container, false)
        return binding.root
    }


    private lateinit var itemsDao: ItemsDao

    private val CHECKLIST_ITEMS_CURSOR_LOADER_ID = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // create database helper object and fetch all checklist items from
        // database
        itemsDao = ItemsDatabase.getInstance(requireContext()).itemsDao()
        setupItemsListUI()
        registerForContextMenu(binding.itemsList)
        loaderManager.initLoader(CHECKLIST_ITEMS_CURSOR_LOADER_ID, null, this)
    }

    private lateinit var itemListAdapter: ChecklistItemRecyclerAdapter

    @UiThread
    private fun setupItemsListUI() {
        val context = requireContext()
        binding.itemsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        itemListAdapter = ChecklistItemRecyclerAdapter()
        itemListAdapter.setOnItemClickListener(object : ChecklistItemRecyclerAdapter.ItemClickListener() {
            internal override fun onClick(view: View, itemId: Long) {
                itemsDao.flipStatus(itemId)
            }
        })
        itemListAdapter.setItemLongClickListener(object : ChecklistItemRecyclerAdapter.ItemLongClickListener() {
            internal override fun onLongClick(view: View, itemId: Long) {
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
                    itemsDao.deleteItem(itemId)
                    return true
                }

                R.id.context_menu_edit -> {
                    val editItemIntent = Intent(requireContext(),
                            ItemDescriptionEntryActivity::class.java)
                    editItemIntent.putExtra(actionTag, EDIT_ITEM_ACTION)
                    editItemIntent.putExtra(ItemDescriptionEntryActivity.EXTRA_KEY_ITEM_ID, itemId)
                    startActivity(editItemIntent)
                    return true
                }
            }
            return false
        }
    }

    override fun onCreateLoader(cursorId: Int, bundle: Bundle?): Loader<List<ChecklistItem>> {
        return ChecklistItemsCursorLoader(requireContext())
    }

    override fun onLoadFinished(cursorLoader: Loader<List<ChecklistItem>>, itemList: List<ChecklistItem>?) {
        itemListAdapter.setItems(itemList)
    }

    override fun onLoaderReset(cursorLoader: Loader<List<ChecklistItem>>) {}

    override fun onCreateContextMenu(menu: ContextMenu, view: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.checklist_context_menu, menu)
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        val info = menuItem
                .menuInfo as AdapterView.AdapterContextMenuInfo
        when (menuItem.itemId) {
            R.id.context_menu_delete -> {
                itemsDao.deleteItem(info.id)
                return true
            }

            R.id.context_menu_edit -> {
                val editItemIntent = Intent(requireContext(),
                        ItemDescriptionEntryActivity::class.java)
                editItemIntent.putExtra(actionTag, EDIT_ITEM_ACTION)
                editItemIntent.putExtra(ItemDescriptionEntryActivity.EXTRA_KEY_ITEM_ID, info.id)
                startActivity(editItemIntent)
                return true
            }

            else -> return super.onContextItemSelected(menuItem)
        }
    }
}
