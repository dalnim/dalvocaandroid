package com.dalread.listener;

import com.dalread.model.VocaMemorize;

public interface OnTargetsClickListener {

    void onPlayAllSoundClick(int grade);

    void onPlaySoundClick(VocaMemorize voca);

    void onInfoClick(VocaMemorize voca);

    void onGradeClick(VocaMemorize voca);
}
