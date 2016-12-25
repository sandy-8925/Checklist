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

package org.sanpra.checklist.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.Locale;

/*
 TODO: Create interfaces that encapsulate sets of operations like adding, editing and removing items.
 These can be used to refer to ItemsDbHelper instance in code, while only allowing access to specific operations.
 It also eases creation of mock classes and testing.
*/
/**
 * Database helper class, used for manipulating database storing checklist information.
 * The constructor is private. Instead, use the method getInstance() to get a reference to an
 * instance of this class.
 */
public final class ItemsDbHelper
{
    private static ItemsDbHelper INSTANCE = null;

	private static final String TABLE_NAME="items";
	private static final String COL_ID="_id";
    /**
     * Column name for checklist item text in the Sqlite DB table
     */
	public static final String COL_DESC="desc";
	private static final String COL_STATUS="checked";

	private SQLiteDatabase mDatabase;
	
	private static final class DbHelper extends SQLiteOpenHelper
	{
		private static final String DATABASE_NAME="data";
		private static final int DATABASE_VERSION=1;
		
		public DbHelper(final Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);			
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
            final String CREATE_DATABASE="create table items(_id integer primary key autoincrement," +
                    " desc text not null, checked integer not null);";
			db.execSQL(CREATE_DATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			//no upgrade as of now
		}	
	}
	
	private final SQLiteOpenHelper mDbHelper;
	
	private ItemsDbHelper(final Context context)
	{
        mDbHelper = new DbHelper(context);
	}

	private void open()
	{
		mDatabase = mDbHelper.getWritableDatabase();		
	}

    /**
     * Creates an instance of ItemsDbHelper if not already created, and returns it. Use this instead
     * of the constructor.
     * @param context The android.content.Context object to be used for creating the database
     * @return A single application-wide instance of ItemsDbHelper
     */
    public static ItemsDbHelper getInstance(final Context context) {
        if(INSTANCE == null) {
            INSTANCE = new ItemsDbHelper(context);
            INSTANCE.open();
        }
        return INSTANCE;
    }

    /**
     * Queries the database for all items in the checklist - fetches every row in the table
     * @return An android.database.Cursor object containing the results of the query
     */
	Cursor fetchAllItems()
	{
		return mDatabase.query(TABLE_NAME, null, null, null, null, null, null);
	}

    /**
     * Adds a checklist item to the database
     * @param itemDesc The text of the item to be added
     */
	public void addItem(final String itemDesc)
	{
		final ContentValues values = new ContentValues();
		values.put(COL_DESC, itemDesc);
		values.put(COL_STATUS, 0);
		mDatabase.insert(TABLE_NAME, null, values);
	}

    /**
     * Flips the status of an item from unchecked to checked, and vice versa
     * @param id The ID of the item to be checked/unchecked
     */
	public void flipStatus(final long id)
	{
		final Cursor item = mDatabase.query(TABLE_NAME, new String[] {COL_STATUS}, buildIdSelectString(id), null, null, null, null);
		item.moveToFirst();
		final int currentStatus = item.getInt(item.getColumnIndex(COL_STATUS));
		item.close();
		final int newStatus = 1 - currentStatus;
		final ContentValues values = new ContentValues();
		values.put(COL_STATUS, newStatus);
		mDatabase.update(TABLE_NAME, values, buildIdSelectString(id), null);
	}
	
	private int getItemStatus(final long id)
	{
		final Cursor item = mDatabase.query(TABLE_NAME, new String[] {COL_STATUS}, buildIdSelectString(id), null, null, null, null);
		item.moveToFirst();
		final int status = item.getInt(item.getColumnIndex(COL_STATUS));
		item.close();
		return status;
	}

    /**
     * Returns the checked/unchecked status of a checklist item
     * @param id The ID of the checklist item
     * @return Returns true if the item is checked, false otherwise
     */
    public boolean isItemChecked(final long id) {
        return (getItemStatus(id) == 1);
    }

    /**
     * Deletes all checked items from the table
     */
	public void deleteCheckedItems()
	{
		mDatabase.delete(TABLE_NAME, COL_STATUS + "=1", null);		
	}

    /**
     * Marks all of the checklist items as checked in the database
     */
	public void checkAllItems()
	{
        final int CHECKED_STATUS = 1;
        setStatusOfAllItems(CHECKED_STATUS);
	}

    /**
     * Sets the status of all checklist items in the database to the given status
     * @param newStatus The new status to be set
     */
    private void setStatusOfAllItems(final int newStatus) {
        final ContentValues newValue = new ContentValues();
        newValue.put(COL_STATUS, newStatus);
        mDatabase.update(TABLE_NAME, newValue, null, null);
    }

    /**
     * Marks all of the checklist items as unchecked in the database
     */
	public void uncheckAllItems()
	{
        final int UNCHECKED_STATUS = 0;
		setStatusOfAllItems(UNCHECKED_STATUS);
	}

    /**
     * Flips the status of all of the checklist items in the database
     */
	public void flipAllItems()
	{
		final Cursor items = mDatabase.query(TABLE_NAME, new String[] {COL_ID,COL_STATUS}, null, null, null, null, null);
		items.moveToFirst();

		do
		{
			final int currentStatus = items.getInt(items.getColumnIndex(COL_STATUS));
			final long id = items.getLong(items.getColumnIndex(COL_ID));
			final int newStatus = 1 - currentStatus;
			final ContentValues values = new ContentValues();
			values.put(COL_STATUS, newStatus);
			mDatabase.update(TABLE_NAME, values, buildIdSelectString(id), null);
		} while(items.moveToNext());

		items.close();
	}

    /**
     * Deletes the checklist item with the given ID
     * @param id The ID of the checklist item to be deleted
     */
	public void deleteItem(final long id)
	{
		mDatabase.delete(TABLE_NAME, buildIdSelectString(id), null);
	}

    /**
     * Returns the description text of the item with the given ID
     * @param id Checklist item ID
     * @return A Java String object containing the item description
     */
	public String getItemDesc(final long id)
	{
		final Cursor itemDescQuery = mDatabase.query(TABLE_NAME, new String[] {COL_DESC}, buildIdSelectString(id), null, null, null, null);
		itemDescQuery.moveToFirst();
		final String itemDesc = itemDescQuery.getString(itemDescQuery.getColumnIndex(COL_DESC));
		itemDescQuery.close();
		return itemDesc;
	}

    /**
     * Changes the description text of the item with the given ID, to the text passed in
     * @param id The ID of the checklist item to be modified
     * @param newItemDesc The new description text for the given item
     */
	public void editItemDesc(final long id, final String newItemDesc)
	{
		final ContentValues values = new ContentValues();
		values.put(COL_DESC, newItemDesc);
		mDatabase.update(TABLE_NAME, values, buildIdSelectString(id), null);
	}

    /**
     * Builds a SQL-style query string for the ID column
     * @param id Checklist item ID
     * @return "_id=$id"  where $id means the value of parameter id
     */
    static String buildIdSelectString(final long id) {
        return String.format(Locale.US, "%s=%d", COL_ID, id);
    }

    public static String getItemDesc(@NonNull Cursor dbCursor) {
		return dbCursor.getString(dbCursor.getColumnIndex(COL_DESC));
	}

	public static boolean isItemChecked(@NonNull Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(COL_STATUS)) != 0;
	}

	public static int getItemId(@NonNull Cursor dbCursor) {
		return dbCursor.getInt(dbCursor.getColumnIndex(COL_ID));
	}
}
