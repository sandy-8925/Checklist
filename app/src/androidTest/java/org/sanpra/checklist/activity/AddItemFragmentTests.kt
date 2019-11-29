package org.sanpra.checklist.activity

import android.content.Context
import androidx.collection.ArrayMap
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.apache.commons.collections4.IterableUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.sanpra.checklist.R
import org.sanpra.checklist.dbhelper.ChecklistItem
import org.sanpra.checklist.dbhelper.ItemsControllerInterface
import org.sanpra.checklist.dbhelper.ItemsDatabase
import org.sanpra.checklist.application.SystemObjects
import java.util.concurrent.atomic.AtomicLong

@RunWith(AndroidJUnit4::class)
class AddItemFragmentTests {
    private lateinit var context : Context
    private val mockItemsController = MockItemsController()

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        setupMockSystemObjects()
    }

    private fun setupMockSystemObjects() {
        SystemObjects.applicationDb = Room.inMemoryDatabaseBuilder(context.applicationContext, ItemsDatabase::class.java)
                .build()
        SystemObjects.itemsCtrlr = mockItemsController
    }

    @Test
    fun testEmptyText() {
        launchFragment()
        onView(withId(R.id.new_item_text)).perform(replaceText(""))
        onView(withId(R.id.new_item_add_button)).perform(click())
        Assert.assertEquals(0, mockItemsController.listItems().size)
    }

    @Test
    fun testAddItemWithButton() {
        launchFragment()
        val testString = "Hello world"
        onView(withId(R.id.new_item_text)).perform(replaceText(testString))
        onView(withId(R.id.new_item_add_button)).perform(click())
        val item = IterableUtils.get(mockItemsController.listItems(), 0)
        Assert.assertEquals(testString, item.description)
    }

    private fun launchFragment() {
        FragmentScenario.launchInContainer(AddItemFragment::class.java, null, R.style.AppTheme, null)
    }
}

private class MockItemsController : ItemsControllerInterface {

    private val itemsMap : MutableMap<Long, ChecklistItem> = ArrayMap()
    private var nextId = AtomicLong(0)

    override fun flipStatus(itemId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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