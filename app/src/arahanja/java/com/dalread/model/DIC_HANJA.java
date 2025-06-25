// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.dalread.model;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;
import com.dalread.util.HanjaWordParsingUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class DIC_HANJA extends RealmObject implements HanjaItem, AmkiItem, HanjaQuizItem, IVocaFullPlayTTSItem, Serializable {

    @PrimaryKey
    private Long ID;
    private String UNICODE_HEX;
    private String VOCA;
    private String VOCAORI;
    private String MEANING1;
    private String PRONOUNCE1_FIRST;
    private Long VOCAORI_ID;
    private Long STROKES;
    private String RADICAL;
    private Long IS_RADICAL;
    private String HANJA_KOREA;
    private String HANJA_TAIWAN;
    private String HANJA_JAPAN;
    private String HANJA_SIMPLIFIED;
    private String HANJA_SHORT_FORM;
    private String HANJA_VARIANT_1;
    private String HANJA_VARIANT_2;
    private String HANJA_SOKJA;
    private String HANJA_TYPE;
    private Long VOCA_LEVEL;
    private String PRONOUNCE1;
    private String MEANING1_PRONOUNCE1_FIRST;
    private String MEANING1_PRONOUNCE1;
    private String MEANING2;
    private String PRONOUNCE2_FIRST;
    private String PRONOUNCE2;
    private String MEANING3;
    private String PRONOUNCE3_FIRST;
    private String PRONOUNCE3;
    private String UPDATE_DATE;
    private String PRONOUNCE;
    private Long PRONOUNCE_USE;
    private String MEANING_KO;
    private String MEANING_CH_S;
    private String MEANING_CH_T;
    private String MEANING_JP;
    private String MEANING_VI;
    private String MEANING_UK;
    private String MEANING_TR;
    private String MEANING_TH;
    private String MEANING_SV;
    private String MEANING_ES;
    private String MEANING_SK;
    private String MEANING_RO;
    private String MEANING_PT;
    private String MEANING_PL;
    private String MEANING_NO;
    private String MEANING_IT;
    private String MEANING_ID;
    private String MEANING_HU;
    private String MEANING_HI;
    private String MEANING_HE;
    private String MEANING_EL;
    private String MEANING_DE;
    private String MEANING_FI;
    private String MEANING_NL;
    private String MEANING_DA;
    private String MEANING_CS;
    private String MEANING_HR;
    private String MEANING_BN;
    private String MEANING_AR;
    private String MEANING_RU;
    private String MEANING_FR;
    private String MEANING_ENG;
    private String MEANING_KO_DETAILED;
    private String MEANING_KO_TTS;
    private String MEANING_ENG_DETAILED;
    private String MEANING_ENG_TTS;
    private String MEANING_VI_DETAILED;
    private String MEANING_VI_TTS;
    private String MEANING_RU_TTS;
    private String MEANING_RU_DETAILED;
    private String MEANING_BN_TTS;
    private String MEANING_BN_DETAILED;
    private String MEANING_HR_TTS;
    private String MEANING_HR_DETAILED;
    private String MEANING_CS_TTS;
    private String MEANING_CS_DETAILED;
    private String MEANING_DA_TTS;
    private String MEANING_DA_DETAILED;
    private String MEANING_NL_TTS;
    private String MEANING_NL_DETAILED;
    private String MEANING_FI_TTS;
    private String MEANING_FI_DETAILED;
    private String MEANING_DE_TTS;
    private String MEANING_DE_DETAILED;
    private String MEANING_EL_TTS;
    private String MEANING_EL_DETAILED;
    private String MEANING_HE_TTS;
    private String MEANING_HE_DETAILED;
    private String MEANING_HI_TTS;
    private String MEANING_HI_DETAILED;
    private String MEANING_HU_TTS;
    private String MEANING_HU_DETAILED;
    private String MEANING_ID_TTS;
    private String MEANING_ID_DETAILED;
    private String MEANING_IT_TTS;
    private String MEANING_IT_DETAILED;
    private String MEANING_NO_TTS;
    private String MEANING_NO_DETAILED;
    private String MEANING_PL_TTS;
    private String MEANING_PL_DETAILED;
    private String MEANING_PT_TTS;
    private String MEANING_PT_DETAILED;
    private String MEANING_RO_TTS;
    private String MEANING_RO_DETAILED;
    private String MEANING_SK_TTS;
    private String MEANING_SK_DETAILED;
    private String MEANING_ES_TTS;
    private String MEANING_ES_DETAILED;
    private String MEANING_SV_TTS;
    private String MEANING_SV_DETAILED;
    private String MEANING_TH_TTS;
    private String MEANING_TH_DETAILED;
    private String MEANING_TR_TTS;
    private String MEANING_TR_DETAILED;
    private String MEANING_UK_TTS;
    private String MEANING_UK_DETAILED;
    private String MEANING_CH_T_TTS;
    private String MEANING_CH_T_DETAILED;
    private String MEANING_JP_DETAILED;
    private String MEANING_JP_TTS;
    private String MEANING_CH_S_DETAILED;
    private String MEANING_CH_S_TTS;
    private Long UID;
    private String MEANING_AR_TTS;
    private String MEANING_AR_DETAILED;
    private String MEANING_FR_TTS;
    private String MEANING_FR_DETAILED;
    private Long CREATOR_TYPE;
    private String PINYIN;
    private String KUNYOMI;
    private String ONYOMI;
    private String DECOMPOSITION;
    private Long STROKES_EXCEPT_RADICAL;
    private String DECOMPOSITION_ORI;
    private String COMPOSITIONTYPE;
    private String LEFTCOMPONENT;
    private String RIGHTCOMPONENT;
    private String SIGNATURE;
    private String NOTES;
    private String VOCA_TTS;
    private Long UNICODE_DEC;
    private String COMMON_USE_JAPAN;
    private String COMMON_USE_KOREA;
    private String LEVEL_KOREA_TYPE_1;
    private String LEVEL_KOREA_TYPE_2;
    private String LEVEL_KOREA_TYPE_3;
    private String LEVEL_HSK;
    private Long VOCA_KNOW;
    private Long VOCA_KNOWPRONOUNCE;
    private Long BOOKMARK;
    private Long PEOPLE_NAME;
    private String UPDATE_DATE_BOOKMARK;
    private String UPDATE_DATE_VOCA_KNOWPRONOUNCE;
    private String UPDATE_DATE_VOCA_KNOW;
    @Ignore
    private String parentVoca;
    @Ignore
    private Long index;
    @Ignore
    private String MEANING;
    @Ignore
    private String MEANING_DETAILED;

    ///For PlaylistItem
    @Ignore
    private boolean checked;  //What is this for?
    @Ignore
    private boolean playing; //??
    @Ignore
    private String path;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getUNICODE_HEX() {
        return UNICODE_HEX;
    }

    public void setUNICODE_HEX(String UNICODE_HEX) {
        this.UNICODE_HEX = UNICODE_HEX;
    }

    public String getVOCA() {
        return VOCA == null ? "" : VOCA;
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public String getVOCAORI() {
        return VOCAORI == null ? "" : VOCAORI;
    }

    public void setVOCAORI(String VOCAORI) {
        this.VOCAORI = VOCAORI;
    }

    public String getMEANING1OrEmptyString() {
        return MEANING1 == null ? "" : MEANING1;
    }

    public String getMEANING1() {
        return MEANING1 == null ? "" : MEANING1;
    }

    public void setMEANING1(String MEANING1) {
        this.MEANING1 = MEANING1;
    }
    //이걸 왜 만들었을까? getPRONOUNCE1_FIRST와 동일하다.
    public String getPRONOUNCE1_FIRSTOrEmptyString() {
        return PRONOUNCE1_FIRST == null ? "" : PRONOUNCE1_FIRST;
    }

    public String getPRONOUNCE1_FIRST() {
        return PRONOUNCE1_FIRST == null ? "" : PRONOUNCE1_FIRST;
    }

    public void setPRONOUNCE1_FIRST(String PRONOUNCE1_FIRST) {
        this.PRONOUNCE1_FIRST = PRONOUNCE1_FIRST;
    }

    public Long getVOCAORI_ID() {
        return VOCAORI_ID;
    }

    public void setVOCAORI_ID(Long VOCAORI_ID) {
        this.VOCAORI_ID = VOCAORI_ID;
    }

    public Long getSTROKES() {
        return STROKES;
    }

    public void setSTROKES(Long STROKES) {
        this.STROKES = STROKES;
    }

    public String getRADICALOrEmptyString() {
        return RADICAL == null ? "" : RADICAL;
    }

    public String getRADICAL() {
        return RADICAL;
    }

    public void setRADICAL(String RADICAL) {
        this.RADICAL = RADICAL;
    }

    public Long getIS_RADICAL() {
        return IS_RADICAL;
    }

    public void setIS_RADICAL(Long IS_RADICAL) {
        this.IS_RADICAL = IS_RADICAL;
    }

    public String getHANJA_KOREA() {
        return HANJA_KOREA;
    }

    public void setHANJA_KOREA(String HANJA_KOREA) {
        this.HANJA_KOREA = HANJA_KOREA;
    }

    public String getHANJA_TAIWAN() {
        return HANJA_TAIWAN;
    }

    public void setHANJA_TAIWAN(String HANJA_TAIWAN) {
        this.HANJA_TAIWAN = HANJA_TAIWAN;
    }

    public String getHANJA_JAPAN() {
        return HANJA_JAPAN;
    }

    public void setHANJA_JAPAN(String HANJA_JAPAN) {
        this.HANJA_JAPAN = HANJA_JAPAN;
    }

    public String getHANJA_SIMPLIFIED() {
        return HANJA_SIMPLIFIED;
    }

    public void setHANJA_SIMPLIFIED(String HANJA_SIMPLIFIED) {
        this.HANJA_SIMPLIFIED = HANJA_SIMPLIFIED;
    }

    public String getHANJA_SHORT_FORM() {
        return HANJA_SHORT_FORM;
    }

    public void setHANJA_SHORT_FORM(String HANJA_SHORT_FORM) {
        this.HANJA_SHORT_FORM = HANJA_SHORT_FORM;
    }

    public String getHANJA_VARIANT_1() {
        return HANJA_VARIANT_1;
    }

    public void setHANJA_VARIANT_1(String HANJA_VARIANT_1) {
        this.HANJA_VARIANT_1 = HANJA_VARIANT_1;
    }

    public String getHANJA_VARIANT_2() {
        return HANJA_VARIANT_2;
    }

    public void setHANJA_VARIANT_2(String HANJA_VARIANT_2) {
        this.HANJA_VARIANT_2 = HANJA_VARIANT_2;
    }

    public String getHANJA_SOKJA() {
        return HANJA_SOKJA;
    }

    public void setHANJA_SOKJA(String HANJA_SOKJA) {
        this.HANJA_SOKJA = HANJA_SOKJA;
    }

    public String getHANJA_TYPE() {
        return HANJA_TYPE;
    }

    public void setHANJA_TYPE(String HANJA_TYPE) {
        this.HANJA_TYPE = HANJA_TYPE;
    }

    public Long getVOCA_LEVEL() {
        return VOCA_LEVEL == null ? Constant.VOCA_LEVEL_INDIC_MAX : VOCA_LEVEL;
    }

    public void setVOCA_LEVEL(Long VOCA_LEVEL) {
        this.VOCA_LEVEL = VOCA_LEVEL;
    }

    public String getPRONOUNCE1() {
        return PRONOUNCE1;
    }

    public void setPRONOUNCE1(String PRONOUNCE1) {
        this.PRONOUNCE1 = PRONOUNCE1;
    }

    public String getMEANING1_PRONOUNCE1_FIRST() {
        return MEANING1_PRONOUNCE1_FIRST;
    }

    public void setMEANING1_PRONOUNCE1_FIRST(String MEANING1_PRONOUNCE1_FIRST) {
        this.MEANING1_PRONOUNCE1_FIRST = MEANING1_PRONOUNCE1_FIRST;
    }

    public String getMEANING1_PRONOUNCE1() {
        return MEANING1_PRONOUNCE1;
    }

    public void setMEANING1_PRONOUNCE1(String MEANING1_PRONOUNCE1) {
        this.MEANING1_PRONOUNCE1 = MEANING1_PRONOUNCE1;
    }

    public String getMEANING2() {
        return MEANING2;
    }

    public void setMEANING2(String MEANING2) {
        this.MEANING2 = MEANING2;
    }

    public String getPRONOUNCE2_FIRST() {
        return PRONOUNCE2_FIRST;
    }

    public void setPRONOUNCE2_FIRST(String PRONOUNCE2_FIRST) {
        this.PRONOUNCE2_FIRST = PRONOUNCE2_FIRST;
    }

    public String getPRONOUNCE2() {
        return PRONOUNCE2;
    }

    public void setPRONOUNCE2(String PRONOUNCE2) {
        this.PRONOUNCE2 = PRONOUNCE2;
    }

    public String getMEANING3() {
        return MEANING3;
    }

    public void setMEANING3(String MEANING3) {
        this.MEANING3 = MEANING3;
    }

    public String getPRONOUNCE3_FIRST() {
        return PRONOUNCE3_FIRST;
    }

    public void setPRONOUNCE3_FIRST(String PRONOUNCE3_FIRST) {
        this.PRONOUNCE3_FIRST = PRONOUNCE3_FIRST;
    }

    public String getPRONOUNCE3() {
        return PRONOUNCE3;
    }

    public void setPRONOUNCE3(String PRONOUNCE3) {
        this.PRONOUNCE3 = PRONOUNCE3;
    }

    public String getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setUPDATE_DATE(String UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public String getPRONOUNCE() {
        return PRONOUNCE == null ? "" : PRONOUNCE;
    }

    public void setPRONOUNCE(String PRONOUNCE) {
        this.PRONOUNCE = PRONOUNCE;
    }

    public Long getPRONOUNCE_USE() {
        return PRONOUNCE_USE;
    }

    public void setPRONOUNCE_USE(Long PRONOUNCE_USE) {
        this.PRONOUNCE_USE = PRONOUNCE_USE;
    }

    public String getMEANING_KO() {
        //한자 단어의 모든 뜻과 발음이 MEANING_KO에 없을수도 있고, 업데이트가 덜 되었을수도 있어서 따로 계산해서 가져온다.
        //근데 MEANING_KO에는 왜 한자 단어의 모든 뜻과 발음을 두었지? 최신성이 유지 안될수도 있는데?
        return HanjaWordParsingUtil.getMeaningKO(this);
//        return StringUtils.trimAndNormalizeString(MEANING_KO);
    }

    public void setMEANING_KO(String MEANING_KO) {
        this.MEANING_KO = MEANING_KO;
    }

    public String getMEANING_CH_S() {
        return MEANING_CH_S;
    }

    public void setMEANING_CH_S(String MEANING_CH_S) {
        this.MEANING_CH_S = MEANING_CH_S;
    }

    public String getMEANING_CH_T() {
        return MEANING_CH_T;
    }

    public void setMEANING_CH_T(String MEANING_CH_T) {
        this.MEANING_CH_T = MEANING_CH_T;
    }

    public String getMEANING_JP() {
        return MEANING_JP;
    }

    public void setMEANING_JP(String MEANING_JP) {
        this.MEANING_JP = MEANING_JP;
    }

    public String getMEANING_VI() {
        return MEANING_VI;
    }

    public void setMEANING_VI(String MEANING_VI) {
        this.MEANING_VI = MEANING_VI;
    }

    public String getMEANING_UK() {
        return MEANING_UK;
    }

    public void setMEANING_UK(String MEANING_UK) {
        this.MEANING_UK = MEANING_UK;
    }

    public String getMEANING_TR() {
        return MEANING_TR;
    }

    public void setMEANING_TR(String MEANING_TR) {
        this.MEANING_TR = MEANING_TR;
    }

    public String getMEANING_TH() {
        return MEANING_TH;
    }

    public void setMEANING_TH(String MEANING_TH) {
        this.MEANING_TH = MEANING_TH;
    }

    public String getMEANING_SV() {
        return MEANING_SV;
    }

    public void setMEANING_SV(String MEANING_SV) {
        this.MEANING_SV = MEANING_SV;
    }

    public String getMEANING_ES() {
        return MEANING_ES;
    }

    public void setMEANING_ES(String MEANING_ES) {
        this.MEANING_ES = MEANING_ES;
    }

    public String getMEANING_SK() {
        return MEANING_SK;
    }

    public void setMEANING_SK(String MEANING_SK) {
        this.MEANING_SK = MEANING_SK;
    }

    public String getMEANING_RO() {
        return MEANING_RO;
    }

    public void setMEANING_RO(String MEANING_RO) {
        this.MEANING_RO = MEANING_RO;
    }

    public String getMEANING_PT() {
        return MEANING_PT;
    }

    public void setMEANING_PT(String MEANING_PT) {
        this.MEANING_PT = MEANING_PT;
    }

    public String getMEANING_PL() {
        return MEANING_PL;
    }

    public void setMEANING_PL(String MEANING_PL) {
        this.MEANING_PL = MEANING_PL;
    }

    public String getMEANING_NO() {
        return MEANING_NO;
    }

    public void setMEANING_NO(String MEANING_NO) {
        this.MEANING_NO = MEANING_NO;
    }

    public String getMEANING_IT() {
        return MEANING_IT;
    }

    public void setMEANING_IT(String MEANING_IT) {
        this.MEANING_IT = MEANING_IT;
    }

    public String getMEANING_ID() {
        return MEANING_ID;
    }

    public void setMEANING_ID(String MEANING_ID) {
        this.MEANING_ID = MEANING_ID;
    }

    public String getMEANING_HU() {
        return MEANING_HU;
    }

    public void setMEANING_HU(String MEANING_HU) {
        this.MEANING_HU = MEANING_HU;
    }

    public String getMEANING_HI() {
        return MEANING_HI;
    }

    public void setMEANING_HI(String MEANING_HI) {
        this.MEANING_HI = MEANING_HI;
    }

    public String getMEANING_HE() {
        return MEANING_HE;
    }

    public void setMEANING_HE(String MEANING_HE) {
        this.MEANING_HE = MEANING_HE;
    }

    public String getMEANING_EL() {
        return MEANING_EL;
    }

    public void setMEANING_EL(String MEANING_EL) {
        this.MEANING_EL = MEANING_EL;
    }

    public String getMEANING_DE() {
        return MEANING_DE;
    }

    public void setMEANING_DE(String MEANING_DE) {
        this.MEANING_DE = MEANING_DE;
    }

    public String getMEANING_FI() {
        return MEANING_FI;
    }

    public void setMEANING_FI(String MEANING_FI) {
        this.MEANING_FI = MEANING_FI;
    }

    public String getMEANING_NL() {
        return MEANING_NL;
    }

    public void setMEANING_NL(String MEANING_NL) {
        this.MEANING_NL = MEANING_NL;
    }

    public String getMEANING_DA() {
        return MEANING_DA;
    }

    public void setMEANING_DA(String MEANING_DA) {
        this.MEANING_DA = MEANING_DA;
    }

    public String getMEANING_CS() {
        return MEANING_CS;
    }

    public void setMEANING_CS(String MEANING_CS) {
        this.MEANING_CS = MEANING_CS;
    }

    public String getMEANING_HR() {
        return MEANING_HR;
    }

    public void setMEANING_HR(String MEANING_HR) {
        this.MEANING_HR = MEANING_HR;
    }

    public String getMEANING_BN() {
        return MEANING_BN;
    }

    public void setMEANING_BN(String MEANING_BN) {
        this.MEANING_BN = MEANING_BN;
    }

    public String getMEANING_AR() {
        return MEANING_AR;
    }

    public void setMEANING_AR(String MEANING_AR) {
        this.MEANING_AR = MEANING_AR;
    }

    public String getMEANING_RU() {
        return MEANING_RU;
    }

    public void setMEANING_RU(String MEANING_RU) {
        this.MEANING_RU = MEANING_RU;
    }

    public String getMEANING_FR() {
        return MEANING_FR;
    }

    public void setMEANING_FR(String MEANING_FR) {
        this.MEANING_FR = MEANING_FR;
    }

    public String getMEANING_ENG() {
        return StringUtils.trimAndNormalizeString(MEANING_ENG);
    }

    public void setMEANING_ENG(String MEANING_ENG) {
        this.MEANING_ENG = MEANING_ENG;
    }

    public String getMEANING_KO_DETAILED() {
        return StringUtils.trimAndNormalizeString(MEANING_KO_DETAILED);
    }

    public void setMEANING_KO_DETAILED(String MEANING_KO_DETAILED) {
        this.MEANING_KO_DETAILED = MEANING_KO_DETAILED;
    }

    public String getMEANING_KO_TTS() {
        return MEANING_KO_TTS;
    }

    public void setMEANING_KO_TTS(String MEANING_KO_TTS) {
        this.MEANING_KO_TTS = MEANING_KO_TTS;
    }

    public String getMEANING_ENG_DETAILED() {
        return StringUtils.trimAndNormalizeString(MEANING_ENG_DETAILED);
    }

    public void setMEANING_ENG_DETAILED(String MEANING_ENG_DETAILED) {
        this.MEANING_ENG_DETAILED = MEANING_ENG_DETAILED;
    }

    public String getMEANING_ENG_TTS() {
        return MEANING_ENG_TTS;
    }

    public void setMEANING_ENG_TTS(String MEANING_ENG_TTS) {
        this.MEANING_ENG_TTS = MEANING_ENG_TTS;
    }

    public String getMEANING_VI_DETAILED() {
        return MEANING_VI_DETAILED;
    }

    public void setMEANING_VI_DETAILED(String MEANING_VI_DETAILED) {
        this.MEANING_VI_DETAILED = MEANING_VI_DETAILED;
    }

    public String getMEANING_VI_TTS() {
        return MEANING_VI_TTS;
    }

    public void setMEANING_VI_TTS(String MEANING_VI_TTS) {
        this.MEANING_VI_TTS = MEANING_VI_TTS;
    }

    public String getMEANING_RU_TTS() {
        return MEANING_RU_TTS;
    }

    public void setMEANING_RU_TTS(String MEANING_RU_TTS) {
        this.MEANING_RU_TTS = MEANING_RU_TTS;
    }

    public String getMEANING_RU_DETAILED() {
        return MEANING_RU_DETAILED;
    }

    public void setMEANING_RU_DETAILED(String MEANING_RU_DETAILED) {
        this.MEANING_RU_DETAILED = MEANING_RU_DETAILED;
    }

    public String getMEANING_BN_TTS() {
        return MEANING_BN_TTS;
    }

    public void setMEANING_BN_TTS(String MEANING_BN_TTS) {
        this.MEANING_BN_TTS = MEANING_BN_TTS;
    }

    public String getMEANING_BN_DETAILED() {
        return MEANING_BN_DETAILED;
    }

    public void setMEANING_BN_DETAILED(String MEANING_BN_DETAILED) {
        this.MEANING_BN_DETAILED = MEANING_BN_DETAILED;
    }

    public String getMEANING_HR_TTS() {
        return MEANING_HR_TTS;
    }

    public void setMEANING_HR_TTS(String MEANING_HR_TTS) {
        this.MEANING_HR_TTS = MEANING_HR_TTS;
    }

    public String getMEANING_HR_DETAILED() {
        return MEANING_HR_DETAILED;
    }

    public void setMEANING_HR_DETAILED(String MEANING_HR_DETAILED) {
        this.MEANING_HR_DETAILED = MEANING_HR_DETAILED;
    }

    public String getMEANING_CS_TTS() {
        return MEANING_CS_TTS;
    }

    public void setMEANING_CS_TTS(String MEANING_CS_TTS) {
        this.MEANING_CS_TTS = MEANING_CS_TTS;
    }

    public String getMEANING_CS_DETAILED() {
        return MEANING_CS_DETAILED;
    }

    public void setMEANING_CS_DETAILED(String MEANING_CS_DETAILED) {
        this.MEANING_CS_DETAILED = MEANING_CS_DETAILED;
    }

    public String getMEANING_DA_TTS() {
        return MEANING_DA_TTS;
    }

    public void setMEANING_DA_TTS(String MEANING_DA_TTS) {
        this.MEANING_DA_TTS = MEANING_DA_TTS;
    }

    public String getMEANING_DA_DETAILED() {
        return MEANING_DA_DETAILED;
    }

    public void setMEANING_DA_DETAILED(String MEANING_DA_DETAILED) {
        this.MEANING_DA_DETAILED = MEANING_DA_DETAILED;
    }

    public String getMEANING_NL_TTS() {
        return MEANING_NL_TTS;
    }

    public void setMEANING_NL_TTS(String MEANING_NL_TTS) {
        this.MEANING_NL_TTS = MEANING_NL_TTS;
    }

    public String getMEANING_NL_DETAILED() {
        return MEANING_NL_DETAILED;
    }

    public void setMEANING_NL_DETAILED(String MEANING_NL_DETAILED) {
        this.MEANING_NL_DETAILED = MEANING_NL_DETAILED;
    }

    public String getMEANING_FI_TTS() {
        return MEANING_FI_TTS;
    }

    public void setMEANING_FI_TTS(String MEANING_FI_TTS) {
        this.MEANING_FI_TTS = MEANING_FI_TTS;
    }

    public String getMEANING_FI_DETAILED() {
        return MEANING_FI_DETAILED;
    }

    public void setMEANING_FI_DETAILED(String MEANING_FI_DETAILED) {
        this.MEANING_FI_DETAILED = MEANING_FI_DETAILED;
    }

    public String getMEANING_DE_TTS() {
        return MEANING_DE_TTS;
    }

    public void setMEANING_DE_TTS(String MEANING_DE_TTS) {
        this.MEANING_DE_TTS = MEANING_DE_TTS;
    }

    public String getMEANING_DE_DETAILED() {
        return MEANING_DE_DETAILED;
    }

    public void setMEANING_DE_DETAILED(String MEANING_DE_DETAILED) {
        this.MEANING_DE_DETAILED = MEANING_DE_DETAILED;
    }

    public String getMEANING_EL_TTS() {
        return MEANING_EL_TTS;
    }

    public void setMEANING_EL_TTS(String MEANING_EL_TTS) {
        this.MEANING_EL_TTS = MEANING_EL_TTS;
    }

    public String getMEANING_EL_DETAILED() {
        return MEANING_EL_DETAILED;
    }

    public void setMEANING_EL_DETAILED(String MEANING_EL_DETAILED) {
        this.MEANING_EL_DETAILED = MEANING_EL_DETAILED;
    }

    public String getMEANING_HE_TTS() {
        return MEANING_HE_TTS;
    }

    public void setMEANING_HE_TTS(String MEANING_HE_TTS) {
        this.MEANING_HE_TTS = MEANING_HE_TTS;
    }

    public String getMEANING_HE_DETAILED() {
        return MEANING_HE_DETAILED;
    }

    public void setMEANING_HE_DETAILED(String MEANING_HE_DETAILED) {
        this.MEANING_HE_DETAILED = MEANING_HE_DETAILED;
    }

    public String getMEANING_HI_TTS() {
        return MEANING_HI_TTS;
    }

    public void setMEANING_HI_TTS(String MEANING_HI_TTS) {
        this.MEANING_HI_TTS = MEANING_HI_TTS;
    }

    public String getMEANING_HI_DETAILED() {
        return MEANING_HI_DETAILED;
    }

    public void setMEANING_HI_DETAILED(String MEANING_HI_DETAILED) {
        this.MEANING_HI_DETAILED = MEANING_HI_DETAILED;
    }

    public String getMEANING_HU_TTS() {
        return MEANING_HU_TTS;
    }

    public void setMEANING_HU_TTS(String MEANING_HU_TTS) {
        this.MEANING_HU_TTS = MEANING_HU_TTS;
    }

    public String getMEANING_HU_DETAILED() {
        return MEANING_HU_DETAILED;
    }

    public void setMEANING_HU_DETAILED(String MEANING_HU_DETAILED) {
        this.MEANING_HU_DETAILED = MEANING_HU_DETAILED;
    }

    public String getMEANING_ID_TTS() {
        return MEANING_ID_TTS;
    }

    public void setMEANING_ID_TTS(String MEANING_ID_TTS) {
        this.MEANING_ID_TTS = MEANING_ID_TTS;
    }

    public String getMEANING_ID_DETAILED() {
        return MEANING_ID_DETAILED;
    }

    public void setMEANING_ID_DETAILED(String MEANING_ID_DETAILED) {
        this.MEANING_ID_DETAILED = MEANING_ID_DETAILED;
    }

    public String getMEANING_IT_TTS() {
        return MEANING_IT_TTS;
    }

    public void setMEANING_IT_TTS(String MEANING_IT_TTS) {
        this.MEANING_IT_TTS = MEANING_IT_TTS;
    }

    public String getMEANING_IT_DETAILED() {
        return MEANING_IT_DETAILED;
    }

    public void setMEANING_IT_DETAILED(String MEANING_IT_DETAILED) {
        this.MEANING_IT_DETAILED = MEANING_IT_DETAILED;
    }

    public String getMEANING_NO_TTS() {
        return MEANING_NO_TTS;
    }

    public void setMEANING_NO_TTS(String MEANING_NO_TTS) {
        this.MEANING_NO_TTS = MEANING_NO_TTS;
    }

    public String getMEANING_NO_DETAILED() {
        return MEANING_NO_DETAILED;
    }

    public void setMEANING_NO_DETAILED(String MEANING_NO_DETAILED) {
        this.MEANING_NO_DETAILED = MEANING_NO_DETAILED;
    }

    public String getMEANING_PL_TTS() {
        return MEANING_PL_TTS;
    }

    public void setMEANING_PL_TTS(String MEANING_PL_TTS) {
        this.MEANING_PL_TTS = MEANING_PL_TTS;
    }

    public String getMEANING_PL_DETAILED() {
        return MEANING_PL_DETAILED;
    }

    public void setMEANING_PL_DETAILED(String MEANING_PL_DETAILED) {
        this.MEANING_PL_DETAILED = MEANING_PL_DETAILED;
    }

    public String getMEANING_PT_TTS() {
        return MEANING_PT_TTS;
    }

    public void setMEANING_PT_TTS(String MEANING_PT_TTS) {
        this.MEANING_PT_TTS = MEANING_PT_TTS;
    }

    public String getMEANING_PT_DETAILED() {
        return MEANING_PT_DETAILED;
    }

    public void setMEANING_PT_DETAILED(String MEANING_PT_DETAILED) {
        this.MEANING_PT_DETAILED = MEANING_PT_DETAILED;
    }

    public String getMEANING_RO_TTS() {
        return MEANING_RO_TTS;
    }

    public void setMEANING_RO_TTS(String MEANING_RO_TTS) {
        this.MEANING_RO_TTS = MEANING_RO_TTS;
    }

    public String getMEANING_RO_DETAILED() {
        return MEANING_RO_DETAILED;
    }

    public void setMEANING_RO_DETAILED(String MEANING_RO_DETAILED) {
        this.MEANING_RO_DETAILED = MEANING_RO_DETAILED;
    }

    public String getMEANING_SK_TTS() {
        return MEANING_SK_TTS;
    }

    public void setMEANING_SK_TTS(String MEANING_SK_TTS) {
        this.MEANING_SK_TTS = MEANING_SK_TTS;
    }

    public String getMEANING_SK_DETAILED() {
        return MEANING_SK_DETAILED;
    }

    public void setMEANING_SK_DETAILED(String MEANING_SK_DETAILED) {
        this.MEANING_SK_DETAILED = MEANING_SK_DETAILED;
    }

    public String getMEANING_ES_TTS() {
        return MEANING_ES_TTS;
    }

    public void setMEANING_ES_TTS(String MEANING_ES_TTS) {
        this.MEANING_ES_TTS = MEANING_ES_TTS;
    }

    public String getMEANING_ES_DETAILED() {
        return MEANING_ES_DETAILED;
    }

    public void setMEANING_ES_DETAILED(String MEANING_ES_DETAILED) {
        this.MEANING_ES_DETAILED = MEANING_ES_DETAILED;
    }

    public String getMEANING_SV_TTS() {
        return MEANING_SV_TTS;
    }

    public void setMEANING_SV_TTS(String MEANING_SV_TTS) {
        this.MEANING_SV_TTS = MEANING_SV_TTS;
    }

    public String getMEANING_SV_DETAILED() {
        return MEANING_SV_DETAILED;
    }

    public void setMEANING_SV_DETAILED(String MEANING_SV_DETAILED) {
        this.MEANING_SV_DETAILED = MEANING_SV_DETAILED;
    }

    public String getMEANING_TH_TTS() {
        return MEANING_TH_TTS;
    }

    public void setMEANING_TH_TTS(String MEANING_TH_TTS) {
        this.MEANING_TH_TTS = MEANING_TH_TTS;
    }

    public String getMEANING_TH_DETAILED() {
        return MEANING_TH_DETAILED;
    }

    public void setMEANING_TH_DETAILED(String MEANING_TH_DETAILED) {
        this.MEANING_TH_DETAILED = MEANING_TH_DETAILED;
    }

    public String getMEANING_TR_TTS() {
        return MEANING_TR_TTS;
    }

    public void setMEANING_TR_TTS(String MEANING_TR_TTS) {
        this.MEANING_TR_TTS = MEANING_TR_TTS;
    }

    public String getMEANING_TR_DETAILED() {
        return MEANING_TR_DETAILED;
    }

    public void setMEANING_TR_DETAILED(String MEANING_TR_DETAILED) {
        this.MEANING_TR_DETAILED = MEANING_TR_DETAILED;
    }

    public String getMEANING_UK_TTS() {
        return MEANING_UK_TTS;
    }

    public void setMEANING_UK_TTS(String MEANING_UK_TTS) {
        this.MEANING_UK_TTS = MEANING_UK_TTS;
    }

    public String getMEANING_UK_DETAILED() {
        return MEANING_UK_DETAILED;
    }

    public void setMEANING_UK_DETAILED(String MEANING_UK_DETAILED) {
        this.MEANING_UK_DETAILED = MEANING_UK_DETAILED;
    }

    public String getMEANING_CH_T_TTS() {
        return MEANING_CH_T_TTS;
    }

    public void setMEANING_CH_T_TTS(String MEANING_CH_T_TTS) {
        this.MEANING_CH_T_TTS = MEANING_CH_T_TTS;
    }

    public String getMEANING_CH_T_DETAILED() {
        return MEANING_CH_T_DETAILED;
    }

    public void setMEANING_CH_T_DETAILED(String MEANING_CH_T_DETAILED) {
        this.MEANING_CH_T_DETAILED = MEANING_CH_T_DETAILED;
    }

    public String getMEANING_JP_DETAILED() {
        return MEANING_JP_DETAILED;
    }

    public void setMEANING_JP_DETAILED(String MEANING_JP_DETAILED) {
        this.MEANING_JP_DETAILED = MEANING_JP_DETAILED;
    }

    public String getMEANING_JP_TTS() {
        return MEANING_JP_TTS;
    }

    public void setMEANING_JP_TTS(String MEANING_JP_TTS) {
        this.MEANING_JP_TTS = MEANING_JP_TTS;
    }

    public String getMEANING_CH_S_DETAILED() {
        return MEANING_CH_S_DETAILED;
    }

    public void setMEANING_CH_S_DETAILED(String MEANING_CH_S_DETAILED) {
        this.MEANING_CH_S_DETAILED = MEANING_CH_S_DETAILED;
    }

    public String getMEANING_CH_S_TTS() {
        return MEANING_CH_S_TTS;
    }

    public void setMEANING_CH_S_TTS(String MEANING_CH_S_TTS) {
        this.MEANING_CH_S_TTS = MEANING_CH_S_TTS;
    }

    public String getMEANING() {
        return MEANING == null ? "" : MEANING;
    }

    public void setMEANING(String MEANING) {
        this.MEANING = MEANING;
    }

    public String getMEANING_DETAILED() {
        return MEANING_DETAILED;
    }

    public void setMEANING_DETAILED(String MEANING_DETAILED) {
        this.MEANING_DETAILED = MEANING_DETAILED;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        this.UID = UID;
    }

    public String getMEANING_AR_TTS() {
        return MEANING_AR_TTS;
    }

    public void setMEANING_AR_TTS(String MEANING_AR_TTS) {
        this.MEANING_AR_TTS = MEANING_AR_TTS;
    }

    public String getMEANING_AR_DETAILED() {
        return MEANING_AR_DETAILED;
    }

    public void setMEANING_AR_DETAILED(String MEANING_AR_DETAILED) {
        this.MEANING_AR_DETAILED = MEANING_AR_DETAILED;
    }

    public String getMEANING_FR_TTS() {
        return MEANING_FR_TTS;
    }

    public void setMEANING_FR_TTS(String MEANING_FR_TTS) {
        this.MEANING_FR_TTS = MEANING_FR_TTS;
    }

    public String getMEANING_FR_DETAILED() {
        return MEANING_FR_DETAILED;
    }

    public void setMEANING_FR_DETAILED(String MEANING_FR_DETAILED) {
        this.MEANING_FR_DETAILED = MEANING_FR_DETAILED;
    }

    public Long getCREATOR_TYPE() {
        return CREATOR_TYPE;
    }

    public void setCREATOR_TYPE(Long CREATOR_TYPE) {
        this.CREATOR_TYPE = CREATOR_TYPE;
    }

    public String getPINYINOrEmptyString() {
        return PINYIN == null ? "" : PINYIN;
    }

    public String getPINYIN() {
        return PINYIN == null ? "" : PINYIN;
    }

    public void setPINYIN(String PINYIN) {
        this.PINYIN = PINYIN;
    }

    public String getKUNYOMIOrEmptyString() {
        return KUNYOMI == null ? "" : KUNYOMI;
    }

    public String getKUNYOMI() {
        return KUNYOMI == null ? "" : KUNYOMI;
    }

    public void setKUNYOMI(String KUNYOMI) {
        this.KUNYOMI = KUNYOMI;
    }

    public String getONYOMIOrEmptyString() {
        return ONYOMI == null ? "" : ONYOMI;
    }

    public String getONYOMI() {
        return ONYOMI == null ? "" : ONYOMI;
    }

    public void setONYOMI(String ONYOMI) {
        this.ONYOMI = ONYOMI;
    }

    public String getDECOMPOSITION() {
        return DECOMPOSITION;
    }

    public void setDECOMPOSITION(String DECOMPOSITION) {
        this.DECOMPOSITION = DECOMPOSITION;
    }

    public Long getSTROKES_EXCEPT_RADICAL() {
        return STROKES_EXCEPT_RADICAL;
    }

    public void setSTROKES_EXCEPT_RADICAL(Long STROKES_EXCEPT_RADICAL) {
        this.STROKES_EXCEPT_RADICAL = STROKES_EXCEPT_RADICAL;
    }

    public String getDECOMPOSITION_ORI() {
        return DECOMPOSITION_ORI;
    }

    public void setDECOMPOSITION_ORI(String DECOMPOSITION_ORI) {
        this.DECOMPOSITION_ORI = DECOMPOSITION_ORI;
    }

    public String getCOMPOSITIONTYPE() {
        return COMPOSITIONTYPE;
    }

    public void setCOMPOSITIONTYPE(String COMPOSITIONTYPE) {
        this.COMPOSITIONTYPE = COMPOSITIONTYPE;
    }

    public String getLEFTCOMPONENT() {
        String uselessComponent = "\\*";
        return StringUtils.removeSpaces(LEFTCOMPONENT).replaceAll(uselessComponent, "");
    }

    public void setLEFTCOMPONENT(String LEFTCOMPONENT) {
        this.LEFTCOMPONENT = LEFTCOMPONENT;
    }

    public String getRIGHTCOMPONENT() {
        String uselessComponent = "\\*";
        return StringUtils.removeSpaces(RIGHTCOMPONENT).replaceAll(uselessComponent, "");
    }

    public void setRIGHTCOMPONENT(String RIGHTCOMPONENT) {
        this.RIGHTCOMPONENT = RIGHTCOMPONENT;
    }

    public String getSIGNATURE() {
        return SIGNATURE;
    }

    public void setSIGNATURE(String SIGNATURE) {
        this.SIGNATURE = SIGNATURE;
    }

    public String getNOTES() {
        return NOTES;
    }

    public void setNOTES(String NOTES) {
        this.NOTES = NOTES;
    }

    public String getVOCA_TTS() {
        return VOCA_TTS == null ? getVOCA() : VOCA_TTS;
    }

    public void setVOCA_TTS(String VOCA_TTS) {
        this.VOCA_TTS = VOCA_TTS;
    }

    public Long getUNICODE_DEC() {
        return UNICODE_DEC;
    }

    public void setUNICODE_DEC(Long UNICODE_DEC) {
        this.UNICODE_DEC = UNICODE_DEC;
    }

    public String getCOMMON_USE_JAPANOrEmptyString() {
        return COMMON_USE_JAPAN == null ? "" : COMMON_USE_JAPAN;
    }

    public String getCOMMON_USE_JAPAN() {
        return COMMON_USE_JAPAN == null ? "" : COMMON_USE_JAPAN;
    }

    public void setCOMMON_USE_JAPAN(String COMMON_USE_JAPAN) {
        this.COMMON_USE_JAPAN = COMMON_USE_JAPAN;
    }

    public String getCOMMON_USE_KOREAOrEmptyString() {
        return COMMON_USE_KOREA == null ? "" : COMMON_USE_KOREA;
    }

    public String getCOMMON_USE_KOREA() {
        return COMMON_USE_KOREA == null ? "" : COMMON_USE_KOREA;
    }

    public void setCOMMON_USE_KOREA(String COMMON_USE_KOREA) {
        this.COMMON_USE_KOREA = COMMON_USE_KOREA;
    }

    public String getLEVEL_KOREA_TYPE_1OrEmptyString() {
        return LEVEL_KOREA_TYPE_1 == null ? "" : LEVEL_KOREA_TYPE_1;
    }

    public String getLEVEL_KOREA_TYPE_1() {
        return LEVEL_KOREA_TYPE_1;
    }

    public void setLEVEL_KOREA_TYPE_1(String LEVEL_KOREA_TYPE_1) {
        this.LEVEL_KOREA_TYPE_1 = LEVEL_KOREA_TYPE_1;
    }

    public String getLEVEL_KOREA_TYPE_2OrEmptyString() {
        return LEVEL_KOREA_TYPE_2 == null ? "" : LEVEL_KOREA_TYPE_2;
    }

    public String getLEVEL_KOREA_TYPE_2() {
        return LEVEL_KOREA_TYPE_2;
    }

    public void setLEVEL_KOREA_TYPE_2(String LEVEL_KOREA_TYPE_2) {
        this.LEVEL_KOREA_TYPE_2 = LEVEL_KOREA_TYPE_2;
    }

    public String getLEVEL_KOREA_TYPE_3OrEmptyString() {
        return LEVEL_KOREA_TYPE_3 == null ? "" : LEVEL_KOREA_TYPE_3;
    }

    public String getLEVEL_KOREA_TYPE_3() {
        return LEVEL_KOREA_TYPE_3;
    }

    public void setLEVEL_KOREA_TYPE_3(String LEVEL_KOREA_TYPE_3) {
        this.LEVEL_KOREA_TYPE_3 = LEVEL_KOREA_TYPE_3;
    }

    public String getLEVEL_HSK() {
        return LEVEL_HSK;
    }

    public void setLEVEL_HSK(String LEVEL_HSK) {
        this.LEVEL_HSK = LEVEL_HSK;
    }

    public Long getVOCA_KNOW() {
        return VOCA_KNOW == null ? Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED : VOCA_KNOW;
    }

    public void setVOCA_KNOW(Long VOCA_KNOW) {
        this.VOCA_KNOW = VOCA_KNOW;
    }

    public Long getVOCA_KNOWPRONOUNCE() {
        return VOCA_KNOWPRONOUNCE == null ? Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED : VOCA_KNOWPRONOUNCE;
    }

    public void setVOCA_KNOWPRONOUNCE(Long VOCA_KNOWPRONOUNCE) {
        this.VOCA_KNOWPRONOUNCE = VOCA_KNOWPRONOUNCE;
    }

    public Long getBOOKMARK() {
        return BOOKMARK == null ? Constant.INT_BOOLEAN.FASLE : BOOKMARK;
    }

    public void setBOOKMARK(Long BOOKMARK) {
        this.BOOKMARK = BOOKMARK;
    }

    public boolean isBOOKMARK() {
        return getBOOKMARK().intValue() == Constant.INT_BOOLEAN.TRUE;
    }

    public void swapBOOKMARK() {
        if (isBOOKMARK()) {
            setBOOKMARK(0L);
        } else {
            setBOOKMARK(1L);
        }
    }

    public Long getPEOPLE_NAME() {
        return PEOPLE_NAME == null ? Constant.INT_BOOLEAN.FASLE : PEOPLE_NAME;
    }

    public void setPEOPLE_NAME(Long PEOPLE_NAME) {
        this.PEOPLE_NAME = PEOPLE_NAME;
    }

    public String getUPDATE_DATE_BOOKMARK() {
        return UPDATE_DATE_BOOKMARK;
    }

    public void setUPDATE_DATE_BOOKMARK(String UPDATE_DATE_BOOKMARK) {
        this.UPDATE_DATE_BOOKMARK = UPDATE_DATE_BOOKMARK;
    }

    public String getUPDATE_DATE_VOCA_KNOWPRONOUNCE() {
        return UPDATE_DATE_VOCA_KNOWPRONOUNCE;
    }

    public void setUPDATE_DATE_VOCA_KNOWPRONOUNCE(String UPDATE_DATE_VOCA_KNOWPRONOUNCE) {
        this.UPDATE_DATE_VOCA_KNOWPRONOUNCE = UPDATE_DATE_VOCA_KNOWPRONOUNCE;
    }

    public String getUPDATE_DATE_VOCA_KNOW() {
        return UPDATE_DATE_VOCA_KNOW;
    }

    public void setUPDATE_DATE_VOCA_KNOW(String UPDATE_DATE_VOCA_KNOW) {
        this.UPDATE_DATE_VOCA_KNOW = UPDATE_DATE_VOCA_KNOW;
    }

    public String getParentVoca() {
        return parentVoca;
    }

    public void setParentVoca(String parentVoca) {
        this.parentVoca = parentVoca;
    }

    public boolean equalFromServerHanjaToUpdateLocalData(DIC_HANJA serverHanja) {
        if (getID().equals(serverHanja.getID())
                && getUNICODE_HEX().equals(serverHanja.getUNICODE_HEX())
                && getVOCA().equals(serverHanja.getVOCA())
                && getVOCAORI().equals(serverHanja.getVOCAORI())
                && getMEANING1().equals(serverHanja.getMEANING1())
                && getPRONOUNCE1_FIRST().equals(serverHanja.getPRONOUNCE1_FIRST())
                && getVOCAORI_ID().equals(serverHanja.getVOCAORI_ID())
                && getSTROKES().equals(serverHanja.getSTROKES())
                && getIS_RADICAL().equals(serverHanja.getIS_RADICAL())
                && getHANJA_TAIWAN().equals(serverHanja.getHANJA_TAIWAN())
                && getHANJA_JAPAN().equals(serverHanja.getHANJA_JAPAN())
                && getHANJA_SIMPLIFIED().equals(serverHanja.getHANJA_SIMPLIFIED())
                && getHANJA_SHORT_FORM().equals(serverHanja.getHANJA_SHORT_FORM())
                && getHANJA_VARIANT_1().equals(serverHanja.getHANJA_VARIANT_1())
                && getHANJA_VARIANT_2().equals(serverHanja.getHANJA_VARIANT_2())
                && getHANJA_SOKJA().equals(serverHanja.getHANJA_SOKJA())
                && getHANJA_TYPE().equals(serverHanja.getHANJA_TYPE())
                && getVOCA_LEVEL().equals(serverHanja.getVOCA_LEVEL())
                && getPRONOUNCE1().equals(serverHanja.getPRONOUNCE1())
//                && getMEANING1_PRONOUNCE1_FIRST().equals(serverHanja.getMEANING1_PRONOUNCE1_FIRST())
//                && getMEANING1_PRONOUNCE1().equals(serverHanja.getMEANING1_PRONOUNCE1())
                && getMEANING2().equals(serverHanja.getMEANING2())
                && getPRONOUNCE2_FIRST().equals(serverHanja.getPRONOUNCE2_FIRST())
                && getPRONOUNCE2().equals(serverHanja.getPRONOUNCE2())
                && getMEANING3().equals(serverHanja.getMEANING3())
                && getPRONOUNCE3_FIRST().equals(serverHanja.getPRONOUNCE3_FIRST())
                && getPRONOUNCE3().equals(serverHanja.getPRONOUNCE3())
//                && getUPDATE_DATE().equals(serverHanja.getUPDATE_DATE())
                && getPRONOUNCE().equals(serverHanja.getPRONOUNCE())
                && getPRONOUNCE_USE().equals(serverHanja.getPRONOUNCE_USE())
                && getMEANING_KO().equals(serverHanja.getMEANING_KO())
                && getMEANING_CH_S().equals(serverHanja.getMEANING_CH_S())
                && getMEANING_CH_T().equals(serverHanja.getMEANING_CH_T())
                && getMEANING_JP().equals(serverHanja.getMEANING_JP())
                && getMEANING_VI().equals(serverHanja.getMEANING_VI())
                && getMEANING_UK().equals(serverHanja.getMEANING_UK())
                && getMEANING_TR().equals(serverHanja.getMEANING_TR())
                && getMEANING_TH().equals(serverHanja.getMEANING_TH())
                && getMEANING_SV().equals(serverHanja.getMEANING_SV())
                && getMEANING_ES().equals(serverHanja.getMEANING_ES())
                && getMEANING_SK().equals(serverHanja.getMEANING_SK())
                && getMEANING_RO().equals(serverHanja.getMEANING_RO())
                && getMEANING_PT().equals(serverHanja.getMEANING_PT())
                && getMEANING_PL().equals(serverHanja.getMEANING_PL())
                && getMEANING_NO().equals(serverHanja.getMEANING_NO())
                && getMEANING_IT().equals(serverHanja.getMEANING_IT())
                && getMEANING_ID().equals(serverHanja.getMEANING_ID())
                && getMEANING_HU().equals(serverHanja.getMEANING_HU())
                && getMEANING_HI().equals(serverHanja.getMEANING_HI())
                && getMEANING_HE().equals(serverHanja.getMEANING_HE())
                && getMEANING_EL().equals(serverHanja.getMEANING_EL())
                && getMEANING_DE().equals(serverHanja.getMEANING_DE())
                && getMEANING_FI().equals(serverHanja.getMEANING_FI())
                && getMEANING_NL().equals(serverHanja.getMEANING_NL())
                && getMEANING_DA().equals(serverHanja.getMEANING_DA())
                && getMEANING_CS().equals(serverHanja.getMEANING_CS())
                && getMEANING_HR().equals(serverHanja.getMEANING_HR())
                && getMEANING_BN().equals(serverHanja.getMEANING_BN())
                && getMEANING_AR().equals(serverHanja.getMEANING_AR())
                && getMEANING_RU().equals(serverHanja.getMEANING_RU())
                && getMEANING_FR().equals(serverHanja.getMEANING_FR())
                && getMEANING_ENG().equals(serverHanja.getMEANING_ENG())
                && getMEANING_KO_DETAILED().equals(serverHanja.getMEANING_KO_DETAILED())
                && getMEANING_KO_TTS().equals(serverHanja.getMEANING_KO_TTS())
                && getMEANING_ENG_DETAILED().equals(serverHanja.getMEANING_ENG_DETAILED())
                && getMEANING_ENG_TTS().equals(serverHanja.getMEANING_ENG_TTS())
                && getMEANING_VI_DETAILED().equals(serverHanja.getMEANING_VI_DETAILED())
                && getMEANING_VI_TTS().equals(serverHanja.getMEANING_VI_TTS())
                && getMEANING_RU_TTS().equals(serverHanja.getMEANING_RU_TTS())
                && getMEANING_RU_DETAILED().equals(serverHanja.getMEANING_RU_DETAILED())
                && getMEANING_BN_TTS().equals(serverHanja.getMEANING_BN_TTS())
                && getMEANING_BN_DETAILED().equals(serverHanja.getMEANING_BN_DETAILED())
                && getMEANING_HR_TTS().equals(serverHanja.getMEANING_HR_TTS())
                && getMEANING_HR_DETAILED().equals(serverHanja.getMEANING_HR_DETAILED())
                && getMEANING_CS_TTS().equals(serverHanja.getMEANING_CS_TTS())
                && getMEANING_CS_DETAILED().equals(serverHanja.getMEANING_CS_DETAILED())
                && getMEANING_DA_TTS().equals(serverHanja.getMEANING_DA_TTS())
                && getMEANING_DA_DETAILED().equals(serverHanja.getMEANING_DA_DETAILED())
                && getMEANING_NL_TTS().equals(serverHanja.getMEANING_NL_TTS())
                && getMEANING_NL_DETAILED().equals(serverHanja.getMEANING_NL_DETAILED())
                && getMEANING_FI_TTS().equals(serverHanja.getMEANING_FI_TTS())
                && getMEANING_FI_DETAILED().equals(serverHanja.getMEANING_FI_DETAILED())
                && getMEANING_DE_TTS().equals(serverHanja.getMEANING_DE_TTS())
                && getMEANING_DE_DETAILED().equals(serverHanja.getMEANING_DE_DETAILED())
                && getMEANING_EL_TTS().equals(serverHanja.getMEANING_EL_TTS())
                && getMEANING_EL_DETAILED().equals(serverHanja.getMEANING_EL_DETAILED())
                && getMEANING_HE_TTS().equals(serverHanja.getMEANING_HE_TTS())
                && getMEANING_HE_DETAILED().equals(serverHanja.getMEANING_HE_DETAILED())
                && getMEANING_HI_TTS().equals(serverHanja.getMEANING_HI_TTS())
                && getMEANING_HI_DETAILED().equals(serverHanja.getMEANING_HI_DETAILED())
                && getMEANING_HU_TTS().equals(serverHanja.getMEANING_HU_TTS())
                && getMEANING_HU_DETAILED().equals(serverHanja.getMEANING_HU_DETAILED())
                && getMEANING_ID_TTS().equals(serverHanja.getMEANING_ID_TTS())
                && getMEANING_ID_DETAILED().equals(serverHanja.getMEANING_ID_DETAILED())
                && getMEANING_IT_TTS().equals(serverHanja.getMEANING_IT_TTS())
                && getMEANING_IT_DETAILED().equals(serverHanja.getMEANING_IT_DETAILED())
                && getMEANING_NO_TTS().equals(serverHanja.getMEANING_NO_TTS())
                && getMEANING_NO_DETAILED().equals(serverHanja.getMEANING_NO_DETAILED())
                && getMEANING_PL_TTS().equals(serverHanja.getMEANING_PL_TTS())
                && getMEANING_PL_DETAILED().equals(serverHanja.getMEANING_PL_DETAILED())
                && getMEANING_PT_TTS().equals(serverHanja.getMEANING_PT_TTS())
                && getMEANING_PT_DETAILED().equals(serverHanja.getMEANING_PT_DETAILED())
                && getMEANING_RO_TTS().equals(serverHanja.getMEANING_RO_TTS())
                && getMEANING_RO_DETAILED().equals(serverHanja.getMEANING_RO_DETAILED())
                && getMEANING_SK_TTS().equals(serverHanja.getMEANING_SK_TTS())
                && getMEANING_SK_DETAILED().equals(serverHanja.getMEANING_SK_DETAILED())
                && getMEANING_ES_TTS().equals(serverHanja.getMEANING_ES_TTS())
                && getMEANING_ES_DETAILED().equals(serverHanja.getMEANING_ES_DETAILED())
                && getMEANING_SV_TTS().equals(serverHanja.getMEANING_SV_TTS())
                && getMEANING_SV_DETAILED().equals(serverHanja.getMEANING_SV_DETAILED())
                && getMEANING_TH_TTS().equals(serverHanja.getMEANING_TH_TTS())
                && getMEANING_TH_DETAILED().equals(serverHanja.getMEANING_TH_DETAILED())
                && getMEANING_TR_TTS().equals(serverHanja.getMEANING_TR_TTS())
                && getMEANING_TR_DETAILED().equals(serverHanja.getMEANING_TR_DETAILED())
                && getMEANING_UK_TTS().equals(serverHanja.getMEANING_UK_TTS())
                && getMEANING_UK_DETAILED().equals(serverHanja.getMEANING_UK_DETAILED())
                && getMEANING_CH_T_TTS().equals(serverHanja.getMEANING_CH_T_TTS())
                && getMEANING_CH_T_DETAILED().equals(serverHanja.getMEANING_CH_T_DETAILED())
                && getMEANING_JP_DETAILED().equals(serverHanja.getMEANING_JP_DETAILED())
                && getMEANING_JP_TTS().equals(serverHanja.getMEANING_JP_TTS())
                && getMEANING_CH_S_DETAILED().equals(serverHanja.getMEANING_CH_S_DETAILED())
                && getMEANING_CH_S_TTS().equals(serverHanja.getMEANING_CH_S_TTS())
//                && getUID().equals(serverHanja.getUID())
                && getMEANING_AR_TTS().equals(serverHanja.getMEANING_AR_TTS())
                && getMEANING_AR_DETAILED().equals(serverHanja.getMEANING_AR_DETAILED())
                && getMEANING_FR_TTS().equals(serverHanja.getMEANING_FR_TTS())
                && getMEANING_FR_DETAILED().equals(serverHanja.getMEANING_FR_DETAILED())
//                && getCREATOR_TYPE().equals(serverHanja.getCREATOR_TYPE())
                && getPINYIN().equals(serverHanja.getPINYIN())
                && getKUNYOMI().equals(serverHanja.getKUNYOMI())
                && getONYOMI().equals(serverHanja.getONYOMI())
                && getDECOMPOSITION().equals(serverHanja.getDECOMPOSITION())
                && getSTROKES_EXCEPT_RADICAL().equals(serverHanja.getSTROKES_EXCEPT_RADICAL())
                && getDECOMPOSITION_ORI().equals(serverHanja.getDECOMPOSITION_ORI())
                && getCOMPOSITIONTYPE().equals(serverHanja.getCOMPOSITIONTYPE())
                && getLEFTCOMPONENT().equals(serverHanja.getLEFTCOMPONENT())
                && getRIGHTCOMPONENT().equals(serverHanja.getRIGHTCOMPONENT())
                && getSIGNATURE().equals(serverHanja.getSIGNATURE())
//                && getNOTES().equals(serverHanja.getNOTES())
                && getVOCA_TTS().equals(serverHanja.getVOCA_TTS())
                && getUNICODE_DEC().equals(serverHanja.getUNICODE_DEC())
                && getCOMMON_USE_JAPAN().equals(serverHanja.getCOMMON_USE_JAPAN())
                && getCOMMON_USE_KOREA().equals(serverHanja.getCOMMON_USE_KOREA())
                && getLEVEL_KOREA_TYPE_1().equals(serverHanja.getLEVEL_KOREA_TYPE_1())
                && getLEVEL_KOREA_TYPE_2().equals(serverHanja.getLEVEL_KOREA_TYPE_2())
                && getLEVEL_KOREA_TYPE_3().equals(serverHanja.getLEVEL_KOREA_TYPE_3())
                && getLEVEL_HSK().equals(serverHanja.getLEVEL_HSK())
                && getPEOPLE_NAME().equals(serverHanja.getPEOPLE_NAME())
//                && getVOCA_KNOW().equals(serverHanja.getVOCA_KNOW())
//                && getVOCA_KNOWPRONOUNCE().equals(serverHanja.getVOCA_KNOWPRONOUNCE())
//                && getBOOKMARK().equals(serverHanja.getBOOKMARK())
//                && getUPDATE_DATE_BOOKMARK().equals(serverHanja.getUPDATE_DATE_BOOKMARK())
//                && getUPDATE_DATE_VOCA_KNOWPRONOUNCE().equals(serverHanja.getUPDATE_DATE_VOCA_KNOWPRONOUNCE())
//                && getUPDATE_DATE_VOCA_KNOW().equals(serverHanja.getUPDATE_DATE_VOCA_KNOW())
        ) {
            return true;
        }
        return false;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DIC_HANJA)) return false;
        DIC_HANJA dic_hanja = (DIC_HANJA) o;
        return Objects.equals(getID(), dic_hanja.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    @Override
    public Long getHI_INDEX() {
        return index;
    }

    @Override
    public void setHI_INDEX(Long INDEX) {
        index = INDEX;
    }

    @Override
    public Long getHI_ID() {
        return getID();
    }

    @Override
    public int getHI_VOCA_TYPE() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
    }

    @Override
    public String getHI_VOCA() {
        return getVOCA();
    }

    @Override
    public String getHI_VOCA_WITH_VOCAORI() {
        String voca = getVOCA();
        String vocaOri = getVOCAORI();
        if (!voca.equals(vocaOri) && (!vocaOri.equals(""))) {
            voca = voca + " (" + vocaOri + ")";
        }
        return voca;
    }

    @Override
    public String getHI_VOCAORI() {
        return getVOCAORI();
    }

    @Override
    public String getHI_ALL_TEXT_FOR_NORMAL_TEXT() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add(getVOCA() + " " + getMEANING_KO());
        if (!Utils.isEmpty(getPINYIN())) {
            sj.add(getPINYIN());
        }
        if (!Utils.isEmpty(getKUNYOMI())) {
            sj.add(getKUNYOMI());
        }
        if (!Utils.isEmpty(getONYOMI())) {
            sj.add(getONYOMI());
        }
        if (!Utils.isEmpty(getUNICODE_HEX())) {
            sj.add(getUNICODE_HEX());
        }

        if (!Utils.isEmpty(getMEANING_KO_DETAILED())) {
            sj.add("");
            sj.add(getMEANING_KO_DETAILED());
        }

        if (!Utils.isEmpty(getMEANING_ENG())) {
            sj.add("");
            sj.add(getMEANING_ENG());
        }
        if (!Utils.isEmpty(getMEANING_ENG_DETAILED())) {
            sj.add(getMEANING_ENG_DETAILED());
        }
        return sj.toString();
    }

    @Override
    public String getHI_ALL_TEXT() {
        return getALL_TEXT();
    }

    public String getALL_TEXT() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getVOCA());
        stringBuilder.append(getVOCAORI());
        stringBuilder.append(getRADICAL());
        stringBuilder.append(getHANJA_KOREA());
        stringBuilder.append(getHANJA_SIMPLIFIED());
        stringBuilder.append(getHANJA_JAPAN());
        stringBuilder.append(getHANJA_SHORT_FORM());
        stringBuilder.append(getHANJA_SOKJA());
        stringBuilder.append(getHANJA_TAIWAN());
        stringBuilder.append(getHANJA_VARIANT_1());
        stringBuilder.append(getHANJA_VARIANT_2());
        stringBuilder.append(getLEFTCOMPONENT());
        stringBuilder.append(getRIGHTCOMPONENT());
        stringBuilder.append(getMEANING_KO());
        stringBuilder.append(getMEANING_KO_DETAILED());
        return stringBuilder.toString();
    }

    @Override
    public String getHI_MEANING_AND_DETAILED() {
        return getMEANING_KO_DETAILED();
    }

    @Override
    public String getHI_MEANING(Context context) {
        return getMeaning(context);
    }

    @Override
    public String getHI_MEANING_DETAILED(Context context) {
        return getMeaningDetailed(context);
    }

    @Override
    public String getHI_MEANING1() {
        return getMEANING1();
    }

    @Override
    public String getHI_PRONOUNCE1_FIRST() {
        return getPRONOUNCE1_FIRST();
    }

    @Override
    public Long getHI_VOCA_KNOW() {
        return getVOCA_KNOW();
    }

    @Override
    public void setHI_VOCA_KNOW(Long VOCA_KNOW) {
        setVOCA_KNOW(VOCA_KNOW);
    }

    @Override
    public Long getHI_VOCA_KNOWPRONOUNCE() {
        return getVOCA_KNOWPRONOUNCE();
    }

    @Override
    public void setHI_VOCA_KNOWPRONOUNCE(Long VOCA_KNOWPRONOUNCE) {
        setVOCA_KNOWPRONOUNCE(VOCA_KNOWPRONOUNCE);
    }

    @Override
    public Long getHI_VOCA_LEVEL() {
        return getVOCA_LEVEL();
    }

    @Override
    public Long getHI_BOOKMARK() {
        return getBOOKMARK();
    }

    @Override
    public void setHI_BOOKMARK(Long BOOKMARK) {
        setBOOKMARK(BOOKMARK);
    }

    @Override
    public void swapHI_BOOKMARK() {
        swapBOOKMARK();
    }

    @Override
    public boolean isHI_BOOKMARK() {
        return isBOOKMARK();
    }

    @Override
    public int getAmkiId() {
        return getID().intValue();
    }

    @Override
    public int getAmkiType() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
    }

    @Override
    public int getAmkiKnow() {
        return getVOCA_KNOW().intValue();
    }

    @Override
    public int getAmkiKnowPronounce() {
        return getVOCA_KNOWPRONOUNCE().intValue();
    }

    @Override
    public String getAmkiEvaluationGrade() {
        return "";
    }

    @Override
    public String getAmki() {
        return getVOCA();
    }

    @Override
    public String getHQIVoca() {
        return getVOCA();
    }

    @Override
    public String getHQIMeaningWithPronounceForHanja() {
        return getMEANING1_PRONOUNCE1_FIRST();
    }

    @Override
    public String getHQIParentVoca() {
        return getParentVoca();
    }

    @Override
    public void setHQIParentVoca(String parentVoca) {
        setParentVoca(parentVoca);
    }

    @Override
    public int getHQIIndexHanja() {
        return index.intValue();
    }

    @Override
    public void setHQIIndexHanja(int indexHanja) {
        index = Long.valueOf(indexHanja);
    }



    @Override
    public Integer getVIIndex() {
        return index.intValue();
    }

    @Override
    public String getVIPosAll() {
        return "";
    }

    @Override
    public Integer getVIId() {
        return Math.toIntExact(ID);
    }

    @Override
    public String getVIVoca() {
        return VOCA;
    }

    @Override
    public Integer getVIVocaKnow() {
        return getVOCA_KNOW().intValue();
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return getVOCA_KNOWPRONOUNCE().intValue();
    }

    @Override
    public String getVIPronounce() {
        return PRONOUNCE == null ? "" : PRONOUNCE;
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguageString) {
        return getMEANING_KO();
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguageString) {
        return getMEANING_KO_DETAILED();
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguageString) {
        return getMEANING_KO_TTS();
    }

    @Override
    public String getVIMeaningEng() {
        return getMEANING_ENG();
    }

    @Override
    public String getVIMeaningEngDetailed() {
        return getMEANING_ENG_DETAILED();
    }

    @Override
    public String getVIMeaningEngTts() {
        return getMEANING_ENG_TTS();
    }

    @Override
    public Integer getVIBookmark() {
        return getBOOKMARK().intValue();
    }

    @Override
    public boolean isVIBookmark() {
        return isBOOKMARK();
    }

    @Override
    public void setVIId(Integer value) {
        //Nothing to do
    }

    @Override
    public void setVIVoca(String value) {
        VOCA = value;
    }

    @Override
    public String getVIVocaTTS() {
        return VOCA_TTS;
    }

    @Override
    public void setVIVocaTTS(String value) {
        VOCA_TTS = value;
    }

    @Override
    public void setVIPronounce(String value) {
        PRONOUNCE = value;
    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguageString, String value) {
        MEANING = value;
    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguageString, String value) {
        MEANING_DETAILED = value;
    }

    @Override
    public void setVIMeaningTts(EnumLanguage enumLanguageString, String value) {

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
        VOCA_KNOW = value.longValue();
    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {
        VOCA_KNOWPRONOUNCE = value.longValue();
    }

    @Override
    public void setVIBookmark(Integer value) {
        BOOKMARK = value.longValue();
    }

    @Override
    public void swapVIBookmark() {
        swapBOOKMARK();
    }

    @Override
    public Integer getVIVocaTypeBase() {
        return -1;
    }

    @Override
    public Integer getVIVocaIdBase() {
        return -1;
    }

    @Override
    public void setVIVocaTypeBase(Integer value) {
        //Do nothing
    }

    @Override
    public void setVIVocaIdBase(Integer value) {
        //Do nothing
    }

    @Override
    public Integer getVIVocaType() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
    }

    @Override
    public Integer getVIVocaId() {
        return ID.intValue();
    }

    @Override
    public void setVIVocaType(Integer value) {
        //Do nothing.
    }

    @Override
    public void setVIVocaId(Integer value) {
        ID = value.longValue();
    }

    @Override
    public boolean isVIChecked() {
        return checked;
    }

    @Override
    public void setVIChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean isVIPlaying() {
        return playing;
    }

    @Override
    public void setVIPlaying(boolean playing) {
        this.playing = playing;
    }

    @Override
    public boolean isVIRecording() {
        return false;
    }

    @Override
    public void setVIRecording(boolean recording) {

    }

    @Override
    public String getVIPath() {
        return path;
    }

    @Override
    public void setVIPath(String path) {
        this.path = path;
    }

    @Override
    public void setVIIndex(Integer index) {
        this.index = Long.valueOf(index);
    }

    @Override
    public boolean hasVIVoiceFile() {
        return false;
    }

    @Override
    public void setVIVoiceFile(Integer value) {

    }

    @Override
    public int getVIVoiceFileVersion() {
        return 0;
    }

    @Override
    public String getPersonAB() {
        return "";
    }

    public String getMeaning(Context context) {
        String result = getMEANING_KO();
        if (EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getMenuLanguage()) != EnumLanguage.KOREAN) {
            result = getMEANING_ENG();
        }
        return result;
    }

    public String getMeaningDetailed(Context context) {
        String result = getMEANING_KO_DETAILED();
        if (EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getMenuLanguage()) != EnumLanguage.KOREAN) {
            result = getMEANING_ENG_DETAILED();
        }
        return result;
    }
}
