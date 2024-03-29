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

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import org.apache.commons.lang3.StringUtils
import org.sanpra.checklist.application.SystemObjects
import org.sanpra.checklist.databinding.AddItemFragmentLayoutBinding

class AddItemFragment : Fragment() {

    private lateinit var binding: AddItemFragmentLayoutBinding

    private val inputEntryTextDoneListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            addNewItem()
        }
        true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AddItemFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupItemAddUI()
    }

    @UiThread
    private fun setupItemAddUI() {
        binding.newItemText.setOnEditorActionListener(inputEntryTextDoneListener)
        binding.newItemText.setOnKeyListener { _, keyCode, keyEvent ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                addNewItem()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.newItemAddButton.setOnClickListener { addNewItem() }
    }

    @UiThread
    private fun addNewItem() {
        val itemText = binding.newItemText.text.toString()
        if (!StringUtils.isBlank(itemText)) {
            SystemObjects.itemsController().addItem(itemText)
            binding.newItemText.setText("")
        }
    }
}