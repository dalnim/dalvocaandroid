package com.dalread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.dalread.activity.AppIntroductionActivity;
import com.dalread.activity.ChooseMotherLanguageActivity;
import com.dalread.activity.MainHomeActivity;
import com.dalread.base.VocaActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.ReceiveCallModel;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;

public class DalFlavor extends BaseFlavor {
    public static void initData(Activity currentActivity, SharedPreferencesDB sharedPreferences) {
        Utils.initStudyLanguage(currentActivity);
        setPreferredNativeSpeakersByHardCoding(currentActivity, sharedPreferences);
        openScreen(sharedPreferences, currentActivity);
    }

    private static void setPreferredNativeSpeakersByHardCoding(Context context, SharedPreferencesDB sharedPreferences) {
        String ids = "1713,1720,2181,2188,2238";
        if (LanguageUtil.isStudyLangKorean(context)) {
            ids = "2072,2073,2075,1510";
        } else if (LanguageUtil.isStudyLangChinese(context)) {
            ids = "2069";
        } else if (LanguageUtil.isStudyLangJapanese(context)) {
            ids = "2226";
        }
        sharedPreferences.setPreferredNativeSpeakers(ids);
        sharedPreferences.setPreferredNativeSpeakersCount((int) ids.chars().filter(e -> e == ',').count());
    }

    private static void openScreen(SharedPreferencesDB sharedPreferences, Activity currentActivity) {
        if (sharedPreferences.isShowIntroductionView()) {
            // go to App Introduction view
            sharedPreferences.setShowIntroductionView(false);
            final Class aClass;
            aClass = AppIntroductionActivity.class;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMainScreen(currentActivity, aClass);
                }
            }, Constant.NEXT_SCREEN_TIME);
        } else {
            if (sharedPreferences.hasMotherTongue()) {
                // go to main menu
                openMainScreen(currentActivity, MainHomeActivity.class);
            } else {
                openMainScreen(currentActivity, ChooseMotherLanguageActivity.class);
            }
        }
    }

    public static void openMainScreen(Activity currentActivity, Class<?> aClass) {
        Intent intent = new Intent(currentActivity, aClass);
        currentActivity.startActivity(intent);
        currentActivity.finishAffinity();
    }

    public static void initDefaultData(Context context) {
    }

    public static void onLoginSuccess(Context context) {
    }

    public static void onLogoutSuccess(Context context) {
    }

    public static int getReceiveDataScreen(Activity currentActivity, ReceiveCallModel receiveCallModel, String roomId) {
        return Constant.JITSI.SCREEN_RECEIVE_DATA.NONE;
    }

    public static void openHelp(VocaActivity activity) {
    }

    public static void openNaverCafe(Activity activity) {
        if (LanguageUtil.isMotherTongueLangKorean(activity)) {
            openNaverCafe(activity, Constant.URL_NAVER_CAFE_ARACONV_MANUAL);
        } else {
            openNaverCafe(activity, Constant.URL_NAVER_CAFE_ARACONV_MANUAL_EN);
        }
    }

    public static void openNaverCafeArahangulWritingPracticePaperDownload(Activity activity) {
        final String URL_NAVER_CAFE_ARAHANGUL_WRITING_PRACTICE_PAPER_DOWNLOAD = "https://cafe.naver.com/dalenglish/856";
        openNaverCafe(activity, URL_NAVER_CAFE_ARAHANGUL_WRITING_PRACTICE_PAPER_DOWNLOAD);
    }
}
