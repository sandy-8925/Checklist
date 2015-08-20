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
                from, to);
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
