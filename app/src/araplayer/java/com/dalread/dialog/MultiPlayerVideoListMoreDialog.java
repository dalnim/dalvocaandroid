package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogMultiplayerVideoListMoreBinding;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

public class MultiPlayerVideoListMoreDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickListener listener;
    private Context context;
    private DialogMultiplayerVideoListMoreBinding binding;
    private boolean isShowingPlaylistVideos;
    @Override
    protected View getContentView() {
        binding = DialogMultiplayerVideoListMoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public MultiPlayerVideoListMoreDialog(@NonNull Context context, boolean isShowingPlaylistVideos, OnClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.isShowingPlaylistVideos = isShowingPlaylistVideos;
        showOrHideMenus();
        initColor();
    }
    private void initColor() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(context, binding.llMain, R.color.multiPlayerBackgroundLightBlackColor);
            AraThemeUtil.setTextColor(context, binding.tvTitle, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvDeleteFile, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSelectAll, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvUnSelectAll, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvRemoveFromPlaylist, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvConvertToHiddenFiles, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvConvertToNormalFiles, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvCancel, R.color.textPrimaryWhiteColor);
        }
    }

    protected void showOrHideMenus() {
        binding.llRemoveFromPlaylist.setVisibility(isShowingPlaylistVideos ? View.VISIBLE : View.GONE);
        //MANAGE_EXTERNAL_STORAGE을 안쓰면 파일명앞에  . 추가/제거는 안보여준다.
//        binding.llConvertToHiddenFiles.setVisibility(View.GONE);
//        binding.llConvertToNormalFiles.setVisibility(View.GONE);
    }

    @Override
    protected void initOnClickListener() {
        binding.llSelectAll.setOnClickListener(this);
        binding.llUnSelectAll.setOnClickListener(this);
        binding.llRemoveFromPlaylist.setOnClickListener(this);
        binding.llConvertToHiddenFiles.setOnClickListener(this);
        binding.llConvertToNormalFiles.setOnClickListener(this);
        binding.llDeleteFile.setOnClickListener(this);
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
}
