package com.dalread.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.AppDownloadListAdapter;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityAppDownloadListBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.AppInfoModel;
import com.dalread.util.OpenViewUtil;

import java.util.ArrayList;
import java.util.List;

public class AppDownloadListActivity extends BaseActivity {

    private ActivityAppDownloadListBinding binding;
    private AppDownloadListAdapter appDownloadListAdapter;

    @Override
    protected View getContentView() {
        binding = ActivityAppDownloadListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.rvInfo.setLayoutManager(new LinearLayoutManager(this));

        appDownloadListAdapter = new AppDownloadListAdapter(this, initAppInforModel(), listener);
        binding.rvInfo.setAdapter(appDownloadListAdapter);
    }

    @NonNull
    private List<AppInfoModel> initAppInforModel() {
        List<AppInfoModel> appInfoModels = new ArrayList<>();
        Drawable araPlayerIcon = ContextCompat.getDrawable(this, R.drawable.ic_app_ja);
        Drawable araHanjaIcon = ContextCompat.getDrawable(this, R.drawable.ic_app_cn);

        appInfoModels.add(new AppInfoModel("AraPlayer", "Video player for English learners.", araPlayerIcon, "https://play.google.com/store/apps/details?id=com.dalnimsoft.araplayer"));
        appInfoModels.add(new AppInfoModel("AraPlayer Japanese", "Video player for Japanese learners.", araPlayerIcon, "https://play.google.com/store/apps/details?id=com.dalnimsoft.araplayer_japanese"));
        appInfoModels.add(new AppInfoModel("AraPlayer Chinese", "Video player for Chinese learners.", araPlayerIcon, "https://play.google.com/store/apps/details?id=com.dalnimsoft.araplayer_chinese"));
        appInfoModels.add(new AppInfoModel("AraMultiPlayer", "Watch 4 videos simultaneously.", araPlayerIcon, "https://play.google.com/store/apps/details?id=com.araonesoft.aramultiplayer"));
        appInfoModels.add(new AppInfoModel("AraConv", "Learn English conversation with ChatGPT.", araPlayerIcon, "https://play.google.com/store/apps/details?id=com.dalnimsoft.araconv"));
        appInfoModels.add(new AppInfoModel("AraConv Korean", "Learn Korean conversation.", araPlayerIcon, "https://play.google.com/store/apps/details?id=com.dalnimsoft.araconv_korea"));
        appInfoModels.add(new AppInfoModel("AraHangul", "Learn Hangul.", araPlayerIcon, "https://play.google.com/store/apps/details?id=com.dalnimsoft.arahangul"));
        appInfoModels.add(new AppInfoModel("AraHanja", "Learn Hanja", araHanjaIcon, "https://play.google.com/store/apps/details?id=com.dalnimsoft.arahanja"));
        return appInfoModels;
    }

    OnClickListener listener = (view, object) -> {
        if (object instanceof AppInfoModel) {
            AppInfoModel model = (AppInfoModel) object;
            OpenViewUtil.openGooglePlayStore(AppDownloadListActivity.this, model.getGooglePlayUrl());
        }
    };

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
}
