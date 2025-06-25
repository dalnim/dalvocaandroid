package com.dalread.database.sqlite.model;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;

import java.io.Serializable;

public class AbstractMeanings implements Serializable {
    public String MEANING_AR = "";
    public String MEANING_AR_DETAILED = "";
    public String MEANING_AR_FOR_HIDE_ALL = "";
    public String MEANING_AR_TTS = "";
    public String MEANING_BN = "";
    public String MEANING_BN_DETAILED = "";
    public String MEANING_BN_FOR_HIDE_ALL = "";
    public String MEANING_BN_TTS = "";
    public String MEANING_CH_S = "";
    public String MEANING_CH_S_DETAILED = "";
    public String MEANING_CH_S_FOR_HIDE_ALL = "";
    public String MEANING_CH_S_TTS = "";
    public String MEANING_CH_T = "";
    public String MEANING_CH_T_DETAILED = "";
    public String MEANING_CH_T_FOR_HIDE_ALL = "";
    public String MEANING_CH_T_TTS = "";
    public String MEANING_CS = "";
    public String MEANING_CS_DETAILED = "";
    public String MEANING_CS_FOR_HIDE_ALL = "";
    public String MEANING_CS_TTS = "";
    public String MEANING_DA = "";
    public String MEANING_DA_DETAILED = "";
    public String MEANING_DA_FOR_HIDE_ALL = "";
    public String MEANING_DA_TTS = "";
    public String MEANING_DE = "";
    public String MEANING_DE_DETAILED = "";
    public String MEANING_DE_FOR_HIDE_ALL = "";
    public String MEANING_DE_TTS = "";
    public String MEANING_EL = "";
    public String MEANING_EL_DETAILED = "";
    public String MEANING_EL_FOR_HIDE_ALL = "";
    public String MEANING_EL_TTS = "";
    public String MEANING_ENG = "";
    public String MEANING_ENG_DETAILED = "";
    public String MEANING_ENG_FOR_HIDE_ALL = "";
    public String MEANING_ENG_TTS = "";
    public String MEANING_ES = "";
    public String MEANING_ES_DETAILED = "";
    public String MEANING_ES_FOR_HIDE_ALL = "";
    public String MEANING_ES_TTS = "";
    public String MEANING_FI = "";
    public String MEANING_FI_DETAILED = "";
    public String MEANING_FI_FOR_HIDE_ALL = "";
    public String MEANING_FI_TTS = "";
    public String MEANING_FR = "";
    public String MEANING_FR_DETAILED = "";
    public String MEANING_FR_FOR_HIDE_ALL = "";
    public String MEANING_FR_TTS = "";
    public String MEANING_HE = "";
    public String MEANING_HE_DETAILED = "";
    public String MEANING_HE_FOR_HIDE_ALL = "";
    public String MEANING_HE_TTS = "";
    public String MEANING_HI = "";
    public String MEANING_HI_DETAILED = "";
    public String MEANING_HI_FOR_HIDE_ALL = "";
    public String MEANING_HI_TTS = "";
    public String MEANING_HR = "";
    public String MEANING_HR_DETAILED = "";
    public String MEANING_HR_FOR_HIDE_ALL = "";
    public String MEANING_HR_TTS = "";
    public String MEANING_HU = "";
    public String MEANING_HU_DETAILED = "";
    public String MEANING_HU_FOR_HIDE_ALL = "";
    public String MEANING_HU_TTS = "";
    public String MEANING_ID = "";
    public String MEANING_ID_DETAILED = "";
    public String MEANING_ID_FOR_HIDE_ALL = "";
    public String MEANING_ID_TTS = "";
    public String MEANING_IT = "";
    public String MEANING_IT_DETAILED = "";
    public String MEANING_IT_FOR_HIDE_ALL = "";
    public String MEANING_IT_TTS = "";
    public String MEANING_JP = "";
    public String MEANING_JP_DETAILED = "";
    public String MEANING_JP_FOR_HIDE_ALL = "";
    public String MEANING_JP_TTS = "";
    public String MEANING_KO = "";
    public String MEANING_KO_DETAILED = "";
    public String MEANING_KO_FOR_HIDE_ALL = "";
    public String MEANING_KO_TTS = "";
    public String MEANING_NL = "";
    public String MEANING_NL_DETAILED = "";
    public String MEANING_NL_FOR_HIDE_ALL = "";
    public String MEANING_NL_TTS = "";
    public String MEANING_NO = "";
    public String MEANING_NO_DETAILED = "";
    public String MEANING_NO_FOR_HIDE_ALL = "";
    public String MEANING_NO_TTS = "";
    public String MEANING_PL = "";
    public String MEANING_PL_DETAILED = "";
    public String MEANING_PL_FOR_HIDE_ALL = "";
    public String MEANING_PL_TTS = "";
    public String MEANING_PT = "";
    public String MEANING_PT_DETAILED = "";
    public String MEANING_PT_FOR_HIDE_ALL = "";
    public String MEANING_PT_TTS = "";
    public String MEANING_RO = "";
    public String MEANING_RO_DETAILED = "";
    public String MEANING_RO_FOR_HIDE_ALL = "";
    public String MEANING_RO_TTS = "";
    public String MEANING_RU = "";
    public String MEANING_RU_DETAILED = "";
    public String MEANING_RU_FOR_HIDE_ALL = "";
    public String MEANING_RU_TTS = "";
    public String MEANING_SK = "";
    public String MEANING_SK_DETAILED = "";
    public String MEANING_SK_FOR_HIDE_ALL = "";
    public String MEANING_SK_TTS = "";
    public String MEANING_SV = "";
    public String MEANING_SV_DETAILED = "";
    public String MEANING_SV_FOR_HIDE_ALL = "";
    public String MEANING_SV_TTS = "";
    public String MEANING_TH = "";
    public String MEANING_TH_DETAILED = "";
    public String MEANING_TH_FOR_HIDE_ALL = "";
    public String MEANING_TH_TTS = "";
    public String MEANING_TR = "";
    public String MEANING_TR_DETAILED = "";
    public String MEANING_TR_FOR_HIDE_ALL = "";
    public String MEANING_TR_TTS = "";
    public String MEANING_UK = "";
    public String MEANING_UK_DETAILED = "";
    public String MEANING_UK_FOR_HIDE_ALL = "";
    public String MEANING_UK_TTS = "";
    public String MEANING_VI = "";
    public String MEANING_VI_DETAILED = "";
    public String MEANING_VI_FOR_HIDE_ALL = "";
    public String MEANING_VI_TTS = "";

