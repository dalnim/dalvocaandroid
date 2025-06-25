package com.dalread.listener;

import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatExam;

public interface OnVocaStudyChatClickListener {

    void onItemClick(VocaStudyChat voca);

    void onDoubleItemClick(VocaStudyChat voca);

    void onPlayClick(VocaStudyChat voca);

    void onBigIconClick(VocaStudyChat voca);

    void onAsteriskSentenceClick(VocaStudyChat voca);

    void onVocaKnowClick(VocaStudyChat voca, int vocaKnow);

    void onEvaluateGradeClick(VocaStudyChat voca, String evaluateGrade);

    void onAnswerClick(VocaStudyChatExam voca);
}
