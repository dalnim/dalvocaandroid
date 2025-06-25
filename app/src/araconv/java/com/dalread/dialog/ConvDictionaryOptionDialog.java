package com.dalread.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogDictionaryOptionBinding;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class ConvDictionaryOptionDialog extends BaseDialog implements View.OnClickListener{
    private OnClickListener listener;
    private SharedPreferencesDB sharedPreferences;
    private boolean isSearchInStudyLang;
    private DialogDictionaryOptionBinding binding;

    private View getContentView() {
        binding = DialogDictionaryOptionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public ConvDictionaryOptionDialog(@NonNull Context context) {
        super(context);
//        this.listener = listener;
        setContentView(getContentView());
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        ButterKnife.bind(this, binding.getRoot());
        setOnClickListeners();
        udpateOptionValue();
    }

    private void setOnClickListeners() {
        binding.tvOk.setOnClickListener(this);
    }

    public void udpateOptionValue() {
        isSearchInStudyLang = sharedPreferences.getSearchInStudyLang();
        binding.scSearchInStudyLang.setChecked(isSearchInStudyLang);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(null, view.getId());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @OnCheckedChanged({
            R.id.scSearchInStudyLang
    })
    void onCheckedChanged(CompoundButton button, boolean checked) {
        int id = button.getId();
        switch (id) {
            case R.id.scSearchInStudyLang:
                sharedPreferences.setSearchInStudyLang(isSearchInStudyLang = checked);
                break;

        }
    }

}