    public String getMEANINGByMenuLang(Context context) {
        return getMEANING(LanguageUtil.getMenuLanguage(context));
    }
    public String getMEANINGByMotherTongue(Context context) {
        return getMEANING(LanguageUtil.getMotherTongueLanguage(context));
    }
    public String getMEANING(EnumLanguage enumLanguage) {
        String result = "";
        switch (enumLanguage) {
            case ARABIC:
                result = getMEANING_AR();
                break;
            case BENGALI:
                result = getMEANING_BN();
                break;
            case CHINESE_SIMPLIFIED:
                result = getMEANING_CH_S();
                break;
            case CHINESE_TRADITIONAL:
                result = getMEANING_CH_T();
                break;
            case CZECH:
                result = getMEANING_CS();
                break;
            case DANISH:
                result = getMEANING_DA();
                break;
            case GERMAN:
                result = getMEANING_DE();
                break;
            case GREEK:
                result = getMEANING_EL();
                break;
            case SPANISH:
                result = getMEANING_ES();
                break;
            case FINNISH:
                result = getMEANING_FI();
                break;
            case FRENCH:
                result = getMEANING_FR();
                break;
            case KOREAN:
                result = getMEANING_KO();
                break;
            case HEBREW:
                result = getMEANING_HE();
                break;
            case HINDI:
                result = getMEANING_HI();
                break;
            case HUNGARIAN:
                result = getMEANING_HU();
                break;
            case INDONESIAN:
                result = getMEANING_ID();
                break;
            case ITALIAN:
                result = getMEANING_IT();
                break;
            case JAPANESE:
                result = getMEANING_JP();
                break;
            case DUTCH:
                result = getMEANING_NL();
                break;
            case NORWEGIAN:
                result = getMEANING_NO();
                break;
            case POLISH:
                result = getMEANING_PL();
                break;
            case PORTUGUESE:
                result = getMEANING_PT();
                break;
            case ROMANIAN:
                result = getMEANING_RO();
                break;
            case RUSSIAN:
                result = getMEANING_RU();
                break;
            case SLOVAK:
                result = getMEANING_SK();
                break;
            case SWEDISH:
                result = getMEANING_SV();
                break;
            case THAI:
                result = getMEANING_TH();
                break;
            case TURKISH:
                result = getMEANING_TR();
                break;
            case UKRAINIAN:
                result = getMEANING_UK();
            case VIETNAMESE:
                result = getMEANING_VI();
                break;
            default:
                result = getMEANING_ENG();
                break;

        }

////        if (Utils.isEmpty(result) && !Utils.isEmpty(getMEANING_ENG())) {
////            result = "(" + getMEANING_ENG() + ")";
////        }
//        if ((result == null) || (result.isEmpty())) {
//            result = getMEANING_ENG() == null ? "" : getMEANING_ENG();
//        }
        return result == null ? "" : result;
    }
    public void setMEANINGByMenuLang(Context context, String value) {
        setMEANING(LanguageUtil.getMenuLanguage(context), value);
    }
    public void setMEANINGByMotherTongue(Context context, String value) {
        setMEANING(LanguageUtil.getMotherTongueLanguage(context), value);
    }
    public void setMEANING(EnumLanguage enumLanguage, String value) {
        switch (enumLanguage) {
            case ARABIC:
                setMEANING_AR(value);
                break;
            case BENGALI:
                setMEANING_BN(value);
                break;
            case CHINESE_SIMPLIFIED:
                setMEANING_CH_S(value);
                break;
            case CHINESE_TRADITIONAL:
                setMEANING_CH_T(value);
                break;
            case CZECH:
                setMEANING_CS(value);
                break;
            case DANISH:
                setMEANING_DA(value);
                break;
            case GERMAN:
                setMEANING_DE(value);
                break;
            case GREEK:
                setMEANING_EL(value);
                break;
            case SPANISH:
                setMEANING_ES(value);
                break;
            case FINNISH:
                setMEANING_FI(value);
                break;
            case FRENCH:
                setMEANING_FR(value);
                break;
            case KOREAN:
                setMEANING_KO(value);
                break;
            case HEBREW:
                setMEANING_HE(value);
                break;
            case HINDI:
                setMEANING_HI(value);
                break;
            case HUNGARIAN:
                setMEANING_HU(value);
                break;
            case INDONESIAN:
                setMEANING_ID(value);
                break;
            case ITALIAN:
                setMEANING_IT(value);
                break;
            case JAPANESE:
                setMEANING_JP(value);
                break;
            case DUTCH:
                setMEANING_NL(value);
                break;
            case NORWEGIAN:
                setMEANING_NO(value);
                break;
            case POLISH:
                setMEANING_PL(value);
                break;
            case PORTUGUESE:
                setMEANING_PT(value);
                break;
            case ROMANIAN:
                setMEANING_RO(value);
                break;
            case RUSSIAN:
                setMEANING_RU(value);
                break;
            case SLOVAK:
                setMEANING_SK(value);
                break;
            case SWEDISH:
                setMEANING_SV(value);
                break;
            case THAI:
                setMEANING_TH(value);
                break;
            case TURKISH:
                setMEANING_TR(value);
                break;
            case UKRAINIAN:
                setMEANING_UK(value);
            case VIETNAMESE:
                setMEANING_VI(value);
                break;
            default:
                setMEANING_ENG(value);
                break;
        }
    }
    public String getMEANING_DETAILED(EnumLanguage enumLanguage) {
        String result = "";
        switch (enumLanguage) {
            case ARABIC:
                result = getMEANING_AR_DETAILED();
                break;
            case BENGALI:
                result = getMEANING_BN_DETAILED();
                break;
            case CHINESE_SIMPLIFIED:
                result = getMEANING_CH_S_DETAILED();
                break;
            case CHINESE_TRADITIONAL:
                result = getMEANING_CH_T_DETAILED();
                break;
            case CZECH:
                result = getMEANING_CS_DETAILED();
                break;
            case DANISH:
                result = getMEANING_DA_DETAILED();
                break;
            case GERMAN:
                result = getMEANING_DE_DETAILED();
                break;
            case GREEK:
                result = getMEANING_EL_DETAILED();
                break;
            case SPANISH:
                result = getMEANING_ES_DETAILED();
                break;
            case FINNISH:
                result = getMEANING_FI_DETAILED();
                break;
            case FRENCH:
                result = getMEANING_FR_DETAILED();
                break;
            case KOREAN:
                result = getMEANING_KO_DETAILED();
                break;
            case HEBREW:
                result = getMEANING_HE_DETAILED();
                break;
            case HINDI:
                result = getMEANING_HI_DETAILED();
                break;
            case HUNGARIAN:
                result = getMEANING_HU_DETAILED();
                break;
            case INDONESIAN:
                result = getMEANING_ID_DETAILED();
                break;
            case ITALIAN:
                result = getMEANING_IT_DETAILED();
                break;
            case JAPANESE:
                result = getMEANING_JP_DETAILED();
                break;
            case DUTCH:
                result = getMEANING_NL_DETAILED();
                break;
            case NORWEGIAN:
                result = getMEANING_NO_DETAILED();
                break;
            case POLISH:
                result = getMEANING_PL_DETAILED();
                break;
            case PORTUGUESE:
                result = getMEANING_PT_DETAILED();
                break;
            case ROMANIAN:
                result = getMEANING_RO_DETAILED();
                break;
            case RUSSIAN:
                result = getMEANING_RU_DETAILED();
                break;
            case SLOVAK:
                result = getMEANING_SK_DETAILED();
                break;
            case SWEDISH:
                result = getMEANING_SV_DETAILED();
                break;
            case THAI:
                result = getMEANING_TH_DETAILED();
                break;
            case TURKISH:
                result = getMEANING_TR_DETAILED();
                break;
            case UKRAINIAN:
                result = getMEANING_UK_DETAILED();
            case VIETNAMESE:
                result = getMEANING_VI_DETAILED();
                break;
            default:
                result = getMEANING_ENG_DETAILED();
                break;

        }

////        if (Utils.isEmpty(result) && !Utils.isEmpty(getMEANING_ENG_DETAILED())) {
////            result = "(" + getMEANING_ENG_DETAILED() + ")";
////        }
//        if ((result == null) || (result.isEmpty())) {
//            result = getMEANING_ENG_DETAILED() == null ? "" : getMEANING_ENG_DETAILED();
//        }
        return result == null ? "" : result;
    }

