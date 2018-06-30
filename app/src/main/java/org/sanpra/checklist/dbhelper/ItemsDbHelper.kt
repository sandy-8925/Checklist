/*
 * Copyright (C) 2011-2018 Sandeep Raghuraman (sandy.8925@gmail.com)

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

package org.sanpra.checklist.dbhelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.WorkerThread

import org.apache.commons.lang3.BooleanUtils

import java.util.Locale

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
class ItemsDbHelper private constructor(context: Context) {

    private var mDatabase: SQLiteDatabase? = null

    private val mDbHelper: SQLiteOpenHelper

    internal class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            val CREATE_DATABASE = "create table items(_id integer primary key autoincrement," + " desc text not null, checked integer not null);"
            db.execSQL(CREATE_DATABASE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            //no upgrade as of now
        }

        companion object {
            val DATABASE_NAME = "data"
            private val DATABASE_VERSION = 1
        }
    }

    init {
        mDbHelper = DbHelper(context.applicationContext)
    }

    private fun open() {
        mDatabase = mDbHelper.writableDatabase
    }

    /**
     * Queries the database for all items in the checklist - fetches every row in the table
     * @return An android.database.Cursor object containing the results of the query
     */
    @WorkerThread
    fun fetchAllItems(): Cursor {
        return mDatabase!!.query(TABLE_NAME, null, null, null, null, null, null)
    }

    /**
     * Adds a checklist item to the database
     * @param itemDesc The text of the item to be added
     */
    @WorkerThread
    fun addItem(itemDesc: String) {
        val values = ContentValues()
        values.put(COL_DESC, itemDesc)
        values.put(COL_STATUS, 0)
        mDatabase!!.insert(TABLE_NAME, null, values)
    }

    /**
     * Flips the status of an item from unchecked to checked, and vice versa
     * @param id The ID of the item to be checked/unchecked
     */
    @WorkerThread
    fun flipStatus(id: Long) {
        val item = mDatabase!!.query(TABLE_NAME, arrayOf(COL_STATUS), buildIdSelectString(id), null, null, null, null)
        item.moveToFirst()
        val currentStatus = item.getInt(item.getColumnIndex(COL_STATUS))
        item.close()
        val newStatus = 1 - currentStatus
        val values = ContentValues()
        values.put(COL_STATUS, newStatus)
        mDatabase!!.update(TABLE_NAME, values, buildIdSelectString(id), null)
    }

    /**
     * Deletes all checked items from the table
     */
    @WorkerThread
    fun deleteCheckedItems() {
        mDatabase!!.delete(TABLE_NAME, "$COL_STATUS=1", null)
    }

    /**
     * Marks all of the checklist items as checked in the database
     */
    @WorkerThread
    fun checkAllItems() {
        val CHECKED_STATUS = 1
        setStatusOfAllItems(CHECKED_STATUS)
    }

    /**
     * Sets the status of all checklist items in the database to the given status
     * @param newStatus The new status to be set
     */
    @WorkerThread
    private fun setStatusOfAllItems(newStatus: Int) {
        val newValue = ContentValues()
        newValue.put(COL_STATUS, newStatus)
        mDatabase!!.update(TABLE_NAME, newValue, null, null)
    }

    /**
     * Marks all of the checklist items as unchecked in the database
     */
    fun uncheckAllItems() {
        val UNCHECKED_STATUS = 0
        setStatusOfAllItems(UNCHECKED_STATUS)
    }

    /**
     * Flips the status of all of the checklist items in the database
     */
    @WorkerThread
    fun flipAllItems() {
        val items = mDatabase!!.query(TABLE_NAME, arrayOf(COL_ID, COL_STATUS), null, null, null, null, null)
        items.moveToFirst()

        do {
            val currentStatus = items.getInt(items.getColumnIndex(COL_STATUS))
            val id = items.getLong(items.getColumnIndex(COL_ID))
            val newStatus = 1 - currentStatus
            val values = ContentValues()
            values.put(COL_STATUS, newStatus)
            mDatabase!!.update(TABLE_NAME, values, buildIdSelectString(id), null)
        } while (items.moveToNext())

        items.close()
    }

    /**
     * Deletes the checklist item with the given ID
     * @param id The ID of the checklist item to be deleted
     */
    @WorkerThread
    fun deleteItem(id: Long) {
        mDatabase!!.delete(TABLE_NAME, buildIdSelectString(id), null)
    }

    /**
     * Returns the description text of the item with the given ID
     * @param id Checklist item ID
     * @return A Java String object containing the item description
     */
    fun getItemDesc(id: Long): String {
        val itemDescQuery = mDatabase!!.query(TABLE_NAME, arrayOf(COL_DESC), buildIdSelectString(id), null, null, null, null)
        itemDescQuery.moveToFirst()
        val itemDesc = itemDescQuery.getString(itemDescQuery.getColumnIndex(COL_DESC))
        itemDescQuery.close()
        return itemDesc
    }

    /**
     * Changes the description text of the item with the given ID, to the text passed in
     * @param id The ID of the checklist item to be modified
     * @param newItemDesc The new description text for the given item
     */
    @WorkerThread
    fun editItemDesc(id: Long, newItemDesc: String) {
        val values = ContentValues()
        values.put(COL_DESC, newItemDesc)
        mDatabase!!.update(TABLE_NAME, values, buildIdSelectString(id), null)
    }

    companion object {
        @Volatile
        private var INSTANCE: ItemsDbHelper? = null

        const val TABLE_NAME = "items"
        private val COL_ID = "_id"
        /**
         * Column name for checklist item text in the Sqlite DB table
         */
        private val COL_DESC = "desc"
        private val COL_STATUS = "checked"

        /**
         * Creates an instance of ItemsDbHelper if not already created, and returns it. Use this instead
         * of the constructor.
         * @param context The android.content.Context object to be used for creating the database
         * @return A single application-wide instance of ItemsDbHelper
         */
        @Synchronized
        fun getInstance(context: Context): ItemsDbHelper? {
            if (INSTANCE == null) {
                INSTANCE = ItemsDbHelper(context)
                INSTANCE!!.open()
            }
            return INSTANCE
        }

        /**
         * Builds a SQL-style query string for the ID column
         * @param id Checklist item ID
         * @return "_id=$id"  where $id means the value of parameter id
         */
        internal fun buildIdSelectString(id: Long): String {
            return String.format(Locale.US, "%s=%d", COL_ID, id)
        }

        fun getItemDesc(dbCursor: Cursor): String {
            return dbCursor.getString(dbCursor.getColumnIndex(COL_DESC))
        }

        fun isItemChecked(cursor: Cursor): Boolean {
            return BooleanUtils.toBoolean(cursor.getInt(cursor.getColumnIndex(COL_STATUS)))
        }

        fun getItemId(dbCursor: Cursor): Int {
            return dbCursor.getInt(dbCursor.getColumnIndex(COL_ID))
        }
    }
}
