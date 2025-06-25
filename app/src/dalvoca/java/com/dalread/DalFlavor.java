package com.dalread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dalread.activity.AdminActivity;
import com.dalread.activity.AppIntroductionActivity;
import com.dalread.activity.ChatDetailsActivity;
import com.dalread.activity.ChooseStudyLanguageActivity;
import com.dalread.activity.HowToUseActivity;
import com.dalread.activity.LessonListActivity;
import com.dalread.activity.MainHomeActivity;
import com.dalread.base.VocaActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.Lesson;
import com.dalread.model.ReceiveCallModel;
import com.dalread.service.callkeep.VoiceUtils;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.dalread.widget.GetLessonListForWidgetService;

public class DalFlavor extends BaseFlavor{

    public static void initData(final Activity currentActivity, SharedPreferencesDB sharedPref) {
        if (openLessonAdmin(currentActivity) || openHomeworkScreen(currentActivity) || openLessonScreen(currentActivity))
            return;

        final Class aClass;
        if (sharedPref.isShowIntroductionView()) {
            sharedPref.setShowIntroductionView(false);
            aClass = AppIntroductionActivity.class;
        } else {
            String studyLanguage = sharedPref.getStudyLanguage();
            String motherTongueLanguage = sharedPref.getMotherTongueLanguage();
            if (TextUtils.isEmpty(studyLanguage) || TextUtils.isEmpty(motherTongueLanguage)) {
                aClass = ChooseStudyLanguageActivity.class;
            } else {
                aClass = MainHomeActivity.class;
            }
        }
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openMainScreen(currentActivity, aClass);
            }
        }, Constant.NEXT_SCREEN_TIME);
    }

    public static void openMainScreen(Activity currentActivity, Class aClass) {
        openMainScreen(currentActivity, aClass, true);
    }

    public static void openMainScreen(Activity currentActivity, Class aClass, boolean isOpenFromLauncher) {
        Intent i = new Intent(currentActivity, aClass);
        i.putExtra(Constant.BUNDLE.KEY_OPEN_FROM_LAUNCHER, isOpenFromLauncher);
        currentActivity.startActivity(i);
        currentActivity.finishAffinity();
    }

    public static boolean openHomeworkScreen(final Activity currentActivity) {
        final Intent intent = currentActivity.getIntent();
        if (intent != null) {
            final String data = intent.getStringExtra(Constant.NOTIFICATION_KEY.METHOD_NAME);
            if (!Utils.isEmpty(data)) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(currentActivity, MainHomeActivity.class);
                        i.putExtra(Constant.BUNDLE.KEY_METHOD_NAME, data);
                        i.putExtra(Constant.BUNDLE.KEY_VOCA_ID, intent.getStringExtra(Constant.NOTIFICATION_KEY.VOCA_ID));
                        i.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, intent.getStringExtra(Constant.NOTIFICATION_KEY.VOCA_TYPE));
                        currentActivity.startActivity(i);
                        currentActivity.finishAffinity();
                    }
                }, Constant.NEXT_SCREEN_TIME);
                return true;
            }
        }
        return false;
    }

    public static boolean openLessonScreen(final Activity currentActivity) {
        final Intent intent = currentActivity.getIntent();
        if (intent != null) {
            final int lessonType = intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_TYPE, 0);
            if (lessonType > 0) {
                final int lessonId = intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_ID, 0);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(currentActivity, MainHomeActivity.class);
                        i.putExtra(Constant.BUNDLE.KEY_LESSON_ID, lessonId);
                        i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, lessonType);
                        currentActivity.startActivity(i);
                        currentActivity.finishAffinity();
                    }
                }, Constant.NEXT_SCREEN_TIME);
                return true;
            }
        }
        return false;
    }

    public static boolean openLessonAdmin(final Activity currentActivity) {
        final Intent intent = currentActivity.getIntent();
        if (intent != null) {
            ReceiveCallModel receiveCallModel = intent.getExtras() == null ?
                    new ReceiveCallModel() : new ReceiveCallModel(intent.getExtras());
            final boolean isAdmin = VoiceUtils.isAdminAPI(receiveCallModel.getMethod());
            if (isAdmin) {
                Lesson lesson = new Lesson().createLesson(currentActivity, receiveCallModel);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(currentActivity, MainHomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(Constant.BUNDLE.KEY_LESSON_ID, lesson.getId());
                        i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, lesson.getLessonType());
                        i.putExtra(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
                        currentActivity.startActivity(i);
                        currentActivity.finishAffinity();
                    }
                }, Constant.NEXT_SCREEN_TIME);
                return true;
            }
        }
        return false;
    }

    public static void onLoginSuccess(Context context) {
        getLessonListForWidget(context);
    }

    public static void onLogoutSuccess(Context context) {
        getLessonListForWidget(context);
    }

    private static void getLessonListForWidget(Context context) {
        GetLessonListForWidgetService.getLessonList(context);
    }

    public static int getReceiveDataScreen(Activity currentActivity, ReceiveCallModel receiveCallModel, String roomId) {
        if (currentActivity instanceof ChatDetailsActivity) {
            if (((ChatDetailsActivity) currentActivity).getChatRoomId().equalsIgnoreCase(roomId)) {
                return Constant.JITSI.SCREEN_RECEIVE_DATA.CHAT_DETAILS;
            } else if (receiveCallModel != null && VoiceUtils.isAdminAPI(receiveCallModel.getMethod())) {
                return Constant.JITSI.SCREEN_RECEIVE_DATA.MAIN;
            } else {
                return Constant.JITSI.SCREEN_RECEIVE_DATA.LESSON;
            }
        } else if (currentActivity instanceof LessonListActivity) {
            return Constant.JITSI.SCREEN_RECEIVE_DATA.LESSON;
        } else if (currentActivity instanceof AdminActivity) {
            return Constant.JITSI.SCREEN_RECEIVE_DATA.ADMIN;
        } else if (currentActivity instanceof MainHomeActivity) {
            return Constant.JITSI.SCREEN_RECEIVE_DATA.MAIN;
        }
        return Constant.JITSI.SCREEN_RECEIVE_DATA.NONE;
    }

//    public static void openSettings(VocaActivity activity) {
//        activity.openNewScreen(SettingsActivity.class);
//    }

    public static void openHelp(VocaActivity activity) {
        activity.openNewScreen(HowToUseActivity.class);
    }

//    public static void openMail(Activity activity) {
//        if (activity instanceof OnOpenNewScreen) {
//            ((OnOpenNewScreen) activity).onOpen();
//        }
//        Utils.openMail(activity);
//    }

    public static void openNaverCafe(Activity activity) {
        openNaverCafe(activity, Constant.URL_NAVER_CAFE_DALVOCA_MANUAL);
//        Utils.openWeb(activity, Constant.URL_NAVER_CAFE_ARAPLAYER_MANUAL);
    }
}