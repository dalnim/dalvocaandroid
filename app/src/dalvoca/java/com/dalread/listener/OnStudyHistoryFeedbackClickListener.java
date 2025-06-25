package com.dalread.listener;

import com.dalread.model.VocaFeedback;

import java.util.Date;

public interface OnStudyHistoryFeedbackClickListener {

    void onPlayAllClick(Date date);

    void onPlayClick(VocaFeedback voca);

    void onInfoClick(VocaFeedback voca);
}
