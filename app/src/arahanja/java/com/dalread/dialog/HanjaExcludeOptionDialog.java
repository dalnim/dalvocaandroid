package com.dalread.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogHanjaExcludeOptionBinding;

import butterknife.ButterKnife;

public class HanjaExcludeOptionDialog extends BaseDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private OnClickListener listener;
    private SharedPreferencesDB sharedPreferences;
    private boolean isShowSimplifiedChineseInWorkBooks;
    private boolean isShowBeyondLevelHanja;

    private DialogHanjaExcludeOptionBinding binding;

    private View getContentView() {
        binding = DialogHanjaExcludeOptionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public HanjaExcludeOptionDialog(@NonNull Context context, OnClickListener listener) {
        super(context);
        this.listener = listener;
        setContentView(getContentView());
        sharedPreferences = SharedPreferencesDB.getInstance(context);
//        ButterKnife.bind(this, binding.getRoot());
        setOnClickListeners();
        udpateOptionValue();
    }

    private void setOnClickListeners() {
        binding.tvOk.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    public void udpateOptionValue() {
        isShowSimplifiedChineseInWorkBooks = sharedPreferences.getShowSimplifiedChinese();
        binding.scShowSimplifiedChinese.setChecked(isShowSimplifiedChineseInWorkBooks);

        isShowBeyondLevelHanja = sharedPreferences.getShowBeyondLevelHanja();
        binding.scShowBeyondLevelHanja.setChecked(isShowBeyondLevelHanja);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (view.getId() == R.id.tv_ok) {
            if (listener != null) {
                listener.onClick(null, view.getId());
            }
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.scShowSimplifiedChinese:
                sharedPreferences.setShowSimplifiedChinese(isShowSimplifiedChineseInWorkBooks = isChecked);
                break;
            case R.id.scShowBeyondLevelHanja:
                sharedPreferences.setShowBeyondLevelHanja(isShowBeyondLevelHanja = isChecked);
                break;
        }
    }
}
