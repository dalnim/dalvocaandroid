package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerCopySubtitleBinding;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.FileUtil;

public class PlayerCopySubtitleDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickListener listener;
    private DialogPlayerCopySubtitleBinding binding;
    private PlayerFileModel playerFileModel;

    @Override
    protected View getContentView() {
        binding = DialogPlayerCopySubtitleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_copy_subtitle;
//    }

    public PlayerCopySubtitleDialog(@NonNull Context context, PlayerFileModel playerFileModel, OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.playerFileModel = playerFileModel;

        if (FileUtil.isAudioFormat(playerFileModel.getVideoModel().getName())) {
            binding.tvExportSrtFile.setText(R.string.export_as_a_lrc_file);
        } else {
            binding.tvExportSrtFile.setText(R.string.export_as_a_srt_file);
        }
    }

    @Override
    protected void initOnClickListener() {
        binding.tvCopy.setOnClickListener(this);
        binding.tvExportSrtFile.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, v.getId());
        }
    }

//    @OnClick({R.id.tv_copy, R.id.tv_export_srt_file, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
