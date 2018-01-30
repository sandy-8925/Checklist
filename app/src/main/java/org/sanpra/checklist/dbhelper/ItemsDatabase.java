package org.sanpra.checklist.dbhelper;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import org.sanpra.checklist.activity.ChecklistItem;
import org.sanpra.checklist.activity.ItemsDao;

@Database(entities = {ChecklistItem.class}, version = 2)
public abstract class ItemsDatabase extends RoomDatabase {
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    private static ItemsDatabase INSTANCE;

    static synchronized ItemsDatabase getInstance(@NonNull Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, ItemsDatabase.class, ItemsDbHelper.DbHelper.DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return INSTANCE;
    }

    public abstract ItemsDao itemsDao();
}
