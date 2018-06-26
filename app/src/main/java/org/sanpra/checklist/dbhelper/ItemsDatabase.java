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

package org.sanpra.checklist.dbhelper;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.sanpra.checklist.activity.ChecklistItem;
import org.sanpra.checklist.activity.ItemsDao;

@Database(entities = {ChecklistItem.class}, version = 2)
public abstract class ItemsDatabase extends RoomDatabase {
    private static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            final String CREATE_ITEMS_COPY_TABLE="create table items_copy(_id integer," +
                    " desc text, checked integer);";
            database.execSQL(CREATE_ITEMS_COPY_TABLE);
            database.execSQL("insert into items_copy select * from items;");
            database.execSQL("drop table items;");
            final String RECREATE_ITEMS_TABLE = "create table items(_id integer primary key autoincrement not null," +
                    " desc text, checked integer not null);";
            database.execSQL(RECREATE_ITEMS_TABLE);
            database.execSQL("insert into items select * from items_copy;");
            database.execSQL("drop table items_copy;");
        }
    };

    private static ItemsDatabase INSTANCE;

    public static synchronized ItemsDatabase getInstance(@NonNull Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, ItemsDatabase.class, ItemsDbHelper.DbHelper.DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public abstract ItemsDao itemsDao();
}
