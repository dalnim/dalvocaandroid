package com.dalread.util.arasubtitle;

public class DTO_INPUT_COMMON_LESSON extends DTO_INPUT_COMMON {
    protected  Integer LESSON_ID = 0;
    protected  Integer CHATROOM_ID = 0;
    protected  Integer READING_ID = 0;
    protected  Integer LESSON_TYPE = 0;
    protected  Integer STUDENT_ID = 0;
    protected  Integer TUTOR_ID = 0;
    protected  Integer OPPONENT_UID = 0;
    protected  Integer VOCABOOK_TYPE = 0;
    protected  Integer VOCABOOKS_ID = 0;

    protected Integer STUDY_ROLE = Constants.STUDY_ROLE_STUDENT;
    protected String STUDENT_langDisplay = Constants.LANG_KO;
    protected String LANG_MEANING_FOR_STUDENT = Constants.LANG_KO;
    protected Integer LANG_MEANING_CODE_FOR_STUDENT = Constants.LANGCODE_KO;

    public void setSTUDENT_langDisplay(String strOne) {
        this.STUDENT_langDisplay = strOne;
        this.LANG_MEANING_CODE_FOR_STUDENT = convertLanguageNameToCode(strOne);
        this.LANG_MEANING_FOR_STUDENT = strOne;
    }

    public Integer getLESSON_ID() {
        return LESSON_ID;
    }

    public void setLESSON_ID(Integer LESSON_ID) {
        this.LESSON_ID = LESSON_ID;
    }

    public Integer getCHATROOM_ID() {
        return CHATROOM_ID;
    }

    public void setCHATROOM_ID(Integer CHATROOM_ID) {
        this.CHATROOM_ID = CHATROOM_ID;
    }

    public Integer getREADING_ID() {
        return READING_ID;
    }

    public void setREADING_ID(Integer READING_ID) {
        this.READING_ID = READING_ID;
    }

    public Integer getLESSON_TYPE() {
        return LESSON_TYPE;
    }

    public void setLESSON_TYPE(Integer LESSON_TYPE) {
        this.LESSON_TYPE = LESSON_TYPE;
    }

    public Integer getSTUDENT_ID() {
        return STUDENT_ID;
    }

    public void setSTUDENT_ID(Integer STUDENT_ID) {
        this.STUDENT_ID = STUDENT_ID;
    }

    public Integer getTUTOR_ID() {
        return TUTOR_ID;
    }

    public void setTUTOR_ID(Integer TUTOR_ID) {
        this.TUTOR_ID = TUTOR_ID;
    }

    public Integer getOPPONENT_UID() {
        return OPPONENT_UID;
    }

    public void setOPPONENT_UID(Integer OPPONENT_UID) {
        this.OPPONENT_UID = OPPONENT_UID;
    }

    public Integer getVOCABOOK_TYPE() {
        return VOCABOOK_TYPE;
    }

    public void setVOCABOOK_TYPE(Integer VOCABOOK_TYPE) {
        this.VOCABOOK_TYPE = VOCABOOK_TYPE;
    }

    public Integer getVOCABOOKS_ID() {
        return VOCABOOKS_ID;
    }

    public void setVOCABOOKS_ID(Integer VOCABOOKS_ID) {
        this.VOCABOOKS_ID = VOCABOOKS_ID;
    }

    public Integer getSTUDY_ROLE() {
        return STUDY_ROLE;
    }

    public void setSTUDY_ROLE(Integer STUDY_ROLE) {
        this.STUDY_ROLE = STUDY_ROLE;
    }

    public String getSTUDENT_langDisplay() {
        return STUDENT_langDisplay;
    }

    public String getLANG_MEANING_FOR_STUDENT() {
        return LANG_MEANING_FOR_STUDENT;
    }

    public void setLANG_MEANING_FOR_STUDENT(String LANG_MEANING_FOR_STUDENT) {
        this.LANG_MEANING_FOR_STUDENT = LANG_MEANING_FOR_STUDENT;
    }

    public Integer getLANG_MEANING_CODE_FOR_STUDENT() {
        return LANG_MEANING_CODE_FOR_STUDENT;
    }

    public void setLANG_MEANING_CODE_FOR_STUDENT(Integer LANG_MEANING_CODE_FOR_STUDENT) {
        this.LANG_MEANING_CODE_FOR_STUDENT = LANG_MEANING_CODE_FOR_STUDENT;
    }
}