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

package org.sanpra.checklist.activity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.content.AsyncTaskLoader

import org.sanpra.checklist.dbhelper.ItemsDatabase

class ChecklistItemsCursorLoader internal constructor(context: Context) : AsyncTaskLoader<List<ChecklistItem>>(context), Observer<List<ChecklistItem>> {

    private val mHandler = Handler(Looper.getMainLooper())
    private val listLiveData: LiveData<List<ChecklistItem>>

    init {
        val appContext = context.applicationContext
        listLiveData = ItemsDatabase.getInstance(appContext).itemsDao().fetchAllItems()
    }

    override fun loadInBackground(): List<ChecklistItem>? {
        return listLiveData.value
    }

    override fun onStartLoading() {
        super.onStartLoading()
        listLiveData.observeForever(this)
        forceLoad()
    }

    override fun onStopLoading() {
        super.onStopLoading()
        listLiveData.removeObserver(this)
    }

    override fun onChanged(checklistItems: List<ChecklistItem>?) {
        mHandler.post { deliverResult(checklistItems) }
    }
}
