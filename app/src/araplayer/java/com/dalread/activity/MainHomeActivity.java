package com.dalread.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.BuildConfig;
import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.MenuAdapter;
import com.dalread.adapter.PlaylistToAddSongsAdapter;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumMultiplePlayer;
import com.dalread.base.EnumType;
import com.dalread.base.EnumWebDictionary;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.component.Toolbar;
import com.dalread.composition.BaseMainHome;
import com.dalread.database.DownloadModelQuery;
import com.dalread.database.ListenComprehensionQuery;
import com.dalread.database.PlaylistModelQuery;
import com.dalread.database.ServerModelQuery;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.databinding.ActivityMainPlayerBinding;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.PlayerDownloadClearDialog;
import com.dalread.dialog.PlayerShowSortDialog;
import com.dalread.dialog.PlayerShowSortMenuDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.AraMultiPlayerBillingClientHelper;
import com.dalread.helper.AraPlayerBillingClientHelper;
import com.dalread.helper.BillingClientHelper;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.DownloadModel;
import com.dalread.model.MenuModel;
import com.dalread.model.PlaylistModel;
import com.dalread.model.ServerModel;
import com.dalread.network.GetMainDataHelper;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.service.DownloadingService;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.PermissionUtils;
import com.dalread.util.PlayerLanguageUtil;
import com.dalread.util.RealmUtil;
import com.dalread.util.ServerManager;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.material.navigation.NavigationBarView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


//import static com.dalread.util.PermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE;

public class MainHomeActivity extends BasePlayerActivity implements OnNavigationItemClickListener, View.OnClickListener {
    private BaseMainHome baseMainHomeActivity;
    private ImageView imgAvatar;
    private TextView tvName;
    private TextView tvPoint;
    private RecyclerView rvLeftNavigation;
    private MenuAdapter leftNavigationAdapter;
    private ArrayList<MenuModel> leftNavigationItems;
    private Runnable leftNavigationRunnable;
    private int currentLeftNavigationId;
    private int currentBaseUrlPos = -1;
    private Handler handler;
    private SingleChoiceDialog singleChoiceDialog;

    // path of player folder
    private String currentPath = Constant.BASE_BLANK;
    private Stack<String> paths = new Stack<>();
    private GetMainDataHelper dataHelper;
    public int currentBottomNavigationId;
    private Stack<String> titles = new Stack<>();
    private boolean mReceiversRegistered;
    private String searchValue;
    private boolean isSmallGroupVideoListLoaded; // isSmallGroupVideoListLoaded_Season or isSmallGroupVideoListLoaded_StudyLanguageVideoFolder
    private boolean isSmallGroupVideoListLoaded_Season; //When Season List is displaying.
    private boolean isSmallGroupVideoListLoaded_StudyLanguageVideoFolder; // When study language's video folder is displaying.
    public boolean isShowSubtitle;
    public boolean isShowSignInUpHiddenDialog = false;

    private PlaylistToAddSongsAdapter playlistAdapter;
    private List<PlaylistModel> playlistModelList = new ArrayList<>();
    private boolean isPlaylistCategory;
    private PlaylistModel selectedEditPlaylist;
    public boolean isInPlaylistWithSongs;
    private boolean isOpenSettingsToGrantPermission;
    private ConfirmationDialog confirmationManageStoragePermissionDialog;
    private String[] displayNumberOfScreens;
    private int selectedNumberOfScreenPos;
    public BillingClientHelper billingClientHelper;
    private ActivityMainPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityMainPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void onHeaderLeftClick() {
        closeEditView();
        if (currentBottomNavigationId == R.id.nav_network) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0 && Utils.isEmpty(currentPath)) {
                if(!isNetwork()) {
                    getAlertDialog().showNoInternet();
                    return;
                }
                popBackNetworkTap();
                return;
            }
        }

        if (isRootPath()) {
            if (isSmallGroupVideoListLoaded()) {
                backToMainVideoListView();
            } else if (!sharedPreferences.getShowNormalVideoFileList()) {
                reloadNormalOrHidedVideoList();
            } else {
                openDrawer();
            }
        } else {
            sendCurrentPath();
        }
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {
    }

    @Override
    public void onHeaderIconRightClick() {
        switch (currentBottomNavigationId) {
            case R.id.nav_video:
                ToastUtil.getInstance(this).show(R.string.msg_under_development);
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_LAST_VIDEO, null));
                break;
            case R.id.nav_network:
//                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_LAST_VIDEO, null));
//                } else {
                    openNewServerTypeScreen();
//                }
                break;
            case R.id.nav_quiz_list:
                break;
