package com.dalread.util;

import android.os.Handler;

import com.google.android.exoplayer2.ExoPlayer;

public class ProgressTracker implements Runnable {

    private final String TAG = "ProgressTracker";
    private ExoPlayer exoPlayer;
    private final Handler handler;
    private PositionListener positionListener;
    private final static int DELAY_MS = 0;
    private long currentPosition = 0;

    public ProgressTracker(ExoPlayer exoPlayer, PositionListener positionListener) {
        this.exoPlayer = exoPlayer;
        this.positionListener = positionListener;
        handler = new Handler();
    }

    public void startHandle() {
        handler.post(this);
    }

    @Override
    public void run() {
        long temp = exoPlayer.getCurrentPosition();
        if (currentPosition != temp) {
            currentPosition = temp;
            if (currentPosition < 0) {
                currentPosition = 0;
            }
            positionListener.progress(currentPosition);
        }
        handler.postDelayed(this, DELAY_MS);
    }

    public void stopHandler() {
        handler.removeCallbacks(this);
    }

    public interface PositionListener {
        void progress(long position);
    }
}
