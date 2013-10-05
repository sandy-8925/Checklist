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

package com.android.checklist.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.android.checklist.ItemsDbHelper;
import com.android.checklist.R;

public class ChecklistActivity extends ListActivity {

	private ItemsDbHelper mDbHelper;
	private Cursor mItemsCursor;
	String[] from;
	int[] to;
	public static final int NEWITEMACTION = 5;
	public static final int EDITITEMACTION = 6;

	class ChecklistItemAdapter extends SimpleCursorAdapter {
		// TODO: Check if these colours are better specified in an Android
		// resource
		private static final int CHECKLIST_ITEM_UNCHECKED_COLOUR = 0xFFFFFFFF;
		private static final int CHECKLIST_ITEM_CHECKED_COLOUR = 0x88888888;

		ChecklistItemAdapter() {
			super(ChecklistActivity.this, R.layout.item_row, mItemsCursor,
					from, to);
		}

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
		 * use requery to refresh cursor data in other methods use
		 * notifyDataSetChanged() to refresh view/adapter
		 */
		from = new String[] { ItemsDbHelper.COL_DESC };
		to = new int[] { R.id.itemtext };
		ListAdapter itemListAdapter = new ChecklistItemAdapter();
		setListAdapter(itemListAdapter);

		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflate menu i.e create the menu from a menu layout file
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.checklist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new_item:
			Intent createItemIntent = new Intent(this,
					ItemDescriptionEntryActivity.class);
			createItemIntent.putExtra("action", NEWITEMACTION);
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

	private void refreshChecklistDataAndView() {
		mItemsCursor.requery();
		((SimpleCursorAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checklist_context_menu, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.context_menu_delete:
			mDbHelper.deleteItem(info.id);
			refreshChecklistDataAndView();
			return true;

		case R.id.context_menu_edit:
			Intent editItemIntent = new Intent(this,
					ItemDescriptionEntryActivity.class);
			editItemIntent.putExtra("action", EDITITEMACTION);
			editItemIntent.putExtra("item_id", info.id);
			startActivity(editItemIntent);
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mDbHelper.flipStatus(id);

		refreshChecklistDataAndView();
	}
}