//            case R.id.nav_download:
//                break;
        }
    }

    @Override
    public void onHeaderTextRightClick() {
        switch (currentBottomNavigationId) {
            case R.id.nav_video:
            case R.id.nav_music:
                showSortDialog();
                break;
            case R.id.nav_network:
                if (binding.header.getTitle().equals(getString(R.string.download_status))) {
                    showDownloadClearDialog();
//                } else {
//                    showSortDialog();
                }
                break;
//            case R.id.nav_download:
//                showDownloadClearDialog();
//                break;
        }
    }

    @Override
    public void initView() {
        setBillingClientHelper();
        baseMainHomeActivity = new BaseMainHome(this, billingClientHelper);
        initEventBus();
        screen = BaseEvent.Screen.MAIN;
//        PermissionUtils.checkSystemWritePermission(this);
//        initPlayVocaHelper();
        initDataHelper();
        initDialog();
        initToolbar();
        initLeftNavigation();
        checkShowLogin();
        initBottomNavigation();
        registerReceiver();
        initData();
        hideNavMenusAtRelease();
        hideUnusedMediaNavMenus();
        displayUIForMultiPlayer();
        initPlaylistAdapter();
        updateToolBarTitleForShowHideMode();
        initOnClickListener();
    }

    private void setBillingClientHelper() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            billingClientHelper = AraMultiPlayerBillingClientHelper.getInstance(this);
        } else {
            billingClientHelper = AraPlayerBillingClientHelper.getInstance(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOpenSettingsToGrantPermission) {
            isOpenSettingsToGrantPermission = false;
            checkPermission();
        }
        setSecureView();
        updateToolBarTitleForShowHideMode();
    }

    private void setSecureView() {
        if (sharedPreferences.getShowNormalVideoFileList()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    private void hideNavMenusAtRelease() {
        if (!UserUtil.isDebugOrAdminUser(this)) {
            binding.nvBottom.getMenu().removeItem(R.id.nav_network);
        }
    }

    private void hideUnusedMediaNavMenus() {
        if (FileUtil.isMusicApp()) {
            binding.nvBottom.getMenu().removeItem(R.id.nav_video);
//            llAddMediaToPlaylist.setVisibility(View.VISIBLE);
            binding.btnHideMedia.setVisibility(View.GONE);
//            btnDeleteMedia.setVisibility(View.GONE);
//            btnSelectAllMedia.setVisibility(View.GONE);
        } else {
            binding.nvBottom.getMenu().removeItem(R.id.nav_music);
            binding.llAddMediaToPlaylist.setVisibility(View.GONE);
        }
    }

    private void displayUIForMultiPlayer() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            hideToolBarForMultiPlayer();
            hideNvBottom();
        }
    }

    private void hideToolBarForMultiPlayer() {
        binding.header.hideRight();
        binding.header.hideSearchView();
        binding.header.hideSearchView();
    }
    private void hideNvBottom() {
        setVisibleNvBottom(View.GONE);
    }
    public void setVisibleNvBottom(int visibility) {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            binding.nvBottom.setVisibility(View.GONE);
        } else {
            binding.nvBottom.setVisibility(visibility);
        }
    }
    @Override
    public void initData() {
//        runOnUiThread(() -> baseMainHomeActivity.signInFirebaseAccount());
        isShowSubtitle = false;
        final List<ServerModel> temp = ServerModelQuery.getAll(Voca.getRealm());
        addServers(temp);
        // check and init listen comprehension
        ListenComprehensionQuery.initData(Voca.getRealm());
        WebDictionaryQuery.initDefault(Voca.getRealm(), EnumWebDictionary.getAll());
        checkPermission();

        displayNumberOfScreens = EnumMultiplePlayer.getNames(this);
        selectedNumberOfScreenPos = Arrays.asList(displayNumberOfScreens).indexOf(String.valueOf(EnumMultiplePlayer.FOUR.getNumberOfScreen()));
    }

    private void addServers(List<ServerModel> temp) {
        final String password = "ara0823!@2";
        if (temp == null | temp.isEmpty()) {
            addFreeDownloadServer(password);
            addServersForDebugging(password);
        }
    }

    private void addServersForDebugging(String password) {
        if (Utils.isDebugOrAdminUser(this)) {
            ServerModel ftpServer = new ServerModel(System.currentTimeMillis() - 1000, "FTP", "dalnimbest.synology.me", "ftparaplayer", password, "2193", Constant.PLAYER.SERVER.TYPE.FTP);
            ServerModelQuery.add(Voca.getRealm(), ftpServer);
            ServerModel webDav = new ServerModel(System.currentTimeMillis(), "WebDAV", "dalnimbest.synology.me", "ftparaplayer", password, "5006", Constant.PLAYER.SERVER.TYPE.WEBDAV);
            ServerModelQuery.add(Voca.getRealm(), webDav);
        }
    }

    private void addFreeDownloadServer(String password) {
        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(BuildConfig.STUDY_LANG);
        ServerModel ftpServerFreeDownload = new ServerModel(System.currentTimeMillis() - 1000, "Free Download", "dalnimbest.synology.me", "ftpfreedownload", password, "2193", Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD);
        if (studyLanguage == EnumLanguage.JAPANESE) {
            ftpServerFreeDownload = new ServerModel(System.currentTimeMillis() - 1000, "Free Download", "dalnimbest.synology.me", "ftpfreedownload_jp", password, "2193", Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD);
        } else if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
            ftpServerFreeDownload = new ServerModel(System.currentTimeMillis() - 1000, "Free Download", "dalnimbest.synology.me", "ftpfreedownload_cn", password, "2193", Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD);
        }
        ServerModelQuery.add(Voca.getRealm(), ftpServerFreeDownload);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        playTTS.destroyPlayVocaHelper(); //destroyPlayVocaHelper();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_CODE_MANAGE_EXTERNAL_STORAGE) {
            if (PermissionUtils.checkManageExternalStoragePermission()) {
                if (confirmationManageStoragePermissionDialog != null && confirmationManageStoragePermissionDialog.isShowing()) {
                    confirmationManageStoragePermissionDialog.dismiss();
                }
                checkPermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE:
                if (PermissionUtils.checkExternalStoragePermission(this)) {
                    swapCurrentFragment();
                } else {
                    PermissionUtils.onRequestPermissionsResultAraPlayer(this, requestCode, permissions, grantResults);
                }
        }
    }

    @Override
    public void onBackPressed() {
//        if (iv_zoomed_photo.getVisibility() == View.VISIBLE) {
//            restoreToPortraitFromShowImage();
//            iv_zoomed_photo.setVisibility(View.GONE);
//            return;
//        } else
        if (binding.layoutEdit.getVisibility() == View.VISIBLE || binding.layoutPopupMenuPlaylist.getVisibility() == View.VISIBLE) {
            closeEditView();
            return;
        }

        if (!sharedPreferences.getShowNormalVideoFileList()) {
            reloadNormalOrHidedVideoList();
            return;
        }
        if (currentBottomNavigationId == R.id.nav_network) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0 && Utils.isEmpty(currentPath)) {
                if(!isNetwork()) {
                    getAlertDialog().showNoInternet();
                    return;
                }
                popBackNetworkTap();
                return;
            }
        }
        if (isInPlaylistWithSongs) {
            isInPlaylistWithSongs = false;
            eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.BACK_TO_PLAYLIST, null));
            return;
        }

        if (isRootPath()) {
            if (isSmallGroupVideoListLoaded()) {
                backToMainVideoListView();
            } else {
                baseMainHomeActivity.confirmExitApp();
            }
        } else if (isOpenDrawer()) {
            closeDrawer();
        } else {
            sendCurrentPath();
        }
    }

    private void closeEditView() {
        binding.layoutEdit.setVisibility(View.GONE);
        binding.layoutPopupMenuPlaylist.setVisibility(View.GONE);
        binding.layoutAddToPlaylist.setVisibility(View.GONE);
        binding.layoutEditPlaylist.setVisibility(View.GONE);
        selectedEditPlaylist = null;
        if (sharedPreferences.getShowNormalVideoFileList()) {
            setVisibleNvBottom(View.VISIBLE);
//            nvBottom.setVisibility(View.VISIBLE);
        }
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_EDIT, false));
    }

    private void backToMainVideoListView() {
        setSmallGroupVideoListLoaded(false);
        setSmallGroupVideoListLoaded_StudyLanguageVideoFolder(false);
        setSmallGroupVideoListLoaded_Season(false);
        updateHeaderSeasonView();
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
//        if (event.getScreen() != screen) return;
        if (event.getScreen() == BaseEvent.Screen.SIGN_IN_UP_HIDDEN_FILES) {
            ToastUtil.getInstance(getBaseContext()).show(R.string.messsage_enter_hidden_video_files);
            switch (event.getEventType()) {
                case LOGIN:
                    reloadNormalOrHidedVideoList();
                    break;
            }
        } else if (event.getScreen() == screen) {
            switch (event.getEventType()) {
                case LOGIN:
                    updateLeftNavigation();
//                    NetworkUtil.updateLastAccessDate(application, Constant.ACCESS_OR_EXIT_APP.ACCESS);
                    initDataHelper();
                    break;
                case LOGOUT:
                    Loading.hide();
                    ToastUtil.getInstance(this).show(R.string.msg_logout_success);
                    updateLeftNavigation();
                    initDataHelper();
                    break;
                case DELETE_ACCOUNT:
                    Loading.hide();
                    ToastUtil.getInstance(this).show(R.string.msg_delete_account_success);
                    updateLeftNavigation();
                    initDataHelper();
                    break;
                case MENU_LANGUAGE_CHANGED:
                    recreateThis();
                    break;
                case PLAYER_EDIT:
                    if (!(boolean) event.getModel()) {
                        if (sharedPreferences.getShowNormalVideoFileList()) {
                            setVisibleNvBottom(View.VISIBLE);
//                            nvBottom.setVisibility(View.VISIBLE);
                        }
                        binding.layoutEdit.setVisibility(View.GONE);
                    }
                    break;
                case ALL_VIDEOS_SELECTED:
                    allVideoSelected((boolean) event.getModel());
                    break;
                case ENABLE_EDIT_WHEN_LONG_CLICK_ITEM:
                    showEditView();
                    break;
                case ADD_SONG_TO_PLAYLIST_SUCCESS:
                    closeEditView();
                    loadPlaylist();
                    break;
                case IS_SELECT_PLAYLIST_CATEGORY:
                    isPlaylistCategory = (boolean) event.getModel();
                    if (isPlaylistCategory) {
                        binding.tvAddMediaToPlaylist.setText(R.string.remove_media_from_playlist);
                    } else {
                        binding.tvAddMediaToPlaylist.setText(R.string.add_media_to_playlist);
                    }
                    break;
                case EDIT_PLAYLIST:
                    binding.layoutPopupMenuPlaylist.setVisibility(View.VISIBLE);
                    binding.layoutEditPlaylist.setVisibility(View.VISIBLE);
                    selectedEditPlaylist = (PlaylistModel) event.getModel();
                    binding.tvPlaylistName.setText(selectedEditPlaylist.getName());
                    break;
                case RENAME_PLAYLIST:
                    renamePlaylist((PlaylistModel) event.getModel());
                    break;
                case DELETE_PLAYLIST:
                    deletePlaylist((PlaylistModel) event.getModel());
                    break;
                case REMOVE_SONGS_FROM_PLAYLIST_SUCCESS:
                    closeEditView();
                    break;
                case REMOVE_BANNER_ADS:
                    updateLeftNavigation();
                    break;
                case OPEN_SETTINGS_TO_GRANT_PERMISSION:
                    isOpenSettingsToGrantPermission = true;
                    break;
            }
        }
    }
    @Subscribe
    public void onEvent(ErrorEvent event) {
        DLog.d(getLogTag(), "ErrorEvent=" + event.getScreen() + " - type=" + event.getEventType());
        if (event.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = event.getEventType();
            switch (type) {
                case DELETE_ACCOUNT:
                    ToastUtil.getInstance(this).show(R.string.msg_delete_account_fail);
                    Loading.hide();
                    break;
            }
        }
    }
    private void allVideoSelected(boolean isSelectAll) {
        binding.tvSelectAll.setText(isSelectAll ? R.string.unselect_all : R.string.select_all);
    }

    private void initToolbar() {
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchValue = s;
                DLog.d(getLogTag(), "onQueryTextChange - searchValue=" + searchValue);
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_SEARCH, searchValue));
                return true;
            }
        }, () -> {
            searchValue = Constant.BASE_BLANK;
            DLog.d(getLogTag(), "searchValue=" + searchValue);
            eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_SEARCH, searchValue));
            return true;
        });
    }
    
    private void initDialog() {
        singleChoiceDialog = new SingleChoiceDialog(this);
    }

    private void initLeftNavigation() {
        View navView = binding.navLeft.inflateHeaderView(R.layout.nav_header_main);
        imgAvatar = navView.findViewById(R.id.nav_iv_avatar);
        if (Utils.isDebugOrAdminUser(this)) {
            imgAvatar.setOnClickListener(v -> {
                if (isLoggedIn()) {
                    openNewScreen(MyProfileActivity.class);
                } else {
                    alertDialog.showLogInRequired();
                }
            });
        }
        tvName = navView.findViewById(R.id.nav_tv_name);
        tvPoint = navView.findViewById(R.id.nav_tv_point);
        rvLeftNavigation = navView.findViewById(R.id.listMenu);
        rvLeftNavigation.setLayoutManager(new LinearLayoutManager(this));
        rvLeftNavigation.setHasFixedSize(true);
        rvLeftNavigation.setItemAnimator(new DefaultItemAnimator());
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
                case Constant.NAVIGATION.DELETE_ACCOUNT:
                    baseMainHomeActivity.showDeleteAccountConfirm(BaseEvent.Screen.MAIN);
                    break;
                case Constant.NAVIGATION.SETTINGS:
                    DalFlavor.openSettings(this);
                    break;
                case Constant.NAVIGATION.NAVER_CAFE:
                    DalFlavor.openNaverCafe(this);
                    break;
                case Constant.NAVIGATION.KAKAOTALK_GROUPCHAT:
                    Utils.openWeb(this, Constant.URL_ARAPLAYER_ENGLISH_KAKAOTALK_GROUPCHAT);
                    break;
                case Constant.NAVIGATION.MAIL:
                    DalFlavor.openMail(this, sharedPreferences);
                    break;
                case Constant.NAVIGATION.HELP:
                    break;
                case Constant.NAVIGATION.SERVER:
                    baseMainHomeActivity.openServerSelector(singleChoiceDialog, currentBaseUrlPos);
                    break;
                case Constant.NAVIGATION.BACKUP:
                    RealmUtil.onBackupRealmDB(this);
                    break;
                case Constant.NAVIGATION.REMOVE_BANNER_ADS:
                    baseMainHomeActivity.removeBannerAds();
                    break;
                case Constant.NAVIGATION.IN_APP_PURCHASE:
                    this.startActivity(new Intent(this, InAppPointListActivity.class));
//                    baseMainHomeActivity.inAppPurchase();
                    break;
                case Constant.NAVIGATION.RESTORE_BANNER_ADS:
                    baseMainHomeActivity.restoreBannerAds();
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
        updateLeftNavigation();
    }

    private void initBottomNavigation() {
        binding.nvBottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id != currentBottomNavigationId) {
                    currentBottomNavigationId = id;
                    swapCurrentFragment();
                    return true;
                }
                return false;
            }
        });
        if (FileUtil.isMusicApp())
            currentBottomNavigationId = R.id.nav_music;
        else
            currentBottomNavigationId = R.id.nav_video;
    }

    private void swapCurrentFragment() {
        titles.clear();
        paths.clear();
        switch (currentBottomNavigationId) {
            case R.id.nav_video:
                openMainPlayerVideoFragment();
                break;
            case R.id.nav_music:
                openMainPlayerMusicFragment();
                break;
            case R.id.nav_network:
                openMainPlayerNetworkFragment();
                break;
            case R.id.nav_quiz_list:
                openMainPlayerQuizListFragment();
                break;
//            case R.id.nav_download:
//                openMainPlayerDownloadFragment();
//                break;
        }
    }

    private void updateLeftNavigation() {
        leftNavigationItems.clear();
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            updateLeftNavigationForMultiPlayer();
        } else {
//            if (isLoggedIn() && getUserID() != Utils.parseInt(Constant.API.DEFAULT_UID)) {
//                imgAvatar.setImageResource(Voca.getAvatarResource(sharedPreferences.getSex(), sharedPreferences.getAge()));
//                tvName.setText(getUserName());
////            tvPoint.setText(getString(R.string.menu_points, getPoint()));
//                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_OUT, R.drawable.ic_logout_2, getString(R.string.menu_log_out)));
//                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.DELETE_ACCOUNT, R.drawable.ic_delete, getString(R.string.menu_delete_account)));
//            } else {
//                imgAvatar.setImageResource(R.mipmap.ic_no_avatar);
//                tvName.setText(R.string.menu_user_name);
////            tvPoint.setText(getString(R.string.menu_points, Constant.BASE_BLANK).trim());
//                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_IN, R.drawable.ic_login_2, getString(R.string.menu_log_in)));
//            }
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SETTINGS, R.drawable.ic_setting_2, getString(R.string.menu_setting)));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.NAVER_CAFE, R.drawable.ic_manual_2, getString(R.string.menu_naver_cafe_manual)));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.KAKAOTALK_GROUPCHAT, R.drawable.ic_manual, getString(R.string.menu_kakaotalk_groupchat1)));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.MAIL, R.drawable.ic_mail_2, getString(R.string.menu_mail)));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.APP_DOWNLOAD, R.drawable.ic_download_2, getString(R.string.menu_app_download)));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SHARE_APP, R.drawable.ic_share_app_2, getString(R.string.menu_share_app)));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.RATE_APP, R.drawable.ic_rate_star, getString(R.string.menu_rate_app)));
//        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.HELP, R.mipmap.ic_help, getString(R.string.help)));

            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.IN_APP_PURCHASE, R.drawable.ic_download_2, getString(R.string.left_navi_items_in_app_purchase)));
