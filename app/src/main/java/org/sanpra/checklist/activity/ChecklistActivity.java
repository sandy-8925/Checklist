/*
 * Copyright (C) 2011-2015 Sandeep Raghuraman (sandy.8925@gmail.com)

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

package org.sanpra.checklist.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.sanpra.checklist.R;
import org.sanpra.checklist.databinding.ChecklistBinding;
import org.sanpra.checklist.dbhelper.ItemsDatabase;

import java.util.List;

/**
 * Main activity, that is displayed when the app is launched. Displays the list of ToDo items.
 */
public final class ChecklistActivity extends AppCompatActivity {

    /**
     * These constants are used to specify whether the ItemDescriptionEntryActivity is being opened
     * to add a new list item or edit an existing list item
     */
    static final int EDIT_ITEM_ACTION = 6;

    static final String actionTag = "actionTag";
    private final TextView.OnEditorActionListener inputEntryTextDoneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                addNewItem();
            }
            return true;
        }
    };
    private ChecklistBinding binding;
    private ItemsDao itemsDao;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.checklist);

        setSupportActionBar(binding.toolbar);

        // create database helper object and fetch all checklist items from
        // database
        itemsDao = ItemsDatabase.getInstance(this.getApplicationContext()).itemsDao();
        setupItemAddUI();
    }

    @UiThread
    private void setupItemAddUI() {
        binding.newItemText.setOnEditorActionListener(inputEntryTextDoneListener);
        binding.newItemAddButton.setOnClickListener(new AddItemOnClickListener());
    }

    private final class AddItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View clickedView) {
            addNewItem();
        }
    }

    @UiThread
    private void addNewItem() {
        final String itemText = binding.newItemText.getText().toString();
        if (!TextUtils.isEmpty(itemText)) {
            ChecklistItem item = new ChecklistItem();item.description = itemText;
            itemsDao.addItem(item);
            binding.newItemText.setText("");
        }
    }
}