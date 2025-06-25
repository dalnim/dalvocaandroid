package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerDownloadClearBinding;

public class PlayerDownloadClearDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickListener listener;
    private DialogPlayerDownloadClearBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerDownloadClearBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_download_clear;
//    }

    public PlayerDownloadClearDialog(@NonNull Context context, OnClickListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void initOnClickListener() {
        binding.tvClearAll.setOnClickListener(this);
        binding.tvClearCompleted.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, v.getId());
        }
    }
//
//    @OnClick({R.id.tv_clear_all, R.id.tv_clear_completed, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
