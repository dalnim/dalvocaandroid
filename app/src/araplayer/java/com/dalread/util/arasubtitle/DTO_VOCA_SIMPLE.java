package com.dalread.util.arasubtitle;

public class DTO_VOCA_SIMPLE extends DTO_INDEX_SUPER_BUILDER {
    protected Integer VOCA_TYPE = Constants.VOCA_TYPE_WORD;
    protected Integer VOCA_ID = 0;
    protected String VOCA = "";
    protected String VOCAORI = "";
    protected String VOCA_DISPLAY = "";
    protected String VOCA_TTS = "";
    protected String MEANING = "";
    protected String MEANING_DETAILED = "";
    protected String MEANING_FOR_HIDE_ALL = "";
    protected String MEANING_TTS = "";
    protected String MEANING_ENG = "";
    protected String PRONOUNCE = "";

    public void setMEANING(String value) {
        this.MEANING = (value == null) ? "" : value.replaceAll("\"", "\\\"");
    }

    public void setMEANING_TTS(String value) {
        this.MEANING_TTS = (value == null) ? "" : value.replaceAll("\"", "\\\"");
    }

    // Builder class
    public static class Builder {
        private DTO_VOCA_SIMPLE instance;

        public Builder() {
            instance = new DTO_VOCA_SIMPLE();
        }

        public Builder VOCA_TYPE(Integer VOCA_TYPE) {
            instance.VOCA_TYPE = VOCA_TYPE;
            return this;
        }

        public Builder VOCA_ID(Integer VOCA_ID) {
            instance.VOCA_ID = VOCA_ID;
            return this;
        }

        public Builder VOCA(String VOCA) {
            instance.VOCA = VOCA;
            return this;
        }

        public Builder VOCAORI(String VOCAORI) {
            instance.VOCAORI = VOCAORI;
            return this;
        }

        public Builder VOCA_DISPLAY(String VOCA_DISPLAY) {
            instance.VOCA_DISPLAY = VOCA_DISPLAY;
            return this;
        }

        public Builder VOCA_TTS(String VOCA_TTS) {
            instance.VOCA_TTS = VOCA_TTS;
            return this;
        }

        public Builder MEANING(String MEANING) {
            instance.setMEANING(MEANING);
            return this;
        }

        public Builder MEANING_DETAILED(String MEANING_DETAILED) {
            instance.MEANING_DETAILED = MEANING_DETAILED;
            return this;
        }

        public Builder MEANING_FOR_HIDE_ALL(String MEANING_FOR_HIDE_ALL) {
            instance.MEANING_FOR_HIDE_ALL = MEANING_FOR_HIDE_ALL;
            return this;
        }

        public Builder MEANING_TTS(String MEANING_TTS) {
            instance.setMEANING_TTS(MEANING_TTS);
            return this;
        }

        public Builder MEANING_ENG(String MEANING_ENG) {
            instance.MEANING_ENG = MEANING_ENG;
            return this;
        }

        public Builder PRONOUNCE(String PRONOUNCE) {
            instance.PRONOUNCE = PRONOUNCE;
            return this;
        }

        public DTO_VOCA_SIMPLE build() {
            return instance;
        }
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

    public String getVOCA() {
        return VOCA;
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public String getVOCAORI() {
        return VOCAORI;
    }

    public void setVOCAORI(String VOCAORI) {
        this.VOCAORI = VOCAORI;
    }

    public String getVOCA_DISPLAY() {
        return VOCA_DISPLAY;
    }

    public void setVOCA_DISPLAY(String VOCA_DISPLAY) {
        this.VOCA_DISPLAY = VOCA_DISPLAY;
    }

    public String getVOCA_TTS() {
        return VOCA_TTS;
    }

    public void setVOCA_TTS(String VOCA_TTS) {
        this.VOCA_TTS = VOCA_TTS;
    }

    public String getMEANING() {
        return MEANING;
    }

    public String getMEANING_DETAILED() {
        return MEANING_DETAILED;
    }

    public void setMEANING_DETAILED(String MEANING_DETAILED) {
        this.MEANING_DETAILED = MEANING_DETAILED;
    }

    public String getMEANING_FOR_HIDE_ALL() {
        return MEANING_FOR_HIDE_ALL;
    }

    public void setMEANING_FOR_HIDE_ALL(String MEANING_FOR_HIDE_ALL) {
        this.MEANING_FOR_HIDE_ALL = MEANING_FOR_HIDE_ALL;
    }

    public String getMEANING_TTS() {
        return MEANING_TTS;
    }

    public String getMEANING_ENG() {
        return MEANING_ENG;
    }

    public void setMEANING_ENG(String MEANING_ENG) {
        this.MEANING_ENG = MEANING_ENG;
    }

    public String getPRONOUNCE() {
        return PRONOUNCE;
    }

    public void setPRONOUNCE(String PRONOUNCE) {
        this.PRONOUNCE = PRONOUNCE;
    }
}

