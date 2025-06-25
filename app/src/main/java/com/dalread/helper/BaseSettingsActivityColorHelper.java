package com.dalread.helper;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.dalread.R;
import com.dalread.base.BaseSettingsActivity;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

public class BaseSettingsActivityColorHelper {
    private BaseSettingsActivity activity;

    public BaseSettingsActivityColorHelper(BaseSettingsActivity activity) {
        this.activity = activity;
        initColor();
    }
    public void initColor() {
        setRoot();
        initHeaders();
        initItemsColor();
    }
    private void setRoot() {
        AraThemeUtil.setBackgroundColor(activity, activity.binding.root, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPrimaryDarkColor : R.color.backgroundCellColor);
    }
    private void initHeaders() {
        initTableHeader(activity.binding.tvTableHeaderChooseLanguage);
        initTableHeader(activity.binding.tvTableHeaderEtc);
    }
    private void initItemsColor() {
        initMenuDisplayLang();
        initAppVersion();
        initChooseFoldersForMedia();
        initChooseDarkTheme();
        initSecureScreen();
    }

    private void initTableHeader(TextView textView) {
        setTextColor(textView, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textPrimaryWhiteColor : R.color.textCellHeaderColor);
        setBackgroundColor(textView, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPrimaryDarkColor : R.color.backgroundCellHeaderColor);
    }


    private void initMenuDisplayLang() {
        initItemColor(activity.binding.llMenuDisplayLangInside, activity.binding.tvMenuDisplayLangItem, activity.binding.tvMenuDisplayLangValue);
    }
    private void initAppVersion() {
        initItemColor(activity.binding.llAppVersionInside, activity.binding.tvAppVersionItem, activity.binding.tvAppVersionValue);
    }
    private void initChooseDarkTheme() {
        initItemColor(activity.binding.llChooseDarkThemeInside, activity.binding.tvChooseDarkThemeItem, activity.binding.tvChooseDarkThemeValue);
    }

    private void initChooseFoldersForMedia() {
        initItemColor(activity.binding.llChooseFoldersForMediaInside, activity.binding.tvChooseFoldersForMediaItem, activity.binding.tvChooseFoldersForMediaValue);
    }
    private void initSecureScreen() {
        //SwitchCompat은 SwitchCompat에 백그라운드 색상을 칠해야한다.
        initItemColor(activity.binding.scSecureScreen, activity.binding.scSecureScreen, null);
    }
    private void initItemColor(View view, TextView textViewItem, TextView textViewValue) {
        setBackgroundColor(view, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPrimaryColor : R.color.backgroundCellColor);
        setTextColor(textViewItem, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textPrimaryWhiteColor : R.color.textCellItemColor);
        if (textViewValue != null) {
            setTextColor(textViewValue, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textPrimaryWhiteColor : R.color.textCellItemValueColor);
        }
    }

    private void setTextColor(TextView textView, @ColorRes int colorResId) {
        AraThemeUtil.setTextColor(activity, textView, colorResId);
    }

    private void setBackgroundColor(View view, @ColorRes int colorResId) {
        AraThemeUtil.setBackgroundColor(activity, view, colorResId);
    }
}
