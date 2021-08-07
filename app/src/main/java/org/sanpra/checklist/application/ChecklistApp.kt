/*
 * Copyright (C) 2011-2018 Sandeep Raghuraman (sandy.8925@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sanpra.checklist.application

import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import org.sanpra.checklist.BuildConfig
import org.sanpra.checklist.dbhelper.ItemsDatabase
import org.sanpra.checklist.dbhelper.getDbInstance

@Suppress("unused")
class ChecklistApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyFlashScreen().build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
        }

        appDb = getDbInstance(applicationContext)
    }
}

internal lateinit var appDb : ItemsDatabase
    private set
