package com.dalread.util.arasubtitle;

public class VO_DIC_COMMON extends VO_COMMON_MEANING_HIDE_ALL {
    private Integer ID;
    private String WORD;
    private String VOCA; //이건
    private Integer WORD_SAME_ID;
    private Integer VOCA_SAME_ID;
    private String WORDORI;
    private String VOCAORI;
    private Integer WORDORI_ID;
    private Integer VOCAORI_ID;
    private Integer VOCA_TYPE_ORI;
    private Integer VOCA_ID_ORI;
    private Integer WORDLEVEL;
    private Integer VOCA_LEVEL;
    private String WORD_DISPLAY;
    private String VOCA_DISPLAY;
    private String WORD_TTS;
    private String VOCA_TTS;


    //이건 한자테이블에만 있는거다.
    //---------------------
    private String MEANING1;
    private String PRONOUNCE1_FIRST;
    private String PRONOUNCE1;
    //---------------------

    private String SENTENCE_ID_LIST;

    private String PRONOUNCE;
    private Integer PRONOUNCE_USE;
    private String POSALL;
    private String POS;
    private String POS2;
    private String POS3;
    private String POS4;
    private String POS6;
    private String POS5;

    private Integer UID;
    private Integer CREATOR_TYPE;

    //이건 SERVER_VOCABOOK에 있는거다
    private String WORDORI_ID_LIST;
    private String VOCAORI_ID_LIST;
    private String WORD_ID_LIST;
    private String VOCA_ID_LIST;

    //이건 SERVER_VOCABOOK에 있는거다
    private String FAKER_CATEGORY_A;
    private String FAKER_CATEGORY_B;
    private String FAKER_CATEGORY_C;

    private String NAME_ITEM;
    private String NAME_ITEM_SUB_DESC;
    private String NAME_SHOP;
    private String PERSON_AB;
    private String PRICE_FIXED;
    private Integer CATEGORY_ID;
    private Integer DISP_ORDER;
    private Integer DISP_ORDER_NAME_ITEM;
    private Integer LANGUAGE_LEVEL_FROM;
    private Integer LANGUAGE_LEVEL_TO;
    //	private Integer LANG_STUDY;
    private Integer MUST_INCLUDE;
    private Integer USED;
    private Integer USE_ROLE_PLAYING_MENU;
    private Integer USE_ROLE_PLAYING_RANDOM_VALUE;
    private Integer USE_SERVER_VOCABOOK_DATA;
    private Integer USE_SHOP_NAME_FOR_RANDOM_VALUE;
    private Integer VOCABOOKS_ID;
    private Integer VOCA_ID;
    private Integer VOCA_TYPE;

    //=======
    public void setWORDORI_ID_LIST(String value) {
        WORDORI_ID_LIST = (value == null) ? "" : value.trim();
        VOCAORI_ID_LIST = (value == null) ? "" : value.trim();
    }
    public void setWORD_ID_LIST(String value) {
        WORD_ID_LIST = (value == null) ? "" : value.trim();
        VOCA_ID_LIST = (value == null) ? "" : value.trim();
    }

    public void setWORD_DISPLAY(String value) {
        WORD_DISPLAY = (value == null) ? "" : value.trim();
        VOCA_DISPLAY = (value == null) ? "" : value.trim();
    }
    public void setWORD_TTS(String value) {
        if ((value == null) || (value.trim().equals(""))) {
            WORD_TTS = this.VOCA.trim();
            VOCA_TTS = this.VOCA.trim();
        } else {
            WORD_TTS = (value == null) ? "" : value.trim();
            VOCA_TTS = (value == null) ? "" : value.trim();
        }
    }
    public void setWORD(String value) {
        WORD = (value == null) ? "" : value.trim();
        VOCA = (value == null) ? "" : value.trim();
    }
    public void setWORD_SAME_ID(Integer value) {
        WORD_SAME_ID = (value == null) ? 0 : value;
        VOCA_SAME_ID = (value == null) ? 0 : value;
    }
    public void setWORDORI(String value) {
        WORDORI = (value == null) ? "" : value.trim();
        VOCAORI = (value == null) ? "" : value.trim();
    }
    public void setWORDORI_ID(Integer value) {
        WORDORI_ID = (value == null) ? 0 : value;
        VOCAORI_ID = (value == null) ? 0 : value;
    }
    public void setWORDLEVEL(Integer value) {
        WORDLEVEL = (value == null) ? 0 : value;
        VOCA_LEVEL = (value == null) ? 0 : value;
    }


