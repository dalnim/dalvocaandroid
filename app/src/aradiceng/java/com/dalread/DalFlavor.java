package com.dalread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;

import com.dalread.activity.AppIntroductionActivity;
import com.dalread.activity.MainHomeActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.base.VocaActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.ReceiveCallModel;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.LocaleHelper;
import com.dalread.util.Utils;

import java.io.InputStream;

public class DalFlavor extends BaseFlavor {
    public static void initData(Activity currentActivity, SharedPreferencesDB sharedPreferences) {
        initLanguages(sharedPreferences);
        openScreen(sharedPreferences, currentActivity);
    }


    private static void openScreen(SharedPreferencesDB sharedPreferences, Activity currentActivity) {
        if (sharedPreferences.isShowIntroductionView()) {
            // go to App Introduction view
            final Class aClass;
            aClass = AppIntroductionActivity.class;
            sharedPreferences.setShowIntroductionView(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMainScreen(currentActivity, aClass);
                }
            }, Constant.NEXT_SCREEN_TIME);
        } else {
            // go to main menu
            openMainScreen(currentActivity, MainHomeActivity.class);
        }
    }

    private static void initLanguages(SharedPreferencesDB sharedPreferences) {
        if (Utils.isEmpty(sharedPreferences.getStudyLanguage())) {
            BaseVoca.setMotherTongueLanguage(sharedPreferences, EnumLanguage.KOREAN);
            BaseVoca.setMenuLanguage(sharedPreferences, EnumLanguage.findByFormatOs(LocaleHelper.getCurrentLanguage()));
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

//    public static void openSettings(Activity activity) {
//        openNewScreen(activity, SettingsActivity.class);
//    }

    public static void openHelp(VocaActivity activity) {
    }

//    public static void openMail(Activity activity, SharedPreferencesDB sharedPreferences) {
//        final String[] toEmail = new String[]{Constant.MAIL.ARA_ONE_SOFT};
//        final String appName = activity.getString(R.string.app_name);
//        final String subject = activity.getString(R.string.mail_subject_text, appName);
//        final EnumLanguage tongueLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getDisplayLanguage());
//        String note = Constant.BASE_BLANK;
//        if (tongueLanguage.getIdApi() == EnumLanguage.KOREAN.getIdApi()) {
//            note = "네이버 카페 : https://cafe.naver.com/dalenglish";
//        }
//        final String content = activity.getString(R.string.mail_content_text,
//                appName,
//                appName,
//                Voca.getAppVersion(),
//                Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName(),
//                String.valueOf(android.os.Build.VERSION.SDK_INT),
//                note);
//        Utils.openMail(activity, toEmail, subject, content);
//    }

    public static int getReceiveDataScreen(Activity currentActivity, ReceiveCallModel receiveCallModel, String roomId) {
        return Constant.JITSI.SCREEN_RECEIVE_DATA.NONE;
    }

    public static void openNaverCafe(Activity activity) {
        openNaverCafe(activity, Constant.URL_NAVER_CAFE_ARAHANJA_MANUAL);
//        Utils.openWeb(activity, Constant.URL_NAVER_CAFE_ARAHANJA_MANUAL);
    }
}
