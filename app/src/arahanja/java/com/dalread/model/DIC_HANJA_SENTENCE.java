// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.dalread.model;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.util.Constant;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class DIC_HANJA_SENTENCE extends RealmObject implements HanjaItem, AmkiItem, HanjaQuizItem, IVocaFullItem, Serializable {

    @PrimaryKey
    private Long ID;
    private String VOCA = "";
    private String VOCA_JP = "";
    private String VOCA_CH_S = "";
    private String PRONOUNCE = "";
    private String MEANING_KO = "";
    private String MEANING_KO_DETAILED = "";
    private String MEANING_ENG = "";
    private String MEANING_ENG_DETAILED = "";
    private Long VOCA_LEVEL;
    private String UPDATE_DATE = "";
    private Long EDITED = -1L;
    private String PRONOUNCE_JP = "";
    private String PRONOUNCE_CH_S = "";
    private Long VOCA_KNOW = (long) Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
    private Long VOCA_KNOWPRONOUNCE = (long) Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
    private Long BOOKMARK = -1L;
    private Long DELETED = 0L;
    @Ignore
    private String parentVoca;
    @Ignore
    private Long index;

    public DIC_HANJA_SENTENCE() {

    }
    public DIC_HANJA_SENTENCE(String VOCA) {
        this.VOCA = VOCA;
        this.ID = -1L;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getVOCA() {
        return StringUtils.trimAndNormalizeString(VOCA);
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public String getVOCA_JP() {
        return StringUtils.trimAndNormalizeString(VOCA_JP);
    }

    public void setVOCA_JP(String VOCA_JP) {
        this.VOCA_JP = VOCA_JP;
    }

    public String getVOCA_CH_S() {
        return StringUtils.trimAndNormalizeString(VOCA_CH_S);
    }

    public void setVOCA_CH_S(String VOCA_CH_S) {
        this.VOCA_CH_S = VOCA_CH_S;
    }

    public String getPRONOUNCEOrEmptyString() {
        return PRONOUNCE == null ? "" : PRONOUNCE;
    }

    public String getPRONOUNCE() {
        return PRONOUNCE == null ? "" : PRONOUNCE;
    }

    public void setPRONOUNCE(String PRONOUNCE) {
        this.PRONOUNCE = PRONOUNCE;
    }

    public String getMEANING_KOOrEmptyString() {
        return MEANING_KO == null ? "" : MEANING_KO;
    }

    public String getMEANING_KO() {
        return StringUtils.trimAndNormalizeString(MEANING_KO);
    }

    public void setMEANING_KO(String MEANING_KO) {
        this.MEANING_KO = MEANING_KO;
    }

    public String getMEANING_KO_DETAILED() {
        return StringUtils.trimAndNormalizeString(MEANING_KO_DETAILED);
    }

    public void setMEANING_KO_DETAILED(String MEANING_KO_DETAILED) {
        this.MEANING_KO_DETAILED = MEANING_KO_DETAILED;
    }

    public String getMEANING_ENG() {
        return StringUtils.trimAndNormalizeString(MEANING_ENG);
    }

    public void setMEANING_ENG(String MEANING_ENG) {
        this.MEANING_ENG = MEANING_ENG;
    }

    public String getMEANING_ENG_DETAILED() {
        return StringUtils.trimAndNormalizeString(MEANING_ENG_DETAILED);
    }

    public void setMEANING_ENG_DETAILED(String MEANING_ENG_DETAILED) {
        this.MEANING_ENG_DETAILED = MEANING_ENG_DETAILED;
    }

    public Long getVOCA_LEVEL() {
        return VOCA_LEVEL == null ? Constant.VOCA_LEVEL_INDIC_MAX : VOCA_LEVEL;
    }

    public void setVOCA_LEVEL(Long VOCA_LEVEL) {
        this.VOCA_LEVEL = VOCA_LEVEL;
    }

    public String getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setUPDATE_DATE(String UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public Long getEDITED() {
        return EDITED;
    }

    public void setEDITED(Long EDITED) {
        this.EDITED = EDITED;
    }

    public String getPRONOUNCE_JP() {
        return PRONOUNCE_JP;
    }

    public void setPRONOUNCE_JP(String PRONOUNCE_JP) {
        this.PRONOUNCE_JP = PRONOUNCE_JP;
    }

    public String getPRONOUNCE_CH_S() {
        return PRONOUNCE_CH_S;
    }

    public void setPRONOUNCE_CH_S(String PRONOUNCE_CH_S) {
        this.PRONOUNCE_CH_S = PRONOUNCE_CH_S;
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

    public Long getDELETED() {
        return DELETED;
    }

    public void setDELETED(Long DELETED) {
        this.DELETED = DELETED;
    }

    public String getParentVoca() {
        return parentVoca;
    }

    public void setParentVoca(String parentVoca) {
        this.parentVoca = parentVoca;
    }

    public boolean equalFromServerHanjaToUpdateLocalData(DIC_HANJA_SENTENCE serverHanjaSentence) {
        if (getID().equals(serverHanjaSentence.getID())
                && getVOCA().equals(serverHanjaSentence.getVOCA())
                && getVOCA_CH_S().equals(serverHanjaSentence.getVOCA_CH_S())
                && getVOCA_JP().equals(serverHanjaSentence.getVOCA_JP())
                && getPRONOUNCE().equals(serverHanjaSentence.getPRONOUNCE())
                && getMEANING_KO().equals(serverHanjaSentence.getMEANING_KO())
                && getMEANING_KO_DETAILED().equals(serverHanjaSentence.getMEANING_KO_DETAILED())
                && getMEANING_ENG().equals(serverHanjaSentence.getMEANING_ENG())
                && getMEANING_ENG_DETAILED().equals(serverHanjaSentence.getMEANING_ENG_DETAILED())
                && getVOCA_LEVEL().equals(serverHanjaSentence.getVOCA_LEVEL())
                && getPRONOUNCE_JP().equals(serverHanjaSentence.getPRONOUNCE_JP())
                && getPRONOUNCE_CH_S().equals(serverHanjaSentence.getPRONOUNCE_CH_S()) ) {
            return true;
        }
        return false;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DIC_HANJA_SENTENCE)) return false;
        DIC_HANJA_SENTENCE dic_hanja_sentence = (DIC_HANJA_SENTENCE) o;
        return Objects.equals(getID(), dic_hanja_sentence.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    @Override
    public int getHI_VOCA_TYPE() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE;
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
    public String getHI_VOCA() {
        return getVOCA();
    }

    @Override
    public String getHI_VOCA_WITH_VOCAORI() {
        return getVOCA();
    }

    @Override
    public String getHI_VOCAORI() {
        return ""; // do not have getVOCAORI();
    }

    @Override
    public String getHI_ALL_TEXT_FOR_NORMAL_TEXT() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add(getVOCA() + " " + getPRONOUNCE());
        if (!Utils.isEmpty(getVOCA_CH_S())) {
            sj.add("");
            sj.add(getVOCA_CH_S() + " " + getPRONOUNCE_CH_S());
        }
        if (!Utils.isEmpty(getVOCA_JP())) {
            sj.add("");
            sj.add(getVOCA_JP() + " " + getPRONOUNCE_JP());
        }
        if (!Utils.isEmpty(getMEANING_KO())) {
            sj.add("");
            sj.add(getMEANING_KO());
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
            sj.add("");
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
        stringBuilder.append(getVOCA_JP());
        stringBuilder.append(getVOCA_CH_S());
        stringBuilder.append(getMEANING_KO());
        stringBuilder.append(getMEANING_KO_DETAILED());
        return stringBuilder.toString();
    }

    @Override
    public String getHI_MEANING_AND_DETAILED() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getMEANING_KO());
        stringBuilder.append(getMEANING_KO_DETAILED());
        return stringBuilder.toString();
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
        return getMEANING_KO() + getMEANING_KO_DETAILED();
    }

    @Override
    public String getHI_PRONOUNCE1_FIRST() {
        return getPRONOUNCE(); // Return getPRONOUNCE() because do not have getPRONOUNCE1_FIRST();
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
        return Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE;
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
        return getMEANING_KO();
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
    public void setVIIndex(Integer index) {
        this.index = Long.valueOf(index);
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
        return getVOCA();
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
    public String getVIMeaning(EnumLanguage enumLanguage) {
        return MEANING_KO == null ? "" : MEANING_KO;
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return MEANING_KO_DETAILED == null ? "" : MEANING_KO_DETAILED;
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguage) {
        return getVIMeaning(enumLanguage);
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
        return getMEANING_ENG();
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

    }

    @Override
    public void setVIVoca(String value) {
        VOCA = value;
    }

    @Override
    public String getVIVocaTTS() {
        return "";
    }

    @Override
    public void setVIVocaTTS(String value) {
        //Do nothing
    }

    @Override
    public void setVIPronounce(String value) {
        PRONOUNCE = value;
    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguageString, String value) {
        MEANING_KO = value;
    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguage, String value) {
        MEANING_KO_DETAILED = value;
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
        return Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE;
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
    public boolean hasVIVoiceFile() {
        return false;
    }

    @Override
    public void setVIVoiceFile(Integer value) {

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
