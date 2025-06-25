package com.dalread.util;

import android.content.Context;

import com.dalread.BuildConfig;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;

public class MobileAd extends BaseMobileAd {
    public static String getAdsBannerId(Context context) {
        String adId = Admob.Test.BANNER_UNIT_ID;
        if (!BuildConfig.DEBUG) {
            if (AppFlavorUtil.isAraMultiPlayerApp()) {
                adId = Admob.AraMultiPlayer.Lite.BANNER_UNIT_ID;
            } else {
                adId = Admob.AraPlayer.English.BANNER_UNIT_ID;
                EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getStudyLanguage());
                if (studyLanguage == EnumLanguage.JAPANESE) {
                    adId = Admob.AraPlayer.Japanese.BANNER_UNIT_ID;
                } else if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
                    adId = Admob.AraPlayer.Chinese.BANNER_UNIT_ID;
                }
            }
        }
        return adId;
    }

    public static String getInterstateId(Context context) {
        String adId = Admob.Test.INTERSTITIAL_UNIT_ID;
        if (!BuildConfig.DEBUG) {
            if (AppFlavorUtil.isAraMultiPlayerApp()) {
                adId = Admob.AraMultiPlayer.Lite.INTERSTITIAL_UNIT_ID;
            } else {
                adId = Admob.AraPlayer.English.INTERSTITIAL_UNIT_ID;

                EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getStudyLanguage());
                if (studyLanguage == EnumLanguage.JAPANESE) {
                    adId = Admob.AraPlayer.Japanese.INTERSTITIAL_UNIT_ID;
                } else if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
                    adId = Admob.AraPlayer.Chinese.INTERSTITIAL_UNIT_ID;
                }
            }
        }
        return adId;
    }



    public static String getAdsRewardId(Context context) {
        String adId = Admob.Test.REWARD_UNIT_ID;
        if (Utils.isReleaseMode()) {
            if (AppFlavorUtil.isAraMultiPlayerApp()) {
                adId = Admob.AraMultiPlayer.Lite.REWARD_UNIT_ID;
            } else {
                if (LanguageUtil.isStudyLangJapanese(context)) {
                    adId = Admob.AraPlayer.Japanese.REWARD_UNIT_ID;
                } else if (LanguageUtil.isStudyLangChinese(context)) {
                    adId = Admob.AraPlayer.Chinese.REWARD_UNIT_ID;
                } else {
                    adId = Admob.AraPlayer.English.REWARD_UNIT_ID;
                }
            }
        }
        return adId;
    }
    public static String getInterstitialAd(Context context) {
        String adId = Admob.Test.INTERSTITIAL_UNIT_ID;
        if (Utils.isReleaseMode()) {
            if (LanguageUtil.isStudyLangJapanese(context)) {
                adId = Admob.AraPlayer.Japanese.INTERSTITIAL_UNIT_ID;
            } else if (LanguageUtil.isStudyLangChinese(context)) {
                adId = Admob.AraPlayer.Chinese.INTERSTITIAL_UNIT_ID;
            } else {
                adId = Admob.AraPlayer.English.INTERSTITIAL_UNIT_ID;
            }
        }
        return adId;
    }
}
