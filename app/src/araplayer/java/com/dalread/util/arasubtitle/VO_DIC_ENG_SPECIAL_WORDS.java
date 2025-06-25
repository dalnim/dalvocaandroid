package com.dalread.util.arasubtitle;

public class VO_DIC_ENG_SPECIAL_WORDS extends VO_COMMON_LANG_STUDY_CODE {
    private Integer ID;
    protected String FULL_WORD;
    protected String START_WORD;
    protected String EACH_WORD;
    protected Integer DISP_ORDER = Constants.DISP_ORDER_MAX;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getFULL_WORD() {
        return FULL_WORD;
    }

    public void setFULL_WORD(String FULL_WORD) {
        this.FULL_WORD = FULL_WORD;
    }

    public String getSTART_WORD() {
        return START_WORD;
    }

    public void setSTART_WORD(String START_WORD) {
        this.START_WORD = START_WORD;
    }

    public String getEACH_WORD() {
        return EACH_WORD;
    }

    public void setEACH_WORD(String EACH_WORD) {
        this.EACH_WORD = EACH_WORD;
    }

    public Integer getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(Integer DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }
}