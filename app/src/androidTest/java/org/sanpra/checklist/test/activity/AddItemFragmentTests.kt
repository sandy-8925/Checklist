package org.sanpra.checklist.test.activity

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
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.sanpra.checklist.R
import org.sanpra.checklist.activity.AddItemFragment
import org.sanpra.checklist.dbhelper.ChecklistItem
import org.sanpra.checklist.dbhelper.ItemsControllerInterface
import org.sanpra.checklist.dbhelper.ItemsDatabase
import org.sanpra.checklist.test.application.SystemObjects

@RunWith(AndroidJUnit4::class)
class AddItemFragmentTests {
    private lateinit var context : Context

    @Before
    fun setupMockSystemObjects() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        SystemObjects.applicationDb = Room.inMemoryDatabaseBuilder(context.applicationContext, ItemsDatabase::class.java)
                .build()
        SystemObjects.itemsCtrlr = MockItemsController()
    }

    @Test
    fun testEmptyText() {
        FragmentScenario.launchInContainer(AddItemFragment::class.java, null, R.style.AppTheme, null)
        onView(withId(R.id.new_item_text)).perform(replaceText(""))
        onView(withId(R.id.new_item_add_button)).perform(click())
        Assert.assertEquals(0, SystemObjects.itemsController().listItems().size)
    }
}

private class MockItemsController : ItemsControllerInterface {

    private val itemsMap : Map<Long, ChecklistItem> = ArrayMap()

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fetchItem(itemId: Long): LiveData<ChecklistItem> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateItem(item: ChecklistItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listItems(): Collection<ChecklistItem> = itemsMap.values
}