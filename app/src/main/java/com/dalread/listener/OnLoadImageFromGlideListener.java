package com.dalread.listener;

import android.graphics.drawable.Drawable;

public interface OnLoadImageFromGlideListener {
    void onLoadSuccess(Drawable drawable);
    void onLoadFailed();
}
