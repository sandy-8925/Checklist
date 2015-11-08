/*
 * Copyright (C) 2011-2015 Sandeep Raghuraman (sandy.8925@gmail.com)

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

package org.sanpra.checklist.dbhelper;

import android.content.Context;
import android.database.Cursor;

import com.commonsware.cwac.loaderex.acl.AbstractCursorLoader;

public final class ChecklistItemsCursorLoader extends AbstractCursorLoader {

    private final Context mContext;

    public ChecklistItemsCursorLoader(final Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected Cursor buildCursor() {
        return ItemsDbHelper.getInstance(mContext).fetchAllItems();
    }
}
