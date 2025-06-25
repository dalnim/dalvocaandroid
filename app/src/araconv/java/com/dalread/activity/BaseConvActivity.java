package com.dalread.activity;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.composition.PlayTTS;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.helper.CustomTabActivityHelper;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaDownload;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.ChatGptWebUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DarkThemeUtil;
import com.dalread.util.MobileAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindColor;
import butterknife.BindDimen;

public abstract class BaseConvActivity extends BaseActivity implements CustomTabActivityHelper.ConnectionCallback {
//    @BindColor(R.color.color_divider)
//    protected int clDivider;
//    @BindDimen(R.dimen.divider_height)
//    protected float dividerHeight;

//    protected PlayVocaHelper playVocaHelper;
    protected PlayTTS playTTS;
    private ArrayList<File> downloadFiles;
    private ArrayList<VocaDownload> serverVocaDownloads, localVocaDownloads;
    private ArrayList<IVocaFullPlayTTSItem> vocas;
    private LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap;
    private int downloadPos;
    private File voiceFolder;
    private int playingState;
    protected VocaKnowActivity vocaKnowActivity;
    protected SubDatabase subDatabase;

    protected FrameLayout adContainerView;
    protected AdView mAdView;
    protected boolean canShowAds = true;
    protected CustomTabActivityHelper customTabActivityHelper;
//    protected ConvCommonMenuDialog convCommonMenuDialog;
//    @Override
//    protected int getContentViewId() {
//        return 0;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSubDatabase();
        vocaKnowActivity = new VocaKnowActivity(this, subDatabase);
        playTTS = new PlayTTS(this);
        long lastTimeClickOnAds = sharedPreferences.getLastAdsClickedTime();
        if (lastTimeClickOnAds > 0) {
            if (System.currentTimeMillis() - lastTimeClickOnAds < Constant.TIME_DISTANCE_TO_SHOW_ADS_AFTER_CLICKED) {
                canShowAds = false;
            }
        }

        initData();
        initLayout();
        initDialog();
        initListener();
         // In VocaActivity.
        getData();
//        BaseMobileAd.loadInterstitialAd(context);
    }


    protected void initData() {

    }

    protected void initDialog() {

    }

    protected void initLayout() {

    }

    protected void initListener() {
        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);
    }

    private void initSubDatabase() {
        if (subDatabase != null) {
            subDatabase.close();
        }
        subDatabase = null;
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(context);
        subDatabase = SubDatabase.getInstance(this, destPathWithFileName);
    }

    protected final DialogInterface.OnClickListener onDialogItemClickListener = (dialog, which) -> {
        switch (which) {
//            case R.id.llRefreshVocaFromServer:
////                refreshVocaFromServer();
//                break;
            case R.id.tvBackToHome:
                backToHome();
                break;
            default:
                break;
        }
    };

    protected void addMobileAdsView() {
        if (!canShowAds) return;
        adContainerView = findViewById(R.id.adViewContainer); // Todo : how to use binding instead of findViewById?
        // Admob Step 1 - Create an AdView and set the ad unit ID on it.
        mAdView = new AdView(this);
        if (DarkThemeUtil.isDarkMode(this)) {
            mAdView.setForeground(new ColorDrawable(ContextCompat.getColor(this, R.color.ads_layer_color_in_dark_mode)));
        }
        mAdView.setAdUnitId(MobileAd.getAdsBannerId(this));
        adContainerView.addView(mAdView);
    }

    protected void loadBanner() {
        if (!canShowAds) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = BaseMobileAd.getAdSize(this);
        // Admob Step 4 - Set the adaptive ad size on the ad view.
        mAdView.setAdSize(adSize);

        // Admob Step 5 - Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                DLog.d(getLogTag(), "onAdLoaded");
                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ON_ADS_LOADED, true));
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
                if (adContainerView != null) {
                    adContainerView.removeView(mAdView);
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
    protected void getData() {

    }

    @Override
    protected void onStart() {
//        playTTS.stopPlayVoca();
//        if (playVocaHelper != null) {
//            playVocaHelper.stop();
//            playVocaHelper.clearListener();
//        }
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
        DLog.i(getLogTag(), "onStart");
        if (!sharedPreferences.getKeepPlayingOnBackgroundMode())
            playTTS.stopPlayVoca();
    }

    @Override
    protected void onPause() {
        DLog.i(getLogTag(), "onPause");
        if (isFinishing()) {
            playTTS.stopPlayVoca();
//        } else if (!backgroundMode) {
//            playVocaHelper.stop();
        }
        super.onPause();
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

    }

    protected void backToHome() {
        backToHome(MainHomeActivity.class);
    }

    @Override
    public void onCustomTabsConnected() {
        Uri uri  = Uri.parse(ChatGptWebUtil.getGptUrl(this));
        customTabActivityHelper.mayLaunchUrl(uri, null, null);
    }

    @Override
    public void onCustomTabsDisconnected() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }
}
