package com.dalread.model.sql;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IServerVocabooks;
import com.dalread.util.Utils;

import java.io.Serializable;

public class SERVER_VOCABOOKS_SQL implements Serializable, IServerVocabooks {
    public long ID;
    public long PARENT_ID;
    public String ID_LIST;
    public long VOCA_COUNT;
    public long IMAGE_ID;
    public long VERSION;
    public long LANG_STUDY;
    public long DISP_ORDER;
    public long USE_RECORDING;
    public String NAME_KO;
    public String NAME_JP;
    public String NAME_CH_S;
    public String NAME_ENG;
    public String NAME_VI;
    public String NAME_FR;
    public String NAME_AR;
    public String NAME_UK;
    public String NAME_TR;
    public String NAME_TH;
    public String NAME_SV;
    public String NAME_SK;
    public String NAME_RU;
    public String NAME_RO;
    public String NAME_PL;
    public String NAME_PT;
    public String NAME_NO;
    public String NAME_NL;
    public String NAME_IT;
    public String NAME_ID;
    public String NAME_HU;
    public String NAME_HR;
    public String NAME_HI;
    public String NAME_HE;
    public String NAME_FI;
    public String NAME_ES;
    public String NAME_EL;
    public String NAME_DE;
    public String NAME_DA;
    public String NAME_CS;
    public String NAME_CH_T;
    public String NAME_BN;
    public long USE_VOCABOOK;
    public long USE_TODAY_EXPRESSION;
    public long USE_PRACTICE;
    public long USE_ALPHABET;
    public long USED;
    public long HAS_SUB_LIST;
    //Not in the TABLE
    private Long COUNT_OF_VOCA_KNOW;
    private int recordedVocaCount;

    public long getID() {
        return ID;
    }

    public long getPARENT_ID() {
        return PARENT_ID;
    }

    public String getID_LIST() {
        return ID_LIST;
    }

    public long getVOCA_COUNT() {
        return VOCA_COUNT;
    }

    public long getIMAGE_ID() {
        return IMAGE_ID;
    }

    public long getVERSION() {
        return VERSION;
    }

    public long getLANG_STUDY() {
        return LANG_STUDY;
    }

    public long getDISP_ORDER() {
        return DISP_ORDER;
    }

    public long getUSE_RECORDING() {
        return USE_RECORDING;
    }

    public String getNAME_KO() {
        return NAME_KO;
    }

    public String getNAME_JP() {
        return NAME_JP;
    }

    public String getNAME_CH_S() {
        return NAME_CH_S;
    }

    public String getNAME_ENG() {
        return NAME_ENG;
    }

    public String getNAME_VI() {
        return NAME_VI;
    }

    public String getNAME_FR() {
        return NAME_FR;
    }

    public String getNAME_AR() {
        return NAME_AR;
    }

    public String getNAME_UK() {
        return NAME_UK;
    }

    public String getNAME_TR() {
        return NAME_TR;
    }

    public String getNAME_TH() {
        return NAME_TH;
    }

    public String getNAME_SV() {
        return NAME_SV;
    }

    public String getNAME_SK() {
        return NAME_SK;
    }

    public String getNAME_RU() {
        return NAME_RU;
    }

    public String getNAME_RO() {
        return NAME_RO;
    }

    public String getNAME_PL() {
        return NAME_PL;
    }

    public String getNAME_PT() {
        return NAME_PT;
    }

    public String getNAME_NO() {
        return NAME_NO;
    }

    public String getNAME_NL() {
        return NAME_NL;
    }

    public String getNAME_IT() {
        return NAME_IT;
    }

    public String getNAME_ID() {
        return NAME_ID;
    }

    public String getNAME_HU() {
        return NAME_HU;
    }

    public String getNAME_HR() {
        return NAME_HR;
    }

    public String getNAME_HI() {
        return NAME_HI;
    }

    public String getNAME_HE() {
        return NAME_HE;
    }

    public String getNAME_FI() {
        return NAME_FI;
    }

    public String getNAME_ES() {
        return NAME_ES;
    }

    public String getNAME_EL() {
        return NAME_EL;
    }

    public String getNAME_DE() {
        return NAME_DE;
    }

    public String getNAME_DA() {
        return NAME_DA;
    }

    public String getNAME_CS() {
        return NAME_CS;
    }

    public String getNAME_CH_T() {
        return NAME_CH_T;
    }

    public String getNAME_BN() {
        return NAME_BN;
    }

    public long getUSE_VOCABOOK() {
        return USE_VOCABOOK;
    }

    public long getUSE_TODAY_EXPRESSION() {
        return USE_TODAY_EXPRESSION;
    }

    public long getUSE_PRACTICE() {
        return USE_PRACTICE;
    }

