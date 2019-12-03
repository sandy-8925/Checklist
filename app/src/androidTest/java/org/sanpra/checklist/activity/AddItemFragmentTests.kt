package org.sanpra.checklist.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.apache.commons.collections4.IterableUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.sanpra.checklist.R

@RunWith(AndroidJUnit4::class)
class AddItemFragmentTests : BaseTests() {
    @Test
    fun testEmptyText() {
        launchFragment(AddItemFragment::class.java)
        onView(withId(R.id.new_item_text)).perform(replaceText(""))
        onView(withId(R.id.new_item_add_button)).perform(click())
        Assert.assertEquals(0, mockItemsController.listItems().size)
    }

    @Test
    fun testAddItemWithButton() {
        launchFragment(AddItemFragment::class.java)
        val testString = "Hello world"
        onView(withId(R.id.new_item_text)).perform(replaceText(testString))
        onView(withId(R.id.new_item_add_button)).perform(click())
        val item = IterableUtils.get(mockItemsController.listItems(), 0)
        Assert.assertEquals(testString, item.description)
    }

    @Test
    fun testAddItemWithImeAction() {
        launchFragment(AddItemFragment::class.java)
        val testString = "Test string"
        onView(withId(R.id.new_item_text)).perform(typeText(testString))
        onView(withId(R.id.new_item_text)).perform(pressImeActionButton())
        val item = IterableUtils.get(mockItemsController.listItems(), 0)
        Assert.assertEquals(testString, item.description)
    }

    @Test
    fun testBlankText() {
        launchFragment(AddItemFragment::class.java)
        onView(withId(R.id.new_item_text)).perform(replaceText("          "))
        onView(withId(R.id.new_item_add_button)).perform(click())
        Assert.assertEquals(0, mockItemsController.listItems().size)
    }
}

