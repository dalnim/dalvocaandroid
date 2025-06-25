package com.dalread.model;

import android.text.TextUtils;

import com.dalread.base.EnumLanguage;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class READING extends RealmObject {

    @PrimaryKey
    private int ID;
    private int PARENT_ID;
    private int HAS_SUB_LIST;
    private int DISP_ORDER;
    private int USED;
    private int LANG_STUDY;
    @Ignore
    private int DISPLAY_LANG;
    private String NAME_ENG;
    private String NAME_KO;
    private String MEANING_ENG_DETAILED;
    private String MEANING_KO_DETAILED;
    @Ignore
    private boolean isRandom;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPARENT_ID() {
        return PARENT_ID;
    }

    public void setPARENT_ID(int PARENT_ID) {
        this.PARENT_ID = PARENT_ID;
    }

    public int getHAS_SUB_LIST() {
        return HAS_SUB_LIST;
    }

    public void setHAS_SUB_LIST(int HAS_SUB_LIST) {
        this.HAS_SUB_LIST = HAS_SUB_LIST;
    }

    public int getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(int DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }

    public int getUSED() {
        return USED;
    }

    public void setUSED(int USED) {
        this.USED = USED;
    }

    public int getLANG_STUDY() {
        return LANG_STUDY;
    }

    public void setLANG_STUDY(int LANG_STUDY) {
        this.LANG_STUDY = LANG_STUDY;
    }

    public int getDISPLAY_LANG() {
        return DISPLAY_LANG;
    }

    public void setDISPLAY_LANG(int DISPLAY_LANG) {
        this.DISPLAY_LANG = DISPLAY_LANG;
    }

    public String getNAME_ENG() {
        return NAME_ENG;
    }

    public void setNAME_ENG(String NAME_ENG) {
        this.NAME_ENG = NAME_ENG;
    }

    public String getNAME_KO() {
        return NAME_KO;
    }

    public void setNAME_KO(String NAME_KO) {
        this.NAME_KO = NAME_KO;
    }

    public String getMEANING_ENG_DETAILED() {
        return MEANING_ENG_DETAILED;
    }

    public void setMEANING_ENG_DETAILED(String MEANING_ENG_DETAILED) {
        this.MEANING_ENG_DETAILED = MEANING_ENG_DETAILED;
    }

    public String getMEANING_KO_DETAILED() {
        return MEANING_KO_DETAILED;
    }

    public void setMEANING_KO_DETAILED(String MEANING_KO_DETAILED) {
        this.MEANING_KO_DETAILED = MEANING_KO_DETAILED;
    }

    public String getNAME() {
        return DISPLAY_LANG == EnumLanguage.KOREAN.getIdApi() ? NAME_KO : NAME_ENG;
    }

    public String getNAME_() {
        String name = LANG_STUDY == EnumLanguage.KOREAN.getIdApi() ? NAME_KO : NAME_ENG;
        String name1 = DISPLAY_LANG == EnumLanguage.KOREAN.getIdApi() ? NAME_KO : NAME_ENG;
        if (!TextUtils.isEmpty(name1) && !name1.equals(name))
            return name + " (" + name1 + ")";
        return name;
    }

    public String getMEANING_DETAILED() {
        return DISPLAY_LANG == EnumLanguage.KOREAN.getIdApi() ? MEANING_KO_DETAILED : MEANING_ENG_DETAILED;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }
}
