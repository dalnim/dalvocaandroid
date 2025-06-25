package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogHanjaMainViewMenuBinding;
import com.dalread.util.UserUtil;

public class HanjaMainMenuDialog extends BaseDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;
    private MenuDialogHanjaMainViewMenuBinding binding;
    private Context context;
    private SharedPreferencesDB sharedPreferencesDB;
    private View getContentView() {
        binding = MenuDialogHanjaMainViewMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public HanjaMainMenuDialog(@NonNull Context context, boolean isMainBottomTab, SharedPreferencesDB sharedPreferences, com.dalread.listener.OnClickListener listener) {
        super(context);
        setContentView(getContentView());
        setOnClickListeners();
        this.context = context;
        this.sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        this.listener = listener;
        hideMenusOnReleaseMode();
        hideMenusOnMainBottomTab(isMainBottomTab);
    }

    private void hideMenusOnReleaseMode() {
        boolean isSyncKnownWithServer = sharedPreferencesDB.getSyncKnownWithServer();
        if (UserUtil.isLoggedIn(context) && (isSyncKnownWithServer == false)) {
            binding.llSyncKnownWithServer.setVisibility(View.VISIBLE);
        } else {
            binding.llSyncKnownWithServer.setVisibility(View.GONE);
        }

        if (UserUtil.isEditContentUser(context)) {
            binding.llAddSentence.setVisibility(View.VISIBLE);
        } else {
            binding.llAddSentence.setVisibility(View.GONE);
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
        binding.llAddSentence.setOnClickListener(this);
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