    public void setMEANING_DETAILED(EnumLanguage enumLanguage, String value) {
        switch (enumLanguage) {
            case ARABIC:
                setMEANING_AR_DETAILED(value);
                break;
            case BENGALI:
                setMEANING_BN_DETAILED(value);
                break;
            case CHINESE_SIMPLIFIED:
                setMEANING_CH_S_DETAILED(value);
                break;
            case CHINESE_TRADITIONAL:
                setMEANING_CH_T_DETAILED(value);
                break;
            case CZECH:
                setMEANING_CS_DETAILED(value);
                break;
            case DANISH:
                setMEANING_DA_DETAILED(value);
                break;
            case GERMAN:
                setMEANING_DE_DETAILED(value);
                break;
            case GREEK:
                setMEANING_EL_DETAILED(value);
                break;
            case SPANISH:
                setMEANING_ES_DETAILED(value);
                break;
            case FINNISH:
                setMEANING_FI_DETAILED(value);
                break;
            case FRENCH:
                setMEANING_FR_DETAILED(value);
                break;
            case KOREAN:
                setMEANING_KO_DETAILED(value);
                break;
            case HEBREW:
                setMEANING_HE_DETAILED(value);
                break;
            case HINDI:
                setMEANING_HI_DETAILED(value);
                break;
            case HUNGARIAN:
                setMEANING_HU_DETAILED(value);
                break;
            case INDONESIAN:
                setMEANING_ID_DETAILED(value);
                break;
            case ITALIAN:
                setMEANING_IT_DETAILED(value);
                break;
            case JAPANESE:
                setMEANING_JP_DETAILED(value);
                break;
            case DUTCH:
                setMEANING_NL_DETAILED(value);
                break;
            case NORWEGIAN:
                setMEANING_NO_DETAILED(value);
                break;
            case POLISH:
                setMEANING_PL_DETAILED(value);
                break;
            case PORTUGUESE:
                setMEANING_PT_DETAILED(value);
                break;
            case ROMANIAN:
                setMEANING_RO_DETAILED(value);
                break;
            case RUSSIAN:
                setMEANING_RU_DETAILED(value);
                break;
            case SLOVAK:
                setMEANING_SK_DETAILED(value);
                break;
            case SWEDISH:
                setMEANING_SV_DETAILED(value);
                break;
            case THAI:
                setMEANING_TH_DETAILED(value);
                break;
            case TURKISH:
                setMEANING_TR_DETAILED(value);
                break;
            case UKRAINIAN:
                setMEANING_UK_DETAILED(value);
            case VIETNAMESE:
                setMEANING_VI_DETAILED(value);
                break;
            default:
                setMEANING_ENG_DETAILED(value);
                break;
        }
    }

    public String getMEANING_TTS(EnumLanguage enumLanguage) {
        String result = "";
        switch (enumLanguage) {
            case ARABIC:
                result = Utils.isEmpty(getMEANING_AR_TTS()) ? getMEANING_AR() : getMEANING_AR_TTS();
                break;
            case BENGALI:
                result = Utils.isEmpty(getMEANING_BN_TTS()) ? getMEANING_BN() : getMEANING_BN_TTS();
                break;
            case CHINESE_SIMPLIFIED:
                result = Utils.isEmpty(getMEANING_CH_S_TTS()) ? getMEANING_CH_S() : getMEANING_CH_S_TTS();
                break;
            case CHINESE_TRADITIONAL:
                result = Utils.isEmpty(getMEANING_CH_T_TTS()) ? getMEANING_CH_T() : getMEANING_CH_T_TTS();
                break;
            case CZECH:
                result = Utils.isEmpty(getMEANING_CS_TTS()) ? getMEANING_CS() : getMEANING_CS_TTS();
                break;
            case DANISH:
                result = Utils.isEmpty(getMEANING_DA_TTS()) ? getMEANING_DA() : getMEANING_DA_TTS();
                break;
            case GERMAN:
                result = Utils.isEmpty(getMEANING_DE_TTS()) ? getMEANING_DE() : getMEANING_DE_TTS();
                break;
            case GREEK:
                result = Utils.isEmpty(getMEANING_EL_TTS()) ? getMEANING_EL() : getMEANING_EL_TTS();
                break;
            case SPANISH:
                result = Utils.isEmpty(getMEANING_ES_TTS()) ? getMEANING_ES() : getMEANING_ES_TTS();
                break;
            case FINNISH:
                result = Utils.isEmpty(getMEANING_FI_TTS()) ? getMEANING_FI() : getMEANING_FI_TTS();
                break;
            case FRENCH:
                result = Utils.isEmpty(getMEANING_FR_TTS()) ? getMEANING_FR() : getMEANING_FR_TTS();
                break;
            case KOREAN:
                result = Utils.isEmpty(getMEANING_KO_TTS()) ? getMEANING_KO() : getMEANING_KO_TTS();
                break;
            case HEBREW:
                result = Utils.isEmpty(getMEANING_HE_TTS()) ? getMEANING_HE() : getMEANING_HE_TTS();
                break;
            case HINDI:
                result = Utils.isEmpty(getMEANING_HI_TTS()) ? getMEANING_HI() : getMEANING_HI_TTS();
                break;
            case HUNGARIAN:
                result = Utils.isEmpty(getMEANING_HU_TTS()) ? getMEANING_HU() : getMEANING_HU_TTS();
                break;
            case INDONESIAN:
                result = Utils.isEmpty(getMEANING_ID_TTS()) ? getMEANING_ID() : getMEANING_ID_TTS();
                break;
            case ITALIAN:
                result = Utils.isEmpty(getMEANING_IT_TTS()) ? getMEANING_IT() : getMEANING_IT_TTS();
                break;
            case JAPANESE:
                result = Utils.isEmpty(getMEANING_JP_TTS()) ? getMEANING_JP() : getMEANING_JP_TTS();
                break;
            case DUTCH:
                result = Utils.isEmpty(getMEANING_NL_TTS()) ? getMEANING_NL() : getMEANING_NL_TTS();
                break;
            case NORWEGIAN:
                result = Utils.isEmpty(getMEANING_NO_TTS()) ? getMEANING_NO() : getMEANING_NO_TTS();
                break;
            case POLISH:
                result = Utils.isEmpty(getMEANING_PL_TTS()) ? getMEANING_PL() : getMEANING_PL_TTS();
                break;
            case PORTUGUESE:
                result = Utils.isEmpty(getMEANING_PT_TTS()) ? getMEANING_PT() : getMEANING_PT_TTS();
                break;
            case ROMANIAN:
                result = Utils.isEmpty(getMEANING_RO_TTS()) ? getMEANING_RO() : getMEANING_RO_TTS();
                break;
            case RUSSIAN:
                result = Utils.isEmpty(getMEANING_RU_TTS()) ? getMEANING_RU() : getMEANING_RU_TTS();
                break;
            case SLOVAK:
                result = Utils.isEmpty(getMEANING_SK_TTS()) ? getMEANING_SK() : getMEANING_SK_TTS();
                break;
            case SWEDISH:
                result = Utils.isEmpty(getMEANING_SV_TTS()) ? getMEANING_SV() : getMEANING_SV_TTS();
                break;
            case THAI:
                result = Utils.isEmpty(getMEANING_TH_TTS()) ? getMEANING_TH() : getMEANING_TH_TTS();
                break;
            case TURKISH:
                result = Utils.isEmpty(getMEANING_TR_TTS()) ? getMEANING_TR() : getMEANING_TR_TTS();
                break;
            case UKRAINIAN:
                result = getMEANING_UK();
            case VIETNAMESE:
                result = Utils.isEmpty(getMEANING_VI_TTS()) ? getMEANING_VI() : getMEANING_VI_TTS();
                break;
            default:
                result = Utils.isEmpty(getMEANING_ENG_TTS()) ? getMEANING_ENG() : getMEANING_ENG_TTS();
                break;

        }

//        if (Utils.isEmpty(result) && !Utils.isEmpty(getMEANING_ENG_TTS())) {
//            result = "(" + getMEANING_ENG() + ")";
//        }
//        if ((result == null) || (result.isEmpty())) {
//            result = getMEANING_ENG_TTS() == null ? getMEANING_ENG() : getMEANING_ENG_TTS();
//        }
        return result == null ? "" : result;
    }

