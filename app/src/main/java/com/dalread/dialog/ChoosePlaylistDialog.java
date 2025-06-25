package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoosePlaylistDialog extends BaseDialog {

    private final OnClickListener listener;

    public ChoosePlaylistDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_choose_playlist);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_all, R.id.tv_unknown, R.id.tv_none, R.id.tv_range, R.id.tv_last, R.id.tv_cancel})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
