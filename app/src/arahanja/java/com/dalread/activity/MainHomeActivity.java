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
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.component.Toolbar;
import com.dalread.composition.BaseMainHome;
import com.dalread.databinding.ActivityMainHanjaBinding;
import com.dalread.dialog.HanjaMainMenuDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.helper.AraHanjaBillingClientHelper;
import com.dalread.helper.BillingClientHelper;
import com.dalread.listener.OnToolbarLeftButtonChangeListener;
import com.dalread.model.HanjaItem;
import com.dalread.model.MenuModel;
import com.dalread.model.VocaKnowAndBookmarkList;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.RealmUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.ViewUtil;
import com.dalread.util.Voca;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;

public class MainHomeActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListener, OnNavigationItemClickListener, OnToolbarLeftButtonChangeListener {
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

    private ActivityMainHanjaBinding binding;
    private int currentBottomNavigationId;
    public BillingClientHelper billingClientHelper;

    @Override
    protected View getContentView() {
        binding = ActivityMainHanjaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseMobileAd.loadRewardedAd(this);
        billingClientHelper = AraHanjaBillingClientHelper.getInstance(this);
        baseMainHomeActivity = new BaseMainHome(this, billingClientHelper);
        if (getIntent().hasExtra(Constant.OPEN_ARA_HANJA_DATA_KEY)) {
            startActivity(ConvertToHanjaActivity.createIntent(this, getIntent().getStringExtra(Constant.OPEN_ARA_HANJA_DATA_KEY)));
        }
//        initData();
//        initLayout();
//        initDialog();
        sharedPreferences.setSyncKnownWithServer(false);

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
        
        // 앱 실행 횟수 증가 및 팝업 표시 확인
        sharedPreferences.incrementAppLaunchCount();
        checkAndShowVocabWavePromotion();
    }

    private void updateVisibilityUI() {
//        updateVisibilityNVBottom();
        updateMenuVisibility();
    }

//    private void updateVisibilityNVBottom() {
//        binding.nvBottom.setVisibility(Utils.isDebugOrAdminUser(this) ? View.VISIBLE : View.GONE);
//    }

    private void updateMenuVisibility() {
        if (UserUtil.isLoggedIn(context, false)) {
            getToolbar().showTvRight();
        } else {
            getToolbar().hideTvRight();
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
//        } else if (isShowingHanjaSearchResultView()) {
//            // 한자 검색을 시작한 상태에서 검색어를 입력해서 검색결과를 보였을때,
//            if (toolbar != null) {
//                toolbar.closeSearchView();
//            }
//            hideHanjaSearchResultView();
        } else if ((getToolbar() != null) && (getToolbar().isSearchStarted())) {
            // 위에꺼만 쓰면 아래 버그가 생긴다.
            // 한자 검색을 시작한 상태에서 검색어를 입력하지 안했을때, 하드웨어 백버튼을 누르면 앱을 종료하겠습니까 팝업이 뜨는걸 방지하고, 한자 검색만을 닫기위해서 아래를 호출.
            getToolbar().closeSearchView();
        } else if (getToolbar() != null && Objects.equals(getToolbar().getIconLeft().getTag(), R.drawable.ic_back)) {
            super.onBackPressed();
        } else if (!isMainBottomNavigationId()) {
            selectFirstNvItem();
        } else {
            baseMainHomeActivity.confirmExitApp();
        }
    }

