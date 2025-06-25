package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.adapter.ChooseLanguageAdapter;
import com.dalread.base.BaseActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.databinding.ActivityChooseLanguageBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.util.BaseBindUtils;

import java.util.List;

public abstract class ChooseLanguageActivity extends BaseActivity {
    protected abstract void setViewTitle();
    protected abstract void getLanguageList();
    protected abstract void openNewScreenAfterChooseLanguage();
    protected abstract void saveChosenLanguage(EnumLanguage enumLanguage);

    protected ChooseLanguageAdapter adapter;
    protected List<EnumLanguage> languages;

    protected ActivityChooseLanguageBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityChooseLanguageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewTitle();
        init();
    }

    @Override
    public void onHeaderLeftClick() {
        finishAffinity();
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
        selectButtonClick();
    }

    protected void init() {
        binding.header.getTvRight().setVisibility(View.INVISIBLE);
        getLanguageList();
        adapter = new ChooseLanguageAdapter(onClickListener, languages);
        binding.rvLanguages.setAdapter(adapter);
        binding.rvLanguages.setLayoutManager(new LinearLayoutManager(this));
//        binding.rvLanguages.addItemDecoration(new SeparatorDecoration(this, ContextCompat.getColor(this, R.color.color_divider), this.getResources().getDimension(R.dimen.divider_height)));
        binding.rvLanguages.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(this), BaseBindUtils.getDividerHeight(this)));
    }

    protected OnClickListener onClickListener = (view, object) -> {
        int pos = (int) object;
        binding.header.getTvRight().setVisibility(pos >= 0 ? View.VISIBLE : View.INVISIBLE);
    };

    private void selectButtonClick() {
        int pos = adapter.getCheckedItem();
        if (pos >= 0) {
            final EnumLanguage motherLanguage = languages.get(pos);
            saveChosenLanguage(motherLanguage);
//            sharedPreferences.setMotherTongueLanguage(motherLanguage.getFormatApi());
//            if (motherLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
//                application.getPlayVocaHelper().setStudyTTSSpeed(sharedPreferences.getSettingTTSSpeedChinese());
//            } else if (motherLanguage == EnumLanguage.ENGLISH) {
//                application.getPlayVocaHelper().setStudyTTSSpeed(sharedPreferences.getSettingTTSSpeedEnglish());
//            } else if (motherLanguage == EnumLanguage.JAPANESE) {
//                application.getPlayVocaHelper().setStudyTTSSpeed(sharedPreferences.getSettingTTSSpeedJapanese());
//            } else if (motherLanguage == EnumLanguage.KOREAN) {
//                application.getPlayVocaHelper().setStudyTTSSpeed(sharedPreferences.getSettingTTSSpeedKorean());
//            } else if (motherLanguage == EnumLanguage.HANJA) {
//                application.getPlayVocaHelper().setStudyTTSSpeed(sharedPreferences.getSettingTTSSpeedHanja());
//            }
            openNewScreenAfterChooseLanguage();
        }
    }
}
