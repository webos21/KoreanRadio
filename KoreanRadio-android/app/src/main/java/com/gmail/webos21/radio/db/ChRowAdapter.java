package com.gmail.webos21.radio.db;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.webos21.radio.R;

public class ChRowAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private boolean bShowIcon;

    public ChRowAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public ChRowAdapter(Context context, Cursor cursor, boolean showIcon) {
        super(context, cursor);
        this.bShowIcon = showIcon;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ch_row, parent, false);
        return new ChRowHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        ChRow item = ChRow.bindCursor(cursor);
        ((ChRowHolder) viewHolder).setChannel(item, cursor.getPosition(), bShowIcon);
    }

    public boolean isShowIcon() {
        return this.bShowIcon;
    }

    public void setShowIcon(boolean onOff) {
        this.bShowIcon = onOff;
    }
}
