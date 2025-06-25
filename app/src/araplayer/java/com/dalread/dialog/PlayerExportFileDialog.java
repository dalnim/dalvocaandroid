package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerExportFileBinding;
import com.dalread.util.EditTextUtils;
import com.dalread.util.Utils;

public class PlayerExportFileDialog extends BasePlayerDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;

    private DialogPlayerExportFileBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerExportFileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerExportFileDialog(@NonNull Context context, int title, int description, String fileName, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        initListener();
        binding.tvTitle.setText(context.getString(title));
        binding.tvDescription.setText(context.getString(description));
        binding.etName.setText(fileName);
        binding.etName.setSelection(binding.etName.getText().length());
        binding.etName.selectAll();

    }
    private void initListener() {
        binding.etName.addTextChangedListener(EditTextUtils.createTextWatcher(binding.etName));
    }
    public PlayerExportFileDialog(@NonNull Context context, int title, String description, String fileName, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        binding.tvTitle.setText(context.getString(title));
        binding.tvDescription.setText(description);
        binding.etName.setText(fileName);
        binding.etName.setSelection(binding.etName.getText().length());
        binding.etName.selectAll();
    }

    @Override
    protected void initOnClickListener() {
        binding.btnOk.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            Utils.hideSoftKeyboard(getContext());
            dismiss();
        } else {
            final String name = binding.etName.getText().toString();
            if (Utils.isEmpty(name.trim())) {
                return;
            }
            dismiss();
            if (listener != null) {
                listener.onClick(v, name);
            }
        }
    }


//    @OnClick(R.id.btn_cancel)
//    void onCancelClick() {
//        Utils.hideSoftKeyboard(getContext());
//        dismiss();
//    }
//
//    @OnClick(R.id.btn_ok)
//    void onOkClick(View v) {
//        final String name = binding.etName.getText().toString();
//        if (Utils.isEmpty(name.trim())) {
//            return;
//        }
//        dismiss();
//        if (listener != null) {
//            listener.onClick(v, name);
//        }
//    }
}
