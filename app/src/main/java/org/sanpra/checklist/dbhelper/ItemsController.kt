package org.sanpra.checklist.dbhelper

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.sanpra.checklist.application.SystemObjects

interface ItemsControllerInterface {
    @AnyThread
    fun flipStatus(itemId : Long)

    @AnyThread
    fun deleteItem(itemId: Long)

    @AnyThread
    fun deleteCheckedItems()

    @AnyThread
    fun checkAllItems()

    @AnyThread
    fun uncheckAllItems()

    @AnyThread
    fun reverseAllItemStatus()

    @AnyThread
    fun itemListLiveData() : LiveData<List<ChecklistItem>>

    @AnyThread
    fun addItem(itemDesc : String)

    @AnyThread
    fun fetchItem(itemId: Long) : LiveData<ChecklistItem>

    @AnyThread
    fun updateItem(item : ChecklistItem)
}

internal object ItemsController : ItemsControllerInterface {
    private val itemsDb : ItemsDatabase = SystemObjects.appDb()

    override fun flipStatus(itemId: Long) {
        Completable.fromRunnable { itemsDb.itemsDao().flipStatus(itemId) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun deleteItem(itemId: Long) {
        Completable.fromRunnable { itemsDb.itemsDao().deleteItem(itemId) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun deleteCheckedItems() {
        Completable.fromRunnable { itemsDb.itemsDao().deleteCheckedItems() }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun checkAllItems() {
        Completable.fromRunnable { itemsDb.itemsDao().checkAllItems() }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun uncheckAllItems() {
        Completable.fromRunnable { itemsDb.itemsDao().uncheckAllItems() }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun reverseAllItemStatus() {
        Completable.fromRunnable { itemsDb.itemsDao().flipAllItems() }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun itemListLiveData(): LiveData<List<ChecklistItem>> = SystemObjects.appDb().itemsDao().fetchAllItems()

    override fun addItem(itemDesc: String) {
        val item = ChecklistItem().apply { description = itemDesc }
        Completable.fromRunnable { itemsDb.itemsDao().addItem(item) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun fetchItem(itemId: Long): LiveData<ChecklistItem> = itemsDb.itemsDao().fetchItem(itemId)

    override fun updateItem(item: ChecklistItem) {
        Completable.fromRunnable { itemsDb.itemsDao().updateItem(item) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}