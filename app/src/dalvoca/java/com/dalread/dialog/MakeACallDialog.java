package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeACallDialog extends BaseDialog {

    private final com.dalread.listener.OnClickListener listener;

    public MakeACallDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_make_a_call);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvMakeCall, R.id.tvJoinCall, R.id.tvCancel})
    void onClick(View view) {
        if (view.getId() != R.id.tvCancel && listener != null) {
            listener.onClick(view, null);
        }
        dismiss();
    }
}
