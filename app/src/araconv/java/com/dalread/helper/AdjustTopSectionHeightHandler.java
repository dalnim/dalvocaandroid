package com.dalread.helper;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public class AdjustTopSectionHeightHandler {
    private int totalHeight;
    private int topSectionHeight;
    private View separatorBar;
    private View topSection;

    public AdjustTopSectionHeightHandler(View topSection, View separatorBar) {
        this.topSection = topSection;
        this.separatorBar = separatorBar;
        init();
    }

    private void init() {
        topSection.post(() -> totalHeight = topSection.getRootView().getHeight());
        separatorBar.setOnTouchListener(separatorBarListener);
    }

    private View.OnTouchListener separatorBarListener = new View.OnTouchListener() {
        private float pressedY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int minHeight = totalHeight / 10; //상단뷰의 최소 높이를 보장
            final int maxHeight = totalHeight - minHeight - (separatorBar.getHeight() * 3); //하단 뷰의 최소 높이를 보장.

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressedY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float currentY = event.getY() - pressedY;
                    topSectionHeight = (int) (topSection.getHeight() + currentY);
                    topSectionHeight = Math.max(minHeight, Math.min(maxHeight, topSectionHeight));
                    setTopSectionHeight();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    private void setTopSectionHeight() {
        topSection.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                topSection.setLayoutParams(new LinearLayout.LayoutParams(topSection.getWidth(), topSectionHeight));
                topSection.getViewTreeObserver().removeOnGlobalLayoutListener(this::onGlobalLayout);
            }
        });
    }
}

