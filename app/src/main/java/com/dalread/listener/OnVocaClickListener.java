package com.dalread.listener;

import com.dalread.model.VocaInBook;

public interface OnVocaClickListener {

    void onPlayAllClick();

    void onPlayAllClick(int vocaKnow);

    void onPlayClick(VocaInBook voca);

    void onVocaKnowClick(VocaInBook voca);

    void onVocaKnowClick(VocaInBook voca, int vocaKnow);

    void onInfoClick(VocaInBook voca);

    void onDisplayOrderChange(VocaInBook voca, int newOrder);
}
