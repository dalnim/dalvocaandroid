package com.dalread.util.arasubtitle;

import java.util.ArrayList;
import java.util.List;

public class DTO_NLP_INPUT_TEXT_AND_WORD_LIST {
    private String INPUT_TEXT = "";
    private List<String> WORD_LIST_IN_INPUT_TEXT = new ArrayList<String>();

    private DTO_NLP_INPUT_TEXT_AND_WORD_LIST() {}

    public String getINPUT_TEXT() {
        return INPUT_TEXT;
    }

    public List<String> getWORD_LIST_IN_INPUT_TEXT() {
        return WORD_LIST_IN_INPUT_TEXT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String inputText = "";
        private List<String> wordList = new ArrayList<String>();

        public Builder setInputText(String inputText) {
            this.inputText = inputText;
            return this;
        }

        public Builder addWord(String word) {
            this.wordList.add(word);
            return this;
        }

        public DTO_NLP_INPUT_TEXT_AND_WORD_LIST build() {
            DTO_NLP_INPUT_TEXT_AND_WORD_LIST dto = new DTO_NLP_INPUT_TEXT_AND_WORD_LIST();
            dto.INPUT_TEXT = this.inputText;
            dto.WORD_LIST_IN_INPUT_TEXT = this.wordList;
            return dto;
        }
    }
}


