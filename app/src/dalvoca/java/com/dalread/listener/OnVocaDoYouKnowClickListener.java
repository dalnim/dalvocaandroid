package com.dalread.listener;

import com.dalread.model.VocaDoYouKnow;

public interface OnVocaDoYouKnowClickListener {

    void onPlayClick(VocaDoYouKnow voca);

    void onGradeClick(VocaDoYouKnow voca);

    void onGradeClick(VocaDoYouKnow voca, int grade);

    void onInfoClick(VocaDoYouKnow voca);
}