//                if (sharedPreferences.isRemoveBannerAds()) {
//                    leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.RESTORE_BANNER_ADS, 0, getString(R.string.restrore_ads)));
//                } else {
//                    leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.REMOVE_BANNER_ADS, 0, getString(R.string.remove_ads)));
//                }
            if (UserUtil.isDebugOrAdminUser(this)) {
                currentBaseUrlPos = sharedPreferences.getBaseUrlIndex();
                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SERVER, R.drawable.ic_setting_2, "Server (" + Constant.BASE_URL_LABELS[currentBaseUrlPos] + ")"));
                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.BACKUP, 0, getString(R.string.backup_realm_db)));
            }
        }
        if (leftNavigationAdapter == null) {
            rvLeftNavigation.setAdapter(leftNavigationAdapter = new MenuAdapter(this, leftNavigationItems));
        } else {
            leftNavigationAdapter.updateData(leftNavigationItems);
        }
    }

    private void updateLeftNavigationForMultiPlayer() {
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SETTINGS, R.drawable.ic_setting, getString(R.string.menu_setting)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.MAIL, R.drawable.ic_mail, getString(R.string.menu_mail)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.NAVER_CAFE, R.drawable.ic_manual, getString(R.string.menu_naver_cafe_manual)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SHARE_APP, R.drawable.ic_share_app, getString(R.string.menu_share_app)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.RATE_APP, R.drawable.ic_rate_star, getString(R.string.menu_rate_app)));
//        if (UserUtil.isDebugOrAdminUser(this)) {
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.IN_APP_PURCHASE, R.drawable.ic_download_2, getString(R.string.left_navi_items_in_app_purchase)));

