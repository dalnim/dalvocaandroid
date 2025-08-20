package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.databinding.DialogTypeInputBinding;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;
import com.dalread.util.EditTextUtils;
import com.dalread.util.Utils;

import java.util.ArrayList;

@SuppressLint("NonConstantResourceId")
public class TypeInputDialog extends BaseDialog {
    private DialogTypeInputBinding binding;
    private Context context;
    private Object object;

    public TypeInputDialog(Context context, BaseDialogListener listener) {
        super(context);
        this.context = context;
        commonConstruct(listener);
    }

    public TypeInputDialog(Context context, int title, int subtitle, int hint, String input, BaseDialogListener listener) {
        super(context);
        commonConstruct(listener);
        setTitle(title);
        setSubTitle(subtitle);
        setHint(hint);
        setInput(input);
    }
    private void commonConstruct(BaseDialogListener listener) {
        binding = DialogTypeInputBinding.inflate(getLayoutInflater());
        //이걸 추가해야 Utils.showSoftKeyboard를 안해도 키보드가 정상적으로 보인다. (이게 없으면 Utils.showSoftKeyboard를 해도 키보드가 금방 사라진다)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(binding.getRoot());
        baseDialogListener = listener;
        initListener();

        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(context, binding.llMain, R.color.multiPlayerBackgroundLightBlackColor);

            AraThemeUtil.setYesNoDialogBodyTextColor(context, binding.tvTitle);
            AraThemeUtil.setYesNoDialogBodyTextColor(context, binding.tvSubTitle);
            AraThemeUtil.setYesNodDialogBodyBackgroundColor(context, binding.tvTitle);
            AraThemeUtil.setYesNodDialogBodyBackgroundColor(context, binding.tvSubTitle);

            AraThemeUtil.setTextColor(context, binding.etInput, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setHintTextColor(context, binding.etInput, R.color.textGrayColor);
            AraThemeUtil.setBackgroundColor(context, binding.etInput, R.color.multiPlayerBackgroundBlackColor);

            AraThemeUtil.setDialogStyle(binding.btnOk);
            AraThemeUtil.setDialogStyle(binding.btnCancel);
            AraThemeUtil.setDialogBtnTextColor(context, binding.btnOk);
            AraThemeUtil.setDialogBtnTextColor(context, binding.btnCancel);
        }
    }

    private void initListener() {
        binding.etInput.addTextChangedListener(EditTextUtils.createTextWatcher(binding.etInput));
        binding.btnCancel.setOnClickListener( v -> {
            if (baseDialogListener != null) {
                baseDialogListener.onBaseDialogListenerCancel(enumType, this, v, 0, null);
            } else {
                dismiss(); // 리스너가 없을 때만 직접 dismiss
            }
        });
        binding.btnOk.setOnClickListener( v -> {
            if (baseDialogListener == null) {
                dismiss();
            } else {
                if (object == null) {
                    baseDialogListener.onBaseDialogListenerOk(enumType, this, v, 0, binding.etInput.getText().toString());
                } else {
                    ArrayList<Object> data = new ArrayList<>();
                    data.add(object);
                    data.add(binding.etInput.getText().toString());
                    baseDialogListener.onBaseDialogListenerOk(enumType, this, v, 0, data);
                }
            }
        });
    }

    public void setTitle(int title) {
        binding.tvTitle.setText(title);
    }

    public void setTitle(String title) {
        binding.tvTitle.setText(title);
    }

    public void setSubTitle(int subTitle) {
        setSubTitle(getContext().getString(subTitle));
    }

    public void setSubTitle(String subTitle) {
        binding.tvSubTitle.setText(subTitle);
        binding.tvSubTitle.setVisibility(TextUtils.isEmpty(subTitle) ? View.GONE : View.VISIBLE);
    }

    public void setHint(int hint) {
        binding.etInput.setHint(hint);
    }

    public void setInput(int input) {
        setInput(getContext().getString(input));
    }

    public void setInput(String input) {
        binding.etInput.setText(input);
    }

    public String getInput() {
        return binding.etInput.getText().toString();
    }

    public void setBtnOkText(String btnOkText) {
        binding.btnOk.setText(btnOkText);
    }

    @Override
    public void show() {
        super.show();
        if (TextUtils.isEmpty(binding.tvTitle.getText())) {
            binding.tvTitle.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(binding.tvSubTitle.getText())) {
            binding.tvSubTitle.setVisibility(View.GONE);
        }
        object = null;
        binding.etInput.post(() -> Utils.showSoftKeyboard(getContext(), binding.etInput));
    }

    public void show(Object object) {
        show();
        this.object = object;
    }
}
