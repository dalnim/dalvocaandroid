package com.dalread.util;

import android.content.Context;

public class MobileAd extends BaseMobileAd {
    public static String getAdsBannerId(Context context) {
        String adId = Admob.Test.BANNER_UNIT_ID;
        if (!Utils.isDebug()) {
            if (LanguageUtil.isStudyLangJapanese(context)) {
                adId = Admob.AraConv.Japanese.BANNER_UNIT_ID;
            } else if (LanguageUtil.isStudyLangChinese(context)) {
                adId = Admob.AraConv.Chinese.BANNER_UNIT_ID;
            } else if (LanguageUtil.isStudyLangKorean(context)) {
                adId = Admob.AraConv.Korean.BANNER_UNIT_ID;
            } else {
                adId = Admob.AraConv.English.BANNER_UNIT_ID;
            }
        }
        return adId;
    }
    public static String getAdsRewardId(Context context) {
        String adId = Admob.Test.REWARD_UNIT_ID;
        if (Utils.isReleaseMode()) {
            if (LanguageUtil.isStudyLangJapanese(context)) {
                adId = Admob.AraConv.Japanese.REWARD_UNIT_ID;
            } else if (LanguageUtil.isStudyLangChinese(context)) {
                adId = Admob.AraConv.Chinese.REWARD_UNIT_ID;
            } else if (LanguageUtil.isStudyLangKorean(context)) {
                adId = Admob.AraConv.Korean.REWARD_UNIT_ID;
            } else {
                adId = Admob.AraConv.English.REWARD_UNIT_ID;
            }
        }
        return adId;
    }

    public static String getInterstitialAd(Context context) {
        String adId = Admob.Test.INTERSTITIAL_UNIT_ID;
        if (Utils.isReleaseMode()) {
            if (LanguageUtil.isStudyLangJapanese(context)) {
                adId = Admob.AraConv.Japanese.INTERSTITIAL_UNIT_ID;
            } else if (LanguageUtil.isStudyLangChinese(context)) {
                adId = Admob.AraConv.Chinese.INTERSTITIAL_UNIT_ID;
            } else if (LanguageUtil.isStudyLangKorean(context)) {
                adId = Admob.AraConv.Korean.INTERSTITIAL_UNIT_ID;
            } else {
                adId = Admob.AraConv.English.INTERSTITIAL_UNIT_ID;
            }
        }
        return adId;
    }

}
