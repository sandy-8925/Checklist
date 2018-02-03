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
public final class ChecklistActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ChecklistItem>> {

    /**
     * These constants are used to specify whether the ItemDescriptionEntryActivity is being opened
     * to add a new list item or edit an existing list item
     */
    static final int EDIT_ITEM_ACTION = 6;
    private static final int CHECKLIST_ITEMS_CURSOR_LOADER_ID = 1;

    private ChecklistItemRecyclerAdapter itemListAdapter;
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
    private boolean shouldScrollToBottom;
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

        setupItemsListUI();
        registerForContextMenu(binding.itemsList);

        getSupportLoaderManager().initLoader(CHECKLIST_ITEMS_CURSOR_LOADER_ID, null, this);
        setupItemAddUI();
    }

    @UiThread
    private void setupItemAddUI() {
        binding.newItemText.setOnEditorActionListener(inputEntryTextDoneListener);
        binding.newItemAddButton.setOnClickListener(new AddItemOnClickListener());
    }

    @UiThread
    private void setupItemsListUI() {
        binding.itemsList.setLayoutManager(new LinearLayoutManager(this));
        itemListAdapter = new ChecklistItemRecyclerAdapter();
        itemListAdapter.setOnItemClickListener(new ChecklistItemRecyclerAdapter.ItemClickListener() {
            @Override
            void onClick(View view, long itemId) {
                itemsDao.flipStatus(itemId);
            }
        });
        itemListAdapter.setItemLongClickListener(new ChecklistItemRecyclerAdapter.ItemLongClickListener() {
            @Override
            void onLongClick(View view, long itemId) {
                showItemPopupMenu(view, itemId);
            }
        });
        binding.itemsList.setAdapter(itemListAdapter);

    }

    @UiThread
    private void showItemPopupMenu(View view, final long itemId) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.checklist_context_menu);
        popupMenu.setOnMenuItemClickListener(new ItemPopupMenuClickListener(itemId));
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checklist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_delcheckeditems:
            itemsDao.deleteCheckedItems();
            return true;

        case R.id.menu_checkall:
            itemsDao.checkAllItems();
            return true;

        case R.id.menu_uncheckall:
            itemsDao.uncheckAllItems();
            return true;

        case R.id.menu_reverseall:
            itemsDao.flipAllItems();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.checklist_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuItem
                .getMenuInfo();
        switch (menuItem.getItemId()) {
        case R.id.context_menu_delete:
            itemsDao.deleteItem(info.id);
            return true;

        case R.id.context_menu_edit:
            final Intent editItemIntent = new Intent(this,
                    ItemDescriptionEntryActivity.class);
            editItemIntent.putExtra(actionTag, EDIT_ITEM_ACTION);
            editItemIntent.putExtra(ItemDescriptionEntryActivity.EXTRA_KEY_ITEM_ID, info.id);
            startActivity(editItemIntent);
            return true;

        default:
            return super.onContextItemSelected(menuItem);
        }
    }

    @Override
    public Loader<List<ChecklistItem>> onCreateLoader(int cursorId, Bundle bundle) {
        switch(cursorId) {
            case CHECKLIST_ITEMS_CURSOR_LOADER_ID:
                return new ChecklistItemsCursorLoader(getApplicationContext());

            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<ChecklistItem>> cursorLoader, List<ChecklistItem> itemList) {
        itemListAdapter.setItems(itemList);
        if(shouldScrollToBottom) {
            shouldScrollToBottom = false;
            binding.itemsList.smoothScrollToPosition(itemListAdapter.getItemCount());
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ChecklistItem>> cursorLoader) {
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
            shouldScrollToBottom = true;
            binding.newItemText.setText("");
        }
    }

    private class ItemPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener {
        private final long itemId;

        ItemPopupMenuClickListener(long itemId) {
            this.itemId = itemId;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.context_menu_delete:
                    itemsDao.deleteItem(itemId);
                    return true;

                case R.id.context_menu_edit:
                    final Intent editItemIntent = new Intent(ChecklistActivity.this,
                            ItemDescriptionEntryActivity.class);
                    editItemIntent.putExtra(actionTag, EDIT_ITEM_ACTION);
                    editItemIntent.putExtra(ItemDescriptionEntryActivity.EXTRA_KEY_ITEM_ID, itemId);
                    startActivity(editItemIntent);
                    return true;
            }
            return false;
        }
    }
}