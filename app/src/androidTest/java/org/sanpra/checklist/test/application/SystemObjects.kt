package org.sanpra.checklist.test.application

import org.sanpra.checklist.dbhelper.ItemsController
import org.sanpra.checklist.dbhelper.ItemsControllerInterface
import org.sanpra.checklist.dbhelper.ItemsDatabase

object SystemObjects {
    internal var applicationDb : ItemsDatabase = org.sanpra.checklist.application.appDb
    internal var itemsCtrlr : ItemsControllerInterface = ItemsController

    fun appDb() : ItemsDatabase = applicationDb
    fun itemsController() : ItemsControllerInterface = itemsCtrlr
}