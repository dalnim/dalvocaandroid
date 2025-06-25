package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.base.BaseDialogListener;
import com.dalread.util.Voca;

import java.util.ArrayList;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class TypeFeedbackDialog extends TypeVocaBookNameDialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;

    private Object voca;
    private String evaluateGrade;

    public TypeFeedbackDialog(Context context, BaseDialogListener listener) {
        super(context, listener);

        tvTitle.setText(R.string.feedback_to_student);
        etName.setHint(R.string.type_your_opinion);
    }

    @Override
    protected void getOkAction(View view) {
        dismiss();
        ArrayList<Object> data = new ArrayList<>();
        data.add(voca);
        data.add(evaluateGrade);
        data.add(etName.getText().toString());
        baseDialogListener.onBaseDialogListenerOk(enumType, this, view, 0, data);
    }

    public void show(Object voca, String evaluateGrade) {
        this.voca = voca;
        this.evaluateGrade = evaluateGrade;

        String tplEvaluate = "%1$s: %2$s";
        String evaluateText = String.format(tplEvaluate, evaluateGrade, Voca.getEvaluateText(getContext(), evaluateGrade));
        tvSubTitle.setText(evaluateText);

        etName.setText("");

        super.show();
    }
}
