package org.sanpra.checklist.application

import org.sanpra.checklist.dbhelper.ItemsController
import org.sanpra.checklist.dbhelper.ItemsControllerInterface
import org.sanpra.checklist.dbhelper.ItemsDatabase

object SystemObjects {
    fun appDb() : ItemsDatabase = appDb
    fun itemsController() : ItemsControllerInterface = ItemsController
}