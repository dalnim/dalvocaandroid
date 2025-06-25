package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowSubtitleMenuBinding;
import com.dalread.listener.OnClickDialogListener;

public class PlayerShowSubtitleMenuDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
//    @BindView(R.id.tv_shadow_mode) TextView tv_shadow_mode;

    private OnClickDialogListener listener;
    private DialogPlayerShowSubtitleMenuBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSubtitleMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerShowSubtitleMenuDialog(@NonNull Context context, OnClickDialogListener listener) {
        super(context);
        setDialogSizeWider(true);
        this.listener = listener;
        setOnDismissListener(this);

        //Make it Gone on release mode.
        binding.tvShadowMode.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initOnClickListener() {
        binding.tvDictationMode.setOnClickListener(this);
        binding.tvShadowMode.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dictation_mode:
                this.view = v;
                break;
            default:
                this.view = null;
                break;
        }
        dismiss();
        if (listener != null) {
            listener.onClick(v, v.getId());
        }
    }

//    @OnClick({R.id.tv_dictation_mode, R.id.tv_shadow_mode, R.id.tv_cancel})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tv_dictation_mode:
//                this.view = view;
//                break;
//            default:
//                this.view = null;
//                break;
//        }
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, view.getId());
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }
}
