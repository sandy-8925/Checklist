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
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import org.apache.commons.lang3.StringUtils
import org.sanpra.checklist.R
import org.sanpra.checklist.activity.ItemDescriptionEntryActivity.EXTRA_KEY_ITEM_ID
import org.sanpra.checklist.dbhelper.ItemsDatabase

class ItemEditDialog : DialogFragment() {
    private val okClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        checklistItem.description = StringUtils.defaultString(editText?.text.toString())
        UpdateItemTask(itemsDao).execute(checklistItem)
    }

    companion object {
        val TAG : String = "ItemEditDialog"
        fun getArgs(itemId : Long) : Bundle {
            val args = Bundle()
            args.putLong(EXTRA_KEY_ITEM_ID, itemId)
            return args
        }
    }

    private val UNINIT_ITEM_ID : Long = -23
    private var itemId : Long = UNINIT_ITEM_ID
    private lateinit var checklistItem: ChecklistItem
    private var editText: EditText? = null
    private lateinit var itemsDao : ItemsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        args ?: return
        itemId = args.getLong(EXTRA_KEY_ITEM_ID, UNINIT_ITEM_ID)
        itemsDao = ItemsDatabase.getInstance(requireContext()).itemsDao()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val builder : AlertDialog.Builder = AlertDialog.Builder(context, R.style.EditDialogTheme)
                .setPositiveButton(android.R.string.ok, okClickListener)
                .setNegativeButton(android.R.string.cancel, null)
        val rootView = requireActivity().layoutInflater.inflate(R.layout.item_edit_dialog_layout, null)
        editText = rootView.findViewById(R.id.iedl_text)
        builder.setView(rootView)
        LoadItemTask(itemsDao).execute()
        return builder.create()
    }

    inner class LoadItemTask(private val itemsDao: ItemsDao) : AsyncTask<Void, Void, ChecklistItem>() {
        override fun doInBackground(vararg params: Void?): ChecklistItem? {
            return itemsDao.fetchItem(itemId)
        }

        override fun onPostExecute(result: ChecklistItem?) {
            super.onPostExecute(result)
            result?: return
            checklistItem = result
            editText?.setText(checklistItem.description)
        }
    }
}

private class UpdateItemTask(private val itemsDao: ItemsDao) : AsyncTask<ChecklistItem, Void, Any?>() {
    override fun doInBackground(vararg itemList: ChecklistItem?): Any? {
        for(item in itemList)
            itemsDao.updateItem(item)
        return null
    }
}