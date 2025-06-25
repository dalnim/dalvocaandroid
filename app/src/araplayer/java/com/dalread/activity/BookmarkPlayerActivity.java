package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityPlayerBinding;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

public class BookmarkPlayerActivity extends BasePlayerActivity {
    private ActivityPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void setFullscreen() {
        Utils.toggleFullscreen(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        PermissionUtils.checkSystemWritePermission(this);
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

    @Subscribe
    public void onEvent(SuccessEvent event) {

    }

    @Override
    public void initView() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                DLog.d(getLogTag(), "not full screen");
                Utils.toggleFullscreen(this);
            }
        });
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
        Fragment fragment = new BookmarkPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, b.getParcelable(Constant.PLAYER.INTENT.KEY_VIDEO_FILE));
        bundle.putInt(Constant.PLAYER.INTENT.KEY_TIME, b.getInt(Constant.PLAYER.INTENT.KEY_TIME, Constant.PLAYER.BOOKMARK.REPEAT.TIME_3));
        fragment.setArguments(bundle);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }
}