    public void setMEANING_TTS(EnumLanguage enumLanguage, String value) {
        switch (enumLanguage) {
            case ARABIC:
                setMEANING_AR_TTS(value);
                break;
            case BENGALI:
                setMEANING_BN_TTS(value);
                break;
            case CHINESE_SIMPLIFIED:
                setMEANING_CH_S_TTS(value);
                break;
            case CHINESE_TRADITIONAL:
                setMEANING_CH_T_TTS(value);
                break;
            case CZECH:
                setMEANING_CS_TTS(value);
                break;
            case DANISH:
                setMEANING_DA_TTS(value);
                break;
            case GERMAN:
                setMEANING_DE_TTS(value);
                break;
            case GREEK:
                setMEANING_EL_TTS(value);
                break;
            case SPANISH:
                setMEANING_ES_TTS(value);
                break;
            case FINNISH:
                setMEANING_FI_TTS(value);
                break;
            case FRENCH:
                setMEANING_FR_TTS(value);
                break;
            case KOREAN:
                setMEANING_KO_TTS(value);
                break;
            case HEBREW:
                setMEANING_HE_TTS(value);
                break;
            case HINDI:
                setMEANING_HI_TTS(value);
                break;
            case HUNGARIAN:
                setMEANING_HU_TTS(value);
                break;
            case INDONESIAN:
                setMEANING_ID_TTS(value);
                break;
            case ITALIAN:
                setMEANING_IT_TTS(value);
                break;
            case JAPANESE:
                setMEANING_JP_TTS(value);
                break;
            case DUTCH:
                setMEANING_NL_TTS(value);
                break;
            case NORWEGIAN:
                setMEANING_NO_TTS(value);
                break;
            case POLISH:
                setMEANING_PL_TTS(value);
                break;
            case PORTUGUESE:
                setMEANING_PT_TTS(value);
                break;
            case ROMANIAN:
                setMEANING_RO_TTS(value);
                break;
            case RUSSIAN:
                setMEANING_RU_TTS(value);
                break;
            case SLOVAK:
                setMEANING_SK_TTS(value);
                break;
            case SWEDISH:
                setMEANING_SV_TTS(value);
                break;
            case THAI:
                setMEANING_TH_TTS(value);
                break;
            case TURKISH:
                setMEANING_TR_TTS(value);
                break;
            case UKRAINIAN:
                setMEANING_UK_TTS(value);
            case VIETNAMESE:
                setMEANING_VI_TTS(value);
                break;
            default:
                setMEANING_ENG_TTS(value);
                break;
        }
    }

    public String getMEANING_AR() {
        return MEANING_AR;
    }

    public void setMEANING_AR(String MEANING_AR) {
        this.MEANING_AR = MEANING_AR;
    }

    public String getMEANING_AR_DETAILED() {
        return MEANING_AR_DETAILED;
    }

    public void setMEANING_AR_DETAILED(String MEANING_AR_DETAILED) {
        this.MEANING_AR_DETAILED = MEANING_AR_DETAILED;
    }

    public String getMEANING_AR_FOR_HIDE_ALL() {
        return MEANING_AR_FOR_HIDE_ALL;
    }

    public void setMEANING_AR_FOR_HIDE_ALL(String MEANING_AR_FOR_HIDE_ALL) {
        this.MEANING_AR_FOR_HIDE_ALL = MEANING_AR_FOR_HIDE_ALL;
    }

    public String getMEANING_AR_TTS() {
        return MEANING_AR_TTS;
    }

    public void setMEANING_AR_TTS(String MEANING_AR_TTS) {
        this.MEANING_AR_TTS = MEANING_AR_TTS;
    }

    public String getMEANING_BN() {
        return MEANING_BN;
    }

    public void setMEANING_BN(String MEANING_BN) {
        this.MEANING_BN = MEANING_BN;
    }

    public String getMEANING_BN_DETAILED() {
        return MEANING_BN_DETAILED;
    }

    public void setMEANING_BN_DETAILED(String MEANING_BN_DETAILED) {
        this.MEANING_BN_DETAILED = MEANING_BN_DETAILED;
    }

    public String getMEANING_BN_FOR_HIDE_ALL() {
        return MEANING_BN_FOR_HIDE_ALL;
    }

    public void setMEANING_BN_FOR_HIDE_ALL(String MEANING_BN_FOR_HIDE_ALL) {
        this.MEANING_BN_FOR_HIDE_ALL = MEANING_BN_FOR_HIDE_ALL;
    }

    public String getMEANING_BN_TTS() {
        return MEANING_BN_TTS;
    }

    public void setMEANING_BN_TTS(String MEANING_BN_TTS) {
        this.MEANING_BN_TTS = MEANING_BN_TTS;
    }

    public String getMEANING_CH_S() {
        return MEANING_CH_S;
    }

    public void setMEANING_CH_S(String MEANING_CH_S) {
        this.MEANING_CH_S = MEANING_CH_S;
    }

    public String getMEANING_CH_S_DETAILED() {
        return MEANING_CH_S_DETAILED;
    }

    public void setMEANING_CH_S_DETAILED(String MEANING_CH_S_DETAILED) {
        this.MEANING_CH_S_DETAILED = MEANING_CH_S_DETAILED;
    }

    public String getMEANING_CH_S_FOR_HIDE_ALL() {
        return MEANING_CH_S_FOR_HIDE_ALL;
    }

    public void setMEANING_CH_S_FOR_HIDE_ALL(String MEANING_CH_S_FOR_HIDE_ALL) {
        this.MEANING_CH_S_FOR_HIDE_ALL = MEANING_CH_S_FOR_HIDE_ALL;
    }

    public String getMEANING_CH_S_TTS() {
        return MEANING_CH_S_TTS;
    }

    public void setMEANING_CH_S_TTS(String MEANING_CH_S_TTS) {
        this.MEANING_CH_S_TTS = MEANING_CH_S_TTS;
    }

    public String getMEANING_CH_T() {
        return MEANING_CH_T;
    }

    public void setMEANING_CH_T(String MEANING_CH_T) {
        this.MEANING_CH_T = MEANING_CH_T;
    }

    public String getMEANING_CH_T_DETAILED() {
        return MEANING_CH_T_DETAILED;
    }

    public void setMEANING_CH_T_DETAILED(String MEANING_CH_T_DETAILED) {
        this.MEANING_CH_T_DETAILED = MEANING_CH_T_DETAILED;
    }

    public String getMEANING_CH_T_FOR_HIDE_ALL() {
        return MEANING_CH_T_FOR_HIDE_ALL;
    }

    public void setMEANING_CH_T_FOR_HIDE_ALL(String MEANING_CH_T_FOR_HIDE_ALL) {
        this.MEANING_CH_T_FOR_HIDE_ALL = MEANING_CH_T_FOR_HIDE_ALL;
    }

    public String getMEANING_CH_T_TTS() {
        return MEANING_CH_T_TTS;
    }

    public void setMEANING_CH_T_TTS(String MEANING_CH_T_TTS) {
        this.MEANING_CH_T_TTS = MEANING_CH_T_TTS;
    }

