package com.dalread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Handler;

import com.dalread.activity.AppIntroductionActivity;
import com.dalread.activity.MainHomeActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.base.VocaActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.DIC_VOCA_GROUP_CONFUSED;
import com.dalread.model.ReceiveCallModel;
import com.dalread.model.VOCABOOKS_HANJA;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;
import com.dalread.model.VOCABOOK_HANJA;
import com.dalread.model.VOCABOOK_HANJA_CLASSICS;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.LocaleHelper;
import com.dalread.util.Utils;

import java.io.IOException;
import java.io.InputStream;

public class DalFlavor extends BaseFlavor {
    //이건 맨처음 Realm DB를 만들때만 사용한다. json파일들은 Realm db가 만들어지면 필요없다.
    private static final String jsonVocabookHanja = "VOCABOOK_HANJA.json";
    private static final String jsonVocabooksHanja = "VOCABOOKS_HANJA.json";
    private static final String jsonVocaGroupConfused = "DIC_VOCA_GROUP_CONFUSED.json";
    private static final String jsonDicHanja = "DIC_HANJA.json";
    private static final String jsonVocabookHanjaClassics = "VOCABOOK_HANJA_CLASSICS.json";
    private static final String jsonVocabooksHanjaClassics = "VOCABOOKS_HANJA_CLASSICS.json";
    private static final String jsonHanjaBook = "DIC_HANJA_BOOK.json";
    private static final String jsonHanjaSentence = "DIC_HANJA_SENTENCE.json";

    public static void initData(Activity currentActivity, SharedPreferencesDB sharedPreferences) {
        initLanguages(sharedPreferences);

        if (Utils.isDebug() && isJsonDbExist(currentActivity)) {
            createRealmFromJson(currentActivity, sharedPreferences);
        } else {
            openScreen(sharedPreferences, currentActivity);
        }
    }

    private static void createRealmFromJson(Activity currentActivity, SharedPreferencesDB sharedPreferences) {
        new AsyncTask<Void, Float, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // debug
                long startTime = System.currentTimeMillis();

                // copy data to app's DB
                BaseVoca.executeRealmTransaction(appRealm -> {
                    onProgressUpdate(0f);
                    try {
                        appRealm.createAllFromJson(VOCABOOK_HANJA.class, currentActivity.getAssets().open(jsonVocabookHanja));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onProgressUpdate(1 / 8f);
                    try {
                        appRealm.createAllFromJson(VOCABOOKS_HANJA.class, currentActivity.getAssets().open(jsonVocabooksHanja));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onProgressUpdate(2 / 8f);
                    try {
                        appRealm.createAllFromJson(DIC_VOCA_GROUP_CONFUSED.class, currentActivity.getAssets().open(jsonVocaGroupConfused));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onProgressUpdate(3 / 8f);
                    try {
                        appRealm.createAllFromJson(DIC_HANJA.class, currentActivity.getAssets().open(jsonDicHanja));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onProgressUpdate(4 / 8f);
                    try {
                        appRealm.createAllFromJson(VOCABOOK_HANJA_CLASSICS.class, currentActivity.getAssets().open(jsonVocabookHanjaClassics));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onProgressUpdate(5 / 8f);
                    try {
                        appRealm.createAllFromJson(VOCABOOKS_HANJA_CLASSICS.class, currentActivity.getAssets().open(jsonVocabooksHanjaClassics));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onProgressUpdate(6 / 8f);
                    try {
                        appRealm.createAllFromJson(DIC_HANJA_BOOK.class, currentActivity.getAssets().open(jsonHanjaBook));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onProgressUpdate(7 / 8f);
                    try {
                        appRealm.createAllFromJson(DIC_HANJA_SENTENCE.class, currentActivity.getAssets().open(jsonHanjaSentence));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onProgressUpdate(1f);
                });

                // debug
                long endTime = System.currentTimeMillis();
                DLog.i("HUY", "Init DB takes " + (endTime - startTime) + " ms");
                return null;
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            protected void onProgressUpdate(Float... values) {
                super.onProgressUpdate(values);

                Loading.show(currentActivity, currentActivity.getString(R.string.init_data, Math.round(values[0] * 100)));
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Loading.hide();
                openScreen(sharedPreferences, currentActivity);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            BaseVoca.setStudyLanguage(sharedPreferences, EnumLanguage.HANJA);
            BaseVoca.setMotherTongueLanguage(sharedPreferences, EnumLanguage.KOREAN);
            BaseVoca.setMenuLanguage(sharedPreferences, EnumLanguage.findByFormatOs(LocaleHelper.getCurrentLanguage()));
        }
    }

    private static boolean isJsonDbExist(Activity currentActivity) {
        boolean isJsonDbExist = false;
        AssetManager am = currentActivity.getAssets() ;
        InputStream is = null ;

        try {
            is = am.open(jsonVocabookHanja) ;
            isJsonDbExist = true;
        } catch (Exception e) {
            e.printStackTrace();
            isJsonDbExist = false;
        }

        if (is != null) {
            try {
                is.close() ;
            } catch (Exception e) {
                e.printStackTrace() ;
                isJsonDbExist = false;
            }
        }
        return isJsonDbExist;
    }

    public static void openMainScreen(Activity currentActivity, Class<?> aClass) {
        Intent intent = new Intent(currentActivity, aClass);
        if (currentActivity.getIntent().hasExtra(Constant.OPEN_ARA_HANJA_DATA_KEY)) {
            intent.putExtra(Constant.OPEN_ARA_HANJA_DATA_KEY, currentActivity.getIntent().getStringExtra(Constant.OPEN_ARA_HANJA_DATA_KEY));
        }
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
        openNaverCafe(activity, Constant.URL_NAVER_CAFE_ARAHANJA_MANUAL);
//        Utils.openWeb(activity, Constant.URL_NAVER_CAFE_ARAHANJA_MANUAL);
    }
}
