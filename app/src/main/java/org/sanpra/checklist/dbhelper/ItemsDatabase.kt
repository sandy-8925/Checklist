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

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context

import org.apache.commons.lang3.StringUtils
import org.sanpra.checklist.activity.ChecklistItem
import org.sanpra.checklist.activity.ItemsDao

@Database(entities = arrayOf(ChecklistItem::class), version = 2)
abstract class ItemsDatabase : RoomDatabase() {

    abstract fun itemsDao(): ItemsDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val CREATE_ITEMS_COPY_TABLE = "create table items_copy(_id integer," + " desc text, checked integer);"
                database.execSQL(CREATE_ITEMS_COPY_TABLE)
                database.execSQL("insert into items_copy select * from items;")
                database.execSQL("drop table items;")
                val RECREATE_ITEMS_TABLE = "create table items(_id integer primary key autoincrement not null," + " desc text, checked integer not null);"
                database.execSQL(RECREATE_ITEMS_TABLE)
                database.execSQL("insert into items select * from items_copy;")
                database.execSQL("drop table items_copy;")
            }
        }

        private var INSTANCE: ItemsDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ItemsDatabase {
            var localInstance = INSTANCE
            if (localInstance == null) {
                localInstance = Room.databaseBuilder(context, ItemsDatabase::class.java, ItemsDbHelper.DbHelper.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .allowMainThreadQueries()
                        .build()
                INSTANCE = localInstance
            }
            return localInstance
        }
    }
}