    public String getMEANING_CS() {
        return MEANING_CS;
    }

    public void setMEANING_CS(String MEANING_CS) {
        this.MEANING_CS = MEANING_CS;
    }

    public String getMEANING_CS_DETAILED() {
        return MEANING_CS_DETAILED;
    }

    public void setMEANING_CS_DETAILED(String MEANING_CS_DETAILED) {
        this.MEANING_CS_DETAILED = MEANING_CS_DETAILED;
    }

    public String getMEANING_CS_FOR_HIDE_ALL() {
        return MEANING_CS_FOR_HIDE_ALL;
    }

    public void setMEANING_CS_FOR_HIDE_ALL(String MEANING_CS_FOR_HIDE_ALL) {
        this.MEANING_CS_FOR_HIDE_ALL = MEANING_CS_FOR_HIDE_ALL;
    }

    public String getMEANING_CS_TTS() {
        return MEANING_CS_TTS;
    }

    public void setMEANING_CS_TTS(String MEANING_CS_TTS) {
        this.MEANING_CS_TTS = MEANING_CS_TTS;
    }

    public String getMEANING_DA() {
        return MEANING_DA;
    }

    public void setMEANING_DA(String MEANING_DA) {
        this.MEANING_DA = MEANING_DA;
    }

    public String getMEANING_DA_DETAILED() {
        return MEANING_DA_DETAILED;
    }

    public void setMEANING_DA_DETAILED(String MEANING_DA_DETAILED) {
        this.MEANING_DA_DETAILED = MEANING_DA_DETAILED;
    }

    public String getMEANING_DA_FOR_HIDE_ALL() {
        return MEANING_DA_FOR_HIDE_ALL;
    }

    public void setMEANING_DA_FOR_HIDE_ALL(String MEANING_DA_FOR_HIDE_ALL) {
        this.MEANING_DA_FOR_HIDE_ALL = MEANING_DA_FOR_HIDE_ALL;
    }

    public String getMEANING_DA_TTS() {
        return MEANING_DA_TTS;
    }

    public void setMEANING_DA_TTS(String MEANING_DA_TTS) {
        this.MEANING_DA_TTS = MEANING_DA_TTS;
    }

    public String getMEANING_DE() {
        return MEANING_DE;
    }

    public void setMEANING_DE(String MEANING_DE) {
        this.MEANING_DE = MEANING_DE;
    }

    public String getMEANING_DE_DETAILED() {
        return MEANING_DE_DETAILED;
    }

    public void setMEANING_DE_DETAILED(String MEANING_DE_DETAILED) {
        this.MEANING_DE_DETAILED = MEANING_DE_DETAILED;
    }

    public String getMEANING_DE_FOR_HIDE_ALL() {
        return MEANING_DE_FOR_HIDE_ALL;
    }

    public void setMEANING_DE_FOR_HIDE_ALL(String MEANING_DE_FOR_HIDE_ALL) {
        this.MEANING_DE_FOR_HIDE_ALL = MEANING_DE_FOR_HIDE_ALL;
    }

    public String getMEANING_DE_TTS() {
        return MEANING_DE_TTS;
    }

    public void setMEANING_DE_TTS(String MEANING_DE_TTS) {
        this.MEANING_DE_TTS = MEANING_DE_TTS;
    }

    public String getMEANING_EL() {
        return MEANING_EL;
    }

    public void setMEANING_EL(String MEANING_EL) {
        this.MEANING_EL = MEANING_EL;
    }

    public String getMEANING_EL_DETAILED() {
        return MEANING_EL_DETAILED;
    }

    public void setMEANING_EL_DETAILED(String MEANING_EL_DETAILED) {
        this.MEANING_EL_DETAILED = MEANING_EL_DETAILED;
    }

    public String getMEANING_EL_FOR_HIDE_ALL() {
        return MEANING_EL_FOR_HIDE_ALL;
    }

    public void setMEANING_EL_FOR_HIDE_ALL(String MEANING_EL_FOR_HIDE_ALL) {
        this.MEANING_EL_FOR_HIDE_ALL = MEANING_EL_FOR_HIDE_ALL;
    }

    public String getMEANING_EL_TTS() {
        return MEANING_EL_TTS;
    }

    public void setMEANING_EL_TTS(String MEANING_EL_TTS) {
        this.MEANING_EL_TTS = MEANING_EL_TTS;
    }

    public String getMEANING_ENG() {
        return MEANING_ENG;
    }

    public void setMEANING_ENG(String MEANING_ENG) {
        this.MEANING_ENG = MEANING_ENG;
    }

    public String getMEANING_ENG_DETAILED() {
        return MEANING_ENG_DETAILED;
    }

    public void setMEANING_ENG_DETAILED(String MEANING_ENG_DETAILED) {
        this.MEANING_ENG_DETAILED = MEANING_ENG_DETAILED;
    }

    public String getMEANING_ENG_FOR_HIDE_ALL() {
        return MEANING_ENG_FOR_HIDE_ALL;
    }

    public void setMEANING_ENG_FOR_HIDE_ALL(String MEANING_ENG_FOR_HIDE_ALL) {
        this.MEANING_ENG_FOR_HIDE_ALL = MEANING_ENG_FOR_HIDE_ALL;
    }

    public String getMEANING_ENG_TTS() {
        return MEANING_ENG_TTS;
    }

    public void setMEANING_ENG_TTS(String MEANING_ENG_TTS) {
        this.MEANING_ENG_TTS = MEANING_ENG_TTS;
    }

    public String getMEANING_ES() {
        return MEANING_ES;
    }

    public void setMEANING_ES(String MEANING_ES) {
        this.MEANING_ES = MEANING_ES;
    }

    public String getMEANING_ES_DETAILED() {
        return MEANING_ES_DETAILED;
    }

    public void setMEANING_ES_DETAILED(String MEANING_ES_DETAILED) {
        this.MEANING_ES_DETAILED = MEANING_ES_DETAILED;
    }

    public String getMEANING_ES_FOR_HIDE_ALL() {
        return MEANING_ES_FOR_HIDE_ALL;
    }

    public void setMEANING_ES_FOR_HIDE_ALL(String MEANING_ES_FOR_HIDE_ALL) {
        this.MEANING_ES_FOR_HIDE_ALL = MEANING_ES_FOR_HIDE_ALL;
    }

    public String getMEANING_ES_TTS() {
        return MEANING_ES_TTS;
    }

    public void setMEANING_ES_TTS(String MEANING_ES_TTS) {
        this.MEANING_ES_TTS = MEANING_ES_TTS;
    }

    public String getMEANING_FI() {
        return MEANING_FI;
    }

    public void setMEANING_FI(String MEANING_FI) {
        this.MEANING_FI = MEANING_FI;
    }

    public String getMEANING_FI_DETAILED() {
        return MEANING_FI_DETAILED;
    }

    public void setMEANING_FI_DETAILED(String MEANING_FI_DETAILED) {
        this.MEANING_FI_DETAILED = MEANING_FI_DETAILED;
    }

    public String getMEANING_FI_FOR_HIDE_ALL() {
        return MEANING_FI_FOR_HIDE_ALL;
    }

    public void setMEANING_FI_FOR_HIDE_ALL(String MEANING_FI_FOR_HIDE_ALL) {
        this.MEANING_FI_FOR_HIDE_ALL = MEANING_FI_FOR_HIDE_ALL;
    }

    public String getMEANING_FI_TTS() {
        return MEANING_FI_TTS;
    }

    public void setMEANING_FI_TTS(String MEANING_FI_TTS) {
        this.MEANING_FI_TTS = MEANING_FI_TTS;
    }

