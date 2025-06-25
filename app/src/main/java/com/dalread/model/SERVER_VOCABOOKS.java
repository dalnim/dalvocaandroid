package com.dalread.model;

import com.dalread.base.EnumLanguage;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SERVER_VOCABOOKS extends RealmObject {

    private int DISP_ORDER;
    private String NAME_TH;
    private String NAME_VI;
    private String NAME_CS;
    private String NAME_AR;
    private String NAME_ES;
    private String NAME_HR;
    private String USE_TODAY_EXPRESSION;
    private String USE_VOCABOOK;
    private String NAME_JP;
    private String NAME_PT;
    private String VOCA_COUNT;
    private String NAME_RU;
    private String NAME_FI;
    @PrimaryKey
    private String ID;
    private String NAME_NO;
    private String NAME_HI;
    private String NAME_DE;
    private String NAME_TR;
    private String NAME_NL;
    private String NAME_PL;
    private String NAME_RO;
    private String NAME_HE;
    private String NAME_DA;
    private String LANG_STUDY;
    private String NAME_ID;
    private String NAME_SK;
    private String USE_PRACTICE;
    private String NAME_ENG;
    private String NAME_UK;
    private String NAME_FR;
    private String NAME_BN;
    private String PARENT_ID;
    private String NAME_HU;
    private String NAME_CH_T;
    private String NAME_IT;
    private String CREATE_DATE;
    private String NAME_CH_S;
    private String NAME_EL;
    private String IMAGE_ID;
    private String NAME_KO;
    private String VERSION;
    private String NAME_SV;
    private String USE_ALPHABET;


    // Getter Methods

    public int getDISP_ORDER() {
        return DISP_ORDER;
    }

    public String getNAME_TH() {
        return NAME_TH;
    }

    public String getNAME_VI() {
        return NAME_VI;
    }

    public String getNAME_CS() {
        return NAME_CS;
    }

    public String getNAME_AR() {
        return NAME_AR;
    }

    public String getNAME_ES() {
        return NAME_ES;
    }

    public String getNAME_HR() {
        return NAME_HR;
    }

    public String getUSE_TODAY_EXPRESSION() {
        return USE_TODAY_EXPRESSION;
    }

    public String getUSE_VOCABOOK() {
        return USE_VOCABOOK;
    }

    public String getNAME_JP() {
        return NAME_JP;
    }

    public String getNAME_PT() {
        return NAME_PT;
    }

    public String getVOCA_COUNT() {
        return VOCA_COUNT;
    }

    public String getNAME_RU() {
        return NAME_RU;
    }

    public String getNAME_FI() {
        return NAME_FI;
    }

    public String getID() {
        return ID;
    }

    public String getNAME_NO() {
        return NAME_NO;
    }

    public String getNAME_HI() {
        return NAME_HI;
    }

    public String getNAME_DE() {
        return NAME_DE;
    }

    public String getNAME_TR() {
        return NAME_TR;
    }

    public String getNAME_NL() {
        return NAME_NL;
    }

    public String getNAME_PL() {
        return NAME_PL;
    }

    public String getNAME_RO() {
        return NAME_RO;
    }

    public String getNAME_HE() {
        return NAME_HE;
    }

    public String getNAME_DA() {
        return NAME_DA;
    }

    public String getLANG_STUDY() {
        return LANG_STUDY;
    }

    public String getNAME_ID() {
        return NAME_ID;
    }

    public String getNAME_SK() {
        return NAME_SK;
    }

    public String getUSE_PRACTICE() {
        return USE_PRACTICE;
    }

    public String getNAME_ENG() {
        return NAME_ENG;
    }

    public String getNAME_UK() {
        return NAME_UK;
    }

    public String getNAME_FR() {
        return NAME_FR;
    }

    public String getNAME_BN() {
        return NAME_BN;
    }

    public String getPARENT_ID() {
        return PARENT_ID;
    }

    public String getNAME_HU() {
        return NAME_HU;
    }

    public String getNAME_CH_T() {
        return NAME_CH_T;
    }

    public String getNAME_IT() {
        return NAME_IT;
    }

    public String getCREATE_DATE() {
        return CREATE_DATE;
    }

    public String getNAME_CH_S() {
        return NAME_CH_S;
    }

    public String getNAME_EL() {
        return NAME_EL;
    }

    public String getIMAGE_ID() {
        return IMAGE_ID;
    }

    public String getNAME_KO() {
        return NAME_KO;
    }

    public String getVERSION() {
        return VERSION;
    }

    public String getNAME_SV() {
        return NAME_SV;
    }

    public String getUSE_ALPHABET() {
        return USE_ALPHABET;
    }

    // Setter Methods

    public void setDISP_ORDER(int DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }

    public void setNAME_TH(String NAME_TH) {
        this.NAME_TH = NAME_TH;
    }

    public void setNAME_VI(String NAME_VI) {
        this.NAME_VI = NAME_VI;
    }

    public void setNAME_CS(String NAME_CS) {
        this.NAME_CS = NAME_CS;
    }

    public void setNAME_AR(String NAME_AR) {
        this.NAME_AR = NAME_AR;
    }

    public void setNAME_ES(String NAME_ES) {
        this.NAME_ES = NAME_ES;
    }

    public void setNAME_HR(String NAME_HR) {
        this.NAME_HR = NAME_HR;
    }

    public void setUSE_TODAY_EXPRESSION(String USE_TODAY_EXPRESSION) {
        this.USE_TODAY_EXPRESSION = USE_TODAY_EXPRESSION;
    }

    public void setUSE_VOCABOOK(String USE_VOCABOOK) {
        this.USE_VOCABOOK = USE_VOCABOOK;
    }

    public void setNAME_JP(String NAME_JP) {
        this.NAME_JP = NAME_JP;
    }

    public void setNAME_PT(String NAME_PT) {
        this.NAME_PT = NAME_PT;
    }

    public void setVOCA_COUNT(String VOCA_COUNT) {
        this.VOCA_COUNT = VOCA_COUNT;
    }

    public void setNAME_RU(String NAME_RU) {
        this.NAME_RU = NAME_RU;
    }

    public void setNAME_FI(String NAME_FI) {
        this.NAME_FI = NAME_FI;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setNAME_NO(String NAME_NO) {
        this.NAME_NO = NAME_NO;
    }

    public void setNAME_HI(String NAME_HI) {
        this.NAME_HI = NAME_HI;
    }

    public void setNAME_DE(String NAME_DE) {
        this.NAME_DE = NAME_DE;
    }

    public void setNAME_TR(String NAME_TR) {
        this.NAME_TR = NAME_TR;
    }

    public void setNAME_NL(String NAME_NL) {
        this.NAME_NL = NAME_NL;
    }

    public void setNAME_PL(String NAME_PL) {
        this.NAME_PL = NAME_PL;
    }

    public void setNAME_RO(String NAME_RO) {
        this.NAME_RO = NAME_RO;
    }

    public void setNAME_HE(String NAME_HE) {
        this.NAME_HE = NAME_HE;
    }

    public void setNAME_DA(String NAME_DA) {
        this.NAME_DA = NAME_DA;
    }

    public void setLANG_STUDY(String LANG_STUDY) {
        this.LANG_STUDY = LANG_STUDY;
    }

    public void setNAME_ID(String NAME_ID) {
        this.NAME_ID = NAME_ID;
    }

    public void setNAME_SK(String NAME_SK) {
        this.NAME_SK = NAME_SK;
    }

    public void setUSE_PRACTICE(String USE_PRACTICE) {
        this.USE_PRACTICE = USE_PRACTICE;
    }

    public void setNAME_ENG(String NAME_ENG) {
        this.NAME_ENG = NAME_ENG;
    }

    public void setNAME_UK(String NAME_UK) {
        this.NAME_UK = NAME_UK;
    }

    public void setNAME_FR(String NAME_FR) {
        this.NAME_FR = NAME_FR;
    }

    public void setNAME_BN(String NAME_BN) {
        this.NAME_BN = NAME_BN;
    }

    public void setPARENT_ID(String PARENT_ID) {
        this.PARENT_ID = PARENT_ID;
    }

    public void setNAME_HU(String NAME_HU) {
        this.NAME_HU = NAME_HU;
    }

    public void setNAME_CH_T(String NAME_CH_T) {
        this.NAME_CH_T = NAME_CH_T;
    }

    public void setNAME_IT(String NAME_IT) {
        this.NAME_IT = NAME_IT;
    }

    public void setCREATE_DATE(String CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public void setNAME_CH_S(String NAME_CH_S) {
        this.NAME_CH_S = NAME_CH_S;
    }

    public void setNAME_EL(String NAME_EL) {
        this.NAME_EL = NAME_EL;
    }

    public void setIMAGE_ID(String IMAGE_ID) {
        this.IMAGE_ID = IMAGE_ID;
    }

    public void setNAME_KO(String NAME_KO) {
        this.NAME_KO = NAME_KO;
    }

    public void setVERSION(String VERSION) {
        this.VERSION = VERSION;
    }

    public void setNAME_SV(String NAME_SV) {
        this.NAME_SV = NAME_SV;
    }

    public void setUSE_ALPHABET(String USE_ALPHABET) {
        this.USE_ALPHABET = USE_ALPHABET;
    }

    public String getName(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case ARABIC:
                return getNAME_AR();
            case BENGALI:
                return getNAME_BN();
            case CHINESE_SIMPLIFIED:
                return getNAME_CH_S();
            case CHINESE_TRADITIONAL:
                return getNAME_CH_T();
            case CZECH:
                return getNAME_CS();
            case DANISH:
                return getNAME_DA();
            case GERMAN:
                return getNAME_DE();
            case GREEK:
                return getNAME_EL();
            case SPANISH:
                return getNAME_ES();
            case FINNISH:
                return getNAME_FI();
            case FRENCH:
                return getNAME_FR();
            case HANJA:
            case KOREAN:
                return getNAME_KO();
            case HEBREW:
                return getNAME_HE();
            case HINDI:
                return getNAME_HI();
            case HUNGARIAN:
                return getNAME_HU();
            case INDONESIAN:
                return getNAME_ID();
            case ITALIAN:
                return getNAME_IT();
            case JAPANESE:
                return getNAME_JP();
            case DUTCH:
                return getNAME_NL();
            case NORWEGIAN:
                return getNAME_NO();
            case POLISH:
                return getNAME_PL();
            case PORTUGUESE:
                return getNAME_PT();
            case ROMANIAN:
                return getNAME_RO();
            case RUSSIAN:
                return getNAME_RU();
            case SLOVAK:
                return getNAME_SK();
            case SWEDISH:
                return getNAME_SV();
            case THAI:
                return getNAME_TH();
            case TURKISH:
                return getNAME_TR();
//            case UKRAINIAN:
//                return getNAME_UK();
            case VIETNAMESE:
                return getNAME_VI();
            default:
                return getNAME_ENG();
        }
    }
}
