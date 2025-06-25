package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerBookmarkRepeatBinding;

public class PlayerBookmarkRepeatDialog extends BasePlayerDialog implements View.OnClickListener{

    private OnClickListener listener;

    private DialogPlayerBookmarkRepeatBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerBookmarkRepeatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerBookmarkRepeatDialog(@NonNull Context context, OnClickListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void initOnClickListener() {
        binding.tv1Time.setOnClickListener(this);
        binding.tv3Time.setOnClickListener(this);
        binding.tv5Time.setOnClickListener(this);
        binding.tv10Time.setOnClickListener(this);
        binding.tv20Time.setOnClickListener(this);
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
//    @OnClick({R.id.tv1Time, R.id.tv3Time, R.id.tv5Time, R.id.tv10Time, R.id.tv20Time, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
