package com.dalread.interfaces;

public interface IBaseVocaKnow {
    void updateVocaKnow(IVocaBasicItem voca, int newValue);
    void updateVocaKnowInDB(IVocaBasicItem voca, int newValue);
    void updateVocaKnowInVariable(IVocaBasicItem voca, int newValue);

    void updateVocaKnowPronounce(IVocaBasicItem voca, int newValue);
    void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newValue);
    void updateVocaKnowPronounceInVariable(IVocaBasicItem voca, int newValue);

    void udpateBookmark(IVocaBasicItem voca);
    void udpateBookmarkInLocalDB(IVocaBasicItem voca);
    void udpateBookmarkInServerDB(IVocaBasicItem voca);
    void udpateBookmarkInVariable(IVocaBasicItem voca);

}
