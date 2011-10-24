package com.android.checklist;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ChecklistActivity extends ListActivity
{
	
	private ItemsDbHelper mDbHelper;
	private Cursor mItemsCursor; 
	private static final int CREATE_ITEM=1;
	
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
        String[] from = new String[] {ItemsDbHelper.COL_DESC};
        int[] to = new int[] {R.id.itemrow};
        ListAdapter itemListAdapter = new SimpleCursorAdapter(this, R.layout.item_row, mItemsCursor, from, to);
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
    	case R.id.show_row_ids:
    		Cursor allItems = mDbHelper.fetchAllItems();
    		String rowIDs = new String("");
    		if(allItems.moveToFirst())
    			rowIDs = rowIDs + allItems.getString(allItems.getColumnIndex(mDbHelper.COL_ID)) + "\n";
    		while(allItems.moveToNext())
    			rowIDs = rowIDs + allItems.getString(allItems.getColumnIndex(mDbHelper.COL_ID)) + "\n";
    		Toast.makeText(this, rowIDs,Toast.LENGTH_LONG).show();
    		return true;
    	}
    	

    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent intent)
    {
    	switch(requestCode)
    	{
    	case CREATE_ITEM:
    		//get item description string returned by activity 
    		Bundle extras = intent.getExtras();
    		String itemDesc = extras.getString(ItemsDbHelper.COL_DESC);
    		
    		//add item to database
    		mDbHelper.addItem(itemDesc);
    		
    		//refresh the display so new item shows up
    		mItemsCursor.requery();
    		SimpleCursorAdapter itemsAdapter = (SimpleCursorAdapter) getListAdapter();
    		itemsAdapter.notifyDataSetChanged();
    		break;
    	}
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {    	
    	int result = mDbHelper.flipStatus(id);  	
    	Toast.makeText(this, "" + result + "\n" + id, Toast.LENGTH_SHORT).show();    //for debugging purposes only. to be removed later 	
    	mItemsCursor.requery();
    	
    	SimpleCursorAdapter itemsAdapter = (SimpleCursorAdapter) getListAdapter();
		itemsAdapter.notifyDataSetChanged();
	}
}