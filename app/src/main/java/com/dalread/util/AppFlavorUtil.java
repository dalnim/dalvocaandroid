package com.dalread.util;

import com.dalread.R;

public class AppFlavorUtil {

    public static boolean isAraConvApp() {
        return isAraConvEnglishApp() || isAraConvKoreaApp() || isAraConvChineseApp() || isAraConvJapaneseApp();
    }
    public static boolean isAraMultiPlayerApp() {
        return isAraMultiPlayerAppLite() || isAraMultiPlayerAppPro();
    }
    public static boolean isAraMultiPlayerAppLite() {
        return "com.araonesoft.aramultiplayer".equals(Utils.getApplicationId());
    }
    public static boolean isAraMultiPlayerAppPro() {
        return "com.araonesoft.aramultiplayerpro".equals(Utils.getApplicationId());
    }
    public static boolean isAraHanjaApp() {
        return "com.dalnimsoft.arahanja".equals(Utils.getApplicationId());
    }
    public static boolean isAraKoicaApp() {
        return "com.dalnimsoft.arakoica".equals(Utils.getApplicationId());
    }
    public static boolean isAraHangulApp() {
        return "com.dalnimsoft.arahangul".equals(Utils.getApplicationId());
    }
    public static boolean isAraPlayerApp() {
        return isAraPlayerEnglishApp() || isAraPlayerChineseApp() || isAraPlayerJapaneseApp();
    }

    public static boolean isAraPlayerEnglishApp() {
        return "com.dalnimsoft.araplayer".equals(Utils.getApplicationId());
    }
    public static boolean isAraPlayerChineseApp() {
        return "com.dalnimsoft.araplayer_chinese".equals(Utils.getApplicationId());
    }
    public static boolean isAraPlayerJapaneseApp() {
        return "com.dalnimsoft.araplayer_japanese".equals(Utils.getApplicationId());
    }

    public static boolean isAraMusicApp() {
        return isAraMusicEnglishApp() || isAraMusicChineseApp() || isAraMusicJapaneseApp();
    }

    public static boolean isAraMusicEnglishApp() {
        return "com.dalnimsoft.aramusic".equals(Utils.getApplicationId());
    }
    public static boolean isAraMusicChineseApp() {
        return "com.dalnimsoft.aramusic_chinese".equals(Utils.getApplicationId());
    }
    public static boolean isAraMusicJapaneseApp() {
        return "com.dalnimsoft.aramusic_japanese".equals(Utils.getApplicationId());
    }

    public static boolean isAraConvEnglishApp() {
        return "com.dalnimsoft.araconv".equals(Utils.getApplicationId());
    }
    public static boolean isAraConvKoreaApp() {
        return "com.dalnimsoft.araconv_korea".equals(Utils.getApplicationId());
    }
    public static boolean isAraConvChineseApp() {
        return "com.dalnimsoft.araconv_chinese".equals(Utils.getApplicationId());
    }
    public static boolean isAraConvJapaneseApp() {
        return "com.dalnimsoft.araconv_japanese".equals(Utils.getApplicationId());
    }
    public static boolean isDalvocaApp() {
        return "com.dalnimsoft.dalvoca".equals(Utils.getApplicationId());
    }

    public static boolean isAraPlayerTestApp() {
        return "com.dalnimsoft.araplayer_test".equals(Utils.getApplicationId());
    }
}
