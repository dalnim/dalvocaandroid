package com.dalread.component;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration{
    private int mSizeSpacingPx;
    private int mOrientation;

    public SpacingItemDecoration(int sizeSpacingPx, int orientation) {
        this.mSizeSpacingPx = sizeSpacingPx;
        this.mOrientation = orientation;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            int bottomSpacing = 0;
            if (position < itemCount - 1) {
                bottomSpacing = mSizeSpacingPx;
            }
            outRect.set(0, 0, 0, bottomSpacing);
        } else {
            int rightSpacing = 0;
            if (position < itemCount - 1) {
                rightSpacing = mSizeSpacingPx;
            }
            outRect.set(0, 0, rightSpacing, 0);
        }
    }
}
