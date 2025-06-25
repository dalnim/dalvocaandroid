package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogPlayerShowSortBinding;
import com.dalread.util.FileUtil;

public class PlayerShowSortDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickListener listener;
    private boolean isSmallGroupVideoListLoaded;
    private DialogPlayerShowSortBinding binding;
    private boolean isShowSubtitle;
    private SharedPreferencesDB sharedPreferences;
    private boolean isTabVideo;
    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSortBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerShowSortDialog(@NonNull Context context, boolean isSmallGroupVideoListLoaded, boolean isShowSubtitle, boolean isTabVideo, SharedPreferencesDB sharedPreferences, OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.sharedPreferences = sharedPreferences;
        this.isTabVideo = isTabVideo;
        this.isSmallGroupVideoListLoaded = isSmallGroupVideoListLoaded;
        setMenuText();
        hideMenus();
        handleMenusForMusicApp();
    }

    private void setMenuText() {
        binding.tvShowSubtitleFiles.setText(isShowSubtitle ?
                R.string.hide_subtitle_files : R.string.show_subtitle_files);
        binding.tvShowNormalVideoFiles.setText(sharedPreferences.getShowNormalVideoFileList() ?
                R.string.show_hidden_video_files : R.string.show_normal_video_files);
    }
    //Dalnim : If I think don't need to showsubtitle file list to users, then remove this code.
    private void hideTvShowSUbtitleFilesMenuOnReleaseMode() {
//        binding.tvShowSubtitleFiles.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }

    private void handleMenusForMusicApp() {
        if (FileUtil.isMusicApp()) {
            binding.llShowNormalVideoFiles.setVisibility(View.GONE);
            binding.tvRefreshMediaList.setText(R.string.refresh_music_list);
        }
    }

    private void hideMenus() {
        hideMenusWhenSmllGroupVideoListLoaded();
        hideTvShowSUbtitleFilesMenuOnReleaseMode();
        int editViewVisibility = !isSmallGroupVideoListLoaded && isTabVideo ? View.VISIBLE : View.GONE;
        boolean isShowNormalVideoFileList = sharedPreferences.getShowNormalVideoFileList();
        binding.tvEdit.setVisibility(editViewVisibility);
        binding.dividerTvEdit.setVisibility(editViewVisibility);

        if (isShowNormalVideoFileList && !isSmallGroupVideoListLoaded) {
            binding.llRefreshMediaList.setVisibility(View.VISIBLE);
            binding.llShowNormalVideoFiles.setVisibility(View.VISIBLE);
        } else {
            binding.llRefreshMediaList.setVisibility(View.GONE);
            binding.llShowNormalVideoFiles.setVisibility(View.GONE);
        }

        if (FileUtil.isMusicApp()) {
            binding.llMultiplePlayer.setVisibility(View.GONE);
        }
    }

    private void hideMenusWhenSmllGroupVideoListLoaded() {
        int visibility = isSmallGroupVideoListLoaded ? View.GONE : View.VISIBLE;
//        binding.tvShowSubtitleFiles.setVisibility(visibility);
        binding.llShowNormalVideoFiles.setVisibility(visibility);
        binding.llRefreshMediaList.setVisibility(visibility);

    }

    @Override
    protected void initOnClickListener() {
        binding.llSort.setOnClickListener(this);
        binding.llEdit.setOnClickListener(this);
        binding.llSettings.setOnClickListener(this);
        binding.llShowSubtitleFiles.setOnClickListener(this);
        binding.llShowNormalVideoFiles.setOnClickListener(this);
        binding.llRefreshMediaList.setOnClickListener(this);
        binding.llMultiplePlayer.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v.getId() == R.id.tvCancel) {
            return;
        }

        if (listener != null) {
            listener.onClick(this, v.getId());
        }
    }

//    @OnClick({R.id.llSort, R.id.llEdit, R.id.llSettings, R.id.llShowSubtitleFiles, R.id.llShowNormalVideoFiles, R.id.llRefreshMediaList, R.id.llMultiplePlayer, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (view.getId() == R.id.tvCancel) {
//            return;
//        }
//
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
