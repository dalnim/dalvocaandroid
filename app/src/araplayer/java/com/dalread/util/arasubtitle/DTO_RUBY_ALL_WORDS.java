package com.dalread.util.arasubtitle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTO_RUBY_ALL_WORDS {

    //	private Map<String, List<DTO_SUBTITLE_CORRECT_FORMAT>> MAP_ALL_INPUT_TEXT_LINES_IN_ALL_FILES = new HashMap<String, List<DTO_SUBTITLE_CORRECT_FORMAT>>();
//	private Map<String, List<List<String>>> MAP_WORD_LIST_SAME_AS_INPUT_WORD_ORDER = new HashMap<String, List<List<String>>>();
//	private Map<String, List<String>> MAP_ALL_INPUT_TEXT_LINES_IN_ALL_FILES1 = new HashMap<String, List<String>>();
    private Map<String, List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST>> MAP_WORD_LIST_AND_INPUT_TEXT = new HashMap<String, List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST>>();
    private Map<String, WordMorpheme> MAP_WORD_MORPHEME_WITH_FREQUENCY = new HashMap<String, WordMorpheme>();

    public Map<String, List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST>> getMAP_WORD_LIST_AND_INPUT_TEXT() {
        return MAP_WORD_LIST_AND_INPUT_TEXT;
    }

    public void setMAP_WORD_LIST_AND_INPUT_TEXT(Map<String, List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST>> MAP_WORD_LIST_AND_INPUT_TEXT) {
        this.MAP_WORD_LIST_AND_INPUT_TEXT = MAP_WORD_LIST_AND_INPUT_TEXT;
    }

    public Map<String, WordMorpheme> getMAP_WORD_MORPHEME_WITH_FREQUENCY() {
        return MAP_WORD_MORPHEME_WITH_FREQUENCY;
    }

    public void setMAP_WORD_MORPHEME_WITH_FREQUENCY(Map<String, WordMorpheme> MAP_WORD_MORPHEME_WITH_FREQUENCY) {
        this.MAP_WORD_MORPHEME_WITH_FREQUENCY = MAP_WORD_MORPHEME_WITH_FREQUENCY;
    }
}