    public String getMEANING_FR() {
        return MEANING_FR;
    }

    public void setMEANING_FR(String MEANING_FR) {
        this.MEANING_FR = MEANING_FR;
    }

    public String getMEANING_FR_DETAILED() {
        return MEANING_FR_DETAILED;
    }

    public void setMEANING_FR_DETAILED(String MEANING_FR_DETAILED) {
        this.MEANING_FR_DETAILED = MEANING_FR_DETAILED;
    }

    public String getMEANING_FR_FOR_HIDE_ALL() {
        return MEANING_FR_FOR_HIDE_ALL;
    }

    public void setMEANING_FR_FOR_HIDE_ALL(String MEANING_FR_FOR_HIDE_ALL) {
        this.MEANING_FR_FOR_HIDE_ALL = MEANING_FR_FOR_HIDE_ALL;
    }

    public String getMEANING_FR_TTS() {
        return MEANING_FR_TTS;
    }

    public void setMEANING_FR_TTS(String MEANING_FR_TTS) {
        this.MEANING_FR_TTS = MEANING_FR_TTS;
    }

    public String getMEANING_HE() {
        return MEANING_HE;
    }

    public void setMEANING_HE(String MEANING_HE) {
        this.MEANING_HE = MEANING_HE;
    }

    public String getMEANING_HE_DETAILED() {
        return MEANING_HE_DETAILED;
    }

    public void setMEANING_HE_DETAILED(String MEANING_HE_DETAILED) {
        this.MEANING_HE_DETAILED = MEANING_HE_DETAILED;
    }

    public String getMEANING_HE_FOR_HIDE_ALL() {
        return MEANING_HE_FOR_HIDE_ALL;
    }

    public void setMEANING_HE_FOR_HIDE_ALL(String MEANING_HE_FOR_HIDE_ALL) {
        this.MEANING_HE_FOR_HIDE_ALL = MEANING_HE_FOR_HIDE_ALL;
    }

    public String getMEANING_HE_TTS() {
        return MEANING_HE_TTS;
    }

    public void setMEANING_HE_TTS(String MEANING_HE_TTS) {
        this.MEANING_HE_TTS = MEANING_HE_TTS;
    }

    public String getMEANING_HI() {
        return MEANING_HI;
    }

    public void setMEANING_HI(String MEANING_HI) {
        this.MEANING_HI = MEANING_HI;
    }

    public String getMEANING_HI_DETAILED() {
        return MEANING_HI_DETAILED;
    }

    public void setMEANING_HI_DETAILED(String MEANING_HI_DETAILED) {
        this.MEANING_HI_DETAILED = MEANING_HI_DETAILED;
    }

    public String getMEANING_HI_FOR_HIDE_ALL() {
        return MEANING_HI_FOR_HIDE_ALL;
    }

    public void setMEANING_HI_FOR_HIDE_ALL(String MEANING_HI_FOR_HIDE_ALL) {
        this.MEANING_HI_FOR_HIDE_ALL = MEANING_HI_FOR_HIDE_ALL;
    }

    public String getMEANING_HI_TTS() {
        return MEANING_HI_TTS;
    }

    public void setMEANING_HI_TTS(String MEANING_HI_TTS) {
        this.MEANING_HI_TTS = MEANING_HI_TTS;
    }

    public String getMEANING_HR() {
        return MEANING_HR;
    }

    public void setMEANING_HR(String MEANING_HR) {
        this.MEANING_HR = MEANING_HR;
    }

    public String getMEANING_HR_DETAILED() {
        return MEANING_HR_DETAILED;
    }

    public void setMEANING_HR_DETAILED(String MEANING_HR_DETAILED) {
        this.MEANING_HR_DETAILED = MEANING_HR_DETAILED;
    }

    public String getMEANING_HR_FOR_HIDE_ALL() {
        return MEANING_HR_FOR_HIDE_ALL;
    }

    public void setMEANING_HR_FOR_HIDE_ALL(String MEANING_HR_FOR_HIDE_ALL) {
        this.MEANING_HR_FOR_HIDE_ALL = MEANING_HR_FOR_HIDE_ALL;
    }

    public String getMEANING_HR_TTS() {
        return MEANING_HR_TTS;
    }

    public void setMEANING_HR_TTS(String MEANING_HR_TTS) {
        this.MEANING_HR_TTS = MEANING_HR_TTS;
    }

    public String getMEANING_HU() {
        return MEANING_HU;
    }

    public void setMEANING_HU(String MEANING_HU) {
        this.MEANING_HU = MEANING_HU;
    }

    public String getMEANING_HU_DETAILED() {
        return MEANING_HU_DETAILED;
    }

    public void setMEANING_HU_DETAILED(String MEANING_HU_DETAILED) {
        this.MEANING_HU_DETAILED = MEANING_HU_DETAILED;
    }

    public String getMEANING_HU_FOR_HIDE_ALL() {
        return MEANING_HU_FOR_HIDE_ALL;
    }

    public void setMEANING_HU_FOR_HIDE_ALL(String MEANING_HU_FOR_HIDE_ALL) {
        this.MEANING_HU_FOR_HIDE_ALL = MEANING_HU_FOR_HIDE_ALL;
    }

    public String getMEANING_HU_TTS() {
        return MEANING_HU_TTS;
    }

    public void setMEANING_HU_TTS(String MEANING_HU_TTS) {
        this.MEANING_HU_TTS = MEANING_HU_TTS;
    }

    public String getMEANING_ID() {
        return MEANING_ID;
    }

    public void setMEANING_ID(String MEANING_ID) {
        this.MEANING_ID = MEANING_ID;
    }

    public String getMEANING_ID_DETAILED() {
        return MEANING_ID_DETAILED;
    }

    public void setMEANING_ID_DETAILED(String MEANING_ID_DETAILED) {
        this.MEANING_ID_DETAILED = MEANING_ID_DETAILED;
    }

    public String getMEANING_ID_FOR_HIDE_ALL() {
        return MEANING_ID_FOR_HIDE_ALL;
    }

    public void setMEANING_ID_FOR_HIDE_ALL(String MEANING_ID_FOR_HIDE_ALL) {
        this.MEANING_ID_FOR_HIDE_ALL = MEANING_ID_FOR_HIDE_ALL;
    }

    public String getMEANING_ID_TTS() {
        return MEANING_ID_TTS;
    }

    public void setMEANING_ID_TTS(String MEANING_ID_TTS) {
        this.MEANING_ID_TTS = MEANING_ID_TTS;
    }

    public String getMEANING_IT() {
        return MEANING_IT;
    }

    public void setMEANING_IT(String MEANING_IT) {
        this.MEANING_IT = MEANING_IT;
    }

    public String getMEANING_IT_DETAILED() {
        return MEANING_IT_DETAILED;
    }

    public void setMEANING_IT_DETAILED(String MEANING_IT_DETAILED) {
        this.MEANING_IT_DETAILED = MEANING_IT_DETAILED;
    }

    public String getMEANING_IT_FOR_HIDE_ALL() {
        return MEANING_IT_FOR_HIDE_ALL;
    }

    public void setMEANING_IT_FOR_HIDE_ALL(String MEANING_IT_FOR_HIDE_ALL) {
        this.MEANING_IT_FOR_HIDE_ALL = MEANING_IT_FOR_HIDE_ALL;
    }

    public String getMEANING_IT_TTS() {
        return MEANING_IT_TTS;
    }

    public void setMEANING_IT_TTS(String MEANING_IT_TTS) {
        this.MEANING_IT_TTS = MEANING_IT_TTS;
    }

