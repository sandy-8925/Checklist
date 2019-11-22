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

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.apache.commons.lang3.BooleanUtils

private const val DATABASE_NAME = "data"
private const val TABLE_NAME = "items"

private const val COLUMN_ID = "_id"
private const val COLUMN_DESC = "desc"
private const val COLUMN_CHECKED = "checked"

@Entity(tableName = TABLE_NAME)
class ChecklistItem {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    var id: Long = 0

    @ColumnInfo(name = COLUMN_DESC)
    var description: String? = null
    @ColumnInfo(name = COLUMN_CHECKED)
    @TypeConverters(Converter::class)
    var isChecked: Boolean = false

    class Converter {
        @TypeConverter
        fun fromInt(checked: Int): Boolean {
            return BooleanUtils.toBoolean(checked)
        }
    }
}


@Dao
interface ItemsDao {
    @Query("select * from $TABLE_NAME")
    fun fetchAllItems(): LiveData<List<ChecklistItem>>

    @Query("select * from $TABLE_NAME where $COLUMN_ID = :id")
    fun fetchItem(id: Long): LiveData<ChecklistItem>

    @Query("select `$COLUMN_DESC` from $TABLE_NAME where $COLUMN_ID = :id")
    fun fetchItemDescription(id: Long): String

    @Insert
    fun addItem(item: ChecklistItem)

    @Query("update $TABLE_NAME set checked=1-checked where $COLUMN_ID=:id")
    fun flipStatus(id: Long)

    @Query("delete from $TABLE_NAME where checked=1")
    fun deleteCheckedItems()

    @Query("update $TABLE_NAME set checked=1")
    fun checkAllItems()

    @Query("update $TABLE_NAME set checked=0")
    fun uncheckAllItems()

    @Query("update $TABLE_NAME set checked=1-checked")
    fun flipAllItems()

    @Query("delete from $TABLE_NAME where $COLUMN_ID=:id")
    fun deleteItem(id: Long)

    @Update
    fun updateItem(item: ChecklistItem)
}

@Database(entities = [ChecklistItem::class], version = 2, exportSchema = false)
abstract class ItemsDatabase : RoomDatabase() {

    abstract fun itemsDao(): ItemsDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val CREATE_ITEMS_COPY_TABLE = "create table items_copy($COLUMN_ID integer, `$COLUMN_DESC` text, $COLUMN_CHECKED integer);"
                database.execSQL(CREATE_ITEMS_COPY_TABLE)
                database.execSQL("insert into items_copy select * from $TABLE_NAME;")
                database.execSQL("drop table $TABLE_NAME;")
                val RECREATE_ITEMS_TABLE = "create table $TABLE_NAME($COLUMN_ID integer primary key autoincrement not null, `$COLUMN_DESC` text, $COLUMN_CHECKED integer not null);"
                database.execSQL(RECREATE_ITEMS_TABLE)
                database.execSQL("insert into $TABLE_NAME select * from items_copy;")
                database.execSQL("drop table items_copy;")
            }
        }

        private var INSTANCE: ItemsDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ItemsDatabase {
            var localInstance = INSTANCE
            if (localInstance == null) {
                localInstance = Room.databaseBuilder(context.applicationContext, ItemsDatabase::class.java, DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .build()
                INSTANCE = localInstance
            }
            return localInstance
        }
    }
}

object ItemsDbThreadHelper {
    private val dbOpsThread : HandlerThread = HandlerThread("database_ops")
    val dbOpsHandler : Handler

    init {
        dbOpsThread.start()
        dbOpsHandler = Handler(dbOpsThread.looper)
    }
}