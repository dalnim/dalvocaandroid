package com.dalread.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SeparatorVerticalDecoration extends RecyclerView.ItemDecoration {
    private final Paint mPaint;

    /**
     * Create a decoration that draws a line in the given color and width between the items in the view.
     *
     * @param context  a context to access the resources.
     * @param color    the color of the separator to draw.
     * @param heightDp the height of the separator in dp.
     */
    public SeparatorVerticalDecoration(@NonNull Context context, @ColorInt int color,
                                       @FloatRange(from = 0, fromInclusive = false) float heightDp) {
        mPaint = new Paint();
        mPaint.setColor(color);
        final float thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                heightDp, context.getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(thickness);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        // add a separator to every views
        outRect.set(0, 0, (int) mPaint.getStrokeWidth()/2, (int) mPaint.getStrokeWidth()/2); // left, top, right, bottom

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // we set the stroke width before, so as to correctly draw the line we have to offset by width / 2
        final int offset = (int) (mPaint.getStrokeWidth() / 2);

        // this will iterate over every visible view
        for (int i = 0; i < parent.getChildCount(); i++) {
            // get the view
            final View view = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            if (params.height != 0) {
                // get the position
                final int position = params.getViewAdapterPosition();

                // and finally draw the separator
                if (position < state.getItemCount()) {
                    c.drawLine(view.getLeft(), view.getBottom(), view.getRight() + offset, view.getBottom(), mPaint); //Bottom line
                    c.drawLine(view.getRight(), view.getTop(), view.getRight(), view.getBottom(), mPaint); //Right line
                }
            }
        }
    }
}
