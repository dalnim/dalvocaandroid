package com.araonesoft.dalstttest;

public interface WhisperCallback {
    /**
     * 새로운 세그먼트가 생성될 때마다 호출됩니다.
     * @param text 세그먼트 텍스트
     * @param t0 시작 시간 (밀리초)
     * @param t1 종료 시간 (밀리초)
     */
    void onSegment(String text, long t0, long t1);
    
    /**
     * 전사 진행률이 업데이트될 때마다 호출됩니다.
     * @param progress 진행률 (0-100)
     */
    void onProgress(int progress);
}
