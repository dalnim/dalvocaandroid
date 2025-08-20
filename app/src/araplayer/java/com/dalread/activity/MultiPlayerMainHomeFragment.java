package com.dalread.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.BasePlayerFragment;
import com.dalread.base.EnumMultiplePlayer;
import com.dalread.base.EnumType;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.databinding.FragmentMultiPlayerMainBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SelectScreenCountDialog;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.DoubleClickHelper;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.util.AbstractPointUtil;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraFreePointUtil;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DarkThemeUtil;
import com.dalread.util.GuideUtil;
import com.dalread.util.InAppReviewUtil;
import com.dalread.util.MobileAd;
import com.dalread.util.PointUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class MultiPlayerMainHomeFragment extends BasePlayerFragment implements View.OnClickListener {
    protected AdView mAdView;
    protected AlertDialog alertDialog;
    private MultiPlayerDatabase multiPlayerDatabase;
    private EnumMultiplePlayer enumMultiplePlayer;
    private AbstractPointUtil pointUtil;
    private boolean needToShowWatchRewardedAd = true;
    protected MultiPlayerMainHomeActivity activity;
    private String randomStringToGetPoint = "AraMulitPlayer";
    private boolean canGetFreePoint = true;

    private FragmentMultiPlayerMainBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentMultiPlayerMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = (MultiPlayerMainHomeActivity) getActivity();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void initView() {
        pointUtil = new PointUtil(activity);
        initDialog();
        initData();
        binding.tvRemainPoint.setOnClickListener(v -> {
            getActivity().startActivity(new Intent(activity, InAppPointListActivity.class));
        });
        initSubDatabase();
        showRewardButton();
        addMobileAdsView();
        loadBanner();
        showGuideHowToUse();
        binding.btnScreenCount.setOnTouchListener((v, event) -> {
            return doubleClickHelper.onTouch(v, event, null);
        });
        binding.ivScreenCount.setOnTouchListener((v, event) -> {
            return doubleClickHelper.onTouch(v, event, null);
        });
        initOnClickListener();

        // 프로 버전이면 광고/포인트 UI 숨김
        if (AppFlavorUtil.isAraMultiPlayerAppPro()) {
            binding.btnWatchRewardedAd.setVisibility(View.GONE);
            binding.tvRemainPoint.setVisibility(View.GONE);
        }
    }

    private void showClearMultiScreenHistoryButton() {
        if (multiPlayerDatabase.isHasList()) {
            binding.ivClearMultiScreenHistory.setVisibility(View.VISIBLE);
        } else {
            binding.ivClearMultiScreenHistory.setVisibility(View.INVISIBLE);
        }
    }

    private void showSetPoint0ButtonOnDebug() {
        if (UserUtil.isDebugOrAdminUser(activity)) {
            binding.ivSetPoint0.setVisibility(View.VISIBLE);
        } else {
            binding.ivSetPoint0.setVisibility(View.GONE);
        }
    }

    private void showRewardButton() {
        if (AppFlavorUtil.isAraMultiPlayerAppLite() && (needToShowWatchRewardedAd || pointUtil.needToShowRewardButton())) {
            binding.btnWatchRewardedAd.setVisibility(View.VISIBLE);
            needToShowWatchRewardedAd = false;
        }
//            if (sharedPreferences.isFirstShowGuideScreenCount() || pointUtil.needToShowRewardButton()) {
//                btnWatchRewardedAd.setVisibility(View.VISIBLE);
////                showGuideWatchAd();
//            } else {
//                btnWatchRewardedAd.setVisibility(View.INVISIBLE);
//            }
    }

    private void initDialog() {
        alertDialog = new AlertDialog(activity);
    }

    private void refreshRemainPoint() {
        int point = pointUtil.getPoint();
        if (sharedPreferences.isPointAdded()) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.text_scale_anim);
            binding.tvRemainPoint.startAnimation(animation);
            sharedPreferences.setPointAdded(false);
        }
        binding.tvRemainPoint.setText(getResources().getQuantityString(R.plurals.point, point, point));
        showRewardButton();
    }

    @Override
    public void initData() {
        DLog.d(getLogTag(), "initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        getMultiPlayerScreenCount();
        showClearMultiScreenHistoryButton();
        showSetPoint0ButtonOnDebug();
        BaseMobileAd.loadRewardedAd(getContext());
        refreshRemainPoint();
    }

    private void getMultiPlayerScreenCount() {
        enumMultiplePlayer = EnumMultiplePlayer.getEnum(sharedPreferences.getMultiPlayerScreenCount());
        updateScreenCountButton();
    }

    void showGuideHowToUse() {
        showGuideWatchAd();
    }

    private void showGuideScreenCount() {
        if (sharedPreferences.isFirstShowGuideScreenCount()) {
            sharedPreferences.setFirstShowGuideScreenCount();
            String title = getString(R.string.guide_multi_player_screen_count);
            activity.runOnUiThread(() -> {
                GuideUtil.showGuideView(activity, title, binding.btnScreenCount, view -> {
                    if (AppFlavorUtil.isAraMultiPlayerAppLite()) {
                        showGuidePointDeduction();
                    }
                });
            });
        } else {
            //일단 무료 포인트 얻는 가이드는 디버깅 모드에서만 보여주자.
            if (UserUtil.isDebugOrAdminUser(activity) && sharedPreferences.isFirstShowGuideGetFreePoint()) {
                sharedPreferences.setFirstShowGuideGetFreePoint();
                String title = getString(R.string.guide_multi_player_get_free_point);
                activity.runOnUiThread(() -> {
                    GuideUtil.showGuideView(activity, title, binding.btnScreenCount, view -> {});
                });
            }
        }
    }

    private void showGuidePointDeduction() {
        if (sharedPreferences.isFirstShowGuidePointDeduction()) {
            sharedPreferences.setFirstShowGuidePointDeduction();
            String title = getString(R.string.guide_multi_player_point_deduction);
            activity.runOnUiThread(() -> {
                GuideUtil.showGuideView(activity, title, binding.tvRemainPoint, view -> {});
            });
        }
    }

    private void showGuideWatchAd() {
        if (AppFlavorUtil.isAraMultiPlayerAppPro()) {
            sharedPreferences.setFirstShowGuideWatchAd();
            binding.btnWatchRewardedAd.setVisibility(View.INVISIBLE);
            showGuideScreenCount();
        } else {
            if (sharedPreferences.isFirstShowGuideWatchAd()) {
                sharedPreferences.setFirstShowGuideWatchAd();
                String title = getString(R.string.guide_watch_ad_to_get_point);
                GuideUtil.showGuideView(activity, title, binding.btnWatchRewardedAd, view -> {
                    showGuideScreenCount();
                });
            }
        }
    }

    private void temporarilyHideBannerAdsAfterClick() {
        if (AppFlavorUtil.isAraMultiPlayerApp() && !BaseMobileAd.isShowAdsAfterTimeSinceLastClick(activity)) {
            binding.adViewContainer.setVisibility(View.GONE);
        }
    }
    private void watchRewardedAd() {
        pointUtil.showRewardedAd(createRewardPointListener());
    }

    private PointUtil.OnRewardPointListener createRewardPointListener() {
        return new PointUtil.OnRewardPointListener() {
            @Override
            public void onSuccess() {
                refreshRemainPoint();
            }

            @Override
            public void onContinue() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail() {

            }
        };
    }

    private void openMultiplePlayerView(int numberOfScreens) {
        if (AppFlavorUtil.isAraMultiPlayerAppLite() && pointUtil.needToShowFullAd()) {
            final YesNoDialog dialog = new YesNoDialog(activity,
                    R.string.info,
                    R.string.msg_warning_no_points_watch_ads_get_points, null,
                    new OnYesNoClickListener() {
                        @Override
                        public void onYesClick(View view, Object object) {
                            watchRewardedAd();
                        }

                        @Override
                        public void onNoClick(View view, Object object) {

                        }
                    });
            dialog.show();
        } else {
            if (multiPlayerDatabase.isHasList()) {
                final YesNoDialog dialog = new YesNoDialog(activity, R.string.info, R.string.msg_ask_multiplayer_open_last_watched_videos, null, new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        openMultiplePlayerView(numberOfScreens, true);
                    }

                    @Override
                    public void onNoClick(View view, Object object) {
                        openMultiplePlayerView(numberOfScreens, false);
                    }
                });
                dialog.show();
            } else {
                openMultiplePlayerView(numberOfScreens, false);
            }
        }
    }
    private void openMultiplePlayerView(int numberOfScreens, boolean isLoadLastWatchedVideos) {
        Intent intent = new Intent(activity, MultiplePlayerActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_NUMBER_OF_SCREENS_MULTIPLE_PLAYER, numberOfScreens);
        intent.putExtra(Constant.BUNDLE.KEY_LOAD_LAST_WATCHED_VIDEO_MULTIPLE_PLAYER, isLoadLastWatchedVideos);
        activity.openNewScreen(intent);
    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        new Thread(() -> ServerManager.getInstance().disconnectFTPServer()).start();
//    }

    private void initOnClickListener() {
        binding.btnWatchVideo.setOnClickListener(this);
        binding.btnScreenCount.setOnClickListener(this);
        binding.btnWatchRewardedAd.setOnClickListener(this);
        binding.ivClearMultiScreenHistory.setOnClickListener(this);
        binding.ivSetPoint0.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWatchVideo:
                openMultiplePlayerView(enumMultiplePlayer.getNumberOfScreen());
                break;
            case R.id.btnScreenCount:
                //스크린 버튼은 여길 안타고, doubleClickHelper의 onClick을 탄다.
                selectScreenCount();
                break;
            case R.id.btnWatchRewardedAd:
                // 새로 만든 액티비티로 이동
                Intent adIntent = new Intent(activity, MultiPlayerWatchAdActivity.class);
                activity.startActivity(adIntent);
                break;
            case R.id.ivClearMultiScreenHistory:
                clearMultiScreenHistory();
                break;
            case R.id.ivSetPoint0:
                pointUtil.consumePoint(1000);
                refreshRemainPoint();
                break;
        }
    }

