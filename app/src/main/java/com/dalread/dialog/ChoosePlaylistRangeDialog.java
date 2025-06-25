package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoosePlaylistRangeDialog extends BaseDialog {

    private final OnClickListener listener;

    public ChoosePlaylistRangeDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_choose_playlist_range);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvSelectAll, R.id.tvSelectFirstWords, R.id.tvSelectSecondWords, R.id.tvSelectOnlyUnknownWords, R.id.tvSelectUnknownAndLess, R.id.tvSelectBookmarked, R.id.tvSelectByRange, R.id.tvSelectByRandom, R.id.tvCancel})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
