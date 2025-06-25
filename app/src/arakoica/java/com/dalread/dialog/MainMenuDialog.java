package com.dalread.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogMainViewBinding;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

public class MainMenuDialog extends BaseDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;
    private MenuDialogMainViewBinding binding;
    private Context context;
    private boolean blnShowIctTerms;
    private boolean blnShowLocalTerms;
    private boolean isEnableRefreshIctTermList;
    private SharedPreferencesDB sharedPreferences;
    private View getContentView() {
        binding = MenuDialogMainViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public MainMenuDialog(@NonNull Context context, boolean isEnableRefreshIctTermList, boolean isMainBottomTab, SharedPreferencesDB sharedPreferences, com.dalread.listener.OnClickListener listener) {
        super(context);
        setContentView(getContentView());
        setOnClickListeners();
        this.context = context;
        this.listener = listener;
        this.isEnableRefreshIctTermList = isEnableRefreshIctTermList;
        this.sharedPreferences = sharedPreferences;
        udpateOptionValue();
        hideMenusOnReleaseMode();
        hideMenusOnMainBottomTab(isMainBottomTab);
    }

    public void udpateOptionValue() {
        blnShowIctTerms = sharedPreferences.getShowIctTerms();
        binding.scShowIctTerm.setChecked(blnShowIctTerms);

        blnShowLocalTerms = sharedPreferences.getShowLocalTerms();
        binding.scShowLocalTerm.setChecked(blnShowLocalTerms);
    }

    private void setBlnShowIctTerms(boolean value) {
        sharedPreferences.setShowIctTerms(value);
    }

    private void setBlnShowLocalTerms(boolean value) {
        sharedPreferences.setShowLocalTerms(value);
    }

    private void hideMenusOnReleaseMode() {
        if (Utils.isDebug() || UserUtil.isEditContentUser(context)) {
            binding.llAddTerm.setVisibility(View.VISIBLE);
        } else {
            binding.llAddTerm.setVisibility(View.GONE);
        }
        binding.llRefreshIctTermListAfterTime.setVisibility(isEnableRefreshIctTermList ? View.VISIBLE : View.GONE);
    }

    private void hideMenusOnMainBottomTab(boolean isMainBottomTab) {
        if (isMainBottomTab) {
            hideMenusOnReleaseMode();
            showBackToHomeMenu(false);
        } else {

            showBackToHomeMenu(true);
        }
    }
    private void showBackToHomeMenu(boolean isShow) {
        binding.llBackToHome.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void setOnClickListeners() {
        binding.llRefreshIctTermListAfterTime.setOnClickListener(this);
        binding.llAddTerm.setOnClickListener(this);
        binding.llBackToHome.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.llShowIctTerm.setOnClickListener(this);
        binding.scShowIctTerm.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.llShowLocalTerm.setOnClickListener(this);
        binding.scShowLocalTerm.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(view, view.getId());
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            switch (id) {
                case R.id.scShowIctTerm:
                    if (blnShowIctTerms != isChecked) {
                        blnShowIctTerms = isChecked;
                        setBlnShowIctTerms(blnShowIctTerms);
                        if (listener != null) {
                            listener.onClick(buttonView, buttonView.getId());
                        }
                    }
                    break;
                case R.id.scShowLocalTerm:
                    if (blnShowLocalTerms != isChecked) {
                        blnShowLocalTerms = isChecked;
                        setBlnShowLocalTerms(blnShowLocalTerms);
                        if (listener != null) {
                            listener.onClick(buttonView, buttonView.getId());
                        }
                    }
                    break;
            }
        }
    };
}
