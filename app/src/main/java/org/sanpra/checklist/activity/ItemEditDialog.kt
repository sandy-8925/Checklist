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

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import org.apache.commons.lang3.StringUtils
import org.sanpra.checklist.R
import org.sanpra.checklist.application.SystemObjects
import org.sanpra.checklist.databinding.ItemEditDialogLayoutBinding
import org.sanpra.checklist.dbhelper.ChecklistItem

class ItemEditDialog : DialogFragment(), Observer<ChecklistItem> {
    override fun onChanged(result: ChecklistItem?) {
        result?: return
        checklistItem = result
        binding.iedlText.setText(checklistItem.description)
    }

    private val okClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ ->
        checklistItem.description = StringUtils.defaultString(binding.iedlText.text.toString())
        itemsController.updateItem(checklistItem)
    }

    companion object {
        private const val EXTRA_KEY_ITEM_ID = "item_id"
        internal const val TAG : String = "ItemEditDialog"
        internal fun getArgs(itemId : Long) = Bundle().apply { putLong(EXTRA_KEY_ITEM_ID, itemId) }
    }

    private val UNINIT_ITEM_ID : Long = -23
    private var itemId : Long = UNINIT_ITEM_ID
    private lateinit var checklistItem: ChecklistItem
    private val itemsController = SystemObjects.itemsController()
    private lateinit var binding : ItemEditDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        args ?: return
        itemId = args.getLong(EXTRA_KEY_ITEM_ID, UNINIT_ITEM_ID)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val builder : AlertDialog.Builder = AlertDialog.Builder(context, R.style.EditDialogTheme)
                .setPositiveButton(android.R.string.ok, okClickListener)
                .setNegativeButton(android.R.string.cancel, null)
        binding = ItemEditDialogLayoutBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        itemsController.fetchItem(itemId).observe(this, this)
        return builder.create()
    }
}