//    @OnClick({
//            R.id.btnWatchVideo, R.id.btnScreenCount, R.id.btnWatchRewardedAd, R.id.ivClearMultiScreenHistory, R.id.ivSetPoint0
//    })
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btnWatchVideo:
//                openMultiplePlayerView(enumMultiplePlayer.getNumberOfScreen());
//                break;
//            case R.id.btnScreenCount:
//                //스크린 버튼은 여길 안타고, doubleClickHelper의 onClick을 탄다.
//                selectScreenCount();
//                break;
//            case R.id.btnWatchRewardedAd:
//                watchRewardedAd();
//                break;
//            case R.id.ivClearMultiScreenHistory:
//                clearMultiScreenHistory();
//                break;
//            case R.id.ivSetPoint0:
//                pointUtil.consumePoint(1000);
//                refreshRemainPoint();
//                break;
//        }
//    }
    private void selectScreenCount() {
        String[] displayNumberOfScreens = EnumMultiplePlayer.getNames(activity);
        SelectScreenCountDialog dialog = new SelectScreenCountDialog(activity, displayNumberOfScreens,new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                final int pos = (int) object;
                if (pos < displayNumberOfScreens.length) {
                    int numberOfScreens = Integer.parseInt(displayNumberOfScreens[pos]);
                    sharedPreferences.setMultiPlayerScreenCount(numberOfScreens);
                    getMultiPlayerScreenCount();
                }
            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void updateScreenCountButton() {
        String screenCountString = getResources().getQuantityString(R.plurals.multi_plyaer_screen_count, enumMultiplePlayer.getNumberOfScreen(), enumMultiplePlayer.getNumberOfScreen());
        binding.btnScreenCount.setText(screenCountString);
    }


    protected void addMobileAdsView() {
        //        if (!canShowAds) return;
        // Admob Step 1 - Create an AdView and set the ad unit ID on it.
        mAdView = new AdView(activity);
        if (DarkThemeUtil.isDarkMode(activity)) {
            mAdView.setForeground(new ColorDrawable(ContextCompat.getColor(activity, R.color.ads_layer_color_in_dark_mode)));
        }
        mAdView.setAdUnitId(MobileAd.getAdsBannerId(activity));
        binding.adViewContainer.addView(mAdView);
        temporarilyHideBannerAdsAfterClick();
    }

    protected void loadBanner() {
        if (AppFlavorUtil.isAraMultiPlayerAppPro()) return;
//        if (!canShowAds) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = BaseMobileAd.getAdSize(activity);
        // Admob Step 4 - Set the adaptive ad size on the ad view.
        mAdView.setAdSize(adSize);

        // Admob Step 5 - Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                DLog.d(getLogTag(), "onAdLoaded");
//                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ON_ADS_LOADED, true));
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                DLog.d(getLogTag(), "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                sharedPreferences.setLastAdsClickedTime(System.currentTimeMillis());
                if (binding.adViewContainer != null) {
                    binding.adViewContainer.removeView(mAdView);
                }
                DLog.d(getLogTag(), "onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                DLog.d(getLogTag(), "onAdClicked");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                DLog.d(getLogTag(), "onAdClosed");
            }
        });
    }

    private void initSubDatabase() {
        String databasePath = BaseStorageUtil.getAraMultiPlayerDBPathWithFileName(getContext());
//        String databasePath = StorageUtil.getAssetFolderName(getContext()) + File.separator + "araonesoft.multiplayer.sqlite";
        boolean isFIleExist = StorageUtil.isFileExist(databasePath);
        if (multiPlayerDatabase != null) {
            multiPlayerDatabase.close();
        }
        multiPlayerDatabase = null;
        multiPlayerDatabase = MultiPlayerDatabase.getInstance(getContext(), databasePath);
    }

    private void clearMultiScreenHistory() {
        final YesNoDialog dialog = new YesNoDialog(activity, R.string.warning, R.string.msg_ask_multiplayer_screen_clear_history, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                multiPlayerDatabase.clearMultiScreenHistory();
                ToastUtil.getInstance(activity).show(R.string.msg_ok_multiplayer_screen_clear_history);
                binding.ivClearMultiScreenHistory.setVisibility(View.INVISIBLE);
                InAppReviewUtil.requestReview(activity);
            }

            @Override
            public void onNoClick(View view, Object object) {
                InAppReviewUtil.requestReview(activity);
            }
        });
        dialog.show();
    }

    private DoubleClickHelper doubleClickHelper = new DoubleClickHelper(new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            DLog.d("", "onClick");
            selectScreenCount();
        }

        @Override
        public void onDoubleClick(View view, Object data) {
            DLog.d("", "onDoubleClick");
            if (canGetFreePoint) {
                randomStringToGetPoint = AraFreePointUtil.generateRandomString(activity);
                TypeInputDialog typeInputDialog = new TypeInputDialog(activity, dialogListener);
                typeInputDialog.setTitle(R.string.dialog_get_free_point_title);
                typeInputDialog.setSubTitle(activity.getString(R.string.dialog_get_free_point_subtitle, randomStringToGetPoint));
                typeInputDialog.show();
//                Utils.showSoftKeyboard(activity);
            } else {
                ToastUtil.getInstance(activity).show(R.string.toast_get_free_point_fail_restart_app);
            }
        }

        @Override
        public void onLongPress(View view, Object data) {
            DLog.d("", "onDoubleClick");
        }
    });

    private BaseDialogListener dialogListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            DLog.d("", "onBaseDialogListenerOk");
            String playlistName = (String) data;
            if (playlistName.trim().equals(randomStringToGetPoint)) {
                pointUtil.addPoint(1);
                refreshRemainPoint();
                ToastUtil.getInstance(activity).show(activity.getString(R.string.toast_get_free_point_success, 1));
                canGetFreePoint = false;
                Utils.hideSoftKeyboard(activity);
                dialog.dismiss();
            } else {
                ToastUtil.getInstance(activity).show(R.string.toast_get_free_point_fail_wrong_name);
            }

        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            DLog.d("", "onBaseDialogListenerCancel");
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            DLog.d("", "onBaseDialogListenerClick");
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
            DLog.d("", "onBaseDialogListenerClickMulti");
        }
    };
}