package com.dalread.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.util.Constant;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;

public class AppIntroductionActivity extends BaseActivity { // BaseVocaActivity {

    @BindView(R.id.v_pager) ViewPager vPager;
    @BindView(R.id.v_dot) TabLayout vDot;
    private boolean fromMenu;
    private boolean showPrevious;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_app_introduction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
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
        final String text = toolbar.getTvRight().getText().toString();
        if (text.equalsIgnoreCase(getString(R.string.next))){
            int pos = vPager.getCurrentItem();
            vPager.setCurrentItem(++pos);
        } else if (text.equalsIgnoreCase(getString(R.string.done))){
            if (!fromMenu) {
                openNewScreen(ChooseStudyLanguageActivity.class, true);
            }
            finish();
        }
    }

    private void initData() {
        fromMenu = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_FROM_MENU, false);
    }

    private void initView() {
        vPager.addOnPageChangeListener(onPageChangeListener);
        vPager.setAdapter(new AppIntroductionAdapter(getSupportFragmentManager()));
        vDot.setupWithViewPager(vPager);
        onPageChangeListener.onPageSelected(0);
    }

    private void hideBackButton() {
        toolbar.hideIconLeft();
        showPrevious = false;
    }

    private void showBackButton() {
        toolbar.showIconLeft();
        showPrevious = false;
    }

    private void showPreviousButton() {
        toolbar.showIconLeft();
        showPrevious = true;
    }

    private void showNextButton() {
        toolbar.setTextRight(R.string.next);
    }

    private void showDoneButton() {
        toolbar.setTextRight(R.string.done);
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
                    if (fromMenu) {
                        showPreviousButton();
                    } else {
                        hideBackButton();
                    }
                    showNextButton();
                    break;
                case 2:
                    if (fromMenu) {
                        showPreviousButton();
                    } else {
                        hideBackButton();
                    }
                    showNextButton();
                    break;
                case 3:
                    if (fromMenu) {
                        showPreviousButton();
                    } else {
                        hideBackButton();
                    }
                    showNextButton();
                    break;
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
