package com.dalread.listener;

import com.dalread.model.VocaStudyChatExam;

public interface OnVocaStudyChatExamClickListener {

    void onPlayClick(VocaStudyChatExam voca);

    void onButtonClick(VocaStudyChatExam voca);

    void onBigIconClick(VocaStudyChatExam voca);

    void onEvaluateGradeClick(VocaStudyChatExam voca, String grade);
}
