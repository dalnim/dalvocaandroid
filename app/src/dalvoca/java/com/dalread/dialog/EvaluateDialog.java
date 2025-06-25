package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.util.Constant;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EvaluateDialog extends BaseDialog {

    private final OnEvaluateClickListener listener;
    private Object voca;

    public EvaluateDialog(@NonNull Context context, OnEvaluateClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_evaluate);
        ButterKnife.bind(this);
    }

    public void show(Object voca) {
        this.voca = voca;
        super.show();
    }

    @OnClick({R.id.v_a, R.id.v_b, R.id.v_c, R.id.tv_action})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            int id = view.getId();
            if (id == R.id.v_a) {
                listener.onClick(voca, Constant.EVALUATE.GRADE_A);
            } else if (id == R.id.v_b) {
                listener.onClick(voca, Constant.EVALUATE.GRADE_B);
            } else if (id == R.id.v_c) {
                listener.onClick(voca, Constant.EVALUATE.GRADE_C);
            }
        }
    }

    public interface OnEvaluateClickListener {

        void onClick(Object voca, String evaluateGrade);
    }
}
