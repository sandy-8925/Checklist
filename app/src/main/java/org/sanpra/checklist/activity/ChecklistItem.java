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
    long id;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static class Converter {
        @TypeConverter public boolean fromInt(int checked) {
            return BooleanUtils.toBoolean(checked);
        }
    }
}
