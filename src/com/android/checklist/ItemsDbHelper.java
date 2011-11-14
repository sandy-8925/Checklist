package com.android.checklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDbHelper 
{
	private static final String DATABASE_NAME="data";
	private static int DATABASE_VERSION=1;
	private static final String TABLE_NAME="items";
	public static final String COL_ID="_id";
	public static final String COL_DESC="desc";
	public static final String COL_STATUS="checked";
	
	private static final String CREATE_DATABASE="create table items(_id integer primary key autoincrement," +
			" desc text not null, checked integer not null);";
	private Context mContext;
	private SQLiteDatabase mDatabase;
	
	private class DbHelper extends SQLiteOpenHelper
	{
		
		public DbHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);			
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			//create the table
			db.execSQL(CREATE_DATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			//no upgrade as of now
		}	
	}
	
	private DbHelper mDbHelper;
	
	public ItemsDbHelper(Context context)
	{
		mContext = context;
	}

	public void open()
	{
		mDbHelper = new DbHelper(mContext);
		mDatabase = mDbHelper.getWritableDatabase();		
	}
	
	public Cursor fetchAllItems()
	{
		Cursor c = mDatabase.query(TABLE_NAME, null, null, null, null, null, null);
		return c;
	}
	
	public void addItem(String itemDesc)
	{
		ContentValues values = new ContentValues();
		values.put(COL_DESC, itemDesc);
		values.put(COL_STATUS, 0);
		mDatabase.insert(TABLE_NAME, null, values);
	}
	
	/*
	 * Flips the status of an item i from unchecked to checked and vice versa and returns the new status
	 */
	public void flipStatus(long id)
	{
		Cursor item = mDatabase.query(TABLE_NAME, new String[] {COL_STATUS}, "_id=" + id, null, null, null, null);
		item.moveToFirst();
		int status = item.getInt(item.getColumnIndex(COL_STATUS));
		status = 1 - status;		
		ContentValues values = new ContentValues();
		values.put(COL_STATUS, status);
		mDatabase.update(TABLE_NAME, values, "_id="+id, null);		
	}
	
	public int getItemStatus(long id)
	{
		Cursor item = mDatabase.query(TABLE_NAME, new String[] {COL_STATUS}, "_id=" + id, null, null, null, null);
		item.moveToFirst();
		int status = item.getInt(item.getColumnIndex(COL_STATUS));		
		return status;
	}
	
	public void deleteCheckedItems()
	{
		mDatabase.delete(TABLE_NAME, COL_STATUS + "=1", null);		
	}
	
	public void checkAllItems()
	{
		ContentValues newValue = new ContentValues();
		newValue.put(COL_STATUS, 1);
		mDatabase.update(TABLE_NAME, newValue, null, null);
	}
	
	public void uncheckAllItems()
	{
		ContentValues newValue = new ContentValues();
		newValue.put(COL_STATUS, 0);
		mDatabase.update(TABLE_NAME, newValue, null, null);
	}
	
	public void flipAllItems()
	{
		int status;
		long id;
		Cursor items = mDatabase.query(TABLE_NAME, new String[] {COL_ID,COL_STATUS}, null, null, null, null, null);
		
		items.moveToFirst();
		do
		{
			status = items.getInt(items.getColumnIndex(COL_STATUS));
			id = items.getLong(items.getColumnIndex(COL_ID));
			status = 1 - status;		
			ContentValues values = new ContentValues();
			values.put(COL_STATUS, status);
			mDatabase.update(TABLE_NAME, values, "_id="+id, null);			
		}while(items.moveToNext());
	}
	
}
