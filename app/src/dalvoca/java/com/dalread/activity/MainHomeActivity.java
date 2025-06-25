package com.dalread.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.MenuAdapter;
import com.dalread.asynctask.CopyVoicesTask;
import com.dalread.asynctask.GetTableVersionAndDownloadTableTask;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumUserRole;
import com.dalread.base.EnumUserType;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.composition.BaseMainHome;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.model.MenuModel;
import com.dalread.model.ReceiveCallModel;
import com.dalread.model.VocaStudy;
import com.dalread.network.DalApiListener;
import com.dalread.network.GetMainDataHelper;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.service.callkeep.CallKeepModule;
import com.dalread.service.callkeep.VoiceUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.PermissionUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dalread.util.PermissionUtils.REQUEST_CODE_RECORD_CAMERA_READ_PHONE;
import static com.dalread.util.PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE;

public class MainHomeActivity extends BaseDalVocaPlayVocaActivity
        implements OnNavigationItemClickListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_left)
    NavigationView leftNavigationView;
    @BindView(R.id.tv_today_voca)
    TextView tvTodayVoca;
    @BindView(R.id.tv_today_meaning)
    TextView tvTodayMeaning;
    @BindView(R.id.gl_option)
    GridLayout glOption;
    @BindView(R.id.v_wordbooks_hangul)
    View vWordbooksHangul;
    @BindView(R.id.v_quiz)
    View vQuiz;
    @BindView(R.id.v_admin)
    View vAdmin;

    private BaseMainHome baseMainHomeActivity;
    private ArrayList<BaseEvent.EventType> eventTypes;
    private AlertDialog alertDialog;
    private ImageView imgAvatar;
    private TextView tvName;
    private TextView tvPoint;
    private RecyclerView rvLeftNavigation;
    private MenuAdapter leftNavigationAdapter;
    private ArrayList<MenuModel> leftNavigationItems;
    private Runnable leftNavigationRunnable;
    private int currentLeftNavigationId;
    private Handler handler;
    private VocaStudy vocaToday;
    private GetMainDataHelper dataHelper;
    private boolean skipDestroyPlayVocaHelper;
    private GetTableVersionAndDownloadTableTask getTableVersionAndDownloadTableTask;
    private int currentBaseUrlPos = -1;
    private SingleChoiceDialog singleChoiceDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_voca_new;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseMainHomeActivity = new BaseMainHome(this);
        Voca.createLessonNotificationChannel(getApplicationContext());
        initPlayVocaHelper();
        initLayout();
        initDialog();
        initLeftNavigation();
        updateLeftNavigation();
        eventTypes = new ArrayList<>();
        initEventBus();
        initDataHelper();
        getMainData();
