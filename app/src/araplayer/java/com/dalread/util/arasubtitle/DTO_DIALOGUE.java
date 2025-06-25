package com.dalread.util.arasubtitle;

public class DTO_DIALOGUE {
    private Integer LANG_STUDY_CODE = Constants.LANGCODE_EN;
    private Integer LANG_MEANING_CODE = Constants.LANGCODE_KO;
    private Integer ID = 0;
    private String VOCA = "";
    private String VOCA_ORIGINAL = "";
    private String VOCA_REMOVE_PUNCT = "";
    private String MEANING = "";
    private Integer CORRECT_FORMAT_VOCA_TO_INSERT_DB = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_NO;
    private Integer START_TIME = 0;
    private Integer END_TIME = 0;
    private Integer START_TIME_ORIGINAL = 0;
    private Integer END_TIME_ORIGINAL = 0;

    private Integer BOOKMARK = Constants.IS_NO;
    private Integer USED = Constants.IS_YES;
    private Integer REPEAT = Constants.IS_YES;


    private String MEMO = "";
    private String RECORDING_PATH = "";

    protected Integer VOCA_TYPE = Constants.VOCA_TYPE_SUBTITLE_UNTUNED;
    protected Integer VOCA_ID = -1;
    protected Integer VOCA_ID_TO_SEND_SERVER = -1;
    protected Integer BASE_VOCA_TYPE = Constants.VOCA_TYPE_NONE;
    protected Integer BASE_VOCA_ID = -1;
    //	protected Integer BASE_VOCA_ID_TO_SEND_SERVER = -1;
    protected Integer VOCA_KNOW = Constants.VOCA_KNOW.NOTRATED;
    protected Integer VOCA_KNOWPRONOUNCE = Constants.VOCA_KNOW.KNOWN; //자막의 발음은 다 안다고 일단 한다.
    protected Integer INSERT_IN_SQLITE = Constants.IS_YES;

    private DTO_DIALOGUE() {
        // private constructor to prevent direct instantiation
    }

    // Builder pattern starts here
    public static class Builder {
        private DTO_DIALOGUE dialogue;

        public Builder() {
            dialogue = new DTO_DIALOGUE();
        }

        public Builder LANG_STUDY_CODE(Integer LANG_STUDY_CODE) {
            dialogue.LANG_STUDY_CODE = LANG_STUDY_CODE;
            return this;
        }

        public Builder LANG_MEANING_CODE(Integer LANG_MEANING_CODE) {
            dialogue.LANG_MEANING_CODE = LANG_MEANING_CODE;
            return this;
        }

        public Builder ID(Integer ID) {
            dialogue.ID = ID;
            return this;
        }

        public Builder VOCA(String VOCA) {
            dialogue.VOCA = VOCA;
            return this;
        }

        public Builder VOCA_ORIGINAL(String VOCA_ORIGINAL) {
            dialogue.VOCA_ORIGINAL = VOCA_ORIGINAL;
            return this;
        }

        public Builder VOCA_REMOVE_PUNCT(String VOCA_REMOVE_PUNCT) {
            dialogue.VOCA_REMOVE_PUNCT = VOCA_REMOVE_PUNCT;
            return this;
        }

        public Builder MEANING(String MEANING) {
            dialogue.MEANING = MEANING;
            return this;
        }

        public Builder CORRECT_FORMAT_VOCA_TO_INSERT_DB(Integer CORRECT_FORMAT_VOCA_TO_INSERT_DB) {
            dialogue.CORRECT_FORMAT_VOCA_TO_INSERT_DB = CORRECT_FORMAT_VOCA_TO_INSERT_DB;
            return this;
        }

        public Builder START_TIME(Integer START_TIME) {
            dialogue.START_TIME = START_TIME;
            return this;
        }

        public Builder END_TIME(Integer END_TIME) {
            dialogue.END_TIME = END_TIME;
            return this;
        }

        public Builder START_TIME_ORIGINAL(Integer START_TIME_ORIGINAL) {
            dialogue.START_TIME_ORIGINAL = START_TIME_ORIGINAL;
            return this;
        }

        public Builder END_TIME_ORIGINAL(Integer END_TIME_ORIGINAL) {
            dialogue.END_TIME_ORIGINAL = END_TIME_ORIGINAL;
            return this;
        }

        public DTO_DIALOGUE build() {
            return dialogue;
        }
    }

    public Integer getLANG_STUDY_CODE() {
        return LANG_STUDY_CODE;
    }

    public void setLANG_STUDY_CODE(Integer LANG_STUDY_CODE) {
        this.LANG_STUDY_CODE = LANG_STUDY_CODE;
    }

