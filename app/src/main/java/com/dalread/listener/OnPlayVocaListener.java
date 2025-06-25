package com.dalread.listener;

import com.dalread.interfaces.IVocaFullPlayTTSItem;

public interface OnPlayVocaListener {

    void onPlay(IVocaFullPlayTTSItem voca);

    void onStop(IVocaFullPlayTTSItem voca);
}
