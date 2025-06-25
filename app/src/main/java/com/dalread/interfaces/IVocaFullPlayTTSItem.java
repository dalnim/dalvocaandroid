package com.dalread.interfaces;

public interface IVocaFullPlayTTSItem extends IVocaFullItem {
    boolean isVIChecked(); //In Play All Words, used to play TTS or Not.
    void setVIChecked(boolean checked);

    boolean isVIPlaying();
    void setVIPlaying(boolean playing);

    boolean isVIRecording();
    void setVIRecording(boolean recording);

    String getVIPath(); //Voice file's name?
    void setVIPath(String path);

//    boolean hasVIVoiceFile();
//    default void setVIVoiceFile(boolean value) {
//
//    }
    int getVIVoiceFileVersion();

    String getPersonAB();
    default void setPersonAB(String value) {

    }
    //Repeat count
    default int getVIRepeatCount() {
        return 0;
    }
    default void setVIRepeatCount(int value) {

    }

    default int getVIDifficultWordsCount() {
        return 0;
    }

    default void setVIDifficultWordsCount(int value) {

    }

    default String getVISEARCH_HISTORY() {
        return "";
    }

    default void setVISEARCH_HISTORY(String SEARCH_HISTORY) {

    }
}
