package org.sanpra.checklist.activity

import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import org.sanpra.checklist.dbhelper.ChecklistItem
import org.sanpra.checklist.dbhelper.ItemsControllerInterface
import java.util.concurrent.atomic.AtomicLong

class MockItemsController : ItemsControllerInterface {

    private val itemsMap : MutableMap<Long, ChecklistItem> = ArrayMap()
    private var nextId = AtomicLong(0)

    override fun flipStatus(itemId: Long) {
        val item = itemsMap[itemId]
        item ?: return
        item.isChecked = !item.isChecked
    }

    override fun deleteItem(itemId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCheckedItems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkAllItems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uncheckAllItems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reverseAllItemStatus() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun itemListLiveData(): LiveData<List<ChecklistItem>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addItem(itemDesc: String) {
        val itemId = nextId.getAndIncrement()
        itemsMap[itemId] = ChecklistItem().apply {
            id = itemId
            description = itemDesc
        }
    }

    override fun fetchItem(itemId: Long): LiveData<ChecklistItem> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateItem(item: ChecklistItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun listItems(): Collection<ChecklistItem> = itemsMap.values
}