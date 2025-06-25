package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BaseDialog;
import com.dalread.databinding.MenuDialogConvertToHanjaBinding;
import com.dalread.util.Utils;

public class ConvertToHanjaMenuDialog extends BaseDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;
    private MenuDialogConvertToHanjaBinding binding;
    private Context context;
    private View getContentView() {
        binding = MenuDialogConvertToHanjaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public ConvertToHanjaMenuDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.context = context;
        setContentView(getContentView());
        setOnClickListeners();
        hideMenusOnReleaseMode();

    }

    private void setOnClickListeners() {
        binding.llAddSentence.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    private void hideMenusOnReleaseMode() {
        if (Utils.isAdminUser(context)) {
            binding.llAddSentence.setVisibility(View.VISIBLE);
        } else {
            binding.llAddSentence.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(view, null);
        }
    }
}
