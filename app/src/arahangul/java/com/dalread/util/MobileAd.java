package com.dalread.util;

import android.content.Context;

public class MobileAd extends BaseMobileAd {
    public static String getAdsBannerId(Context context) {
        String bannerId = Admob.Test.BANNER_UNIT_ID;
        if (!Utils.isDebug()) {
            if (LanguageUtil.isStudyLangJapanese(context)) {
                bannerId = Admob.AraConv.Japanese.BANNER_UNIT_ID;
            } else if (LanguageUtil.isStudyLangChinese(context)) {
                bannerId = Admob.AraConv.Chinese.BANNER_UNIT_ID;
            } else if (LanguageUtil.isStudyLangKorean(context)) {
                bannerId = Admob.AraConv.Korean.BANNER_UNIT_ID;
            } else {
                bannerId = Admob.AraConv.English.BANNER_UNIT_ID;
            }
        }
        return bannerId;
    }
}
