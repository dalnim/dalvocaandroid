package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityExportWordListBinding;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;

public class ExportWordListActivity extends BaseActivity {

    private ActivityExportWordListBinding binding;
    private PlayerFileModel playerFileModel;

    public static Intent createIntent(Context context, PlayerFileModel playerFileModel) {
        Intent intent = new Intent(context, ExportWordListActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        return intent;
    }

    @Override
    protected View getContentView() {
        binding = ActivityExportWordListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setupToolbar();
    }

    private void initData() {
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
    }

    private void setupToolbar() {
        binding.header.setTitle(R.string.export_word_list);
        binding.header.setIconLeft(R.drawable.ic_back);
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