    public String getMEANING_JP() {
        return MEANING_JP;
    }

    public void setMEANING_JP(String MEANING_JP) {
        this.MEANING_JP = MEANING_JP;
    }

    public String getMEANING_JP_DETAILED() {
        return MEANING_JP_DETAILED;
    }

    public void setMEANING_JP_DETAILED(String MEANING_JP_DETAILED) {
        this.MEANING_JP_DETAILED = MEANING_JP_DETAILED;
    }

    public String getMEANING_JP_FOR_HIDE_ALL() {
        return MEANING_JP_FOR_HIDE_ALL;
    }

    public void setMEANING_JP_FOR_HIDE_ALL(String MEANING_JP_FOR_HIDE_ALL) {
        this.MEANING_JP_FOR_HIDE_ALL = MEANING_JP_FOR_HIDE_ALL;
    }

    public String getMEANING_JP_TTS() {
        return MEANING_JP_TTS;
    }

    public void setMEANING_JP_TTS(String MEANING_JP_TTS) {
        this.MEANING_JP_TTS = MEANING_JP_TTS;
    }

    public String getMEANING_KO() {
        return MEANING_KO;
    }

    public void setMEANING_KO(String MEANING_KO) {
        this.MEANING_KO = MEANING_KO;
    }

    public String getMEANING_KO_DETAILED() {
        return MEANING_KO_DETAILED;
    }

    public void setMEANING_KO_DETAILED(String MEANING_KO_DETAILED) {
        this.MEANING_KO_DETAILED = MEANING_KO_DETAILED;
    }

    public String getMEANING_KO_FOR_HIDE_ALL() {
        return MEANING_KO_FOR_HIDE_ALL;
    }

    public void setMEANING_KO_FOR_HIDE_ALL(String MEANING_KO_FOR_HIDE_ALL) {
        this.MEANING_KO_FOR_HIDE_ALL = MEANING_KO_FOR_HIDE_ALL;
    }

    public String getMEANING_KO_TTS() {
        return MEANING_KO_TTS;
    }

    public void setMEANING_KO_TTS(String MEANING_KO_TTS) {
        this.MEANING_KO_TTS = MEANING_KO_TTS;
    }

    public String getMEANING_NL() {
        return MEANING_NL;
    }

    public void setMEANING_NL(String MEANING_NL) {
        this.MEANING_NL = MEANING_NL;
    }

    public String getMEANING_NL_DETAILED() {
        return MEANING_NL_DETAILED;
    }

    public void setMEANING_NL_DETAILED(String MEANING_NL_DETAILED) {
        this.MEANING_NL_DETAILED = MEANING_NL_DETAILED;
    }

    public String getMEANING_NL_FOR_HIDE_ALL() {
        return MEANING_NL_FOR_HIDE_ALL;
    }

    public void setMEANING_NL_FOR_HIDE_ALL(String MEANING_NL_FOR_HIDE_ALL) {
        this.MEANING_NL_FOR_HIDE_ALL = MEANING_NL_FOR_HIDE_ALL;
    }

    public String getMEANING_NL_TTS() {
        return MEANING_NL_TTS;
    }

    public void setMEANING_NL_TTS(String MEANING_NL_TTS) {
        this.MEANING_NL_TTS = MEANING_NL_TTS;
    }

    public String getMEANING_NO() {
        return MEANING_NO;
    }

    public void setMEANING_NO(String MEANING_NO) {
        this.MEANING_NO = MEANING_NO;
    }

    public String getMEANING_NO_DETAILED() {
        return MEANING_NO_DETAILED;
    }

    public void setMEANING_NO_DETAILED(String MEANING_NO_DETAILED) {
        this.MEANING_NO_DETAILED = MEANING_NO_DETAILED;
    }

    public String getMEANING_NO_FOR_HIDE_ALL() {
        return MEANING_NO_FOR_HIDE_ALL;
    }

    public void setMEANING_NO_FOR_HIDE_ALL(String MEANING_NO_FOR_HIDE_ALL) {
        this.MEANING_NO_FOR_HIDE_ALL = MEANING_NO_FOR_HIDE_ALL;
    }

    public String getMEANING_NO_TTS() {
        return MEANING_NO_TTS;
    }

    public void setMEANING_NO_TTS(String MEANING_NO_TTS) {
        this.MEANING_NO_TTS = MEANING_NO_TTS;
    }

    public String getMEANING_PL() {
        return MEANING_PL;
    }

    public void setMEANING_PL(String MEANING_PL) {
        this.MEANING_PL = MEANING_PL;
    }

    public String getMEANING_PL_DETAILED() {
        return MEANING_PL_DETAILED;
    }

    public void setMEANING_PL_DETAILED(String MEANING_PL_DETAILED) {
        this.MEANING_PL_DETAILED = MEANING_PL_DETAILED;
    }

    public String getMEANING_PL_FOR_HIDE_ALL() {
        return MEANING_PL_FOR_HIDE_ALL;
    }

    public void setMEANING_PL_FOR_HIDE_ALL(String MEANING_PL_FOR_HIDE_ALL) {
        this.MEANING_PL_FOR_HIDE_ALL = MEANING_PL_FOR_HIDE_ALL;
    }

    public String getMEANING_PL_TTS() {
        return MEANING_PL_TTS;
    }

    public void setMEANING_PL_TTS(String MEANING_PL_TTS) {
        this.MEANING_PL_TTS = MEANING_PL_TTS;
    }

    public String getMEANING_PT() {
        return MEANING_PT;
    }

    public void setMEANING_PT(String MEANING_PT) {
        this.MEANING_PT = MEANING_PT;
    }

    public String getMEANING_PT_DETAILED() {
        return MEANING_PT_DETAILED;
    }

    public void setMEANING_PT_DETAILED(String MEANING_PT_DETAILED) {
        this.MEANING_PT_DETAILED = MEANING_PT_DETAILED;
    }

    public String getMEANING_PT_FOR_HIDE_ALL() {
        return MEANING_PT_FOR_HIDE_ALL;
    }

    public void setMEANING_PT_FOR_HIDE_ALL(String MEANING_PT_FOR_HIDE_ALL) {
        this.MEANING_PT_FOR_HIDE_ALL = MEANING_PT_FOR_HIDE_ALL;
    }

    public String getMEANING_PT_TTS() {
        return MEANING_PT_TTS;
    }

    public void setMEANING_PT_TTS(String MEANING_PT_TTS) {
        this.MEANING_PT_TTS = MEANING_PT_TTS;
    }

    public String getMEANING_RO() {
        return MEANING_RO;
    }

    public void setMEANING_RO(String MEANING_RO) {
        this.MEANING_RO = MEANING_RO;
    }

    public String getMEANING_RO_DETAILED() {
        return MEANING_RO_DETAILED;
    }

    public void setMEANING_RO_DETAILED(String MEANING_RO_DETAILED) {
        this.MEANING_RO_DETAILED = MEANING_RO_DETAILED;
    }

    public String getMEANING_RO_FOR_HIDE_ALL() {
        return MEANING_RO_FOR_HIDE_ALL;
    }

    public void setMEANING_RO_FOR_HIDE_ALL(String MEANING_RO_FOR_HIDE_ALL) {
        this.MEANING_RO_FOR_HIDE_ALL = MEANING_RO_FOR_HIDE_ALL;
    }

    public String getMEANING_RO_TTS() {
        return MEANING_RO_TTS;
    }

    public void setMEANING_RO_TTS(String MEANING_RO_TTS) {
        this.MEANING_RO_TTS = MEANING_RO_TTS;
    }

