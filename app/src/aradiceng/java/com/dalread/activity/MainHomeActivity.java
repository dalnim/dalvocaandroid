package com.dalread.activity;

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

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.MenuAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.composition.BaseMainHome;
import com.dalread.databinding.ActivityMainIctTermBinding;
import com.dalread.dialog.HanjaMainMenuDialog;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnToolbarLeftButtonChangeListener;
import com.dalread.model.MenuModel;
import com.dalread.model.VocaKnowAndBookmarkList;
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
import java.util.Objects;

public class MainHomeActivity extends BaseTermIctActivity implements OnAsyncTaskListenerWithType, OnNavigationItemClickListener, OnToolbarLeftButtonChangeListener {
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

    private ActivityMainIctTermBinding binding;
    private int currentBottomNavigationId;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_SYNC_KNOW = TYPE_INIT_DATA + 1;

    @Override
    protected int getContentViewId() {
        return 0;//R.layout.activity_workbooks;
    }

    @Override
    protected View getContentView() {
        binding = ActivityMainIctTermBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseMainHomeActivity = new BaseMainHome(this);
        initLeftNavigation();
        updateLeftNavigation();
        initFragment();
        updateMenuVisibility();
        initEventBus();
        selectFirstNvItem();
        sharedPreferences.setFirstLaunchApp(false);
    }
    @NonNull
    private void initFragment() {
        fragmentIctTermList = new MainIctTermListFragment();
        fragmentSearchHistory = new MainSearchHistoryFragment();
    }


    private void updateMenuVisibility() {
        toolbar.showTvRight();
    }

    private void selectFirstNvItem() {
        binding.nvBottom.setSelectedItemId(R.id.nav_main_ict_term_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    }

    @Override
    public void onHeaderTextRightClick() {
        openHanjaMainMenuDialog();
    }

    private void openHanjaMainMenuDialog() {
        final HanjaMainMenuDialog dialog = new HanjaMainMenuDialog(this, isMainBottomNavigationId(), sharedPreferences, (view, object) -> {
            switch (view.getId()) {
                case R.id.llSyncKnownWithServer:
                    syncKnowWithServer();
                    break;
                case R.id.llAddTerm:
                    if (isMainBottomNavigationId()) {
                        fragmentIctTermList.chooseSttEngine(this);
                    }
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
    }
    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(this, this, data, type, true).execute();
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
        toolbar.setTitle(R.string.app_name);
        toolbar.showTvRight();
        toolbar.hideIconRight();
        toolbar.setTextRight(R.string.menu);
        currentBottomNavigationId = R.id.nav_main_ict_term_list;
    }

    private void changeToMainSearchHistoryTab() {
        toolbar.hideSearchView();
        Utils.loadFragment(MainHomeActivity.this, fragmentSearchHistory, getFragmentContainerId(), false);
        toolbar.setTitle(R.string.view_title_search_history);
        toolbar.showTitle(); //MainHome에서 검색하다가 히스토리탭으로오면 타이틀이 사라진다.
        toolbar.hideTvRight();
        currentBottomNavigationId = R.id.nav_main_search_history;
    }

    private boolean isMainBottomNavigationId() {
        return currentBottomNavigationId == R.id.nav_main_ict_term_list;
    }

    private void initLeftNavigation() {
        View navView = binding.navLeft.inflateHeaderView(R.layout.nav_header_main);
        imgAvatar = navView.findViewById(R.id.nav_iv_avatar);
        imgAvatar.setOnClickListener(v -> {
            if (getUserID() > 0) {
                openNewScreen(MyProfileActivity.class);
            } else {
                alertDialog.showLogInRequired();
            }
        });
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
        if (leftNavigationAdapter == null) {
            rvLeftNavigation.setAdapter(leftNavigationAdapter = new MenuAdapter(this, leftNavigationItems));
        } else {
            leftNavigationAdapter.updateData(leftNavigationItems);
        }
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
    public void onInitAsyncTask(int searchType) {
        switch (searchType) {
            case TYPE_SYNC_KNOW:
//                Loading.show(this, R.string.msg_type_refresh_voice_recording_file_list_message);
                //Want to show with progress
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
}
