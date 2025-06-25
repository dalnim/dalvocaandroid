package com.dalread.helper;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearSmoothScroller;
//비디오 리스트에서 파일이 리스트에서 많이 밑에 있을때 빨리 갈려고 하는데, 리스트가 너무 길면 이것도 좀 느리다.
public class FastScroller extends LinearSmoothScroller {
    public FastScroller(Context context) {
        super(context);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        // 스크롤 속도를 조정 (값이 작을수록 더 빠르게 스크롤)
        return 8f / displayMetrics.densityDpi; // 기본값은 25f
    }
}