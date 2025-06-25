package com.dalread.listener;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public abstract class LongTouchIntervalListener implements View.OnTouchListener {

    private final long touchIntervalMills;
    private long touchTime;
    private Handler handler = new Handler();
    private View touchedView;

    public LongTouchIntervalListener() {
        this(100);
    }

    public LongTouchIntervalListener(final long touchIntervalMills) {
        if (touchIntervalMills <= 0) {
            throw new IllegalArgumentException("Touch touch interval must be more than zero");
        }
        this.touchIntervalMills = touchIntervalMills;
    }

    public abstract void onTouchInterval(View view);

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchedView = v;
                onTouchInterval(touchedView);
                touchTime = System.currentTimeMillis();
                handler.postDelayed(touchInterval, touchIntervalMills);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchTime = 0;
                handler.removeCallbacks(touchInterval);
                return true;
            default:
                break;
        }
        return false;
    }

    private final Runnable touchInterval = new Runnable() {
        @Override
        public void run() {
            onTouchInterval(touchedView);
            if (touchTime > 0) {
                handler.postDelayed(this, touchIntervalMills);
            }
        }
    };
}
