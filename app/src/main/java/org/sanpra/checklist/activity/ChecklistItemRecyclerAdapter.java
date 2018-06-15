package org.sanpra.checklist.activity;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.collections4.ListUtils;
import org.sanpra.checklist.R;
import org.sanpra.checklist.databinding.ItemRowBinding;

import java.util.Collections;
import java.util.List;

class ChecklistItemRecyclerAdapter extends RecyclerView.Adapter<ChecklistItemRecyclerAdapter.ViewHolder> {

    private static final List<ChecklistItem> DEFAULT_LIST = Collections.emptyList();
    private static final int VIEWHOLDER_TAG = R.id.CursorItemId;
    @Nullable private View.OnClickListener itemClickListener;
    @Nullable private View.OnLongClickListener itemLongClickListener;
    private List<ChecklistItem> items = DEFAULT_LIST;

    @UiThread
    ChecklistItemRecyclerAdapter() {
        super();
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_row, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChecklistItem item = items.get(position);
        holder.binding.setItem(item);
        holder.itemView.setTag(VIEWHOLDER_TAG, getItemId(position));
        holder.itemView.setOnClickListener(itemClickListener);
        holder.itemView.setOnLongClickListener(itemLongClickListener);
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

    void setItemLongClickListener(@NonNull View.OnLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    static abstract class ItemClickListener implements View.OnClickListener {

        @Override
        public final void onClick(View v) {
            long itemId = (long) v.getTag(VIEWHOLDER_TAG);
            onClick(v, itemId);
        }

        abstract void onClick(View view, long itemId);
    }

    static abstract class ItemLongClickListener implements View.OnLongClickListener {
        abstract void onLongClick(View view, long itemId);

        @Override
        public boolean onLongClick(View v) {
            long itemId = (long) v.getTag(VIEWHOLDER_TAG);
            onLongClick(v, itemId);
            return true;
        }
    }

    /**
     * Used to hold a reference to the TextView containing the text in a row
     */
    static final class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ItemRowBinding binding;

        ViewHolder(@NonNull ItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
