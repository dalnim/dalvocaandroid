package com.dalread.database.sqlite.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.util.Constant;

import java.io.Serializable;

public class UserDicEngModel implements Serializable, IVocaFullItem {// IVocaFullItem, PlaylistItem {
    private int ID;
    private String VOCA;
    private String PRONOUNCE;
    private int BOOKMARK;
    private int VOCA_KNOWPRONOUNCE;
    private int VOCA_KNOW;
    private String MEANING;
    private String MEANING_DETAILED;

    @Override
    public Integer getVIId() {
        return ID;
    }

    @Override
    public String getVIVoca() {
        return VOCA != null ? VOCA : "";
    }

    @Override
    public Integer getVIVocaKnow() {
        return VOCA_KNOW;
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return VOCA_KNOWPRONOUNCE;
    }

    @Override
    public String getVIVocaTTS() {
        return "";
    }

    @Override
    public String getVIPronounce() {
        return PRONOUNCE != null ? PRONOUNCE : "";
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        return MEANING != null ? MEANING : "";
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return MEANING_DETAILED != null ? MEANING_DETAILED : "";
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguage) {
        return "";
    }

    @Override
    public String getVIMeaningEng() {
        return "";
    }

    @Override
    public String getVIMeaningEngDetailed() {
        return "";
    }

    @Override
    public String getVIMeaningEngTts() {
        return "";
    }

    @Override
    public Integer getVIBookmark() {
        return BOOKMARK;
    }

    @Override
    public boolean isVIBookmark() {
        return BOOKMARK == Constant.INT_BOOLEAN.TRUE;
    }

    @Override
    public void setVIId(Integer value) {
        ID = value;
    }

    @Override
    public void setVIVoca(String value) {
        VOCA = value;
    }

    @Override
    public void setVIVocaTTS(String value) {

    }

    @Override
    public void setVIPronounce(String value) {
        PRONOUNCE = value;
    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguage, String value) {
        MEANING = value;
    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguage, String value) {
        MEANING_DETAILED = value;
    }

    @Override
    public void setVIMeaningTts(EnumLanguage enumLanguage, String value) {

    }

    @Override
    public void setVIMeaningEng(String value) {

    }

    @Override
    public void setVIMeaningEngDetailed(String value) {

    }

    @Override
    public void setVIMeaningEngTts(String value) {

    }

    @Override
    public void setVIVocaKnow(Integer value) {
        VOCA_KNOW = value;
    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {
        VOCA_KNOWPRONOUNCE = value;
    }

    @Override
    public void setVIBookmark(Integer value) {
        BOOKMARK = value;
    }

    @Override
    public void swapVIBookmark() {

    }

    @Override
    public Integer getVIVocaTypeBase() {
        return 0;
    }

    @Override
    public Integer getVIVocaIdBase() {
        return 0;
    }

    @Override
    public void setVIVocaTypeBase(Integer value) {

    }

    @Override
    public void setVIVocaIdBase(Integer value) {

    }

    @Override
    public boolean hasVIVoiceFile() {
        return false;
    }

    @Override
    public Integer getVIVocaType() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
    }

    @Override
    public Integer getVIVocaId() {
        return ID;
    }

    @Override
    public void setVIVocaType(Integer value) {

    }

    @Override
    public void setVIVocaId(Integer value) {
        ID = value;
    }

    @Override
    public Integer getVIIndex() {
        return null;
    }

    @Override
    public void setVIIndex(Integer index) {

    }

    @Override
    public String getVIPosAll() {
        return null;
    }

//    @Override
//    public boolean isVIChecked() {
//        return false;
//    }
//
//    @Override
//    public void setVIChecked(boolean checked) {
//
//    }
//
//    @Override
//    public boolean isVIPlaying() {
//        return false;
//    }
//
//    @Override
//    public void setVIPlaying(boolean playing) {
//
//    }
//
//    @Override
//    public boolean isVIRecording() {
//        return false;
//    }
//
//    @Override
//    public void setVIRecording(boolean recording) {
//
//    }
//
//    @Override
//    public String getVIPath() {
//        return null;
//    }
//
//    @Override
//    public void setVIPath(String path) {
//
//    }
//
//    @Override
//    public int getVIVoiceFileVersion() {
//        return 0;
//    }
//
//    @Override
//    public String getPersonAB() {
//        return null;
//    }
}
