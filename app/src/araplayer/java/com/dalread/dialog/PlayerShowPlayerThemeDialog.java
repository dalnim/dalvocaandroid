package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowPlayerThemeBinding;
import com.dalread.listener.OnClickDialogListener;

public class PlayerShowPlayerThemeDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
//    @BindView(R.id.tv_shadow_mode) TextView tv_shadow_mode;

    private OnClickDialogListener listener;
    private DialogPlayerShowPlayerThemeBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowPlayerThemeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_show_subtitle_menu;
//    }

    public PlayerShowPlayerThemeDialog(@NonNull Context context, OnClickDialogListener listener) {
        super(context);
        setDialogSizeWider(true);
        this.listener = listener;
        setOnDismissListener(this);
    }

    @Override
    protected void initOnClickListener() {
        binding.tvPalyerTheme1.setOnClickListener(this);
        binding.tvPalyerTheme2.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v, v.getId());
        }
    }
//
//    @OnClick({R.id.tvPalyerTheme1, R.id.tvPalyerTheme2, R.id.tv_cancel})
//    void onClick(View view) {
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
