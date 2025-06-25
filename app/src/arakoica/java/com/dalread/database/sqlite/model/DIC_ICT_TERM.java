package com.dalread.database.sqlite.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.io.Serializable;

public class DIC_ICT_TERM implements Serializable, IVocaBasicItem {
    private int ID;
    private int INDEX;
    private String TERM_ENG_ABBR;
    private String TERM_ENG_FULL;
    private String TERM_KO_TITLE;
    private String TERM_HANJA_TITLE;
    private String TERM_KO_SHORT;
    private String TERM_KO_FULL;
    private String URL;
    private String MEMO; //Todo : 나중에 메모를 사용하게 되면, 서버에서 최신 데이타를 가져오게 하면 유저가 쓴 메모가 사라지게 된다. 별도 처리가 필요함.

    private int GROUP_ICT;
    private int GROUP_ETC;
    private int GROUP_GIS;
    private int GROUP_HW;
    private String GROUP_NATION;
    private int ORIGINAL_ID;
    private int EDITED;
    private int VOCA_KNOW;
    private int BOOKMARK;
    private String UPDATE_DATE;
    private String SEARCH_HISTORY;
    private int TERM_LEVEL = Constant.TERM_LEVEL.DEFAULT;

    private boolean useEngFullAsEngAbbr = false;
    private boolean useKoShortAsEngAbbr = false;
    private boolean useKoShortAsEngFull = false;

