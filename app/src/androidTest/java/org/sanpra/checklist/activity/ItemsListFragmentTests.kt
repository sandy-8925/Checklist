package org.sanpra.checklist.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.ActionProvider
import android.view.ContextMenu
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.apache.commons.collections4.Closure
import org.apache.commons.collections4.IterableUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.sanpra.checklist.R
import org.sanpra.checklist.dbhelper.ChecklistItem

@RunWith(AndroidJUnit4::class)
class ItemsListFragmentTests : BaseTests() {
    @Test
    fun testDeleteCheckedItemsMenuOption() {
        val prefilledItems = ArrayList<ChecklistItem>()
        prefilledItems.add(ChecklistItem().apply {
            id = 1
            description = "Hello world"
            isChecked = false
        })
        prefilledItems.add(ChecklistItem().apply {
            id = 2
            description = "Testing"
            isChecked = true
        })
        prefilledItems.add(ChecklistItem().apply {
            id = 3
            description = "Yet again"
            isChecked = false
        })
        mockItemsController.addItems(prefilledItems)
        val fragmentScenario = launchFragment(ItemsListFragment::class.java, R.style.AppTheme_ActionBar)
        fragmentScenario.onFragment {
            it.onOptionsItemSelected(MockMenuItem(R.id.menu_delcheckeditems))
        }
        Thread.sleep(1000)
        val itemCheckStatusClosure = ItemCheckStatusClosure()
        IterableUtils.forEach(mockItemsController.listItems(), itemCheckStatusClosure)
        Assert.assertFalse(itemCheckStatusClosure.isAnyChecked)
    }
}

private class ItemCheckStatusClosure : Closure<ChecklistItem> {
    var isAnyChecked : Boolean = false
        private set

    override fun execute(input: ChecklistItem) {
        if(input.isChecked) isAnyChecked = true
    }
}

private class MockMenuItem(private val itemId : Int) : MenuItem {
    override fun expandActionView(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasSubMenu(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMenuInfo(): ContextMenu.ContextMenuInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemId(): Int = itemId

    override fun getAlphabeticShortcut(): Char {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setEnabled(enabled: Boolean): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTitle(title: CharSequence?): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTitle(title: Int): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setChecked(checked: Boolean): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActionView(): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTitle(): CharSequence {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOrder(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOnActionExpandListener(listener: MenuItem.OnActionExpandListener?): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIntent(): Intent {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVisible(visible: Boolean): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEnabled() = true

    override fun isCheckable()= false

    override fun setShowAsAction(actionEnum: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setActionProvider(actionProvider: ActionProvider?): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTitleCondensed(title: CharSequence?): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNumericShortcut(): Char {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isActionViewExpanded(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun collapseActionView(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isVisible(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNumericShortcut(numericChar: Char): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setActionView(view: View?): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setActionView(resId: Int): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAlphabeticShortcut(alphaChar: Char): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setIcon(icon: Drawable?): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setIcon(iconRes: Int): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isChecked(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setIntent(intent: Intent?): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setShortcut(numericChar: Char, alphaChar: Char): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIcon(): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setShowAsActionFlags(actionEnum: Int): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setOnMenuItemClickListener(menuItemClickListener: MenuItem.OnMenuItemClickListener?): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActionProvider(): ActionProvider {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCheckable(checkable: Boolean): MenuItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSubMenu(): SubMenu {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTitleCondensed(): CharSequence {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}