package com.dalread.component;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSeparatorItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private int mNumColumns;

    public GridSeparatorItemDecoration(Drawable divider, int numColumns) {
        mDivider = divider;
        mNumColumns = numColumns;
    }

    /**
     * Draws horizontal and/or vertical dividers onto the parent RecyclerView.
     *
     * @param canvas The {@link Canvas} onto which dividers will be drawn
     * @param parent The RecyclerView onto which dividers are being added
     * @param state The current RecyclerView.State of the RecyclerView
     */
    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        drawHorizontalDividers(canvas, parent);
        drawVerticalDividers(canvas, parent);
    }

    /**
     * Determines the size and location of offsets between items in the parent
     * RecyclerView.
     *
     * @param outRect The {@link Rect} of offsets to be added around the child view
     * @param view The child view to be decorated with an offset
     * @param parent The RecyclerView onto which dividers are being added
     * @param state The current RecyclerView.State of the RecyclerView
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        boolean childIsInLeftmostColumn = (parent.getChildAdapterPosition(view) % mNumColumns) == 0;
        if (!childIsInLeftmostColumn) {
            outRect.left = mDivider.getIntrinsicWidth();
        }

        boolean childIsInFirstRow = (parent.getChildAdapterPosition(view)) < mNumColumns;
        if (!childIsInFirstRow) {
            outRect.top = mDivider.getIntrinsicHeight();
        }
    }

    /**
     * Adds horizontal dividers to a RecyclerView with a GridLayoutManager or its
     * subclass.
     *
     * @param canvas The {@link Canvas} onto which dividers will be drawn
     * @param parent The RecyclerView onto which dividers are being added
     */
    private void drawHorizontalDividers(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int rowCount = childCount / mNumColumns;
        int lastRowChildCount = childCount % mNumColumns;

        for (int i = 1; i < mNumColumns; i++) {
            int lastRowChildIndex;
            if (i < lastRowChildCount) {
                lastRowChildIndex = i + (rowCount * mNumColumns);
            } else {
                lastRowChildIndex = i + ((rowCount - 1) * mNumColumns);
            }

            View firstRowChild = parent.getChildAt(i);
            View lastRowChild = parent.getChildAt(lastRowChildIndex);
            if (firstRowChild == null || lastRowChild == null) {
                continue;
            }

            int dividerTop = firstRowChild.getTop();
            int dividerRight = firstRowChild.getLeft();
            int dividerLeft = dividerRight - mDivider.getIntrinsicWidth();
            int dividerBottom = lastRowChild.getBottom();

            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            mDivider.draw(canvas);
        }
    }

    /**
     * Adds vertical dividers to a RecyclerView with a GridLayoutManager or its
     * subclass.
     *
     * @param canvas The {@link Canvas} onto which dividers will be drawn
     * @param parent The RecyclerView onto which dividers are being added
     */
    private void drawVerticalDividers(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int rowCount = childCount / mNumColumns;
        int rightmostChildIndex;
        for (int i = 1; i <= rowCount; i++) {
            if (i == rowCount) {
                rightmostChildIndex = parent.getChildCount() - 1;
            } else {
                rightmostChildIndex = (i * mNumColumns) + mNumColumns - 1;
            }

            View leftmostChild = parent.getChildAt(i * mNumColumns);
            View rightmostChild = parent.getChildAt(rightmostChildIndex);
            if (leftmostChild == null || rightmostChild == null) {
                continue;
            }

            int dividerLeft = leftmostChild.getLeft();
            int dividerBottom = leftmostChild.getTop();
            int dividerTop = dividerBottom - mDivider.getIntrinsicHeight();
            int dividerRight = rightmostChild.getRight();

            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            mDivider.draw(canvas);
        }
    }
}
