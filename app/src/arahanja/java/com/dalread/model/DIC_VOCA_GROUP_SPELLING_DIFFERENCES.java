// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.dalread.model;

import io.realm.RealmObject;

public class DIC_VOCA_GROUP_SPELLING_DIFFERENCES extends RealmObject {
    private Long ID;
    private String VOCA;
    private Long VOCA_ID;
    private Long GROUP_ID;
    private Long DISP_ORDER;
    private Long VOCA_TYPE;
    private Long LANG_STUDY;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getVOCA() {
        return VOCA;
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public Long getVOCA_ID() {
        return VOCA_ID;
    }

    public void setVOCA_ID(Long VOCA_ID) {
        this.VOCA_ID = VOCA_ID;
    }

    public Long getGROUP_ID() {
        return GROUP_ID;
    }

    public void setGROUP_ID(Long GROUP_ID) {
        this.GROUP_ID = GROUP_ID;
    }

    public Long getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(Long DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }

    public Long getVOCA_TYPE() {
        return VOCA_TYPE;
    }

    public void setVOCA_TYPE(Long VOCA_TYPE) {
        this.VOCA_TYPE = VOCA_TYPE;
    }

    public Long getLANG_STUDY() {
        return LANG_STUDY;
    }

    public void setLANG_STUDY(Long LANG_STUDY) {
        this.LANG_STUDY = LANG_STUDY;
    }


}
