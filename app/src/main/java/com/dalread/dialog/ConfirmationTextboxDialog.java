package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.util.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmationTextboxDialog extends BaseDialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btn_positive)
    TextView btnPositive;
    @BindView(R.id.btn_negative)
    TextView btnNegative;

    private final OnTextboxDialogClickListener listener;

    public ConfirmationTextboxDialog(@NonNull Context context, OnTextboxDialogClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_confirmation_textbox);
        ButterKnife.bind(this);
    }

    public ConfirmationTextboxDialog(@NonNull Context context, int title, int msg, int positiveText, int negativeText, OnTextboxDialogClickListener listener) {
        this(context, listener);

        setMyTitle(title);
        setMessage(msg);
        setPositiveText(positiveText);
        setNegativeText(negativeText);
    }
//    public ConfirmationTextboxDialog(@NonNull Context context, OnTextboxDialogClickListener listener) {
//        super(context, R.style.TransparentDialog);
//        this.listener = listener;
//
//        setContentView(R.layout.dialog_confirmation_textbox);
//        ButterKnife.bind(this);
//    }

    public void setMyTitle(CharSequence text) {
        tvTitle.setText(text);
        tvTitle.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    public void setMyTitle(int text) {
        setMyTitle(text == 0 ? Constant.BASE_BLANK : getContext().getText(text));
    }

    public void setMessage(String text) {
        tvMessage.setText(text);
    }

    public void setMessage(int text) {
        tvMessage.setText(text);
    }

    public void setPositiveText(int text) {
        btnPositive.setText(text);
    }

    public void setNegativeText(int text) {
        btnNegative.setText(text);
    }

    @OnClick({R.id.btn_positive, R.id.btn_negative})
    void onClick(View view) {
        if (view.getId() == R.id.btn_positive) {
            if (listener != null) {
                String password = etPassword.getText().toString();
                listener.onPositive(this, password);
            }
        } else {
            if (listener != null) {
                listener.onNegative(this);
            }
        }
    }

    public interface OnTextboxDialogClickListener {
        void onPositive(DialogInterface dialog, String password);

        void onNegative(DialogInterface dialog);
    }
}
