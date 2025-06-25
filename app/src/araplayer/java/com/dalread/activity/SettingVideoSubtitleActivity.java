package com.dalread.activity;

import android.view.View;

import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityVideoSettingSubtitlePlayerBinding;

public class SettingVideoSubtitleActivity extends BasePlayerActivity {
    private ActivityVideoSettingSubtitlePlayerBinding binding;
    @Override
    protected View getContentView() {
        binding = ActivityVideoSettingSubtitlePlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }


    @Override
    protected void setFullscreen() {

    }

    @Override
    public void initView() {
        initData();
    }

    @Override
    public void initData() {
        binding.tvFontSize.setText(String.valueOf(sharedPreferences.getPlayerSubtitleFontSize(this)));
    }

    @Override
    public void onHeaderLeftClick() {
        super.onBackPressed();
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
