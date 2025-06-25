package com.dalread.util.arasubtitle;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DalReadTextService {

    MOVIE_SRTService movie_SRTService;


    MOVIE_BracketSubtitleService movie_BracketSubtitleService;
    //

    MOVIE_ASSService movie_ASSService;


    MOVIE_SMIService movie_SMIService;


    MOVIE_SQLITEService movie_SQLITEService;



//    private Map<String, Object> parseReq(HttpServletRequest req) throws Exception {
//        Map<String, Object> returnVal = new HashMap<String, Object>();
//        int uid = 0;
//        String studyLang = Constants.LANG_EN;
//        String dispMeaningLang = Constants.LANG_KO;
//
//        String strParam = req.getParameter(Constants.KEY_COMMENT);
//        DLog.i("","comment : " + strParam);
//        String strUID = req.getParameter(Constants.KEY_UID);
//        studyLang = req.getParameter(Constants.KEY_studyLang);
//        dispMeaningLang = req.getParameter(Constants.KEY_langDisplay);
//
//        if (strUID.length() > 0) {
//            uid = Integer.parseInt(strUID);
//        } else {
//            uid = Constants.UID_DEFAULT;
//        }
//        logger.debug("UID : " + uid);
//
//        returnVal.put(Constants.KEY_COMMENT, strParam);
//        returnVal.put(Constants.KEY_UID, uid);
//        returnVal.put(Constants.KEY_studyLang, studyLang);
//        returnVal.put(Constants.KEY_langDisplay, dispMeaningLang);
//        return returnVal;
//    }

//	private DTO_OUTPUT_RUBY_FILE parseStringDTO(DTO_INPUT_MAKE_RUBY dto) throws Exception {
//		AbstractLanguage lang = langFactory.getLanguageService(dalVocaService.convertLangCodeToLangName(dto.getLANG_STUDY_CODE()));
//		return lang.makeHTMLWithMeaningFromMapDTO(dto);
//	}


//    private Map<String, Object> parseString(Map<String, List<String>> mapAllTextWithoutHTMLTag, String studyLang, String dispMeaningLang, String sourceType) throws Exception {
//        Map<String, Object> returnVal = makeHTMLWithMeaningFromMap(mapAllTextWithoutHTMLTag, studyLang, dispMeaningLang, sourceType);
//        return returnVal;
//    }
//    // strOri(본문)을 분석하여 유일한 단어들을 추출하고 본문에 단어의 뜻등을 추가한 HTML을
//    // 만들어준다.(htmlWithMeaning)
//    public Map<String, Object> makeHTMLWithMeaningFromMap(Map<String, List<String>> mapAllTextWithoutHTMLTag, String studyLang, String dispMeaningLang, int uid, String sourceType) {
//        Map<String, Object> returnVal = new HashMap<String, Object>();
//        try {
//            // clearMapAndListOfWords();
//            // 문장의 Array로 부터 문장을 하나로 합친다.
//            String strOri = mergeMapAllTextWithoutHTMLTag(mapAllTextWithoutHTMLTag);
//            String fld_Meaning = getUserDispMeaningLangFldName(dispMeaningLang);
//            String fld_Meaning_TTS = getUserDispMeaningLangTTSFldName(dispMeaningLang);
//            DLog.i("","strOri : " + strOri);
//            DLog.i("","studyLang : " + studyLang);
//            DLog.i("","dispMeaningLang : " + dispMeaningLang);
//            DLog.i("","uid : " + uid);
//            DLog.i("","fld_Meaning : " + fld_Meaning);
//
////			long startTime = System.currentTimeMillis();
////			Date startParsingDate = new Date();
//            boolean isStrOverMax = false;
//
//
//            Integer maxIDofWord = getMaxIDofWord();
//            //문장에서 NLP를 통해서 단어와 단어의 원형 그리고 POS를 가져온다. (POS는 현재 일본어/한국어에서만 사용한다.)
//            Map<String, Object> mapExtractedWords = extractWordsFromText(mapAllTextWithoutHTMLTag);
//
//            long endOfExtractedWords = System.currentTimeMillis();
//            Map<String, List<List<String>>> mapListFromStrOri = (Map<String, List<List<String>>>) mapExtractedWords.get(Constants.KEY_listWordsFromText); //문장에서 단어의 순서대로 단어의 NLP정보를 가짐.
//            Map<String, Object> mapAllWords = (Map<String, Object>) mapExtractedWords.get(Constants.KEY_mapWordsFromText); //단어의 순서와 상관없이 단어의 NLP정보를 가짐. Map형식으로 Key로 Value를 가져옴.
//
//            // 유일한 단어중 사전에 있는 단어만 별도로 추출한다.
//            Map<String, Object> mapWithWorduniqueAndDataForJSP = new HashMap<String, Object>();
//            Integer wordCount = mapAllWords.size();
//            mapWithWorduniqueAndDataForJSP = getUniqueWordsInDicWithHTMLTag(mapAllWords, fld_Meaning, fld_Meaning_TTS, uid, maxIDofWord, sourceType);
//
//            long endOfWorduniqueAndDataForJSP = System.currentTimeMillis();
//            Map<String, String> mapWordUnique = (Map<String, String>) mapWithWorduniqueAndDataForJSP.get("getUniqueWordsInDicWithHTMLTag_mapWordUniqueInDic");
//            Map<String, JSONObject> mapUniqueWordsForJSP = (Map<String, JSONObject>) mapWithWorduniqueAndDataForJSP.get("getUniqueWordsInDicWithHTMLTag_mapUniqueWordsForJSP");
//            List<String> listUinqueWordNameLowcaseForJSP = (List<String>) mapWithWorduniqueAndDataForJSP.get("getUniqueWordsInDicWithHTMLTag_listUinqueWordNameLowcaseForJSP");
//
//            // 기존문장에 모르는 단어는 뜻을 추가한 HTML을 만든다.
//            Map<String, List<String>> mapTextWithMeaning = makeHTMLWithMeaning(mapListFromStrOri, mapWordUnique, sourceType);
//
//            long endOfHtmlWithMeaning = System.currentTimeMillis();
//            // TO CHECK:이거 정렬을 여기서 해야 하나?
//            if (listUinqueWordNameLowcaseForJSP != null) {
//                Collections.sort(listUinqueWordNameLowcaseForJSP, String.CASE_INSENSITIVE_ORDER);
//            }
//
//            returnVal.put(Constants.KEY_HTMLWithMeaningOfWordMap, mapTextWithMeaning);
//            returnVal.put(Constants.KEY_strOri, strOri);
//            returnVal.put(Constants.KEY_IS_STRING_OVER_MAX_WORD_COUNT, isStrOverMax);
//            returnVal.put(Constants.KEY_blnShowMeaningAsRubyText, getShowMeaningAsRubyText());
//
//            returnVal.put(Constants.KEY_listUinqueWordNameLowcase, listUinqueWordNameLowcaseForJSP);
//            returnVal.put("mapWords", mapUniqueWordsForJSP); // HTMLAdd~.jsp에서 단어리스트에서 단어/발음/뜻 표시시 사용하는데 상수로 해도 될려나?
//            returnVal.put(Constants.KEY_mapWordsFromText, mapUniqueWordsForJSP);
//
//            returnVal.put(Constants.KEY_WORD_COUNT, wordCount);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return returnVal;
//    }

    private Integer getMaxIDofWord() {
        Integer maxIDofWord = 0;
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put(Constants.KEY_TBL_NAME, getTbl_DIC());
//            maxIDofWord = sqlSession.selectOne("DalReadTextMapper.getMaxIDofWord", map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxIDofWord;

    }

    public String getUserDispMeaningLangFldName(String dispMeaningLang) {
        return convertFldLangEngToFldName(dispMeaningLang);
    }

    public String convertFldLangEngToFldName(String langNameInEnglish) {

        String strDispMeaningFldName = Constants.FLD_MEANING_KO;
        switch (langNameInEnglish.toUpperCase()) {
            case Constants.LANG_AR:
                strDispMeaningFldName = Constants.FLD_MEANING_AR;
                break;
            case Constants.LANG_BN:
                strDispMeaningFldName = Constants.FLD_MEANING_BN;
                break;
            case Constants.LANG_CH_S:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_S;
                break;
            case Constants.LANG_CH_T:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_T;
                break;
            case Constants.LANG_CS:
                strDispMeaningFldName = Constants.FLD_MEANING_CS;
                break;
            case Constants.LANG_DA:
                strDispMeaningFldName = Constants.FLD_MEANING_DA;
                break;
            case Constants.LANG_DE:
                strDispMeaningFldName = Constants.FLD_MEANING_DE;
                break;
            case Constants.LANG_EL:
                strDispMeaningFldName = Constants.FLD_MEANING_EL;
                break;

            case Constants.LANG_EN:
                strDispMeaningFldName = Constants.FLD_MEANING_ENG;
                break;
            case Constants.LANG_ES:
                strDispMeaningFldName = Constants.FLD_MEANING_ES;
                break;
            case Constants.LANG_FI:
                strDispMeaningFldName = Constants.FLD_MEANING_FI;
                break;
            case Constants.LANG_FR:
                strDispMeaningFldName = Constants.FLD_MEANING_FR;
                break;
            case Constants.LANG_HE:
                strDispMeaningFldName = Constants.FLD_MEANING_HE;
                break;
            case Constants.LANG_HI:
                strDispMeaningFldName = Constants.FLD_MEANING_HI;
                break;
            case Constants.LANG_HR:
                strDispMeaningFldName = Constants.FLD_MEANING_HR;
                break;
            case Constants.LANG_HU:
                strDispMeaningFldName = Constants.FLD_MEANING_HU;
                break;
            case Constants.LANG_ID:
                strDispMeaningFldName = Constants.FLD_MEANING_ID;
                break;
            case Constants.LANG_IT:
                strDispMeaningFldName = Constants.FLD_MEANING_IT;
                break;
            case Constants.LANG_JP:
                strDispMeaningFldName = Constants.FLD_MEANING_JP;
                break;
            case Constants.LANG_KO:
                strDispMeaningFldName = Constants.FLD_MEANING_KO;
                break;
            case Constants.LANG_NL:
                strDispMeaningFldName = Constants.FLD_MEANING_NL;
                break;
            case Constants.LANG_NO:
                strDispMeaningFldName = Constants.FLD_MEANING_NO;
                break;
            case Constants.LANG_PL:
                strDispMeaningFldName = Constants.FLD_MEANING_PL;
                break;
            case Constants.LANG_PT:
                strDispMeaningFldName = Constants.FLD_MEANING_PT;
                break;
            case Constants.LANG_RO:
                strDispMeaningFldName = Constants.FLD_MEANING_RO;
                break;
            case Constants.LANG_RU:
                strDispMeaningFldName = Constants.FLD_MEANING_RU;
                break;
            case Constants.LANG_SK:
                strDispMeaningFldName = Constants.FLD_MEANING_SK;
                break;

            case Constants.LANG_SV:
                strDispMeaningFldName = Constants.FLD_MEANING_SV;
                break;
            case Constants.LANG_TH:
                strDispMeaningFldName = Constants.FLD_MEANING_TH;
                break;
            case Constants.LANG_TR:
                strDispMeaningFldName = Constants.FLD_MEANING_TR;
                break;
            case Constants.LANG_UK:
                strDispMeaningFldName = Constants.FLD_MEANING_UK;
                break;
            case Constants.LANG_VI:
                strDispMeaningFldName = Constants.FLD_MEANING_VI;
                break;
        }
        return strDispMeaningFldName;
    }
    public String getUserDispMeaningLangTTSFldName(String dispMeaningLang) {
        return convertFldLangEngToLongFldName(dispMeaningLang);
    }

    public String convertFldLangEngToLongFldName(String langNameInEnglish) {

        String strDispMeaningFldName = Constants.FLD_MEANING_KO_TTS;
        switch (langNameInEnglish.toUpperCase()) {
            case Constants.LANG_AR:
                strDispMeaningFldName = Constants.FLD_MEANING_AR_TTS;
                break;
            case Constants.LANG_BN:
                strDispMeaningFldName = Constants.FLD_MEANING_BN_TTS;
                break;
            case Constants.LANG_CH_S:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_S_TTS;
                break;
            case Constants.LANG_CH_T:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_T_TTS;
                break;
            case Constants.LANG_CS:
                strDispMeaningFldName = Constants.FLD_MEANING_CS_TTS;
                break;
            case Constants.LANG_DA:
                strDispMeaningFldName = Constants.FLD_MEANING_DA_TTS;
                break;
            case Constants.LANG_DE:
                strDispMeaningFldName = Constants.FLD_MEANING_DE_TTS;
                break;
            case Constants.LANG_EL:
                strDispMeaningFldName = Constants.FLD_MEANING_EL_TTS;
                break;
            case Constants.LANG_EN:
                strDispMeaningFldName = Constants.FLD_MEANING_ENG_TTS;
                break;
            case Constants.LANG_ES:
                strDispMeaningFldName = Constants.FLD_MEANING_ES_TTS;
                break;
            case Constants.LANG_FI:
                strDispMeaningFldName = Constants.FLD_MEANING_FI_TTS;
                break;
            case Constants.LANG_FR:
                strDispMeaningFldName = Constants.FLD_MEANING_FR_TTS;
                break;
            case Constants.LANG_HE:
                strDispMeaningFldName = Constants.FLD_MEANING_HE_TTS;
                break;
            case Constants.LANG_HI:
                strDispMeaningFldName = Constants.FLD_MEANING_HI_TTS;
                break;
            case Constants.LANG_HR:
                strDispMeaningFldName = Constants.FLD_MEANING_HR_TTS;
                break;
            case Constants.LANG_HU:
                strDispMeaningFldName = Constants.FLD_MEANING_HU_TTS;
                break;
            case Constants.LANG_ID:
                strDispMeaningFldName = Constants.FLD_MEANING_ID_TTS;
                break;
            case Constants.LANG_IT:
                strDispMeaningFldName = Constants.FLD_MEANING_IT_TTS;
                break;
            case Constants.LANG_JP:
                strDispMeaningFldName = Constants.FLD_MEANING_JP_TTS;
                break;
            case Constants.LANG_KO:
                strDispMeaningFldName = Constants.FLD_MEANING_KO_TTS;
                break;
            case Constants.LANG_NL:
                strDispMeaningFldName = Constants.FLD_MEANING_NL_TTS;
                break;
            case Constants.LANG_NO:
                strDispMeaningFldName = Constants.FLD_MEANING_NO_TTS;
                break;
            case Constants.LANG_PL:
                strDispMeaningFldName = Constants.FLD_MEANING_PL_TTS;
                break;
            case Constants.LANG_PT:
                strDispMeaningFldName = Constants.FLD_MEANING_PT_TTS;
                break;
            case Constants.LANG_RO:
                strDispMeaningFldName = Constants.FLD_MEANING_RO_TTS;
                break;
            case Constants.LANG_RU:
                strDispMeaningFldName = Constants.FLD_MEANING_RU_TTS;
                break;
            case Constants.LANG_SK:
                strDispMeaningFldName = Constants.FLD_MEANING_SK_TTS;
                break;
            case Constants.LANG_SV:
                strDispMeaningFldName = Constants.FLD_MEANING_SV_TTS;
                break;
            case Constants.LANG_TH:
                strDispMeaningFldName = Constants.FLD_MEANING_TH_TTS;
                break;
            case Constants.LANG_TR:
                strDispMeaningFldName = Constants.FLD_MEANING_TR_TTS;
                break;
            case Constants.LANG_UK:
                strDispMeaningFldName = Constants.FLD_MEANING_UK_TTS;
                break;
            case Constants.LANG_VI:
                strDispMeaningFldName = Constants.FLD_MEANING_VI_TTS;
                break;
        }
        return strDispMeaningFldName;
    }

    private String mergeMapAllTextWithoutHTMLTag(Map<String, List<String>> mapAllTextWithoutHTMLTag) {
        StringBuilder strBuilder = new StringBuilder();
        for (String strFilePath : mapAllTextWithoutHTMLTag.keySet()) {
            List<String> arrContents = mapAllTextWithoutHTMLTag.get(strFilePath);
            for (String strOne : arrContents) {
                strBuilder.append(strOne);
                strBuilder.append(System.getProperty("line.separator"));
            }
        }
        String strOri = strBuilder.toString();
        return strOri;
    }

//    public Map<String, Object> getEpubResult(Map<String, List<String>> mapAllTextWithoutHTMLTag, String studyLang, String dispMeaningLang) throws Exception {
//        Map<String, Object> returnVal = new HashMap<String, Object>();
//
//        try {
//            returnVal = parseString(mapAllTextWithoutHTMLTag, studyLang, dispMeaningLang, Constants.SOURCETYPE_MOBILE);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return returnVal;
//    }
//
//    public AbstractTranslateFileService chooseTranslateService(MultipartFile file, String translateType, String fileExtension) {
//        AbstractTranslateFileService translateFile = null;
//
//        if (translateType.equals(Constants.TRANSLATE_TYPE_BOOK)) {
//            if (fileExtension.toLowerCase().equals(Constants.FILEEXT_epub)) {
//                translateFile = book_EPubService;
////							} else if (fileExtension.toLowerCase().equals(Constants.FILEEXT_txt)) {
//            } else {
//                translateFile = book_TEXTService;
//            }
//
//        } else {
//            int subtitleFormatFromContent = checkSubtitleFileFormatFromContent(file);
//            if (subtitleFormatFromContent > Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_NONE) {
//                if (subtitleFormatFromContent == Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_SMI) {
//                    translateFile = movie_SMIService;
//                } else if (subtitleFormatFromContent == Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_SRT) {
//                    translateFile = movie_SRTService;
//                } else if (subtitleFormatFromContent == Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_ASS) {
//                    translateFile = movie_ASSService;
//                } else if (subtitleFormatFromContent == Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_BRACKET) {
//                    translateFile = movie_BracketSubtitleService;
//                } else if (subtitleFormatFromContent == Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_LRC) {
//                    translateFile = movie_LRCService;
//                }
//            } else {
//                if (fileExtension.toLowerCase().equals(Constants.FILEEXT_srt)) {
//                    translateFile = movie_SRTService;
//                } else if (fileExtension.toLowerCase().equals(Constants.FILEEXT_ass) || fileExtension.toLowerCase().equals(Constants.FILEEXT_ssa) ) {
//                    translateFile = movie_ASSService;
//                } else if (fileExtension.toLowerCase().equals(Constants.FILEEXT_smi)) {
//                    translateFile = movie_SMIService;
//                } else if (fileExtension.toLowerCase().equals(Constants.FILEEXT_sqlite)) {
//                    translateFile = movie_SQLITEService;
//                } else if (fileExtension.toLowerCase().equals(Constants.FILEEXT_lrc)) {
//                    translateFile = movie_LRCService;
//                } else {
//                    translateFile = movie_SRTService;
//                }
//            }
//        }
//        return translateFile;
//    }
//
//    private int checkSubtitleFileFormatFromContent(MultipartFile file) {
//        int subtitleFormatFromContent = Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_NONE;
//        try {
//            String content = new String(file.getBytes());
//            List<String> subtitleList = new BufferedReader(new StringReader(content))
//                    .lines()
//                    .collect(Collectors.toList());
//
//            if (isSMISubtitleFile(subtitleList)) {
//                subtitleFormatFromContent = Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_SMI;
//            } else if (isSRTSubtitleFile(subtitleList)) {
//                subtitleFormatFromContent = Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_SRT;
//            } else if (isASSSubtitleFile(subtitleList)) {
//                subtitleFormatFromContent = Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_ASS;
//            } else if (isBracketSubtitleFile(subtitleList)) {
//                subtitleFormatFromContent = Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_BRACKET;
//            } else if (isLrcFile(subtitleList)) {
//                subtitleFormatFromContent = Constants.SUBTITLE_FILE_FORMAT_FROM_CONTENT_LRC;
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return subtitleFormatFromContent;
//    }

    private boolean isSMISubtitleFile(List<String> subtitleList) {
        boolean result = false;
        try {

            Pattern EXPRESSION_PATTERN = Pattern.compile("^<sync start.*$", Pattern.CASE_INSENSITIVE);
            for(String subtitle : subtitleList) {
                Matcher matcher = EXPRESSION_PATTERN.matcher(subtitle);
                if (matcher.matches()) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    private boolean isSRTSubtitleFile(List<String> subtitleList) {
        boolean result = false;
        try {
            for(String subtitle : subtitleList) {
                if (parseTime(subtitle)) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    private boolean isASSSubtitleFile(List<String> subtitleList) {
        boolean result = false;
        try {
            Pattern EXPRESSION_PATTERN = Pattern.compile("^Dialogue:.*$", Pattern.CASE_INSENSITIVE);
            for(String subtitle : subtitleList) {
                Matcher matcher = EXPRESSION_PATTERN.matcher(subtitle);
                if (matcher.matches()) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    private boolean isBracketSubtitleFile(List<String> subtitleList) {
        boolean result = false;
        try {
            Pattern EXPRESSION_PATTERN = Pattern.compile("^\\[\\d+\\]\\[\\d+\\].*$");
            for(String subtitle : subtitleList) {
                Matcher matcher = EXPRESSION_PATTERN.matcher(subtitle);
                if (matcher.matches()) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    //Lyrics file format
    private boolean isLrcFile(List<String> subtitleList) {
        boolean result = false;
        try {
            for(String subtitle : subtitleList) {
                Matcher matcher = Constants.EXPRESSION_PATTERN_LRC_TIME.matcher(subtitle);
                if (matcher.matches()) {
                    result = true;
                    break;
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * Extract a subtitle time from string
     *
     * @param timeLine: ex 00:02:08,822 --> 00:02:11,574
     * @return the SRTTime object
     * @throws InvalidSRTSubException
     */
    private boolean parseTime(String timeLine) {
        boolean result = false;
        String normalizeTimeLine = normalizeTimeLine(timeLine);
        String times[] = normalizeTimeLine.split(SRTTime.DELIMITER.trim());

        try {
            if (times.length == 2) {
                LocalTime start = SRTTime.fromString(times[0]);
                LocalTime end = SRTTime.fromString(times[1]);
                SRTTime time = new SRTTime(start, end);
                result = true;
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    // 시간뒤에 xx같은게 붙어있는것을 제거한다.
    private String normalizeTimeLine(String timeLine) {
        String normalizedTimeLine = timeLine;
        String times[] = timeLine.split(SRTTime.DELIMITER.trim());

        if (times.length == 2) {
            String normalizedStartTime= times[0].trim();
            String endTime = times[1].trim();
            String normalizedEndTime  = endTime.split(" ")[0];
            normalizedStartTime = makeTimeToCompleteForm(normalizedStartTime); //00,000을 00:00:00,000으로 바꾸어준다.
            normalizedEndTime = makeTimeToCompleteForm(normalizedEndTime);

            StringBuilder sb = new StringBuilder();
            sb.append(normalizedStartTime);
            sb.append(SRTTime.DELIMITER.trim());
            sb.append(normalizedEndTime);
            normalizedTimeLine = sb.toString();
//				public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(SRTTime.PATTERN);
//				public static final String PATTERN = "HH:mm:ss,SSS";
//				private static final String TS_PATTERN = "%02d:%02d:%02d,%03d";
//				public static final String DELIMITER = " --> ";
        }

        return normalizedTimeLine;
    }

    private String makeTimeToCompleteForm(String text) {
        int hour = 0, minute = 0, second = 0, milliSecond = 0;
        String strMilliSecond = "000";
        String[] values = text.split(",");
        if (values.length > 0) {
            String[] times = values[0].split(":");

            if (times.length == 3){
                hour = Integer.parseInt(times[0]);
                minute = Integer.parseInt(times[1]);
                second = Integer.parseInt(times[2]);
            } else if (times.length == 2) {
                minute = Integer.parseInt(times[0]);
                second = Integer.parseInt(times[1]);
            } else {
                second = Integer.parseInt(times[0]);
            }

            if (values.length > 1) {
                strMilliSecond = values[1];
                if (strMilliSecond.length() == 1) {
                    strMilliSecond += "00";
                } else if (strMilliSecond.length() == 2) {
                    strMilliSecond += "0";
                }
                milliSecond = Integer.parseInt(strMilliSecond);
            }
        }
        String completedForm = String.format("%02d:%02d:%02d,", hour, minute ,second) + strMilliSecond;
        return completedForm;
    }

//    public void updateUserAnalyzeSubtitleHistory(DTO_INPUT_MAKE_RUBY dto) {
//        try {
//            long currentDateTime = DalString.getCurrentDateTimeInTIMESTAMP();
////			Integer studyLangCode = getLangCodeFromLangName(studyLang);
//
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put(Constants.KEY_TBL_NAME, Constants.TBL_USER_ANALYZE_SUBTITLE_HISTORY);
//            map.put("uid", dto.getUID());
//            String userName = loginServiceImpl.getUserName(dto.getUID());
//            map.put("strAppName", dto.getAPP_NAME().toUpperCase());
//            map.put("userName", userName);
//            map.put("currentDateTime", currentDateTime);
//            map.put("clientType", dto.getCLIENT_TYPE());
//
//            Integer intResultSelect = sqlSession.selectOne("DalVocaMapper.selectUserAnalyzeSubtitleHistory", map);
//            if (intResultSelect == 0) {
//                Integer intResult = sqlSession.insert("DalVocaMapper.insertUserAnalyzeSubtitleHistory", map);
//            } else {
//                Integer intResultUpdate = sqlSession.update("DalVocaMapper.updateUserAnalyzeSubtitleHistory", map);
//            }
//
//        } catch (Exception e) {
//            logger.error("fail to open mysql");
//            e.printStackTrace();
//        }
//    }
}