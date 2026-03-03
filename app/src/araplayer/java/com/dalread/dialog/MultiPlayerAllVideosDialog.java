package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogMultiplayerAllVideosBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

public class MultiPlayerAllVideosDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickDialogListener listener;
    private Context context;
    private DialogMultiplayerAllVideosBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogMultiplayerAllVideosBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public MultiPlayerAllVideosDialog(@NonNull Context context, OnClickDialogListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        initColor();
    }
    private void initColor() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(context, binding.llMain, R.color.multiPlayerBackgroundLightBlackColor);
            AraThemeUtil.setTextColor(context, binding.tvTitle, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvPlayAllVideosABRepeat, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvPlayAllVideosIntervals, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvMoveAllVideosToStart, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvHideAllVideosUI, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvCloseAllVideos, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvResizeAllVideos, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvChooseScreenNumberOfScreens, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSelectVideosToPlay, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSettings, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvScreenStoreLayout, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvExportSqlite, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSwapScreens, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvCancel, R.color.textPrimaryWhiteColor);

        }
    }

    @Override
    protected void initOnClickListener() {
        binding.tvHideAllVideosUI.setOnClickListener(this);
        binding.tvCloseAllVideos.setOnClickListener(this);
        binding.tvResizeAllVideos.setOnClickListener(this);
        binding.tvPlayAllVideosABRepeat.setOnClickListener(this);
        binding.tvPlayAllVideosIntervals.setOnClickListener(this);
        binding.tvMoveAllVideosToStart.setOnClickListener(this);
        binding.tvMuteAllVideos.setOnClickListener(this);
        binding.tvUnmuteAllVideos.setOnClickListener(this);
        binding.tvSelectVideosToPlay.setOnClickListener(this);
        binding.llChooseScreenNumberOfScreens.setOnClickListener(this);
        binding.llSettings.setOnClickListener(this);
        binding.llScreenStoreLayout.setOnClickListener(this);
        binding.llExportSqlite.setOnClickListener(this);
        binding.llSwapScreens.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v, null);
        }
    }
}
