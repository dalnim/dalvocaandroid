package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.databinding.MenuDialogPracticeConversationBinding;
import com.dalread.util.UserUtil;

public class PracticeConversationMenuDialog extends BaseDialog implements View.OnClickListener {
    private final com.dalread.listener.OnClickListener listener;
    private MenuDialogPracticeConversationBinding binding;
    private Context context;

    private View getContentView() {
        binding = MenuDialogPracticeConversationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PracticeConversationMenuDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context, R.style.TransparentDialog);
        this.listener = listener;
        this.context = context;

        setContentView(getContentView());
        showOrHideMenus();
        setOnClickListeners();
    }
    protected void showOrHideMenus() {
        if (!UserUtil.isDebugOrAdminUser(context)) {
            binding.llShowPlayAllWords.setVisibility(View.GONE);
        }
    }
    private void setOnClickListeners() {
        binding.llOption.setOnClickListener(this);
        binding.llShowPlayAllWords.setOnClickListener(this);
        binding.llOpenSetting.setOnClickListener(this);
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
