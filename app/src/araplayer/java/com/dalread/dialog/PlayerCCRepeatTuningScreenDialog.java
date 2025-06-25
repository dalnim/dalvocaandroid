package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogCcRepeatTuningScreenBinding;
import com.dalread.listener.OnClickDialogListener;

public class PlayerCCRepeatTuningScreenDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private OnClickDialogListener listener;

    private DialogCcRepeatTuningScreenBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogCcRepeatTuningScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_cc_repeat_tuning_screen;
//    }

    public PlayerCCRepeatTuningScreenDialog(@NonNull Context context, OnClickDialogListener listener) {
        super(context);
        this.listener = listener;
        setOnDismissListener(this);
    }

    @Override
    protected void initOnClickListener() {
        binding.tvSyncPreviousDialogueEndTimeToCurrentDialogStartTime.setOnClickListener(this);
        binding.tvSyncCurrentDialogueEndTimeToNextDialogStartTime.setOnClickListener(this);
        binding.tvSyncCurrentDialogueStartTimeToPreviousDialogEndTime.setOnClickListener(this);
        binding.tvSyncNextDialogueStartTimeToCurrentDialogEndTime.setOnClickListener(this);
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
//    @OnClick({R.id.tv_sync_previous_dialogue_end_time_to_current_dialog_start_time,
//            R.id.tv_sync_current_dialogue_start_time_to_previous_dialog_end_time,
//            R.id.tv_sync_next_dialogue_start_time_to_current_dialog_end_time,
//            R.id.tv_sync_current_dialogue_end_time_to_next_dialog_start_time,
//            R.id.tv_cancel})
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
