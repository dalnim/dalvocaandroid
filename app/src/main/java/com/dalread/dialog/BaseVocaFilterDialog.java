package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogBaseVocaFilterBinding;

public class BaseVocaFilterDialog extends BaseDialog implements View.OnClickListener {

    protected final com.dalread.listener.OnClickListener listener;
    protected MenuDialogBaseVocaFilterBinding binding;
    protected Context context;
    protected boolean show4Buttons;
    protected SharedPreferencesDB sharedPreferences;
    private View getContentView() {
        binding = MenuDialogBaseVocaFilterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public BaseVocaFilterDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context, R.style.TransparentDialog);
        this.listener = listener;
        this.context = context;

        setContentView(getContentView());
        setOnClickListeners();
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        showOrHideMenus();
    }
    protected void showOrHideMenus() {

    }

    private void setOnClickListeners() {
        binding.llShowAllVocas.setOnClickListener(this);
        binding.llFilterByVocaKnow.setOnClickListener(this);
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
