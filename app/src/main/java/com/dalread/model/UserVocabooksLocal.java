package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocabooksCommon;

public class UserVocabooksLocal implements IVocabooksCommon {
    public int ID;
    public int PARENT_ID;
    public String ID_LIST;
    public int ID_LIST_TYPE;
    public int UID;
    public String TITLE;
    public int VOCA_COUNT;
    public int IMAGE_ID;
    public int VERSION;
    public int LANG_STUDY;
    public int USED;
    public int HAS_SUB_LIST;
    public int DISP_ORDER;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(int DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }

    public int getPARENT_ID() {
        return PARENT_ID;
    }

    public void setPARENT_ID(int PARENT_ID) {
        this.PARENT_ID = PARENT_ID;
    }

    public String getID_LIST() {
        return ID_LIST;
    }

    public void setID_LIST(String ID_LIST) {
        this.ID_LIST = ID_LIST;
    }

    public int getID_LIST_TYPE() {
        return ID_LIST_TYPE;
    }

    public void setID_LIST_TYPE(int ID_LIST_TYPE) {
        this.ID_LIST_TYPE = ID_LIST_TYPE;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public int getVOCA_COUNT() {
        return VOCA_COUNT;
    }

    public void setVOCA_COUNT(int VOCA_COUNT) {
        this.VOCA_COUNT = VOCA_COUNT;
    }

    public int getIMAGE_ID() {
        return IMAGE_ID;
    }

    public void setIMAGE_ID(int IMAGE_ID) {
        this.IMAGE_ID = IMAGE_ID;
    }

    public int getVERSION() {
        return VERSION;
    }

    public void setVERSION(int VERSION) {
        this.VERSION = VERSION;
    }

    public int getLANG_STUDY() {
        return LANG_STUDY;
    }

    public void setLANG_STUDY(int LANG_STUDY) {
        this.LANG_STUDY = LANG_STUDY;
    }

    public int getUSED() {
        return USED;
    }

    public void setUSED(int USED) {
        this.USED = USED;
    }

    public int getHAS_SUB_LIST() {
        return HAS_SUB_LIST;
    }

    public void setHAS_SUB_LIST(int HAS_SUB_LIST) {
        this.HAS_SUB_LIST = HAS_SUB_LIST;
    }

    @Override
    public long getIBookId() {
        return getID();
    }

    @Override
    public long getIBookVocaCount() {
        return getVOCA_COUNT();
    }

    @Override
    public long getIBookRecordedVocaCount() {
        return 0;
    }

    @Override
    public void setIBookRecordedVocaCount(int value) {

    }

    @Override
    public String getIBookName(EnumLanguage enumLanguage) {
        return getTITLE();
    }

    @Override
    public String getIBookNameEng() {
        return getTITLE();
    }

    @Override
    public String getIBookNameStudyLang() {
        return getTITLE();
    }

    @Override
    public long getIBookUsed() {
        return getUSED();
    }

    @Override
    public boolean isIBookHasSubList() {
        return getHAS_SUB_LIST() > 0;
    }

    @Override
    public long getIBookCountOfVocaKnow() {
        return 0;
    }
}
