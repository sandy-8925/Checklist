package com.android.checklist;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ChecklistActivity extends ListActivity
{
	
	private ItemsDbHelper mDbHelper;
	private Cursor mItemsCursor; 
	private static final int CREATE_ITEM=1;
	private static final int DELETE_CHECKED_ITEMS=2;
	String[] from;
    int[] to;
	
	class ChecklistItemAdapter extends SimpleCursorAdapter
	{
		ChecklistItemAdapter() {
			super(ChecklistActivity.this, R.layout.item_row, mItemsCursor, from, to);
		}		
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View item = super.getView(position, convertView, parent);
			
			TextView itemText = (TextView) item.findViewById(R.id.itemtext);			
			long itemRowId = getItemId(position);
			
			int itemCheckedColor = 0x88888888;
			int itemUncheckedColor = 0xFFFFFFFF;	
			int itemColor = (mDbHelper.getItemStatus(itemRowId)==0)? itemUncheckedColor:itemCheckedColor;		
			itemText.setTextColor(itemColor);
			
			return item;
		}
		
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);
        
        //create database helper object and fetch all checklist items from database
        mDbHelper = new ItemsDbHelper(this);
        mDbHelper.open();
        mItemsCursor = mDbHelper.fetchAllItems();
        
        //manage cursor ; create cursor adapter and use it
        startManagingCursor(mItemsCursor); 
        /*
         * use requery to refresh cursor data in other methods
         * use notifyDataSetChanged() to refresh view/adapter
         */
        from = new String[] {ItemsDbHelper.COL_DESC};
        to = new int[] {R.id.itemtext};
        ListAdapter itemListAdapter = new ChecklistItemAdapter();
        setListAdapter(itemListAdapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	//inflate menu i.e create the menu from a menu layout file
    	MenuInflater menuInflater = getMenuInflater();
    	menuInflater.inflate(R.menu.checklist_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.menu_new_item:    		    		
    		Intent createItemIntent = new Intent(this, ItemDescriptionEntryActivity.class);
    		startActivityForResult(createItemIntent, CREATE_ITEM);
    		return true;
    		
    	case R.id.menu_delcheckeditems:
    		mDbHelper.deleteCheckedItems();
    		mItemsCursor.requery();        	
        	SimpleCursorAdapter itemsAdapter = (SimpleCursorAdapter) getListAdapter();
    		itemsAdapter.notifyDataSetChanged();
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent intent)
    {
    	super.onActivityResult(requestCode, resultCode, intent);
    	if(resultCode == RESULT_CANCELED)
    		return;
    	
    	switch(requestCode)
    	{
    	case CREATE_ITEM:
    		//get item description string returned by activity
    		Bundle extras = intent.getExtras();    		
    		    		
    		if(extras == null)
    			break;
    		
    		String itemDesc = extras.getString(ItemsDbHelper.COL_DESC);
    		
    		if(itemDesc.length() != 0 )
    		{
    		//add item to database
    		mDbHelper.addItem(itemDesc);
    		
    		//refresh the display so new item shows up
    		mItemsCursor.requery();
    		SimpleCursorAdapter itemsAdapter = (SimpleCursorAdapter) getListAdapter();
    		itemsAdapter.notifyDataSetChanged();
    		}
    		break;
    	}
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {    	
    	int result = mDbHelper.flipStatus(id);  	
    	//Toast.makeText(this, "" + result + "\n" + id, Toast.LENGTH_SHORT).show();    //for debugging purposes only. to be removed later 	
    	mItemsCursor.requery();
    	
    	SimpleCursorAdapter itemsAdapter = (SimpleCursorAdapter) getListAdapter();
		itemsAdapter.notifyDataSetChanged();
	}
}