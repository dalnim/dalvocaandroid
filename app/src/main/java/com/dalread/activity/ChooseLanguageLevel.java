package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.ActivityChooseLanguageLevelBinding;
import com.dalread.util.Constant;

public class ChooseLanguageLevel extends BaseActivity implements View.OnClickListener {
    private ActivityChooseLanguageLevelBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityChooseLanguageLevelBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }
    private void initData() {
        binding.btnWordLevelBeginner.setOnClickListener(this);
        binding.btnWordLevelPreIntermediate.setOnClickListener(this);
        binding.btnWordLevelIntermediate.setOnClickListener(this);
        binding.btnWordLevelPostIntermediate.setOnClickListener(this);
        binding.btnWordLevelAdvanced.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String strLanguagelevel = Constant.SIGN_UP_LEVEL_BEGINNER;
        switch (v.getId()) {
            case R.id.btnWordLevelBeginner:
                strLanguagelevel = Constant.SIGN_UP_LEVEL_BEGINNER;
                break;
            case R.id.btnWordLevelPreIntermediate:
                strLanguagelevel = Constant.SIGN_UP_LEVEL_PRE_INTERMEDIATE;
                break;
            case R.id.btnWordLevelIntermediate:
                strLanguagelevel = Constant.SIGN_UP_LEVEL_INTERMEDIATE;
                break;
            case R.id.btnWordLevelPostIntermediate:
                strLanguagelevel = Constant.SIGN_UP_LEVEL_POST_INTERMEDIATE;
                break;
            case R.id.btnWordLevelAdvanced:
                strLanguagelevel = Constant.SIGN_UP_LEVEL_ADVANCED;
                break;
        }
        SharedPreferencesDB.getInstance(this).setSettingMyLanguageLevel(strLanguagelevel);
        startActivity(new Intent(this, ChooseMotherLanguageActivity.class));
    }

    @Override
    public void onHeaderLeftClick() {

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
}