package com.dalread.model;

import com.dalread.base.EnumLanguage;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class TBL_MESSAGE extends RealmObject {

    private String MEANING_PL;
    private String MEANING_RO;
    private String MEANING_HE;
    private String MEANING_DA;
    private String MEANING_CS;
    private String MEANING_AR;
    private String MEANING_ES;
    private String MEANING_TH;
    private String MEANING_VI;
    private String MEANING_EL;
    private String MEANING_IT;
    @PrimaryKey
    private String ID;
    private String MEANING_KO;
    private String MEANING_SV;
    private String MEANING_SK;
    private String WORD;
    private String MEANING_UK;
    private int USE_STUDYMODE;
    private int STUDY_ROLE;
    private String MEANING_ID;
    private String MEANING_CH_S;
    private String MEANING_CH_T;
    private String MEANING_JP;
    private String MEANING_BN;
    private String MEANING_FR;
    private String MEANING_HU;
    private String MEANING_HR;
    private String MEANING_NO;
    private String MEANING_HI;
    private String MEANING_DE;
    private String MEANING_ENG;
    private String MEANING_TR;
    private String MEANING_NL;
    private String USER_ROLE;
    private String MEANING_PT;
    private int DISP_ORDER_STUDYMODE;
    private String MEANING_FI;
    private String MEANING_RU;
    @Ignore
    private String meaning;
    @Ignore
    private String meaningStudy;


    // Getter Methods

    public String getMEANING_PL() {
        return MEANING_PL;
    }

    public String getMEANING_RO() {
        return MEANING_RO;
    }

    public String getMEANING_HE() {
        return MEANING_HE;
    }

    public String getMEANING_DA() {
        return MEANING_DA;
    }

    public String getMEANING_CS() {
        return MEANING_CS;
    }

    public String getMEANING_AR() {
        return MEANING_AR;
    }

    public String getMEANING_ES() {
        return MEANING_ES;
    }

    public String getMEANING_TH() {
        return MEANING_TH;
    }

    public String getMEANING_VI() {
        return MEANING_VI;
    }

    public String getMEANING_EL() {
        return MEANING_EL;
    }

    public String getMEANING_IT() {
        return MEANING_IT;
    }

    public String getID() {
        return ID;
    }

    public String getMEANING_KO() {
        return MEANING_KO;
    }

    public String getMEANING_SV() {
        return MEANING_SV;
    }

    public String getMEANING_SK() {
        return MEANING_SK;
    }

    public String getWORD() {
        return WORD;
    }

    public String getMEANING_UK() {
        return MEANING_UK;
    }

    public int getUSE_STUDYMODE() {
        return USE_STUDYMODE;
    }

    public int getSTUDY_ROLE() {
        return STUDY_ROLE;
    }

    public String getMEANING_ID() {
        return MEANING_ID;
    }

    public String getMEANING_CH_S() {
        return MEANING_CH_S;
    }

    public String getMEANING_CH_T() {
        return MEANING_CH_T;
    }

    public String getMEANING_JP() {
        return MEANING_JP;
    }

    public String getMEANING_BN() {
        return MEANING_BN;
    }

    public String getMEANING_FR() {
        return MEANING_FR;
    }

    public String getMEANING_HU() {
        return MEANING_HU;
    }

    public String getMEANING_HR() {
        return MEANING_HR;
    }

    public String getMEANING_NO() {
        return MEANING_NO;
    }

    public String getMEANING_HI() {
        return MEANING_HI;
    }

    public String getMEANING_DE() {
        return MEANING_DE;
    }

    public String getMEANING_ENG() {
        return MEANING_ENG;
    }

    public String getMEANING_TR() {
        return MEANING_TR;
    }

    public String getMEANING_NL() {
        return MEANING_NL;
    }

    public String getUSER_ROLE() {
        return USER_ROLE;
    }

    public String getMEANING_PT() {
        return MEANING_PT;
    }

    public int getDISP_ORDER_STUDYMODE() {
        return DISP_ORDER_STUDYMODE;
    }

    public String getMEANING_FI() {
        return MEANING_FI;
    }

    public String getMEANING_RU() {
        return MEANING_RU;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getMeaningStudy() {
        return meaningStudy;
    }

    // Setter Methods

    public void setMEANING_PL(String MEANING_PL) {
        this.MEANING_PL = MEANING_PL;
    }

    public void setMEANING_RO(String MEANING_RO) {
        this.MEANING_RO = MEANING_RO;
    }

    public void setMEANING_HE(String MEANING_HE) {
        this.MEANING_HE = MEANING_HE;
    }

    public void setMEANING_DA(String MEANING_DA) {
        this.MEANING_DA = MEANING_DA;
    }

    public void setMEANING_CS(String MEANING_CS) {
        this.MEANING_CS = MEANING_CS;
    }

    public void setMEANING_AR(String MEANING_AR) {
        this.MEANING_AR = MEANING_AR;
    }

    public void setMEANING_ES(String MEANING_ES) {
        this.MEANING_ES = MEANING_ES;
    }

    public void setMEANING_TH(String MEANING_TH) {
        this.MEANING_TH = MEANING_TH;
    }

    public void setMEANING_VI(String MEANING_VI) {
        this.MEANING_VI = MEANING_VI;
    }

    public void setMEANING_EL(String MEANING_EL) {
        this.MEANING_EL = MEANING_EL;
    }

    public void setMEANING_IT(String MEANING_IT) {
        this.MEANING_IT = MEANING_IT;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setMEANING_KO(String MEANING_KO) {
        this.MEANING_KO = MEANING_KO;
    }

    public void setMEANING_SV(String MEANING_SV) {
        this.MEANING_SV = MEANING_SV;
    }

    public void setMEANING_SK(String MEANING_SK) {
        this.MEANING_SK = MEANING_SK;
    }

    public void setWORD(String WORD) {
        this.WORD = WORD;
    }

    public void setMEANING_UK(String MEANING_UK) {
        this.MEANING_UK = MEANING_UK;
    }

    public void setUSE_STUDYMODE(int USE_STUDYMODE) {
        this.USE_STUDYMODE = USE_STUDYMODE;
    }

    public void setSTUDY_ROLE(int STUDY_ROLE) {
        this.USE_STUDYMODE = STUDY_ROLE;
    }

    public void setMEANING_ID(String MEANING_ID) {
        this.MEANING_ID = MEANING_ID;
    }

    public void setMEANING_CH_S(String MEANING_CH_S) {
        this.MEANING_CH_S = MEANING_CH_S;
    }

    public void setMEANING_CH_T(String MEANING_CH_T) {
        this.MEANING_CH_T = MEANING_CH_T;
    }

    public void setMEANING_JP(String MEANING_JP) {
        this.MEANING_JP = MEANING_JP;
    }

    public void setMEANING_BN(String MEANING_BN) {
        this.MEANING_BN = MEANING_BN;
    }

    public void setMEANING_FR(String MEANING_FR) {
        this.MEANING_FR = MEANING_FR;
    }

    public void setMEANING_HU(String MEANING_HU) {
        this.MEANING_HU = MEANING_HU;
    }

    public void setMEANING_HR(String MEANING_HR) {
        this.MEANING_HR = MEANING_HR;
    }

    public void setMEANING_NO(String MEANING_NO) {
        this.MEANING_NO = MEANING_NO;
    }

    public void setMEANING_HI(String MEANING_HI) {
        this.MEANING_HI = MEANING_HI;
    }

    public void setMEANING_DE(String MEANING_DE) {
        this.MEANING_DE = MEANING_DE;
    }

    public void setMEANING_ENG(String MEANING_ENG) {
        this.MEANING_ENG = MEANING_ENG;
    }

    public void setMEANING_TR(String MEANING_TR) {
        this.MEANING_TR = MEANING_TR;
    }

    public void setMEANING_NL(String MEANING_NL) {
        this.MEANING_NL = MEANING_NL;
    }

    public void setUSER_ROLE(String USER_ROLE) {
        this.USER_ROLE = USER_ROLE;
    }

    public void setMEANING_PT(String MEANING_PT) {
        this.MEANING_PT = MEANING_PT;
    }

    public void setDISP_ORDER_STUDYMODE(int DISP_ORDER_STUDYMODE) {
        this.DISP_ORDER_STUDYMODE = DISP_ORDER_STUDYMODE;
    }

    public void setMEANING_FI(String MEANING_FI) {
        this.MEANING_FI = MEANING_FI;
    }

    public void setMEANING_RU(String MEANING_RU) {
        this.MEANING_RU = MEANING_RU;
    }

    public String getMeaning(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case ARABIC:
                return meaning = getMEANING_AR();
            case BENGALI:
                return meaning = getMEANING_BN();
            case CHINESE_SIMPLIFIED:
                return meaning = getMEANING_CH_S();
            case CHINESE_TRADITIONAL:
                return meaning = getMEANING_CH_T();
            case CZECH:
                return meaning = getMEANING_CS();
            case DANISH:
                return meaning = getMEANING_DA();
            case GERMAN:
                return meaning = getMEANING_DE();
            case GREEK:
                return meaning = getMEANING_EL();
            case SPANISH:
                return meaning = getMEANING_ES();
            case FINNISH:
                return meaning = getMEANING_FI();
            case FRENCH:
                return meaning = getMEANING_FR();
            case HANJA:
            case KOREAN:
                return meaning = getMEANING_KO();
            case HEBREW:
                return meaning = getMEANING_HE();
            case HINDI:
                return meaning = getMEANING_HI();
            case HUNGARIAN:
                return meaning = getMEANING_HU();
            case INDONESIAN:
                return meaning = getMEANING_ID();
            case ITALIAN:
                return meaning = getMEANING_IT();
            case JAPANESE:
                return meaning = getMEANING_JP();
            case DUTCH:
                return meaning = getMEANING_NL();
            case NORWEGIAN:
                return meaning = getMEANING_NO();
            case POLISH:
                return meaning = getMEANING_PL();
            case PORTUGUESE:
                return meaning = getMEANING_PT();
            case ROMANIAN:
                return meaning = getMEANING_RO();
            case RUSSIAN:
                return meaning = getMEANING_RU();
            case SLOVAK:
                return meaning = getMEANING_SK();
            case SWEDISH:
                return meaning = getMEANING_SV();
            case THAI:
                return meaning = getMEANING_TH();
            case TURKISH:
                return meaning = getMEANING_TR();
//            case UKRAINIAN:
//                return meaning = getMEANING_UK();
            case VIETNAMESE:
                return meaning = getMEANING_VI();
            default:
                return meaning = getMEANING_ENG();
        }
    }

    public void setMeaningStudy(String meaningStudy) {
        this.meaningStudy = meaningStudy;
    }

    public String generateMeaningContent() {
        if (meaning.equals(meaningStudy)) {
            return meaning;
        }
        return meaning + "\n(" + meaningStudy + ")";
    }
}