    public String getMEANING_RU() {
        return MEANING_RU;
    }

    public void setMEANING_RU(String MEANING_RU) {
        this.MEANING_RU = MEANING_RU;
    }

    public String getMEANING_RU_DETAILED() {
        return MEANING_RU_DETAILED;
    }

    public void setMEANING_RU_DETAILED(String MEANING_RU_DETAILED) {
        this.MEANING_RU_DETAILED = MEANING_RU_DETAILED;
    }

    public String getMEANING_RU_FOR_HIDE_ALL() {
        return MEANING_RU_FOR_HIDE_ALL;
    }

    public void setMEANING_RU_FOR_HIDE_ALL(String MEANING_RU_FOR_HIDE_ALL) {
        this.MEANING_RU_FOR_HIDE_ALL = MEANING_RU_FOR_HIDE_ALL;
    }

    public String getMEANING_RU_TTS() {
        return MEANING_RU_TTS;
    }

    public void setMEANING_RU_TTS(String MEANING_RU_TTS) {
        this.MEANING_RU_TTS = MEANING_RU_TTS;
    }

    public String getMEANING_SK() {
        return MEANING_SK;
    }

    public void setMEANING_SK(String MEANING_SK) {
        this.MEANING_SK = MEANING_SK;
    }

    public String getMEANING_SK_DETAILED() {
        return MEANING_SK_DETAILED;
    }

    public void setMEANING_SK_DETAILED(String MEANING_SK_DETAILED) {
        this.MEANING_SK_DETAILED = MEANING_SK_DETAILED;
    }

    public String getMEANING_SK_FOR_HIDE_ALL() {
        return MEANING_SK_FOR_HIDE_ALL;
    }

    public void setMEANING_SK_FOR_HIDE_ALL(String MEANING_SK_FOR_HIDE_ALL) {
        this.MEANING_SK_FOR_HIDE_ALL = MEANING_SK_FOR_HIDE_ALL;
    }

    public String getMEANING_SK_TTS() {
        return MEANING_SK_TTS;
    }

    public void setMEANING_SK_TTS(String MEANING_SK_TTS) {
        this.MEANING_SK_TTS = MEANING_SK_TTS;
    }

    public String getMEANING_SV() {
        return MEANING_SV;
    }

    public void setMEANING_SV(String MEANING_SV) {
        this.MEANING_SV = MEANING_SV;
    }

    public String getMEANING_SV_DETAILED() {
        return MEANING_SV_DETAILED;
    }

    public void setMEANING_SV_DETAILED(String MEANING_SV_DETAILED) {
        this.MEANING_SV_DETAILED = MEANING_SV_DETAILED;
    }

    public String getMEANING_SV_FOR_HIDE_ALL() {
        return MEANING_SV_FOR_HIDE_ALL;
    }

    public void setMEANING_SV_FOR_HIDE_ALL(String MEANING_SV_FOR_HIDE_ALL) {
        this.MEANING_SV_FOR_HIDE_ALL = MEANING_SV_FOR_HIDE_ALL;
    }

    public String getMEANING_SV_TTS() {
        return MEANING_SV_TTS;
    }

    public void setMEANING_SV_TTS(String MEANING_SV_TTS) {
        this.MEANING_SV_TTS = MEANING_SV_TTS;
    }

    public String getMEANING_TH() {
        return MEANING_TH;
    }

    public void setMEANING_TH(String MEANING_TH) {
        this.MEANING_TH = MEANING_TH;
    }

    public String getMEANING_TH_DETAILED() {
        return MEANING_TH_DETAILED;
    }

    public void setMEANING_TH_DETAILED(String MEANING_TH_DETAILED) {
        this.MEANING_TH_DETAILED = MEANING_TH_DETAILED;
    }

    public String getMEANING_TH_FOR_HIDE_ALL() {
        return MEANING_TH_FOR_HIDE_ALL;
    }

    public void setMEANING_TH_FOR_HIDE_ALL(String MEANING_TH_FOR_HIDE_ALL) {
        this.MEANING_TH_FOR_HIDE_ALL = MEANING_TH_FOR_HIDE_ALL;
    }

    public String getMEANING_TH_TTS() {
        return MEANING_TH_TTS;
    }

    public void setMEANING_TH_TTS(String MEANING_TH_TTS) {
        this.MEANING_TH_TTS = MEANING_TH_TTS;
    }

    public String getMEANING_TR() {
        return MEANING_TR;
    }

    public void setMEANING_TR(String MEANING_TR) {
        this.MEANING_TR = MEANING_TR;
    }

    public String getMEANING_TR_DETAILED() {
        return MEANING_TR_DETAILED;
    }

    public void setMEANING_TR_DETAILED(String MEANING_TR_DETAILED) {
        this.MEANING_TR_DETAILED = MEANING_TR_DETAILED;
    }

    public String getMEANING_TR_FOR_HIDE_ALL() {
        return MEANING_TR_FOR_HIDE_ALL;
    }

    public void setMEANING_TR_FOR_HIDE_ALL(String MEANING_TR_FOR_HIDE_ALL) {
        this.MEANING_TR_FOR_HIDE_ALL = MEANING_TR_FOR_HIDE_ALL;
    }

    public String getMEANING_TR_TTS() {
        return MEANING_TR_TTS;
    }

    public void setMEANING_TR_TTS(String MEANING_TR_TTS) {
        this.MEANING_TR_TTS = MEANING_TR_TTS;
    }

    public String getMEANING_UK() {
        return MEANING_UK;
    }

    public void setMEANING_UK(String MEANING_UK) {
        this.MEANING_UK = MEANING_UK;
    }

    public String getMEANING_UK_DETAILED() {
        return MEANING_UK_DETAILED;
    }

    public void setMEANING_UK_DETAILED(String MEANING_UK_DETAILED) {
        this.MEANING_UK_DETAILED = MEANING_UK_DETAILED;
    }

    public String getMEANING_UK_FOR_HIDE_ALL() {
        return MEANING_UK_FOR_HIDE_ALL;
    }

    public void setMEANING_UK_FOR_HIDE_ALL(String MEANING_UK_FOR_HIDE_ALL) {
        this.MEANING_UK_FOR_HIDE_ALL = MEANING_UK_FOR_HIDE_ALL;
    }

    public String getMEANING_UK_TTS() {
        return MEANING_UK_TTS;
    }

    public void setMEANING_UK_TTS(String MEANING_UK_TTS) {
        this.MEANING_UK_TTS = MEANING_UK_TTS;
    }

    public String getMEANING_VI() {
        return MEANING_VI;
    }

    public void setMEANING_VI(String MEANING_VI) {
        this.MEANING_VI = MEANING_VI;
    }

    public String getMEANING_VI_DETAILED() {
        return MEANING_VI_DETAILED;
    }

    public void setMEANING_VI_DETAILED(String MEANING_VI_DETAILED) {
        this.MEANING_VI_DETAILED = MEANING_VI_DETAILED;
    }

    public String getMEANING_VI_FOR_HIDE_ALL() {
        return MEANING_VI_FOR_HIDE_ALL;
    }

    public void setMEANING_VI_FOR_HIDE_ALL(String MEANING_VI_FOR_HIDE_ALL) {
        this.MEANING_VI_FOR_HIDE_ALL = MEANING_VI_FOR_HIDE_ALL;
    }

    public String getMEANING_VI_TTS() {
        return MEANING_VI_TTS;
    }

    public void setMEANING_VI_TTS(String MEANING_VI_TTS) {
        this.MEANING_VI_TTS = MEANING_VI_TTS;
    }
}
