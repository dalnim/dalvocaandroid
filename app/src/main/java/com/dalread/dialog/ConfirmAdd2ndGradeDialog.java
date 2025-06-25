package com.dalread.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dalread.R;
//TODO : Need to delete this. (Don't use it anymore)
public class ConfirmAdd2ndGradeDialog extends ConfirmationDialog {

    public ConfirmAdd2ndGradeDialog(@NonNull Context context, OnDialogClickListener listener) {
        super(context, listener);

        tvTitle.setText(R.string.warning);
        tvMessage.setText(R.string.msg_max_1st_grade);
        btnPositive.setText(R.string.yes);
        btnNegative.setText(R.string.no);
    }
}
