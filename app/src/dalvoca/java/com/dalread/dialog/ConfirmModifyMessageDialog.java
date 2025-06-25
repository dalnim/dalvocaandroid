package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmModifyMessageDialog extends BaseDialog {

    private final com.dalread.listener.OnClickListener listener;
    private Object object;

    public ConfirmModifyMessageDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_confirm_modify_message);
        ButterKnife.bind(this);
    }

    @Override
    public void show() {
        super.show();

        object = null;
    }

    public void show(Object object) {
        show();
        this.object = object;
    }

    @OnClick({R.id.tv_yes, R.id.tv_no, R.id.tv_cancel})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(view, object);
        }
    }
}
