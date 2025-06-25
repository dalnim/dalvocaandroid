package com.dalread.util.arasubtitle;

public class DTO_VOCA_DETAIL_RUBY_TEXT extends DTO_VOCA_DETAIL {
    protected String JMDICT_MEANING = "";
    protected String JMDICT_MEANING_ENG = "";
    protected String POS_VOCA = "";
    protected String POS_VOCA_BASEFORM = "";
    protected String PRONOUNCE_BASEFORM = "";
    protected String PRONOUNCE_From_Analyzer = "";
    protected String PRONOUNCE_FOR_HURIGANA = "";
    protected String PRONOUNCE_VOCAORI = "";
    protected Integer FREQUENCY = 1;
    protected String VOCALIST_KEY = "";
    protected String VOCA_WithConjugation = "";
    protected Integer SERIAL_ID_FOR_VOCAS = 0;
    protected String EXISTIN_SERVERDIC = Constants.DATAINDIC_NO;
    protected Integer IS_RUBY_ON_CONJUGATION = Constants.IS_NO;
    protected String TITLE_OF_TOOLTIP = "";
    protected String MEANING_WITH_PRONOUNCE_FOR_HANJA = "";
    protected String HANJA_MEANING_PRONOUNCE_FOR_KOREAN = "";
    protected String HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN = "";

    public void increaseFrequency() {
        FREQUENCY = FREQUENCY + 1;
    }

    // Builder class
    public static class Builder {
        private DTO_VOCA_DETAIL_RUBY_TEXT instance;

        public Builder() {
            instance = new DTO_VOCA_DETAIL_RUBY_TEXT();
        }

        public Builder VOCA(String VOCA) {
            instance.VOCA = VOCA;
            return this;
        }

