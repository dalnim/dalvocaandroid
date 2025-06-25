package com.dalread.helper;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.dalread.listener.OnDoubleClickListener;

//이건 클릭, 더블클릭, 길게 누르기를 할려고 만들고 있는 중이다. 자막 테이블뷰에서 스크롤할때 자막 메뉴가 뜨는 버그가 있고, 테이블 뷰에서 클릭시 재생을 하고, 길게 누를때 메뉴를 보여줄려고 하는게 맞는 UI인지 의심이 감.
public class DoubleClickHelper {
    public final int DEFAULT_LONG_PRESS_TIMEOUT = 600;
    private int numberOfTaps = 0;
    private boolean isDoubleClick = false;
    private boolean isLongPress = false;
    private Handler mHandler = new Handler();
    private Object currentItem; // Store the current item being clicked
    private View currentView; // Store the current view being clicked
    private float initialX; // Store the initial touch X position
    private float initialY; // Store the initial touch Y position
    private float movingX; // Store the initial touch X position
    private float movingY; // Store the initial touch Y position
    private float swipeThreshold = 0; // Adjust this value as needed (in pixels)

    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            isLongPress = true;
            if (listener != null) {
                if (Math.abs(movingX - initialX) >= swipeThreshold || Math.abs(movingY - initialY) >= swipeThreshold) {
                    resetFlags();
                    listener.onLongPress(currentView, currentItem);
                }
            }
        }
    };

    private OnDoubleClickListener listener;

    public DoubleClickHelper(OnDoubleClickListener listener) {
        this.listener = listener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent, Object item) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Store the initial touch position
                movingX = initialX = motionEvent.getX();
                movingY = initialY = motionEvent.getY();
                // Store the current view and item for later use in the long press check
                currentItem = item;
                currentView = view;

                isLongPress = false;
                mHandler.postDelayed(mLongPressRunnable, DEFAULT_LONG_PRESS_TIMEOUT);
                break;
            case MotionEvent.ACTION_MOVE:
                // Calculate the distance between the initial touch position and current touch position
                movingX = motionEvent.getX();// Math.abs(motionEvent.getX() - initialX);
                movingY = motionEvent.getY(); // Math.abs(motionEvent.getY() - initialY);
//
//                // Set a threshold value for swipe detection
//
//
                if (Math.abs(movingX - initialX) >= swipeThreshold || Math.abs(movingY - initialY) >= swipeThreshold) {
                    // Swipe detected, reset the long-press handler and return early
                    mHandler.removeCallbacks(mLongPressRunnable);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongPressRunnable);
                if (!isLongPress) {
                    handleClickItem(view, item);
                }
                break;
        }
        return true;
    }


    private void handleClickItem(View view, Object item) {
        // Store the current view and item for later use in the long press check
        currentItem = item;
        currentView = view;

        if (numberOfTaps == 0) {
            numberOfTaps++;
            mHandler.postDelayed(() -> {
                if (!isLongPress) {
                    // Check if a double-click occurred
                    isDoubleClick = numberOfTaps > 1;
                    if (listener != null) {
                        if (isDoubleClick) {
                            listener.onDoubleClick(view, item);
                        } else {
                            listener.onClick(view, item);
                        }
                    }
                }
                resetFlags(); // Reset flags for click and double-click detection
            }, ViewConfiguration.getDoubleTapTimeout());
        } else {
            // This is the second tap, increment numberOfTaps to detect double-click
            numberOfTaps++;
        }
    }

    public void resetFlags() {
        initialX = 0;
        initialY = 0;
        movingX = 0;
        movingY = 0;
        numberOfTaps = 0;
        isDoubleClick = false;
        isLongPress = false;
    }
}