    @Override
    public void onHeaderLeftClick() {
        if (getToolbar() != null && Objects.equals(getToolbar().getIconLeft().getTag(), R.drawable.ic_back)) {
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
        openHanjaMainMenuDialog();
    }

    private void openHanjaMainMenuDialog() {
        final HanjaMainMenuDialog dialog = new HanjaMainMenuDialog(this, isMainBottomNavigationId(), sharedPreferences, (view, object) -> {
            switch (view.getId()) {
                case R.id.llAddSentence:
                    openEditViewIfVocaIsNotInServerDB();
                    break;
                case R.id.llSyncKnownWithServer:
                    sharedPreferences.setSyncKnownWithServer(true);
                    syncKnowWithServer();
                    break;
                case R.id.llBackToHome:
                    selectFirstNvItem();
                    break;
            }
        });
        dialog.show();
    }

    private void syncKnowWithServer() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
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
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
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
                case DELETE_ACCOUNT:
                    if (type == BaseEvent.EventType.LOGOUT) {
                        ToastUtil.getInstance(this).show(R.string.msg_logout_success);
//                    } else {
//                        LoginModel loginModel = (LoginModel) successEvent.getModel();
//                        int userType = loginModel.getUserType();
//                        if (userType > 0)
//                            ToastUtil.getInstance(context).show("Log in User Type [" + userType + "]");
                    } else if (type == BaseEvent.EventType.DELETE_ACCOUNT) {
                        ToastUtil.getInstance(this).show(R.string.msg_delete_account_success);
                    }
                    updateLeftNavigation();
                    updateVisibilityUI();
                    Loading.hide();
                    break;
                case MULTIPLE_VOCA_KNOW_CHANGED:
                    searchHanjaAdapter.notifyDataSetChanged();
                    break;
                case VOCA_KNOW_CHANGED: // refreshSearchViewAdapterVocaKnow에서 또 함
                case BOOKMARK_CHANGED:
                    if (searchHanjaAdapter != null && successEvent.getModel() instanceof HanjaItem) {
                        searchHanjaAdapter.notifyItemChanged((HanjaItem) successEvent.getModel());
                    }
                    break;
                case MENU_LANGUAGE_CHANGED:
                    recreateThis();
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
                    switch (id) {
                        case R.id.nav_main_menu:
                            changeToMainMenuTab();
//                            onChangeToMenu();
//                            Fragment fragment = new MainMenuFragment();
//                            Utils.loadFragment(MainHanjaActivity.this, fragment, getFragmentContainerId(), false);
//                            toolbar.setTitle(R.string.app_name);
//                            currentBottomNavigationId = id;
                            return true;
                        case R.id.nav_classics:
                            changeToClassicsTab(id);
                            return true;
                        case R.id.nav_ocr:
                            changeToOcrTab(id);
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void changeToClassicsTab(int id) {
        Fragment fragment = new MainHanjaBooksFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, null);
        fragment.setArguments(bundle);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
        getToolbar().setTitle(R.string.hanja_classics);
        currentBottomNavigationId = id;
    }

    private void changeToOcrTab(int id) {
        Fragment fragment = new MainHanjaOcrFragment();
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
        getToolbar().setTitle(R.string.view_title_ocr);
        currentBottomNavigationId = id;
    }
    private void changeToMainMenuTab() {
        onChangeToMenu();
        Fragment fragment = new MainMenuFragment();
        Utils.loadFragment(MainHomeActivity.this, fragment, getFragmentContainerId(), false);
        getToolbar().setTitle(R.string.app_name);
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

    @Override
    protected void getData() {

    }

//    @Override
//    protected List<HanjaQuizItem> getQuizHanjaList() {
//        return null;
//    }

//    @Override
//    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
//        super.updateVocaKnowInDB(voca, newVocaKnow);
//    }
//
//    @Override
//    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
//        super.updateVocaKnowPronounceInDB(voca, newVocaKnowPronounce);
//    }

    private void initLeftNavigation() {
        View navView = binding.navLeft.inflateHeaderView(R.layout.nav_header_main);
        imgAvatar = navView.findViewById(R.id.nav_iv_avatar);
//        imgAvatar.setOnClickListener(v -> {
//            if (getUserID() > 0) {
//                openNewScreen(MyProfileActivity.class);
//            } else {
//                alertDialog.showLogInRequired();
//            }
//        });
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
                case Constant.NAVIGATION.DELETE_ACCOUNT:
                    baseMainHomeActivity.showDeleteAccountConfirm(BaseEvent.Screen.MAIN);
                    break;
                case Constant.NAVIGATION.SETTINGS:
                    DalFlavor.openSettings(MainHomeActivity.this);
                    break;

                case Constant.NAVIGATION.MAIL:
                    DalFlavor.openMail(MainHomeActivity.this, sharedPreferences);
                    break;
                case Constant.NAVIGATION.NAVER_CAFE:
                    DalFlavor.openNaverCafe(this);
                    break;
                case Constant.NAVIGATION.KAKAOTALK_GROUPCHAT:
                    Utils.openWeb(this, Constant.URL_ARAHANJA_KAKAOTALK_GROUPCHAT);
                    break;
                case Constant.NAVIGATION.SERVER:
                    baseMainHomeActivity.openServerSelector(singleChoiceDialog, currentServerUrlPos);
                    break;
                case Constant.NAVIGATION.BACKUP:
                    RealmUtil.onBackupRealmDB(this);
                    break;
                case Constant.NAVIGATION.IN_APP_PURCHASE:
                    this.startActivity(new Intent(this, InAppPointListActivity.class));
                    break;
                case Constant.NAVIGATION.SHARE_APP:
                    baseMainHomeActivity.shareApp();
                    break;
                case Constant.NAVIGATION.RATE_APP:
                    baseMainHomeActivity.rateApp();
                    break;
                case Constant.NAVIGATION.VOCAB_WAVE:
                    Utils.openWeb(MainHomeActivity.this, Constant.URL_VOCAB_WAVE_HOMEPAGE);
                    break;
            }
        };
    }

