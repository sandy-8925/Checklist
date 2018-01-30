package org.sanpra.checklist.activity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import org.apache.commons.lang3.BooleanUtils;
import org.sanpra.checklist.dbhelper.ItemsDbHelper;

@Entity(tableName = ItemsDbHelper.TABLE_NAME)
public class ChecklistItem {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    int id;

    @ColumnInfo(name = "desc")
    String description;
    @ColumnInfo(name = "checked")
    @TypeConverters(Converter.class)
    boolean checked;

    public String getDescription() {
        return description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class Converter {
        @TypeConverter public boolean fromInt(int checked) {
            return BooleanUtils.toBoolean(checked);
        }
    }
}
