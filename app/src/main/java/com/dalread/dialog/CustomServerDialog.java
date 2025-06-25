package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class CustomServerDialog extends BaseDialog {

    private final OnCustomServerClickListener listener;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_url)
    EditText etUrl;

    public CustomServerDialog(Context context, OnCustomServerClickListener listener, String name) {
        super(context);

        this.listener = listener;

        setContentView(R.layout.dialog_custom_server);
        ButterKnife.bind(this);
        initView(name);
    }

    private void initView(String name) {
        this.tvTitle.setText(name);
        this.etUrl.setText(mSharedPref.getBaseCustomUrl());
    }

    @OnClick({R.id.btn_ok, R.id.btn_cancel})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                handleOkButton();
                break;
        }
    }

    private void handleOkButton() {
        String url = etUrl.getText().toString().trim();
        if (Utils.isEmpty(url)) {
            return;
        }
        if (!url.endsWith("/")) {
            url += "/";
        }
        if (!URLUtil.isValidUrl(url)) return;

        dismiss();

        if (listener != null) {
            listener.onClick(url);
        }
    }

    public interface OnCustomServerClickListener {
        void onClick(String url);
    }
}
