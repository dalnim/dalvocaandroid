package com.dalread.util.arasubtitle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTO_OUTPUT_RUBY_TEXT {
    protected String INPUT_TEXT = "";
    protected String VOCA_DISPLAY_RUBY_TEXT = "";
    protected Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> VOCA_LIST = new HashMap<>();
    protected List<List<DTO_VOCA_DETAIL_RUBY_TEXT>> VOCA_DISPLAY_RUBY_VOCA_LIST = new ArrayList<>();
    protected List<String> LIST_RUBY_TEXT = new ArrayList<>();
    protected List<DTO_INPUT_LINE_NLP_PARSED> LIST_NLP_PARSED = new ArrayList<>();

    public String getINPUT_TEXT() {
        return INPUT_TEXT;
    }

    public void setINPUT_TEXT(String INPUT_TEXT) {
        this.INPUT_TEXT = INPUT_TEXT;
    }

    public String getVOCA_DISPLAY_RUBY_TEXT() {
        return VOCA_DISPLAY_RUBY_TEXT;
    }

    public void setVOCA_DISPLAY_RUBY_TEXT(String VOCA_DISPLAY_RUBY_TEXT) {
        this.VOCA_DISPLAY_RUBY_TEXT = VOCA_DISPLAY_RUBY_TEXT;
    }

    public Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> getVOCA_LIST() {
        return VOCA_LIST;
    }

    public void setVOCA_LIST(Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> VOCA_LIST) {
        this.VOCA_LIST = VOCA_LIST;
    }

    public List<List<DTO_VOCA_DETAIL_RUBY_TEXT>> getVOCA_DISPLAY_RUBY_VOCA_LIST() {
        return VOCA_DISPLAY_RUBY_VOCA_LIST;
    }

    public void setVOCA_DISPLAY_RUBY_VOCA_LIST(List<List<DTO_VOCA_DETAIL_RUBY_TEXT>> VOCA_DISPLAY_RUBY_VOCA_LIST) {
        this.VOCA_DISPLAY_RUBY_VOCA_LIST = VOCA_DISPLAY_RUBY_VOCA_LIST;
    }

    public List<String> getLIST_RUBY_TEXT() {
        return LIST_RUBY_TEXT;
    }

    public void setLIST_RUBY_TEXT(List<String> LIST_RUBY_TEXT) {
        this.LIST_RUBY_TEXT = LIST_RUBY_TEXT;
    }

    public List<DTO_INPUT_LINE_NLP_PARSED> getLIST_NLP_PARSED() {
        return LIST_NLP_PARSED;
    }

    public void setLIST_NLP_PARSED(List<DTO_INPUT_LINE_NLP_PARSED> LIST_NLP_PARSED) {
        this.LIST_NLP_PARSED = LIST_NLP_PARSED;
    }

    private DTO_OUTPUT_RUBY_TEXT() {
        // private constructor to prevent direct instantiation
    }

    public static class Builder {
        private DTO_OUTPUT_RUBY_TEXT rubyTextOutput;

        public Builder() {
            rubyTextOutput = new DTO_OUTPUT_RUBY_TEXT();
        }

        public Builder INPUT_TEXT(String inputText) {
            rubyTextOutput.INPUT_TEXT = inputText;
            return this;
        }

        public Builder VOCA_DISPLAY_RUBY_TEXT(String vocaDisplayRubyText) {
            rubyTextOutput.VOCA_DISPLAY_RUBY_TEXT = vocaDisplayRubyText;
            return this;
        }

        public Builder VOCA_LIST(Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> vocaList) {
            rubyTextOutput.VOCA_LIST = vocaList;
            return this;
        }

        public Builder VOCA_DISPLAY_RUBY_VOCA_LIST(List<List<DTO_VOCA_DETAIL_RUBY_TEXT>> vocaDisplayRubyVocaList) {
            rubyTextOutput.VOCA_DISPLAY_RUBY_VOCA_LIST = vocaDisplayRubyVocaList;
            return this;
        }

        public Builder LIST_RUBY_TEXT(List<String> listRubyText) {
            rubyTextOutput.LIST_RUBY_TEXT = listRubyText;
            return this;
        }

        public Builder LIST_NLP_PARSED(List<DTO_INPUT_LINE_NLP_PARSED> listNlpParsed) {
            rubyTextOutput.LIST_NLP_PARSED = listNlpParsed;
            return this;
        }

        public DTO_OUTPUT_RUBY_TEXT build() {
            return rubyTextOutput;
        }
    }
}