//        NetworkUtil.updateLastAccessDate(application, Constant.ACCESS_OR_EXIT_APP.ACCESS);
        checkUpdateAppVersion();
        if (PermissionUtils.checkWriteExternalStorage(this, true)) {
            copyNativeSpeakerVoices();
            if (PermissionUtils.checkRecordReadCameraPermission(this, true)) {
                registerPhoneAccount();
            }
        }
    }


    @Override
    public void onHeaderLeftClick() {
        openDrawer();
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
        DalFlavor.openHelp(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    copyNativeSpeakerVoices();
                }
                if (PermissionUtils.checkRecordReadCameraPermission(this, true)) {
                    registerPhoneAccount();
                }
                break;
            case REQUEST_CODE_RECORD_CAMERA_READ_PHONE:
                if (PermissionUtils.checkRecordReadCameraPermission(this)) {
                    registerPhoneAccount();
                }
                break;
            default:
                break;
        }
    }

    private void copyNativeSpeakerVoices() {
        new CopyVoicesTask(
                getAssets(),
                Voca.getSoundFolderOnLocal(this)
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void registerPhoneAccount() {
        CallKeepModule callKeepModule = CallKeepModule.getInstance(this);
        callKeepModule.unregisterPhoneAccount();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getTodayData();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                baseMainHomeActivity.signInFirebaseAccount();
            }
        }, Constant.ON_RESUME_DELAY);
    }

    @Override
    protected void onResume() {
        DLog.d(getLogTag(), "onResume");
        super.onResume();

        for (BaseEvent.EventType eventType : eventTypes) {
            handleEvent(eventType);
        }
        eventTypes.clear();

        final Intent intent = getIntent();
        if (intent != null) {
            final ReceiveCallModel receiveCallModel = (ReceiveCallModel) intent.getSerializableExtra(Constant.BUNDLE.KEY_VOICE_DATA);
            DLog.d(getLogTag(), "data=" + receiveCallModel);
            intent.removeExtra(Constant.BUNDLE.KEY_VOICE_DATA);
            if (handleAdminAPI(intent, receiveCallModel)) return;

            final String data = intent.getStringExtra(Constant.BUNDLE.KEY_METHOD_NAME);
            if (!Utils.isEmpty(data)) {
                intent.removeExtra(Constant.BUNDLE.KEY_METHOD_NAME);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainHomeActivity.this, HomeworkActivity.class);
                        i.putExtra(Constant.BUNDLE.KEY_METHOD_NAME, data);
                        i.putExtra(Constant.BUNDLE.KEY_VOCA_ID, intent.getStringExtra(Constant.BUNDLE.KEY_VOCA_ID));
                        i.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, intent.getStringExtra(Constant.BUNDLE.KEY_VOCA_TYPE));
                        openNewScreen(i);
                    }
                }, Constant.ON_RESUME_DELAY);
                return;
            }
            final int lessonId = intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_ID, 0);
            DLog.d(getLogTag(), "lessonId=" + lessonId);
            if (lessonId > 0) {
                intent.removeExtra(Constant.BUNDLE.KEY_LESSON_ID);
                final int lessonType = intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_STUDENT);
                intent.removeExtra(Constant.BUNDLE.KEY_LESSON_TYPE);
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent i = new Intent(MainHomeActivity.this, LessonListActivity.class);
                        i.putExtra(Constant.BUNDLE.KEY_LESSON_ID, lessonId);
                        i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, lessonType);
                        i.putExtra(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
                        openNewScreen(i);
                    }
                }, Constant.ON_RESUME_DELAY);
            } else if (sharedPreferences.getUserType() == EnumUserType.SYSTEM_MANAGER.getType()) {
                final int lessonType = intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_STUDENT);
                if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
                    intent.removeExtra(Constant.BUNDLE.KEY_LESSON_ID);
                    intent.removeExtra(Constant.BUNDLE.KEY_LESSON_TYPE);
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Intent i = new Intent(MainHomeActivity.this, AdminActivity.class);
                            i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, lessonType);
                            openNewScreen(i);
                        }
                    }, Constant.ON_RESUME_DELAY);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (!skipDestroyPlayVocaHelper) {
//            NetworkUtil.updateLastAccessDate(application, Constant.ACCESS_OR_EXIT_APP.EXIT);
            destroyPlayVocaHelper();
        }
        unRegisterEventBus();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isOpenDrawer()) {
            closeDrawer();
        } else {
            baseMainHomeActivity.confirmExitApp();
        }
    }

    @OnClick({
            R.id.v_today,
            R.id.v_wordbooks_hangul,
            R.id.v_lesson_student, R.id.v_lesson_tutor,
            R.id.v_wordbooks, R.id.v_favorites,
            R.id.v_targets, R.id.v_homework,
            R.id.v_recording_all, R.id.v_chatting,
            R.id.v_practice_with_people, R.id.v_quiz,
            R.id.v_study_history,
            R.id.v_admin
    })
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_today:
                if (vocaToday != null) {
                    Intent intent;
                    intent = new Intent(this, WordInfoActivity.class);
                    intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, vocaToday.getVocaId());
                    intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, vocaToday.getType());

                    openNewScreen(intent);
                }
                break;
            case R.id.v_wordbooks_hangul:
                openWordbooksHangulScreen();
                break;
            case R.id.v_lesson_student:
                if (getUserID() > 0) {
                    openLessonListScreen(Constant.API_VALUE.LIST_LESSON_FOR_STUDENT);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            case R.id.v_lesson_tutor:
                if (getUserID() > 0) {
                    openLessonListScreen(Constant.API_VALUE.LIST_LESSON_FOR_TUTOR);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            case R.id.v_wordbooks:
                openWordbooksScreen(false);
                break;
            case R.id.v_favorites:
                if (getUserID() > 0) {
                    openNewScreen(FavoritesActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            case R.id.v_targets:
                if (getUserID() > 0) {
                    openNewScreen(TargetsActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            case R.id.v_homework:
                if (getUserID() > 0) {
                    openNewScreen(HomeworkActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            case R.id.v_recording_all:
                if (getUserID() > 0) {
                    openNewScreen(NativeSpeakerMainActivity.class);
//                    openNewScreen(VocaListRecordingBookListActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            case R.id.v_chatting:
                if (getUserID() > 0) {
                    openNewScreen(ChatActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            case R.id.v_practice_with_people:
                openWordbooksScreen(true);
                break;
            case R.id.v_quiz:
                openNewScreen(ChoiceQuizActivity.class);
                break;
            case R.id.v_study_history:
                if (getUserID() > 0) {
                    openNewScreen(StudyHistoryActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            case R.id.v_admin:
                if (getUserID() > 0) {
                    openNewScreen(AdminActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
                break;
            default:
                break;
        }
    }

    private void initPlayVocaHelper() {
        playVocaHelper.initMotherTongueTTS(EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getLocale());
        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        int ttsSpeed = 0;
        if (LanguageUtil.isStudyLangChinese(this)) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedChinese();
        } else if (LanguageUtil.isStudyLangJapanese(this)) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedJapanese();
        } else if (LanguageUtil.isStudyLangKorean(this)) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedKorean();
        } else if (LanguageUtil.isStudyLangHanja(this)) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedHanja();
        } else {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedEnglish();
        }
        playVocaHelper.initStudyTTS(
                studyLanguage.getLocale(),
                Integer.parseInt(sharedPreferences.getReadCount()),
                ttsSpeed
        );
//        playVocaHelper.setIncludeMyVoice(sharedPreferences.getIncludeMyVoice());
        playVocaHelper.setIncludeMeaning(sharedPreferences.getIncludeMeaning());
        playVocaHelper.initMediaPlayer();
    }

    private void initLayout() {
        if (!LanguageUtil.isStudyLangKorean(this)) {
            glOption.removeView(vWordbooksHangul);
        }
        updateLayoutByRole();
    }

    private void updateLayoutByRole() {
        int userRole = sharedPreferences.getUserRole();
        if (userRole == EnumUserRole.STUDENT_TUTOR_NATIVE_SPEAKER.getRole()) {
            // TODO: display menu Tutor
        } else {
            // TODO: hide menu Tutor
        }

        glOption.removeView(vAdmin);
        int userType = sharedPreferences.getUserType();
        if (userType == EnumUserType.SYSTEM_MANAGER.getType()) {
            glOption.addView(vAdmin);
        }
    }

    private void initDialog() {
        alertDialog = new AlertDialog(this);
        singleChoiceDialog = new SingleChoiceDialog(this);
    }

    private void initLeftNavigation() {
        View navView = leftNavigationView.inflateHeaderView(R.layout.nav_header_main);
        imgAvatar = navView.findViewById(R.id.nav_iv_avatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getUserID() > 0) {
                    openNewScreen(MyProfileActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
            }
        });
        tvName = navView.findViewById(R.id.nav_tv_name);
        tvPoint = navView.findViewById(R.id.nav_tv_point);
        rvLeftNavigation = navView.findViewById(R.id.listMenu);
        rvLeftNavigation.setLayoutManager(new LinearLayoutManager(this));
        rvLeftNavigation.setHasFixedSize(true);
        rvLeftNavigation.setItemAnimator(new DefaultItemAnimator());
        leftNavigationItems = new ArrayList<>();
        handler = new Handler();
        leftNavigationRunnable = new Runnable() {

            @Override
            public void run() {
                switch (currentLeftNavigationId) {
                    case Constant.NAVIGATION.LOG_IN:
                        baseMainHomeActivity.logIn();
                        break;
                    case Constant.NAVIGATION.LOG_OUT:
                        baseMainHomeActivity.showLogoutConfirm(BaseEvent.Screen.MAIN);
                        break;
                    case Constant.NAVIGATION.SETTINGS:
                        DalFlavor.openSettings(MainHomeActivity.this);
                        break;
                    case Constant.NAVIGATION.MAIL:
                        DalFlavor.openMail(MainHomeActivity.this, sharedPreferences);
                        break;
                    case Constant.NAVIGATION.HELP:
//                        DalFlavor.openHelp(MainVocaNewActivity.this);
                        DalFlavor.openNaverCafe(MainHomeActivity.this);
                        break;
                    case Constant.NAVIGATION.SERVER:
                        baseMainHomeActivity.openServerSelector(singleChoiceDialog, currentBaseUrlPos);
                        break;
                }
            }
        };
    }

    private void updateLeftNavigation() {
        leftNavigationItems.clear();
        if (isLoggedIn()) {
            imgAvatar.setImageResource(Voca.getAvatarResource(sharedPreferences.getSex(), sharedPreferences.getAge()));
            tvName.setText(getUserName());
//            tvPoint.setText(getString(R.string.menu_points, getPoint()));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_OUT, R.drawable.ic_logout, getString(R.string.menu_log_out)));
        } else {
            imgAvatar.setImageResource(R.mipmap.ic_no_avatar);
            tvName.setText(R.string.menu_user_name);
//            tvPoint.setText(getString(R.string.menu_points, Constant.BASE_BLANK).trim());
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_IN, R.drawable.ic_login, getString(R.string.menu_log_in)));
        }
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SETTINGS, R.drawable.ic_setting, getString(R.string.menu_setting)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.MAIL, R.drawable.ic_mail, getString(R.string.menu_mail)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.HELP, R.mipmap.ic_help, getString(R.string.help)));
        if (Utils.isDebug()
                || sharedPreferences.getUserType() == EnumUserType.SYSTEM_MANAGER.getType()) {
            currentBaseUrlPos = sharedPreferences.getBaseUrlIndex();
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SERVER, R.mipmap.ic_setting, "Server (" + Constant.BASE_URL_LABELS[currentBaseUrlPos] + ")"));
        }
        if (leftNavigationAdapter == null) {
            rvLeftNavigation.setAdapter(leftNavigationAdapter = new MenuAdapter(this, leftNavigationItems));
        } else {
            leftNavigationAdapter.updateData(leftNavigationItems);
        }
    }

    @Override
    public void onClick(int id) { // OnNavigationItemClickListener
        closeDrawer();
        currentLeftNavigationId = id;
        handler.postDelayed(leftNavigationRunnable, Constant.DRAWER_CLOSE_TIME);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private boolean isOpenDrawer() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.LOGOUT) {
                Loading.hide();
                ToastUtil.getInstance(this).show(R.string.msg_logout_success);
                updateLeftNavigation();
                updateLayoutByRole();
                Voca.deleteAllRealmData();
                Voca.deleteVocaVersion();
                getTodayData();
                getMainData();
            } else if (type == BaseEvent.EventType.ADMIN_API) {
                handleAdminAPI(null, (ReceiveCallModel) successEvent.getModel());
            } else if (eventTypes.indexOf(BaseEvent.EventType.MENU_LANGUAGE_CHANGED) == -1 // not had MENU_LANGUAGE_CHANGED yet
                    && eventTypes.indexOf(type) == -1) { // not had this eventType yet
                if (type == BaseEvent.EventType.MENU_LANGUAGE_CHANGED) { // if have MENU_LANGUAGE_CHANGED, activity will be re-created, no need to track for other events
                    eventTypes.clear();
                }
                eventTypes.add(type);
            }
        }
    }

    private void handleEvent(BaseEvent.EventType eventType) {
        if (eventType != null) {
            switch (eventType) {
                case LOGIN:
                    updateLayoutByRole();
                    updateLeftNavigation();
                    getTodayData();
                    getMainData();
//                    NetworkUtil.updateLastAccessDate(application, Constant.ACCESS_OR_EXIT_APP.ACCESS);
                    break;
                case STUDY_LANGUAGE_CHANGED:
                case DATA_CHANGED:
                    Voca.deleteAllRealmData();
                    getMainData();
                    break;
                case MENU_LANGUAGE_CHANGED:
                    recreateThis();
                    break;
                default:
                    break;
            }
        }
    }

    public void recreateThis() {
        skipDestroyPlayVocaHelper = true;
        super.recreateThis();
    }

    private void initDataHelper() {
        dataHelper = new GetMainDataHelper(this, application.getDalAiImpl(), sharedPreferences, null);
        getTableVersionAndDownloadTableTask = new GetTableVersionAndDownloadTableTask(application);
    }

    private void getMainData() {
        int uid = getUserID();
        dataHelper.getDataNew(uid);
        getTableVersionAndDownloadTableTask.start();
    }

    private void getTodayData() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                application.getDalAiImpl().getExpressionOfToday(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        new DalApiListener<VocaStudy>() {

                            @Override
                            public void onSuccess(VocaStudy response) {
                                vocaToday = response;
                                if (vocaToday == null) {
                                    displayNA();
                                } else {
                                    displayData();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                displayNA();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
                displayNA();
            }
        } else {
            alertDialog.showLogInRequired();
            displayNA();
        }
    }

    private void displayData() {
        tvTodayVoca.setText(Voca.getVocaDisplay(vocaToday));
        tvTodayMeaning.setText(vocaToday.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)));
    }

    private void displayNA() {
        tvTodayVoca.setText(R.string.not_available);
        tvTodayMeaning.setText(R.string.not_available);
    }

    private void openWordbooksHangulScreen() {
//        openNewScreen(WordbooksHangulActivity.class);
        Intent i = new Intent(this, WordbooksHangulActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_GET_DATA_FROM_NETWORK, true);
        openNewScreen(i);

    }

    private void openWordbooksScreen(boolean practiceOnly) {
        Intent i = new Intent(this, WordbookByCategoryActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_PRACTICE_ONLY, practiceOnly);
        openNewScreen(i);
    }

    private void openLessonListScreen(int lessonType) {
        Intent i = new Intent(this, LessonListActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, lessonType);
        openNewScreen(i);
    }

    private void destroyPlayVocaHelper() {
        playVocaHelper.destroy();
        playVocaHelper = null;
    }

    private void checkUpdateAppVersion() {
        boolean isOpenFromLauncher = false;
        final Intent intent = getIntent();
        if (intent != null) {
            isOpenFromLauncher = intent.getBooleanExtra(Constant.BUNDLE.KEY_OPEN_FROM_LAUNCHER, false);
        }
        if (!isOpenFromLauncher) return;

        // check network is LTE Mode (Mobile Network)
        baseMainHomeActivity.checkOpenMobileInfoDialog(singleChoiceDialog, currentBaseUrlPos);

        // check update app version
        application.getDalAiImpl().getHasNewAppVersion(Constant.CLIENT_TYPE_ANDROID, Utils.getAppVersion(), getString(R.string.app_name), new DalApiListener<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                if (response) {
                    baseMainHomeActivity.confirmUpdateApp();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private boolean handleAdminAPI(Intent intent, ReceiveCallModel receiveCallModel) {
        if (receiveCallModel != null && VoiceUtils.isAdminAPI(receiveCallModel.getMethod())) {
            if (intent != null) {
                intent.removeExtra(Constant.BUNDLE.KEY_LESSON_ID);
                intent.removeExtra(Constant.BUNDLE.KEY_LESSON_TYPE);
            }
            if (Utils.isEmpty(receiveCallModel.getLessonId())) {
                return true;
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainHomeActivity.this, AdminActivity.class);
                    i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_ADMIN);
                    i.putExtra(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
                    openNewScreen(i);
                }
            }, Constant.ON_RESUME_DELAY);
            return true;
        }
        return false;
    }
}
