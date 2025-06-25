package com.dalread.listener;

import com.dalread.interfaces.IVocaBasicItem;

public interface OnKnowChangeListener {
    void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow);

    void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce);

    void onAddToWordbook(IVocaBasicItem iVocaBasicItem);

    void onAddToBookmark(IVocaBasicItem iVocaBasicItem);

    void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem); //Don't use this, use onAddToBookmark.(It will swap inside) Will combine them later

    void onDismiss();
}
