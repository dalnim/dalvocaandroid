package com.dalread.util.arasubtitle;

public class DTO_INPUT_LINE_NLP_PARSED {
    protected String VOCA_NLP_PARSED = "";
    protected String VOCA = "";

    // Builder Class
    public static class Builder {
        private DTO_INPUT_LINE_NLP_PARSED instance;

        public Builder() {
            instance = new DTO_INPUT_LINE_NLP_PARSED();
        }

        public Builder VOCA_NLP_PARSED(String value) {
            instance.VOCA_NLP_PARSED = (value == null) ? "" : value;
            return this;
        }

        public Builder VOCA(String value) {
            instance.VOCA = (value == null) ? "" : value;
            return this;
        }

        public DTO_INPUT_LINE_NLP_PARSED build() {
            return instance;
        }
    }


}

