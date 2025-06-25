package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogConvMainViewMenuBinding;
import com.dalread.util.UserUtil;

public class ConvMainMenuDialog extends BaseDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;
    private MenuDialogConvMainViewMenuBinding binding;
    private Context context;
    private View getContentView() {
        binding = MenuDialogConvMainViewMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public ConvMainMenuDialog(@NonNull Context context, boolean isMainBottomTab, SharedPreferencesDB sharedPreferences, com.dalread.listener.OnClickListener listener) {
        super(context);
        setContentView(getContentView());
        setOnClickListeners();
        this.context = context;
        this.listener = listener;
        hideMenusOnReleaseMode();
        hideMenusOnMainBottomTab(isMainBottomTab);
    }

    private void hideMenusOnReleaseMode() {
        if (UserUtil.isLoggedIn(context)) {
            binding.llSyncKnownWithServer.setVisibility(View.VISIBLE);
        } else {
            binding.llSyncKnownWithServer.setVisibility(View.GONE);
        }
    }

    private void hideMenusOnMainBottomTab(boolean isMainBottomTab) {
        if (isMainBottomTab) {
            hideMenusOnReleaseMode();
            showBackToHomeMenu(false);
        } else {
            binding.llSyncKnownWithServer.setVisibility(View.GONE);
            showBackToHomeMenu(true);
        }
    }
    private void showBackToHomeMenu(boolean isShow) {
        binding.llBackToHome.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void setOnClickListeners() {
        binding.llSyncKnownWithServer.setOnClickListener(this);
        binding.llBackToHome.setOnClickListener(this);
        binding.llRefreshVoiceRecordingFileList.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.tvCancel:
                break;
            default:
                if (listener != null) {
                    listener.onClick(view, null);
                }
                break;
        }


    }
}
