package com.dalread.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ClickableRecyclerView extends RecyclerView {

    private final GestureDetectorCompat detector;

    public ClickableRecyclerView(Context context) {
        this(context, null);
    }

    public ClickableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector = new GestureDetectorCompat(context, new ClickListener());
    }

    private class ClickListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            performClick();
            return true;
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        detector.onTouchEvent(e);
        return super.dispatchTouchEvent(e);
    }
}