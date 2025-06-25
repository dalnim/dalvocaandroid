package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.databinding.DialogHanjaWordListBinding;
import com.dalread.util.UserUtil;

public class HanjaWordListDialog extends BaseDialog implements View.OnClickListener {
    private final OnClickListener listener;
    private DialogHanjaWordListBinding binding;
    private Context context;
    private boolean show4Buttons;
    private View getContentView() {
        binding = DialogHanjaWordListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public HanjaWordListDialog(@NonNull Context context, boolean show4Buttons, OnClickListener listener) {
        super(context, R.style.TransparentDialog);
        this.listener = listener;
        this.context = context;
        this.show4Buttons = show4Buttons;

        setContentView(getContentView());
        setOnClickListeners();
        update4ButtonName(show4Buttons);
    }

    private void setOnClickListeners() {
        binding.tvShow4Buttons.setOnClickListener(this);
        binding.tvAllToKnown.setOnClickListener(this);
        binding.tvAllToUnknown.setOnClickListener(this);
        binding.tvBackToHome.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    public void update4ButtonName(boolean show4Buttons) {
        if (show4Buttons) {
            binding.tvShow4Buttons.setText(R.string.dialog_menu_hide_4_buttons);
        } else {
            binding.tvShow4Buttons.setText(R.string.dialog_menu_show_4_buttons);
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.tvAllToKnown:
            case R.id.tvAllToUnknown:
                if (!UserUtil.isLoggedIn(context, true)) {
                    return;
                }
                break;
        }
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
