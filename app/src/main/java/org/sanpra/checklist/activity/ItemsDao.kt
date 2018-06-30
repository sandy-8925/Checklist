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

package org.sanpra.checklist.activity

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface ItemsDao {
    @Query("select * from items")
    fun fetchAllItems(): LiveData<List<ChecklistItem>>

    @Query("select * from items where _id = :id")
    fun fetchItem(id: Long): ChecklistItem

    @Query("select `desc` from items where _id = :id")
    fun fetchItemDescription(id: Long): String

    @Insert
    fun addItem(item: ChecklistItem)

    @Query("update items set checked=1-checked where _id=:id")
    fun flipStatus(id: Long)

    @Query("delete from items where checked=1")
    fun deleteCheckedItems()

    @Query("update items set checked=1")
    fun checkAllItems()

    @Query("update items set checked=0")
    fun uncheckAllItems()

    @Query("update items set checked=1-checked")
    fun flipAllItems()

    @Query("delete from items where _id=:id")
    fun deleteItem(id: Long)

    @Update
    fun updateItem(item: ChecklistItem)
}
