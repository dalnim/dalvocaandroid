package com.dalread.component.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//SeparatorDecoration와 다르게 양쪽 끝이 조금씩 padding이 있다.
public class SeparatorDecorationAddPaddingStartEnd extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private int mHeight = 2;
    private int mPaddingStart;
    private int mPaddingEnd;

    public SeparatorDecorationAddPaddingStartEnd(@NonNull Context context, @ColorInt int color,
                                                 @FloatRange(from = 0, fromInclusive = false) float heightDp) {
        mPaint = new Paint();
        mPaint.setColor(color);
        final float thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                heightDp, context.getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(thickness);
        mPaddingStart = 20;
        mPaddingEnd = 20;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int lastChildPosition = parent.getChildCount() - 1;
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int left = child.getLeft() + mPaddingStart;
            int right = child.getRight() - mPaddingEnd;
            int top = child.getBottom();
            int bottom = top + mHeight;
            if (i == lastChildPosition) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            } else {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
