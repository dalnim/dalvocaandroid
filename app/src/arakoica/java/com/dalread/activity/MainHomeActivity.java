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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.AraKoicaApplication;
import com.dalread.BaseApplication;
import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.MenuAdapter;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.component.Toolbar;
import com.dalread.composition.BaseMainHome;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.databinding.ActivityMainIctTermBinding;
import com.dalread.dialog.MainMenuDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.helper.AraKoicaBillingClientHelper;
import com.dalread.helper.BillingClientHelper;
import com.dalread.listener.OnToolbarLeftButtonChangeListener;
import com.dalread.model.MenuModel;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainHomeActivity extends BaseTermIctActivity implements OnNavigationItemClickListener, OnToolbarLeftButtonChangeListener {
    private BaseMainHome baseMainHomeActivity;
    private ImageView imgAvatar;
    private TextView tvName;
    private RecyclerView rvLeftNavigation;
    private MenuAdapter leftNavigationAdapter;
    private ArrayList<MenuModel> leftNavigationItems;
    private Runnable leftNavigationRunnable;
    private int currentLeftNavigationId;
    private Handler handler;
    private MainIctTermListFragment fragmentIctTermList;
    private MainSearchHistoryFragment fragmentSearchHistory;
    private SingleChoiceDialog singleChoiceDialog;
    private int currentServerUrlPos = -1; //To choose release or test server
    private ActivityMainIctTermBinding binding;
    private int currentBottomNavigationId;
    private boolean isEnableRefreshIctTermList = false;
    private boolean isFromExtraProcessText = false;
    public BillingClientHelper billingClientHelper;

    @Override
    protected View getContentView() {
        binding = ActivityMainIctTermBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        billingClientHelper = AraKoicaBillingClientHelper.getInstance(this);
        baseMainHomeActivity = new BaseMainHome(this, billingClientHelper);
        isEnableRefreshIctTermList = true;

        initLeftNavigation();
        updateLeftNavigation();
        initFragment();
        updateMenuVisibility();
        initEventBus();
        selectFirstNvItem();
        sharedPreferences.setFirstLaunchApp(false);
    }

    private void initFragment() {
        fragmentIctTermList = new MainIctTermListFragment();
        fragmentSearchHistory = new MainSearchHistoryFragment();
    }

    private void updateMenuVisibility() {
        getToolbar().showTvRight();
    }

    private void selectFirstNvItem() {
        binding.nvBottom.setSelectedItemId(R.id.nav_main_ict_term_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doActionProcessText();
    }

    private void doActionProcessText() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_PROCESS_TEXT.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                //https://betterprogramming.pub/custom-text-selection-with-action-process-text-9c1cd9b24027 or //https://dev.to/bigaru/providing-custom-text-selection-actions-in-android-1akc
                String str = (String) intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
                if (!Utils.isEmpty(str)) {
                    //선택한 문구를 한번 사용하고, 초기화 시킨다.
                    intent.removeExtra(Intent.EXTRA_PROCESS_TEXT); //or getIntent().getExtras().clear();
//                    ToastUtil.getInstance(this).show("select : " + str);
                    selectFirstNvItem();
                    fragmentIctTermList.searchByIntentKeyword(str.trim());
                    isFromExtraProcessText = true;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isOpenDrawer()) {
            closeDrawer();
        } else if (isToolbarLeftBackIcon()) { //toolbar != null && Objects.equals(toolbar.getIconLeft().getTag(), R.drawable.ic_back)) {
            super.onBackPressed();
        } else if (!isMainBottomNavigationId()) {
            selectFirstNvItem();
        } else if (isToolbarSearchMode()) {
            if (isFromExtraProcessText) {
                this.finish();
            } else {
//            toolbar.getViewSearch().setIconified(true); //Don't delete this
                getToolbar().getViewSearch().onActionViewCollapsed();
                getToolbar().showTitle();
            }
        } else {
            if (isFromExtraProcessText) {
                this.finish();
            } else {
                baseMainHomeActivity.confirmExitApp();
            }
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
    public void onClick(int id) {
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
        fragmentSearchHistory.clearSearchHistory();
    }

    @Override
    public void onHeaderTextRightClick() {
        openMainMenuDialog();
    }

    private void openMainMenuDialog() {
        final MainMenuDialog dialog = new MainMenuDialog(this, isEnableRefreshIctTermList, isMainBottomNavigationId(), sharedPreferences, (view, object) -> {
            switch (view.getId()) {
                case R.id.llRefreshIctTermListAfterTime:
                    getIctTermListAfterTime(subDatabase.getLastUpdateDate());
                    break;
                case R.id.llAddTerm:
                    openEditView();
                    break;
                case R.id.scShowIctTerm:
                case R.id.scShowLocalTerm:
                    refreshIctTermList();
                    break;
                case R.id.llBackToHome:
                    selectFirstNvItem();
                    break;
            }
        });
        dialog.show();
    }
    private void openEditView() {
        openNewScreen(
                EditTermIctActivity.createIntentAddTerm(this)
        );
    }

    private void refreshIctTermList() {
        fragmentIctTermList.refreshIctTermList();
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
                    Loading.hide();
                    break;
                case MENU_LANGUAGE_CHANGED:
                    recreateThis();
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
                        case R.id.nav_main_ict_term_list:
                            changeToMainIctTermListTab();
                            return true;
                        case R.id.nav_main_search_history:
                            changeToMainSearchHistoryTab();
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void changeToMainIctTermListTab() {
//        toolbar.showSearchView();
//        onChangeToMenu();
        Utils.loadFragment(MainHomeActivity.this, fragmentIctTermList, getFragmentContainerId(), false);
        getToolbar().setTitle(R.string.app_name);
        getToolbar().showTvRight();
        getToolbar().hideIconRight();
        getToolbar().setTextRight(R.string.menu);
        currentBottomNavigationId = R.id.nav_main_ict_term_list;
    }

    private void changeToMainSearchHistoryTab() {
        getToolbar().hideSearchView();
        Utils.loadFragment(MainHomeActivity.this, fragmentSearchHistory, getFragmentContainerId(), false);
        getToolbar().setTitle(R.string.view_title_search_history);
        getToolbar().showTitle(); //MainHome에서 검색하다가 히스토리탭으로오면 타이틀이 사라진다.
        getToolbar().hideTvRight();
        currentBottomNavigationId = R.id.nav_main_search_history;
    }

    private boolean isMainBottomNavigationId() {
        return currentBottomNavigationId == R.id.nav_main_ict_term_list;
    }
    @Override
    protected void initDialog() {
        super.initDialog();
//        alertDialog = new AlertDialog(this);
        singleChoiceDialog = new SingleChoiceDialog(this);
    }
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
                case Constant.NAVIGATION.SETTINGS:
                    DalFlavor.openSettings(MainHomeActivity.this);
                    break;
                case Constant.NAVIGATION.MAIL:
                    DalFlavor.openMail(MainHomeActivity.this, sharedPreferences);
                    break;
                case Constant.NAVIGATION.SHARE_APP:
                    baseMainHomeActivity.shareApp();
                    break;
                case Constant.NAVIGATION.SERVER:
                    baseMainHomeActivity.openServerSelector(singleChoiceDialog, currentServerUrlPos);
                    break;
                case Constant.NAVIGATION.RATE_APP:
                    baseMainHomeActivity.rateApp();
                    break;
            }
        };
    }

    private void updateLeftNavigation() {
        leftNavigationItems.clear();
        if (isLoggedIn()) {
            imgAvatar.setImageResource(BaseVoca.getAvatarResource(sharedPreferences.getSex(), sharedPreferences.getAge()));
            tvName.setText(getUserName());
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_OUT, R.drawable.ic_logout, getString(R.string.menu_log_out)));
        } else {
            imgAvatar.setImageResource(R.mipmap.ic_no_avatar);
            tvName.setText(R.string.menu_user_name);
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.LOG_IN, R.drawable.ic_login, getString(R.string.menu_log_in)));
        }
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SETTINGS, R.drawable.ic_setting, getString(R.string.menu_setting)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.MAIL, R.drawable.ic_mail, getString(R.string.menu_mail)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SHARE_APP, R.drawable.ic_share_app, getString(R.string.menu_share_app)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.RATE_APP, R.drawable.ic_tutor_eval_a, getString(R.string.menu_rate_app)));
        if (UserUtil.isDebugOrAdminUser(this)) {
            currentServerUrlPos = sharedPreferences.getBaseUrlIndex();
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SERVER, R.mipmap.ic_setting, "Server (" + Constant.BASE_URL_LABELS[currentServerUrlPos] + ")"));
        }

        if (leftNavigationAdapter == null) {
            rvLeftNavigation.setAdapter(leftNavigationAdapter = new MenuAdapter(this, leftNavigationItems));
        } else {
            leftNavigationAdapter.updateData(leftNavigationItems);
        }
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

    private void getIctTermListAfterTime(String afterTime) {
        isEnableRefreshIctTermList = false;
        Loading.show(this);
        afterTime = "1970-01-01 00:00:00";
        AraKoicaApplication araKoicaApplication = (AraKoicaApplication) BaseApplication.getInstance();
        araKoicaApplication.getAraKoicaApiImpl().getNewIctTermList(afterTime,
                new DalApiListener<List<DIC_ICT_TERM>>() {
                    @Override
                    public void onSuccess(List<DIC_ICT_TERM> response) {
                        boolean result = false;
                        if ((response != null) && (response.size() > 0)) {
                            result = subDatabase.replaceIctTermListWithoutUserInfo(response);
                            refreshIctTermList();
                            ToastUtil.getInstance(getApplicationContext()).show(R.string.updated_with_latest_data_from_server);
                        } else {
                            ToastUtil.getInstance(getApplicationContext()).show(R.string.toast_nothing_to_update);
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
