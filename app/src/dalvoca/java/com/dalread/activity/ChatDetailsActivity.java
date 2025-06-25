package com.dalread.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.dalread.asynctask.GetTableVersionAndDownloadTableTask;
import com.dalread.base.BaseChatDetailsFragment;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.component.ChatPopupWindow;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.InfoDialog;
import com.dalread.dialog.MakeACallDialog;
import com.dalread.dialog.StudentOptionDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.ChatRoom;
import com.dalread.model.ChatRoomInfo;
import com.dalread.model.Lesson;
import com.dalread.model.LessonOption;
import com.dalread.model.ReceiveCallModel;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.BaseEvent.EventType;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.ApiManager;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.dalread.util.FormatTime;
import com.dalread.util.Loading;
import com.dalread.util.PermissionUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.facebook.react.modules.core.PermissionListener;

import org.greenrobot.eventbus.Subscribe;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

import static com.dalread.util.PermissionUtils.REQUEST_CODE_RECORD_CAMERA_READ_PHONE;

public class ChatDetailsActivity extends BaseDalVocaPlayVocaActivity
        implements JitsiMeetActivityInterface, JitsiMeetViewListener {

    @BindView(R.id.llLayout)
    LinearLayout llLayout;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.tvCalling)
    TextView tvCalling;

    @BindString(R.string.tpl_lesson_will_be_finished)
    String tplLessonWillBeFinished;

    private Context context;
    private ChatRoom chatRoom;
    private ChatRoomInfo chatRoomInfo;
    private AlertDialog alertDialog;
    private ChatPopupWindow chatPopupWindow;
    private boolean isChatMode;
    private Bundle chatModeBundle;
    private Bundle studyModeBundle;
    private Lesson lesson;
    private LessonOption lessonOption;
    private int studyRole;
    private boolean isStudying;
    private ConfirmationDialog confirmationDialog;
    private StudentOptionDialog studentOptionDialog;
    private boolean isPaused;

    private JitsiMeetView view;
    private JitsiMeetConferenceOptions options;
    private ReceiveCallModel receiveCallModel;
    private final Handler mHandler = new Handler();
    private int secondsVoiceCall = 0;
    private boolean isSendReplyFromOpponent = true;
    private InfoDialog infoDialog;
    private boolean isCalling = false;
    private int msgCalling;
    // check show popup menu
    private int typeCall = Constant.JITSI.CALl_TYPE.HIDE;

    private View statusBarView;
    private int decorHeight, viewHeight;
    private float positionYStatusBar;
    private ConfirmationDialog exitVoiceCallDialog;
    private MakeACallDialog makeACallDialog;
    private boolean isJoinCall = false;
    private ConfirmationDialog exitVoiceDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_chat_details;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEventBus();
        initData();
        initLayout();

        getTableVersionAndDownloadTable();
        updateFirebaseToken();

        llLayout.post(new Runnable() {
            @Override
            public void run() {
                decorHeight = getWindow().getDecorView().getHeight();
                viewHeight = llLayout.getHeight();
                // get position Y statusBar
                int[] location = new int[2];
                llLayout.getLocationOnScreen(location);
                positionYStatusBar = location[1];

                DLog.d(getLogTag(), "decorHeight=" + decorHeight
                        + " - viewHeight=" + viewHeight
                        + " - positionYStatusBar=" + positionYStatusBar);
            }
        });
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {
        showChatPopupWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();

        JitsiMeetActivityDelegate.onHostResume(this);
        if (isPaused) {
            isPaused = false;
            updateAppStateMode(Constant.API_VALUE.APP_STATE_MODE_FOREGROUND);
        }
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            exitStudyModeInTheChatroom();
            cancelPendingFinishLessonToast();
            destroyJitsiView();
        } else {
            isPaused = true;
            updateAppStateMode(Constant.API_VALUE.APP_STATE_MODE_BACKGROUND);
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        JitsiMeetActivityDelegate.onHostPause(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        dismissVoiceCallDialog();

        super.onDestroy();
    }

    private void initData() {
        context = this;
        Intent intent = getIntent();
        chatRoom = (ChatRoom) intent.getSerializableExtra(Constant.BUNDLE.KEY_CHAT_ROOM);
        lesson = (Lesson) intent.getSerializableExtra(Constant.BUNDLE.KEY_LESSON);
        receiveCallModel = (ReceiveCallModel) intent.getSerializableExtra(Constant.BUNDLE.KEY_VOICE_DATA);
    }

    private void initLayout() {
        chatPopupWindow = new ChatPopupWindow(this, toolbar.getIconRight(), sharedPreferences);
        chatPopupWindow.setListener(onChatPopupItemClickListener);

        alertDialog = new AlertDialog(context);

        confirmationDialog = new ConfirmationDialog(context, onConfirmQuitStudyingListener);
        confirmationDialog.setMyTitle(R.string.btn_confirm);
        confirmationDialog.setMessage(R.string.msg_confirm_exit_study_mode);
        confirmationDialog.setNegativeText(R.string.no);
        confirmationDialog.setPositiveText(R.string.yes);

        studentOptionDialog = new StudentOptionDialog(context);
        makeACallDialog = new MakeACallDialog(this, onMakeACallDialogListener);
    }

    private void getTableVersionAndDownloadTable() {
        new GetTableVersionAndDownloadTableTask(application).start();
    }

    private void updateFirebaseToken() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().updateFirebaseToken(new DalApiListener<Boolean>() {

                    @Override
                    public void onSuccess(Boolean response) {
                        afterUpdateFirebaseToken();
                    }

                    @Override
                    public void onFailure(String error) {
                        afterUpdateFirebaseToken();
                    }
                });
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void afterUpdateFirebaseToken() {
        if (isFromLessonList()) {
            createOrGetStudyModeAndJoin();
        } else {
            getChatroomIDandAllUserInfo();
        }
    }

    private void createOrGetStudyModeAndJoin() {
        application.getDalAiImpl().createOrGetStudyModeAndJoin(
                isFromLessonList() ? lesson.getStudyLangCode() : sharedPreferences.getLangStudyCode(),
                getStudyRole(),
                lesson.getStudentId(),
                lesson.getTutorId(),
                lesson.getId(),
                new DalApiListener<ChatRoomInfo>() {

                    @Override
                    public void onSuccess(ChatRoomInfo response) {
                        setChatUserListToPopup(chatRoomInfo = response);
                        Utils.loadFragment(ChatDetailsActivity.this, getStudyModeFragment(), getFragmentContainerId(), false);
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                }
        );
    }

    private void getChatroomIDandAllUserInfo() {
        int chatRoomId = chatRoom.getId();
        int chatRoomType = chatRoomId > 0 ? 0 : chatRoom.getChatRoomType();
        application.getDalAiImpl().getChatroomIDandAllUserInfo(
                chatRoom.getOpponentUid(),
                0, // temp
                chatRoomType,
                chatRoomId,
                new DalApiListener<ChatRoomInfo>() {

                    @Override
                    public void onSuccess(ChatRoomInfo response) {
                        setChatUserListToPopup(chatRoomInfo = response);
                        Utils.loadFragment(ChatDetailsActivity.this, getChatModeFragment(), getFragmentContainerId(), false);
                        Loading.hide();
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                }
        );
    }

    public void setChatUserListToPopup(ChatRoomInfo chatRoomInfo) {
        chatPopupWindow.setChatUsers(chatRoomInfo.getUserList());
    }

    public void setStudyUserListToPopup(ChatRoomInfo studyInfo) {
        chatPopupWindow.setStudyUsers(studyInfo.getUserList());
    }

    public void setStudyRole(int studyRole) {
        this.studyRole = studyRole;
    }

    private boolean isFromLessonList() {
        return lesson != null;
    }

    private int getStudyRole() {
        int lessonType = lesson.getLessonType();
        if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT)
            return Constant.STUDY_ROLE_STUDENT;
        if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR)
            return Constant.STUDY_ROLE_TUTOR;
        return Constant.STUDY_ROLE_OBSERVER;
    }

    @OnClick(R.id.tvCalling)
    void onClick() {
        openVoiceExitDialog();
    }

    private void showChatPopupWindow() {
        DLog.d(getLogTag(), "showChatPopupWindow");
        if (isCalling) {
            typeCall = Constant.JITSI.CALl_TYPE.CALLING;
        } else if (isVoiceCallingFromNotification()) {
            typeCall = Constant.JITSI.CALl_TYPE.CALLED;
        } else {
            typeCall = Constant.JITSI.CALl_TYPE.NONE;
        }
        chatPopupWindow.show(isChatMode,
                view != null && view.getVisibility() == View.VISIBLE ?
                        Constant.JITSI.CALl_TYPE.HIDE : typeCall);
    }

    private ChatPopupWindow.OnItemClickListener onChatPopupItemClickListener = new ChatPopupWindow.OnItemClickListener() {

        @Override
        public void onStudyModeClick() {
            chatPopupWindow.dismiss();
            flipScreen(getStudyModeFragment());
        }

        @Override
        public void onChatModeClick() {
            chatPopupWindow.dismiss();
            flipScreen(getChatModeFragment());
        }

        @Override
        public void onVoiceCallClick() {
            chatPopupWindow.dismiss();
        }

        @Override
        public void onUseSpeakerPhoneClick() {
        }

        @Override
        public void onMuteClick() {
        }

        @Override
        public void onCurrentGuideClick() {
            chatPopupWindow.dismiss();
            Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
            if (fragment instanceof StudyModeFragment) {
                ((StudyModeFragment) fragment).onCurrentGuideClick();
            }
        }

        @Override
        public void onStudentOptionClick() {
            chatPopupWindow.dismiss();
            openLessonOptionScreen();
        }

        @Override
        public void onMessageClick() {
            chatPopupWindow.dismiss();
            Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
            if (fragment instanceof StudyModeFragment) {
                ((StudyModeFragment) fragment).onMenuMessageClick();
            }
        }

        @Override
        public void onChatMessageClick() {
            chatPopupWindow.dismiss();
            Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
            if (fragment instanceof StudyModeFragment) {
                ((StudyModeFragment) fragment).onMenuChatMessageClick();
            }
        }

        @Override
        public void onInviteClick() {
            chatPopupWindow.dismiss();
            ToastUtil.getInstance(context).show(R.string.msg_under_development);
        }

        @Override
        public void onChangeRoleClick() {
            chatPopupWindow.dismiss();
            ToastUtil.getInstance(context).show(R.string.msg_under_development);
        }

        @Override
        public void onFinishStudyClick() {
            onChatModeClick();
            exitStudyModeInTheChatroom();
        }

        @Override
        public void onAboutLessonClick() {
            chatPopupWindow.dismiss();
            openAboutLessonScreen();
        }

        @Override
        public void onCallClick() {
            chatPopupWindow.dismiss();
            if (typeCall == Constant.JITSI.CALl_TYPE.NONE) {
                makeACallDialog.show();
            } else if (typeCall == Constant.JITSI.CALl_TYPE.CALLED) {
                showVoiceCall();
            }
        }

        @Override
        public void onRefreshUserListClick() {
            chatPopupWindow.dismiss();
            Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
            if (fragment instanceof StudyModeFragment) {
                ((StudyModeFragment) fragment).onRefreshUserListClick(true);
            }
        }

        @Override
        public void onShowHideStudentMeaningClick(boolean isShow) {
            chatPopupWindow.dismiss();
            Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
            if (fragment instanceof StudyModeFragment) {
                ((StudyModeFragment) fragment).onShowHideStudentMeaningClick(isShow);
            }
        }
    };

    private Fragment getChatModeFragment() {
        isChatMode = true;
        ChatModeFragment fragment = new ChatModeFragment();
        if (chatModeBundle == null) {
            chatModeBundle = new Bundle();
            chatModeBundle.putSerializable(Constant.BUNDLE.KEY_CHAT_ROOM_INFO, chatRoomInfo);
        }
        fragment.setArguments(chatModeBundle);
        return fragment;
    }

    private Fragment getStudyModeFragment() {
        isChatMode = false;
        StudyModeFragment fragment = new StudyModeFragment();
        if (studyModeBundle == null) {
            studyModeBundle = new Bundle();
            studyModeBundle.putSerializable(Constant.BUNDLE.KEY_CHAT_ROOM_INFO, chatRoomInfo);
            if (isFromLessonList()) {
                studyModeBundle.putSerializable(Constant.BUNDLE.KEY_LESSON, lesson);
            }
            studyModeBundle.putBoolean(Constant.BUNDLE.KEY_IS_OPEN, true);
        } else {
            studyModeBundle.putBoolean(Constant.BUNDLE.KEY_IS_OPEN, false);
        }
        fragment.setArguments(studyModeBundle);
        return fragment;
    }

    private void flipScreen(final Fragment fragment) {
        toolbar.postDelayed(new Runnable() {

            @Override
            public void run() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.animator.card_flip_right_in,
                                R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in,
                                R.animator.card_flip_left_out)
                        .replace(getFragmentContainerId(), fragment)
                        .commit();
            }
        }, 500);
    }

    public void setToolbarTitle(int title) {
        toolbar.setTitle(title);
    }

    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    public void exitStudyModeInTheChatroom() {
        if (chatRoomInfo == null)
            return;
        if (Utils.isConnected(context)) {
            application.getDalAiImpl().exitStudyModeInTheChatroom(
                    chatRoomInfo.getFirestoreChatRoomId(),
                    isFromLessonList() ? lesson.getId() : 0,
                    studyRole,
                    new DalApiListener<Boolean>() {

                        @Override
                        public void onSuccess(Boolean response) {
                            if (response) {
                                studyModeBundle = null;
                                setStudying(false);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment instanceof BaseChatDetailsFragment) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (isVoiceCalling()) {
            hideVoiceCall();
            return;
        } else if (isStudying && isChatMode) {
            flipScreen(getStudyModeFragment());
            return;
        } else if (isHideCalling()) {
            openExitWhenVoiceHideDialog();
            return;
        } else if (isStudying && (studyRole == Constant.STUDY_ROLE_STUDENT || studyRole == Constant.STUDY_ROLE_TUTOR)) {
            confirmationDialog.show();
            return;
        }
        JitsiMeetActivityDelegate.onBackPressed();
        super.onBackPressed();
    }

    private ConfirmationDialog.OnDialogClickListener onConfirmQuitStudyingListener = new ConfirmationDialog.OnDialogClickListener() {

        @Override
        public void onPositive(DialogInterface dialog) {
            dialog.dismiss();
            isStudying = false;
            finish();
        }

        @Override
        public void onNegative(DialogInterface dialog) {
            dialog.dismiss();
        }
    };

    public void setStudying(boolean studying) {
        isStudying = studying;
    }

    public void setPendingFinishLessonToast(LessonOption lessonOption) {
        this.lessonOption = lessonOption;
        if (lessonOption == null || lesson.getId() == 0)
            return;
        long delayMillis;
        if (lesson.hasNextLessonInARow()) {
            delayMillis = DateUtils.secondsToMillis(lesson.getLessonFinishTimeTS())
                    - System.currentTimeMillis()
                    - TimeUnit.MINUTES.toMillis(lessonOption.getPnMinutesBeforeFinishLesson());
        } else {
            delayMillis = DateUtils.secondsToMillis(lesson.getLessonFinishTimeTS() - lesson.getLessonStartTimeTS())
                    - TimeUnit.MINUTES.toMillis(lessonOption.getPnMinutesBeforeFinishLesson());
        }
        DLog.i(getLogTag(), "setPendingFinishLessonToast: " + TimeUnit.MILLISECONDS.toMinutes(delayMillis) + " minutes");
        toolbar.postDelayed(finishLessonToastRunnable, delayMillis);
    }

    private void cancelPendingFinishLessonToast() {
        DLog.i(getLogTag(), "cancelPendingFinishLessonToast");
        toolbar.removeCallbacks(finishLessonToastRunnable);
    }

    private Runnable finishLessonToastRunnable = new Runnable() {

        @Override
        public void run() {
            int minutesBeforeFinishLesson = lessonOption.getPnMinutesBeforeFinishLesson();
            String message = String.format(
                    tplLessonWillBeFinished,
                    minutesBeforeFinishLesson + " " + getResources().getQuantityString(R.plurals.minute, minutesBeforeFinishLesson)
            );
            ToastUtil.getInstance(context).show(message);
        }
    };

    public void showStudentOptionDialog() {
        studentOptionDialog.show(lesson, lessonOption);
    }

    private void openLessonOptionScreen() {
        Intent intent = new Intent(context, StudentLessonOptionActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, lesson.getStudentId());
        intent.putExtra(Constant.BUNDLE.KEY_OTHER_USER_NAME, lesson.getStudentName());
        openNewScreenForResult(intent, Constant.REQUEST_CODE.STUDY_OPTION);
    }

    private void checkVoiceCall() {
        DLog.d(getLogTag(), "checkVoiceCall");
        if (chatRoomInfo == null || lesson == null) {
            DLog.d(getLogTag(), "chatRoomInfo is null or lesson is null");
            return;
        }
        DLog.d(getLogTag(), chatRoomInfo.toString());
        if (isVoiceCallingFromNotification()) {
            DLog.d(getLogTag(), "jitsi calling");
            return;
        }
        dismissDialog();
        checkPermissionVoiceCall();
    }

    private void requestCallToOpponent() {
        // observer not send api
        if (isRoleAdmin() || isJoinCall()) return;
        // don't need call when receive
        if (receiveCallModel != null) return;
        final int opponentStudyRole;
        final String room = Voca.generateVoiceRoomCall(chatRoomInfo.getFirestoreChatRoomId());
        final int uid, opponentUid;
        if (lesson.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
            uid = lesson.getStudentId();
            opponentUid = lesson.getTutorId();
            opponentStudyRole = Constant.STUDY_ROLE_TUTOR;
        } else if (lesson.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR) {
            uid = lesson.getTutorId();
            opponentUid = lesson.getStudentId();
            opponentStudyRole = Constant.STUDY_ROLE_STUDENT;
        } else {
            uid = Utils.parseInt(sharedPreferences.getUid());
            opponentUid = 0;
            opponentStudyRole = 0;
        }
        application.getDalAiImpl().requestCallToOpponent("", uid,
                opponentUid,
                room,
                Constant.JITSI.TYPE.VOICE,
                chatRoomInfo.getFirestoreChatRoomId(),
                lesson.getId(),
                opponentStudyRole,
                null);
    }

    private void checkPermissionVoiceCall() {
        DLog.d(getLogTag(), "checkPermissionVoiceCall");
        eventBus.post(new SuccessEvent(BaseEvent.Screen.STUDY_MODE_FRAGMENT, EventType.VOICE_CALL, Constant.JITSI.STATUS.NONE));
        if (PermissionUtils.checkRecordReadCameraPermission(this, true)) {
            initJitsiView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_RECORD_CAMERA_READ_PHONE:
                if (PermissionUtils.checkRecordReadCameraPermission(this)) {
                    initJitsiView();
                }
        }
    }

    private void initJitsiView() {
        DLog.d(getLogTag(), "initJitsiView");
        if (view == null) {
            view = new JitsiMeetView(this);
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            view.setListener(this);
            view.setVisibility(View.GONE);
        }
        if (options == null) {
            initJitsiOptions();
        }
        isSendReplyFromOpponent = true;
        requestCallToOpponent();
        if (isRoleAdmin() || isJoinCall() || receiveCallModel != null) {
            isCalling = false;
        }
        startTimerVoiceCall();
    }

    private void initJitsiOptions() {
        final String room = Voca.generateVoiceRoomCall(chatRoomInfo.getFirestoreChatRoomId());
        try {
            final JitsiMeetUserInfo userJitsi = new JitsiMeetUserInfo();
            userJitsi.setDisplayName(receiveCallModel != null ? receiveCallModel.getName() : sharedPreferences.getUserName());
            boolean isMute = isRoleAdmin();
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(Constant.JITSI.SERVER_URL))
                    .setFeatureFlag("call-integration.enabled", false)
                    .setRoom(room)
                    .setAudioOnly(false)
                    .setAudioMuted(isMute)
                    .setVideoMuted(true)
                    .setWelcomePageEnabled(false)
                    .setUserInfo(userJitsi)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void joinVoiceCalling() {
        DLog.d(getLogTag(), "joinVoiceCalling");
        hideVoiceCall();
        eventBus.post(new SuccessEvent(BaseEvent.Screen.STUDY_MODE_FRAGMENT, EventType.VOICE_CALL, Constant.JITSI.STATUS.CALL));
    }

    private void joinVoiceCalled() {
        DLog.d(getLogTag(), "joinVoiceCalled");
        view.join(options);
        llLayout.addView(view);
        hideVoiceCall();
        eventBus.post(new SuccessEvent(BaseEvent.Screen.STUDY_MODE_FRAGMENT, EventType.VOICE_CALL, Constant.JITSI.STATUS.CALL));
    }

    private void hideVoiceCall() {
        DLog.d(getLogTag(), "hideVoiceCall");
        fragmentContainer.setVisibility(View.VISIBLE);
        toolbar.showIconRight();
        tvCalling.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        eventBus.post(new SuccessEvent(BaseEvent.Screen.STUDY_MODE_FRAGMENT, EventType.UPDATE_TITLE, null));
    }

    private void showVoiceCall() {
        DLog.d(getLogTag(), "showVoiceCall");
        view.setVisibility(View.VISIBLE);
        toolbar.hideIconRight();
        tvCalling.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.GONE);
        setToolbarTitle(R.string.voice_call_title);
    }

    private void leaveVoiceCall() {
        if (!isVoiceCallingFromNotification()) return;
        DLog.d(getLogTag(), "leaveVoiceCall - sendReply=" + isSendReplyFromOpponent);
        isCalling = false;
        hideVoiceCall();
        tvCalling.setVisibility(View.GONE);
        llLayout.removeView(view);
        view.leave();
        eventBus.post(new SuccessEvent(BaseEvent.Screen.STUDY_MODE_FRAGMENT, EventType.VOICE_CALL, Constant.JITSI.STATUS.NONE));
        stopTimerVoiceCall();
        if (isSendReplyFromOpponent && !isJoinCall()) {
            replyCallFromOpponent(Constant.JITSI.REPLY_CALL_TYPE.END);
        }
        receiveCallModel = null;
        resetJitsiView();
    }

    private void destroyJitsiView() {
        DLog.d(getLogTag(), "destroyJitsiView");
        unRegisterEventBus();
        stopTimerVoiceCall();
        if (view != null) {
            view.leave();
            view.dispose();
        }
        options = null;
        view = null;
        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    private boolean isVoiceCalling() {
        DLog.d(getLogTag(), "isVoiceCalling");
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    private boolean isHideCalling() {
        return tvCalling != null && tvCalling.getVisibility() == View.VISIBLE;
    }

    public boolean isVoiceCallingFromNotification() {
        DLog.d(getLogTag(), "isVoiceCallingFromNotification");
        return isVoiceCalling() || isHideCalling();
    }

    public boolean isRoleAdmin() {
        return lesson != null && lesson.isAdmin();
    }

    public boolean isJoinCall() {
        return isJoinCall;
    }

    public String getChatRoomId() {
        return String.valueOf(chatRoomInfo.getFirestoreChatRoomId());
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {
        DLog.d(getLogTag(), "requestPermissions");
    }

    @Override
    public void onConferenceJoined(Map<String, Object> map) {
        DLog.d(getLogTag(), "onConferenceJoined");
    }

    @Override
    public void onConferenceTerminated(Map<String, Object> map) {
        DLog.d(getLogTag(), "onConferenceTerminated");
        leaveVoiceCall();
    }

    @Override
    public void onConferenceWillJoin(Map<String, Object> map) {
        DLog.d(getLogTag(), "onConferenceWillJoin");
        mHandler.post(runnableFullscreen);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        DLog.d(getLogTag(), "onPointerCaptureChanged");
    }

    @Subscribe
    public void onEvent(ErrorEvent event) {
        DLog.d(getLogTag(), "ErrorEvent=" + event.getScreen() + " - type=" + event.getEventType());
    }

    @Subscribe
    public void onEvent(final SuccessEvent event) {
        DLog.d(getLogTag(), "SuccessEvent=" + event.getScreen() + " - type=" + event.getEventType());
        if (event.getScreen() == BaseEvent.Screen.CHAT_DETAILS_ACTIVITY) {
            switch (event.getEventType()) {
                case CALL_BACK_SUCCESS:
                    if (receiveCallModel != null) {
                        checkVoiceCall();
                    }
                    break;
                case VOICE_CALL:
                    final int type = (int) event.getModel();
                    DLog.d(getLogTag(), "type=" + type);
                    switch (type) {
                        case Constant.JITSI.STATUS.CALL:
                        case Constant.JITSI.STATUS.JOIN_CALL:
                            isJoinCall = type == Constant.JITSI.STATUS.JOIN_CALL;
                            isCalling = true;
                            checkVoiceCall();
                            break;
                        case Constant.JITSI.STATUS.END:
                            leaveVoiceCall();
                            break;
                    }
                    break;
                case EXIT:
                    finish();
                    break;
                case REQUEST_CALL_TO_OPPONENT:
                    receiveCallModel = (ReceiveCallModel) event.getModel();
                    checkVoiceCall();
                    break;
                case REPLY_CALL_FROM_OPPONENT:
                    DLog.d(getLogTag(), "REPLY_CALL_FROM_OPPONENT");
                    showReplyCallFromOpponent((Bundle) event.getModel());
                    break;
            }
        }
    }

    private void openExitWhenVoiceHideDialog() {
        if (exitVoiceCallDialog == null) {
            exitVoiceCallDialog = new ConfirmationDialog(context,
                    R.string.voice_call_exit_when_hide_title,
                    R.string.voice_call_exit_when_hide_msg,
                    R.string.voice_call_exit_when_hide_yes,
                    R.string.voice_call_exit_when_hide_no,
                    new ConfirmationDialog.OnDialogClickListener() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            if (isVoiceCallingFromNotification()) {
                                replyCallFromOpponent(Constant.JITSI.REPLY_CALL_TYPE.END);
                            }
                            destroyJitsiView();
                            finish();
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
        }
        dismissVoiceCallDialog();
        exitVoiceCallDialog.show();
    }

    private void runVoiceCallRunTime() {
        tvCalling.setText(getString(msgCalling, FormatTime.convertSecondFormatHMS(secondsVoiceCall)));
    }

    private final Runnable runnableTimerVoiceCall = new Runnable() {
        public void run() {
            try {
                secondsVoiceCall += 1;
                runVoiceCallRunTime();
                mHandler.postDelayed(this, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void startTimerVoiceCall() {
        DLog.d(getLogTag(), "startTimerVoiceCall - isCalling=" + isCalling);
        if (isCalling) {
            tvCalling.setBackgroundResource(R.color.color_voice_calling_status_blue);
            msgCalling = R.string.voice_calling_status_calling;
            joinVoiceCalling();
        } else {
            tvCalling.setBackgroundResource(R.color.color_voice_calling_status_red);
            msgCalling = R.string.voice_calling_status_touch_to_return_to_call;
            joinVoiceCalled();
        }
        secondsVoiceCall = 0;
        mHandler.post(runnableTimerVoiceCall);
    }

    private void stopTimerVoiceCall() {
        mHandler.removeCallbacks(runnableTimerVoiceCall);
        mHandler.removeCallbacks(runnableFullscreen);
    }

    private void replyCallFromOpponent(int type) {
        ApiManager.replyCallFromOpponent(application, lesson, type, chatRoomInfo.getFirestoreChatRoomId(), null);
        isSendReplyFromOpponent = true;
    }

    private void showReplyCallFromOpponent(Bundle bundle) {
        if (!isVoiceCallingFromNotification()) return;
        final int replyType = Utils.parseInt(bundle.get(Constant.NOTIFICATION_KEY.REPLY_CALL_TYPE).toString());
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showReplyFromOpponentDialog(bundle, replyType);
            }
        }, replyType == Constant.JITSI.REPLY_CALL_TYPE.ACCEPT ? 2000 : 200);
    }

    private void showReplyFromOpponentDialog(Bundle bundle, final int replyType) {
        final String name = bundle.get(Constant.NOTIFICATION_KEY.CALLER_USERNAME).toString();
        DLog.d(getLogTag(), "showReplyFromOpponentDialog-replyType=" + replyType + " - name=" + name);
        int msg;
        if (replyType == Constant.JITSI.REPLY_CALL_TYPE.ACCEPT) {
            msg = R.string.voice_call_receive_msg_accepted;
            isCalling = false;
            stopTimerVoiceCall();
            startTimerVoiceCall();
        } else if (replyType == Constant.JITSI.REPLY_CALL_TYPE.DECLINE) {
            msg = R.string.voice_call_receive_msg_declined;
        } else {
            msg = R.string.voice_call_receive_msg_ended;
        }
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.dismiss();
        }
        infoDialog = new InfoDialog(this, Constant.BASE_BLANK, getString(msg, name), R.string.voice_call_receive_msg_btn_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replyType != Constant.JITSI.REPLY_CALL_TYPE.ACCEPT) {
                    isSendReplyFromOpponent = false;
                    leaveVoiceCall();
                }
                if (replyType != Constant.JITSI.REPLY_CALL_TYPE.DECLINE) {
                    dismissDialog();
                }
            }
        });
        infoDialog.setCancelable(false);
        infoDialog.setCanceledOnTouchOutside(false);
        infoDialog.show();
    }

    private void updateJitsiView() {
        DLog.d(getLogTag(), "updateJitsiView");
        // show statusBar
        final Window window = getWindow();
        final View decorView = window.getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
        // clear fullscreen
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        // set decor background
//        decorView.setBackgroundResource(R.color.colorHeader);
        if (statusBarView == null) {
            statusBarView = new View(context);
            statusBarView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            statusBarView.getLayoutParams().height = (int) positionYStatusBar;
            statusBarView.setBackgroundResource((R.color.colorHeader));
        }
        ((ViewGroup) decorView).addView(statusBarView);
        // update view and translation Y
        translationYLayout(positionYStatusBar);
        ViewGroup.LayoutParams params = llLayout.getLayoutParams();
        params.height = (int) (decorHeight - positionYStatusBar);
        llLayout.setLayoutParams(params);
        countCheckFullscreen = 0;
    }

    public void resetJitsiView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                translationYLayout(0);
                ((ViewGroup) getWindow().getDecorView()).removeView(statusBarView);
                ViewGroup.LayoutParams params = llLayout.getLayoutParams();
                params.height = viewHeight;
                llLayout.setLayoutParams(params);
            }
        });
    }

    private void translationYLayout(float value) {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(llLayout, "translationY", value);
        anim.start();
    }

    private boolean isFullscreen() {
        int location[] = new int[2];
        llLayout.getLocationOnScreen(location);
        return location[0] == 0 && location[1] == 0;
    }

    int countCheckFullscreen = 0;
    private final int maxCountCheckFullScreen = 20;
    private final Runnable runnableFullscreen = new Runnable() {
        public void run() {
            DLog.d(getLogTag(), "isFullScreen()=" + isFullscreen());
            if (!isFullscreen()) {
                if (countCheckFullscreen >= maxCountCheckFullScreen) {
                    countCheckFullscreen = 0;
                    return;
                }
                countCheckFullscreen++;
                mHandler.postDelayed(this, 1000);
            } else {
                updateJitsiView();
                return;
            }
        }
    };

    private void openAboutLessonScreen() {
        Intent intent = new Intent(context, AboutLessonActivity.class);
        openNewScreen(intent);
    }

    private void dismissDialog() {
        dismissVoiceCallDialog();
        dismissVoiceExitDialog();
    }

    private void dismissVoiceCallDialog() {
        if (exitVoiceCallDialog != null && exitVoiceCallDialog.isShowing()) {
            exitVoiceCallDialog.dismiss();
        }
    }

    private OnClickListener onMakeACallDialogListener = (view, object) -> {
        isJoinCall = false;
        isSendReplyFromOpponent = false;
        leaveVoiceCall();
        switch (view.getId()) {
            case R.id.tvMakeCall:
                eventBus.post(new SuccessEvent(BaseEvent.Screen.STUDY_MODE_FRAGMENT, EventType.CALL_REQUEST_START, Constant.JITSI.STATUS.NONE));
                break;
            case R.id.tvJoinCall:
                eventBus.post(new SuccessEvent(BaseEvent.Screen.STUDY_MODE_FRAGMENT, EventType.CALL_REQUEST_JOIN, Constant.JITSI.STATUS.NONE));
                break;
        }
    };

    private void openVoiceExitDialog() {
        if (exitVoiceDialog == null) {
            exitVoiceDialog = new ConfirmationDialog(context,
                    R.string.voice_call_information_title,
                    R.string.voice_call_exit_title,
                    R.string.voice_call_exit_btn_yes,
                    R.string.voice_call_exit_btn_no,
                    new ConfirmationDialog.OnDialogClickListener() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            if (isChatMode) {
                                typeCall = Constant.JITSI.STATUS.END;
                                eventBus.post(new SuccessEvent(BaseEvent.Screen.CHAT_DETAILS_ACTIVITY, BaseEvent.EventType.VOICE_CALL, typeCall));
                            } else {
                                eventBus.post(new SuccessEvent(BaseEvent.Screen.STUDY_MODE_FRAGMENT, EventType.CALL_REQUEST_END, Constant.JITSI.STATUS.NONE));
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
        }
        dismissVoiceExitDialog();
        exitVoiceDialog.show();
    }

    private void dismissVoiceExitDialog() {
        if (exitVoiceDialog != null && exitVoiceDialog.isShowing()) {
            DLog.d(getLogTag(), "dismissVoiceExitDialog");
            exitVoiceDialog.dismiss();
        }
    }

    public void setButtonShowHideStudentMeaningText(boolean isShow) {
        chatPopupWindow.setButtonShowHideStudentMeaningText(isShow);
    }

    private void updateAppStateMode(int appStateMode) {
        if (chatRoomInfo == null)
            return;
        if (Utils.isConnected(context)) {
            application.getDalAiImpl().updateAppStateMode(
                    chatRoomInfo.getFirestoreChatRoomId(),
                    isFromLessonList() ? lesson.getId() : 0,
                    appStateMode,
                    null
            );
        }
    }
}