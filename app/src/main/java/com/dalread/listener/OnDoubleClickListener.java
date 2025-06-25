package com.dalread.listener;

import android.view.View;

public interface OnDoubleClickListener {
    void onClick(final View view, Object data);
    void onDoubleClick(final View view, Object data);
    default void onLongPress(View view, Object data) {

    }
}
