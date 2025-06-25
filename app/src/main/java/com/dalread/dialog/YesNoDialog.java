package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogYesNoBinding;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

@SuppressLint("NonConstantResourceId")
public class YesNoDialog extends BasePlayerDialog implements View.OnClickListener {
    private final OnYesNoClickListener listener;
    private final Object data;

    protected DialogYesNoBinding binding;
    @Override
    protected View getContentView() {
        binding = DialogYesNoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public YesNoDialog(@NonNull Context context, int title, int message, Object data, OnYesNoClickListener listener) {
        this(context, context.getString(title), context.getString(message), data, listener);
    }

    public YesNoDialog(@NonNull Context context, int title, String message, Object data, OnYesNoClickListener listener) {
        this(context, context.getString(title), message, data, listener);
    }


    public YesNoDialog(@NonNull Context context, String title, String message, Object data, OnYesNoClickListener listener) {
//        super(context); // 테마 적용
        super(context, AraThemeUtil.getYesNoDialogThemeResId()); // 테마 적용

        this.listener = listener;
        this.data = data;
//        setOnClickListeners();

        binding.tvTitle.setText(title);
        binding.tvMessage.setText(message);

        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setYesNoDialogBodyTextColor(context, binding.tvTitle);
            AraThemeUtil.setYesNoDialogBodyTextColor(context, binding.tvMessage);
            AraThemeUtil.setYesNodDialogBodyBackgroundColor(context, binding.tvTitle);
            AraThemeUtil.setYesNodDialogBodyBackgroundColor(context, binding.tvMessage);

            AraThemeUtil.setDialogStyle(binding.btnYes);
            AraThemeUtil.setDialogStyle(binding.btnNo);
            AraThemeUtil.setDialogBtnTextColor(context, binding.btnYes);
            AraThemeUtil.setDialogBtnTextColor(context, binding.btnNo);
        } else {
            //AraThemeUtil.setDialogStyle로 하는건 style을 통채로는 안되어서, 이렇게 background만 따로 해야한다.
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.btn_button);
            binding.btnNo.setBackground(drawable);
            binding.btnYes.setBackground(drawable);
        }
    }

//    private void setOnClickListeners() {
//        binding.btnNo.setOnClickListener(this);
//        binding.btnYes.setOnClickListener(this);
//    }
    @Override
    protected void initOnClickListener() {
        binding.btnNo.setOnClickListener(this);
        binding.btnYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            switch (v.getId()) {
                case R.id.btnNo:
                    listener.onNoClick(v, data);
                    break;
                case R.id.btnYes:
                    listener.onYesClick(v, data);
                    break;
            }
        }
    }
}
