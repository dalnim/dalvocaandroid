package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogMultiplayerOneVideoBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;
import com.dalread.BuildConfig;

public class MultiPlayerOneVideoDialog extends BasePlayerDialog implements View.OnClickListener{
    private OnClickDialogListener listener;
    private DialogMultiplayerOneVideoBinding binding;
    private boolean isHasSavedAbRepeatTime;
    private boolean isHasPlaylist;
    private boolean isFullScreenMode;
    private Context context;

    @Override
    protected View getContentView() {
        binding = DialogMultiplayerOneVideoBinding.inflate(getLayoutInflater());
        updateFullScreenMenuText();
        View view = binding.getRoot();
        return view;
    }


    public MultiPlayerOneVideoDialog(@NonNull Context context, boolean isHasPlaylist, boolean isHasSavedAbRepeatTime, boolean isFullScreenMode, OnClickDialogListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.isHasSavedAbRepeatTime = isHasSavedAbRepeatTime;
        this.isHasPlaylist = isHasPlaylist;
        this.isFullScreenMode = isFullScreenMode;
        showOrHideAbRepeatMenu();
        showOrHidePlayFromPlaylistMenu();
        showOrHideNetworkTestMenu();
        initColor();
    }

    private void updateFullScreenMenuText() {
        binding.tvFullScreen.setText(isFullScreenMode ? R.string.multi_player_video_menu_exit_full_screen : R.string.multi_player_video_menu_full_screen);
    }
    private void initColor() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(context, binding.llMain, R.color.multiPlayerBackgroundLightBlackColor);
            AraThemeUtil.setTextColor(context, binding.tvTitle, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvPlaySaveAbRepeat, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvDeleteSavedAbRepeat, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvPlayFromPlayList, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvShowVideoTitle, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvNetworkTest, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvDuplicateAllScreens, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvDeleteFile, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvFullScreen, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvCancel, R.color.textPrimaryWhiteColor);
        }
    }
    private void showOrHideAbRepeatMenu() {
        int visibility = isHasSavedAbRepeatTime ? View.VISIBLE : View.GONE;
        binding.llPlaySaveAbRepeat.setVisibility(visibility);
        binding.llDeleteSavedAbRepeat.setVisibility(visibility);
    }

    private void showOrHidePlayFromPlaylistMenu() {
        int visibility = isHasPlaylist ? View.VISIBLE : View.GONE;
        binding.llPlayFromPlayList.setVisibility(visibility);
    }

    private void showOrHideNetworkTestMenu() {
        // 디버그 모드에서만 네트워크 테스트 메뉴 표시
        int visibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
        binding.llNetworkTest.setVisibility(visibility);
    }

    @Override
    protected void initOnClickListener() {
        binding.llPlaySaveAbRepeat.setOnClickListener(this);
        binding.llDeleteSavedAbRepeat.setOnClickListener(this);
        binding.llPlayFromPlayList.setOnClickListener(this);
        binding.llDuplicateAllScreens.setOnClickListener(this);
        binding.llDeleteFile.setOnClickListener(this);
        binding.llShowVideoTitle.setOnClickListener(this);
        binding.llFullScreen.setOnClickListener(this);
        binding.llNetworkTest.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v, null);
        }
    }

//    @OnClick({R.id.llPlaySaveAbRepeat, R.id.llDeleteSavedAbRepeat, R.id.llDuplicateAllScreens, R.id.llShowVideoTitle, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, null);
//        }
//    }
}
