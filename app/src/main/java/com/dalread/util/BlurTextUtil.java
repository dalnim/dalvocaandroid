package com.dalread.util;

import android.graphics.BlurMaskFilter;
import android.graphics.MaskFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.MaskFilterSpan;

public class BlurTextUtil {
    public static SpannableString getBlurText(String str) {
        SpannableString strBlur = new SpannableString(str);
        MaskFilter blurMask = new BlurMaskFilter(30f, BlurMaskFilter.Blur.NORMAL);
        float percentNoBlur = 0.5f;
        strBlur.setSpan(new MaskFilterSpan(blurMask),  (int)(strBlur.length() * percentNoBlur), strBlur.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return strBlur;
    }
}