    public long getUSE_ALPHABET() {
        return USE_ALPHABET;
    }

    public long getUSED() {
        return USED;
    }

    public long getHAS_SUB_LIST() {
        return HAS_SUB_LIST;
    }

    public void setCOUNT_OF_VOCA_KNOW(Long COUNT_OF_VOCA_KNOW) {
        this.COUNT_OF_VOCA_KNOW = COUNT_OF_VOCA_KNOW;
    }

    public Long getCOUNT_OF_VOCA_KNOW() {
        return COUNT_OF_VOCA_KNOW;
    }

    @Override
    public long getIBookId() {
        return getID();
    }

    @Override
    public long getIBookParentId() {
        return getPARENT_ID();
    }

    @Override
    public long getIBookVocaCount() {
        return getVOCA_COUNT();
    }

    @Override
    public long getIBookImageId() {
        return getIMAGE_ID();
    }

    @Override
    public long getIBookVersion() {
        return getVERSION();
    }

    @Override
    public long getIBookLangStudy() {
        return getLANG_STUDY();
    }

    @Override
    public long getIBookDispOrder() {
        return getDISP_ORDER();
    }

    @Override
    public long getIBookUseRecording() {
        return getUSE_RECORDING();
    }

    @Override
    public String getIBookName(EnumLanguage enumLanguage) {
        String result = getNAME_ENG();
        switch (enumLanguage) {
            case ARABIC:
                result = getNAME_AR();
                break;
            case BENGALI:
                result = getNAME_BN();
                break;
            case CHINESE_SIMPLIFIED:
                result = getNAME_CH_S();
                break;
            case CHINESE_TRADITIONAL:
                result = getNAME_CH_T();
                break;
            case CZECH:
                result = getNAME_CS();
                break;
            case DANISH:
                result = getNAME_DA();
                break;
            case GERMAN:
                result = getNAME_DE();
                break;
            case GREEK:
                result = getNAME_EL();
                break;
            case SPANISH:
                result = getNAME_ES();
                break;
            case FINNISH:
                result = getNAME_FI();
                break;
            case FRENCH:
                result = getNAME_FR();
                break;
            case KOREAN:
                result = getNAME_KO();
                break;
            case HEBREW:
                result = getNAME_HE();
                break;
            case HINDI:
                result = getNAME_HI();
                break;
            case HUNGARIAN:
                result = getNAME_HU();
                break;
            case INDONESIAN:
                result = getNAME_ID();
                break;
            case ITALIAN:
                result = getNAME_IT();
                break;
            case JAPANESE:
                result = getNAME_JP();
                break;
            case DUTCH:
                result = getNAME_NL();
                break;
            case NORWEGIAN:
                result = getNAME_NO();
                break;
            case POLISH:
                result = getNAME_PL();
                break;
            case PORTUGUESE:
                result = getNAME_PT();
                break;
            case ROMANIAN:
                result = getNAME_RO();
                break;
            case RUSSIAN:
                result = getNAME_RU();
                break;
            case SLOVAK:
                result = getNAME_SK();
                break;
            case SWEDISH:
                result = getNAME_SV();
                break;
            case THAI:
                result = getNAME_TH();
                break;
            case TURKISH:
                result = getNAME_TR();
                break;
//            case UKRAINIAN:
//                result = getNAME_UK();
            case VIETNAMESE:
                result = getNAME_VI();
                break;
            default:
                result = getNAME_ENG();
                break;
        }

        if (Utils.isEmpty(result) && !Utils.isEmpty(getNAME_ENG())) {
            result = "(" + getNAME_ENG() + ")";
        }
        return result;
    }
    @Override
    public String getIBookNameEng() {
        return getNAME_ENG();
    }
    @Override
    public String getIBookNameStudyLang() {
        return "";
    }
    @Override
    public long getIBookUseVocabook() {
        return getUSE_VOCABOOK();
    }

    @Override
    public long getIBookUseTodayExpression() {
        return getUSE_TODAY_EXPRESSION();
    }

    @Override
    public long getIBookUsePractice() {
        return getUSE_PRACTICE();
    }

    @Override
    public long getIBookUseAlphabet() {
        return getUSE_ALPHABET();
    }

    @Override
    public long getIBookUsed() {
        return getUSED();
    }

    @Override
    public boolean isIBookHasSubList() {
        return getHAS_SUB_LIST() > 0 ? true : false;
    }

    @Override
    public long getIBookCountOfVocaKnow() {
        return getCOUNT_OF_VOCA_KNOW();
    }

    @Override
    public long getIBookRecordedVocaCount() {
        return recordedVocaCount;
    }

    @Override
    public void setIBookRecordedVocaCount(int value) {
        this.recordedVocaCount = value;
    }
}