//            if (sharedPreferences.isRemoveBannerAds()) {
//                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.RESTORE_BANNER_ADS, 0, getString(R.string.restrore_ads)));
//            } else {
//                leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.REMOVE_BANNER_ADS, 0, getString(R.string.remove_ads)));
//            }
        if (UserUtil.isDebugOrAdminUser(this)) {
            currentBaseUrlPos = sharedPreferences.getBaseUrlIndex();
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SERVER, R.drawable.ic_setting, "Server (" + Constant.BASE_URL_LABELS[currentBaseUrlPos] + ")"));
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.BACKUP, 0, getString(R.string.backup_realm_db)));
        }
    }

    private void openMainPlayerVideoFragment() {
        DLog.d(getLogTag(), "openMainPlayerFragment");
        binding.header.setIconLeft(R.drawable.ic_drawer_menu);
//        binding.header.setIconRight(R.drawable.ic_search_white_24dp);
        binding.header.setIconRight2(R.drawable.ic_toolbar_sort);
        binding.header.getIconRight().setVisibility(View.GONE);
        binding.header.getTvRight().setVisibility(View.GONE);
        showSearchViewInToolbar();
        setToolBarTitleForHomeView();
        binding.header.showTitle();
//        createDalPlayerFolder();
        Fragment fragment = new MainPlayerVideoFragment();
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }

    private void showSearchViewInToolbar() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            binding.header.hideSearchView();
        } else {
            binding.header.showSearchView();
        }
    }

    private void openMainPlayerMusicFragment() {
        DLog.d(getLogTag(), "openMainPlayerFragment");
        binding.header.setIconLeft(R.drawable.ic_drawer_menu);
//        binding.header.setIconRight(R.drawable.ic_search_white_24dp);
        binding.header.setIconRight2(R.drawable.ic_toolbar_sort);
        binding.header.getIconRight().setVisibility(View.GONE);
        binding.header.getTvRight().setVisibility(View.GONE);
        showSearchViewInToolbar();
        setToolBarTitleForHomeView();
        binding.header.showTitle();
//        createDalPlayerFolder();
        Fragment fragment = new MainPlayerMusicFragment();
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }

    private int getTitleByStudyLang() {
        EnumLanguage studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        if (FileUtil.isVideoApp()) {
//            if (studyLang.equals(EnumLanguage.JAPANESE))
//                return R.string.nav_video_jp;
//            else if (studyLang.equals(EnumLanguage.CHINESE_SIMPLIFIED))
//                return R.string.nav_video_cn;
            if (AppFlavorUtil.isAraMultiPlayerApp()) {
                if (AppFlavorUtil.isAraMultiPlayerAppPro())
                    return R.string.nav_home_multiplayer;
                else
                    return R.string.nav_home_multiplayer_free;
            } else {
                return R.string.nav_video;
            }

        } else {
//            if (studyLang.equals(EnumLanguage.JAPANESE))
//                return R.string.nav_music_jp;
//            else if (studyLang.equals(EnumLanguage.CHINESE_SIMPLIFIED))
//                return R.string.nav_video_cn;

            return R.string.nav_music;
        }
    }

    public void openMainNetworkItemFragment(ServerModel serverModel) {
        if(!isNetwork()) {
            getAlertDialog().showNoInternet();
            return;
        }
        Loading.show(this);
        DLog.d(getLogTag(), "openMainPlayerFragment");
        ServerManager.getInstance().setServerModel(serverModel);
        binding.header.setIconLeft(R.drawable.ic_back);
//        binding.header.setIconRight(R.drawable.ic_search_white_24dp);
//        binding.header.setIconRight2(R.drawable.ic_toolbar_sort);
        binding.header.getIconRight2().setVisibility(View.GONE);
        binding.header.getIconRight().setVisibility(View.GONE);
        binding.header.getTvRight().setVisibility(View.GONE);
        showSearchViewInToolbar();
        setTitle(serverModel.getTitle());
        binding.header.showTitle();
        Fragment fragment = new MainPlayerVideoFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.PLAYER.INTENT.KEY_DATA, serverModel);
        fragment.setArguments(bundle);
        Utils.loadFragment(MainHomeActivity.this, fragment, getFragmentContainerId());
    }

    private void openMainPlayerNetworkFragment() {
        DLog.d(getLogTag(), "openMainPlayerNetworkFragment");
        // clear all fragment can popBack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        binding.header.setIconLeft(R.drawable.ic_drawer_menu);
        binding.header.setIconRight(R.drawable.ic_add_white_24dp);
//        binding.header.getIconRight().setVisibility(View.GONE);
        binding.header.getIconRight2().setVisibility(View.GONE);
        binding.header.getTvRight().setVisibility(View.GONE);
        binding.header.hideSearchView();
        binding.header.setTitle(R.string.nav_network);
        binding.header.showTitle();
        Fragment fragment = new MainPlayerNetworkFragment();
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }

    public void updateHeaderSeasonView() {
//        DLog.d(getLogTag(), "updateHeaderSeasonView");
        if (isSmallGroupVideoListLoaded()) {
            binding.header.invisibleRight();
            binding.header.setIconLeft(R.drawable.ic_back);
            if (isSmallGroupVideoListLoaded_Season())
                setTitle(R.string.season);
            else
                setTitle(PlayerLanguageUtil.getStudyLanguageFolderText(Constant.AppMediaType.VIDEO));
        } else {
            binding.header.visibleRight();
            binding.header.setIconLeft(R.drawable.ic_drawer_menu);
            binding.header.setTitle(titles.pop());
            eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_SEASON_BACK, null));
        }
    }

    private void updateToolBarTitleForShowHideMode() {
        boolean showNormalVideoFileList = sharedPreferences.getShowNormalVideoFileList();
        if(showNormalVideoFileList) {
            setToolBarTitleForHomeView();
            binding.header.setIconLeft(R.drawable.ic_drawer_menu);
        } else {
            binding.header.setTitle(R.string.title_toolbar_home_hidden_video_mode);
            binding.header.setIconLeft(R.drawable.ic_back);
        }
    }

    private void setToolBarTitleForHomeView() {
        binding.header.setTitle(getTitleByStudyLang());
    }

    private void openMainPlayerQuizListFragment() {
        DLog.d(getLogTag(), "openMainPlayerQuizListFragment");
        binding.header.setIconLeft(R.drawable.ic_drawer_menu);
        binding.header.hideIconRight();
        binding.header.hideSearchView();
        binding.header.setTitle(R.string.quiz_list);
        binding.header.showTitle();
        Fragment fragment = new MainPlayerQuizListFragment();
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }

    public void openMainNetworkDownloadFragment(ServerModel serverModel) {
        DLog.d(getLogTag(), "openMainPlayerDownloadFragment");
        binding.header.setIconLeft(R.drawable.ic_back);
        binding.header.getIconRight().setVisibility(View.GONE);
        binding.header.getIconRight2().setVisibility(View.GONE);
        binding.header.setTextRight(R.string.clear);
        binding.header.getTvRight().setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        binding.header.hideSearchView();
        setTitle(serverModel.getTitle());
        binding.header.showTitle();
        Fragment fragment = new MainPlayerDownloadFragment();
        Utils.loadFragment(this, fragment, getFragmentContainerId());
    }

