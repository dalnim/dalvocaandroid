package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.databinding.DialogAddSentenceBinding;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

@SuppressLint("NonConstantResourceId")
public class DialogAddSentence extends BaseDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private final OnAddSentenceListener listener;
    private DialogAddSentenceBinding binding;
    private View getContentView() {
        binding = DialogAddSentenceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public DialogAddSentence(@NonNull Context context, String str, OnAddSentenceListener listener) {
        super(context, R.style.TransparentDialog);
        this.listener = listener;
        setContentView(getContentView());
        setOnClickListeners();
        setOnDismissListener(this);
        updateEditText(str);
    }

    private void updateEditText(String str) {
        if (!Utils.isEmpty(str)) {
            if (StringUtils.isOnlyChinese(str)) {
                binding.etNewSentenceVoca.setText(str.trim());
            } else {
                binding.etNewSentencePronounce.setText(str.trim());
            }
        }
    }

    public void show(int from, int to) {
        String text = String.valueOf(from);
        binding.etNewSentenceVoca.setText(text);
        binding.etNewSentenceVoca.setSelection(text.length());
        text = String.valueOf(to);
        binding.etNewSentencePronounce.setText(text);
        binding.etNewSentencePronounce.setSelection(text.length());
        show();
    }

    private void setOnClickListeners() {
        binding.btnPositive.setOnClickListener(this);
        binding.btnNegative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_negative:
                dismiss();
                break;
            case R.id.btn_positive:
                String sentence = binding.etNewSentenceVoca.getText().toString().trim();
                if (isValidNewSentence(sentence)) {
                    dismiss();
                    if (listener != null) {
                        String pronounce = binding.etNewSentencePronounce.getText().toString().trim();
                        listener.onSelect(sentence, pronounce);
                    }
                } else {
                    if (sentence.length() < 2) {
                        ToastUtil.getInstance(getContext()).show(R.string.toast_add_sentence_no_two_or_more_letter);
                    } else if (!StringUtils.isOnlyCKJUnifiedChinese(sentence)) {
                        ToastUtil.getInstance(getContext()).show(R.string.toast_add_sentence_no_unicode_unified_hanja);
                    }
                }
                break;
        }
    }

//    @OnClick(R.id.btn_negative)
//    void onCancel(View view) {
//        dismiss();
//    }
//
//    @OnClick(R.id.btn_positive)
//    void onOK(View view) {
//        String sentence = binding.etNewSentenceVoca.getText().toString().trim();
//        if (isValidNewSentence(sentence)) {
//            dismiss();
//            if (listener != null) {
//                String pronounce = binding.etNewSentencePronounce.getText().toString().trim();
//                listener.onSelect(sentence, pronounce);
//            }
//        } else {
//            ToastUtil.getInstance(getContext()).show(R.string.hint_add_sentence_voca);
//        }
//
//    }

    private boolean isValidNewSentence(String sentence) {
        boolean blnResult = false;
        if ((!Utils.isEmpty(sentence)) && (StringUtils.isOnlyCKJUnifiedChinese(sentence)) && (sentence.length() > 1)){
            blnResult = true;
        }
        return blnResult;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(null);
        }
    }

    public interface OnAddSentenceListener {
        void onSelect(String sentence, String pronounce);

        void onDismiss(View v);
    }
}
