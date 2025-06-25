package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogMultiplayerShowSortBinding;
import com.dalread.manager.PlaylistManager;
import com.dalread.model.PlaylistModel;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;
import com.dalread.util.UserUtil;

import java.util.List;

public class MultiPlayerShowSortDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickListener listener;
    private Context context;
    private DialogMultiplayerShowSortBinding binding;
    private SharedPreferencesDB sharedPreferences;
    private boolean isShowingPlaylistVideos;
    @Override
    protected View getContentView() {
        binding = DialogMultiplayerShowSortBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public MultiPlayerShowSortDialog(@NonNull Context context, SharedPreferencesDB sharedPreferences, boolean isShowingPlaylistVideos, OnClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.sharedPreferences = sharedPreferences;
        this.isShowingPlaylistVideos = isShowingPlaylistVideos;
        showOrHideMenus();
        initColor();
    }

    private void initColor() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(context, binding.llMain, R.color.multiPlayerBackgroundLightBlackColor);
            AraThemeUtil.setTextColor(context, binding.tvTitle, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvShowNormalVideoFiles, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvShowHiddenVideoFiles, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSort, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvEdit, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvRefreshMediaList, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvCreatePlaylist, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvDeletePlaylist, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvPlaylist, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvRestorePlaylistBackup, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvCancel, R.color.textPrimaryWhiteColor);
        }
    }
    protected void showOrHideMenus() {
        List<PlaylistModel> models = PlaylistManager.getAllPlaylistModels();
        if (models.isEmpty()) {
            binding.llDeletePlaylist.setVisibility(View.GONE);
            binding.llPlaylist.setVisibility(View.GONE);
        } else {
            binding.llDeletePlaylist.setVisibility(View.VISIBLE);
            binding.llPlaylist.setVisibility(View.VISIBLE);
        }

        if (sharedPreferences.getShowNormalVideoFileList()) {
            binding.llShowNormalVideoFiles.setVisibility(View.VISIBLE);
            binding.llShowHiddenVideoFiles.setVisibility(View.GONE);
        } else {
            binding.llShowNormalVideoFiles.setVisibility(View.GONE);
            binding.llShowHiddenVideoFiles.setVisibility(View.VISIBLE);
        }
        binding.llRestorePlaylistBackup.setVisibility(UserUtil.isDebugOrAdminUser(context) ? View.VISIBLE : View.GONE);
        //플레이 리스트이면, 둘다 갈수 있도록 보여준다.
        if (isShowingPlaylistVideos) {
            binding.llShowNormalVideoFiles.setVisibility(View.VISIBLE);
            binding.llShowHiddenVideoFiles.setVisibility(View.VISIBLE);
        } else {
        }
    }

    @Override
    protected void initOnClickListener() {
        binding.llShowNormalVideoFiles.setOnClickListener(this);
        binding.llShowHiddenVideoFiles.setOnClickListener(this);
        binding.llSort.setOnClickListener(this);
        binding.llEdit.setOnClickListener(this);
        binding.llRefreshMediaList.setOnClickListener(this);
        binding.llCreatePlaylist.setOnClickListener(this);
        binding.llDeletePlaylist.setOnClickListener(this);
        binding.llPlaylist.setOnClickListener(this);
        binding.llRestorePlaylistBackup.setOnClickListener(this);
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
