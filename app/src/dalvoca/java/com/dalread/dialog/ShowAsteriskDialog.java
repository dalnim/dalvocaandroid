package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ShowAsteriskDialog extends BaseDialog {

    @BindView(R.id.tv_display_sentence_as_asterisk)
    TextView tvDisplaySentenceAsStar;

    private final OnClickListener listener;

    public ShowAsteriskDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_show_asterisk);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_display_sentence_as_asterisk, R.id.tv_hide_known_and_excellent_sentences, R.id.tv_hide_known_sentences, R.id.tv_hide_excellent_sentences,
            R.id.tv_show_all_sentences, R.id.tv_make_role_playing_contents, R.id.tv_change_to_exam_mode, R.id.tv_cancel})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }

    public void setShowAsterisk(boolean showAsterisk) {
        tvDisplaySentenceAsStar.setText(showAsterisk ? R.string.restore_sentence_from_star : R.string.display_sentence_as_star);
    }
}
