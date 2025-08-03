package com.dalread.util;

import android.os.Handler;

import com.google.android.exoplayer2.ExoPlayer;

public class ProgressTracker implements Runnable {

    private final String TAG = "ProgressTracker";
    private ExoPlayer exoPlayer;
    private final Handler handler;
    private PositionListener positionListener;
    
    // DELAY_MS 설정 가이드:
    // DELAY_MS | FPS    | 사용자 경험      | CPU 부하        | 추천도
    // ---------|--------|----------------|----------------|------------------
    // 0ms      | 무제한  | 매우 부드러움     | 🔥🔥🔥🔥🔥     | ❌ 절대 금지
    // 16ms     | 60fps  | 매우 부드러움     | 🔥🔥🔥🔥       | ⚠️ 싱글 비디오만
    // 33ms     | 30fps  | 부드러움         | 🔥🔥🔥         | ✅ 일반적 추천
    // 50ms     | 20fps  | 약간 버벅        | 🔥🔥           | ✅ 멀티플레이어 추천
    // 100ms    | 10fps  | 버벅거림         | 🔥             | ⚠️ 배터리 절약용
    private final static int DELAY_MS = 50;
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
