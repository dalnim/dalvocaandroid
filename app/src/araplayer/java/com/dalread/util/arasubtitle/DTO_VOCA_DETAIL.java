package com.dalread.util.arasubtitle;

import java.util.ArrayList;
import java.util.List;

public class DTO_VOCA_DETAIL extends DTO_VOCA_SIMPLE {
    protected Integer ID = 0;
    protected String VOCA_DISPLAY_RUBY_TEXT = "";
    protected List<DTO_VOCA_DETAIL_RUBY_TEXT> VOCA_DISPLAY_RUBY_VOCA_LIST = new ArrayList<>();
    protected Integer DISP_ORDER = Constants.DISP_ORDER_MAX;
    protected Integer VOCAORI_ID = 0;
    protected Integer VOCA_KNOW = Constants.VOCA_KNOW.NOTRATED;
    protected Integer VOCA_KNOWPRONOUNCE = Constants.VOCA_KNOW.KNOWN;
    protected Integer STUDY_COUNT = 0;
    protected Integer HAS_VOICE_FILE = Constants.HAS_FILE_NO;
    protected Integer VOICE_FILE_VERSION = Constants.FILES_VOICE_VERSION_NOT_UPLOADED_TO_SERVER;
    protected Integer FILE_VERSION = Constants.FILES_VOICE_VERSION_NOT_UPLOADED_TO_SERVER;
    protected String EVALUATE_VOCA_GRADE = Constants.EVALUATE_VOCA_GRADE_A;
    protected String EVALUATE_VOCA_GRADE_TUTORS = Constants.EVALUATE_VOCA_GRADE_A;
    protected Integer RE_RECORD_STATUS = Constants.RE_RECORD_STATUS_NONE;
    protected Integer SHARE_MY_RECORDING = Constants.SHARE_MY_RECORDING_NO;
    protected long RECORDED_DATE_TS = 0;
    protected Integer BOOKMARK = Constants.BOOKMARKED_NO;
    protected Integer VOCA_LEVEL = Constants.WORDLEVEL_INDIC_MAX;
    protected String MEANING_ENG_DETAILED = "";
    protected String VOCA_WITH_MEANING = "";
    protected String PERSON_AB = "";
    protected Integer BELONG_TO_USER_VOCABOOK = Constants.IS_NO;

    public void setFILE_VERSION(Integer value) {
        this.FILE_VERSION = value;
        this.VOICE_FILE_VERSION = value;
    }

    // Builder class
    public static class Builder {
        private DTO_VOCA_DETAIL instance;

        public Builder() {
            instance = new DTO_VOCA_DETAIL();
        }

        public Builder ID(Integer ID) {
            instance.ID = ID;
            return this;
        }

        public Builder VOCA_DISPLAY_RUBY_TEXT(String VOCA_DISPLAY_RUBY_TEXT) {
            instance.VOCA_DISPLAY_RUBY_TEXT = VOCA_DISPLAY_RUBY_TEXT;
            return this;
        }

        public Builder VOCA_DISPLAY_RUBY_VOCA_LIST(List<DTO_VOCA_DETAIL_RUBY_TEXT> VOCA_DISPLAY_RUBY_VOCA_LIST) {
            instance.VOCA_DISPLAY_RUBY_VOCA_LIST = VOCA_DISPLAY_RUBY_VOCA_LIST;
            return this;
        }

        public Builder DISP_ORDER(Integer DISP_ORDER) {
            instance.DISP_ORDER = DISP_ORDER;
            return this;
        }

        public Builder VOCAORI_ID(Integer VOCAORI_ID) {
            instance.VOCAORI_ID = VOCAORI_ID;
            return this;
        }

        public Builder VOCA_KNOW(Integer VOCA_KNOW) {
            instance.VOCA_KNOW = VOCA_KNOW;
            return this;
        }

        public Builder VOCA_KNOWPRONOUNCE(Integer VOCA_KNOWPRONOUNCE) {
            instance.VOCA_KNOWPRONOUNCE = VOCA_KNOWPRONOUNCE;
            return this;
        }

        public Builder STUDY_COUNT(Integer STUDY_COUNT) {
            instance.STUDY_COUNT = STUDY_COUNT;
            return this;
        }

