package com.dalread.activity;

import androidx.annotation.StringRes;

import com.dalread.R;
import com.dalread.util.AppFlavorUtil;

public class AppIntroduction3Fragment extends AppIntroductionFragment {
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_app_introduction_3;
    }

    protected int getStringResId() {
        @StringRes int resId = R.string.app_introduction_3_araplayer;
        if (AppFlavorUtil.isAraHangulApp()) {
            resId = R.string.app_introduction_3_arahangul;
        } else if (AppFlavorUtil.isAraConvEnglishApp()) {
            resId = R.string.app_introduction_3_araconv;
        } else if (AppFlavorUtil.isAraConvKoreaApp()) {
            resId = R.string.app_introduction_3_araconv;
        } else if (AppFlavorUtil.isAraConvChineseApp()) {
            resId = R.string.app_introduction_3_araconv;
        } else if (AppFlavorUtil.isAraConvJapaneseApp()) {
            resId = R.string.app_introduction_3_araconv;
        } else if (AppFlavorUtil.isAraMultiPlayerApp()) {
            resId = R.string.app_introduction_3_aramultiplayer;
        } else if (AppFlavorUtil.isAraHanjaApp()) {
            resId = R.string.app_introduction_3_arahanja;
        }
        return resId;
    }
}
