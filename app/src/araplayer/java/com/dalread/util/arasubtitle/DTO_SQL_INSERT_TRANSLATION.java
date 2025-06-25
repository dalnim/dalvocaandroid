package com.dalread.util.arasubtitle;

public class DTO_SQL_INSERT_TRANSLATION {
    protected Integer UID = Constants.UID_DEFAULT;
    protected String VOCA = "";
    protected String VOCA_WITHOUT_PUNCTUATION = "";
    protected String MEANING = "";

    // Builder class for DTO_SQL_INSERT_TRANSLATION
    public static class Builder {
        private DTO_SQL_INSERT_TRANSLATION instance;

        public Builder() {
            instance = new DTO_SQL_INSERT_TRANSLATION();
        }

        public Builder UID(Integer UID) {
            instance.UID = UID;
            return this;
        }

        public Builder VOCA(String VOCA) {
            instance.VOCA = VOCA;
            return this;
        }

        public Builder VOCA_WITHOUT_PUNCTUATION(String VOCA_WITHOUT_PUNCTUATION) {
            instance.VOCA_WITHOUT_PUNCTUATION = VOCA_WITHOUT_PUNCTUATION;
            return this;
        }

        public Builder MEANING(String MEANING) {
            instance.MEANING = MEANING;
            return this;
        }

        public DTO_SQL_INSERT_TRANSLATION build() {
            return instance;
        }
    }

    public Integer getUID() {
        return UID;
    }

    public void setUID(Integer UID) {
        this.UID = UID;
    }

    public String getVOCA() {
        return VOCA;
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public String getVOCA_WITHOUT_PUNCTUATION() {
        return VOCA_WITHOUT_PUNCTUATION;
    }

    public void setVOCA_WITHOUT_PUNCTUATION(String VOCA_WITHOUT_PUNCTUATION) {
        this.VOCA_WITHOUT_PUNCTUATION = VOCA_WITHOUT_PUNCTUATION;
    }

    public String getMEANING() {
        return MEANING;
    }

    public void setMEANING(String MEANING) {
        this.MEANING = MEANING;
    }
}

