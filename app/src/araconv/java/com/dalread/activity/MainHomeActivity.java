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

import com.dalread.AraConvApplication;
import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.MenuAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumWebDictionary;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.composition.BaseMainHome;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.databinding.ActivityMainConvBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConvMainMenuDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.helper.ExecutorHelper;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnToolbarLeftButtonChangeListener;
import com.dalread.model.MenuModel;
import com.dalread.model.VocaKnowAndBookmarkList;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseFileUtil;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.ChatGptWebUtil;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.PointGptUtil;
import com.dalread.util.RealmUtil;
import com.dalread.util.SnackbarUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UpdateWordLevelTask;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaListUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainHomeActivity extends BaseConvActivity implements OnAsyncTaskListenerWithType, OnNavigationItemClickListener, OnToolbarLeftButtonChangeListener {
    private BaseMainHome baseMainHomeActivity;
    private ExecutorHelper executorHelper;
    private ArrayList<BaseEvent.EventType> eventTypes;
    private ImageView imgAvatar;
    private TextView tvName;
    private TextView tvPoint;
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
        executorHelper = new ExecutorHelper();
        sharedPreferences.setSyncKnownWithServer(false);
        initWebDictionaryQuery();
        initListener();
        initLeftNavigation();
        updateLeftNavigation();
        switchBottomNaviMenu();
        updateVisibilityUI();
        eventTypes = new ArrayList<>();
        initEventBus();
//        NetworkUtil.updateLastAccessDate(application, Constant.ACCESS_OR_EXIT_APP.ACCESS);
        selectFirstNvItem();
        updateWordLevelForFirstInstall();
        checkUpdateAppVersion();
        addMobileAdsView();
        loadBanner();
//        selectFirstNvItem();
    }

    private void switchBottomNaviMenu() {
        binding.nvBottom.getMenu().clear();
        if (AppFlavorUtil.isAraConvApp()) {
            binding.nvBottom.inflateMenu(R.menu.menu_bottom_navigation_main);
        } else {
            binding.nvBottom.inflateMenu(R.menu.menu_bottom_navigation_main_hangul);
        }
    }

    private void updateWordLevelForFirstInstall() {
        if (AppFlavorUtil.isAraConvApp()) {
            if (sharedPreferences.getFirstLaunchApp()) {
                sharedPreferences.setFirstLaunchApp(false);
                Runnable task = new UpdateWordLevelTask(this, null);
                executorHelper.executeTask(task);
//
//            Runnable task = new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        subDatabase.updateWordLevelInWord(LanguageUtil.getWordLevelByLanguageLevel(MainHomeActivity.this, sharedPreferences.getSettingMyLanguageLevel()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//
//                    }
//                }
//            };
//            executorHelper.executeTask(task);
            }
        }
    }

    private void initWebDictionaryQuery() {
        WebDictionaryQuery.initDefault(Voca.getRealm(), EnumWebDictionary.getAll());
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                baseMainHomeActivity.signInFirebaseAccount();
            }
        }, Constant.ON_RESUME_DELAY);
    }

    private void updateVisibilityUI() {
        updateMenuVisibilityByLogin();
        hideMenusOnReleaseMode();
        hideHangulTabBar();
    }

    private void hideHangulTabBar() {
//        if (!LanguageUtil.isStudyLangKorean(this)) {
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_hangul);
//        }
    }

    private void updateMenuVisibilityByLogin() {
        //Hide this until I implement it
//        if (UserUtil.isLoggedIn(context, false)) {
//            toolbar.showTvRight();
//
//        } else {
//            toolbar.hideTvRight();
//        }
    }

    private void hideMenusOnReleaseMode() {
        if (AppFlavorUtil.isAraHangulApp()) {
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_role_playing);
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_menu);
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_hangul);
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_workbook);
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_dictionary);
        } else {
////            binding.nvBottom.getMenu().removeItem(R.id.nav_main_menu_hangul);
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_hangul_writing);
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_hangul_typing);
//            binding.nvBottom.getMenu().removeItem(R.id.nav_main_bookmark);

            if (UserUtil.isDebugOrAdminUser(context)) {
//            binding.nvBottom.setVisibility(View.VISIBLE);
//            toolbar.showTvRight();
            } else {
                binding.nvBottom.getMenu().removeItem(R.id.nav_main_workbook);
                binding.nvBottom.getMenu().removeItem(R.id.nav_main_dictionary);
//            binding.nvBottom.setVisibility(View.GONE);
//            toolbar.hideTvRight();
            }
        }
    }

    private void selectFirstNvItem() {
        binding.nvBottom.setSelectedItemId(R.id.nav_main_menu);
        //Without this code main tap's icon is not set to blue when I'm back from Bookmark view.
        binding.nvBottom.getMenu().findItem(R.id.nav_main_menu).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseMobileAd.loadRewardedAd(this);
        for (BaseEvent.EventType eventType : eventTypes) {
            handleEvent(eventType);
        }
        eventTypes.clear();
        updatePoint();
        updateMainMenuDialogVisibility();
//        selectFirstNvItem();
    }
    private void updateMainMenuDialogVisibility() {
        if (UserUtil.isLoggedIn(context)) {
            toolbar.getTvRight().setVisibility(View.VISIBLE);
        } else {
            toolbar.getTvRight().setVisibility(View.GONE);
        }
    }
    private void updatePoint() {
        tvPoint.setText(getString(R.string.menu_points, String.valueOf(PointGptUtil.getPoint(this))));
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
        openMainMenuDialog();
    }

    private void editPrompt() {
        ((AraConvApplication) application).getAraHanjaApiImpl().editPrompt(new DalApiListener<String>() {
            @Override
            public void onSuccess(String response) {
                SnackbarUtil.getInstance(MainHomeActivity.this).show(response);
            }

            @Override
            public void onFailure(String error) {
                ToastUtil.getInstance(MainHomeActivity.this).show(error);
            }
        });
    }

    private void openMainMenuDialog() {
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
//        int uid = getUserID();
//        if (uid > 0) {
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
//        }
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
                            return true;
                        case R.id.nav_main_hangul_writing:
                            Fragment fragment = new MenuFragmentHangulWriting();
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, null);
//                            fragment.setArguments(bundle);
                            Utils.loadFragment(MainHomeActivity.this, fragment, getFragmentContainerId(), false);
                            toolbar.setTitle(R.string.nav_bottom_menu_hangul_writing);
                            currentBottomNavigationId = id;
                            return true;
                        case R.id.nav_main_hangul_typing:
                            Fragment fragmentHangulTyping = new MenuFragmentHangulTyping();
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, null);
//                            fragment.setArguments(bundle);
                            Utils.loadFragment(MainHomeActivity.this, fragmentHangulTyping, getFragmentContainerId(), false);
                            toolbar.setTitle(R.string.nav_bottom_menu_hangul_typing);
                            currentBottomNavigationId = id;
                            return true;
