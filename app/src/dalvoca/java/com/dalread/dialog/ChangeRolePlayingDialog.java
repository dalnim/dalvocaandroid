package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeRolePlayingDialog extends BaseDialog {

    private final OnClickListener listener;

    public ChangeRolePlayingDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_change_role_playing);
        ButterKnife.bind(this);
    }

    @OnClick({
            R.id.tv_change_subject, R.id.tv_study_words, R.id.tv_change_word_known_status,
            R.id.tv_refresh, R.id.tv_study_sentences, R.id.tv_cancel
    })
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
