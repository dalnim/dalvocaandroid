package com.dalread.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import androidx.core.content.ContextCompat;

import com.dalread.R;

public class BaseColorUtil {
    public static int getTextPrimaryColor(Context context) {
        return ContextCompat.getColor(context, R.color.textPrimaryColor);
    }
    public static int getTextSecondryColor(Context context) {
        return ContextCompat.getColor(context, R.color.textSecondaryColor);
    }

    public static int getVocaUnknownPronounceColor(Context context) {
        return ContextCompat.getColor(context, R.color.rubyTextUnknownPronounceColor);
    }

    public static int getVocaUnknownColor(Context context) {
        return ContextCompat.getColor(context, R.color.rubyTextUnknownColor);
    }
    public static ColorDrawable getColorDrawableLayerColorInDarkMode(Context context) {
        return new ColorDrawable(ContextCompat.getColor(context, R.color.ads_layer_color_in_dark_mode));
    }


}