    //==========
    public void setID(Integer value) {
        ID = (value == null) ? 0 : value;
    }
    public void setVOCA_TYPE_ORI(Integer value) {
        VOCA_TYPE_ORI = (value == null) ? 0 : value;
    }
    public void setVOCA_ID_ORI(Integer vOCA_ID_ORI) {
        VOCA_ID_ORI = vOCA_ID_ORI;
    }
    public void setMEANING1(String value) {
        MEANING1 = (value == null) ? "" : value.trim();
    }
    public void setPRONOUNCE1_FIRST(String value) {
        PRONOUNCE1_FIRST = (value == null) ? "" : value.trim();
    }
    public void setPRONOUNCE1(String value) {
        PRONOUNCE1 = (value == null) ? "" : value.trim();
    }
    public void setPRONOUNCE(String value) {
        PRONOUNCE = (value == null) ? "" : value.trim();
    }
    public void setPRONOUNCE_USE(Integer value) {
        PRONOUNCE_USE = (value == null) ? 0 : value;
    }
    public void setPOSALL(String value) {
        POSALL = (value == null) ? "" : value.trim();
    }
    public void setPOS(String value) {
        POS = (value == null) ? "" : value.trim();
    }
    public void setPOS2(String value) {
        POS2 = (value == null) ? "" : value.trim();
    }
    public void setPOS3(String value) {
        POS3 = (value == null) ? "" : value.trim();
    }
    public void setPOS4(String value) {
        POS4 = (value == null) ? "" : value.trim();
    }
    public void setPOS6(String value) {
        POS6 = (value == null) ? "" : value.trim();
    }
    public void setPOS5(String value) {
        POS5 = (value == null) ? "" : value.trim();
    }
    public void setSENTENCE_ID_LIST(String value) {
        SENTENCE_ID_LIST = (value == null) ? "" : value.trim();
    }

    public void setUID(Integer value) {
        UID = (value == null) ? 0 : value;
    }
    public void setCREATOR_TYPE(Integer value) {
        CREATOR_TYPE = (value == null) ? 0 : value;
    }


    public void setFAKER_CATEGORY_A(String value) {
        FAKER_CATEGORY_A = (value == null) ? "" : value.trim();
    }
    public void setFAKER_CATEGORY_B(String value) {
        FAKER_CATEGORY_B = (value == null) ? "" : value.trim();
    }
    public void setFAKER_CATEGORY_C(String value) {
        FAKER_CATEGORY_C = (value == null) ? "" : value.trim();
    }
    public void setNAME_ITEM(String value) {
        NAME_ITEM = (value == null) ? "" : value.trim();
    }
    public void setNAME_ITEM_SUB_DESC(String value) {
        NAME_ITEM_SUB_DESC = (value == null) ? "" : value.trim();
    }
    public void setNAME_SHOP(String value) {
        NAME_SHOP = (value == null) ? "" : value.trim();
    }
    public void setPERSON_AB(String value) {
        PERSON_AB = (value == null) ? "" : value.trim();
    }
    public void setPRICE_FIXED(String value) {
        PRICE_FIXED = (value == null) ? "" : value.trim();
    }

    public void setVOCA_TYPE(Integer value) {
        VOCA_TYPE = (value == null) ? Constants.VOCA_TYPE_WORD : value;
    }

    public Integer getID() {
        return ID;
    }

    public String getWORD() {
        return WORD;
    }

