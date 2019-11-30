package org.sanpra.checklist.activity

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.apache.commons.collections4.IterableUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.sanpra.checklist.R
import org.sanpra.checklist.dbhelper.ItemsDatabase
import org.sanpra.checklist.application.SystemObjects

@RunWith(AndroidJUnit4::class)
class AddItemFragmentTests {
    private lateinit var context : Context
    private lateinit var mockItemsController : MockItemsController

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        setupMockSystemObjects()
    }

    private fun setupMockSystemObjects() {
        SystemObjects.applicationDb = Room.inMemoryDatabaseBuilder(context.applicationContext, ItemsDatabase::class.java)
                .build()
        mockItemsController = MockItemsController()
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

    @Test
    fun testAddItemWithImeAction() {
        launchFragment()
        val testString = "Test string"
        onView(withId(R.id.new_item_text)).perform(typeText(testString))
        onView(withId(R.id.new_item_text)).perform(pressImeActionButton())
        val item = IterableUtils.get(mockItemsController.listItems(), 0)
        Assert.assertEquals(testString, item.description)
    }

    @Test
    fun testBlankText() {
        launchFragment()
        onView(withId(R.id.new_item_text)).perform(replaceText("          "))
        onView(withId(R.id.new_item_add_button)).perform(click())
        Assert.assertEquals(0, mockItemsController.listItems().size)
    }

    private fun launchFragment() {
        FragmentScenario.launchInContainer(AddItemFragment::class.java, null, R.style.AppTheme, null)
    }
}

