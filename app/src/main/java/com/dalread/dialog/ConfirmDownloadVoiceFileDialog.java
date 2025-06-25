package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.dalread.R;

import butterknife.BindString;

@SuppressLint("NonConstantResourceId")
public class ConfirmDownloadVoiceFileDialog extends ConfirmationDialog {

    @BindString(R.string.confirm_download_voice_files_dialog_message)
    String tplConfirm;

    public ConfirmDownloadVoiceFileDialog(@NonNull Context context, OnDialogClickListener listener) {
        super(context, listener);

        tvTitle.setText(R.string.warning);
        btnPositive.setText(R.string.yes);
        btnNegative.setText(R.string.no);
    }

    public void show(int size) {
        String sizeMB = String.valueOf(size / (1024 * 1024));
        String text = String.format(tplConfirm, sizeMB);
        tvMessage.setText(text);
        show();
    }
}
