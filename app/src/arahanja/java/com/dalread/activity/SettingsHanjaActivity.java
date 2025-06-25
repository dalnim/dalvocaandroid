package com.dalread.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumTheme;
import com.dalread.base.PlayVocaActivity;
import com.dalread.databinding.ActivityHanjaSettingsBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.DarkThemeUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;

//TODO : Need to inherit BaseSettingsActivity
public class SettingsHanjaActivity extends PlayVocaActivity {
    private SingleChoiceDialog singleChoiceDialog;
    private String[] languageList;
    private int selectedMenuLanguage;

    private ActivityHanjaSettingsBinding binding;
    private String[] displayFontSizeList;
    private int[] displayFontSizeListRatioList;
    private EnumLanguage menuLanguage;
    private String[] displayThemes;
    private int selectedTheme;

    protected View getContentView() {
        binding = ActivityHanjaSettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }
    @Override
    protected int getContentViewId() {
        return 0;//R.layout.activity_hanja_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initData();
        updateUI();
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        alertDialog = new AlertDialog(this);
        singleChoiceDialog = new SingleChoiceDialog(this);
    }

    private void initData() {
        languageList = EnumLanguage.getLanguages();
        List<String> languages = Arrays.asList(languageList);
        menuLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMenuLanguage());
        selectedMenuLanguage = languages.indexOf(menuLanguage.getFormatUser());
        displayFontSizeList = this.getResources().getStringArray(R.array.array_display_font_size_list);
        displayFontSizeListRatioList = this.getResources().getIntArray(R.array.array_display_font_size_list_ratio);

        displayThemes = EnumTheme.getNames(this);
        int themeStorage = sharedPreferences.getSelectedTheme();
        String themeStorageValue = EnumTheme.getNameFromId(this, themeStorage);
        selectedTheme = Arrays.asList(displayThemes).indexOf(themeStorageValue);
        binding.tvSelectedTheme.setText(themeStorageValue);
    }

    private void updateUI() {
        binding.tvMenuValue.setText(menuLanguage.getFormatUser());

        binding.tvFontSize.setText(getStoredFontSize());

        if (Utils.isDebugOrAdminUser(this)) {
            binding.llHanjaFontSize.setVisibility(View.VISIBLE);
        } else {
            binding.llHanjaFontSize.setVisibility(View.GONE);
        }

        binding.tvAppVersionValue.setText(Voca.getAppVersion());
    }

    @OnClick({
            R.id.v_menu_value, R.id.llHanjaFontSize, R.id.v_choose_theme})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.v_menu_value:
                showMenuLanguageDialog();
                break;
            case R.id.llHanjaFontSize:
                showMenuHanjaFontSizeDialog();
                break;
            case R.id.v_choose_theme:
                showChooseThemeDialog();
                break;
            default:
                break;
        }
    }

    private void showMenuLanguageDialog() {
        singleChoiceDialog.show(
            R.string.menu,
                languageList,
            selectedMenuLanguage,
            R.string.ok,
            R.string.cancel,
            new OnClickDialogListener() {
                @Override
                public void onClick(View view, Object object) {
                    final int which = (int) object;
                    if (which != selectedMenuLanguage) {
                        sharedPreferences.setMenuLanguage(EnumLanguage.findByFormatUser(languageList[which]).getFormatApi());
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.MENU_LANGUAGE_CHANGED, null));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                            finishAffinity();
                            startActivity(i);
                        } else {
                            recreate();
                        }
                    }
                }

                @Override
                public void onDismiss(View view, Object object) {

                }
            });
    }

    private void showMenuHanjaFontSizeDialog() {
        int selectedFontSizeIndex = getSelectedHanjaFontSizeIndex();
        singleChoiceDialog.show(
                R.string.menu_hanja_font_size,
                displayFontSizeList,
                selectedFontSizeIndex,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedFontSizeIndex) {
                            sharedPreferences.setAraHanjaFontSizeRatio(displayFontSizeListRatioList[which] / 10.0f);
                            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                            finishAffinity();
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private int getSelectedHanjaFontSizeIndex() {
        float storedAraHanjaFontSizeRatio = sharedPreferences.getAraHanjaFontSizeRatio(this);
        int selectedFontSizeIndex = 1;
        for(int i = 0; i < displayFontSizeListRatioList.length; i++) {
            float displayFontSizeListRatio = displayFontSizeListRatioList[i] / 10.0f;
            if (storedAraHanjaFontSizeRatio == displayFontSizeListRatio) {
                selectedFontSizeIndex = i;
                break;
            }
        }
        return selectedFontSizeIndex;
    }

    private String getStoredFontSize() {
        String result = "";
        int selectedFontSizeIndex = getSelectedHanjaFontSizeIndex();
        if (selectedFontSizeIndex >= 0 && getSelectedHanjaFontSizeIndex() <= displayFontSizeList.length) {
            result = displayFontSizeList[selectedFontSizeIndex];
        }
        return result;
    }

    private void showChooseThemeDialog() {
        singleChoiceDialog.showWrapContentHeight(
                R.string.choose_theme,
                displayThemes,
                selectedTheme,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedTheme) {
                            sharedPreferences.setSelectedTheme(which);
//                            int darkLightMode = ((AraHanjaApplication) application).getDarkLightThemeFromStorage();
                            int darkLightMode = DarkThemeUtil.getDarkLightThemeFromStorage(sharedPreferences);//.getDarkLightThemeFromStorage();
                            AppCompatDelegate.setDefaultNightMode(darkLightMode);
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.MENU_LANGUAGE_CHANGED, null));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                finishAffinity();
                                startActivity(i);
                            } else {
                                recreate();
                            }
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }
}
