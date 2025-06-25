package com.dalread.util.arasubtitle;

import org.simpleframework.xml.Default;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTO_OUTPUT_RUBY_FILE {
    protected Map<String, List<String>> HTMLWithMeaningOfWordMAP = new HashMap<String, List<String>>();
    @Default protected List<String> arrHtmlWithMeaningInFile = new ArrayList<String>();

    @Default protected String strOri = "";

    @Default protected boolean IS_STRING_OVER_MAX_WORD_COUNT = true;
    @Default protected Integer showMeaningAsRubyText = 0;


    @Default protected List<String> listUinqueWordNameLowcase =  new ArrayList<String>();

    @Default protected Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapWords = new HashMap<String, DTO_VOCA_DETAIL_RUBY_TEXT>();

    @Default protected Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapWordsFromText = new HashMap<String, DTO_VOCA_DETAIL_RUBY_TEXT>();

    @Default protected Integer WORD_COUNT = 0;

}