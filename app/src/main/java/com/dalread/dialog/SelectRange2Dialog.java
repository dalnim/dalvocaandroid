package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.databinding.DialogSelectRange2Binding;
import com.dalread.util.Constant;
import com.dalread.util.EditTextUtils;

//@SuppressLint("NonConstantResourceId")
public class SelectRange2Dialog extends BaseDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    protected DialogSelectRange2Binding binding;
    private Context context;
    private final OnRangeSelectListener listener;
    private int fromIndex = 0;
    private int maxIndex = 0; //총 아이템 수
    private int countToSelectMax = Constant.vocaFilterCountToCheck * 5;
    private int countToSelect = Constant.vocaFilterCountToCheck;
    private View getContentView() {
        binding = DialogSelectRange2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public SelectRange2Dialog(@NonNull Context context, OnRangeSelectListener listener) {
        super(context, R.style.TransparentDialog);
        this.context = context;
        this.listener = listener;
        setContentView(getContentView());

        setOnClickListeners();
        setOnDismissListener(this);
    }

    public void show(int from, int to) {
        fromIndex = from;
        maxIndex = to;

        String text = String.valueOf(from);
        binding.etFromIndex.setText(text);
        binding.etFromIndex.setSelection(text.length());

        updatNpCountToSelect();
        show();
    }

    private void updatNpCountToSelect() {
        binding.npCountToSelect.setMinValue(1);
        binding.npCountToSelect.setMaxValue(maxIndex);
        binding.npCountToSelect.setValue(countToSelect);
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(null);
        }
    }
    private void setOnClickListeners() {
        binding.etFromIndex.addTextChangedListener(createTextWatcher(binding.etFromIndex));
        binding.btnNegative.setOnClickListener(this);
        binding.btnPositive.setOnClickListener(this);
        binding.npCountToSelect.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateStatus();
                countToSelect = newVal;
            }
        });
    }
    private TextWatcher createTextWatcher(EditText editText) {
        EditTextUtils.setClearButtonOnTouchListener(editText);
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStatus();
                EditTextUtils.updateClearButtonVisibility(editText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }
    private void updateFromIndex() {
        try {
            fromIndex = Integer.parseInt(binding.etFromIndex.getText().toString());
        } catch (Exception e) {
            fromIndex = 0;
        }

        if (fromIndex >= maxIndex) {
            fromIndex = maxIndex - 1;
            binding.etFromIndex.setText(String.valueOf(fromIndex));
        }
    }
    private void updateStatus() {
        updateFromIndex();
        binding.tvCountToSelect.setText(context.getString(R.string.title_select_range_count_to_select,  maxIndex, fromIndex, countToSelect));
    }
    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.btn_positive:
                if (listener != null) {
                    listener.onSelect(fromIndex, fromIndex + countToSelect - 1);
                }
                break;
        }
    }

    public interface OnRangeSelectListener {
        void onSelect(int from, int to);

        void onDismiss(View v);
    }
}
