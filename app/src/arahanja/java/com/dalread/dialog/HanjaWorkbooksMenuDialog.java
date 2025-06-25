package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BaseDialog;
import com.dalread.databinding.DialogHanjaWorkbooksMenuBinding;

public class HanjaWorkbooksMenuDialog extends BaseDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;
    private DialogHanjaWorkbooksMenuBinding binding;

    private View getContentView() {
        binding = DialogHanjaWorkbooksMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }
    public HanjaWorkbooksMenuDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        setContentView(getContentView());
        setOnClickListeners();
        this.listener = listener;
        binding.llShowKnownWordCount.setVisibility(View.GONE);

    }

    private void setOnClickListeners() {
        binding.llShowKnownWordCount.setOnClickListener(this);
        binding.llBackToHome.setOnClickListener(this);
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
