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
public class TopicsDialog extends BaseDialog {

    @BindView(R.id.v_move_to_next_book)
    View vMoveToNextBook;
    @BindView(R.id.v_move_to_previous_book)
    View vMoveToPreviousBook;

    private final OnClickListener listener;

    public TopicsDialog(@NonNull Context context, OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_topics);
        ButterKnife.bind(this);
    }

    public void showMoveToNextBook() {
        vMoveToNextBook.setVisibility(View.VISIBLE);
    }

    public void hideMoveToNextBook() {
        vMoveToNextBook.setVisibility(View.GONE);
    }

    public void showMoveToPreviousBook() {
        vMoveToPreviousBook.setVisibility(View.VISIBLE);
    }

    public void hideMoveToPreviousBook() {
        vMoveToPreviousBook.setVisibility(View.GONE);
    }

    @OnClick({
            R.id.tv_move_to_next_book, R.id.tv_move_to_previous_book, R.id.tv_sync_current_topic,
            R.id.tv_select_wordbook, R.id.tv_save_as_a_last_wordbook,
            R.id.tv_cancel
    })
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, view.getId());
        }
    }
}
