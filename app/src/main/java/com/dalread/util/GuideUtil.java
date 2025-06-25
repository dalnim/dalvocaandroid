package com.dalread.util;

import android.content.Context;
import android.view.View;

import com.dalread.R;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class GuideUtil {
    public static void showGuideView(Context context, String title, View targetView, GuideListener listener) {
        new GuideView.Builder(context)
                .setTitle(title)
                .setGravity(Gravity.center)
                .setDismissType(DismissType.anywhere)
                .setTargetView(targetView)
                .setTitleTextSize((int) (context.getResources().getDimensionPixelSize(R.dimen.font_showcaseview_title) / context.getResources().getDisplayMetrics().density))
                .setGuideListener(listener)
                .build()
                .show();
    }
}
