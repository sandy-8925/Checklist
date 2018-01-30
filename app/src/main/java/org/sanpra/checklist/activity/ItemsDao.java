package org.sanpra.checklist.activity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ItemsDao {
    @Query("select * from items")
    List<ChecklistItem> fetchAllItems();

    @Query("select * from items where _id = :id")
    ChecklistItem fetchItem(int id);

    @Query("select `desc` from items where _id = :id")
    String fetchItemDescription(int id);

    @Insert
    void addItem(ChecklistItem item);

    @Query("update items set checked=1-checked where _id=:id")
    void flipStatus(int id);

    @Query("delete from items where checked=1")
    void deleteCheckedItems();

    @Query("update items set checked=1")
    void checkAllItems();

    @Query("update items set checked=0")
    void uncheckAllItems();

    @Query("update items set checked=1-checked")
    void flipAllItems();

    @Delete
    void deleteItem(ChecklistItem item);

    @Update
    void updateItem(ChecklistItem item);
}
