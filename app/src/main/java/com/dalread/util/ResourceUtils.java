package com.dalread.util;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;

import com.dalread.R;

public class ResourceUtils {

    public static int getDefaultCellBottomColor(Context context) {
        return ContextCompat.getColor(context, R.color.color_divider);
    }
    public static int getColor(Context context, @ColorRes int colorResId) {
        return ContextCompat.getColor(context, colorResId);
    }

    public static float getDefaultCellDimension(Context context) {
        return context.getResources().getDimension(R.dimen.divider_height);
    }

    public static float getDimension(Context context, @DimenRes int dimenResId) {
        return context.getResources().getDimension(dimenResId);
    }
}
