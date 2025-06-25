package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.MenuAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.base.EnumLanguage;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.composition.BaseMainHome;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.ActivityMainConvBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConvMainMenuDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnToolbarLeftButtonChangeListener;
import com.dalread.model.MenuModel;
import com.dalread.model.VocaKnowAndBookmarkList;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseFileUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.RealmUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainHomeActivity extends BaseConvActivity implements OnAsyncTaskListenerWithType, OnNavigationItemClickListener, OnToolbarLeftButtonChangeListener {
    private BaseMainHome baseMainHomeActivity;

    private ArrayList<BaseEvent.EventType> eventTypes;
    private ImageView imgAvatar;
    private TextView tvName;
    private RecyclerView rvLeftNavigation;
    private MenuAdapter leftNavigationAdapter;
    private ArrayList<MenuModel> leftNavigationItems;
    private Runnable leftNavigationRunnable;
    private int currentLeftNavigationId;
    private Handler handler;
    private int currentServerUrlPos = -1; //To choose release or test server
    private SingleChoiceDialog singleChoiceDialog;
//    protected OnDoubleClickListener listener;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_SYNC_KNOW = TYPE_INIT_DATA + 1;
    private final int TYPE_REFRESH_VOICE_RECORDING_FILE_LIST = TYPE_SYNC_KNOW + 1;

    private ActivityMainConvBinding binding;
    private int currentBottomNavigationId;

    @Override
    protected int getContentViewId() {
        return 0;//R.layout.activity_workbooks;
    }

    @Override
    protected View getContentView() {
        binding = ActivityMainConvBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        baseMainHomeActivity = new BaseMainHome(this);
        binding.adViewContainer.setVisibility(View.VISIBLE);
        initListener();
        initLeftNavigation();
        updateLeftNavigation();
        updateVisibilityUI();
        eventTypes = new ArrayList<>();
        initEventBus();
//        NetworkUtil.updateLastAccessDate(application, Constant.ACCESS_OR_EXIT_APP.ACCESS);
        checkUpdateAppVersion();
        addMobileAdsView();
        loadBanner();
        selectFirstNvItem();
        sharedPreferences.setFirstLaunchApp(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(() -> baseMainHomeActivity.signInFirebaseAccount(), Constant.ON_RESUME_DELAY);
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                baseMainHomeActivity.signInFirebaseAccount();
//            }
//        }, Constant.ON_RESUME_DELAY);
    }

    private void updateVisibilityUI() {
        updateMenuVisibilityByLogin();
        hideMenusOnReleaseMode();
//        hideBottomNaviationBar();
    }

//    private void hideBottomNaviationBar() {
//        binding.nvBottom.setVisibility(View.GONE);
//    }

    private void updateMenuVisibilityByLogin() {
        if (UserUtil.isLoggedIn(context, false)) {
            toolbar.showTvRight();

        } else {
            toolbar.hideTvRight();
        }
    }

    private void hideMenusOnReleaseMode() {
        if (UserUtil.isDebugOrAdminUser(context)) {

//            toolbar.showTvRight();
        } else {
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_bookmark);
//            toolbar.hideTvRight();
        }
    }

    private void selectFirstNvItem() {
        binding.nvBottom.setSelectedItemId(R.id.nav_main_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (BaseEvent.EventType eventType : eventTypes) {
            handleEvent(eventType);
        }
        eventTypes.clear();
    }

    @Override
    public void onBackPressed() {
        if (isOpenDrawer()) {
            closeDrawer();
        } else if (toolbar != null && Objects.equals(toolbar.getIconLeft().getTag(), R.drawable.ic_back)) {
            super.onBackPressed();
        } else if (!isMainBottomNavigationId()) {
            selectFirstNvItem();
        } else {
            baseMainHomeActivity.confirmExitApp();
        }
    }

    @Override
    public void onHeaderLeftClick() {
        if (toolbar != null && Objects.equals(toolbar.getIconLeft().getTag(), R.drawable.ic_back)) {
            onBackPressed();
        } else {
            openDrawer();
        }
    }

    @Override
    public void onHeaderLeft2Click() {

    }

    @Override
    public void onClick(int id) { // OnNavigationItemClickListener
        closeDrawer();
        currentLeftNavigationId = id;
        handler.postDelayed(leftNavigationRunnable, Constant.DRAWER_CLOSE_TIME);
    }

    private void openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private boolean isOpenDrawer() {
        return binding.drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public void onHeaderRightClick() {
    }

    @Override
    public void onHeaderIconRightClick() {
    }

    @Override
    public void onHeaderTextRightClick() {
//        openHanjaMainMenuDialog();
    }

    private void openHanjaMainMenuDialog() {
        final ConvMainMenuDialog dialog = new ConvMainMenuDialog(this, isMainBottomNavigationId(), sharedPreferences, (view, object) -> {
            switch (view.getId()) {
                case R.id.llSyncKnownWithServer:
                    syncKnowWithServer();
                    break;
                case R.id.llRefreshVoiceRecordingFileList:
                    callAsyncTask(TYPE_REFRESH_VOICE_RECORDING_FILE_LIST, null);
                    break;
                case R.id.llBackToHome:
                    selectFirstNvItem();
                    break;
            }
        });
        dialog.show();
    }

    private void syncKnowWithServer() {
        if (UserUtil.isLoggedIn(context, true)) {
            Loading.show(this);
            application.getDalAiImpl().getAllVocasKnowAndBookmarkOfUser(
                    new DalApiListener<VocaKnowAndBookmarkList>() {
                        @Override
                        public void onSuccess(VocaKnowAndBookmarkList response) {
                            if (response != null) {
                                callAsyncTask(TYPE_SYNC_KNOW, response);
                            }
                            Loading.hide();
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                        }
                    }
            );
        }
    }

    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(this, this, data, type, true).execute();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            switch (type) {
                case LOGOUT:
                case LOGIN:
                    if (type == BaseEvent.EventType.LOGOUT) {
                        ToastUtil.getInstance(this).show(R.string.msg_logout_success);
//                    } else {
//                        LoginModel loginModel = (LoginModel) successEvent.getModel();
//                        int userType = loginModel.getUserType();
//                        if (userType > 0)
//                            ToastUtil.getInstance(context).show("Log in User Type [" + userType + "]");
                    }
                    updateLeftNavigation();
                    updateVisibilityUI();
                    Loading.hide();
                    break;
                case MULTIPLE_VOCA_KNOW_CHANGED:

                    break;
                case VOCA_KNOW_CHANGED: // refreshSearchViewAdapterVocaKnow에서 또 함
                case BOOKMARK_CHANGED:
                    break;
                case MENU_LANGUAGE_CHANGED:
                    recreateThis();
                    break;
            }
        }
    }

    private void handleEvent(BaseEvent.EventType eventType) {
        if (eventType != null) {
            switch (eventType) {
                case LOGIN:
                    updateLeftNavigation();
//                    NetworkUtil.updateLastAccessDate(application, Constant.ACCESS_OR_EXIT_APP.ACCESS);
//                    getCountOf1StAmkiGrade();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        binding.nvBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id != currentBottomNavigationId) {
                    playTTS.stopPlaylist();
                    switch (id) {
                        case R.id.nav_main_menu:
                            changeToMainMenuTab();
//                            onChangeToMenu();
//                            Fragment fragment = new MainMenuFragment();
//                            Utils.loadFragment(MainHanjaActivity.this, fragment, getFragmentContainerId(), false);
//                            toolbar.setTitle(R.string.app_name);
//                            currentBottomNavigationId = id;
                            return true;
                        case R.id.nav_main_writing:
                            Fragment fragment = new MenuFragmentHangul();
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, null);
//                            fragment.setArguments(bundle);
                            Utils.loadFragment(MainHomeActivity.this, fragment, getFragmentContainerId(), false);
                            toolbar.setTitle(R.string.nav_bottom_menu_hangul_writing);
                            currentBottomNavigationId = id;
                            return true;
                        case R.id.nav_main_typing:
                            Fragment fragmentHangulTyping = new MenuFragmentHangulTyping();
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, null);
//                            fragment.setArguments(bundle);
                            Utils.loadFragment(MainHomeActivity.this, fragmentHangulTyping, getFragmentContainerId(), false);
                            toolbar.setTitle(R.string.nav_bottom_menu_hangul_typing);
                            currentBottomNavigationId = id;
                            return true;
                        case R.id.nav_main_bookmark:
                            openNewScreen(
                                    ConvVocaListActivity.createIntentByBookmark(getBaseContext())
                            );
//
//                            Fragment fragmentBasic = new MenuFragmentWorkbook();
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, null);
//                            fragmentBasic.setArguments(bundle);
//                            Utils.loadFragment(MainHomeActivity.this, fragmentBasic, getFragmentContainerId(), false);
//                            toolbar.setTitle(R.string.nav_bottom_menu_workbook);
//                            currentBottomNavigationId = id;
                            return true;
                    }

                }
                return false;
            }
        });
    }

    private void changeToMainMenuTab() {
        onChangeToMenu();
        Fragment fragment = new MenuFragmentMain();
        Utils.loadFragment(MainHomeActivity.this, fragment, getFragmentContainerId(), false);
        toolbar.setTitle(R.string.app_name);
        currentBottomNavigationId = R.id.nav_main_menu;
    }

    private boolean isMainBottomNavigationId() {
        return currentBottomNavigationId == R.id.nav_main_menu;
    }

    @Override
    protected void initDialog() {
        super.initDialog();
//        alertDialog = new AlertDialog(this);
        singleChoiceDialog = new SingleChoiceDialog(this);
    }