    private void updateLeftNavigation() {
        leftNavigationItems.clear();
//        if (isLoggedIn()) {
//            imgAvatar.setImageResource(Voca.getAvatarResource(sharedPreferences.getSex(), sharedPreferences.getAge()));
//            tvName.setText(getUserName());
//            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_OUT, R.drawable.ic_logout_2, getString(R.string.menu_log_out)));
//            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.DELETE_ACCOUNT, R.drawable.ic_delete, getString(R.string.menu_delete_account)));
//        } else {
//            imgAvatar.setImageResource(R.mipmap.ic_no_avatar);
//            tvName.setText(R.string.menu_user_name);
//            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_IN, R.drawable.ic_login_2, getString(R.string.menu_log_in)));
//        }
        imgAvatar.setVisibility(View.INVISIBLE);
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SETTINGS, R.drawable.ic_setting_2, getString(R.string.menu_setting)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.NAVER_CAFE, R.drawable.ic_manual_2, getString(R.string.menu_naver_cafe_manual)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.KAKAOTALK_GROUPCHAT, R.drawable.ic_manual, getString(R.string.menu_kakaotalk_groupchat1)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.MAIL, R.drawable.ic_mail_2, getString(R.string.menu_mail)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SHARE_APP, R.drawable.ic_share_app_2, getString(R.string.menu_share_app)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.RATE_APP, R.drawable.ic_rate_star, getString(R.string.menu_rate_app)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.IN_APP_PURCHASE, R.drawable.ic_download_2, getString(R.string.left_navi_items_in_app_purchase)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.VOCAB_WAVE, R.drawable.ic_manual_2, getString(R.string.menu_vocab_wave)));
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
    public void onInitAsyncTask() {
        Loading.show(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_SYNC_KNOW:
                VocaKnowAndBookmarkList vocaKnowAndBookmarkList = (VocaKnowAndBookmarkList) data;
                Voca.updateVocaKnowAndVocaKnowpronounceAndBookmark(Realm.getDefaultInstance(), vocaKnowAndBookmarkList);
                return null;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_SYNC_KNOW:
                ToastUtil.getInstance(this).show(R.string.sync_is_finished);
                break;
        }
        Loading.hide();
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

    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        ViewUtil.setViewListVisibilityGone(binding.llMain, binding.adViewContainer, binding.nvBottom);
    }

    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        ViewUtil.setViewListVisibilityVisible(binding.llMain, binding.adViewContainer, binding.nvBottom);
    }

    @Override
    public void onChangeToBack() {
        if (getToolbar() != null) {
            getToolbar().setIconLeft(R.drawable.ic_back);
        }
    }

    @Override
    public void onChangeToMenu() {
        if (getToolbar() != null) {
            getToolbar().setIconLeft(R.drawable.ic_drawer_menu);
        }
    }

    private void checkAndShowVocabWavePromotion() {
        // "그만 보기"가 체크되어 있으면 표시하지 않음
        if (!sharedPreferences.shouldShowVocabWavePromotion()) {
            return;
        }

        // 앱 실행 횟수 확인
        int launchCount = sharedPreferences.getAppLaunchCount();
        boolean isLaunchCountReached = launchCount >= Constant.VOCAB_WAVE_PROMOTION_MIN_LAUNCH_COUNT;

        // 조건 확인: 실행 횟수 2회 이상
        if (isLaunchCountReached) {
            showVocabWavePromotionDialog();
        }
    }

    private void showVocabWavePromotionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.vocab_wave_promotion_title)
                .setMessage(R.string.vocab_wave_promotion_message)
                .setPositiveButton(getString(R.string.close), (dialogInterface, which) -> {
                    // 닫기 버튼 클릭 시 - 다시 열지 않게 설정하고 웹사이트 열기
                    sharedPreferences.setDontShowVocabWavePromotion(true);
                    Utils.openWeb(MainHomeActivity.this, Constant.URL_VOCAB_WAVE_HOMEPAGE);
                })
                .create();
        dialog.show();
    }
}
