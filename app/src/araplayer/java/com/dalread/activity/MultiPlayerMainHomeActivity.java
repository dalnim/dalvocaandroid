package com.dalread.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.MenuAdapter;
import com.dalread.base.BaseActivity;
import com.dalread.base.EnumMultiplePlayer;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.component.Toolbar;
import com.dalread.composition.BaseMainHome;
import com.dalread.databinding.ActivityMultiPlayerMainBinding;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.AraMultiPlayerBillingClientHelper;
import com.dalread.helper.BillingClientHelper;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.MenuModel;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.PermissionUtils;
import com.dalread.util.RealmUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiPlayerMainHomeActivity extends BaseActivity implements OnNavigationItemClickListener {
    private BaseMainHome baseMainHomeActivity;
    private ImageView imgAvatar;
    private RecyclerView rvLeftNavigation;
    private MenuAdapter leftNavigationAdapter;
    private ArrayList<MenuModel> leftNavigationItems;
    private Runnable leftNavigationRunnable;
    private int currentBaseUrlPos = -1;
    private SingleChoiceDialog singleChoiceDialog;
    private int currentLeftNavigationId;

    private boolean isOpenSettingsToGrantPermission;
    private String[] displayNumberOfScreens;
    private int selectedNumberOfScreenPos;
    public BillingClientHelper billingClientHelper;
    private ActivityMultiPlayerMainBinding binding;
    @Override
    protected View getContentView() {
        binding = ActivityMultiPlayerMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Nullable
    public Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    public void onHeaderLeftClick() {
        openDrawer();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBillingClientHelper();
        baseMainHomeActivity = new BaseMainHome(this, billingClientHelper);
        initDialog();
        initLeftNavigation();
        openMaiHomeFragment();
        initData();
        hideToolBarForMultiPlayer();
    }

    private void setBillingClientHelper() {
        billingClientHelper = AraMultiPlayerBillingClientHelper.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOpenSettingsToGrantPermission) {
            isOpenSettingsToGrantPermission = false;
            checkPermission();
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        switch (event.getEventType()) {
            case OPEN_SETTINGS_TO_GRANT_PERMISSION:
                isOpenSettingsToGrantPermission = true;
                break;
        }
    }

    private void hideToolBarForMultiPlayer() {
        runOnUiThread(() -> {
            binding.header.hideRight();
            binding.header.hideSearchView();
        });
    }

//    @Override
    public void initData() {
        checkPermission();
        displayNumberOfScreens = EnumMultiplePlayer.getNames(this);
        selectedNumberOfScreenPos = Arrays.asList(displayNumberOfScreens).indexOf(String.valueOf(EnumMultiplePlayer.FOUR.getNumberOfScreen()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_CODE_MANAGE_EXTERNAL_STORAGE) {
            if (PermissionUtils.checkManageExternalStoragePermission()) {
                sharedPreferences.setFirstShowManageExternalStorageInstalledUser();
            } else {
                showExternalManageStoragePermissionDialogAgain();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE:
                if (PermissionUtils.checkExternalStoragePermission(this)) {
                    sharedPreferences.setFirstShowExternalStorage();
//                    openMaiHomeFragment(); //여기에서 다시 부르면 프레그먼트가 다시 불리기 때문에 가이드를 보여줄때 getString에서 MultiPlayerMainHomeFragment not attached to a context.에러가 난다.
                } else {
                    PermissionUtils.onRequestPermissionsResultAraMultiPlayer(this, requestCode, permissions, grantResults);
                }
        }
    }
    private void openMaiHomeFragment() {
        DLog.d(getLogTag(), "openMainPlayerFragment");
        binding.header.setIconLeft(R.drawable.ic_drawer_menu);
//        toolbar.setIconRight(R.drawable.ic_search_white_24dp);
        binding.header.setIconRight2(R.drawable.ic_toolbar_sort);
        binding.header.getIconRight().setVisibility(View.GONE);
        binding.header.getTvRight().setVisibility(View.GONE);
        binding.header.showTitle();
//        createDalPlayerFolder();
        Fragment fragment = new MultiPlayerMainHomeFragment();
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }
    @Override
    public void onBackPressed() {
        if (isOpenDrawer()) {
            closeDrawer();
        } else {
            baseMainHomeActivity.confirmExitApp();
        }
    }
    
    private void initDialog() {
        singleChoiceDialog = new SingleChoiceDialog(this);
    }

    @Override
    public void onClick(int id) {
        closeDrawer();
        currentLeftNavigationId = id;
        handler.postDelayed(leftNavigationRunnable, Constant.DRAWER_CLOSE_TIME);
    }

    private void initLeftNavigation() {
        View navView = binding.navLeft.inflateHeaderView(R.layout.nav_header_main);
        AraThemeUtil.setBackgroundColor(this, binding.navLeft, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundLightBlackColor : R.color.home_side_menu_item_background);
        ConstraintLayout constraintLayout = navView.findViewById(R.id.csHomeSideMenuTopAvatarRectangle);
        AraThemeUtil.setBackgroundColor(this, constraintLayout, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundBlackColor : R.color.home_side_menu_top_avatar_rectangle);
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
        rvLeftNavigation = navView.findViewById(R.id.listMenu);
        rvLeftNavigation.setLayoutManager(new LinearLayoutManager(this));
        rvLeftNavigation.setHasFixedSize(true);
        rvLeftNavigation.setItemAnimator(new DefaultItemAnimator());
        leftNavigationItems = new ArrayList<>();
        handler = new Handler();
        leftNavigationRunnable = () -> {
            switch (currentLeftNavigationId) {
                case Constant.NAVIGATION.SETTINGS:
                    DalFlavor.openSettings(this);
                    break;
                case Constant.NAVIGATION.NAVER_CAFE:
                    DalFlavor.openNaverCafe(this);
                    break;
                case Constant.NAVIGATION.KAKAOTALK_GROUPCHAT:
                    Utils.openWeb(this, Constant.URL_ARAMULTIPLAYER_KAKAOTALK_GROUPCHAT);
                    break;
                case Constant.NAVIGATION.MAIL:
                    DalFlavor.openMail(this, sharedPreferences);
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
            }
        };
        updateLeftNavigation();
    }

    private void updateLeftNavigation() {
        leftNavigationItems.clear();
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SETTINGS, R.drawable.ic_setting, getString(R.string.menu_setting)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.MAIL, R.drawable.ic_mail, getString(R.string.menu_mail)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.NAVER_CAFE, R.drawable.ic_manual, getString(R.string.menu_naver_cafe_manual)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.KAKAOTALK_GROUPCHAT, R.drawable.ic_manual, getString(R.string.menu_kakaotalk_groupchat1)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.SHARE_APP, R.drawable.ic_share_app, getString(R.string.menu_share_app)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.RATE_APP, R.drawable.ic_rate_star, getString(R.string.menu_rate_app)));
        leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.IN_APP_PURCHASE, R.drawable.ic_download_2, getString(R.string.left_navi_items_in_app_purchase)));
        if (UserUtil.isDebugOrAdminUser(this)) {
            currentBaseUrlPos = sharedPreferences.getBaseUrlIndex();
            leftNavigationItems.add(new MenuModel(Constant.NAVIGATION.BACKUP, 0, getString(R.string.backup_realm_db)));
        }
        if (leftNavigationAdapter == null) {
            rvLeftNavigation.setAdapter(leftNavigationAdapter = new MenuAdapter(this, leftNavigationItems));
        } else {
            leftNavigationAdapter.updateData(leftNavigationItems);
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!PermissionUtils.checkManageExternalStoragePermission()) {
                //새로 설치하는 유저와 기존유저들 둘다 불어본다.
                if (sharedPreferences.isFirstShowManageExternalStorage() || sharedPreferences.isFirstShowManageExternalStorageInstalledUser()) {
                    showExternalManageStoragePermissionDialog();
                }
            }
        } else {
            if (sharedPreferences.isFirstShowExternalStorage() && !PermissionUtils.checkExternalStoragePermission(this)) {
                PermissionUtils.checkExternalStoragePermission(this, true);
            }
        }
        //MANAGE_EXTERNAL_STORAGE을 안쓰면 아래를 사용하면 된다.
