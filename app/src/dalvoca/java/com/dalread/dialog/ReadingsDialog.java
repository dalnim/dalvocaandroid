package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ReadingsDialog extends BaseDialog {

    @BindView(R.id.v_move_to_next_reading)
    View vMoveToNextReading;
    @BindView(R.id.v_move_to_previous_reading)
    View vMoveToPreviousReading;

    private final OnClickListener listener;

    public ReadingsDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_readings);
        ButterKnife.bind(this);
    }

    public void showMoveToNextReading() {
        vMoveToNextReading.setVisibility(View.VISIBLE);
    }

    public void hideMoveToNextReading() {
        vMoveToNextReading.setVisibility(View.GONE);
    }

    public void showMoveToPreviousReading() {
        vMoveToPreviousReading.setVisibility(View.VISIBLE);
    }

    public void hideMoveToPreviousReading() {
        vMoveToPreviousReading.setVisibility(View.GONE);
    }

    @OnClick({
            R.id.tv_move_to_next_reading, R.id.tv_move_to_previous_reading, R.id.tv_refresh_current_reading,
            R.id.tv_select_a_reading, R.id.tv_save_as_the_last_reading, R.id.tv_change_word_known_status,
            R.id.tv_cancel
    })
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
