package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.databinding.DialogHanjaWordGridGroupBinding;

public class HanjaWordGridGroupDialog extends BaseDialog implements View.OnClickListener {
    private final OnClickListener listener;
    private boolean isExpandAll;
    private DialogHanjaWordGridGroupBinding binding;

    private View getContentView() {
        binding = DialogHanjaWordGridGroupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public HanjaWordGridGroupDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);
        setContentView(getContentView());
        setOnClickListeners();

        this.listener = listener;
    }

    private void setOnClickListeners() {
        binding.tvExpandCollapseAll.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    public void updateExpandAllMenuName(boolean isExpandAll) {
        if (isExpandAll) {
            binding.tvExpandCollapseAll.setText("Collapse all");
        } else {
            binding.tvExpandCollapseAll.setText("Expand all");
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
