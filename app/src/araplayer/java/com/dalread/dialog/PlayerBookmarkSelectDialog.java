package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerBookmarkSelectBinding;

public class PlayerBookmarkSelectDialog extends BasePlayerDialog implements View.OnClickListener{
    private OnClickListener listener;

    private DialogPlayerBookmarkSelectBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerBookmarkSelectBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_bookmark_select;
//    }

    public PlayerBookmarkSelectDialog(@NonNull Context context, OnClickListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void initOnClickListener() {
        binding.tvAll.setOnClickListener(this);
        binding.tvNone.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, v.getId());
        }
    }

//    @OnClick({R.id.tvAll, R.id.tvNone, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