    public Integer getLANG_MEANING_CODE() {
        return LANG_MEANING_CODE;
    }

    public void setLANG_MEANING_CODE(Integer LANG_MEANING_CODE) {
        this.LANG_MEANING_CODE = LANG_MEANING_CODE;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getVOCA() {
        return VOCA;
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public String getVOCA_ORIGINAL() {
        return VOCA_ORIGINAL;
    }

    public void setVOCA_ORIGINAL(String VOCA_ORIGINAL) {
        this.VOCA_ORIGINAL = VOCA_ORIGINAL;
    }

    public String getVOCA_REMOVE_PUNCT() {
        return VOCA_REMOVE_PUNCT;
    }

    public void setVOCA_REMOVE_PUNCT(String VOCA_REMOVE_PUNCT) {
        this.VOCA_REMOVE_PUNCT = VOCA_REMOVE_PUNCT;
    }

    public String getMEANING() {
        return MEANING;
    }

    public void setMEANING(String MEANING) {
        this.MEANING = MEANING;
    }

    public Integer getCORRECT_FORMAT_VOCA_TO_INSERT_DB() {
        return CORRECT_FORMAT_VOCA_TO_INSERT_DB;
    }

    public void setCORRECT_FORMAT_VOCA_TO_INSERT_DB(Integer CORRECT_FORMAT_VOCA_TO_INSERT_DB) {
        this.CORRECT_FORMAT_VOCA_TO_INSERT_DB = CORRECT_FORMAT_VOCA_TO_INSERT_DB;
    }

    public Integer getSTART_TIME() {
        return START_TIME;
    }

    public void setSTART_TIME(Integer START_TIME) {
        this.START_TIME = START_TIME;
    }

    public Integer getEND_TIME() {
        return END_TIME;
    }

    public void setEND_TIME(Integer END_TIME) {
        this.END_TIME = END_TIME;
    }

    public Integer getSTART_TIME_ORIGINAL() {
        return START_TIME_ORIGINAL;
    }

    public void setSTART_TIME_ORIGINAL(Integer START_TIME_ORIGINAL) {
        this.START_TIME_ORIGINAL = START_TIME_ORIGINAL;
    }

    public Integer getEND_TIME_ORIGINAL() {
        return END_TIME_ORIGINAL;
    }

    public void setEND_TIME_ORIGINAL(Integer END_TIME_ORIGINAL) {
        this.END_TIME_ORIGINAL = END_TIME_ORIGINAL;
    }

    public Integer getBOOKMARK() {
        return BOOKMARK;
    }

    public void setBOOKMARK(Integer BOOKMARK) {
        this.BOOKMARK = BOOKMARK;
    }

    public Integer getUSED() {
        return USED;
    }

    public void setUSED(Integer USED) {
        this.USED = USED;
    }

    public Integer getREPEAT() {
        return REPEAT;
    }

    public void setREPEAT(Integer REPEAT) {
        this.REPEAT = REPEAT;
    }

    public String getMEMO() {
        return MEMO;
    }

    public void setMEMO(String MEMO) {
        this.MEMO = MEMO;
    }

    public String getRECORDING_PATH() {
        return RECORDING_PATH;
    }

    public void setRECORDING_PATH(String RECORDING_PATH) {
        this.RECORDING_PATH = RECORDING_PATH;
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

    public Integer getVOCA_ID_TO_SEND_SERVER() {
        return VOCA_ID_TO_SEND_SERVER;
    }

    public void setVOCA_ID_TO_SEND_SERVER(Integer VOCA_ID_TO_SEND_SERVER) {
        this.VOCA_ID_TO_SEND_SERVER = VOCA_ID_TO_SEND_SERVER;
    }

    public Integer getBASE_VOCA_TYPE() {
        return BASE_VOCA_TYPE;
    }

    public void setBASE_VOCA_TYPE(Integer BASE_VOCA_TYPE) {
        this.BASE_VOCA_TYPE = BASE_VOCA_TYPE;
    }

    public Integer getBASE_VOCA_ID() {
        return BASE_VOCA_ID;
    }

    public void setBASE_VOCA_ID(Integer BASE_VOCA_ID) {
        this.BASE_VOCA_ID = BASE_VOCA_ID;
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

    public Integer getINSERT_IN_SQLITE() {
        return INSERT_IN_SQLITE;
    }

    public void setINSERT_IN_SQLITE(Integer INSERT_IN_SQLITE) {
        this.INSERT_IN_SQLITE = INSERT_IN_SQLITE;
    }
}
