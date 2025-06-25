package com.dalread.service.callkeep;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.dalread.DalRealApplication;
import com.dalread.activity.AdminActivity;
import com.dalread.activity.LessonListActivity;
import com.dalread.activity.MainHomeActivity;
import com.dalread.base.BaseVocaActivity;
import com.dalread.model.Lesson;
import com.dalread.model.ReceiveCallModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.ApiManager;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

public class VoiceUtils {

    public static final String TAG = "VoiceUtils";

    public static boolean isAdminAPI(String data) {
        return !Utils.isEmpty(data) && (data.equalsIgnoreCase(Constant.NOTIFICATION_VALUE.REPLY_CALL_FROM_OPPONENT_ADMIN) ||
                data.equalsIgnoreCase(Constant.NOTIFICATION_VALUE.REQUEST_CALL_TO_OPPONENT_ADMIN));
    }

    public static void openScreen(Application application,
                                  Lesson lesson,
                                  ReceiveCallModel receiveCallModel,
                                  int screen) {
        try {
            if (Utils.isEmpty(receiveCallModel.getLessonId())) {
                return;
            }
            final BaseVocaActivity currentActivity = (BaseVocaActivity) ((DalRealApplication) application).getCurrentActivity();
            switch (screen) {
                case Constant.JITSI.SCREEN_RECEIVE_DATA.LESSON:
                    openLessonListScreen(currentActivity, lesson, receiveCallModel);
                    break;
                case Constant.JITSI.SCREEN_RECEIVE_DATA.CHAT_DETAILS:
                    currentActivity.eventBus.post(new SuccessEvent(BaseEvent.Screen.CHAT_DETAILS_ACTIVITY, BaseEvent.EventType.REQUEST_CALL_TO_OPPONENT, receiveCallModel));
                    break;
                case Constant.JITSI.SCREEN_RECEIVE_DATA.ADMIN:
                    currentActivity.eventBus.post(new SuccessEvent(BaseEvent.Screen.ADMIN_ACTIVITY, BaseEvent.EventType.ADMIN_API, receiveCallModel));
                    break;
                case Constant.JITSI.SCREEN_RECEIVE_DATA.MAIN:
                    if (receiveCallModel != null && isAdminAPI(receiveCallModel.getMethod())) {
                        openAdminScreen(currentActivity, receiveCallModel);
                    } else {
                        openLessonListScreen(currentActivity, lesson, receiveCallModel);
                    }
                    break;
                default:
                    openMainActivity(application, lesson, receiveCallModel);
                    break;
            }
        } catch (Exception ex) {
            openMainActivity(application, lesson, receiveCallModel);
        }
    }

    public static void openLessonListScreen(BaseVocaActivity activity,
                                            Lesson lesson,
                                            ReceiveCallModel receiveCallModel) {
        activity.eventBus.post(new SuccessEvent(BaseEvent.Screen.CHAT_DETAILS_ACTIVITY, BaseEvent.EventType.EXIT, null));
        activity.eventBus.post(new SuccessEvent(BaseEvent.Screen.LESSON_LIST_ACTIVITY, BaseEvent.EventType.EXIT, null));
        final boolean isAdmin = isAdminAPI(receiveCallModel.getMethod());
        Intent i = new Intent(activity, LessonListActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_LESSON_ID, lesson.getId());
        i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, isAdmin ?
                Constant.API_VALUE.LIST_LESSON_FOR_ADMIN : lesson.getLessonType());
        i.putExtra(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
        activity.openNewScreen(i);
    }

    public static void openMainActivity(Context context,
                                        Lesson lesson,
                                        ReceiveCallModel receiveCallModel) {
        Intent i = new Intent(context, MainHomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Constant.BUNDLE.KEY_LESSON_ID, lesson.getId());
        i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, lesson.getLessonType());
        i.putExtra(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
        context.startActivity(i);
    }

    public static void replyCallFromOpponent(Application application,
                                             Lesson lesson,
                                             ReceiveCallModel receiveCallModel,
                                             int replyCallType) {
        if (application instanceof DalRealApplication) {
            final DalRealApplication dalRealApplication = (DalRealApplication) application;
            ApiManager.replyCallFromOpponent(dalRealApplication, lesson, replyCallType, receiveCallModel.getRoomId(), null);
        }
    }

    public static void openAdminScreen(BaseVocaActivity activity,
                                            ReceiveCallModel receiveCallModel) {
        activity.eventBus.post(new SuccessEvent(BaseEvent.Screen.ADMIN_ACTIVITY, BaseEvent.EventType.EXIT, null));
        Intent i = new Intent(activity, AdminActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_ADMIN);
        i.putExtra(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
        activity.openNewScreen(i);
    }
}