    /* 복사 팩터리 */
    public static DIC_ICT_TERM copy(DIC_ICT_TERM original) {
        DIC_ICT_TERM copy = new DIC_ICT_TERM();
        copy.ID = original.ID;
        copy.INDEX = original.INDEX;
        copy.TERM_ENG_ABBR = original.TERM_ENG_ABBR;
        copy.TERM_ENG_FULL = original.TERM_ENG_FULL;
        copy.TERM_KO_TITLE = original.TERM_KO_TITLE;
        copy.TERM_HANJA_TITLE = original.TERM_HANJA_TITLE;
        copy.TERM_KO_SHORT = original.TERM_KO_SHORT;
        copy.TERM_KO_FULL = original.TERM_KO_FULL;
        copy.URL = original.URL;
        copy.MEMO = original.MEMO;
        copy.GROUP_ICT = original.GROUP_ICT;
        copy.GROUP_ETC = original.GROUP_ETC;
        copy.GROUP_GIS = original.GROUP_GIS;
        copy.GROUP_HW = original.GROUP_HW;
        copy.GROUP_NATION = original.GROUP_NATION;
        copy.ORIGINAL_ID = original.ORIGINAL_ID;
        copy.EDITED = original.EDITED;
        copy.VOCA_KNOW = original.VOCA_KNOW;
        copy.BOOKMARK = original.BOOKMARK;
        copy.UPDATE_DATE = original.UPDATE_DATE;
        copy.SEARCH_HISTORY = original.SEARCH_HISTORY;
        copy.TERM_LEVEL = original.TERM_LEVEL;
        return copy;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getINDEX() {
        return INDEX;
    }

    public void setINDEX(int INDEX) {
        this.INDEX = INDEX;
    }

    public String getTERM_ENG_ABBR() {
        return TERM_ENG_ABBR == null ? "" : TERM_ENG_ABBR.trim();
    }

    public void setTERM_ENG_ABBR(String TERM_ENG_ABBR) {
        this.TERM_ENG_ABBR = TERM_ENG_ABBR;
    }

    public String getTERM_ENG_FULL() {
        return TERM_ENG_FULL == null ? "" : TERM_ENG_FULL.trim();
    }

    public void setTERM_ENG_FULL(String TERM_ENG_FULL) {
        this.TERM_ENG_FULL = TERM_ENG_FULL;
    }

    public String getTERM_KO_TITLE() {
        return  TERM_KO_TITLE == null ? "" : TERM_KO_TITLE.trim();
    }

    public void setTERM_KO_TITLE(String TERM_KO_TITLE) {
        this.TERM_KO_TITLE = TERM_KO_TITLE;
    }

    public String getTERM_HANJA_TITLE() {
        return TERM_HANJA_TITLE == null ? "" : TERM_HANJA_TITLE.trim();
    }

    public void setTERM_HANJA_TITLE(String TERM_HANJA_TITLE) {
        this.TERM_HANJA_TITLE = TERM_HANJA_TITLE;
    }

    public String getTERM_KO_FULL() {
        return TERM_KO_FULL == null ? "" : TERM_KO_FULL.trim();
    }

    public void setTERM_KO_FULL(String TERM_KO_FULL) {
        this.TERM_KO_FULL = TERM_KO_FULL;
    }

    public String getTERM_KO_SHORT() {
        return TERM_KO_SHORT == null ? "" : TERM_KO_SHORT.trim();
    }

    public void setTERM_KO_SHORT(String TERM_KO_SHORT) {
        this.TERM_KO_SHORT = TERM_KO_SHORT;
    }

    public String getURL() {
        return URL == null ? "" : URL.trim();
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getTERM_LEVEL() {
        return TERM_LEVEL;
    }

    public void setTERM_LEVEL(int TERM_LEVEL) {
        this.TERM_LEVEL = TERM_LEVEL;
    }

    public String getMEMO() {
        return MEMO == null ? "" : MEMO.trim();
    }

    public void setMEMO(String MEMO) {
        this.MEMO = MEMO;
    }

    public int getGROUP_ICT() {
        return GROUP_ICT;
    }

    public void setGROUP_ICT(int GROUP_ICT) {
        this.GROUP_ICT = GROUP_ICT;
    }
    public boolean isGROUP_ICT() {
        return getGROUP_ICT() == 0 ? false : true;
    }
    public int getGROUP_ETC() {
        return GROUP_ETC;
    }

    public void setGROUP_ETC(int GROUP_ETC) {
        this.GROUP_ETC = GROUP_ETC;
    }
    public boolean isGROUP_ETC() {
        return getGROUP_ETC() == 0 ? false : true;
    }

    public int getGROUP_GIS() {
        return GROUP_GIS;
    }

    public void setGROUP_GIS(int GROUP_GIS) {
        this.GROUP_GIS = GROUP_GIS;
    }
    public boolean isGROUP_GIS() {
        return getGROUP_GIS() == 0 ? false : true;
    }
    public int getGROUP_HW() {
        return GROUP_HW;
    }

    public void setGROUP_HW(int GROUP_HW) {
        this.GROUP_HW = GROUP_HW;
    }
    public boolean isGROUP_HW() {
        return getGROUP_HW() == 0 ? false : true;
    }
    public String getGROUP_NATION() {
        return GROUP_NATION == null ? "" : GROUP_NATION.trim();
    }

    public void setGROUP_NATION(String GROUP_NATION) {
        this.GROUP_NATION = GROUP_NATION;
    }

    public int getORIGINAL_ID() {
        return ORIGINAL_ID;
    }

    public void setORIGINAL_ID(int ORIGINAL_ID) {
        this.ORIGINAL_ID = ORIGINAL_ID;
    }

    public int getEDITED() {
        return EDITED;
    }

    public void setEDITED(int EDITED) {
        this.EDITED = EDITED;
    }

    public int getVOCA_KNOW() {
        return VOCA_KNOW;
    }

    public void setVOCA_KNOW(int VOCA_KNOW) {
        this.VOCA_KNOW = VOCA_KNOW;
    }

    public int getBOOKMARK() {
        return BOOKMARK;
    }

    public void setBOOKMARK(int BOOKMARK) {
        this.BOOKMARK = BOOKMARK;
    }

    public String getUPDATE_DATE() {
        return UPDATE_DATE == null ? "" : UPDATE_DATE;
    }

    public void setUPDATE_DATE(String UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public String getSEARCH_HISTORY() {
        return SEARCH_HISTORY;
    }

    public void setSEARCH_HISTORY(String SEARCH_HISTORY) {
        this.SEARCH_HISTORY = SEARCH_HISTORY;
    }

    @Override
    public Integer getVIId() {
        return getID();
    }

    @Override
    public String getVIVoca() {
        return getTERM_ENG_ABBR();
    }

    @Override
    public Integer getVIVocaKnow() {
        return getVOCA_KNOW();
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    @Override
    public String getVIVocaTTS() {
        return null;
    }

    @Override
    public String getVIPronounce() {
        return "";
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        return getTERM_KO_TITLE();
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return getTERM_KO_FULL();
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguage) {
        return null;
    }

    @Override
    public String getVIMeaningEng() {
        return null;
    }

    @Override
    public String getVIMeaningEngDetailed() {
        return null;
    }

    @Override
    public String getVIMeaningEngTts() {
        return null;
    }

    @Override
    public Integer getVIBookmark() {
        return getBOOKMARK();
    }

    @Override
    public boolean isVIBookmark() {
        return false;
    }

    @Override
    public void setVIId(Integer value) {

    }

    @Override
    public void setVIVoca(String value) {

    }

    @Override
    public void setVIVocaTTS(String value) {

    }

    @Override
    public void setVIPronounce(String value) {

    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguage, String value) {

    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguage, String value) {

    }

    @Override
    public void setVIMeaningTts(EnumLanguage enumLanguage, String value) {

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

    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {

    }

    @Override
    public void setVIBookmark(Integer value) {
        setBOOKMARK(value);
    }

    @Override
    public void swapVIBookmark() {
        swapBOOKMARK();
    }
    public boolean isBOOKMARK() {
        return getBOOKMARK() == Constant.INT_BOOLEAN.TRUE;
    }
    public void swapBOOKMARK() {
        if (isBOOKMARK()) {
            setBOOKMARK(Constant.INT_BOOLEAN.FASLE);
        } else {
            setBOOKMARK(Constant.INT_BOOLEAN.TRUE);
        }
    }
    @Override
    public Integer getVIVocaTypeBase() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_KOICA;
    }

    @Override
    public Integer getVIVocaIdBase() {
        return null;
    }

    @Override
    public void setVIVocaTypeBase(Integer value) {

    }

    @Override
    public void setVIVocaIdBase(Integer value) {

    }

    @Override
    public boolean hasVIVoiceFile() {
        return false;
    }

    @Override
    public Integer getVIVocaType() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_KOICA;
    }

    @Override
    public Integer getVIVocaId() {
        return getID();
    }

    @Override
    public void setVIVocaType(Integer value) {

    }

    @Override
    public void setVIVocaId(Integer value) {

    }

    public String getKoAndHanjaShort() {
        String koShort = getTERM_KO_TITLE();
        String hanjaShort = getTERM_HANJA_TITLE();
        String result = "";
        if (!Utils.isEmpty(koShort) && !Utils.isEmpty(hanjaShort)) {
            result = koShort + " (" + hanjaShort + ")";
        } else if (!Utils.isEmpty(koShort)) {
            result = koShort;
        } else if (!Utils.isEmpty(hanjaShort)) {
            result = hanjaShort;
        }
        return result;
    }
    private void resetUseAs() {
        useEngFullAsEngAbbr = false;
        useKoShortAsEngAbbr = false;
        useKoShortAsEngFull = false;
    }
    public String getFirstItemToDisplay() {
        resetUseAs();
        return getTERM_ENG_ABBR();
//        if (getTERM_ENG_ABBR().length() > 0) {
//            return getTERM_ENG_ABBR();
//        } else if (getTERM_ENG_FULL().length() > 0) {
//            useEngFullAsEngAbbr = true;
//            return getTERM_ENG_FULL();
//        } else if (getKoAndHanjaShort().length() > 0) {
//            useKoShortAsEngAbbr = true;
//            return getKoAndHanjaShort();
//        }
//        return "";
    }

    public String getSecondItemToDisplay() {
        return getTERM_ENG_FULL();
//        if (useEngFullAsEngAbbr) {
//            if (getKoAndHanjaShort().length() > 0) {
//                useKoShortAsEngFull = true;
//                return getKoAndHanjaShort();
//            }
//        } else if (useKoShortAsEngAbbr) {
//            useEngFullAsEngAbbr = true;
//        } else if (getTERM_ENG_FULL().length() > 0 ) {
//            return getTERM_ENG_FULL();
//        }
//        return "";
    }


    public String getKoreanShortOrFullToDisplay() {
        return getTERM_KO_SHORT().trim().equals("") ? getTERM_KO_FULL() : getTERM_KO_SHORT();
    }
    public String getThirdItemToDisplay() {
        return getKoAndHanjaShort();
//        if (!useEngFullAsEngAbbr && !useKoShortAsEngFull) {
//            if (getKoAndHanjaShort().length() > 0) {
//                return getKoAndHanjaShort();
//            }
//        }
//        return "";
    }

    public String getHanjaToSearchInWebDictionary() {
        if (getTERM_HANJA_TITLE().length() > 0) {
            return getTERM_HANJA_TITLE();
        }
        return getTERM_KO_TITLE();
    }

    public String getAllValue() {
        return getTERM_ENG_ABBR() + "\t" + getTERM_ENG_FULL() + "\t" + getTERM_KO_TITLE() + "\t" + getTERM_HANJA_TITLE() + "\t" + getTERM_KO_FULL();
    }

    public boolean isEdited(DIC_ICT_TERM item) {
        if (!(this.getTERM_ENG_ABBR().equals(item.getTERM_ENG_ABBR()))) {
            return true;
        }
        if (!(this.getTERM_ENG_FULL().equals(item.getTERM_ENG_FULL()))) {
            return true;
        }
        if (!(this.getTERM_KO_TITLE().equals(item.getTERM_KO_TITLE()))) {
            return true;
        }
        if (!(this.getTERM_HANJA_TITLE().equals(item.getTERM_HANJA_TITLE()))) {
            return true;
        }
        if (!(this.getTERM_KO_SHORT().equals(item.getTERM_KO_SHORT()))) {
            return true;
        }
        if (!(this.getTERM_KO_FULL().equals(item.getTERM_KO_FULL()))) {
            return true;
        }
        if (!(this.getURL().equals(item.getURL()))) {
            return true;
        }
        if (!(this.getMEMO().equals(item.getMEMO()))) {
            return true;
        }
        if (this.getGROUP_ICT() != item.getGROUP_ICT()) {
            return true;
        }
        if (this.getGROUP_ETC() != item.getGROUP_ETC()) {
            return true;
        }
        if (this.getGROUP_GIS() != item.getGROUP_GIS()) {
            return true;
        }
        if (this.getGROUP_HW() != item.getGROUP_HW()) {
            return true;
        }


        if (!(this.getGROUP_NATION().equals(item.getGROUP_NATION()))) {
            return true;
        }
        if (!(this.getTERM_ENG_ABBR().equals(item.getTERM_ENG_ABBR()))) {
            return true;
        }
        if (!(this.getTERM_ENG_ABBR().equals(item.getTERM_ENG_ABBR()))) {
            return true;
        }
        if (this.getTERM_LEVEL() != item.getTERM_LEVEL()) {
            return true;
        }
        return false;
    }

}
