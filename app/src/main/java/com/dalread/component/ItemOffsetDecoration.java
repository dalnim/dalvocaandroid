package com.dalread.component;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int itemOffset;

    public ItemOffsetDecoration(int itemOffset) {
        this.itemOffset = itemOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.set(itemOffset, itemOffset, itemOffset, itemOffset);
    }
}
