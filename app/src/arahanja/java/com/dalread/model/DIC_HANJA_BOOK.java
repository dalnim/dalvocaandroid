// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.dalread.model;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.util.Constant;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class DIC_HANJA_BOOK extends RealmObject implements HanjaItem, Serializable, IVocaBasicItem {

    @PrimaryKey
    private Long ID;
    private Long GROUP_ID_1;
    private Long GROUP_ID_2;
    private Long GROUP_ID_3;
    private Long GROUP_ID_4;
    private String VOCA;
    private String PRONOUNCE;
    private String MEANING_KO;
    private String MEANING_KO_DETAILED;
    private String MEANING_ENG;
    private String MEANING_ENG_DETAILED;
    private String UPDATE_DATE;
    private Long EDITED;
    private Long VOCA_KNOW;
    private Long VOCA_KNOWPRONOUNCE;
    private Long BOOKMARK;
    private String MEMO;
    @Ignore
    private Long index;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Long getGROUP_ID_1() {
        return GROUP_ID_1;
    }

    public void setGROUP_ID_1(Long GROUP_ID_1) {
        this.GROUP_ID_1 = GROUP_ID_1;
    }

    public Long getGROUP_ID_2() {
        return GROUP_ID_2;
    }

    public void setGROUP_ID_2(Long GROUP_ID_2) {
        this.GROUP_ID_2 = GROUP_ID_2;
    }

    public Long getGROUP_ID_3() {
        return GROUP_ID_3;
    }

    public void setGROUP_ID_3(Long GROUP_ID_3) {
        this.GROUP_ID_3 = GROUP_ID_3;
    }

    public Long getGROUP_ID_4() {
        return GROUP_ID_4;
    }

    public void setGROUP_ID_4(Long GROUP_ID_4) {
        this.GROUP_ID_4 = GROUP_ID_4;
    }

    public String getVOCA() {
        return StringUtils.trimAndNormalizeString(VOCA);
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public String getPRONOUNCE() {
        return PRONOUNCE;
    }

    public void setPRONOUNCE(String PRONOUNCE) {
        this.PRONOUNCE = PRONOUNCE;
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

    public String getMEMO() {
        return MEMO;
    }

    public void setMEMO(String MEMO) {
        this.MEMO = MEMO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DIC_HANJA_BOOK)) return false;
        DIC_HANJA_BOOK dic_hanja_books = (DIC_HANJA_BOOK) o;
        return Objects.equals(getID(), dic_hanja_books.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    @Override
    public int getHI_VOCA_TYPE() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_BOOK;
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
        sj.add(getVOCA());
        if (!Utils.isEmpty(getPRONOUNCE())) {
            sj.add(getPRONOUNCE());
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
        stringBuilder.append(getMEANING_KO());
        stringBuilder.append(getMEANING_KO_DETAILED());
        return stringBuilder.toString();
    }

    @Override
    public String getHI_MEANING_AND_DETAILED() {
        return getALL_TEXT();
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
        return getPRONOUNCE();
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
        return Long.valueOf(Constant.VOCA_LEVEL_INDIC_MAX);
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

    public boolean equalFromServerHanjaToUpdateLocalData(DIC_HANJA_BOOK serverHanjaBook) {
        if (getID().equals(serverHanjaBook.getID())
                && getVOCA().equals(serverHanjaBook.getVOCA())
                && getPRONOUNCE().equals(serverHanjaBook.getPRONOUNCE())
                && getMEANING_KO().equals(serverHanjaBook.getMEANING_KO())
                && getMEANING_KO_DETAILED().equals(serverHanjaBook.getMEANING_KO_DETAILED()) ) {
            return true;
        }
        return false;

    }


    @Override
    public Integer getVIId() {
        return ID.intValue();
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
    public String getVIVocaTTS() {
        return VOCA;
    }

    @Override
    public String getVIPronounce() {
        return PRONOUNCE == null ? "" : PRONOUNCE;
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguageString) {
        return MEANING_KO == null ? "" : MEANING_KO;
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguageString) {
        return MEANING_KO_DETAILED == null ? "" : MEANING_KO_DETAILED;
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguageString) {
        return getVIMeaning(enumLanguageString);
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
        //Need to check this is necessary. ID means VOCA_ID here.
//        ID = value.longValue();
    }

    @Override
    public void setVIVoca(String value) {
        VOCA = value;
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
    public void setVIMeaningDetailed(EnumLanguage enumLanguageString, String value) {
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
        return Constant.API_VALUE.VALUE_VOCA_TYPE_BOOK;
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
