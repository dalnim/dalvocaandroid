package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.dalread.R;
import com.dalread.activity.SignInUpActivity;
import com.dalread.base.BaseDialog;
import com.dalread.util.ToastUtil;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class AlertDialog extends BaseDialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_action)
    TextView tvAction;

    @BindString(R.string.msg_no_internet)
    String msgNoInternet;
    @BindString(R.string.msg_log_in_required)
    String msgLogInRequired;
    @BindString(R.string.ok)
    String msgOK;

    private OnClickListener listener;

    public AlertDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialog);

        setContentView(R.layout.dialog_alert);
        ButterKnife.bind(this);

        tvMessage.setMovementMethod(new ScrollingMovementMethod());
    }

    public void show(int title, @StringRes int message, @StringRes int action, OnClickListener listener) {
        this.listener = listener;

        if (title == 0) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }
        tvMessage.scrollTo(0, 0);
        tvMessage.setText(message);
        if (action == 0) {
            tvAction.setText(msgOK);
        } else {
            tvAction.setText(action);
        }
        show();
    }

    public void show(@StringRes int message, @StringRes int action, OnClickListener listener) {
        show(0, message, action, listener);
    }

    public void show(String title, String message, String action, OnClickListener listener) {
        this.listener = listener;

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }
        tvMessage.scrollTo(0, 0);
        tvMessage.setText(message);
        tvAction.setText(TextUtils.isEmpty(action) ? msgOK : action);
        show();
    }

    public void show(String message, String action, OnClickListener listener) {
        show("", message, action, listener);
    }

    @OnClick({R.id.tv_action, R.id.tv_message})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_action:
                if (listener == null) {
                    dismiss();
                } else {
                    listener.onClick(this, DialogInterface.BUTTON_NEUTRAL);
                    listener = null;
                }
                break;
            case R.id.tv_message:
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    String text = tvMessage.getText().toString();
                    clipboard.setPrimaryClip(ClipData.newPlainText("", text));
                    ToastUtil.getInstance(getContext()).show(R.string.copied);
                }
                break;
        }

    }

    public void showNoInternet() {
        show(msgNoInternet, null, null);
    }

    public void showLogInRequired() {
        show(msgLogInRequired, null, (dialog, which) -> {
            dialog.dismiss();
            mContext.startActivity(SignInUpActivity.createIntent(mContext));
//            mContext.startActivity(LoginActivity.createIntent(mContext));
        });
    }
    public void showLogInRequired(boolean isShowLoginView) {
        show(msgLogInRequired, null, (dialog, which) -> {
            dialog.dismiss();
            if (isShowLoginView)
                mContext.startActivity(SignInUpActivity.createIntent(mContext));
        });
    }
}
