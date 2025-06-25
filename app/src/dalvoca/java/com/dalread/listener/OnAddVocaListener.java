package com.dalread.listener;

import com.dalread.model.VocaSearch;

public interface OnAddVocaListener {

    void onPlayAllClick();

    void onPlayClick(VocaSearch voca);

    void onRegisterClick(VocaSearch voca);

    void onInfoClick(VocaSearch voca);
}