//        if (!PermissionUtils.checkExternalStoragePermission(this)) {
//            PermissionUtils.checkExternalStoragePermission(this, true);
//        }
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

    private void showExternalManageStoragePermissionDialog() {
            int msgId = AppFlavorUtil.isAraMultiPlayerApp() ? R.string.msg_reason_request_manage_external_storage_for_multi_player : R.string.msg_reason_request_manage_external_storage;
            final YesNoDialog dialog = new YesNoDialog(MultiPlayerMainHomeActivity.this, R.string.info, msgId, null, new OnYesNoClickListener() {
                @Override
                public void onYesClick(View view, Object object) {
                    PermissionUtils.requestManageExternalStoragePermission(MultiPlayerMainHomeActivity.this);
                }

                @Override
                public void onNoClick(View view, Object object) {
                    showExternalManageStoragePermissionDialogAgain();
                }
            });
            dialog.show();
    }

    private void showExternalManageStoragePermissionDialogAgain() {
        final YesNoDialog yesNoDialog = new YesNoDialog(MultiPlayerMainHomeActivity.this, R.string.warning, R.string.msg_warning_need_manage_external_storage_for_multi_player, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                PermissionUtils.requestManageExternalStoragePermission(MultiPlayerMainHomeActivity.this);
            }

            @Override
            public void onNoClick(View view, Object object) {
                finishAffinity();
            }
        });
        yesNoDialog.show();
    }
}
