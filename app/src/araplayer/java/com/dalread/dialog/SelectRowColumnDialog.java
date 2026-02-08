package com.dalread.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogSelectRowColumnBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

public class SelectRowColumnDialog extends BasePlayerDialog implements View.OnClickListener {
    private static final int MIN = 1;
    private static final int MAX = 5;

    private final OnClickDialogListener listener;
    private DialogSelectRowColumnBinding binding;

    public SelectRowColumnDialog(@NonNull Context context, int currentRow, int currentColumn,
                                OnClickDialogListener listener) {
        super(context);
        this.listener = listener;
    }

    private static int clamp(int value) {
        if (value < MIN) return MIN;
        if (value > MAX) return MAX;
        return value;
    }

    @Override
    protected View getContentView() {
        binding = DialogSelectRowColumnBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void initOnClickListener() {
        String[] options = new String[MAX - MIN + 1];
        for (int i = 0; i < options.length; i++) {
            options[i] = String.valueOf(MIN + i);
        }
        int itemLayout = AppFlavorUtil.isAraMultiPlayerApp()
                ? R.layout.item_spinner_white_text
                : android.R.layout.simple_spinner_item;
        int dropDownLayout = AppFlavorUtil.isAraMultiPlayerApp()
                ? R.layout.item_spinner_white_text
                : android.R.layout.simple_spinner_dropdown_item;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), itemLayout, options);
        adapter.setDropDownViewResource(dropDownLayout);
        binding.spRow.setAdapter(adapter);
        binding.spColumn.setAdapter(adapter);
        int savedRow = clamp(SharedPreferencesDB.getInstance(getContext()).getMultiPlayerRow());
        int savedColumn = clamp(SharedPreferencesDB.getInstance(getContext()).getMultiPlayerColumn());
        binding.spRow.setSelection(savedRow - MIN);
        binding.spColumn.setSelection(savedColumn - MIN);
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(getContext(), binding.getRoot(), R.color.multiPlayerBackgroundLightBlackColor);
            AraThemeUtil.setTextColor(getContext(), binding.tvTitle, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(getContext(), binding.tvWarning, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(getContext(), binding.tvRowLabel, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(getContext(), binding.tvColumnLabel, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(getContext(), binding.btnCancel, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(getContext(), binding.btnOk, R.color.textPrimaryWhiteColor);
            int bgColor = ContextCompat.getColor(getContext(), R.color.multiPlayerBackgroundLightBlackColor);
            binding.spRow.setPopupBackgroundDrawable(new ColorDrawable(bgColor));
            binding.spColumn.setPopupBackgroundDrawable(new ColorDrawable(bgColor));
        }
        binding.btnOk.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancel) {
            dismiss();
            if (listener != null) {
                listener.onDismiss(v, null);
            }
            return;
        }
        if (v.getId() == R.id.btnOk) {
            int row = MIN + binding.spRow.getSelectedItemPosition();
            int column = MIN + binding.spColumn.getSelectedItemPosition();
            dismiss();
            if (listener != null) {
                listener.onClick(v, new Pair<>(row, column));
            }
        }
    }
}
