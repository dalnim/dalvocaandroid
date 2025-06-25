package com.dalread.util;

import android.content.Context;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.SeparatorVerticalDecoration;

public class BaseBindUtils {

    public static float getDividerHeight(Context context) {
        return context.getResources().getDimension(R.dimen.divider_height);
    }

    public static @DimenRes int getDividerHeightRes() {
        return R.dimen.divider_height;
    }

    public static RecyclerView.ItemDecoration getSeparatorDecoration(Context context) {
        return new SeparatorDecoration(context, getDividerColor(), getDividerHeight(context));
    }

    public static RecyclerView.ItemDecoration getSeparatorVerticalDecoration(Context context) {
        return new SeparatorVerticalDecoration(context, getDividerColor(), getDividerHeight(context));
    }

    public static int getDividerColor(Context context) {
        return ContextCompat.getColor(context, R.color.color_divider);
    }

    //이건 색상이 내가 원하는게 안나온다. 적용된거 확인하고 필요없으면 이 코드 제거할것
    public static int getDividerColor() {
        return R.color.color_divider;
    }

    public static int getDisableColor() {
        return R.color.color_stop;
    }

    public static int getMicPlayColor() {
        return R.color.color_play;
    }

    public static int getMicStopColor() {
        return R.color.color_stop;
    }

    public static int getEnableColor() {
        return R.color.colorBlack;
    }



    public static int getDividerColorBackgroundLightWhite() {
        return R.color.color_background_light_white_divider;
    }

    public static int getDividerColorBackgroundLightWhite(Context context) {
        return ContextCompat.getColor(context, R.color.color_background_light_white_divider);
    }

    public static int getPronounceSymbolSize(Context context) {
        return (int) context.getResources().getDimension(R.dimen.item_pronounce_symbol_size);
    }

    public static int getPronounceSymbolMargin(Context context) {
        return (int) context.getResources().getDimension(R.dimen.rv_pronounce_symbol_margin);
    }
    public static int getStudyHanjaSmallBoxWidth(Context context) {
        return (int) context.getResources().getDimension(R.dimen.study_hanja_small_box_width);
    }

    public static int getStudyHanjaSmallBoxMargin(Context context) {
        return (int) context.getResources().getDimension(R.dimen.study_hanja_small_box_margin);
    }
}
