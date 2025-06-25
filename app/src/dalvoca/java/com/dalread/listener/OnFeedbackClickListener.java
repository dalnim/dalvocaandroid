package com.dalread.listener;

import com.dalread.model.VocaFeedback;

public interface OnFeedbackClickListener {

    void onPlayAllClick();

    void onPlayClick(VocaFeedback voca);

    void onConfirmClick(VocaFeedback voca);
}
