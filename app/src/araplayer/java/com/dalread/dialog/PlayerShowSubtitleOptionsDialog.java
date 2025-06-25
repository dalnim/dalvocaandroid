package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowSubtitleOptionsBinding;

public class PlayerShowSubtitleOptionsDialog extends BasePlayerDialog implements View.OnClickListener {
    private Context context;
    private OnClickListener listener;
    private DialogPlayerShowSubtitleOptionsBinding binding;

//    @Override
    protected void initOnClickListener() {
        binding.tvSelectSubtitle.setOnClickListener(this);
        binding.tvDownloadSubtitle.setOnClickListener(this);
        binding.tvDetachSubtitle.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSubtitleOptionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerShowSubtitleOptionsDialog(@NonNull Context context, OnClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        initOnClickListener();
    }

    public void updateVisibilityDetachSubtitle(boolean isShow) {
        binding.tvDetachSubtitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, v.getId());
        }
    }

//    @OnClick({R.id.tv_select_subtitle, R.id.tvDownloadSubtitle, R.id.tv_detach_subtitle, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
