package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.util.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SetToAllPhrasesDialog extends BaseDialog {

    @BindView(R.id.tv_blink)
    TextView tvBlink;
    @BindView(R.id.tv_asterisk)
    TextView tvAsterisk;

    private final OnVocaKnowChangeListener listener;

    public SetToAllPhrasesDialog(@NonNull Context context, OnVocaKnowChangeListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_set_to_all_phrases);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.v_known, R.id.v_exclude, R.id.v_blink, R.id.v_asterisk, R.id.tv_action, R.id.v_backToHome})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            int grade;
            int id = view.getId();
            switch (id) {
                case R.id.tv_action:
                    return;
                case R.id.v_exclude:
                    grade = Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
                    break;
                case R.id.v_blink:
                    grade = Constant.AMKI_GRADE.VALUE_BLINK;
                    break;
                case R.id.v_asterisk:
                    grade = Constant.AMKI_GRADE.VALUE_ASTERISK;
                    break;
                case R.id.v_backToHome:
                    grade = Constant.AMKI_GRADE.VALUE_BACK_TO_HOME;
                    break;
                default:
                    grade = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
                    break;
            }
            listener.onVocaKnowChange(grade);
        }
    }

    public void setBlinkMode(boolean isBlinkMode) {
        tvBlink.setText(isBlinkMode ? R.string.normal_mode : R.string.blink_mode);
    }

    public void setAsteriskMode(boolean isAsteriskMode) {
        tvAsterisk.setText(isAsteriskMode ? R.string.restore_sentence_from_star : R.string.display_sentence_as_star);
    }

    public interface OnVocaKnowChangeListener {

        void onVocaKnowChange(int vocaKnow);
    }
}
