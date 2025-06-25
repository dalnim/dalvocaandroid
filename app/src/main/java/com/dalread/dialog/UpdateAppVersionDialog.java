package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class UpdateAppVersionDialog extends BaseDialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.btn_positive)
    TextView btnPositive;
    @BindView(R.id.btn_negative)
    TextView btnNegative;

    private final ConfirmationDialog.OnDialogClickListener listener;

    public UpdateAppVersionDialog(@NonNull Context context, ConfirmationDialog.OnDialogClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_update_app_version);
        ButterKnife.bind(this);
    }

    public UpdateAppVersionDialog(@NonNull Context context, int title, int msg, int positiveText, int negativeText, ConfirmationDialog.OnDialogClickListener listener) {
        this(context, listener);

        setMyTitle(title);
        setMessage(msg);
        setPositiveText(positiveText);
        setNegativeText(negativeText);
    }

    public void setMyTitle(CharSequence text) {
        tvTitle.setText(text);
        tvTitle.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    public void setMyTitle(int text) {
        setMyTitle(getContext().getText(text));
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
                listener.onPositive(this);
            }
        } else {
            if (listener != null) {
                listener.onNegative(this);
            }
        }
        dismiss();
    }
}
