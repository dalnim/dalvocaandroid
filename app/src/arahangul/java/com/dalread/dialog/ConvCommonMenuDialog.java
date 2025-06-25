package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogConvCommonBinding;

public class ConvCommonMenuDialog extends BaseDialog implements View.OnClickListener {

    private final com.dalread.listener.OnClickListener listener;
    private MenuDialogConvCommonBinding binding;
    private Context context;
    private boolean show4Buttons;
    private SharedPreferencesDB sharedPreferences;

    private View getContentView() {
        binding = MenuDialogConvCommonBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public ConvCommonMenuDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context, R.style.TransparentDialog);
        this.listener = listener;
        this.context = context;

        setContentView(getContentView());
        setOnClickListeners();
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        update4ButtonName(show4Buttons);
    }

    public void setShow4Buttons(boolean show4Buttons) {
        this.show4Buttons = show4Buttons;
        update4ButtonName(show4Buttons);
    }

    private void update4ButtonName(boolean show4Buttons) {
        if (show4Buttons) {
            binding.tvShow4Buttons.setText(R.string.dialog_menu_hide_4_buttons);
        } else {
            binding.tvShow4Buttons.setText(R.string.dialog_menu_show_4_buttons);
        }
    }



    private void setOnClickListeners() {
        binding.llShow4Buttons.setOnClickListener(this);
        binding.llBackToHome.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.llOpenSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(view, null);
        }
    }
}
