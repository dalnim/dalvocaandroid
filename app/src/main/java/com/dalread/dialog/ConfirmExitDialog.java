package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumType;
import com.dalread.databinding.DialogConfirmExitBinding;
import com.dalread.util.AraThemeUtil;

/**
 * Created by JetVHS on 6/13/17.
 */

@SuppressLint("NonConstantResourceId")
public class ConfirmExitDialog extends BaseDialog implements View.OnClickListener {

    protected DialogConfirmExitBinding binding;

    protected View getContentView() {
        binding = DialogConfirmExitBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public ConfirmExitDialog(EnumType enumType, Context context, BaseDialogListener baseDialogListener) {
        super(context, AraThemeUtil.getYesNoDialogThemeResId()); // 테마 적용
        setContentView(getContentView());
        initOnClickListener();
        this.enumType = enumType;

        setBaseDialogListener(baseDialogListener);

        AraThemeUtil.setYesNoDialogBodyTextColor(context, binding.tvTitle);
        AraThemeUtil.setYesNoDialogBodyTextColor(context, binding.tvMessage);
        AraThemeUtil.setYesNodDialogBodyBackgroundColor(context, binding.tvTitle);
        AraThemeUtil.setYesNodDialogBodyBackgroundColor(context, binding.tvMessage);

        AraThemeUtil.setDialogStyle(binding.btnYes);
        AraThemeUtil.setDialogStyle(binding.btnCancel);
        AraThemeUtil.setDialogRedBtnTextColor(context, binding.btnYes);
        AraThemeUtil.setDialogBtnTextColor(context, binding.btnCancel);
    }

    private void initOnClickListener() {
        binding.btnCancel.setOnClickListener(this);
        binding.btnYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnYes) {
            if (baseDialogListener != null) {
                baseDialogListener.onBaseDialogListenerOk(enumType, this, null, 0, null);
            }
        }
        dismiss();
    }
}
