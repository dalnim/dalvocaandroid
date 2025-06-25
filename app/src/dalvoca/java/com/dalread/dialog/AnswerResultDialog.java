package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class AnswerResultDialog extends BaseDialog {

    @BindView(R.id.ic_result)
    ImageView imgResult;

    public AnswerResultDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialog);

        setContentView(R.layout.dialog_answer_result);
        ButterKnife.bind(this);
    }

    public void showCorrect() {
        imgResult.setImageResource(R.drawable.ic_answer_correct);
        show();
    }

    public void showWrong() {
        imgResult.setImageResource(R.drawable.ic_answer_wrong);
        show();
    }

    @OnClick(R.id.v_container)
    void onClick() {
        dismiss();
    }
}
