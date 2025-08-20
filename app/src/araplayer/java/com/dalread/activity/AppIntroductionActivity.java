package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityAppIntroductionBinding;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseInterstitialAdManager;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

public class AppIntroductionActivity extends BaseActivity {

    private boolean fromMenu;
    private boolean showPrevious;
    private ActivityAppIntroductionBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityAppIntroductionBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            binding.vDot.setBackgroundColor(ContextCompat.getColor(this, R.color.multiPrimaryDarkColor));
        }

        BaseInterstitialAdManager.loadInterstitialAd(this, BaseMobileAd.Admob.AraMultiPlayer.Lite.INTERSTITIAL_UNIT_ID);
        initData();
        initView();
    }

//    @Override
//    protected void bindButterKnife() {
//        // Do nothing
//    }

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
        final String text = getToolbar().getTvRight().getText().toString();
        if (text.equalsIgnoreCase(getString(R.string.next))){
            int pos = binding.vPager.getCurrentItem();
            binding.vPager.setCurrentItem(++pos);
        } else if (text.equalsIgnoreCase(getString(R.string.done))){
            if (!fromMenu) {
                if (AppFlavorUtil.isAraMultiPlayerAppLite()) {
                    Loading.showDelay(this);
                    if (Utils.isDebug()) {
                        // 디버그는 앱설치시 광고 없이 바로 홈 화면으로 이동
                        openMultiPlayerHomeActivity();
                    } else {
                        // 앱 설치시 광고 보여줌.
                         BaseInterstitialAdManager.showInterstitialAd(this,
                             BaseMobileAd.Admob.AraMultiPlayer.Lite.INTERSTITIAL_UNIT_ID,
                             () -> {
                                 BaseInterstitialAdManager.setNullToInterstitialAd();
                                 openMultiPlayerHomeActivity();
                             }
                         );
                    }
////                    openNewScreen(MainHomeActivity.class, true);
                } else if (AppFlavorUtil.isAraMultiPlayerAppPro()) {
                    openMultiPlayerHomeActivity();
                } else {
                    openNewScreen(ChooseMotherLanguageActivity.class, true);
                    finish();
                }
            }
//            finish();
        }
    }

    private void openMultiPlayerHomeActivity() {
        openNewScreen(MultiPlayerMainHomeActivity.class, true);
        Loading.hide();
        finish();
    }

    private void initData() {
        fromMenu = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_FROM_MENU, false);
    }

    private void initView() {
        binding.vPager.addOnPageChangeListener(onPageChangeListener);
        binding.vPager.setAdapter(new AppIntroductionAdapter(getSupportFragmentManager()));
        binding.vDot.setupWithViewPager(binding.vPager);
        onPageChangeListener.onPageSelected(0);
    }

    @Nullable
    @Override
    public Toolbar getToolbar() {
        return binding.header;
    }

    private void hideBackButton() {
        getToolbar().hideIconLeft();
        showPrevious = false;
    }

    private void showBackButton() {
        getToolbar().showIconLeft();
        showPrevious = false;
    }

    private void showPreviousButton() {
        getToolbar().showIconLeft();
        showPrevious = true;
    }

    private void showNextButton() {
        getToolbar().setTextRight(R.string.next);
    }

    private void showDoneButton() {
        getToolbar().setTextRight(R.string.done);
    }

    @Override
    public void onBackPressed() {
//        if (showPrevious) {
//            int pos = vPager.getCurrentItem();
//            vPager.setCurrentItem(--pos);
//            return;
//        }

        super.onBackPressed();
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            switch (i) {
                case 0:
                    if (fromMenu) {
                        showBackButton();
                    } else {
                        hideBackButton();
                    }
                    showNextButton();
                    break;
                case 1:
                case 2:
                case 3:
                    if (fromMenu) {
                        showPreviousButton();
                    } else {
                        hideBackButton();
                    }
                    showNextButton();
                    break;
//                case 2:
//                    if (fromMenu) {
//                        showPreviousButton();
//                    } else {
//                        hideBackButton();
//                    }
//                    showNextButton();
//                    break;
//                case 3:
//                    if (fromMenu) {
//                        showPreviousButton();
//                    } else {
//                        hideBackButton();
//                    }
//                    showNextButton();
//                    break;
                case 4:
                    if (fromMenu) {
                        showPreviousButton();
                    } else {
                        hideBackButton();
                    }
                    showDoneButton();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

    static class AppIntroductionAdapter extends FragmentPagerAdapter {

        public AppIntroductionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new AppIntroduction1Fragment();
                case 1:
                    return new AppIntroduction2Fragment();
                case 2:
                    return new AppIntroduction3Fragment();
                case 3:
                    return new AppIntroduction4Fragment();
                default:
                    return new AppIntroduction5Fragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
