package org.sanpra.checklist.activity

import android.content.Context
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.sanpra.checklist.application.SystemObjects
import org.sanpra.checklist.dbhelper.ItemsDatabase

abstract class BaseTests {
    private lateinit var context : Context
    protected lateinit var mockItemsController : MockItemsController

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

    protected fun launchFragment(fragmentClass: Class<out Fragment>, @StyleRes themeId: Int) = FragmentScenario.launchInContainer(fragmentClass, null, themeId, null)

}