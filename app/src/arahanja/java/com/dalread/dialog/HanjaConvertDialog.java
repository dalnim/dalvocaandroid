package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogHanjaConvertBinding;

public class HanjaConvertDialog extends BaseDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;

    private MenuDialogHanjaConvertBinding binding;

    private View getContentView() {
        binding = MenuDialogHanjaConvertBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public HanjaConvertDialog(@NonNull Context context, SharedPreferencesDB sharedPreferences, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        setContentView(getContentView());
        setOnClickListeners();

        this.listener = listener;
    }

    private void setOnClickListeners() {
        binding.llChooseHanja.setOnClickListener(this);
        binding.llOpenInfoView.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(view, null);
        }
    }
}
