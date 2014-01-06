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

package org.sanpra.checklist.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDbHelper 
{
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
		private static final String DATABASE_NAME="data";
		private static final int DATABASE_VERSION=1;
		
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
		return mDatabase.query(TABLE_NAME, null, null, null, null, null, null);
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
	
	public void deleteItem(long id)
	{
		mDatabase.delete(TABLE_NAME, "_id="+id, null);
	}
	
	public String getItemDesc(long id)
	{
		Cursor itemDescQuery = mDatabase.query(TABLE_NAME, new String[] {COL_DESC}, "_id="+id, null, null, null, null);
		itemDescQuery.moveToFirst();
		return itemDescQuery.getString(itemDescQuery.getColumnIndex(COL_DESC));
	}
	
	public void editItemDesc(long id, String newItemDesc)
	{
		ContentValues values = new ContentValues();
		values.put(COL_DESC, newItemDesc);
		mDatabase.update(TABLE_NAME, values, "_id="+id, null);
	}
}
