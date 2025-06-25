package com.dalread.listener;

import com.dalread.model.VocaStudyHistory;

import java.util.Date;

public interface OnStudyHistoryClickListener {

    void onPlayAllClick(Date date);

    void onPlayClick(VocaStudyHistory voca);

    void onInfoClick(VocaStudyHistory voca);
}
