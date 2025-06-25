package com.dalread.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaActivity;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

@SuppressLint("NonConstantResourceId")
public class UnicodeActivity extends BaseVocaActivity {

    @BindView(R.id.et_unicode)
    EditText etUnicode;
    @BindView(R.id.tv_unicode)
    TextView tvUnicode;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_unicode;
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {
    }

    @Override
    public void onHeaderIconRightClick() {
    }

    @Override
    public void onHeaderTextRightClick() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvUnicode.setMovementMethod(new ScrollingMovementMethod());
    }

    @OnEditorAction(R.id.et_unicode)
    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            showUnicode();
        }
        return false;
    }

    @OnClick(R.id.btn_show_unicode)
    void onClick(View v) {
        showUnicode();
    }

    private void showUnicode() {
        String input = etUnicode.getText().toString();
        if (input.isEmpty()) return;

        StringBuilder output = new StringBuilder();

        String[] split = input.split("(?!^)");
        for (String s : split) {
            int codePoint = s.codePointAt(0);
            output.append(s)
                    .append(" | U+").append(Integer.toString(codePoint, 16).toUpperCase())
                    .append(" | ").append(codePoint)
                    .append("\n");
        }

        tvUnicode.setText(output.toString());
    }
}
