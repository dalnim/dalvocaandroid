package com.dalread.util;

import android.content.Context;

public class MobileAd extends BaseMobileAd {
    public static String getAdsBannerId(Context context) {
        String bannerId = Admob.Test.BANNER_UNIT_ID;
        if (!Utils.isDebug()) {
            bannerId = Admob.AraHanja.BANNER_UNIT_ID;
        }
        return bannerId;
    }

    public static String getAdsRewardId(Context context) {
        String adId = Admob.Test.REWARD_UNIT_ID;
        if (Utils.isReleaseMode()) {
            adId = Admob.AraHanja.REWARD_UNIT_ID;
        }
        return adId;
    }

}
