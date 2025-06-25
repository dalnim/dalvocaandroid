package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityPlayerEditSubtitleBinding;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

public class EditSubtitleActivity extends BasePlayerActivity {
    private ActivityPlayerEditSubtitleBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayerEditSubtitleBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void setFullscreen() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    public void initView() {
        initData();
    }

    @Override
    public void initData() {
//        initPlayVocaHelper();
        openPlayerFragment();
    }

    private void openPlayerFragment() {
        Bundle b = getIntent().getExtras();
        if (b == null) {
            finish();
            return;
        }
        Fragment fragment = new EditSubtitleFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, b.getParcelable(Constant.PLAYER.INTENT.KEY_VIDEO_FILE));
        bundle.putInt(Constant.PLAYER.INTENT.KEY_INDEX, b.getInt(Constant.PLAYER.INTENT.KEY_INDEX));
        if (b.getString(Constant.PLAYER.INTENT.KEY_DATA) != null) {
            bundle.putString(Constant.PLAYER.INTENT.KEY_DATA, b.getString(Constant.PLAYER.INTENT.KEY_DATA));
        }
        fragment.setArguments(bundle);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }
}