        public Builder HAS_VOICE_FILE(Integer HAS_VOICE_FILE) {
            instance.HAS_VOICE_FILE = HAS_VOICE_FILE;
            return this;
        }

        public Builder VOICE_FILE_VERSION(Integer VOICE_FILE_VERSION) {
            instance.VOICE_FILE_VERSION = VOICE_FILE_VERSION;
            return this;
        }

        public Builder FILE_VERSION(Integer FILE_VERSION) {
            instance.FILE_VERSION = FILE_VERSION;
            return this;
        }

        public Builder EVALUATE_VOCA_GRADE(String EVALUATE_VOCA_GRADE) {
            instance.EVALUATE_VOCA_GRADE = EVALUATE_VOCA_GRADE;
            return this;
        }

        public Builder EVALUATE_VOCA_GRADE_TUTORS(String EVALUATE_VOCA_GRADE_TUTORS) {
            instance.EVALUATE_VOCA_GRADE_TUTORS = EVALUATE_VOCA_GRADE_TUTORS;
            return this;
        }

        public Builder RE_RECORD_STATUS(Integer RE_RECORD_STATUS) {
            instance.RE_RECORD_STATUS = RE_RECORD_STATUS;
            return this;
        }

        public Builder SHARE_MY_RECORDING(Integer SHARE_MY_RECORDING) {
            instance.SHARE_MY_RECORDING = SHARE_MY_RECORDING;
            return this;
        }

        public Builder RECORDED_DATE_TS(long RECORDED_DATE_TS) {
            instance.RECORDED_DATE_TS = RECORDED_DATE_TS;
            return this;
        }

        public Builder BOOKMARK(Integer BOOKMARK) {
            instance.BOOKMARK = BOOKMARK;
            return this;
        }

        public Builder VOCA_LEVEL(Integer VOCA_LEVEL) {
            instance.VOCA_LEVEL = VOCA_LEVEL;
            return this;
        }

        public Builder MEANING_ENG_DETAILED(String MEANING_ENG_DETAILED) {
            instance.MEANING_ENG_DETAILED = MEANING_ENG_DETAILED;
            return this;
        }

        public Builder VOCA_WITH_MEANING(String VOCA_WITH_MEANING) {
            instance.VOCA_WITH_MEANING = VOCA_WITH_MEANING;
            return this;
        }

        public Builder PERSON_AB(String PERSON_AB) {
            instance.PERSON_AB = PERSON_AB;
            return this;
        }

        public Builder BELONG_TO_USER_VOCABOOK(Integer BELONG_TO_USER_VOCABOOK) {
            instance.BELONG_TO_USER_VOCABOOK = BELONG_TO_USER_VOCABOOK;
            return this;
        }

        public DTO_VOCA_DETAIL build() {
            return instance;
        }
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getVOCA_DISPLAY_RUBY_TEXT() {
        return VOCA_DISPLAY_RUBY_TEXT;
    }

    public void setVOCA_DISPLAY_RUBY_TEXT(String VOCA_DISPLAY_RUBY_TEXT) {
        this.VOCA_DISPLAY_RUBY_TEXT = VOCA_DISPLAY_RUBY_TEXT;
    }

    public List<DTO_VOCA_DETAIL_RUBY_TEXT> getVOCA_DISPLAY_RUBY_VOCA_LIST() {
        return VOCA_DISPLAY_RUBY_VOCA_LIST;
    }

    public void setVOCA_DISPLAY_RUBY_VOCA_LIST(List<DTO_VOCA_DETAIL_RUBY_TEXT> VOCA_DISPLAY_RUBY_VOCA_LIST) {
        this.VOCA_DISPLAY_RUBY_VOCA_LIST = VOCA_DISPLAY_RUBY_VOCA_LIST;
    }

    public Integer getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(Integer DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }

    public Integer getVOCAORI_ID() {
        return VOCAORI_ID;
    }

    public void setVOCAORI_ID(Integer VOCAORI_ID) {
        this.VOCAORI_ID = VOCAORI_ID;
    }

    public Integer getVOCA_KNOW() {
        return VOCA_KNOW;
    }

    public void setVOCA_KNOW(Integer VOCA_KNOW) {
        this.VOCA_KNOW = VOCA_KNOW;
    }

    public Integer getVOCA_KNOWPRONOUNCE() {
        return VOCA_KNOWPRONOUNCE;
    }

