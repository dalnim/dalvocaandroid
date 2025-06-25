package com.dalread.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.dalread.databinding.ActivityGptWebMenuEditBinding;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

public class ChatGptMenuEditDialog {
    private Activity activity;
    private Dialog dialog;
    private String shortCut;
    private String expandedPhrase;
    private ActivityGptWebMenuEditBinding binding;
    private OnDescEnteredListener onDescEnteredListener;
    public interface OnDescEnteredListener {
        void onDescEntered(String shortCut, String expandedPhrase);
    }

    public ChatGptMenuEditDialog(Activity activity, String shortCut, String expandedPhrase, OnDescEnteredListener onDescEnteredListener) {
        this.binding = ActivityGptWebMenuEditBinding.inflate(LayoutInflater.from(activity));
        this.activity = activity;
        this.shortCut = shortCut;
        this.expandedPhrase = expandedPhrase;
        this.onDescEnteredListener = onDescEnteredListener;
    }

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(binding.getRoot());

        binding.etShortcut.setText(shortCut);
        binding.etExpandedPharse.setText(expandedPhrase);

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shortCut = binding.etShortcut.getText().toString();
                String expandedPhrase = binding.etExpandedPharse.getText().toString();
                if (Utils.isEmpty(shortCut) || Utils.isEmpty(expandedPhrase)) {
                    ToastUtil.getInstance(dialog.getContext()).show("내용이 둘다 있어야 합니다.");
                } else {
                    if (onDescEnteredListener != null) {
                        onDescEnteredListener.onDescEntered(shortCut, expandedPhrase);
                    }
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}

