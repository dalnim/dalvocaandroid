package com.dalread.service;

import android.app.Activity;
import android.os.Bundle;

import com.dalread.DalRealApplication;
import com.dalread.activity.ChatDetailsActivity;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.Lesson;
import com.dalread.model.ReceiveCallModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.service.callkeep.CallKeepModule;
import com.dalread.util.ApiManager;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Set;

public class DalFCMService extends FirebaseMessagingService {

    private static final String TAG = "DalFCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        DLog.d(TAG, "onMessageReceived");
        super.onMessageReceived(remoteMessage);

        Bundle bundle = remoteMessage.toIntent().getExtras();
        if (bundle != null) {
            DLog.i(TAG, "bundle: " + bundle.toString());
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                DLog.i(TAG, "key: " + key);
                DLog.i(TAG, "value: " + bundle.get(key));
            }
            if (getApplication() instanceof DalRealApplication) {
                DalRealApplication application = (DalRealApplication) getApplication();
                if (isCorrectOpponent(bundle, application)) {
                    application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.ALL, BaseEvent.EventType.NOTIFICATION_RECEIVED, bundle));
                    application.getFirebaseApiImpl().trackLog(bundle.toString(), null);
                    checkTypeAPI(application, bundle);
                }
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        DLog.i(TAG, "onNewToken: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        if (getApplication() instanceof DalRealApplication) {
            DalRealApplication application = (DalRealApplication) getApplication();
            SharedPreferencesDB sharedPreferences = application.getSharedPref();
            sharedPreferences.setFirebaseToken(token);
            int uid = sharedPreferences.getRealUid();
            if (uid > 0 && Utils.isConnected(getApplicationContext())) {
                application.getDalAiImpl().updateFirebaseToken(
                        String.valueOf(uid),
                        sharedPreferences.getEmail(),
                        token,
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        null
                );
            }
        }
    }

    private boolean isCorrectOpponent(Bundle bundle, DalRealApplication application) {
        if (bundle.containsKey(Constant.NOTIFICATION_KEY.OPPONENT_UID)) {
            int opponentUid = Utils.parseInt(bundle.getString(Constant.NOTIFICATION_KEY.OPPONENT_UID));
            int uid = application.getSharedPref().getRealUid();
            return opponentUid == uid;
        }
        return true;
    }

    private void checkTypeAPI(DalRealApplication application, Bundle bundle) {
        final String method = bundle.get(Constant.NOTIFICATION_KEY.METHOD_NAME).toString();
        if (method.equals(Constant.NOTIFICATION_VALUE.REQUEST_CALL_TO_OPPONENT)) {
            checkVoiceReceiveCall(application, bundle);
        } else if (method.equals(Constant.NOTIFICATION_VALUE.REPLY_CALL_FROM_OPPONENT)) {
            checkReplyCallFromOpponent(application, bundle);
        } else if (method.equals(Constant.NOTIFICATION_VALUE.REQUEST_CALL_TO_OPPONENT_ADMIN) ||
                method.equals(Constant.NOTIFICATION_VALUE.REPLY_CALL_FROM_OPPONENT_ADMIN)) {
            checkAdminReceived(application, bundle);
        }
    }

    private void checkVoiceReceiveCall(DalRealApplication application, Bundle bundle) {
        DLog.d(TAG, "checkVoiceReceiveCall");
        ReceiveCallModel receiveCallModel = new ReceiveCallModel(bundle);
        final Activity currentActivity = application.getCurrentActivity();
        if (!application.isBackground() && currentActivity != null) {
            DLog.d(TAG, currentActivity.getLocalClassName());
            receiveCallModel.setScreen(currentActivity);
            if ((currentActivity instanceof ChatDetailsActivity)) {
                final ChatDetailsActivity activity = (ChatDetailsActivity) application.getCurrentActivity();
                if (activity.getChatRoomId().equalsIgnoreCase(receiveCallModel.getRoomId())) {
                    // If open voice call and type != RoleAdmin then auto accept
                    if (activity.isVoiceCallingFromNotification() && !activity.isRoleAdmin()) {
                        Lesson lesson = new Lesson().createLesson(application, receiveCallModel);
                        ApiManager.replyCallFromOpponent(application, lesson, Constant.JITSI.REPLY_CALL_TYPE.ACCEPT, receiveCallModel.getRoomId(), null);
                        return;
                    }
                }
            }
        }
        // CALL_TYPE is 3, then please don’t show incoming call view.
        if (!receiveCallModel.getCallType().equals("3")) {
            openIncomingCall(application, receiveCallModel);
        }
    }

    private void openIncomingCall(DalRealApplication application, ReceiveCallModel receiveCallModel) {
        DLog.d(TAG, "openIncomingCall");
        CallKeepModule rnCallKeepModule = CallKeepModule.getInstance(application);
        rnCallKeepModule.displayIncomingCall(receiveCallModel);
    }

    private void checkReplyCallFromOpponent(DalRealApplication application, Bundle bundle) {
        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.CHAT_DETAILS_ACTIVITY, BaseEvent.EventType.REPLY_CALL_FROM_OPPONENT, bundle));
        CallKeepModule.getInstance(application).rejectCall(bundle.get(Constant.NOTIFICATION_KEY.CALLER_UID).toString());
    }

    private void checkAdminReceived(DalRealApplication application, Bundle bundle) {
        DLog.d(TAG, "checkAdminReceived");
        ReceiveCallModel receiveCallModel = new ReceiveCallModel(bundle);
        Lesson lesson = new Lesson().createLesson(application, receiveCallModel);
        final BaseDalVocaPlayVocaActivity currentActivity = (BaseDalVocaPlayVocaActivity) application.getCurrentActivity();
        if (!application.isBackground() && currentActivity != null) {
            DLog.d(TAG, currentActivity.getLocalClassName());
            receiveCallModel.setScreen(currentActivity, receiveCallModel);
            if ((currentActivity instanceof ChatDetailsActivity)) {
                final ChatDetailsActivity activity = (ChatDetailsActivity) application.getCurrentActivity();
                if (activity.getChatRoomId().equalsIgnoreCase(receiveCallModel.getRoomId())) {
                    // If open voice call and type != RoleAdmin then auto accept
                    if (activity.isVoiceCallingFromNotification()) {
                        if (!activity.isRoleAdmin()) {
                            ApiManager.replyCallFromOpponent(application, lesson, Constant.JITSI.REPLY_CALL_TYPE.ACCEPT, receiveCallModel.getRoomId(), null);
                        }
                        return;
                    }
                }
            }
            currentActivity.showAdminDialog(application, lesson, receiveCallModel);
        }
    }
}