//    private void createDalPlayerFolder() {
//        DLog.d(getLogTag(), "createDalPlayerFolder");
//        StorageUtil.createAllBaseFolder(this);
//        StorageUtil.createAppFolder(this);
//    }

    private void checkPermission() {
//        if (sharedPreferences.isFirstShowManageExternalStorage() && !PermissionUtils.checkManageExternalStoragePermission()) {
//            sharedPreferences.setFirstShowManageExternalStorage();
//            showConfirmationManageStoragePermissionDialog();
//        } else if (sharedPreferences.isFirstShowManageExternalStorage() && !PermissionUtils.checkExternalStoragePermission(this)) {
//            sharedPreferences.setFirstShowManageExternalStorage();
//            PermissionUtils.checkExternalStoragePermission(this, true);
//        } else {
//            swapCurrentFragment();
//        }
        //MANAGE_EXTERNAL_STORAGE을 안쓰면 아래를 사용하면 된다.
        if (!PermissionUtils.checkExternalStoragePermission(this)) {
            PermissionUtils.checkExternalStoragePermission(this, true);
        } else {
            swapCurrentFragment();
        }
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentNextPath(String name, String path) {
        setTitle(name);
        paths.push(currentPath);
        currentPath = path;
        DLog.d(getLogTag(), "setCurrentPath - currentPath=" + this.currentPath);
        updateLeftButton();
    }

    public void setCurrentPrevPath() {
        binding.header.setTitle(titles.pop());
        DLog.d(getLogTag(), "setCurrentPath - currentPath=" + this.currentPath);
        updateLeftButton();
    }

    private void updateLeftButton() {
        if (currentBottomNavigationId != R.id.nav_network) {
            if (isRootPath()) {
                binding.header.setIconLeft(R.drawable.ic_drawer_menu);
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            } else {
                binding.header.setIconLeft(R.drawable.ic_back);
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }
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

    private boolean isRootPath() {
        return paths.size() <= 0;
    }

    private void sendCurrentPath() {
        DLog.d(getLogTag(), "sendCurrentPath - currentPath=" + currentPath);
        currentPath = paths.pop();
        DLog.d(getLogTag(), "sendCurrentPath - currentPath edited=" + currentPath);
        updateLeftButton();
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_BACK_FOLDER, currentPath));
    }

    private void showSortDialog() {
        boolean isTabVideo = currentBottomNavigationId == R.id.nav_video;
        PlayerShowSortDialog dialog = new PlayerShowSortDialog(this, isSmallGroupVideoListLoaded(), isShowSubtitle, isTabVideo, sharedPreferences, (dialogInterface, i) -> {
            switch (i) {
                case R.id.llSort:
                    showSortMenuDialog();
                    break;
                case R.id.llSettings:
                    openVideoSettingScreen();
                    break;
                case R.id.llShowSubtitleFiles:
                    isShowSubtitle = !isShowSubtitle;
                    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_RELOAD, null));
                    break;
                case R.id.llShowNormalVideoFiles:
                    closeEditView();
                    showLoginPoupForHiddenFiles();
                    break;
                case R.id.llRefreshMediaList:
                    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_REFRESH_VIDEO_LIST, null));
                    break;
                case R.id.llEdit:
                    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_EDIT, true));
                    showEditView();
                    break;
                case R.id.llMultiplePlayer:
                    showChooseNumberOfMultiplePlayerDialog();
                    break;
            }
        });
        dialog.show();
    }

    private void showEditView() {
        setVisibleNvBottom(View.GONE);
//        nvBottom.setVisibility(View.GONE);
        binding.layoutEdit.setVisibility(View.VISIBLE);
    }

    private void showLoginPoupForHiddenFiles() {
//        reloadNormalOrHidedVideoList();
        if (sharedPreferences.getShowNormalVideoFileList()) {
            isShowSignInUpHiddenDialog = true;
            boolean isLoginMode = Utils.isEmpty(sharedPreferences.getPasswordHiddenFiles()) ? false : true;
            startActivity(SignInUpHiddenFilesActivity.createIntent(this, isLoginMode));
        } else {
            reloadNormalOrHidedVideoList();
        }
    }
    private void reloadNormalOrHidedVideoList() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            return;
        }
        boolean showNormalVideoFileList = !sharedPreferences.getShowNormalVideoFileList();
        sharedPreferences.setShowNormalVideoFileList(showNormalVideoFileList);
        setSecureView();
        showViewWithAnimation(binding.header.getIconLeft(), showNormalVideoFileList, null);
        showViewWithAnimation(binding.header.getTvTitle(), showNormalVideoFileList, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (!showNormalVideoFileList) {
                    updateToolBarTitleForShowHideMode();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (showNormalVideoFileList) {
                    updateToolBarTitleForShowHideMode();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        showViewWithAnimation(binding.fragmentContainer, showNormalVideoFileList, null);
        binding.tvShowHide.setText(showNormalVideoFileList ? R.string.hide : R.string.show);

        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_RELOAD, null));
    }

    private void showViewWithAnimation(View view, boolean showNormalVideoFileList, Animation.AnimationListener animationListener) {
        TranslateAnimation animation = new TranslateAnimation(view.getWidth(), 0, 0 , 0);
        if (showNormalVideoFileList) {
            animation = new TranslateAnimation(0, view.getWidth(), 0 , 0);
        }
        animation.setDuration(300);
        animation.setAnimationListener(animationListener);
        view.startAnimation(animation);
    }


    private void showSortMenuDialog() {
        PlayerShowSortMenuDialog dialog = new PlayerShowSortMenuDialog(this, (dialogInterface, i) -> {
            int sort = Constant.PLAYER.SORT.FILE_NAME_ASC;
            switch (i) {
//                @OnClick({R.id.tv_sort_created_date_asc, R.id.tv_sort_created_date_desc, R.id.tv_sort_file_size_asc, R.id.tv_sort_file_size_desc, R.id.tv_sort_file_name_asc, R.id., R.id.tv_sort_difficulty_asc, R.id.tv_sort_difficulty_desc, R.id.tv_cancel})
                case R.id.tv_sort_created_date_asc:
                    sort = Constant.PLAYER.SORT.CREATE_DATE_ASC;
                    break;
                case R.id.tv_sort_created_date_desc:
                    sort = Constant.PLAYER.SORT.CREATE_DATE_DESC;
                    break;
                case R.id.tv_sort_file_size_asc:
                    sort = Constant.PLAYER.SORT.FILE_SIZE_ASC;
                    break;
                case R.id.tv_sort_file_size_desc:
                    sort = Constant.PLAYER.SORT.FILE_SIZE_DESC;
                    break;
                case R.id.tv_sort_file_name_asc:
                    sort = Constant.PLAYER.SORT.FILE_NAME_ASC;
                    break;
                case R.id.tv_sort_file_name_desc:
                    sort = Constant.PLAYER.SORT.FILE_NAME_DESC;
                    break;
                case R.id.tv_sort_difficulty_asc:
                    sort = Constant.PLAYER.SORT.DIFFICULTY_ASC;
                    break;
                case R.id.tv_sort_difficulty_desc:
                    sort = Constant.PLAYER.SORT.DIFFICULTY_DESC;
                    break;
            }
            sharedPreferences.setPlayerFileSort(sort);
            eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_SORT, sort));
        });
        dialog.show();
    }

    @Override
    public void onClick(int id) {
        closeDrawer();
        currentLeftNavigationId = id;
        handler.postDelayed(leftNavigationRunnable, Constant.DRAWER_CLOSE_TIME);
    }

    private void initOnClickListener() {
        binding.btnHideMedia.setOnClickListener(this);
        binding.btnDeleteMedia.setOnClickListener(this);
        binding.llAddMediaToPlaylist.setOnClickListener(this);
        binding.btnSelectAllMedia.setOnClickListener(this);
        binding.btnCreateNewPlaylist.setOnClickListener(this);
        binding.btnRenamePlaylist.setOnClickListener(this);
        binding.btnDeletePlaylist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHideMedia:
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.SHOW_HIDE_VIDEOS, null));
                break;
            case R.id.llAddMediaToPlaylist:
                // ToastUtil.getInstance(this).show(R.string.msg_under_development);
                if (isPlaylistCategory) {
                    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.REMOVE_SONGS_FROM_PLAYLIST, null));
                } else {
                    binding.layoutPopupMenuPlaylist.setVisibility(View.VISIBLE);
                    binding.layoutAddToPlaylist.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnDeleteMedia:
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DELETE_VIDEOS, null));
                break;
            case R.id.btnSelectAllMedia:
                boolean isSelectAll = binding.tvSelectAll.getText().equals(getString(R.string.select_all));
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.SELECT_ALL_VIDEOS, isSelectAll));
                allVideoSelected(isSelectAll);
                break;
            case R.id.btnCreateNewPlaylist:
                showEditPlaylistNameDialog(null, true);
                break;
            case R.id.btnRenamePlaylist:
                showEditPlaylistNameDialog(selectedEditPlaylist, false);
                break;
            case R.id.btnDeletePlaylist:
                showConfirmDeletePlaylistDialog(selectedEditPlaylist);
                break;
        }
    }
