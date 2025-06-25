// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.dalread.model;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
public class VOCABOOKS_HANJA_CLASSICS extends RealmObject implements Serializable {

    @PrimaryKey
    private Long ID;
    private String HANJA_KO;
    private String NAME_KO;
    private String NAME_ENG;
    private Long DISP_ORDER;
    private String NAME_KO_DETAILED;
    private String NAME_ENG_DETAILED;
    private Long HAS_SUB_LIST;
    private Long VOCA_COUNT;
    private Long USED;
    private Long PARENT_ID;
    private String ID_LIST;
    private Long VERSION;
    private String CREATE_DATE;
    private Long LANG_STUDY;
    private Long IMAGE_ID; //Use Last Read Item (Temp, I'll have other key for this purpose later)
    private String HANJA_JP;
    private String NAME_JP;
    private String NAME_CH_S;
    private String HANJA_CH_S;
    private String NAME_CH_T;
    private String HANJA_CH_T;
    private String NAME_JP_DETAILED;
    private String NAME_CH_S_DETAILED;
    private String NAME_CH_T_DETAILED;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getHANJA_KO() {
        return HANJA_KO;
    }

    public void setHANJA_KO(String HANJA_KO) {
        this.HANJA_KO = HANJA_KO;
    }

    public String getNAME_KO() {
        return NAME_KO;
    }

    public void setNAME_KO(String NAME_KO) {
        this.NAME_KO = NAME_KO;
    }

    public String getNAME_ENG() {
        return NAME_ENG;
    }

    public void setNAME_ENG(String NAME_ENG) {
        this.NAME_ENG = NAME_ENG;
    }

    public Long getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(Long DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }

    public String getNAME_KO_DETAILED() {
        return NAME_KO_DETAILED;
    }

    public void setNAME_KO_DETAILED(String NAME_KO_DETAILED) {
        this.NAME_KO_DETAILED = NAME_KO_DETAILED;
    }

    public String getNAME_ENG_DETAILED() {
        return NAME_ENG_DETAILED;
    }

    public void setNAME_ENG_DETAILED(String NAME_ENG_DETAILED) {
        this.NAME_ENG_DETAILED = NAME_ENG_DETAILED;
    }

    public Long getHAS_SUB_LIST() {
        return HAS_SUB_LIST;
    }

    public void setHAS_SUB_LIST(Long HAS_SUB_LIST) {
        this.HAS_SUB_LIST = HAS_SUB_LIST;
    }

    public Long getVOCA_COUNT() {
        return VOCA_COUNT;
    }

    public void setVOCA_COUNT(Long VOCA_COUNT) {
        this.VOCA_COUNT = VOCA_COUNT;
    }

    public Long getUSED() {
        return USED;
    }

    public void setUSED(Long USED) {
        this.USED = USED;
    }

    public Long getPARENT_ID() {
        return PARENT_ID;
    }

    public void setPARENT_ID(Long PARENT_ID) {
        this.PARENT_ID = PARENT_ID;
    }

    public String getID_LIST() {
        return ID_LIST;
    }

    public void setID_LIST(String ID_LIST) {
        this.ID_LIST = ID_LIST;
    }

    public Long getVERSION() {
        return VERSION;
    }

    public void setVERSION(Long VERSION) {
        this.VERSION = VERSION;
    }

    public String getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATE_DATE(String CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public Long getLANG_STUDY() {
        return LANG_STUDY;
    }

    public void setLANG_STUDY(Long LANG_STUDY) {
        this.LANG_STUDY = LANG_STUDY;
    }

    public Long getIMAGE_ID() {
        return IMAGE_ID;
    }

    public void setIMAGE_ID(Long IMAGE_ID) {
        this.IMAGE_ID = IMAGE_ID;
    }

    public String getHANJA_JP() {
        return HANJA_JP;
    }

    public void setHANJA_JP(String HANJA_JP) {
        this.HANJA_JP = HANJA_JP;
    }

    public String getNAME_JP() {
        return NAME_JP;
    }

    public void setNAME_JP(String NAME_JP) {
        this.NAME_JP = NAME_JP;
    }

    public String getNAME_CH_S() {
        return NAME_CH_S;
    }

    public void setNAME_CH_S(String NAME_CH_S) {
        this.NAME_CH_S = NAME_CH_S;
    }

    public String getHANJA_CH_S() {
        return HANJA_CH_S;
    }

    public void setHANJA_CH_S(String HANJA_CH_S) {
        this.HANJA_CH_S = HANJA_CH_S;
    }

    public String getNAME_CH_T() {
        return NAME_CH_T;
    }

    public void setNAME_CH_T(String NAME_CH_T) {
        this.NAME_CH_T = NAME_CH_T;
    }

    public String getHANJA_CH_T() {
        return HANJA_CH_T;
    }

    public void setHANJA_CH_T(String HANJA_CH_T) {
        this.HANJA_CH_T = HANJA_CH_T;
    }

    public String getNAME_JP_DETAILED() {
        return NAME_JP_DETAILED;
    }

    public void setNAME_JP_DETAILED(String NAME_JP_DETAILED) {
        this.NAME_JP_DETAILED = NAME_JP_DETAILED;
    }

    public String getNAME_CH_S_DETAILED() {
        return NAME_CH_S_DETAILED;
    }

    public void setNAME_CH_S_DETAILED(String NAME_CH_S_DETAILED) {
        this.NAME_CH_S_DETAILED = NAME_CH_S_DETAILED;
    }

    public String getNAME_CH_T_DETAILED() {
        return NAME_CH_T_DETAILED;
    }

    public void setNAME_CH_T_DETAILED(String NAME_CH_T_DETAILED) {
        this.NAME_CH_T_DETAILED = NAME_CH_T_DETAILED;
    }

    public String getName(Context context) {
        String result = NAME_KO;
        if (EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getMenuLanguage()) != EnumLanguage.KOREAN) {
            result = NAME_ENG;
        }
        return result;
    }

    public String getNameDetailed(Context context) {
        String result = NAME_KO_DETAILED;
        if (EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getMenuLanguage()) != EnumLanguage.KOREAN) {
            result = NAME_ENG_DETAILED;
        }
        return result;
    }

}