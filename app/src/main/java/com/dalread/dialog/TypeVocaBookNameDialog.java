package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class TypeVocaBookNameDialog extends BaseDialog {

    @BindView(R.id.et_name)
    EditText etName;

    public TypeVocaBookNameDialog(Context context, BaseDialogListener listener) {
        super(context);

        setBaseDialogListener(listener);
        setContentView(R.layout.dialog_wordbook_name);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_ok)
    public void onClickOk(View view) {
        if (baseDialogListener == null) {
            onClickCancel();
        } else {
            getOkAction(view);
        }
    }

    protected void getOkAction(View view) {
        baseDialogListener.onBaseDialogListenerOk(enumType, this, view, 0, etName.getText().toString());
    }

    @OnClick(R.id.btn_cancel)
    public void onClickCancel() {
        dismiss();
        clearText();
    }

    public void clearText() {
        etName.setText("");
    }

    @Override
    public void show() {
        super.show();

        etName.post(() -> Utils.showSoftKeyboard(getContext(), etName));
    }
}
