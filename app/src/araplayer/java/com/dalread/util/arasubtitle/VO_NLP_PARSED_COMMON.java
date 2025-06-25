package com.dalread.util.arasubtitle;

public class VO_NLP_PARSED_COMMON {
    private Integer ID;
    private Integer LANG_STUDY;
    private Integer VOCA_TYPE;
    private Integer VOCA_ID;
    private Integer USED;
    protected String VOCA;
    protected String VOCA_NLP_PARSED;

    public void setVOCA(String value) {
        VOCA = (value == null) ? "" : value.trim();
    }

    public void setVOCA_NLP_PARSED(String value) {
        VOCA_NLP_PARSED = (value == null) ? "" : value.trim();
    }

    public Integer getLANG_STUDY() {
        return LANG_STUDY;
    }

    public void setLANG_STUDY(Integer LANG_STUDY) {
        this.LANG_STUDY = LANG_STUDY;
    }

    public Integer getVOCA_TYPE() {
        return VOCA_TYPE;
    }

    public void setVOCA_TYPE(Integer VOCA_TYPE) {
        this.VOCA_TYPE = VOCA_TYPE;
    }

    public Integer getVOCA_ID() {
        return VOCA_ID;
    }

    public void setVOCA_ID(Integer VOCA_ID) {
        this.VOCA_ID = VOCA_ID;
    }

    public Integer getUSED() {
        return USED;
    }

    public void setUSED(Integer USED) {
        this.USED = USED;
    }

    public String getVOCA() {
        return VOCA;
    }

    public String getVOCA_NLP_PARSED() {
        return VOCA_NLP_PARSED;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }
}