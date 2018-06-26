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

package org.sanpra.checklist.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import org.sanpra.checklist.dbhelper.ItemsDatabase;

import java.util.List;

public final class ChecklistItemsCursorLoader extends AsyncTaskLoader<List<ChecklistItem>> implements Observer<List<ChecklistItem>> {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final LiveData<List<ChecklistItem>> listLiveData;

    ChecklistItemsCursorLoader(@NonNull final Context context) {
        super(context);
        final Context appContext = context.getApplicationContext();
        listLiveData = ItemsDatabase.getInstance(appContext).itemsDao().fetchAllItems();
    }

    @Override
    public List<ChecklistItem> loadInBackground() {
        return listLiveData.getValue();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        listLiveData.observeForever(this);
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        listLiveData.removeObserver(this);
    }

    @Override
    public void onChanged(@Nullable final List<ChecklistItem> checklistItems) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                deliverResult(checklistItems);
            }
        });
    }
}
