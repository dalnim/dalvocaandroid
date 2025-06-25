package com.dalread.activity;

import androidx.annotation.StringRes;

import com.dalread.R;
import com.dalread.util.AppFlavorUtil;

public class AppIntroduction1Fragment extends AppIntroductionFragment {
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_app_introduction_1;
    }

    protected int getStringResId() {
        @StringRes int resId = R.string.app_introduction_1_araplayer;
        if (AppFlavorUtil.isAraHangulApp()) {
            resId = R.string.app_introduction_1_arahangul;
        } else if (AppFlavorUtil.isAraConvEnglishApp()) {
            resId = R.string.app_introduction_1_araconv;
        } else if (AppFlavorUtil.isAraConvKoreaApp()) {
            resId = R.string.app_introduction_1_araconv_korean;
        } else if (AppFlavorUtil.isAraConvChineseApp()) {
            resId = R.string.app_introduction_1_araconv_chinese;
        } else if (AppFlavorUtil.isAraConvJapaneseApp()) {
            resId = R.string.app_introduction_1_araconv_japanese;
        } else if (AppFlavorUtil.isAraMultiPlayerApp()) {
            resId = R.string.app_introduction_1_aramultiplayer;
        } else if (AppFlavorUtil.isAraHanjaApp()) {
            resId = R.string.app_introduction_1_arahanja;
        }
        return resId;
    }
}

