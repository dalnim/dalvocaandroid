package com.dalread.component.page_curl;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class PageTransform implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        if (page instanceof FrameLayout) {
            View childView = ((FrameLayout) page).getChildAt(0);
            if (childView instanceof PageCurlFrameLayout) {
                if (position > -1.0F && position < 1.0F) {
                    // hold the page steady and let the views do the work
                    page.setTranslationX(-position * page.getWidth());
                } else {
                    page.setTranslationX(0.0F);
                }
                if (position <= 1.0F && position >= -1.0F) {
                    ((PageCurlListener) childView).setCurlFactor(position);
                }
            }
        }

    }
}
