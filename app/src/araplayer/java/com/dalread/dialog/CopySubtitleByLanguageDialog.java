package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerCopySubtitleByLanguageBinding;
import com.dalread.listener.OnClickDialogListener;

public class CopySubtitleByLanguageDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

    private OnClickDialogListener listener;
    private Object data;

    private DialogPlayerCopySubtitleByLanguageBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerCopySubtitleByLanguageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public CopySubtitleByLanguageDialog(@NonNull Context context, Object data, OnClickDialogListener listener) {
        super(context);
        this.data = data;
        this.listener = listener;
        setOnDismissListener(this);
    }

    @Override
    protected void initOnClickListener() {
        binding.tvStudyLanguage.setOnClickListener(this);
        binding.tvMotherTongue.setOnClickListener(this);
        binding.tvBothStudyMotherLanguage.setOnClickListener(this);
        binding.tvBothMotherStudyLanguage.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onDismiss(v, -1);
        }
    }


//    @OnClick({R.id.tv_study_language, R.id.tv_mother_tongue,
//            R.id.tv_both_study_mother_language, R.id.tv_both_mother_study_language,
//            R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, data);
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }
}
