package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExamDialog extends BaseDialog {

    private final OnClickListener listener;

    public ExamDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_exam);
        ButterKnife.bind(this);
    }

    @OnClick({
            R.id.tv_from_all_phrases, R.id.tv_from_memorization_targets,
            R.id.tv_except_known_and_excellent, R.id.tv_except_known,
            R.id.tv_except_excellent, R.id.tv_shuffle_this_exam,
            R.id.tv_cancel
    })
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
