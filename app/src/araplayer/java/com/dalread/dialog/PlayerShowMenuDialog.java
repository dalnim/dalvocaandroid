package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowMenuBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.UserUtil;

public class PlayerShowMenuDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

//    @BindView(R.id.llSubtitle) View llSubtitle;
//    @BindView(R.id.ll_clear_video_cache) View llClearVideoCache;
//    @BindView(R.id.llVideoBookmarkList) View llVideoBookmarkList;
//    @BindView(R.id.llStudyMode) View llStudyMode;
//    @BindView(R.id.llEnableChangeTableWidth) View llEnableChangeTableWidth;

//    @BindView(R.id.ll_sleep) View llSleep;
    private OnClickDialogListener listener;
    private boolean isRightHandMode = true;
    private DialogPlayerShowMenuBinding binding;
    @Override
    protected View getContentView() {
        binding = DialogPlayerShowMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerShowMenuDialog(@NonNull Context context, PlayerFileModel playerFileModel, boolean isFullScreenMode, boolean isVerticalMode, OnClickDialogListener listener) {
        super(context);
        setDialogSizeWider(true);
        this.listener = listener;
        setOnDismissListener(this);

        if (!playerFileModel.isHasSubRuby(context)) {
            binding.llStudyMode.setVisibility(View.GONE);
        }
        if (isVerticalMode || isFullScreenMode) {
            binding.llEnableChangeTableWidth.setVisibility(View.GONE);
        } else {
            if (playerFileModel.isHasSubRuby(context)) {
                binding.llEnableChangeTableWidth.setVisibility(View.VISIBLE);
            } else {
                binding.llEnableChangeTableWidth.setVisibility(View.GONE);
            }
        }

        hideMenusOnReleaseMode();
    }

    private void hideMenusOnReleaseMode() {
        if (UserUtil.isDebugOrAdminUser(getContext())) {
            binding.llClearVideoCache.setVisibility(View.VISIBLE);
            binding.llChoosePlayingScreenTheme.setVisibility(View.VISIBLE);
            binding.llVideoBookmarkList.setVisibility(View.VISIBLE);
            binding.llStudyMode.setVisibility(View.VISIBLE);
        } else {
            binding.llClearVideoCache.setVisibility(View.GONE);
            binding.llChoosePlayingScreenTheme.setVisibility(View.GONE);
            binding.llVideoBookmarkList.setVisibility(View.GONE);
            binding.llStudyMode.setVisibility(View.GONE);

        }
    }

    @Override
    protected void initOnClickListener() {
        binding.tvSetting.setOnClickListener(this);
        binding.tvVideoBookmarkList.setOnClickListener(this);
        binding.llStudyMode.setOnClickListener(this);
        binding.llClearVideoCache.setOnClickListener(this);
        binding.llWordList.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.llEnableChangeTableWidth.setOnClickListener(this);
        binding.llChoosePlayingScreenTheme.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                this.view = null;
                break;
            default:
                this.view = v;
                break;
        }
        dismiss();
        if (listener != null) {
            listener.onClick(v, v);
        }
    }

//    @OnClick({R.id.tvSetting, R.id.tvVideoBookmarkList, R.id.llStudyMode, R.id.ll_clear_video_cache, R.id.ll_word_list,
//            R.id.tv_cancel, R.id.llEnableChangeTableWidth, R.id.llChoosePlayingScreenTheme })
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tv_cancel:
//                this.view = null;
//                break;
//            default:
//                this.view = view;
//                break;
//        }
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, view);
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }
}