        public Builder VOCA_ID(Integer VOCA_ID) {
            instance.VOCA_ID = VOCA_ID;
            return this;
        }
        public Builder BOOKMARK(Integer BOOKMARK) {
            instance.BOOKMARK = BOOKMARK;
            return this;
        }
        public Builder VOCAORI(String VOCAORI) {
            instance.VOCAORI = VOCAORI;
            return this;
        }
        public Builder VOCA_WITH_MEANING(String VOCA_WITH_MEANING) {
            instance.VOCA_WITH_MEANING = VOCA_WITH_MEANING;
            return this;
        }
        public Builder VOCA_DISPLAY(String VOCA_DISPLAY) {
            instance.VOCA_DISPLAY = VOCA_DISPLAY;
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
        public Builder VOCAORI_ID(Integer VOCAORI_ID) {
            instance.VOCAORI_ID = VOCAORI_ID;
            return this;
        }
        public Builder MEANING_FOR_HIDE_ALL(String MEANING_FOR_HIDE_ALL) {
            instance.MEANING_FOR_HIDE_ALL = MEANING_FOR_HIDE_ALL;
            return this;
        }
        public Builder VOCA_LEVEL(Integer VOCA_LEVEL) {
            instance.VOCA_LEVEL = VOCA_LEVEL;
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

        public Builder JMDICT_MEANING(String JMDICT_MEANING) {
            instance.JMDICT_MEANING = JMDICT_MEANING;
            return this;
        }

        public Builder JMDICT_MEANING_ENG(String JMDICT_MEANING_ENG) {
            instance.JMDICT_MEANING_ENG = JMDICT_MEANING_ENG;
            return this;
        }

        public Builder POS_VOCA(String POS_VOCA) {
            instance.POS_VOCA = POS_VOCA;
            return this;
        }

        public Builder POS_VOCA_BASEFORM(String POS_VOCA_BASEFORM) {
            instance.POS_VOCA_BASEFORM = POS_VOCA_BASEFORM;
            return this;
        }

        public Builder PRONOUNCE_BASEFORM(String PRONOUNCE_BASEFORM) {
            instance.PRONOUNCE_BASEFORM = PRONOUNCE_BASEFORM;
            return this;
        }

        public Builder PRONOUNCE_From_Analyzer(String PRONOUNCE_From_Analyzer) {
            instance.PRONOUNCE_From_Analyzer = PRONOUNCE_From_Analyzer;
            return this;
        }

        public Builder PRONOUNCE_FOR_HURIGANA(String PRONOUNCE_FOR_HURIGANA) {
            instance.PRONOUNCE_FOR_HURIGANA = PRONOUNCE_FOR_HURIGANA;
            return this;
        }

        public Builder PRONOUNCE_VOCAORI(String PRONOUNCE_VOCAORI) {
            instance.PRONOUNCE_VOCAORI = PRONOUNCE_VOCAORI;
            return this;
        }

        public Builder FREQUENCY(Integer FREQUENCY) {
            instance.FREQUENCY = FREQUENCY;
            return this;
        }

        public Builder VOCALIST_KEY(String VOCALIST_KEY) {
            instance.VOCALIST_KEY = VOCALIST_KEY;
            return this;
        }

        public Builder VOCA_WithConjugation(String VOCA_WithConjugation) {
            instance.VOCA_WithConjugation = VOCA_WithConjugation;
            return this;
        }

        public Builder SERIAL_ID_FOR_VOCAS(Integer SERIAL_ID_FOR_VOCAS) {
            instance.SERIAL_ID_FOR_VOCAS = SERIAL_ID_FOR_VOCAS;
            return this;
        }

        public Builder EXISTIN_SERVERDIC(String EXISTIN_SERVERDIC) {
            instance.EXISTIN_SERVERDIC = EXISTIN_SERVERDIC;
            return this;
        }

        public Builder IS_RUBY_ON_CONJUGATION(Integer IS_RUBY_ON_CONJUGATION) {
            instance.IS_RUBY_ON_CONJUGATION = IS_RUBY_ON_CONJUGATION;
            return this;
        }

        public Builder TITLE_OF_TOOLTIP(String TITLE_OF_TOOLTIP) {
            instance.TITLE_OF_TOOLTIP = TITLE_OF_TOOLTIP;
            return this;
        }

        public Builder MEANING_WITH_PRONOUNCE_FOR_HANJA(String MEANING_WITH_PRONOUNCE_FOR_HANJA) {
            instance.MEANING_WITH_PRONOUNCE_FOR_HANJA = MEANING_WITH_PRONOUNCE_FOR_HANJA;
            return this;
        }

        public Builder HANJA_MEANING_PRONOUNCE_FOR_KOREAN(String HANJA_MEANING_PRONOUNCE_FOR_KOREAN) {
            instance.HANJA_MEANING_PRONOUNCE_FOR_KOREAN = HANJA_MEANING_PRONOUNCE_FOR_KOREAN;
            return this;
        }

        public Builder HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN(String HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN) {
            instance.HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN = HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN;
            return this;
        }

        public DTO_VOCA_DETAIL_RUBY_TEXT build() {
            return instance;
        }
    }

    public String getJMDICT_MEANING() {
        return JMDICT_MEANING;
    }

    public void setJMDICT_MEANING(String JMDICT_MEANING) {
        this.JMDICT_MEANING = JMDICT_MEANING;
    }

    public String getJMDICT_MEANING_ENG() {
        return JMDICT_MEANING_ENG;
    }

    public void setJMDICT_MEANING_ENG(String JMDICT_MEANING_ENG) {
        this.JMDICT_MEANING_ENG = JMDICT_MEANING_ENG;
    }

    public String getPOS_VOCA() {
        return POS_VOCA;
    }

    public void setPOS_VOCA(String POS_VOCA) {
        this.POS_VOCA = POS_VOCA;
    }

    public String getPOS_VOCA_BASEFORM() {
        return POS_VOCA_BASEFORM;
    }

    public void setPOS_VOCA_BASEFORM(String POS_VOCA_BASEFORM) {
        this.POS_VOCA_BASEFORM = POS_VOCA_BASEFORM;
    }

    public String getPRONOUNCE_BASEFORM() {
        return PRONOUNCE_BASEFORM;
    }

    public void setPRONOUNCE_BASEFORM(String PRONOUNCE_BASEFORM) {
        this.PRONOUNCE_BASEFORM = PRONOUNCE_BASEFORM;
    }

    public String getPRONOUNCE_From_Analyzer() {
        return PRONOUNCE_From_Analyzer;
    }

    public void setPRONOUNCE_From_Analyzer(String PRONOUNCE_From_Analyzer) {
        this.PRONOUNCE_From_Analyzer = PRONOUNCE_From_Analyzer;
    }

    public String getPRONOUNCE_FOR_HURIGANA() {
        return PRONOUNCE_FOR_HURIGANA;
    }

    public void setPRONOUNCE_FOR_HURIGANA(String PRONOUNCE_FOR_HURIGANA) {
        this.PRONOUNCE_FOR_HURIGANA = PRONOUNCE_FOR_HURIGANA;
    }

    public String getPRONOUNCE_VOCAORI() {
        return PRONOUNCE_VOCAORI;
    }

    public void setPRONOUNCE_VOCAORI(String PRONOUNCE_VOCAORI) {
        this.PRONOUNCE_VOCAORI = PRONOUNCE_VOCAORI;
    }

    public Integer getFREQUENCY() {
        return FREQUENCY;
    }

    public void setFREQUENCY(Integer FREQUENCY) {
        this.FREQUENCY = FREQUENCY;
    }

    public String getVOCALIST_KEY() {
        return VOCALIST_KEY;
    }

    public void setVOCALIST_KEY(String VOCALIST_KEY) {
        this.VOCALIST_KEY = VOCALIST_KEY;
    }

    public String getVOCA_WithConjugation() {
        return VOCA_WithConjugation;
    }

    public void setVOCA_WithConjugation(String VOCA_WithConjugation) {
        this.VOCA_WithConjugation = VOCA_WithConjugation;
    }

    public Integer getSERIAL_ID_FOR_VOCAS() {
        return SERIAL_ID_FOR_VOCAS;
    }

    public void setSERIAL_ID_FOR_VOCAS(Integer SERIAL_ID_FOR_VOCAS) {
        this.SERIAL_ID_FOR_VOCAS = SERIAL_ID_FOR_VOCAS;
    }

    public String getEXISTIN_SERVERDIC() {
        return EXISTIN_SERVERDIC;
    }

    public void setEXISTIN_SERVERDIC(String EXISTIN_SERVERDIC) {
        this.EXISTIN_SERVERDIC = EXISTIN_SERVERDIC;
    }

    public Integer getIS_RUBY_ON_CONJUGATION() {
        return IS_RUBY_ON_CONJUGATION;
    }

    public void setIS_RUBY_ON_CONJUGATION(Integer IS_RUBY_ON_CONJUGATION) {
        this.IS_RUBY_ON_CONJUGATION = IS_RUBY_ON_CONJUGATION;
    }

    public String getTITLE_OF_TOOLTIP() {
        return TITLE_OF_TOOLTIP;
    }

    public void setTITLE_OF_TOOLTIP(String TITLE_OF_TOOLTIP) {
        this.TITLE_OF_TOOLTIP = TITLE_OF_TOOLTIP;
    }

    public String getMEANING_WITH_PRONOUNCE_FOR_HANJA() {
        return MEANING_WITH_PRONOUNCE_FOR_HANJA;
    }

    public void setMEANING_WITH_PRONOUNCE_FOR_HANJA(String MEANING_WITH_PRONOUNCE_FOR_HANJA) {
        this.MEANING_WITH_PRONOUNCE_FOR_HANJA = MEANING_WITH_PRONOUNCE_FOR_HANJA;
    }

    public String getHANJA_MEANING_PRONOUNCE_FOR_KOREAN() {
        return HANJA_MEANING_PRONOUNCE_FOR_KOREAN;
    }

    public void setHANJA_MEANING_PRONOUNCE_FOR_KOREAN(String HANJA_MEANING_PRONOUNCE_FOR_KOREAN) {
        this.HANJA_MEANING_PRONOUNCE_FOR_KOREAN = HANJA_MEANING_PRONOUNCE_FOR_KOREAN;
    }

    public String getHANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN() {
        return HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN;
    }

    public void setHANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN(String HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN) {
        this.HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN = HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN;
    }
}
