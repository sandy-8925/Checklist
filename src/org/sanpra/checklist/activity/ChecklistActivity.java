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

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import org.sanpra.checklist.R;
import org.sanpra.checklist.dbhelper.ItemsDbHelper;

public final class ChecklistActivity extends Activity {

    //TODO: See if enum can be used instead of int for specifying action to ItemDescriptionEntryActivity
    public static final int NEW_ITEM_ACTION = 5;
    public static final int EDIT_ITEM_ACTION = 6;

    private final static String[] from =  new String[] { ItemsDbHelper.COL_DESC };
    private final static int[] to = new int[] { R.id.itemtext };

    private ItemsDbHelper mDbHelper;
    private Cursor mItemsCursor;
    private BaseAdapter itemListAdapter;

    private class ChecklistItemAdapter extends SimpleCursorAdapter {
        private int CHECKLIST_ITEM_UNCHECKED_COLOUR;
        private int CHECKLIST_ITEM_CHECKED_COLOUR;

        ChecklistItemAdapter() {
            super(ChecklistActivity.this, R.layout.item_row, mItemsCursor,
                    from, to);
            Resources appResources = getResources();
            CHECKLIST_ITEM_UNCHECKED_COLOUR = appResources.getInteger(R.integer.white);
            CHECKLIST_ITEM_CHECKED_COLOUR = appResources.getInteger(R.integer.grey);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = super.getView(position, convertView, parent);

            TextView itemText = (TextView) item.findViewById(R.id.itemtext);
            long itemRowId = getItemId(position);

            int itemColor = (mDbHelper.getItemStatus(itemRowId) == 0) ? CHECKLIST_ITEM_UNCHECKED_COLOUR
                    : CHECKLIST_ITEM_CHECKED_COLOUR;
            itemText.setTextColor(itemColor);

            return item;
        }

    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);

        // create database helper object and fetch all checklist items from
        // database
        mDbHelper = new ItemsDbHelper(this);
        mDbHelper.open();
        mItemsCursor = mDbHelper.fetchAllItems();

        // manage cursor ; create cursor adapter and use it
        startManagingCursor(mItemsCursor);
        /*
         * use requery to refresh cursor data in other methods
         * use notifyDataSetChanged() to refresh view/adapter
         */
        itemListAdapter = new ChecklistItemAdapter();
        ListView itemsListView = (ListView) findViewById(R.id.items_list);
        itemsListView.setAdapter(itemListAdapter);
        itemsListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDbHelper.flipStatus(id);
                refreshChecklistDataAndView();
            }
        });

        registerForContextMenu(itemsListView);
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
        case R.id.menu_new_item:
            Intent createItemIntent = new Intent(this,
                    ItemDescriptionEntryActivity.class);
            createItemIntent.putExtra("action", NEW_ITEM_ACTION);
            startActivity(createItemIntent);
            return true;

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

    /*
     * TODO: Use CursorLoader (introduced in Android 3.0) so that data is automatically refreshed
     */
    private void refreshChecklistDataAndView() {
        mItemsCursor.requery();
        itemListAdapter.notifyDataSetChanged();
    }

    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.checklist_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuItem
                .getMenuInfo();
        switch (menuItem.getItemId()) {
        case R.id.context_menu_delete:
            mDbHelper.deleteItem(info.id);
            refreshChecklistDataAndView();
            return true;

        case R.id.context_menu_edit:
            Intent editItemIntent = new Intent(this,
                    ItemDescriptionEntryActivity.class);
            editItemIntent.putExtra("action", EDIT_ITEM_ACTION);
            editItemIntent.putExtra("item_id", info.id);
            startActivity(editItemIntent);
            return true;

        default:
            return super.onContextItemSelected(menuItem);
        }
    }
}