package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HanjaInfoDialog extends BaseDialog {

    private final OnClickListener listener;

    public HanjaInfoDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_hanja_info);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_practice, R.id.tvBackToHome, R.id.tv_cancel})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
