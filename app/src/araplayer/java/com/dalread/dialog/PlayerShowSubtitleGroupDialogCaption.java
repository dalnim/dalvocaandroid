package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowSubtitleDialogCaptionBinding;
import com.dalread.listener.OnClickDialogListener;

public class PlayerShowSubtitleGroupDialogCaption extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private OnClickDialogListener listener;
    private boolean isHideKnownDialogDuringPlaying;
    private DialogPlayerShowSubtitleDialogCaptionBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSubtitleDialogCaptionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        isHideKnownDialogDuringPlaying = mSharedPref.getHideKnownDialogsDuringPlaying();
        binding.scHideKnownDialog.setChecked(isHideKnownDialogDuringPlaying);
    }

    public PlayerShowSubtitleGroupDialogCaption(@NonNull Context context, OnClickDialogListener listener) {
        this(context, false, listener);
    }

    public PlayerShowSubtitleGroupDialogCaption(@NonNull Context context, boolean isDictationMode, OnClickDialogListener listener) {
        super(context);
        setDialogSizeWider(true);
        this.listener = listener;
        setOnDismissListener(this);
    }

    @Override
    protected void initOnClickListener() {
        binding.scHideKnownDialog.setOnClickListener(this);
        binding.llDeleteUselessSubtitles.setOnClickListener(this);
        binding.llSelectSubtitles.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scHideKnownDialog:
                isHideKnownDialogDuringPlaying = !isHideKnownDialogDuringPlaying;
                mSharedPref.setHideKnownDialogsDuringPlaying(isHideKnownDialogDuringPlaying);
                break;
        }
        dismiss();
        if (listener != null) {
            listener.onClick(v, v.getId());
        }
    }

//
//    @OnClick({
//            R.id.scHideKnownDialog,
//            R.id.llDeleteUselessSubtitles,
//            R.id.llSelectSubtitles,
//            R.id.tv_cancel})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.scHideKnownDialog:
//                isHideKnownDialogDuringPlaying = !isHideKnownDialogDuringPlaying;
//                mSharedPref.setHideKnownDialogsDuringPlaying(isHideKnownDialogDuringPlaying);
//                break;
//        }
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, view.getId());
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if ((listener != null) && (view != null)){
            listener.onDismiss(view, -1);
        }
    }
}
