package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LessonModeDialog extends BaseDialog {

    private final OnClickListener listener;

    public LessonModeDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_lesson_mode);
        ButterKnife.bind(this);
    }

    @OnClick({
            R.id.tv_normal_lesson_mode, R.id.tv_exam_mode,
            R.id.tv_role_playing_mode, R.id.tv_grammar_mode,
            R.id.tv_reading_mode, R.id.tv_review_mode,
            R.id.tv_cancel
    })
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
