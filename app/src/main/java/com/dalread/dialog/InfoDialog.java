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
public class InfoDialog extends BaseDialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.btn_positive)
    TextView btnPositive;

    private final View.OnClickListener listener;

    public InfoDialog(@NonNull Context context, View.OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_info);
        ButterKnife.bind(this);
    }

    public InfoDialog(@NonNull Context context, int title, int msg, int positiveText, View.OnClickListener listener) {
        this(context, listener);

        setMyTitle(title);
        setMessage(msg);
        setPositiveText(positiveText);
    }

    public InfoDialog(@NonNull Context context, String title, String msg, int positiveText, View.OnClickListener listener) {
        this(context, listener);

        setMyTitle(title);
        setMessage(msg);
        setPositiveText(positiveText);
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


    @OnClick({R.id.btn_positive})
    void onClick(View view) {
        if (view.getId() == R.id.btn_positive) {
            if (listener != null) {
                listener.onClick(view);
            }
        }
        dismiss();
    }
}
