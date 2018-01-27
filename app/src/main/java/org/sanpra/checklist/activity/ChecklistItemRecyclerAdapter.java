package org.sanpra.checklist.activity;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.sanpra.checklist.R;
import org.sanpra.checklist.dbhelper.ItemsDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ChecklistItemRecyclerAdapter extends RecyclerView.Adapter<ChecklistItemRecyclerAdapter.ViewHolder> {

    private static final List<ChecklistItem> DEFAULT_LIST = new ArrayList<>(0);
    private final int CHECKLIST_ITEM_CHECKED_COLOR;
    private final int CHECKLIST_ITEM_UNCHECKED_COLOR;
    private View.OnClickListener itemClickListener;
    private List<ChecklistItem> items = DEFAULT_LIST;

    @UiThread
    ChecklistItemRecyclerAdapter(@NonNull Context context) {
        super();
        CHECKLIST_ITEM_CHECKED_COLOR = ContextCompat.getColor(context, R.color.grey);
        CHECKLIST_ITEM_UNCHECKED_COLOR = ContextCompat.getColor(context, R.color.white);
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ChecklistItem item = items.get(position);
        holder.itemTextView.setText(item.description);
        final int itemColor = item.checked ?
                CHECKLIST_ITEM_CHECKED_COLOR : CHECKLIST_ITEM_UNCHECKED_COLOR ;
        holder.itemTextView.setTextColor(itemColor);
        holder.itemView.setTag(ItemClickListener.VIEWHOLDER_TAG, getItemId(position));
        holder.itemView.setOnClickListener(itemClickListener);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @UiThread
    void setOnItemClickListener(@NonNull ItemClickListener onClickListener) {
        this.itemClickListener = onClickListener;
    }

    void setItems(@Nullable List<ChecklistItem> items) {
        this.items = ListUtils.defaultIfNull(items, DEFAULT_LIST);
        notifyDataSetChanged();
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

    /**
     * Used to hold a reference to the TextView containing the text in a row
     */
    static final class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = (TextView) itemView.findViewById(R.id.itemtext);
        }
    }
}
