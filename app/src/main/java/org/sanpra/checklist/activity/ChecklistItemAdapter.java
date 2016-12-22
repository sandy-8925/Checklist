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

package org.sanpra.checklist.activity;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.sanpra.checklist.R;
import org.sanpra.checklist.dbhelper.ItemsDbHelper;

final class ChecklistItemAdapter extends SimpleCursorAdapter {
    private final int CHECKLIST_ITEM_UNCHECKED_COLOR;
    private final int CHECKLIST_ITEM_CHECKED_COLOR;
    private final ItemsDbHelper mDbHelper;
    /**
     * These arrays specify the mapping from database columns to list item View elements
     */
    private final static String[] from =  new String[] { ItemsDbHelper.COL_DESC };
    private final static int[] to = new int[] { R.id.itemtext };

    ChecklistItemAdapter(final Context context, final Cursor checklistItemsCursor) {
        super(context, R.layout.item_row, checklistItemsCursor,
                from, to, 0);
        final Resources appResources = context.getResources();
        CHECKLIST_ITEM_UNCHECKED_COLOR = appResources.getColor(R.color.white);
        CHECKLIST_ITEM_CHECKED_COLOR = appResources.getColor(R.color.grey);
        mDbHelper = ItemsDbHelper.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View item = super.getView(position, convertView, parent);

        TextView itemText;
        if(item.getTag() != null) {
            itemText = ((ViewHolder) item.getTag()).itemTextView;
        }
        else {
            itemText = (TextView) item.findViewById(R.id.itemtext);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.itemTextView = itemText;
            item.setTag(viewHolder);
        }

        final long itemRowId = getItemId(position);
        final int itemColor = mDbHelper.isItemChecked(itemRowId) ?
                CHECKLIST_ITEM_CHECKED_COLOR : CHECKLIST_ITEM_UNCHECKED_COLOR ;
        itemText.setTextColor(itemColor);

        return item;
    }

    /**
     * Used to hold a reference to the TextView containing the text in a row
     */
    private static final class ViewHolder {
        TextView itemTextView;
    }
}