    public void setVOCA_KNOWPRONOUNCE(Integer VOCA_KNOWPRONOUNCE) {
        this.VOCA_KNOWPRONOUNCE = VOCA_KNOWPRONOUNCE;
    }

    public Integer getSTUDY_COUNT() {
        return STUDY_COUNT;
    }

    public void setSTUDY_COUNT(Integer STUDY_COUNT) {
        this.STUDY_COUNT = STUDY_COUNT;
    }

    public Integer getHAS_VOICE_FILE() {
        return HAS_VOICE_FILE;
    }

    public void setHAS_VOICE_FILE(Integer HAS_VOICE_FILE) {
        this.HAS_VOICE_FILE = HAS_VOICE_FILE;
    }

    public Integer getVOICE_FILE_VERSION() {
        return VOICE_FILE_VERSION;
    }

    public void setVOICE_FILE_VERSION(Integer VOICE_FILE_VERSION) {
        this.VOICE_FILE_VERSION = VOICE_FILE_VERSION;
    }

    public Integer getFILE_VERSION() {
        return FILE_VERSION;
    }

    public String getEVALUATE_VOCA_GRADE() {
        return EVALUATE_VOCA_GRADE;
    }

    public void setEVALUATE_VOCA_GRADE(String EVALUATE_VOCA_GRADE) {
        this.EVALUATE_VOCA_GRADE = EVALUATE_VOCA_GRADE;
    }

    public String getEVALUATE_VOCA_GRADE_TUTORS() {
        return EVALUATE_VOCA_GRADE_TUTORS;
    }

    public void setEVALUATE_VOCA_GRADE_TUTORS(String EVALUATE_VOCA_GRADE_TUTORS) {
        this.EVALUATE_VOCA_GRADE_TUTORS = EVALUATE_VOCA_GRADE_TUTORS;
    }

    public Integer getRE_RECORD_STATUS() {
        return RE_RECORD_STATUS;
    }

    public void setRE_RECORD_STATUS(Integer RE_RECORD_STATUS) {
        this.RE_RECORD_STATUS = RE_RECORD_STATUS;
    }

    public Integer getSHARE_MY_RECORDING() {
        return SHARE_MY_RECORDING;
    }

    public void setSHARE_MY_RECORDING(Integer SHARE_MY_RECORDING) {
        this.SHARE_MY_RECORDING = SHARE_MY_RECORDING;
    }

    public long getRECORDED_DATE_TS() {
        return RECORDED_DATE_TS;
    }

    public void setRECORDED_DATE_TS(long RECORDED_DATE_TS) {
        this.RECORDED_DATE_TS = RECORDED_DATE_TS;
    }

    public Integer getBOOKMARK() {
        return BOOKMARK;
    }

    public void setBOOKMARK(Integer BOOKMARK) {
        this.BOOKMARK = BOOKMARK;
    }

    public Integer getVOCA_LEVEL() {
        return VOCA_LEVEL;
    }

    public void setVOCA_LEVEL(Integer VOCA_LEVEL) {
        this.VOCA_LEVEL = VOCA_LEVEL;
    }

    public String getMEANING_ENG_DETAILED() {
        return MEANING_ENG_DETAILED;
    }

    public void setMEANING_ENG_DETAILED(String MEANING_ENG_DETAILED) {
        this.MEANING_ENG_DETAILED = MEANING_ENG_DETAILED;
    }

    public String getVOCA_WITH_MEANING() {
        return VOCA_WITH_MEANING;
    }

    public void setVOCA_WITH_MEANING(String VOCA_WITH_MEANING) {
        this.VOCA_WITH_MEANING = VOCA_WITH_MEANING;
    }

    public String getPERSON_AB() {
        return PERSON_AB;
    }

    public void setPERSON_AB(String PERSON_AB) {
        this.PERSON_AB = PERSON_AB;
    }

    public Integer getBELONG_TO_USER_VOCABOOK() {
        return BELONG_TO_USER_VOCABOOK;
    }

    public void setBELONG_TO_USER_VOCABOOK(Integer BELONG_TO_USER_VOCABOOK) {
        this.BELONG_TO_USER_VOCABOOK = BELONG_TO_USER_VOCABOOK;
    }
}
