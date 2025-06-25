package com.dalread.interfaces;

import com.dalread.base.EnumLanguage;

public interface IVocabooksCommon {
    long getIBookId();
    long getIBookVocaCount();
    long getIBookRecordedVocaCount();
    void setIBookRecordedVocaCount(int value);
    String getIBookName(EnumLanguage enumLanguage);
    String getIBookNameEng();
    String getIBookNameStudyLang();
    long getIBookUsed();
    boolean isIBookHasSubList();
    long getIBookCountOfVocaKnow();
}
