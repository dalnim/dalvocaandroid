package com.dalread.listener;

import androidx.recyclerview.widget.RecyclerView;

public interface OnDragListener {

    void onDragStarted(RecyclerView.ViewHolder viewHolder);

    void onDragStopped();
}
