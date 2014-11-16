/*
 * Copyright (C) 2011-2012 Sandeep Raghuraman (sandy.8925@gmail.com)

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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.support.v4.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.sanpra.checklist.R;
import org.sanpra.checklist.dbhelper.ChecklistItemsCursorLoader;
import org.sanpra.checklist.dbhelper.ItemsDbHelper;

/**
 * Main activity, that is displayed when the app is launched. Displays the list of ToDo items.
 */
public final class ChecklistActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //TODO: See if enum can be used instead of int for specifying action to ItemDescriptionEntryActivity
    /**
     * These constants are used to specify whether the ItemDescriptionEntryActivity is being opened
     * to add a new list item or edit an existing list item
     */
    static final int EDIT_ITEM_ACTION = 6;
    private static final int CHECKLIST_ITEMS_CURSOR_LOADER_ID = 1;

    private ItemsDbHelper mDbHelper;
    /**
     * Cursor holding query results for all items in the list
     */
    private Cursor mItemsCursor;
    private CursorAdapter itemListAdapter;
    static final String actionTag = "actionTag";

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);

        // create database helper object and fetch all checklist items from
        // database
        mDbHelper = ItemsDbHelper.getInstance(this);
        mItemsCursor = mDbHelper.fetchAllItems();

        // manage cursor ; create cursor adapter and use it
        startManagingCursor(mItemsCursor);
        /*
         * use requery to refresh cursor data in other methods
         * use notifyDataSetChanged() to refresh view/adapter
         */
        itemListAdapter = new ChecklistItemAdapter(this, mItemsCursor);
        final ListView itemsListView = (ListView) findViewById(R.id.items_list);
        itemsListView.setAdapter(itemListAdapter);
        itemsListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDbHelper.flipStatus(id);
                refreshChecklistDataAndView();
            }
        });

        registerForContextMenu(itemsListView);

        findViewById(R.id.new_item_add_button).setOnClickListener(new AddItemOnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu i.e create the menu from a menu layout file
            getMenuInflater().inflate(R.menu.checklist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_delcheckeditems:
            mDbHelper.deleteCheckedItems();
            refreshChecklistDataAndView();
            return true;

        case R.id.menu_checkall:
            mDbHelper.checkAllItems();
            refreshChecklistDataAndView();
            return true;

        case R.id.menu_uncheckall:
            mDbHelper.uncheckAllItems();
            refreshChecklistDataAndView();
            return true;

        case R.id.menu_reverseall:
            mDbHelper.flipAllItems();
            refreshChecklistDataAndView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Use CursorLoader (introduced in Android 3.0) so that data is automatically refreshed
    /**
     * Notifies different components that list data has changed, so that the new data can be displayed to the user
     */
    private void refreshChecklistDataAndView() {
        mItemsCursor.requery();
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
            mDbHelper.deleteItem(info.id);
            refreshChecklistDataAndView();
            return true;

        case R.id.context_menu_edit:
            final Intent editItemIntent = new Intent(this,
                    ItemDescriptionEntryActivity.class);
            editItemIntent.putExtra(actionTag, EDIT_ITEM_ACTION);
            editItemIntent.putExtra("item_id", info.id);
            startActivity(editItemIntent);
            return true;

        default:
            return super.onContextItemSelected(menuItem);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int cursorId, Bundle bundle) {
        switch(cursorId) {
            case CHECKLIST_ITEMS_CURSOR_LOADER_ID:
                return new ChecklistItemsCursorLoader(this);

            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        itemListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        itemListAdapter.swapCursor(null);
    }

    private final class AddItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View clickedView) {
            final EditText newItemEditTextBox = (EditText) clickedView.getRootView().findViewById(R.id.new_item_text);
            if(newItemEditTextBox == null) {
                Toast.makeText(clickedView.getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            final String itemText = newItemEditTextBox.getText().toString();
            if (itemText.length() != 0) {
                mDbHelper.addItem(itemText);
                refreshChecklistDataAndView();
                newItemEditTextBox.setText("");
            }
        }
    }
}