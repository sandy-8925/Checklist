package org.sanpra.checklist.activity;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.sanpra.checklist.R;
import org.sanpra.checklist.dbhelper.ItemsDbHelper;

class ChecklistItemRecyclerAdapter extends RecyclerView.Adapter<ChecklistItemAdapter.ViewHolder> {

    private final int CHECKLIST_ITEM_CHECKED_COLOR;
    private final int CHECKLIST_ITEM_UNCHECKED_COLOR;
    private Cursor cursor;
    private View.OnClickListener itemClickListener;

    ChecklistItemRecyclerAdapter(@NonNull Context context) {
        super();
        CHECKLIST_ITEM_CHECKED_COLOR = ContextCompat.getColor(context, R.color.grey);
        CHECKLIST_ITEM_UNCHECKED_COLOR = ContextCompat.getColor(context, R.color.white);
        setHasStableIds(true);
    }

    @Override
    public ChecklistItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_row, parent, false);
        return new ChecklistItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChecklistItemAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String itemDesc = ItemsDbHelper.getItemDesc(cursor);
        holder.itemTextView.setText(itemDesc);
        final int itemColor = ItemsDbHelper.isItemChecked(cursor) ?
                CHECKLIST_ITEM_CHECKED_COLOR : CHECKLIST_ITEM_UNCHECKED_COLOR ;
        holder.itemTextView.setTextColor(itemColor);
        holder.itemView.setTag(ItemClickListener.VIEWHOLDER_TAG, getItemId(position));
        holder.itemView.setOnClickListener(itemClickListener);
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                return ItemsDbHelper.getItemId(cursor);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if(cursor == null) return  0;
        return cursor.getCount();
    }

    void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    void setOnItemClickListener(ItemClickListener onClickListener) {
        this.itemClickListener = onClickListener;
    }

    static abstract class ItemClickListener implements View.OnClickListener {
        private static final int VIEWHOLDER_TAG = R.id.CursorItemId;

        @Override
        public final void onClick(View v) {
            long itemId = (long) v.getTag(VIEWHOLDER_TAG);
            onClick(v, itemId);
        }

        abstract void onClick(View view, long itemId);
    }
}
