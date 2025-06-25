package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerVideoFileNameBinding;

public class PlayerVideoFileNameDialog extends BasePlayerDialog implements View.OnClickListener {

    private com.dalread.listener.OnClickListener listener;
    private DialogPlayerVideoFileNameBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerVideoFileNameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerVideoFileNameDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void initOnClickListener() {
        binding.tvCopyName.setOnClickListener(this);
        binding.tvRename.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v, v.getId());
        }
    }

//    @OnClick({R.id.tv_copy_name, R.id.tv_rename, R.id.tv_cancel})
//    void onOkClick(View v) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(v, v.getId());
//        }
//    }
}
