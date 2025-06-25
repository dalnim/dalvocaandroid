package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogHanjaCommonBinding;
import com.dalread.util.Utils;

public class HanjaCommonMenuDialog extends BaseDialog implements View.OnClickListener {

    private final OnClickListener listener;
    private MenuDialogHanjaCommonBinding binding;
    private SharedPreferencesDB sharedPreferences;

    private View getContentView() {
        binding = MenuDialogHanjaCommonBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public HanjaCommonMenuDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);
        setContentView(getContentView());
        setOnClickListeners();
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        this.listener = listener;
        showPracticeMenu(false);
        showRefreshVocaFromServerMenu(false);
        showOpenHanjaExcludeOptionDialogMenu(true);
    }

    public void showPracticeMenu(boolean isShow) {
        if (isShow) {
            binding.llWritingPractice.setVisibility(View.VISIBLE);
        } else {
            binding.llWritingPractice.setVisibility(View.GONE);
        }
    }

    public void showRefreshVocaFromServerMenu(boolean isShow) {
        if ((isShow) && (Utils.isDebugOrAdminUser(getContext()))) {
            binding.llRefreshVocaFromServer.setVisibility(View.VISIBLE);
        } else {
            binding.llRefreshVocaFromServer.setVisibility(View.GONE);
        }
    }

    public void showOpenHanjaExcludeOptionDialogMenu(boolean isShow) {
        if ((isShow) && (Utils.isDebugOrAdminUser(getContext()))) {
            binding.llOpenHanjaExcludeOptionDialog.setVisibility(View.VISIBLE);
        } else {
            binding.llOpenHanjaExcludeOptionDialog.setVisibility(View.GONE);
        }
    }



    private void setOnClickListeners() {
        binding.llWritingPractice.setOnClickListener(this);
        binding.tvBackToHome.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.llOpenHanjaExcludeOptionDialog.setOnClickListener(this);
        binding.llRefreshVocaFromServer.setOnClickListener(this);
        binding.llOpenHanjaExcludeOptionDialog.setOnClickListener(this);
        binding.llOpenNormalTextViewForHurigana.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
