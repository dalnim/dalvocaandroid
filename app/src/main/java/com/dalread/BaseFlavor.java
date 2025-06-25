package com.dalread;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import com.dalread.base.EnumLanguage;
import com.dalread.activity.SettingsActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

public class BaseFlavor {

    public static void openNewScreen(Activity activity, Class aClass) {
        Intent i = new Intent(activity, aClass);
        activity.startActivity(i);
    }

    public static void openMail(Activity activity, SharedPreferencesDB sharedPreferences) {
        final String[] toEmail = new String[]{Constant.MAIL.ARA_ONE_SOFT};
        final String appName = activity.getString(R.string.app_name);
        final String subject = activity.getString(R.string.mail_subject_text, appName);
        final EnumLanguage tongueLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
        String note = Constant.BASE_BLANK;
        if (tongueLanguage.getIdApi() == EnumLanguage.KOREAN.getIdApi()) {
            if (!AppFlavorUtil.isAraKoicaApp()) {
                note = "네이버 카페 : https://cafe.naver.com/dalenglish";
            }
        }
        final String content = activity.getString(R.string.mail_content_text,
                appName,
                BaseVoca.getAppVersion(),
                Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName(),
                String.valueOf(android.os.Build.VERSION.SDK_INT),
                note);
        Utils.openMail(activity, toEmail, subject, content);
    }

    public static void openSettings(Activity activity) {
        openNewScreen(activity, SettingsActivity.class);
    }

    protected static void openNaverCafe(Activity activity, final String url) {
        Utils.openWeb(activity, url);
    }
}