    public String getVOCA() {
        return VOCA;
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public Integer getWORD_SAME_ID() {
        return WORD_SAME_ID;
    }

    public Integer getVOCA_SAME_ID() {
        return VOCA_SAME_ID;
    }

    public void setVOCA_SAME_ID(Integer VOCA_SAME_ID) {
        this.VOCA_SAME_ID = VOCA_SAME_ID;
    }

    public String getWORDORI() {
        return WORDORI;
    }

    public String getVOCAORI() {
        return VOCAORI;
    }

    public void setVOCAORI(String VOCAORI) {
        this.VOCAORI = VOCAORI;
    }

    public Integer getWORDORI_ID() {
        return WORDORI_ID;
    }

    public Integer getVOCAORI_ID() {
        return VOCAORI_ID;
    }

    public void setVOCAORI_ID(Integer VOCAORI_ID) {
        this.VOCAORI_ID = VOCAORI_ID;
    }

    public Integer getVOCA_TYPE_ORI() {
        return VOCA_TYPE_ORI;
    }

    public Integer getVOCA_ID_ORI() {
        return VOCA_ID_ORI;
    }

    public Integer getWORDLEVEL() {
        return WORDLEVEL;
    }

    public Integer getVOCA_LEVEL() {
        return VOCA_LEVEL;
    }

    public void setVOCA_LEVEL(Integer VOCA_LEVEL) {
        this.VOCA_LEVEL = VOCA_LEVEL;
    }

    public String getWORD_DISPLAY() {
        return WORD_DISPLAY;
    }

    public String getVOCA_DISPLAY() {
        return VOCA_DISPLAY;
    }

    public void setVOCA_DISPLAY(String VOCA_DISPLAY) {
        this.VOCA_DISPLAY = VOCA_DISPLAY;
    }

    public String getWORD_TTS() {
        return WORD_TTS;
    }

    public String getVOCA_TTS() {
        return VOCA_TTS;
    }

    public void setVOCA_TTS(String VOCA_TTS) {
        this.VOCA_TTS = VOCA_TTS;
    }

    public String getMEANING1() {
        return MEANING1;
    }

    public String getPRONOUNCE1_FIRST() {
        return PRONOUNCE1_FIRST;
    }

    public String getPRONOUNCE1() {
        return PRONOUNCE1;
    }

    public String getSENTENCE_ID_LIST() {
        return SENTENCE_ID_LIST;
    }

    public String getPRONOUNCE() {
        return PRONOUNCE;
    }

    public Integer getPRONOUNCE_USE() {
        return PRONOUNCE_USE;
    }

    public String getPOSALL() {
        return POSALL;
    }

    public String getPOS() {
        return POS;
    }

    public String getPOS2() {
        return POS2;
    }

    public String getPOS3() {
        return POS3;
    }

    public String getPOS4() {
        return POS4;
    }

    public String getPOS6() {
        return POS6;
    }

    public String getPOS5() {
        return POS5;
    }

    public Integer getUID() {
        return UID;
    }

    public Integer getCREATOR_TYPE() {
        return CREATOR_TYPE;
    }

    public String getWORDORI_ID_LIST() {
        return WORDORI_ID_LIST;
    }

    public String getVOCAORI_ID_LIST() {
        return VOCAORI_ID_LIST;
    }

    public void setVOCAORI_ID_LIST(String VOCAORI_ID_LIST) {
        this.VOCAORI_ID_LIST = VOCAORI_ID_LIST;
    }

    public String getWORD_ID_LIST() {
        return WORD_ID_LIST;
    }

    public String getVOCA_ID_LIST() {
        return VOCA_ID_LIST;
    }

    public void setVOCA_ID_LIST(String VOCA_ID_LIST) {
        this.VOCA_ID_LIST = VOCA_ID_LIST;
    }

    public String getFAKER_CATEGORY_A() {
        return FAKER_CATEGORY_A;
    }

    public String getFAKER_CATEGORY_B() {
        return FAKER_CATEGORY_B;
    }

    public String getFAKER_CATEGORY_C() {
        return FAKER_CATEGORY_C;
    }

    public String getNAME_ITEM() {
        return NAME_ITEM;
    }

    public String getNAME_ITEM_SUB_DESC() {
        return NAME_ITEM_SUB_DESC;
    }

    public String getNAME_SHOP() {
        return NAME_SHOP;
    }

    public String getPERSON_AB() {
        return PERSON_AB;
    }

    public String getPRICE_FIXED() {
        return PRICE_FIXED;
    }

    public Integer getCATEGORY_ID() {
        return CATEGORY_ID;
    }

    public void setCATEGORY_ID(Integer CATEGORY_ID) {
        this.CATEGORY_ID = CATEGORY_ID;
    }

    public Integer getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(Integer DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }

    public Integer getDISP_ORDER_NAME_ITEM() {
        return DISP_ORDER_NAME_ITEM;
    }

    public void setDISP_ORDER_NAME_ITEM(Integer DISP_ORDER_NAME_ITEM) {
        this.DISP_ORDER_NAME_ITEM = DISP_ORDER_NAME_ITEM;
    }

    public Integer getLANGUAGE_LEVEL_FROM() {
        return LANGUAGE_LEVEL_FROM;
    }

    public void setLANGUAGE_LEVEL_FROM(Integer LANGUAGE_LEVEL_FROM) {
        this.LANGUAGE_LEVEL_FROM = LANGUAGE_LEVEL_FROM;
    }

    public Integer getLANGUAGE_LEVEL_TO() {
        return LANGUAGE_LEVEL_TO;
    }

    public void setLANGUAGE_LEVEL_TO(Integer LANGUAGE_LEVEL_TO) {
        this.LANGUAGE_LEVEL_TO = LANGUAGE_LEVEL_TO;
    }

    public Integer getMUST_INCLUDE() {
        return MUST_INCLUDE;
    }

    public void setMUST_INCLUDE(Integer MUST_INCLUDE) {
        this.MUST_INCLUDE = MUST_INCLUDE;
    }

    public Integer getUSED() {
        return USED;
    }

    public void setUSED(Integer USED) {
        this.USED = USED;
    }

    public Integer getUSE_ROLE_PLAYING_MENU() {
        return USE_ROLE_PLAYING_MENU;
    }

    public void setUSE_ROLE_PLAYING_MENU(Integer USE_ROLE_PLAYING_MENU) {
        this.USE_ROLE_PLAYING_MENU = USE_ROLE_PLAYING_MENU;
    }

    public Integer getUSE_ROLE_PLAYING_RANDOM_VALUE() {
        return USE_ROLE_PLAYING_RANDOM_VALUE;
    }

    public void setUSE_ROLE_PLAYING_RANDOM_VALUE(Integer USE_ROLE_PLAYING_RANDOM_VALUE) {
        this.USE_ROLE_PLAYING_RANDOM_VALUE = USE_ROLE_PLAYING_RANDOM_VALUE;
    }

    public Integer getUSE_SERVER_VOCABOOK_DATA() {
        return USE_SERVER_VOCABOOK_DATA;
    }

    public void setUSE_SERVER_VOCABOOK_DATA(Integer USE_SERVER_VOCABOOK_DATA) {
        this.USE_SERVER_VOCABOOK_DATA = USE_SERVER_VOCABOOK_DATA;
    }

    public Integer getUSE_SHOP_NAME_FOR_RANDOM_VALUE() {
        return USE_SHOP_NAME_FOR_RANDOM_VALUE;
    }

    public void setUSE_SHOP_NAME_FOR_RANDOM_VALUE(Integer USE_SHOP_NAME_FOR_RANDOM_VALUE) {
        this.USE_SHOP_NAME_FOR_RANDOM_VALUE = USE_SHOP_NAME_FOR_RANDOM_VALUE;
    }

    public Integer getVOCABOOKS_ID() {
        return VOCABOOKS_ID;
    }

    public void setVOCABOOKS_ID(Integer VOCABOOKS_ID) {
        this.VOCABOOKS_ID = VOCABOOKS_ID;
    }

    public Integer getVOCA_ID() {
        return VOCA_ID;
    }

    public void setVOCA_ID(Integer VOCA_ID) {
        this.VOCA_ID = VOCA_ID;
    }

    public Integer getVOCA_TYPE() {
        return VOCA_TYPE;
    }
}

