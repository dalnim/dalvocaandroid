package com.dalread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.dalread.activity.AppIntroductionActivity;
import com.dalread.activity.ChooseMotherLanguageActivity;
import com.dalread.activity.MainHomeActivity;
import com.dalread.activity.MultiPlayerMainHomeActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.base.VocaActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.ReceiveCallModel;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.LocaleHelper;
import com.dalread.util.Utils;

public class DalFlavor extends BaseFlavor {

    public static void initData(final Activity currentActivity, SharedPreferencesDB sharedPreferences) {
        if (Utils.isEmpty(sharedPreferences.getStudyLanguage())) {
            BaseVoca.setStudyLanguage(sharedPreferences, EnumLanguage.findByFormatApi(BuildConfig.STUDY_LANG));
        }
        Intent intent;
        if (sharedPreferences.isShowIntroductionView()) {
            sharedPreferences.setShowIntroductionView(false);
            final EnumLanguage lang = EnumLanguage.findByFormatOs(LocaleHelper.getCurrentLanguage());
            sharedPreferences.setMenuLanguage(lang.getFormatApi());
            intent = new Intent(currentActivity, AppIntroductionActivity.class);
        } else {
            if (sharedPreferences.hasMotherTongue()) {
                if (AppFlavorUtil.isAraMultiPlayerApp()) {
                    intent = new Intent(currentActivity, MultiPlayerMainHomeActivity.class);
                } else {
                    intent = new Intent(currentActivity, MainHomeActivity.class);
                }
            } else {
                intent = new Intent(currentActivity, ChooseMotherLanguageActivity.class);
            }
        }
        currentActivity.startActivity(intent);
        currentActivity.finishAffinity();
    }

    public static void openMainScreen(Activity currentActivity) {
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
    public static void openNaverCafe(Activity activity) {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            openNaverCafe(activity, Constant.URL_NAVER_CAFE_ARAMULTIPLAYER_MANUAL);
        } else {
            openNaverCafe(activity, Constant.URL_NAVER_CAFE_ARAPLAYER_MANUAL);
        }
//        Utils.openWeb(activity, Constant.URL_NAVER_CAFE_ARAPLAYER_MANUAL);
    }
}
