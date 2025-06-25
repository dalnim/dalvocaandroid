package com.dalread.listener;

import com.dalread.model.VocaStudy;

public interface OnHomeworkClickListener {

    void onPlayAllClick();

    void onPlayClick(VocaStudy voca);

    void onStudyClick(VocaStudy voca);

    void onInfoClick(VocaStudy voca);
}
