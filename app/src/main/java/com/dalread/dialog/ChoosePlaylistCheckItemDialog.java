package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoosePlaylistCheckItemDialog extends BaseDialog {

    private final OnClickListener listener;

    public ChoosePlaylistCheckItemDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_choose_playlist_check_item);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvCheckAll, R.id.tvCheckNone, R.id.tvCheckLastChosen, R.id.tvCancel})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