//    @Override
//    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
//        return null;
//    }


    private void initLeftNavigation() {
        View navView = binding.navLeft.inflateHeaderView(R.layout.nav_header_main);
        imgAvatar = navView.findViewById(R.id.nav_iv_avatar);
        imgAvatar.setOnClickListener(v -> {
            if (getUserID() > 0) {
                openNewScreen(MyProfileActivity.class);
            } else {
//                alertDialog.showLogInRequired();
            }
        });
//        imgAvatar.setVisibility(View.INVISIBLE);
        tvName = navView.findViewById(R.id.nav_tv_name);
        rvLeftNavigation = navView.findViewById(R.id.listMenu);
        rvLeftNavigation.setLayoutManager(new LinearLayoutManager(this));
        rvLeftNavigation.setHasFixedSize(true);
//        rvLeftNavigation.setItemAnimator(new DefaultItemAnimator()); //TODO : need this?
        leftNavigationItems = new ArrayList<>();
        handler = new Handler();
        leftNavigationRunnable = () -> {
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
                case Constant.NAVIGATION.NAVER_CAFE:
                    DalFlavor.openNaverCafe(this);
                    break;
                case Constant.NAVIGATION.MAIL:
                    DalFlavor.openMail(MainHomeActivity.this, sharedPreferences);
                    break;
                case Constant.NAVIGATION.KAKAOTALK_GROUPCHAT:
                    Utils.openWeb(this, Constant.URL_ARACONV_KOREAN_KAKAOTALK_GROUPCHAT_1);
                    break;
                case Constant.NAVIGATION.SERVER:
                    baseMainHomeActivity.openServerSelector(singleChoiceDialog, currentServerUrlPos);
                    break;
                case Constant.NAVIGATION.BACKUP:
                    RealmUtil.onBackupRealmDB(this);
                    break;
            }
        };
    }

    private void updateLeftNavigation() {
        leftNavigationItems.clear();
//        if (isLoggedIn()) {
//            imgAvatar.setImageResource(Voca.getAvatarResource(sharedPreferences.getSex(), sharedPreferences.getAge()));
//            tvName.setText(getUserName());
//            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_OUT, R.drawable.ic_logout, getString(R.string.menu_log_out)));
//        } else {
//            imgAvatar.setImageResource(R.mipmap.ic_no_avatar);
//            tvName.setText(R.string.menu_user_name);
//            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_IN, R.drawable.ic_login, getString(R.string.menu_log_in)));
//        }
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SETTINGS, R.drawable.ic_setting, getString(R.string.menu_setting)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.NAVER_CAFE, R.drawable.ic_memo, getString(R.string.menu_naver_cafe_writing_practice_paper_download)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.KAKAOTALK_GROUPCHAT, R.drawable.ic_manual, getString(R.string.menu_kakaotalk_groupchat)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.MAIL, R.drawable.ic_mail, getString(R.string.menu_mail)));
        if (UserUtil.isDebugOrAdminUser(this)) {
            currentServerUrlPos = sharedPreferences.getBaseUrlIndex();
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SERVER, R.mipmap.ic_setting, "Server (" + Constant.BASE_URL_LABELS[currentServerUrlPos] + ")"));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.BACKUP, 0, getString(R.string.backup_realm_db)));
        }

        if (leftNavigationAdapter == null) {
            rvLeftNavigation.setAdapter(leftNavigationAdapter = new MenuAdapter(this, leftNavigationItems));
        } else {
            leftNavigationAdapter.updateData(leftNavigationItems);
        }
    }

    @Override
    public void onInitAsyncTask(int searchType) {
        switch (searchType) {
            case TYPE_REFRESH_VOICE_RECORDING_FILE_LIST:
//                Loading.show(this, R.string.msg_type_refresh_voice_recording_file_list_message);
                //Want to show with progress
                Loading.showWithProgress(this, R.string.msg_type_refresh_voice_recording_file_list_message,100);
                break;
            default:
                Loading.show(this);
                break;
        }
    }

    @Override
    public void onInitAsyncTask() {

    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_SYNC_KNOW:
                BaseVoca.updateVocaKnowAndVocaKnowpronounceAndBookmark(subDatabase, (VocaKnowAndBookmarkList) data);
                return null;
            case TYPE_REFRESH_VOICE_RECORDING_FILE_LIST:
                return Voca.udpateVoiceFileInVocaList(this, subDatabase.getAllSentenceVocaList());
//                return updateMyVoiceFileInLocal();
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_SYNC_KNOW:
                ToastUtil.getInstance(this).show(R.string.sync_is_finished);
                break;
            case TYPE_REFRESH_VOICE_RECORDING_FILE_LIST:
                int cntOfVoiceFiles = (int) resultData;
                AlertDialog alertDialog = new AlertDialog(context);
                if (cntOfVoiceFiles == 0) {
                    alertDialog.show(context.getString(R.string.toast_no_my_recorded_voice_file), null, null);
                } else {
                    alertDialog.show(context.getString(R.string.toast_show_my_recorded_voice_file_count, cntOfVoiceFiles), null, null);
//                    ToastUtil.getInstance(getBaseContext()).show(getString(R.string.toast_show_my_recorded_voice_file_count, cntOfVoiceFiles));
                }
                break;
        }
        Loading.hide();
    }

    public int updateMyVoiceFileInLocal() {
        List<IVocaFullPlayTTSItem> vocaList = subDatabase.getAllSentenceVocaList();
        int cntOfVoiceFiles = 0;
        if (!Utils.isEmpty(vocaList)) {
            int studyLangCode = EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getStudyLanguage()).getIdApi();
            int uid = SharedPreferencesDB.getInstance(context).getRealUid();
            for (int i = 0; i < vocaList.size(); i++) {
                Loading.setProgress((i + 1) * 100 / vocaList.size());
                IVocaFullPlayTTSItem item = vocaList.get(i);
                String voiceFileName = BaseVoca.getOutputRecordingFileName(studyLangCode, item.getVIVocaType(), item.getVIVocaId(), uid);
                if ((voiceFileName != null) && (BaseFileUtil.isVoiceFileExistInVoiceFolder(context, voiceFileName))) {
                    vocaKnowActivity.updateHasVoiceFile(item, Constant.INT_BOOLEAN.TRUE);
                    cntOfVoiceFiles++;
                } else {
                    vocaKnowActivity.updateHasVoiceFile(item, Constant.INT_BOOLEAN.FASLE);
                }
            }
        }
        return cntOfVoiceFiles;
    }

    private void checkUpdateAppVersion() {
        boolean isOpenFromLauncher = false;
        final Intent intent = getIntent();
        if (intent != null) {
            isOpenFromLauncher = intent.getBooleanExtra(Constant.BUNDLE.KEY_OPEN_FROM_LAUNCHER, false);
        }
        if (!isOpenFromLauncher) return;

        // check network is LTE Mode (Mobile Network)
        baseMainHomeActivity.checkOpenMobileInfoDialog(singleChoiceDialog, currentServerUrlPos);

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

    @Override
    public void onChangeToBack() {
        if (toolbar != null) {
            toolbar.setIconLeft(R.drawable.ic_back);
        }
    }

    @Override
    public void onChangeToMenu() {
        if (toolbar != null) {
            toolbar.setIconLeft(R.drawable.ic_drawer_menu);
        }
    }
}
