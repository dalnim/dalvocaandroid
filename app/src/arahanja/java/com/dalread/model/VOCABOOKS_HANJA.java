// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.dalread.model;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class VOCABOOKS_HANJA extends RealmObject implements Serializable {

    @PrimaryKey
    private Long ID;
    private String NAME_KO;
    private Long DISP_ORDER;
    private Long HAS_SUB_LIST;
    private Long VOCA_COUNT;
    private Long USED;
    private Long PARENT_ID;
    private String ID_LIST;
    private Long VERSION;
    private String CREATE_DATE;
    private Long LANG_STUDY;
    private Long IMAGE_ID;
    private String NAME_JP;
    private String NAME_CH_S;
    private String NAME_ENG;
    private String NAME_VI;
    private String NAME_FR;
    private String NAME_AR;
    private String NAME_UK;
    private String NAME_TR;
    private String NAME_TH;
    private String NAME_SV;
    private String NAME_SK;
    private String NAME_RU;
    private String NAME_RO;
    private String NAME_PL;
    private String NAME_PT;
    private String NAME_NO;
    private String NAME_NL;
    private String NAME_IT;
    private String NAME_ID;
    private String NAME_HU;
    private String NAME_HR;
    private String NAME_HI;
    private String NAME_HE;
    private String NAME_FI;
    private String NAME_ES;
    private String NAME_EL;
    private String NAME_DE;
    private String NAME_DA;
    private String NAME_CS;
    private String NAME_CH_T;
    private String NAME_BN;

    @Ignore
    private Long COUNT_OF_VOCA_KNOW;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getNAME_KO() {
        return NAME_KO;
    }

    public void setNAME_KO(String NAME_KO) {
        this.NAME_KO = NAME_KO;
    }

    public Long getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(Long DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
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

    public String getNAME_ENG() {
        return NAME_ENG;
    }

    public void setNAME_ENG(String NAME_ENG) {
        this.NAME_ENG = NAME_ENG;
    }

    public String getNAME_VI() {
        return NAME_VI;
    }

    public void setNAME_VI(String NAME_VI) {
        this.NAME_VI = NAME_VI;
    }

    public String getNAME_FR() {
        return NAME_FR;
    }

    public void setNAME_FR(String NAME_FR) {
        this.NAME_FR = NAME_FR;
    }

    public String getNAME_AR() {
        return NAME_AR;
    }

    public void setNAME_AR(String NAME_AR) {
        this.NAME_AR = NAME_AR;
    }

    public String getNAME_UK() {
        return NAME_UK;
    }

    public void setNAME_UK(String NAME_UK) {
        this.NAME_UK = NAME_UK;
    }

    public String getNAME_TR() {
        return NAME_TR;
    }

    public void setNAME_TR(String NAME_TR) {
        this.NAME_TR = NAME_TR;
    }

    public String getNAME_TH() {
        return NAME_TH;
    }

    public void setNAME_TH(String NAME_TH) {
        this.NAME_TH = NAME_TH;
    }

    public String getNAME_SV() {
        return NAME_SV;
    }

    public void setNAME_SV(String NAME_SV) {
        this.NAME_SV = NAME_SV;
    }

    public String getNAME_SK() {
        return NAME_SK;
    }

    public void setNAME_SK(String NAME_SK) {
        this.NAME_SK = NAME_SK;
    }

    public String getNAME_RU() {
        return NAME_RU;
    }

    public void setNAME_RU(String NAME_RU) {
        this.NAME_RU = NAME_RU;
    }

    public String getNAME_RO() {
        return NAME_RO;
    }

    public void setNAME_RO(String NAME_RO) {
        this.NAME_RO = NAME_RO;
    }

    public String getNAME_PL() {
        return NAME_PL;
    }

    public void setNAME_PL(String NAME_PL) {
        this.NAME_PL = NAME_PL;
    }

    public String getNAME_PT() {
        return NAME_PT;
    }

    public void setNAME_PT(String NAME_PT) {
        this.NAME_PT = NAME_PT;
    }

    public String getNAME_NO() {
        return NAME_NO;
    }

    public void setNAME_NO(String NAME_NO) {
        this.NAME_NO = NAME_NO;
    }

    public String getNAME_NL() {
        return NAME_NL;
    }

    public void setNAME_NL(String NAME_NL) {
        this.NAME_NL = NAME_NL;
    }

    public String getNAME_IT() {
        return NAME_IT;
    }

    public void setNAME_IT(String NAME_IT) {
        this.NAME_IT = NAME_IT;
    }

    public String getNAME_ID() {
        return NAME_ID;
    }

    public void setNAME_ID(String NAME_ID) {
        this.NAME_ID = NAME_ID;
    }

    public String getNAME_HU() {
        return NAME_HU;
    }

    public void setNAME_HU(String NAME_HU) {
        this.NAME_HU = NAME_HU;
    }

    public String getNAME_HR() {
        return NAME_HR;
    }

    public void setNAME_HR(String NAME_HR) {
        this.NAME_HR = NAME_HR;
    }

    public String getNAME_HI() {
        return NAME_HI;
    }

    public void setNAME_HI(String NAME_HI) {
        this.NAME_HI = NAME_HI;
    }

    public String getNAME_HE() {
        return NAME_HE;
    }

    public void setNAME_HE(String NAME_HE) {
        this.NAME_HE = NAME_HE;
    }

    public String getNAME_FI() {
        return NAME_FI;
    }

    public void setNAME_FI(String NAME_FI) {
        this.NAME_FI = NAME_FI;
    }

    public String getNAME_ES() {
        return NAME_ES;
    }

    public void setNAME_ES(String NAME_ES) {
        this.NAME_ES = NAME_ES;
    }

    public String getNAME_EL() {
        return NAME_EL;
    }

    public void setNAME_EL(String NAME_EL) {
        this.NAME_EL = NAME_EL;
    }

    public String getNAME_DE() {
        return NAME_DE;
    }

    public void setNAME_DE(String NAME_DE) {
        this.NAME_DE = NAME_DE;
    }

    public String getNAME_DA() {
        return NAME_DA;
    }

    public void setNAME_DA(String NAME_DA) {
        this.NAME_DA = NAME_DA;
    }

    public String getNAME_CS() {
        return NAME_CS;
    }

    public void setNAME_CS(String NAME_CS) {
        this.NAME_CS = NAME_CS;
    }

    public String getNAME_CH_T() {
        return NAME_CH_T;
    }

    public void setNAME_CH_T(String NAME_CH_T) {
        this.NAME_CH_T = NAME_CH_T;
    }

    public String getNAME_BN() {
        return NAME_BN;
    }

    public void setNAME_BN(String NAME_BN) {
        this.NAME_BN = NAME_BN;
    }

    public void setCOUNT_OF_VOCA_KNOW(Long COUNT_OF_VOCA_KNOW) {
        this.COUNT_OF_VOCA_KNOW = COUNT_OF_VOCA_KNOW;
    }

    public Long getCOUNT_OF_VOCA_KNOW() {
        return COUNT_OF_VOCA_KNOW;
    }

    public String getName(Context context) {
        String result = NAME_KO;
        if (EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getMenuLanguage()) != EnumLanguage.KOREAN) {
            result = NAME_ENG;
        }
        return result;
    }
}
