package com.dalread.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.DimenRes;

public class UnitUtil {
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.heightPixels;
    }

    public static int getScreenSizeDp(Context context, boolean isWidth) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        int pixels = isWidth ? displayMetrics.widthPixels : displayMetrics.heightPixels;
        float scaleFactor = displayMetrics.density;
        return Math.round(pixels / scaleFactor);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int getDimenInPixel(Context context, @DimenRes int id) {
        Resources resources = context.getResources();
        return (int)resources.getDimension(id);
    }
    public static int getDimenInDp(Context context, @DimenRes int id) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float px = getDimenInPixel(context, id);
        return Math.round(px / (displayMetrics.density));
    }

}
