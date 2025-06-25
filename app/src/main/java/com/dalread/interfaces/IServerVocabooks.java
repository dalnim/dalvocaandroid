package com.dalread.interfaces;

public interface IServerVocabooks extends IVocabooksCommon{
//    long getIBookId();
    long getIBookParentId();
//    long getIBookVocaCount();
    long getIBookImageId();
    long getIBookVersion();
    long getIBookLangStudy();
    long getIBookDispOrder();
    long getIBookUseRecording();
//    String getBookName(EnumLanguage enumLanguage);
//    String getBookNameEng();
    long getIBookUseVocabook();
    long getIBookUseTodayExpression();
    long getIBookUsePractice();
    long getIBookUseAlphabet();
//    long getIBookUsed();
//    boolean isIBookHasSubList();
//    long getIBookCountOfVocaKnow();
}