////                        case R.id.nav_main_hangul:
////                            Fragment fragment = new MenuFragmentHangulWriting();
//////                            Bundle bundle = new Bundle();
//////                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, null);
//////                            fragment.setArguments(bundle);
////                            Utils.loadFragment(MainHomeActivity.this, fragment, getFragmentContainerId(), false);
////                            toolbar.setTitle(R.string.nav_bottom_menu_hangul);
////                            currentBottomNavigationId = id;
////                            return true;
                        case R.id.nav_main_workbook:
                            Fragment fragmentWorkbook = new MenuFragmentWorkbook();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, null);
                            fragmentWorkbook.setArguments(bundle);
                            Utils.loadFragment(MainHomeActivity.this, fragmentWorkbook, getFragmentContainerId(), false);
                            toolbar.setTitle(R.string.nav_bottom_menu_workbook);
                            currentBottomNavigationId = id;
                            return true;
                        case R.id.nav_main_dictionary:
                            openNewScreen(
                                    ConvVocaListActivity.createIntentForDictionary(getBaseContext())
                            );
                            return true;
                        case R.id.nav_main_role_playing:
                            startActivity(WorkbookListUserVocaBookLocalActivity.createIntent(MainHomeActivity.this));
//                            startActivity(ChatGptActivity.createIntent(MainHomeActivity.this));
                            return true;
                        case R.id.nav_main_bookmark:
                            startActivity(ConvVocaListActivity.createIntentByBookmark(MainHomeActivity.this));
                            break;
                        case R.id.nav_main_chat_gpt:
                            ChatGptWebUtil.openUrlInCustomTabWithLastBookId(context, "", customTabActivityHelper.getSession());
                            return true;
                    }

                }
                return false;
            }
        });
    }

    private void changeToMainMenuTab() {
        onChangeToMenu();
        Fragment fragment = getMainFragment(); //new MenuFragmentMain();
        Utils.loadFragment(MainHomeActivity.this, fragment, getFragmentContainerId(), false);
        toolbar.setTitle(R.string.nav_bottom_menu_main);
        currentBottomNavigationId = R.id.nav_main_menu;
    }

    private Fragment getMainFragment() {
        if (AppFlavorUtil.isAraHangulApp()) {
            return new MenuFragmentMainHangul();
        }
        return new MenuFragmentMain();
    }