//    @OnClick({R.id.btnHideMedia, R.id.btnDeleteMedia, R.id.llAddMediaToPlaylist, R.id.btnSelectAllMedia, R.id.btnCreateNewPlaylist,
//            R.id.btnRenamePlaylist, R.id.btnDeletePlaylist})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btnHideMedia:
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.SHOW_HIDE_VIDEOS, null));
//                break;
//            case R.id.llAddMediaToPlaylist:
//                // ToastUtil.getInstance(this).show(R.string.msg_under_development);
//                if (isPlaylistCategory) {
//                    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.REMOVE_SONGS_FROM_PLAYLIST, null));
//                } else {
//                    binding.layoutPopupMenuPlaylist.setVisibility(View.VISIBLE);
//                    binding.layoutAddToPlaylist.setVisibility(View.VISIBLE);
//                }
//                break;
//            case R.id.btnDeleteMedia:
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DELETE_VIDEOS, null));
//                break;
//            case R.id.btnSelectAllMedia:
//                boolean isSelectAll = binding.tvSelectAll.getText().equals(getString(R.string.select_all));
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.SELECT_ALL_VIDEOS, isSelectAll));
//                allVideoSelected(isSelectAll);
//                break;
//            case R.id.btnCreateNewPlaylist:
//                showEditPlaylistNameDialog(null, true);
//                break;
//            case R.id.btnRenamePlaylist:
//                showEditPlaylistNameDialog(selectedEditPlaylist, false);
//                break;
//            case R.id.btnDeletePlaylist:
//                showConfirmDeletePlaylistDialog(selectedEditPlaylist);
//                break;
//        }
//    }

    private void checkShowLogin() {
        if (!isLoggedIn() && sharedPreferences.getShowLoginWhenChangeServer()) {
            sharedPreferences.setShowLoginWhenChangeServer(false);
            baseMainHomeActivity.logIn();
        }
    }

    private void openVideoSettingScreen() {
        Intent intent = new Intent(this, SettingVideoActivity.class);
        startActivity(intent);
    }

    private void initDataHelper() {
        if (dataHelper == null) {
            dataHelper = new GetMainDataHelper(this, application.getDalAiImpl(), sharedPreferences, null);
        }
        dataHelper.getDataToDalPlayer(sharedPreferences.getRealUid());
    }

