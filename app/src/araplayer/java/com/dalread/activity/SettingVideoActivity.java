package com.dalread.activity;

import android.content.Intent;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityVideoSettingPlayerBinding;

import butterknife.OnClick;

public class SettingVideoActivity extends BasePlayerActivity {

    private ActivityVideoSettingPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityVideoSettingPlayerBinding.inflate(getLayoutInflater());
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

    @Override
    public void initView() {
        initData();
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.llSubtitle, R.id.llGesture})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSubtitle:
                openSubtitleScreen();
                break;
            case R.id.llGesture:
                openGestureScreen();
                break;
        }
    }

    private void openSubtitleScreen() {
        Intent intent = new Intent(this, SettingVideoSubtitleActivity.class);
        startActivity(intent);
    }

    private void openGestureScreen() {
        Intent intent = new Intent(this, SettingVideoGestureActivity.class);
        startActivity(intent);
    }
}