//    private void changeToMainMenuHangulTab() {
//        onChangeToMenu();
//        Fragment fragment = new MenuFragmentMainHangul();
//        Utils.loadFragment(MainHomeActivity.this, fragment, getFragmentContainerId(), false);
//        toolbar.setTitle(R.string.nav_bottom_menu_main_hangul);
//        currentBottomNavigationId = R.id.nav_main_menu_hangul;
//    }

    private boolean isMainBottomNavigationId() {
        return currentBottomNavigationId == R.id.nav_main_menu;
//        if (AppFlavorUtil.isAraHangulApp()) {
//            return currentBottomNavigationId == R.id.nav_main_menu_hangul;
//        } else {
//            return currentBottomNavigationId == R.id.nav_main_menu;
//        }


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
        tvName = navView.findViewById(R.id.nav_tv_name);
        tvPoint = navView.findViewById(R.id.nav_tv_point);
        if (AppFlavorUtil.isAraHangulApp()) {
            tvPoint.setVisibility(View.GONE);
        }
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
                case Constant.NAVIGATION.NAVER_CAFE_ARAHANGUL_WRITING_PRACTICE_PAPER_DOWNLOAD:
                    DalFlavor.openNaverCafeArahangulWritingPracticePaperDownload(this);
                    break;
                case Constant.NAVIGATION.MAIL:
                    DalFlavor.openMail(MainHomeActivity.this, sharedPreferences);
                    break;
                case Constant.NAVIGATION.KAKAOTALK_GROUPCHAT:
                    if (AppFlavorUtil.isAraHangulApp()) {
                        Utils.openWeb(this, Constant.URL_ARAHANGUL_KAKAOTALK_GROUPCHAT_1);
                    } else {
                        Utils.openWeb(this, Constant.URL_ARACONV_KOREAN_KAKAOTALK_GROUPCHAT_1);
                    }
                    break;
                case Constant.NAVIGATION.SERVER:
                    baseMainHomeActivity.openServerSelector(singleChoiceDialog, currentServerUrlPos);
                    break;
                case Constant.NAVIGATION.BACKUP:
                    RealmUtil.onBackupRealmDB(this);
                    break;
                case Constant.NAVIGATION.NAVER_CAFE:
                    DalFlavor.openNaverCafe(this);
                    break;
                case Constant.NAVIGATION.SHARE_APP:
                    baseMainHomeActivity.shareApp();
                    break;
                case Constant.NAVIGATION.RATE_APP:
                    baseMainHomeActivity.rateApp();
                    break;
                case Constant.NAVIGATION.APP_DOWNLOAD:
                    baseMainHomeActivity.openAppDownload(this);
                    break;
            }
        };
    }

    private void updateLeftNavigation() {
        leftNavigationItems.clear();
        if (isLoggedIn()) {
            imgAvatar.setImageResource(Voca.getAvatarResource(sharedPreferences.getSex()));
            tvName.setText(getUserName());
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_OUT, R.drawable.ic_logout_2, getString(R.string.menu_log_out)));
        } else {
            imgAvatar.setImageResource(R.drawable.ic_no_avatar_2);
            tvName.setText(R.string.menu_user_name);
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_IN, R.drawable.ic_login_2, getString(R.string.menu_log_in)));
        }
        updatePoint();
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SETTINGS, R.drawable.ic_setting_2, getString(R.string.menu_setting)));
        if (!AppFlavorUtil.isAraHangulApp()) {
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.NAVER_CAFE, R.drawable.ic_manual_2, getString(R.string.menu_naver_cafe_manual)));
        }
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.MAIL, R.drawable.ic_mail_2, getString(R.string.menu_mail)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.APP_DOWNLOAD, R.drawable.ic_download_2, getString(R.string.menu_app_download)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SHARE_APP, R.drawable.ic_share_app_2, getString(R.string.menu_share_app)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.RATE_APP, R.drawable.ic_rate_star, getString(R.string.menu_rate_app)));
        if (LanguageUtil.isStudyLangKorean(this)) {
            if (AppFlavorUtil.isAraHangulApp()) {
                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.NAVER_CAFE_ARAHANGUL_WRITING_PRACTICE_PAPER_DOWNLOAD, R.drawable.ic_memo, getString(R.string.menu_naver_cafe_writing_practice_paper_download)));
                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.KAKAOTALK_GROUPCHAT, R.drawable.ic_manual, getString(R.string.menu_kakaotalk_groupchat_arahangul)));
            } else {
                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.KAKAOTALK_GROUPCHAT, R.drawable.ic_manual, getString(R.string.menu_kakaotalk_groupchat)));
            }
        }
        if (UserUtil.isDebugOrAdminUser(this)) {
            currentServerUrlPos = sharedPreferences.getBaseUrlIndex();
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SERVER, R.drawable.ic_setting_2, "Server (" + Constant.BASE_URL_LABELS[currentServerUrlPos] + ")"));
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
                VocaKnowAndBookmarkList vocaKnowAndBookmarkList = VocaListUtil.getVocaKnowAndBookmarkListToSync((VocaKnowAndBookmarkList) data);
                BaseVoca.updateVocaKnowAndVocaKnowpronounceAndBookmark(subDatabase, vocaKnowAndBookmarkList);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BasePermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE:
                if (BasePermissionUtils.checkExternalStoragePermission(this)) {
//                    swapCurrentFragment();
                } else {
//                    BasePermissionUtils.onRequestPermissionsResultAraPlayer(this, requestCode, permissions, grantResults);
                }
        }
    }
}
