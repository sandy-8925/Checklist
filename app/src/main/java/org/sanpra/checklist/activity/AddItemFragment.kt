package org.sanpra.checklist.activity

import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import org.sanpra.checklist.databinding.AddItemFragmentLayoutBinding
import org.sanpra.checklist.dbhelper.ItemsDatabase

class AddItemFragment : Fragment() {

    private lateinit var binding: AddItemFragmentLayoutBinding

    private val inputEntryTextDoneListener = TextView.OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            addNewItem()
        }
        true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AddItemFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var itemsDao: ItemsDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemsDao = ItemsDatabase.getInstance(requireContext()).itemsDao()
        setupItemAddUI()
    }

    @UiThread
    private fun setupItemAddUI() {
        binding.newItemText.setOnEditorActionListener(inputEntryTextDoneListener)
        binding.newItemAddButton.setOnClickListener(AddItemOnClickListener())
    }

    private inner class AddItemOnClickListener : View.OnClickListener {
        override fun onClick(clickedView: View) {
            addNewItem()
        }
    }

    @UiThread
    private fun addNewItem() {
        val itemText = binding.newItemText.text.toString()
        if (!TextUtils.isEmpty(itemText)) {
            val item = ChecklistItem()
            item.description = itemText
            itemsDao.addItem(item)
            binding.newItemText.setText("")
        }
    }
}