//    private void destroyPlayVocaHelper() {
//        playVocaHelper.destroy();
//        playVocaHelper = null;
//    }

    private void openNewServerTypeScreen() {
        Intent intent = new Intent(this, ServerTypePlayerActivity.class);
        startActivity(intent);
    }

    private void popBackNetworkTap() {
        // logout FTP server when close screen
        getSupportFragmentManager().popBackStack();
        binding.header.setIconLeft(R.drawable.ic_drawer_menu);
        binding.header.setIconRight(R.drawable.ic_add_white_24dp);
        binding.header.getIconRight2().setVisibility(View.GONE);
        binding.header.getTvRight().setVisibility(View.GONE);
        binding.header.hideSearchView();
        binding.header.setTitle(titles.pop());
    }

    public void resetCurrentPath() {
        currentPath = Constant.BASE_BLANK;
    }

    public void setTitle(int id) {
        setTitle(getString(id));
    }

    public void setTitle(String msg) {
        titles.push(binding.header.getTitle());
        binding.header.setTitle(msg);
    }

    public void addDownload(DownloadModel model) {
        model.setServerModel(ServerModelQuery.getById(Voca.getRealm(), model.getIdServer()));
        DLog.d(getLogTag(), "addDownload - model=" + model.toString());
        Intent intent = new Intent(this, DownloadingService.class);
        intent.putExtra(DownloadingService.FILE, model);
        startService(intent);
    }

    public void cancelDownload(DownloadModel model) {
        DLog.d(getLogTag(), "cancelDownload - model=" + model.toString());
        Intent i = new Intent();
        i.setAction(DownloadingService.ACTION_CANCEL_DOWNLOAD);
        i.putExtra(DownloadingService.ID, model.getId());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private void registerReceiver() {
        unregisterReceiver();
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter
                .addAction(DownloadingService.PROGRESS_UPDATE_ACTION);
        intentToReceiveFilter
                .addAction(DownloadingService.PROGRESS_COMPLETED_ACTION);
        intentToReceiveFilter
                .addAction(DownloadingService.PROGRESS_UPDATE_DATA_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDownloadingProgressReceiver, intentToReceiveFilter);
        mReceiversRegistered = true;
    }

    private void unregisterReceiver() {
        if (mReceiversRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    mDownloadingProgressReceiver);
            mReceiversRegistered = false;
        }
    }

    private final BroadcastReceiver mDownloadingProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final DownloadModel file = intent.getParcelableExtra(DownloadingService.FILE);
            if (file == null) {
                return;
            }
            if (intent.getAction().equals(DownloadingService.PROGRESS_UPDATE_ACTION)) {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.PLAYER_DOWNLOAD, BaseEvent.EventType.PLAYER_DOWNLOAD_PROGRESS, file));
            } else if (intent.getAction().equals(DownloadingService.PROGRESS_COMPLETED_ACTION)) {
                DownloadModelQuery.update(Voca.getRealm(), file);
                eventBus.post(new SuccessEvent(BaseEvent.Screen.PLAYER_DOWNLOAD, BaseEvent.EventType.PLAYER_DOWNLOAD_COMPLETED, file));
                downloadNext();
            } else if (intent.getAction().equals(DownloadingService.PROGRESS_UPDATE_DATA_ACTION)) {
                DownloadModelQuery.update(Voca.getRealm(), file);
                eventBus.post(new SuccessEvent(BaseEvent.Screen.PLAYER_DOWNLOAD, BaseEvent.EventType.PLAYER_DOWNLOAD_UPDATE, file));
            }
        }
    };

    private void downloadNext() {
        DLog.d(getLogTag(), "downloadNext");
        final DownloadModel model = DownloadModelQuery.getDownloadNext(Voca.getRealm());
        if (model != null) {
            model.setServerModel(ServerModelQuery.getById(Voca.getRealm(), model.getIdServer()));
            DLog.d(getLogTag(), "downloadNext - model=" + model.toString());
            addDownload(new DownloadModel(model));
        }
    }

    private void showDownloadClearDialog() {
        final PlayerDownloadClearDialog dialog = new PlayerDownloadClearDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case R.id.tv_clear_all:
                        callDownloadClear(true);
                        break;
                    case R.id.tv_clear_completed:
                        callDownloadClear(false);
                        break;
                }
            }
        });
        dialog.show();
    }

    private void callDownloadClear(boolean isAll) {
        Loading.show(this);
        if (isAll) {
            final DownloadModel file = DownloadModelQuery.getByDownload(Voca.getRealm());
            if (file != null) {
                cancelDownload(file);
            }
            DownloadModelQuery.deleteAll(Voca.getRealm());
        } else {
            DownloadModelQuery.deleteByComplete(Voca.getRealm());
        }
        Loading.hide();
        eventBus.post(new SuccessEvent(BaseEvent.Screen.PLAYER_DOWNLOAD, BaseEvent.EventType.PLAYER_DOWNLOAD_INIT, null));
    }

    public void setClearButtonEnable(boolean isEnable) {
        binding.header.getTvRight().setEnabled(isEnable);
        binding.header.getTvRight().setAlpha(isEnable ? 1.0f : 0.5f);
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public boolean isSmallGroupVideoListLoaded() {
        return isSmallGroupVideoListLoaded;
    }

    public void setSmallGroupVideoListLoaded(boolean smallGroupVideoListLoaded) {
        isSmallGroupVideoListLoaded = smallGroupVideoListLoaded;
    }

    public boolean isSmallGroupVideoListLoaded_Season() {
        return isSmallGroupVideoListLoaded_Season;
    }

    public void setSmallGroupVideoListLoaded_Season(boolean smallGroupVideoListLoaded_Season) {
        isSmallGroupVideoListLoaded_Season = smallGroupVideoListLoaded_Season;
    }

    public boolean isSmallGroupVideoListLoaded_StudyLanguageVideoFolder() {
        return isSmallGroupVideoListLoaded_StudyLanguageVideoFolder;
    }

    public void setSmallGroupVideoListLoaded_StudyLanguageVideoFolder(boolean smallGroupVideoListLoaded_StudyLanguageVideoFolder) {
        isSmallGroupVideoListLoaded_StudyLanguageVideoFolder = smallGroupVideoListLoaded_StudyLanguageVideoFolder;
    }

    public boolean canEditMediaList() {
        boolean isTabVideo = currentBottomNavigationId == R.id.nav_video;
        boolean isTabMusic = currentBottomNavigationId == R.id.nav_music;
//        return !isSmallGroupVideoListLoaded() && (isTabVideo || isTabMusic);
        return isTabVideo || isTabMusic;
    }

    private void initPlaylistAdapter() {
        if (!FileUtil.isMusicApp()) return;
        playlistAdapter = new PlaylistToAddSongsAdapter(this, playlistModelList, (view, object) -> {
            if (object instanceof PlaylistModel) {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ADD_SONGS_TO_SELECTED_PLAYLIST, ((PlaylistModel) object).getPlayListId()));
            }
        });
        binding.rvPlaylist.setAdapter(playlistAdapter);
        loadPlaylist();
    }

    private void showEditPlaylistNameDialog(PlaylistModel playlistModel, boolean isCreate) {
        TypeInputDialog dialog = new TypeInputDialog(this, new BaseDialogListener() {
            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                if (data == null || data.toString().isEmpty()) return;
                dialog.dismiss();
                if (isCreate) {
                    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.CREATE_A_NEW_PLAYLIST, data.toString()));
                } else {
                    playlistModel.setName(data.toString());
                    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.RENAME_PLAYLIST, playlistModel));
                }
            }

            @Override
            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {

            }
        });
        if (isCreate) {
            dialog.setBtnOkText(getString(R.string.create));
        } else {
            dialog.setBtnOkText(getString(R.string.update));
        }
        dialog.setHint(R.string.enter_playlist_name);
        if (playlistModel != null) {
            dialog.setInput(playlistModel.getName());
        }
        dialog.show();
    }

    private void loadPlaylist() {
        playlistModelList = PlaylistModelQuery.getAll(Voca.getRealm());
        playlistAdapter.setData(playlistModelList);
    }

    private void renamePlaylist(PlaylistModel playlistModel) {
        PlaylistModelQuery.updatePlaylist(Voca.getRealm(), playlistModel);
        int playlistIndexFromList = getPlaylistIndexFromList(playlistModel);
        if (playlistIndexFromList >= 0) {
            playlistModelList.get(playlistIndexFromList).setName(playlistModel.getName());
            playlistAdapter.notifyItemChanged(playlistIndexFromList);
        }
        closeEditView();
        ToastUtil.getInstance(this).show(R.string.toast_result_rename_succeed);
    }

    private void showConfirmDeletePlaylistDialog(PlaylistModel playlistModel) {
        YesNoDialog dialog = new YesNoDialog(this, R.string.warning, R.string.msg_confirm_delete_playlist, playlistModel, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DELETE_PLAYLIST, playlistModel));
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void deletePlaylist(PlaylistModel playlistModel) {
        PlaylistModelQuery.deletePlaylistById(Voca.getRealm(), playlistModel.getPlayListId());
        int playlistIndexFromList = getPlaylistIndexFromList(playlistModel);
        if (playlistIndexFromList >= 0) {
            playlistModelList.remove(playlistIndexFromList);
            playlistAdapter.notifyItemRemoved(playlistIndexFromList);
        }
        closeEditView();
        ToastUtil.getInstance(this).show(R.string.deleted);
    }

    private int getPlaylistIndexFromList(PlaylistModel playlistModel) {
        int index = -1;
        for (int i = 0; i < playlistModelList.size(); i++) {
            if (playlistModelList.get(i).getPlayListId() == playlistModel.getPlayListId()) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void showChooseNumberOfMultiplePlayerDialog() {
        singleChoiceDialog.showWrapContentHeight(
                R.string.choose_number_of_screens,
                displayNumberOfScreens,
                selectedNumberOfScreenPos,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int pos = (int) object;
                        selectedNumberOfScreenPos = pos;
                        int numberOfScreens = Integer.parseInt(displayNumberOfScreens[pos]);
                        Intent intent = new Intent(MainHomeActivity.this, MultiplePlayerActivity.class);
                        intent.putExtra(Constant.BUNDLE.KEY_NUMBER_OF_SCREENS_MULTIPLE_PLAYER, numberOfScreens);
                        openNewScreen(intent);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void showConfirmationManageStoragePermissionDialog() {
        if (confirmationManageStoragePermissionDialog == null) {
            int msgId = AppFlavorUtil.isAraMultiPlayerApp() ? R.string.msg_reason_request_manage_external_storage_for_multi_player : R.string.msg_reason_request_manage_external_storage;
            confirmationManageStoragePermissionDialog = new ConfirmationDialog(this,
                    R.string.confirm,
                    msgId,
                    R.string.yes,
                    R.string.no, new ConfirmationDialog.OnDialogClickListener() {
                @Override
                public void onPositive(DialogInterface dialog) {
                    PermissionUtils.requestManageExternalStoragePermission(MainHomeActivity.this);
                }

                @Override
                public void onNegative(DialogInterface dialog) {
                    sharedPreferences.setFirstShowManageExternalStorage();
                    if (!PermissionUtils.checkExternalStoragePermission(MainHomeActivity.this)) {
                        PermissionUtils.checkExternalStoragePermission(MainHomeActivity.this, true);
                    }
                    dialog.dismiss();
//                    finish();
                }
            });
            confirmationManageStoragePermissionDialog.setCancelable(false);
        }

        if (!confirmationManageStoragePermissionDialog.isShowing()) {
            confirmationManageStoragePermissionDialog.show();
        }
    }
}
