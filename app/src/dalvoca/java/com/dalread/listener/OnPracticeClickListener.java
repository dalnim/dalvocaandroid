package com.dalread.listener;

import com.dalread.model.VocaPractice;

public interface OnPracticeClickListener {

    void onPlayClick(VocaPractice voca);

    void onGradeClick(VocaPractice voca);

    void onInfoClick(VocaPractice voca);
}
