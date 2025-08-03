package com.dalread.util.arasubtitle;

import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.model.DIC_WORD_MODEL;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.DLog;
import com.dalread.util.VocaListUtil;
import com.facebook.stetho.json.ObjectMapper;
import com.google.gson.Gson;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import opennlp.tools.tokenize.SimpleTokenizer;

public abstract class AbstractMOVIE_SubtitleService extends AbstractTranslateFileService {
    protected static final String subtitleSmi = "subtitle.smi";
    @Override
    protected void translateInputFileDTO(File FILE_input, File FILE_input2, AbstractTranslateFileService translateFile2) {
        long startTime = System.currentTimeMillis();
        DLog.i("SUBTITLE_ANALYSIS", "=== 자막 분석 시작 ===");
        DLog.i("SUBTITLE_ANALYSIS", "파일: " + FILE_input.getName());
        
        //뜻을 달 파일명과 내용들(HTML태그들 없음. 텍스트들의 배열)
        Map<String, List<String>> mapAllTextWithoutHTMLTag =  new HashMap<String, List<String>>();
        
        long cleanStartTime = System.currentTimeMillis();
        DLog.i("SUBTITLE_ANALYSIS", "1. 파일 정리");
        cleanInputFileDTO(FILE_input);
        long cleanEndTime = System.currentTimeMillis();
        long cleanTime = cleanEndTime - cleanStartTime;
        DLog.i("SUBTITLE_ANALYSIS", "   [1 총 시간: " + cleanTime + "ms]");

        int subtitleLang = Constants.SUBTITLE_LANG_AUTO;
        int subtitleLang2 = Constants.SUBTITLE_LANG_AUTO;
        if (FILE_input2 != null) {
            boolean hasSmi_SubtitleFile = false;
            if ((FilenameUtils.getExtension(FILE_input.getName()).equals(Constants.FILEEXT_smi))
                    || (FilenameUtils.getExtension(FILE_input2.getName()).equals(Constants.FILEEXT_smi))) {
                hasSmi_SubtitleFile = true;
            }
            if (hasSmi_SubtitleFile) {
                subtitleLang = Constants.SUBTITLE_LANG_STUDY_LANG;
                subtitleLang2 = Constants.SUBTITLE_LANG_MOTHER_TONGUE;

            } else {
                subtitleLang = Constants.SUBTITLE_LANG_AUTO;
                subtitleLang2 = Constants.SUBTITLE_LANG_AUTO;
            }
        }


        long parseStartTime = System.currentTimeMillis();
        DLog.i("SUBTITLE_ANALYSIS", "2. 자막 파싱");
        DTO_SUBTITLE_PARSED dtoSubtitleParsed = getDialogueInSubtitleDTO(FILE_input, subtitleLang);
        long parseEndTime = System.currentTimeMillis();
        long parseTime = parseEndTime - parseStartTime;
        DLog.i("SUBTITLE_ANALYSIS", "   * 대화 수: " + dtoSubtitleParsed.getLIST_DIALOGUE_INFO().size() + "개");


        //자막을 루비형태로 리턴해야 할때...
            //싱크가 안맞는 자막이면 싱크를 맞춰준다.
            if ((FILE_input2 != null)
                    || ((dtoSubtitleParsed.isNEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE()))) {
                if (FILE_input2 != null) {
                    dtoSubtitleParsed = appendSubtitle2(FILE_input2, translateFile2, dtoSubtitleParsed, subtitleLang2);
                }
                //		if (dtoSubtitleParsed.isNEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE()) {
                dtoSubtitleParsed = syncSubtitlesDTO(dtoSubtitleParsed);
            }


            //새로운 자막이면 테이블에 넣어둔다. (SUBTITLE_언어, SUBTITLE_언어_UNTUNED 테이블에 넣는다. DIC_언어_SENTENCE테이블에는 안 넣는다.)
//            insertSubtitleToTableDTO(dtoSubtitleParsed);

            //		//영어 공부시 영어자막이 있어야 한다. (예를들어  한국어 자막만 넣으면 영어자막이 없으므로 아래작업은 불필요하다)
            //		if (dtoSubtitleParsed.isHAS_STUDY_LANG_DIALOGUE() == true) {
            //테이블로 부터 자막을 VOCA, VOCA_ID, VOCA_TYPE을 받아온다.
//            getSubtitleVocaIDDTO(dtoSubtitleParsed);

            //자막의 북마크, KNOW를 받아온다.

            //자막의 북마크는 아직 서버에 저장하는 기능이 없다. 자막의 KNOW는 저장하나?
            //		}

//            dto.setLIST_INPUT_TEXT(dtoSubtitleParsed.getLIST_DIALOGUE_STUDY_LANG());
        List<String> LIST_INPUT_TEXT = dtoSubtitleParsed.getLIST_DIALOGUE_STUDY_LANG();
        DLog.i("SUBTITLE_ANALYSIS", "   * 분석할 텍스트 수: " + LIST_INPUT_TEXT.size() + "개");
        DLog.i("SUBTITLE_ANALYSIS", "   [2 총 시간: " + parseTime + "ms]");
        
        long rubyStartTime = System.currentTimeMillis();
        DLog.i("SUBTITLE_ANALYSIS", "3. 루비 텍스트 생성 시작");
        DTO_OUTPUT_RUBY_TEXT dtoOutputRubyText = makeRubyTextDTO(LIST_INPUT_TEXT);
        long rubyEndTime = System.currentTimeMillis();
        long rubyTime = rubyEndTime - rubyStartTime;

        DLog.i("SUBTITLE_ANALYSIS", "   [3 총 시간: " + rubyTime + "ms]");
        
        long dbStartTime = System.currentTimeMillis();
        DLog.i("SUBTITLE_ANALYSIS", "4. DB 저장");
        insertRubyTextInSqliteDTO(dtoOutputRubyText, dtoSubtitleParsed, LIST_INPUT_TEXT);
        long dbEndTime = System.currentTimeMillis();
        long dbTime = dbEndTime - dbStartTime;
        DLog.i("SUBTITLE_ANALYSIS", "   [4 총 시간: " + dbTime + "ms]");
        
        long totalTime = System.currentTimeMillis() - startTime;
        DLog.i("SUBTITLE_ANALYSIS", "=== 자막 분석 완료: 총 " + totalTime + "ms ===");

    }
    // 문장으로 부터 단어를 추출한다.
    private DTO_RUBY_ALL_WORDS extractWordsFromTextDTO(Map<String, List<String>> mapAllTextWithoutHTMLTag) {
        DTO_RUBY_ALL_WORDS dtoRubyAllWords = new DTO_RUBY_ALL_WORDS();
        Map<String, WordMorpheme> mapWordMorphemeWithFrequency = new HashMap<String, WordMorpheme>();
        Map<String, List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST>> mapWordListAndInputText = new HashMap<String, List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST>>();

        for (String strFilePath : mapAllTextWithoutHTMLTag.keySet()) {
            List<String> listDtoSubtitleCorrectFormat = mapAllTextWithoutHTMLTag.get(strFilePath);
            List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST> listNLPInputTextAndWordList = new ArrayList<>();
            for (String strInputTextLine : listDtoSubtitleCorrectFormat) {
                DTO_NLP_INPUT_TEXT_AND_WORD_LIST dtoNLPInputTextAndWordList = new DTO_NLP_INPUT_TEXT_AND_WORD_LIST.Builder().setInputText(strInputTextLine).build();
                extractWordsFromTextDTO2Main(mapWordMorphemeWithFrequency, dtoNLPInputTextAndWordList);
                listNLPInputTextAndWordList.add(dtoNLPInputTextAndWordList);
            }
            mapWordListAndInputText.put(strFilePath, listNLPInputTextAndWordList);
        }

        dtoRubyAllWords.setMAP_WORD_LIST_AND_INPUT_TEXT(mapWordListAndInputText);
        dtoRubyAllWords.setMAP_WORD_MORPHEME_WITH_FREQUENCY(mapWordMorphemeWithFrequency);
        return dtoRubyAllWords;
    }
    protected void extractWordsFromTextDTO2Main(Map<String, WordMorpheme> mapMorphemeWithFrequency, DTO_NLP_INPUT_TEXT_AND_WORD_LIST dtoNLPInputTextAndWordList) {
        try {
            String inputText = dtoNLPInputTextAndWordList.getINPUT_TEXT();
            String simpleTokens[] = tokenizeText(inputText);
            DLog.i("","count of tokens : " + simpleTokens.length);
            
            // 인덱스 기반 처리로 변경
            int currentIndex = 0;
            for (int i = 0; i < simpleTokens.length; i++) {
                String strWord = simpleTokens[i];
                
                // 현재 위치에서 토큰 찾기
                int tokenIndex = inputText.indexOf(strWord, currentIndex);
                if (tokenIndex == -1) {
                    // 토큰을 찾을 수 없는 경우, 단어만 처리
                    processWordOnly(strWord, dtoNLPInputTextAndWordList, mapMorphemeWithFrequency);
                    continue;
                }
                
                // 토큰 앞의 공백/문자 처리
                String strBeforeWord = inputText.substring(currentIndex, tokenIndex);
                processBeforeWord(strBeforeWord, dtoNLPInputTextAndWordList);
                
                // 토큰 처리
                processWord(strWord, dtoNLPInputTextAndWordList, mapMorphemeWithFrequency);
                
                // 다음 위치로 이동
                currentIndex = tokenIndex + strWord.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void processBeforeWord(String strBeforeWord, DTO_NLP_INPUT_TEXT_AND_WORD_LIST dtoNLPInputTextAndWordList) {
        if (strBeforeWord.isEmpty()) {
            return;
        }
        
        // StringBuilder를 사용하여 한 번에 처리
        StringBuilder processedBeforeWord = new StringBuilder(strBeforeWord);
        
        // HTML 태그 및 특수문자 처리 (한 번에 처리)
        if (processedBeforeWord.indexOf(Constants.HTMLTAG_SPACE_nbsp) != -1) {
            replaceAll(processedBeforeWord, Constants.HTMLTAG_SPACE_nbsp, Constants.HTMLTAG_SPACE);
        }
        replaceAll(processedBeforeWord, "\r\n", Constants.SPACE + "<br />");
        replaceAll(processedBeforeWord, "\n", Constants.SPACE + "<br />");
        replaceAll(processedBeforeWord, "\t", "&#9;");
        replaceAll(processedBeforeWord, "&", "&#38;");
        
        dtoNLPInputTextAndWordList.getWORD_LIST_IN_INPUT_TEXT().add(processedBeforeWord.toString());
    }
    
    private void processWord(String strWord, DTO_NLP_INPUT_TEXT_AND_WORD_LIST dtoNLPInputTextAndWordList, Map<String, WordMorpheme> mapMorphemeWithFrequency) {
        // 단어 처리
        String processedWord = strWord.replace("&", "&#38;");
        dtoNLPInputTextAndWordList.getWORD_LIST_IN_INPUT_TEXT().add(processedWord + Constants.POS_SEPERATOR_UNDERSCORE);
        
        // 올바른 단어가 아니라도 mapWordListSameAsInputWordOrder에는 넣고, mapMorphemeWithFrequency에는 안 넣기 위해서 여기서 continue한다.
        if (!(isRightChar(strWord))) {
            return;
        }
        if (isSpecialString(strWord)) {
            //특수문자등은 mapWordsFromText에 넣으면 에러가 날수 있다...
            return;
        }

        String strWordLowercase = strWord.toLowerCase();
        String strLowcasewordWithPOS_For_Map = strWordLowercase + Constants.POS_SEPERATOR_UNDERSCORE;
        //단어의 WordMorpheme에 빈도수를 넣어준다.
        if (!mapMorphemeWithFrequency.containsKey(strLowcasewordWithPOS_For_Map)) {
            mapMorphemeWithFrequency.put(strLowcasewordWithPOS_For_Map, new WordMorpheme(strWordLowercase, "", "", "", ""));
        } else {
            WordMorpheme wordMorpheme = (WordMorpheme) mapMorphemeWithFrequency.get(strLowcasewordWithPOS_For_Map);
            wordMorpheme.increaseFrequency();
            mapMorphemeWithFrequency.put(strLowcasewordWithPOS_For_Map, wordMorpheme);
        }
    }
    
    private void processWordOnly(String strWord, DTO_NLP_INPUT_TEXT_AND_WORD_LIST dtoNLPInputTextAndWordList, Map<String, WordMorpheme> mapMorphemeWithFrequency) {
        // 토큰을 찾을 수 없는 경우의 처리
        dtoNLPInputTextAndWordList.getWORD_LIST_IN_INPUT_TEXT().add(strWord + Constants.POS_SEPERATOR_UNDERSCORE);
        
        if (!(isRightChar(strWord))) {
            return;
        }
        if (isSpecialString(strWord)) {
            return;
        }

        String strWordLowercase = strWord.toLowerCase();
        String strLowcasewordWithPOS_For_Map = strWordLowercase + Constants.POS_SEPERATOR_UNDERSCORE;
        if (!mapMorphemeWithFrequency.containsKey(strLowcasewordWithPOS_For_Map)) {
            mapMorphemeWithFrequency.put(strLowcasewordWithPOS_For_Map, new WordMorpheme(strWordLowercase, "", "", "", ""));
        } else {
            WordMorpheme wordMorpheme = (WordMorpheme) mapMorphemeWithFrequency.get(strLowcasewordWithPOS_For_Map);
            wordMorpheme.increaseFrequency();
            mapMorphemeWithFrequency.put(strLowcasewordWithPOS_For_Map, wordMorpheme);
        }
    }
    
    private void replaceAll(StringBuilder sb, String target, String replacement) {
        int index = 0;
        while ((index = sb.indexOf(target, index)) != -1) {
            sb.replace(index, index + target.length(), replacement);
            index += replacement.length();
        }
    }
    
    private String[] tokenizeText(String text) {
        return SimpleTokenizer.INSTANCE.tokenize(text);
    }
    public static boolean isSpecialString(String strOne) {
        String match = "[・,、`~!@#$%^&*+=';:：/?。「」【】<>{}・.\"()，]";
//		String match = "[・,、`~!@#$%^&*+=';:/?。「」【】<>{}・.\"]";
//		match = "[\`~!@#$%^&*()-_=+\\|\[\]{};:\'\",.<>/?]";[続きを読む]

        String strOneWithoutSpecialString =strOne.replaceAll(match, "");
        if (strOneWithoutSpecialString.length() == 0) {
            return true;
        }
        return false;

//		return strOne.matches("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]");
    }
    public boolean isRightChar(String strOne) {
        boolean blnRightChar = true;
        for (char c : strOne.toCharArray()) {
            String strOneAtIndex = Character.toString(c);
            if (!(strOneAtIndex.matches("[a-z|A-Z|']"))) {
                blnRightChar = false;
            }
        }
        return blnRightChar;
    }



    public DTO_OUTPUT_RUBY_TEXT makeRubyTextDTO(List<String> LIST_INPUT_TEXT) {
        long startTime = System.currentTimeMillis();
        DLog.i("SUBTITLE_ANALYSIS", "   * 입력 텍스트 수: " + LIST_INPUT_TEXT.size() + "개");
        
        DTO_OUTPUT_RUBY_TEXT dtoResult = new DTO_OUTPUT_RUBY_TEXT.Builder().build();
        try {

            Map<String, List<String>> mapAllInputTextWithoutHTMLTag = new HashMap<String, List<String>>();
            String strTempFileName = "dalnim";
            mapAllInputTextWithoutHTMLTag.put(strTempFileName, LIST_INPUT_TEXT);
            
            long extractStartTime = System.currentTimeMillis();
            //listComment에서 유일한 단어에 대해서 NLP 파싱을 한다.
            DTO_RUBY_ALL_WORDS mapExtractedWords = extractWordsFromTextDTO(mapAllInputTextWithoutHTMLTag);
            long extractEndTime = System.currentTimeMillis();
            long extractTime = extractEndTime - extractStartTime;
            DLog.i("SUBTITLE_ANALYSIS", "   - 단어 추출 완료: " + extractTime + "ms");
            DLog.i("SUBTITLE_ANALYSIS", "   * 추출된 고유 단어 수: " + mapExtractedWords.getMAP_WORD_MORPHEME_WITH_FREQUENCY().size() + "개");
            //이미 분석해놓은것이 있으면 가져온다.
//            addAlreadyParsedVocaToExractedWords(mapExtractedWords, LIST_INPUT_TEXT);
            long dictStartTime = System.currentTimeMillis();
            // 유일한 단어중 사전에 있는 단어만 별도로 추출한다.
            Map<String, Object> mapWithWorduniqueAndData = getUniqueWordsInDicWithHTMLTagRubyTextDTOCOMMON2(mapExtractedWords.getMAP_WORD_MORPHEME_WITH_FREQUENCY());
            long dictEndTime = System.currentTimeMillis();
            long dictTime = dictEndTime - dictStartTime;
            DLog.i("SUBTITLE_ANALYSIS", "   - 사전 검색 완료: " + dictTime + "ms");
            Map<String, String> mapWordUnique = (Map<String, String>) mapWithWorduniqueAndData.get("getUniqueWordsInDicWithHTMLTag_mapWordUniqueInDic");
            Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWordList = (Map<String, DTO_VOCA_DETAIL_RUBY_TEXT>) mapWithWorduniqueAndData.get("getUniqueWordsInDicWithHTMLTag_mapUniqueWordsForJSP");
            Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWordInfoList = (Map<String, DTO_VOCA_DETAIL_RUBY_TEXT>) mapWithWorduniqueAndData.get("getUniqueWordsInDicWithHTMLTag_mapUniqueWordsInfo");

            Integer wordCount = mapUniqueWordList.size();
            DLog.i("SUBTITLE_ANALYSIS", "   * 사전에서 찾은 단어 수: " + wordCount + "개");

            long htmlStartTime = System.currentTimeMillis();
            Map<String, Object> mapTextWithMeaning = makeHTMLWithMeaningRubyTextDTOCOMMON(mapExtractedWords, mapWordUnique, mapUniqueWordInfoList);
            long htmlEndTime = System.currentTimeMillis();
            long htmlTime = htmlEndTime - htmlStartTime;
            DLog.i("SUBTITLE_ANALYSIS", "   - HTML 루비 태그 생성 완료: " + htmlTime + "ms");

//            if (mapUniqueWordList.size() > 0) {
//                mapUniqueWordList = getJMDictInfoDTO(mapUniqueWordList, dto.getLANG_MEANING());
//            }

            List<String> arrHtmlWithMeaningInFile = (List<String>) mapTextWithMeaning.get(strTempFileName);
            List<List<DTO_VOCA_DETAIL_RUBY_TEXT>> arrWordInfoInFile = (List<List<DTO_VOCA_DETAIL_RUBY_TEXT>>)mapTextWithMeaning.get(strTempFileName+"_");
            List<DTO_INPUT_LINE_NLP_PARSED> listDtoNLPParsed = (List<DTO_INPUT_LINE_NLP_PARSED>)mapTextWithMeaning.get("listDtoNLPParsed");

//			dtoResult.setINPUT_TEXT(inputText);
//			dtoResult.setRUBY_TEXT(String.join("", arrHtmlWithMeaningInFile)); //배열을 문자열로 바꿀때 []가 앞뒤로 들어가는걸 없애준다.
            dtoResult.setVOCA_DISPLAY_RUBY_TEXT(arrHtmlWithMeaningInFile.toString());
//			dtoResult.setVOCA_COUNT(mapUniqueWordList.size());
            dtoResult.setVOCA_LIST(mapUniqueWordList);
            dtoResult.setVOCA_DISPLAY_RUBY_VOCA_LIST(arrWordInfoInFile);
            dtoResult.setLIST_RUBY_TEXT(arrHtmlWithMeaningInFile);
            dtoResult.setLIST_NLP_PARSED(listDtoNLPParsed);

//            insertAlreadyNLPParsedListDTO(dto, listDtoNLPParsed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        return dtoResult;
    }
    //원래문장(영어, 한글등이 섞여 있는)에서 분리한 영어단어(영어 학습시)를 돌면서 HTML문장을 만든다. (각 단어별 Ruby Tag는 이미 만들어져 있다.)
    protected Map<String, Object> makeHTMLWithMeaningRubyTextDTOCOMMON(DTO_RUBY_ALL_WORDS mapExtractedWords, Map<String, String> mapWordUnique,
                                                                       Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWordInfoList) {

        Map<String, List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST>> mapListFromStrOri = mapExtractedWords.getMAP_WORD_LIST_AND_INPUT_TEXT();

        Map<String, List<DTO_VOCA_POS>> mapVocaAlreadyNlpParsed = new HashMap<>();// dto.getMAP_VOCA_ALREADY_NLP_PARSED();
        Map<String, Object> mapTextWithMeaning = new HashMap<String, Object>();
        Map<String, List<String>> HTMLWithMeaningOfWordMAP = new HashMap<String, List<String>>();
        List<DTO_INPUT_LINE_NLP_PARSED> listDtoNLPParsed = new ArrayList<DTO_INPUT_LINE_NLP_PARSED>(); //기존에 DB에 존재하는 NLP결과물을 사용할 데이타
        List<WordMorpheme> listDtoNLPParsedWordMorpheme = new ArrayList<WordMorpheme>(); //기존에 DB에 존재하는 NLP결과물을 사용할 데이타

        Gson gson = new Gson();
        try {
            //EPub등은 여러개의 파일로 구성되어서 별도로 돌아야 한다.
            for (String strFilePath : mapListFromStrOri.keySet()) {
                List<DTO_NLP_INPUT_TEXT_AND_WORD_LIST> listFromStrOriInFile = mapListFromStrOri.get(strFilePath);
                List<String> arrHtmlWithMeaningInFile = new ArrayList<String>();
                List<List<DTO_VOCA_DETAIL_RUBY_TEXT>> arrWordInfoInFile = new ArrayList<List<DTO_VOCA_DETAIL_RUBY_TEXT>>();
                Integer indexForListAllInputTextLinesInOneFile = 0;
                //각 파일에 있는 각 라인들을 돈다.
                for (DTO_NLP_INPUT_TEXT_AND_WORD_LIST dtoNLPInputTextAndWordList : listFromStrOriInFile) {
                    List<String> listFromStrOri = dtoNLPInputTextAndWordList.getWORD_LIST_IN_INPUT_TEXT();
                    //VOCA_POS가 있다는것은 이 줄의 자막을 이미 다른 형태로 분석한것이 있어서 분석한것을 사용할려고 한다.
                    if (mapVocaAlreadyNlpParsed.containsKey(dtoNLPInputTextAndWordList.getINPUT_TEXT())) {
                        List<DTO_VOCA_POS> dtoVocaPOS = mapVocaAlreadyNlpParsed.get(dtoNLPInputTextAndWordList.getINPUT_TEXT());
                        listFromStrOri = dtoVocaPOS.stream()
                                .map(e -> e.getVOCA() +"_" + e.getPOSALL())
                                .collect(Collectors.toList());

                    }

                    // HTML 루비 태그 생성 최적화
                    ProcessedLineResult result = processLineForHTMLGeneration(listFromStrOri, mapWordUnique, mapUniqueWordInfoList);
                    
                    String jsonString = gson.toJson(result.getListDtoVocaTypeIDVoca());
                    DTO_INPUT_LINE_NLP_PARSED dtoNLPParsed = new DTO_INPUT_LINE_NLP_PARSED.Builder()
                            .VOCA(dtoNLPInputTextAndWordList.getINPUT_TEXT())
                            .VOCA_NLP_PARSED(jsonString)
                            .build();
                    listDtoNLPParsed.add(dtoNLPParsed);
                    arrHtmlWithMeaningInFile.add(result.getHtmlWithMeaning());
                    arrWordInfoInFile.add(result.getListDTOVocaDetailRubyText());
                }
                HTMLWithMeaningOfWordMAP.put(strFilePath, arrHtmlWithMeaningInFile);
                mapTextWithMeaning.put(strFilePath, arrHtmlWithMeaningInFile);
                mapTextWithMeaning.put(strFilePath + "_", arrWordInfoInFile);
                mapTextWithMeaning.put("listDtoNLPParsed", listDtoNLPParsed);
                mapTextWithMeaning.put("listDtoNLPParsedWordMorpheme", listDtoNLPParsed);

            }
            mapTextWithMeaning.put("HTMLWithMeaningOfWordMAP", HTMLWithMeaningOfWordMAP);
        } catch (Exception e) {
//            logger.error("fail makeHTMLWithMeaning");
            e.printStackTrace();
        }
        return mapTextWithMeaning;
    }

    // HTML 루비 태그 생성을 위한 최적화된 라인 처리 메서드
    private ProcessedLineResult processLineForHTMLGeneration(List<String> listFromStrOri, 
                                                           Map<String, String> mapWordUnique,
                                                           Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWordInfoList) {
        List<DTO_VOCA_TYPE_ID_VOCA> listDtoVocaTypeIDVoca = new ArrayList<>();
        StringBuilder htmlWithMeaning = new StringBuilder();
        List<DTO_VOCA_DETAIL_RUBY_TEXT> listDTOVocaDetailRubyText = new ArrayList<>();
        
        for (String wordFromTextWithPOS : listFromStrOri) {
            WordProcessingResult wordResult = processWordForHTML(wordFromTextWithPOS, mapWordUnique, mapUniqueWordInfoList);
            
            htmlWithMeaning.append(wordResult.getHtmlContent());
            listDtoVocaTypeIDVoca.add(wordResult.getDtoVocaTypeIDVoca());
            if (wordResult.getDtoVocaDetailRubyText() != null) {
                listDTOVocaDetailRubyText.add(wordResult.getDtoVocaDetailRubyText());
            }
        }
        
        return new ProcessedLineResult(htmlWithMeaning.toString(), listDtoVocaTypeIDVoca, listDTOVocaDetailRubyText);
    }

    // 단어별 HTML 처리 최적화
    private WordProcessingResult processWordForHTML(String wordFromTextWithPOS, 
                                                  Map<String, String> mapWordUnique,
                                                  Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWordInfoList) {
        // 문자열 분할 최적화
        String[] arrWordWithPOS = wordFromTextWithPOS.split(Constants.POS_SEPERATOR_UNDERSCORE);
        String wordFromTextWithoutPOS = arrWordWithPOS[0];
        String strWordWithOutLowercase = wordFromTextWithoutPOS.toLowerCase();
        
        // POS 처리 최적화
        String wordFromTextWithPOSLowercase = buildWordWithPOSLowercase(arrWordWithPOS);
        
        // Map 검색 최적화 - getOrDefault 사용
        String htmlOfWordFromText = mapWordUnique.get(wordFromTextWithPOSLowercase);
        if (htmlOfWordFromText != null) {
            // 사전에 있는 단어이면 뜻과 발음을 달아준다.
            htmlOfWordFromText = replacestrWordLowercaseWithWordFromText(htmlOfWordFromText, strWordWithOutLowercase, wordFromTextWithoutPOS);
            
            DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailRubyText = mapUniqueWordInfoList.get(wordFromTextWithPOSLowercase);
            DTO_VOCA_TYPE_ID_VOCA dtoVocaPOS;
            
            if (dtoVocaDetailRubyText != null) {
                dtoVocaPOS = new DTO_VOCA_TYPE_ID_VOCA.Builder()
                        .VOCA_TYPE(dtoVocaDetailRubyText.getVOCA_TYPE())
                        .VOCA_ID(dtoVocaDetailRubyText.getVOCA_ID())
                        .VOCA(wordFromTextWithoutPOS)
                        .build();
            } else {
                dtoVocaPOS = new DTO_VOCA_TYPE_ID_VOCA.Builder().VOCA(wordFromTextWithoutPOS).build();
            }
            
            return new WordProcessingResult(htmlOfWordFromText, dtoVocaPOS, dtoVocaDetailRubyText);
        } else {
            // 사전에 없는 단어
            DTO_VOCA_TYPE_ID_VOCA dtoVocaPOS = new DTO_VOCA_TYPE_ID_VOCA.Builder().VOCA(wordFromTextWithoutPOS).build();
            return new WordProcessingResult(wordFromTextWithoutPOS, dtoVocaPOS, null);
        }
    }

    // POS와 함께 소문자 단어 생성 최적화
    private String buildWordWithPOSLowercase(String[] arrWordWithPOS) {
        if (arrWordWithPOS.length == 0) {
            return "";
        }
        
        StringBuilder result = new StringBuilder(arrWordWithPOS[0].toLowerCase());
        
        if (arrWordWithPOS.length == 1) {
            result.append(Constants.POS_SEPERATOR_UNDERSCORE);
        } else {
            for (int i = 1; i < arrWordWithPOS.length; i++) {
                result.append(Constants.POS_SEPERATOR_UNDERSCORE).append(arrWordWithPOS[i]);
            }
        }
        
        return result.toString();
    }

    // 결과를 담는 내부 클래스들
    private static class ProcessedLineResult {
        private final String htmlWithMeaning;
        private final List<DTO_VOCA_TYPE_ID_VOCA> listDtoVocaTypeIDVoca;
        private final List<DTO_VOCA_DETAIL_RUBY_TEXT> listDTOVocaDetailRubyText;

        public ProcessedLineResult(String htmlWithMeaning, List<DTO_VOCA_TYPE_ID_VOCA> listDtoVocaTypeIDVoca, 
                                 List<DTO_VOCA_DETAIL_RUBY_TEXT> listDTOVocaDetailRubyText) {
            this.htmlWithMeaning = htmlWithMeaning;
            this.listDtoVocaTypeIDVoca = listDtoVocaTypeIDVoca;
            this.listDTOVocaDetailRubyText = listDTOVocaDetailRubyText;
        }

        public String getHtmlWithMeaning() { return htmlWithMeaning; }
        public List<DTO_VOCA_TYPE_ID_VOCA> getListDtoVocaTypeIDVoca() { return listDtoVocaTypeIDVoca; }
        public List<DTO_VOCA_DETAIL_RUBY_TEXT> getListDTOVocaDetailRubyText() { return listDTOVocaDetailRubyText; }
    }

    private static class WordProcessingResult {
        private final String htmlContent;
        private final DTO_VOCA_TYPE_ID_VOCA dtoVocaTypeIDVoca;
        private final DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailRubyText;

        public WordProcessingResult(String htmlContent, DTO_VOCA_TYPE_ID_VOCA dtoVocaTypeIDVoca, 
                                  DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailRubyText) {
            this.htmlContent = htmlContent;
            this.dtoVocaTypeIDVoca = dtoVocaTypeIDVoca;
            this.dtoVocaDetailRubyText = dtoVocaDetailRubyText;
        }

        public String getHtmlContent() { return htmlContent; }
        public DTO_VOCA_TYPE_ID_VOCA getDtoVocaTypeIDVoca() { return dtoVocaTypeIDVoca; }
        public DTO_VOCA_DETAIL_RUBY_TEXT getDtoVocaDetailRubyText() { return dtoVocaDetailRubyText; }
    }
    protected String replacestrWordLowercaseWithWordFromText(String htmlOfWordFromText, String strWordLowercase, String wordFromText) {
        // 속성(data-wordfromtext)을 원래 단어(대소문자구분)으로 바꾸어준다.
        htmlOfWordFromText = htmlOfWordFromText.replace("data-wordfromtext=\"" + strWordLowercase + "\"", "data-wordfromtext=\"" + wordFromText + "\"");
        // 루비문자로 표시하는것은 다음방법으로 대소문자가 구분된 원래단어로 바꾸어준다.
        htmlOfWordFromText = htmlOfWordFromText.replace("<rb>" + strWordLowercase + "</rb>", "<rb>" + wordFromText + "</rb>");
        // 단어다음에 바로 "["가 이어지는 경우에는 다음방법으로 대소문자가 구분된 원래단어로 바꾸어준다.
        htmlOfWordFromText = htmlOfWordFromText.replace(strWordLowercase + "[", wordFromText + "[");
        // 단어다음에 바로 </a>가 있는 경우에는 다음방법으로 대소문자가 구분된 원래단어로 바꾸어준다.
        htmlOfWordFromText = htmlOfWordFromText.replace(">" + strWordLowercase + "</a>", ">" + wordFromText + "</a>");

//        logger.info("htmlOfWordFromText after : " + htmlOfWordFromText);
//        logger.info(htmlOfWordFromText);
        return htmlOfWordFromText;
    }
    //추출한 단어로부터 VOCA_ID와 Ruby Tag등을 넣어준다.
    private Map<String, Object> getUniqueWordsInDicWithHTMLTagRubyTextDTOCOMMON2(Map<String, WordMorpheme> mapMorphemeWithFrequency) {
        Map<String, Object> mapResult = new HashMap<String, Object>();
//		List<String> listUinqueWordNameLowcaseForJSP = new ArrayList<String>();
        Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWordsForJSP = new HashMap<>();
        Map<String, String> mapWordUniqueInDic = new HashMap<>();
        Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWordsInfo = new HashMap<String, DTO_VOCA_DETAIL_RUBY_TEXT>();
        Integer noOfWord = 1;
        try {
            Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapDTOVocaDetail = searchWordListInDicDTO2(mapMorphemeWithFrequency);
            Integer vocaID_NotExist = -1;
            for (String strWordWithPos : mapMorphemeWithFrequency.keySet()) {
                String strWord = strWordWithPos.split(Constants.POS_SEPERATOR_UNDERSCORE)[0].toLowerCase();
                WordMorpheme wordMorpheme = mapMorphemeWithFrequency.get(strWordWithPos);
                DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailRubyText = mapDTOVocaDetail.get(strWordWithPos);
                if (!mapWordUniqueInDic.containsKey(strWordWithPos)) {
                    String strResultHTMLTag = addInfoAtWordWithJsonWordRubyTextDTOCOMMON(wordMorpheme, dtoVocaDetailRubyText, noOfWord, vocaID_NotExist);
                    if (mapUniqueWordsForJSP.containsKey(strWordWithPos)) {
                        DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailRubyTextForJSP = mapUniqueWordsForJSP.get(strWordWithPos);
                        dtoVocaDetailRubyTextForJSP.increaseFrequency();
                        mapUniqueWordsForJSP.put(strWordWithPos, dtoVocaDetailRubyTextForJSP);
                    } else {
                        if ( (dtoVocaDetailRubyText != null) && (dtoVocaDetailRubyText.getVOCA_ID() != null) && (dtoVocaDetailRubyText.getVOCA_ID() > 0)) {
                            dtoVocaDetailRubyText.setSERIAL_ID_FOR_VOCAS(noOfWord);
                            dtoVocaDetailRubyText.setFREQUENCY(wordMorpheme.getFrequency());

                            mapUniqueWordsForJSP.put(strWordWithPos, dtoVocaDetailRubyText);

                            DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailTemp = new DTO_VOCA_DETAIL_RUBY_TEXT.Builder()
                                    .VOCA_ID(dtoVocaDetailRubyText.getVOCA_ID())
                                    .VOCA(dtoVocaDetailRubyText.getVOCA())
                                    .VOCA_DISPLAY(dtoVocaDetailRubyText.getVOCA_DISPLAY())
                                    .VOCA_TTS(dtoVocaDetailRubyText.getVOCA_TTS())
                                    .VOCAORI(dtoVocaDetailRubyText.getVOCAORI())
                                    .PRONOUNCE(dtoVocaDetailRubyText.getPRONOUNCE())
                                    .MEANING(dtoVocaDetailRubyText.getMEANING())
                                    .MEANING_TTS(dtoVocaDetailRubyText.getMEANING_TTS())
                                    .VOCA_KNOW(dtoVocaDetailRubyText.getVOCA_KNOW())
                                    .VOCA_KNOWPRONOUNCE(dtoVocaDetailRubyText.getVOCA_KNOWPRONOUNCE())
                                    .POS_VOCA(dtoVocaDetailRubyText.getPOS_VOCA())
                                    .build();
                            mapUniqueWordsInfo.put(strWordWithPos, dtoVocaDetailTemp);
                        } else {
                            DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailTemp = new DTO_VOCA_DETAIL_RUBY_TEXT.Builder()
                                    .SERIAL_ID_FOR_VOCAS(noOfWord)
                                    .VOCA_ID(vocaID_NotExist)
                                    .VOCA(wordMorpheme.getWord())
                                    .VOCA_DISPLAY(wordMorpheme.getWord())
                                    .VOCA_TTS(wordMorpheme.getWord())
                                    .VOCAORI(wordMorpheme.getWord())
                                    .PRONOUNCE(wordMorpheme.getPronounciation())
                                    .VOCA_KNOW(Constants.VOCA_KNOW.KNOWN)
                                    .VOCA_KNOWPRONOUNCE(Constants.VOCA_KNOW.KNOWN)
                                    .FREQUENCY(wordMorpheme.getFrequency())
                                    .POS_VOCA(wordMorpheme.getAllPOS())
                                    .build();
                            mapUniqueWordsInfo.put(strWordWithPos, dtoVocaDetailTemp);
                            mapUniqueWordsForJSP.put(strWordWithPos, dtoVocaDetailTemp);
                            vocaID_NotExist--;
                        }
                        mapWordUniqueInDic.put(strWordWithPos, strResultHTMLTag);
                    }
                    noOfWord++;
                }
            }
        } catch (Exception e) {
//            DLog.d("","fail to open mysql");
            e.printStackTrace();
        }
//		mapResult.put("getUniqueWordsInDicWithHTMLTag_listUinqueWordNameLowcaseForJSP", listUinqueWordNameLowcaseForJSP);
        mapResult.put("getUniqueWordsInDicWithHTMLTag_mapUniqueWordsForJSP", mapUniqueWordsForJSP);
        mapResult.put("getUniqueWordsInDicWithHTMLTag_mapWordUniqueInDic", mapWordUniqueInDic);
        mapResult.put("getUniqueWordsInDicWithHTMLTag_mapUniqueWordsInfo", mapUniqueWordsInfo);
        return mapResult;
    }

    //baseform을 가지고 단어를 찾는다.(뜻도, 단 POS는 word와 wordBaseForm은 다를수 있다.)
    public Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> searchWordListInDicDTO2(Map<String, WordMorpheme> mapUniqueWordsWithPOSAndWOrdMorpheme) {
        long startTime = System.currentTimeMillis();
        DLog.i("SUBTITLE_ANALYSIS", "   * 사전 검색 시작");
        DLog.i("SUBTITLE_ANALYSIS", "   * 검색할 단어 수: " + mapUniqueWordsWithPOSAndWOrdMorpheme.size() + "개");
        
        Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapJsonWord = new HashMap<String, DTO_VOCA_DETAIL_RUBY_TEXT>();
        if (mapUniqueWordsWithPOSAndWOrdMorpheme.size() == 0) {
            return mapJsonWord;
        }
        try {
            String fld_Meaning = getUserDispMeaningLangFldName(Constants.LANG_EN);
            String fld_Meaning_TTS = getUserDispMeaningLangTTSFldName(Constants.LANG_KO);

            List<DTO_VOCA_POS> listWordAndPos = new ArrayList<DTO_VOCA_POS>();
            // mapUniqueWordsWithPOSAndWOrdMorpheme를 돌면서 단어(영어+중국어 = 문장그대로, 일본어는 기본형)만 가져온다.
            // 뜻을 찾을때 사용한다. (일본어는 뜻이 기본형으로 들어 가 있다. 활용형으로 넣으면 사전이 너무 커진다.)
            List<String> listWordOrBaseForm = new ArrayList<String>();
            List<DTO_VOCA_TYPE_ID> listVocaTypeID = new ArrayList<DTO_VOCA_TYPE_ID>();
            // 단어를 문장그대로 가져온다. (북마크, 안다/모른다등은 문장에 나온 그대로 저장한다.)
            List<String> listFromText = new ArrayList<String>();
            //WorhMorpheme에서 각 리스트를을 뽑아온다.
            getListFromWordMorpheme(mapUniqueWordsWithPOSAndWOrdMorpheme, listWordAndPos, listWordOrBaseForm, listFromText);

            long dbQueryStartTime = System.currentTimeMillis();
            //단어 테이블에서 해당되는 단어를 가져온다. - 배치 쿼리로 최적화
            List<String> words = mapUniqueWordsWithPOSAndWOrdMorpheme.values().stream()
                    .map(WordMorpheme::getWord)
                    .collect(Collectors.toList());
            
            int wordBatchSizeForDBQuery = 500; // DB 쿼리용 단어 배치 크기
            final List<IVocaFullPlayTTSItem> result = getWordsInBatches(words, wordBatchSizeForDBQuery);
            long dbQueryEndTime = System.currentTimeMillis();
            long dbQueryTime = dbQueryEndTime - dbQueryStartTime;
            DLog.i("SUBTITLE_ANALYSIS", "   - DB 쿼리 완료: " + dbQueryTime + "ms");
            DLog.i("SUBTITLE_ANALYSIS", "   * DB에서 찾은 단어 수: " + result.size() + "개");
//            List<VO_DIC_COMMON> resultList = convertToVODicCommon(result);//new ArrayList<>();//getVocaFromPOSAndAlreadyParsedVocaID(listWordAndPos, dto.getLANG_MEANING_CODE());


            // (일본어는 사전에는 기본형밖에 없으므로 정보는 기본형으로 가져온다.)
            // Map<String, Object> mapWordBaseFormInDic = new HashMap<String, Object>();
            Map<String, List<IVocaFullPlayTTSItem>> mapWordBaseFormInDic = new HashMap<>();
            List<Integer> listVocaID = new ArrayList<>();
//            for (VO_DIC_COMMON vo : resultList) {
////				DTO_MEANING dtoMeaning = dalVocaService.getValueFromMethod(dto.getLANG_MEANING_CODE(), vo); //뜻은 모국어에 따라서 다르므로 따로 처리한다.
//                String Word = vo.getVOCA(); // (String) resultMap.get(Constants.FLD_WORD);
//                Integer id = vo.getID();//(Integer) resultMap.get(Constants.FLD_ID);
//
//                //단어 테이블에서 가져왔기 때문에 VOCA_TYPE이 NULL로 나와서 여기서 하드코딩으로 넣어준다.
//                listVocaTypeID.add(new DTO_VOCA_TYPE_ID.Builder()
//                        .VOCA_TYPE(Constants.VOCA_TYPE_WORD)
//                        .VOCA_ID(id)
//                        .build());
//
//                listVocaID.add(id);
//                if (!mapWordBaseFormInDic.containsKey(Word)) {
//                    List<VO_DIC_COMMON> listResultMap = new ArrayList<VO_DIC_COMMON>();
//                    listResultMap.add(vo);
//                    mapWordBaseFormInDic.put(Word, listResultMap);
//                } else {
//                    // 기본형이 동음이의어라서 word는 같지만 POS, 발음등이 다를수 있다.
//                    // Map<String, List<Object>> resultMapSave = (Map<String, List<Object>>)
//                    // mapWordBaseFormInDic.get(Word);
//                    // if (resultMapSave == resultMap) {
//                    // continue;
//                    // }
//                    List<VO_DIC_COMMON> listResultMap = (List<VO_DIC_COMMON>) mapWordBaseFormInDic.get(Word);
//                    listResultMap.add(vo);
//                    mapWordBaseFormInDic.put(Word, listResultMap);
//
////					String WordOri = vo.getVOCAORI();// (String) resultMap.get(Constants.FLD_WORDORI);
////					String Meaning =  (String) resultMap.get(fld_Meaning);
////					String strAllPOS = (String) resultMap.get(Constants.FLD_POSALL);
////					logger.info("Word : " + Word);
////					logger.info("WordOri : " + WordOri);
////					logger.info("Meaning : " + Meaning);
////					logger.info("strAllPOS : " + strAllPOS);
//                }
//            }
            long processStartTime = System.currentTimeMillis();
            // 스트림과 그룹핑으로 데이터 처리 최적화
            mapWordBaseFormInDic = result.stream()
                    .collect(Collectors.groupingBy(IVocaFullPlayTTSItem::getVIVoca));
            
            // listVocaTypeID와 listVocaID 생성도 스트림으로 최적화
            result.forEach(vo -> {
                listVocaTypeID.add(new DTO_VOCA_TYPE_ID.Builder()
                        .VOCA_TYPE(Constants.VOCA_TYPE_WORD)
                        .VOCA_ID(vo.getVIId())
                        .build());
                listVocaID.add(vo.getVIId());
            });
            long processEndTime = System.currentTimeMillis();
            long processTime = processEndTime - processStartTime;
            DLog.i("SUBTITLE_ANALYSIS", "   - 데이터 처리 완료: " + processTime + "ms");
//			Map<Integer, Integer> mapAmkiGradeList = getAmkiGradeListForUser(uid, studylangCodeInClass, listVocaID);

            // DB에는 북마크와 안다 모른다는 기본형이 아니라 본문에 나온그대로 저장한다.(활용형)
            // 일본어와 한국어는 본문에 나온 그대로가 아니고 기본형으로 저장을 한다.
//            Map<Integer, Object> mapKnowOfWordAndKnwoPronounceFromUserDic = getKnowOfWordAndKnwoPronounceFromUserDic(listWordOrBaseForm); //일본어는 기본형으로 해당되는 단어의 안다/모른다를 가져온다.
////
////
//            Map<DTO_VOCA_TYPE_ID, DTO_VOCA_TYPE_ID> mapBookmarkedWordList = getBookmarkedWordListDTO(listVocaTypeID); //일본어는 기본형으로 해당되는 단어의 북마크를 가져온다.

//            Map<Integer, DTO_TBL_DIC> mapDicTblInfoByWordList = getDicTblInfoByWordList(listWordOrBaseForm, Constants.FLD_PRONOUNCE); //일본어는 기본형으로 해당되는 단어의 북마크를 가져온다.

//			Map<Integer, Integer> mapAmkiGradeList = getAmkiGradeListForUserFromWord(uid, studylangCodeInClass, listVocaID);

            List<String> listWordOri = new ArrayList<>();
            // DB에서 관련정보를 가져왔으면 본문의 단어를 돌면서 DB의 정보를 넣어준다.
            for (String strWordWithPos : mapUniqueWordsWithPOSAndWOrdMorpheme.keySet()) {
                WordMorpheme wordMorpheme = mapUniqueWordsWithPOSAndWOrdMorpheme.get(strWordWithPos);
//                logger.info("wordMorpheme : " + wordMorpheme);
                String strWordOrBaseForm = useWordOrBaseFormToGetWordsFromDic(wordMorpheme);
                String strPOSofWOrdMorpheme = wordMorpheme.getAllPOS();
//				String strPOSofWordBaseFormMorpheme = wordMorpheme.getAllPOSWithBaseForm();
                String strPOSTouse = wordMorpheme.getAllPOS();//getPOSofWordOrBaseForm(wordMorpheme);



                // 현재 단어(영어는 문장내 단어 그대로,일본어는 기본형이 있으면 기본형으로)에 대한 사전DB에 대한 정보를 가져온다.
                List<IVocaFullPlayTTSItem> listResultMap = new ArrayList<>();
                if (mapWordBaseFormInDic.containsKey(strWordOrBaseForm)) {
                    listResultMap = mapWordBaseFormInDic.get(strWordOrBaseForm);
                }

                if (listResultMap.size() == 0) { // 사전에 없는 경우.. 숫자등... 3日등은 고쳐야 한다.
//                    logger.info(strWordOrBaseForm + " has no listResultMap : ");
                    continue;
                }

                // 서버 사전DB에는 같은 단어라도 POS나 발음등이 다를 수 있고, 또 일본어의 경우에는 동사등의 경우에는 기본형밖에 없다.
                // 그래서 서버의 같은 단어들을 돌면서 현재 문장내의 단어에 정보를 추가할 서버의 단어정보를 찾는다.
                IVocaFullPlayTTSItem resultMap = new DIC_WORD_MODEL();
                // 먼저 문장내의 단어정보(단어 + POS)와 동일한 서버 사전 정보가 있는지 찾는다.
                for (IVocaFullPlayTTSItem voTemp : listResultMap) {
                    String Word = voTemp.getVIVoca();// (String) resultMapTemp.get(Constants.FLD_WORD);
                    String strAllPOS = voTemp.getVIPosAll();// (String) resultMapTemp.get(Constants.FLD_POSALL);
                    // if (strAllPOS.equals("")) {
                    // strAllPOS = "_____";
                    // }
                    if (strWordOrBaseForm.equals(Word) && strPOSofWOrdMorpheme.equals(strAllPOS)) {
                        resultMap = voTemp;
                        break;
                    }
                }

                // 만약 먼저 문장내의 단어정보(단어 + POS)와 동일한 서버 사전 정보가 없으면, 문장은 활용형이고, 사전 DB는 기본형일것이므로 다시 찾는다.
                // 단어와 POS맨 마지막을 사전껄로 넣고 다시 찾아본다.
                if ((resultMap.getVIId() == null) || (resultMap.getVIId() == 0)) {
                    for (IVocaFullPlayTTSItem voTemp : listResultMap) {
                        String Word = voTemp.getVIVoca();// (String) resultMapTemp.get(Constants.FLD_WORD);
                        String strAllPOS = voTemp.getVIPosAll();// (String) resultMapTemp.get(Constants.FLD_POSALL);
                        String strAllPOSWithWordMMorphemeWordAndLastPOS = strAllPOS;
                        String pronounce = voTemp.getVIPronounce();
                        //이것이 있어서 .kita같은 활용형이 제대로 분석인 안되고, ki만 리턴한다.
//						if (!(pronounce.equals(wordMorpheme.getPronounciation())))
//							continue;

                        String[] arrPOS = strAllPOS.split(Constants.POS_SEPERATOR_UNDERSCORE);
                        if (arrPOS.length == 6) {
                            if (arrPOS[4].toString().equals("基本形")) {
                                continue;
                            }
                            String strLastPOSFromWordMorpheme = "";
                            if (strPOSofWOrdMorpheme.contains(Constants.POS_SEPERATOR_UNDERSCORE)) {
                                String[] arrPOSofWOrdMorpheme = strPOSofWOrdMorpheme
                                        .split(Constants.POS_SEPERATOR_UNDERSCORE);
                                strLastPOSFromWordMorpheme = arrPOSofWOrdMorpheme[arrPOSofWOrdMorpheme.length - 1];
                            }
                            strAllPOSWithWordMMorphemeWordAndLastPOS = strWordOrBaseForm
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[0].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[1].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[2].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[3].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[4].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + strLastPOSFromWordMorpheme;
                            if (strAllPOSWithWordMMorphemeWordAndLastPOS.equals(wordMorpheme.getWordBaseForm() + Constants.POS_SEPERATOR_UNDERSCORE + strPOSofWOrdMorpheme)) {
                                resultMap = voTemp;
                                break;
                            } else {
//                                logger.info("no match");
                            }
                        }
                    }
                }


                // 그래도 못 찾으면 일단 그냥 넘어간다.
                if ((resultMap.getVIId() == null) || (resultMap.getVIId() == 0)) {
//                    logger.error(strWordOrBaseForm + " has no listResultMap");
                    if (!mapJsonWord.containsKey(strWordWithPos)) {
                        mapJsonWord.put(strWordWithPos, new DTO_VOCA_DETAIL_RUBY_TEXT.Builder().build()); // jsonWord가 null이라도 넣는다. (사전에 없어도 HTML Tag는 만들어 준다.)
                    }
                    continue;
                }

                // 일본어의 경우에는 기본형에 대한 정보임을 명심하자...
                String Word = resultMap.getVIVoca();// (String) resultMap.get(Constants.FLD_WORD);
                String wordDisplay = resultMap.getVIVoca();// (String) resultMap.get(Constants.FLD_WORD_DISPLAY);
                String wordTTS = resultMap.getVIVocaTTS();// (String) resultMap.get(Constants.FLD_WORD_TTS);
                String WordOri = resultMap.getVIVoca();// (String) resultMap.get(Constants.FLD_WORDORI);
//				DTO_MEANING dtoMeaningInResultMap = dalVocaService.getValueFromMethod(dto.getLANG_MEANING_CODE(), resultMap); //뜻은 모국어에 따라서 다르므로 따로 처리한다.
                String meaning = resultMap.getVIMeaning(EnumLanguage.KOREAN);// (String) resultMap.get(fld_Meaning);
                String meaningEng = resultMap.getVIMeaningEng();// (String) resultMap.get(fld_Meaning);
                String meaningTTS = resultMap.getVIMeaningTts(EnumLanguage.KOREAN);// (String) resultMap.get(fld_Meaning_TTS);

                String strAllPOS = resultMap.getVIPosAll();// (String) resultMap.get(Constants.FLD_POSALL);

                String PronounceFromToken = wordMorpheme.getPronounciation();
                String PronounceFromDB = resultMap.getVIPronounce();// (String) resultMap.get(Constants.FLD_PRONOUNCE);

                // 언어별로 발음을 손질할필요가 있다..
                Integer usePronouceInTbl = Constants.PRONOUNCE_USE_IN_DB;// (Integer) resultMap.get(Constants.FLD_PRONOUNCE_USE);
                String Pronounce = getPronouceFromDBorToken(PronounceFromToken, usePronouceInTbl, PronounceFromDB);
                String PronounceBaseForm = getPronouceBaseForm(PronounceFromToken, wordDisplay, PronounceFromDB);

                // 뜻은 없으면 WordOri로 가서 가져온다.
                // 일본어의 歩은 POS도 같은데 발음이 다른게 있어서 selectOne에서 에러가 난다.(getFldValInDic)
                if ((meaning == null) || (meaning.equals(""))) {
//                    logger.info("getOriMeaning");
                    if (strAllPOS == "") {
                        meaning = getOriMeaning(WordOri, fld_Meaning);
                    } else {
                        meaning = getOriMeaningWithPOS(WordOri, fld_Meaning);
                    }
                }

//                Integer WordLevel = resultMap.getVOCA_LEVEL();// (Integer) resultMap.get(Constants.FLD_WORDLEVEL);

                int vocaKnow = resultMap.getVIVocaKnow();// Constants.VOCA_KNOW.NOTRATED;
                int vocaKnowPronounce = resultMap.getVIVocaKnowPronounce();// Constants.VOCA_KNOW.NOTRATED;

//				String wordOrWordWithConjugationToFindBookmarkAndKnow = getWordOrWordWithConjugationToFindBookmarkAndKnow(wordMorpheme);
//				if (mapKnowOfWordAndKnwoPronounceFromUserDic.containsKey(wordOrWordWithConjugationToFindBookmarkAndKnow + strPOSTouse)) {
                // 단어의 유일한 ID를 가져온다.
                Integer wordID = resultMap.getVIVocaId();//  (Integer) resultMap.get(Constants.FLD_ID);
                Integer wordOriID = resultMap.getVIVocaIdBase();// (Integer) resultMap.get(Constants.FLD_WORDORI_ID);

                //단어 테이블에서 가져왔기 때문에 VOCA_TYPE이 NULL로 나와서 여기서 하드코딩으로 넣어준다.
                DTO_VOCA_TYPE_ID dtoVocaTypeID = new DTO_VOCA_TYPE_ID.Builder()
                        .VOCA_TYPE(Constants.VOCA_TYPE_WORD)
                        .VOCA_ID(resultMap.getVIVocaId())
                        .build();

//                if (mapKnowOfWordAndKnwoPronounceFromUserDic.containsKey(wordID)) {
//                    Map<String, String> mapWordInUserDic = (Map<String, String>) mapKnowOfWordAndKnwoPronounceFromUserDic.get(wordID);
////					Know = Integer.parseInt(mapWordInUserDic.get(Constants.FLD_KNOW));
////					KnowPronounce = Integer.parseInt(mapWordInUserDic.get(Constants.FLD_KNOWPRONOUNCE));
//                    vocaKnow = Integer.parseInt(mapWordInUserDic.get(Constants.FLD_VOCA_KNOW));
//                    vocaKnowPronounce = Integer.parseInt(mapWordInUserDic.get(Constants.FLD_VOCA_KNOWPRONOUNCE));
//                }

                long endgetKnowOfWordFromUserDic = System.currentTimeMillis();
                Integer blnBookmarked = resultMap.getVIBookmark();// Constants.BOOKMARKED_NO;
//                if (mapBookmarkedWordList.containsKey(dtoVocaTypeID)) {
//                    blnBookmarked = Constants.BOOKMARKED_YES;
//                }

                String PronounceWordOri = PronounceFromDB;

                long endPronounceWordOri = System.currentTimeMillis();
                int frequencyOfWord = wordMorpheme.getFrequency();

                DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetail = new DTO_VOCA_DETAIL_RUBY_TEXT.Builder()
                        .VOCA_ID(wordID)
                        .VOCA(Word)
                        .VOCA_DISPLAY(wordDisplay)
                        .VOCA_TTS(wordTTS)
                        .BOOKMARK(blnBookmarked)
                        .VOCA_KNOW(vocaKnow)
                        .VOCA_KNOWPRONOUNCE(vocaKnowPronounce)
                        .MEANING(meaning)
                        .MEANING_ENG(meaningEng)
                        .MEANING_TTS(meaningTTS)
                        .POS_VOCA(wordMorpheme.getAllPOS())
                        .POS_VOCA_BASEFORM(strAllPOS)
                        .PRONOUNCE(Pronounce)
                        .PRONOUNCE_BASEFORM(PronounceBaseForm)
                        .FREQUENCY(frequencyOfWord)
                        .PRONOUNCE_From_Analyzer(wordMorpheme.getPronounciation())
                        .PRONOUNCE_VOCAORI(PronounceWordOri)
                        .VOCA_LEVEL(0)
                        .VOCAORI(WordOri)
                        .VOCAORI_ID(wordOriID)
                        .VOCALIST_KEY(wordMorpheme.getWord())
                        .VOCA_WithConjugation(wordMorpheme.getWordWithConjugation())
                        .EXISTIN_SERVERDIC(Constants.DATAINDIC_YES)
                        .build();

                mapJsonWord.put(strWordWithPos, dtoVocaDetail); // jsonWord가 null이라도 넣는다. (사전에 없어도 HTML Tag는 만들어 준다.)
            }
        } catch (Exception e) {
//            logger.error("fail to open mysql");
            e.printStackTrace();
            return null;
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        return mapJsonWord;
    }

    // 배치 쿼리로 단어 검색을 최적화하는 헬퍼 메서드
    private List<IVocaFullPlayTTSItem> getWordsInBatches(List<String> words, int batchSize) {
        List<IVocaFullPlayTTSItem> allResults = new ArrayList<>();
        for (int i = 0; i < words.size(); i += batchSize) {
            int end = Math.min(i + batchSize, words.size());
            List<String> batch = words.subList(i, end);
            String batchQuery = batch.stream().collect(Collectors.joining(", "));
            List<IVocaFullPlayTTSItem> batchResult = VocaListUtil.getAllWordListOfFromDB(batchQuery, dicDatabase);
            allResults.addAll(batchResult);
        }
        return allResults;
    }
    public static List<VO_DIC_COMMON> convertToVODicCommon(List<IVocaFullPlayTTSItem> items) {
        return items.stream().map(item -> {
            VO_DIC_COMMON voDicCommon = new VO_DIC_COMMON();
            voDicCommon.setID(item.getVIId());
            voDicCommon.setWORD(item.getVIVoca());
            voDicCommon.setVOCA(item.getVIVoca());
            voDicCommon.setPRONOUNCE(item.getVIPronounce());
            voDicCommon.setWORD_DISPLAY(item.getVIVocaTTS());
            voDicCommon.setMEANING1(item.getVIMeaning(EnumLanguage.ENGLISH)); // Assuming EnumLanguage.ENGLISH
            voDicCommon.setPOSALL(item.getVIPosAll());
            voDicCommon.setPERSON_AB(item.getPersonAB());
            voDicCommon.setWORD_SAME_ID(item.getVIId());
            voDicCommon.setVOCA_SAME_ID(item.getVIId());
            voDicCommon.setWORDORI(item.getVIVoca());
            voDicCommon.setVOCAORI(item.getVIVoca());
            voDicCommon.setWORDORI_ID(item.getVIVocaIdBase());
            voDicCommon.setVOCAORI_ID(item.getVIVocaIdBase());
            voDicCommon.setVOCA_TYPE_ORI(item.getVIVocaTypeBase());
            voDicCommon.setVOCA_ID_ORI(item.getVIVocaIdBase());
            voDicCommon.setWORDLEVEL(item.getVIVocaKnow());
            voDicCommon.setVOCA_LEVEL(item.getVIVocaKnowPronounce());
            voDicCommon.setWORD_DISPLAY(item.getVIVocaTTS());
            voDicCommon.setVOCA_DISPLAY(item.getVIVocaTTS());
            voDicCommon.setWORD_TTS(item.getVIMeaningTts(EnumLanguage.ENGLISH)); // Assuming EnumLanguage.ENGLISH
            voDicCommon.setVOCA_TTS(item.getVIMeaningTts(EnumLanguage.ENGLISH));
            voDicCommon.setMEANING1(item.getVIMeaning(EnumLanguage.ENGLISH)); // Assuming EnumLanguage.ENGLISH
            voDicCommon.setPRONOUNCE1_FIRST(item.getVIPronounce());
            voDicCommon.setPRONOUNCE1(item.getVIPronounce());
            voDicCommon.setSENTENCE_ID_LIST(item.getVISEARCH_HISTORY());
            voDicCommon.setPOSALL(item.getVIPosAll());
            voDicCommon.setPOS(item.getVIPosAll());
            voDicCommon.setPOS2(item.getVIPosAll());
            voDicCommon.setPOS3(item.getVIPosAll());
            voDicCommon.setPOS4(item.getVIPosAll());
            voDicCommon.setPOS5(item.getVIPosAll());
            voDicCommon.setPOS6(item.getVIPosAll());
            voDicCommon.setUID(item.getVIId());
            voDicCommon.setCREATOR_TYPE(item.getVIId());
            voDicCommon.setWORDORI_ID_LIST(item.getVISEARCH_HISTORY());
            voDicCommon.setVOCAORI_ID_LIST(item.getVISEARCH_HISTORY());
            voDicCommon.setWORD_ID_LIST(item.getVISEARCH_HISTORY());
            voDicCommon.setVOCA_ID_LIST(item.getVISEARCH_HISTORY());
            voDicCommon.setFAKER_CATEGORY_A(item.getVISEARCH_HISTORY());
            voDicCommon.setFAKER_CATEGORY_B(item.getVISEARCH_HISTORY());
            voDicCommon.setFAKER_CATEGORY_C(item.getVISEARCH_HISTORY());
            voDicCommon.setNAME_ITEM(item.getVIPronounce());
            voDicCommon.setNAME_ITEM_SUB_DESC(item.getVIPronounce());
            voDicCommon.setNAME_SHOP(item.getVIPronounce());
            voDicCommon.setPERSON_AB(item.getPersonAB());
            voDicCommon.setPRICE_FIXED(item.getVIPronounce());
            voDicCommon.setCATEGORY_ID(item.getVIId());
            voDicCommon.setDISP_ORDER(item.getVIId());
            voDicCommon.setDISP_ORDER_NAME_ITEM(item.getVIId());
            voDicCommon.setLANGUAGE_LEVEL_FROM(item.getVIId());
            voDicCommon.setLANGUAGE_LEVEL_TO(item.getVIId());
            voDicCommon.setMUST_INCLUDE(item.getVIId());
            voDicCommon.setUSED(item.getVIId());
            voDicCommon.setUSE_ROLE_PLAYING_MENU(item.getVIId());
            voDicCommon.setUSE_ROLE_PLAYING_RANDOM_VALUE(item.getVIId());
            voDicCommon.setUSE_SERVER_VOCABOOK_DATA(item.getVIId());
            voDicCommon.setUSE_SHOP_NAME_FOR_RANDOM_VALUE(item.getVIId());
            voDicCommon.setVOCABOOKS_ID(item.getVIId());
            voDicCommon.setVOCA_ID(item.getVIId());
            voDicCommon.setVOCA_TYPE(item.getVIId());

            return voDicCommon;
        }).collect(Collectors.toList());
    }
    protected Map<Integer, Object> getKnowOfWordAndKnwoPronounceFromUserDic(List<String> list) {
        Map<Integer, Object> mapResult = new HashMap<Integer, Object>();
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put(Constants.KEY_TBL_NAME, getTbl_USER_DIC(uid));
            map.put("wordArray", list);
            List<Map<String, Object>> resultList = new ArrayList<>();// sqlSession.selectList("DalReadTextMapper.getKnowOfWordAndKnwoPronounceFromUserDic", map);
            for (Map<String, Object> resultMap : resultList) {
                Integer wordID = (Integer) resultMap.get(Constants.FLD_WORD_ID);
                String word = (String) resultMap.get(Constants.FLD_WORD);
                Integer vocaKnow = (Integer) resultMap.get(Constants.FLD_VOCA_KNOW);
                Integer vocaKnowPronounce = (Integer) resultMap.get(Constants.FLD_VOCA_KNOWPRONOUNCE);
                Map<String, String> mapWordInUserDic = new HashMap<String, String>();
                mapWordInUserDic.put(Constants.KEY_VOCA_ID, wordID.toString());
                mapWordInUserDic.put(Constants.KEY_VOCA, word);
                mapWordInUserDic.put(Constants.KEY_VOCA_KNOW, vocaKnow.toString());
                mapWordInUserDic.put(Constants.KEY_VOCA_KNOWPRONOUNCE, vocaKnowPronounce.toString());
//				mapWordInUserDic.put(Constants.KEY_KNOW, know.toString());
//				mapWordInUserDic.put(Constants.KEY_KNOWPRONOUNCE, knowPronounce.toString());
                mapResult.put(wordID, mapWordInUserDic);
            }
        } catch (Exception e) {
//            logger.error("fail to open mysql");
            e.printStackTrace();
        }
        return mapResult;
    }
    protected String getPronouceFromDBorToken(String PronounceFromToken, Integer usePronouceInTbl, String PronounceFromDB) {
        String Pronounce = PronounceFromToken;
        if ((usePronouceInTbl == Constants.PRONOUNCE_USE_IN_DB) || (PronounceFromToken == null)
                || (PronounceFromToken.equals(""))) {
            // 형태소분석기에서 발음이 없으면 사전에서의 발음을 사용한다. 또는 강제로 사전의 발음을 사용할때도...
//            logger.info("Pronounce");
            Pronounce = PronounceFromDB;
        }
        return Pronounce;
    }
    protected String getPronouceBaseForm(String PronounceFromToken, String wordDisplay, String PronounceFromDB) {
        String Pronounce = PronounceFromDB;
        if ((PronounceFromDB == null) || (PronounceFromDB.equals(""))) {
//			if (isAllKatakana(wordDisplay) || isAllhirakana(wordDisplay)) {
//            logger.info("Pronounce");
            Pronounce = PronounceFromToken;
//			}
        }
        return Pronounce;
    }
    public Map<DTO_VOCA_TYPE_ID, DTO_VOCA_TYPE_ID> getBookmarkedWordListDTO(List<DTO_VOCA_TYPE_ID> listVocaTypeID) {
        Map<DTO_VOCA_TYPE_ID, DTO_VOCA_TYPE_ID> mapResult = new HashMap<DTO_VOCA_TYPE_ID, DTO_VOCA_TYPE_ID>();
        try {
            // VOCA의 Bookmark 여부를 가져온다. (Bookmark는 테이블 한개에 단어, 문장등을 같이 넣는다.
            List<VO_DIC_BOOKMARK> listBookmark = getBookmarkedVocaListByVocaIDDTO(listVocaTypeID);
            for (VO_DIC_BOOKMARK vo : listBookmark) {
                DTO_VOCA_TYPE_ID dtoVocaTypeID = new DTO_VOCA_TYPE_ID.Builder()
                        .VOCA_TYPE(vo.getVOCA_TYPE())
                        .VOCA_ID(vo.getVOCA_ID())
                        .build();
                mapResult.put(dtoVocaTypeID, dtoVocaTypeID);
            }
        } catch (Exception e) {
//            logger.error("fail to open mysql");
            e.printStackTrace();
        }
        return mapResult;
    }
//    public Map<Integer, DTO_TBL_DIC> getDicTblInfoByWordList(List<String> list, String fldName) {
//        Map<Integer, DTO_TBL_DIC> mapResult = new HashMap<Integer, DTO_TBL_DIC>();
//        try {
//            HashMap<String, Object> map = new HashMap<String, Object>();
////            map.put(Constants.KEY_TBL_NAME, getTbl_DIC_BOOKMARK_OLD());
//            map.put("wordArray", list);
//            List<DTO_TBL_DIC> resultList = new ArrayList<>();// sqlSession.selectList("DalReadTextMapper.getDicTblInfoByWordList", map);
//            for (DTO_TBL_DIC resultMap : resultList) {
//                Integer wordID = resultMap.getID();
//                mapResult.put(wordID, resultMap);
//            }
//        } catch (Exception e) {
////            logger.error("fail to open mysql");
//            e.printStackTrace();
//        }
//        return mapResult;
//    }
    public List<VO_DIC_BOOKMARK> getBookmarkedVocaListByVocaIDDTO(List<DTO_VOCA_TYPE_ID> listVocaTypeID) {
        List<VO_DIC_BOOKMARK> resultList = new ArrayList<VO_DIC_BOOKMARK>();
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(Constants.KEY_TBL_NAME, getTbl_DIC_BOOKMARK());
            map.put("list", listVocaTypeID);
            resultList = new ArrayList<>();// sqlSession.selectList("voMapper.getBookmarkedVocaListByVocaID", map);

        } catch (Exception e) {
//            logger.error("fail to open mysql");
            e.printStackTrace();
        }
        return resultList;
    }
    protected String addInfoAtWordWithJsonWordRubyTextDTOCOMMON(WordMorpheme token, DTO_VOCA_DETAIL_RUBY_TEXT dtoVoca, Integer noOfWord,
                                                                Integer vocaID_NotExist) {
        String strResult = "";
        String strWord = token.getWord(); // 이미 strWord는 lowercase로 들어가 있다.
        if ( (dtoVoca != null) && (dtoVoca.getVOCA_ID() != null) && (dtoVoca.getVOCA_ID() > 0) ){
            String Meaning = dtoVoca.getMEANING();// dtoVocaDetail.getString(Constants.KEY_MEANING);
            String Meaning_TTS = dtoVoca.getMEANING_TTS();
            String strWordWithConjugation = dtoVoca.getVOCA_WithConjugation();// .getString(Constants.KEY_WORD_WithConjugation);
            if ((strWordWithConjugation != null) && (strWordWithConjugation.length() > 0)
                    && (!strWord.equals(strWordWithConjugation))) {
                // 동사변형이 있고 원형과 다르면 strWord를 활용형으로 넣어준다.
                strWord = strWordWithConjugation;
            }
            Map<String, String> mapMeaningAndPronounceOfWord = addMeaningAndPronounceToWordRubyTextDTO(dtoVoca, strWord, noOfWord);
            String strWordWithMeaning = mapMeaningAndPronounceOfWord.get(Constants.KEY_WORD_WordWitMeaning);
            //가타가나이면 발음을 보여주지 않는다.
            String Pronounce = dtoVoca.getPRONOUNCE();


            //스트링을 class로 변환
            ObjectMapper mapper = new ObjectMapper();
            DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDeepCopy = new DTO_VOCA_DETAIL_RUBY_TEXT.Builder().build();
            try {
                dtoVocaDeepCopy = mapper.convertValue(dtoVoca, DTO_VOCA_DETAIL_RUBY_TEXT.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            dtoVocaDeepCopy.setVOCA_TYPE(dtoVoca.getVOCA_TYPE());
//            dtoVocaDeepCopy.setVOCA_ID(dtoVoca.getVOCA_ID());
            dtoVocaDeepCopy.setPRONOUNCE(Pronounce);
            dtoVocaDeepCopy.setVOCA_WITH_MEANING(strWordWithMeaning);

            strResult = makeRubyTag(dtoVocaDeepCopy, noOfWord);
        } else {
            if (isRightChar(strWord)) {
                DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDeepCopy = new DTO_VOCA_DETAIL_RUBY_TEXT.Builder()
                        .VOCA(strWord)
                        .VOCA_ID(vocaID_NotExist)
                        .VOCAORI_ID(vocaID_NotExist)
                        .VOCAORI(strWord)
                        .VOCA_KNOW(Constants.VOCA_KNOW.KNOWN)
                        .VOCA_KNOWPRONOUNCE(Constants.VOCA_KNOW.KNOWN)
                        .VOCA_WITH_MEANING("<ruby><rb>" + strWord + "</rb><rt></rt></ruby>")
                        .build();

                strResult = makeRubyTag(dtoVocaDeepCopy, noOfWord);
            }
        }
        return strResult;
    }
    public String makeRubyTag(DTO_VOCA_DETAIL_RUBY_TEXT dtoVoca, Integer noOfWord) {
        String strResult = "<span VOCA_TYPE=" + dtoVoca.getVOCA_TYPE() + " VOCA_ID=" + dtoVoca.getVOCA_ID() + ">" + dtoVoca.getVOCA_WITH_MEANING() + "</span>";
        return strResult;
    }
    //단어의 뜻과 발음에 대해서 Ruby Tag를 넣어준다.
    protected Map<String, String> addMeaningAndPronounceToWordRubyTextDTO(DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailRubyText, String strWord, Integer noOfWord) {
        Map<String, String> mapMeaningAndPronounceOfWord = new HashMap<String, String>();

        dtoVocaDetailRubyText.setPRONOUNCE_FOR_HURIGANA(dtoVocaDetailRubyText.getPRONOUNCE());
        dtoVocaDetailRubyText.setTITLE_OF_TOOLTIP(dtoVocaDetailRubyText.getMEANING());
        List<String> pronounceKanjiOnConjugation = new ArrayList<String>();

        String strWordWithMeaning = addMeaningAndPronounceOnWordAsRubyText(dtoVocaDetailRubyText, strWord, pronounceKanjiOnConjugation);
        mapMeaningAndPronounceOfWord.put(Constants.KEY_WORD_WordWitMeaning, strWordWithMeaning);
        return mapMeaningAndPronounceOfWord;
    }
    protected String addMeaningAndPronounceOnWordAsRubyText(DTO_VOCA_DETAIL_RUBY_TEXT dtoVocaDetailRubyText,
                                                            String strWord, List<String> pronounceKanjiOnConjugation) {
        return "<ruby><rb>" + strWord + "</rb><rt>" + dtoVocaDetailRubyText.getMEANING() + "</rt></ruby>";
    }

    private void getListFromWordMorpheme(Map<String, WordMorpheme> mapUniqueWordsWithPOSAndWOrdMorpheme,
                                         List<DTO_VOCA_POS> listWordAndPos, List<String> listWordOrBaseForm, List<String> listFromText) {
        for (String strWordWithPos : mapUniqueWordsWithPOSAndWOrdMorpheme.keySet()) {

            // strWordWithPos.split(Constants.POS_SEPERATOR_UNDERSCORE)[0].toLowerCase();
            WordMorpheme wordMorpheme = (WordMorpheme) mapUniqueWordsWithPOSAndWOrdMorpheme.get(strWordWithPos);
//            DLog.d("","wordMorpheme : " + wordMorpheme);
            listWordOrBaseForm.add(useWordOrBaseFormToGetWordsFromDic(wordMorpheme));
            listFromText.add(getWordOrWordWithConjugationToFindBookmarkAndKnow(wordMorpheme)); //TODO : useWordOrBaseFormToGetWordsFromDic와 같은걸 가져오네?
            listWordAndPos.add(getWordAndPOSFromMorphemeDTO(wordMorpheme)); //일본어의 경우에는 기본형이 아니면 기본형의 POS와 VOCA를 가져온다.
        }
    }
    protected String useWordOrBaseFormToGetWordsFromDic(WordMorpheme token) {
        return token.getWord();
    }
    protected String getWordOrWordWithConjugationToFindBookmarkAndKnow(WordMorpheme wordMorpheme) {
        return wordMorpheme.getWord();
    }
    protected DTO_VOCA_POS getWordAndPOSFromMorphemeDTO(WordMorpheme wordMorpheme) {
        return new DTO_VOCA_POS.Builder()
                .VOCA(wordMorpheme.getWord())
                .POSALL(wordMorpheme.getAllPOS())
                .build();
    }
    protected String getOriMeaning(String WordOriParam, String fld_Meaning) {

        return getMeaning(WordOriParam, fld_Meaning, "");
    }

    protected String getOriMeaningWithPOS(String WordOriParam, String fld_Meaning) {
        String strPOS = getPOSFromWord(WordOriParam);
        return getMeaning(WordOriParam, fld_Meaning, strPOS);
    }

    protected String getMeaning(String Word, String fld_Meaning, String strPOS) {
        return getFldValInDic(Word, fld_Meaning, strPOS);
    }

    //이건 두개인 경우(일본어)는 공백이 리턴된다.
    protected String getFldValInDic(String Word, String fldName, String strPOS) {
        String strResult = "";
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(Constants.KEY_TBL_NAME, getTbl_DIC());
            map.put("word", Word.toLowerCase());
            map.put("fldName", fldName);
            getMapPutFromPOS(strPOS, map);
            List<String> resultList = new ArrayList<>();// sqlSession.selectList("DalReadTextMapper.getFldValWithPOSInDic", map);
            for(String fldValue : resultList) {
                if (fldValue == null) {
                    strResult = "";
                } else {
                    strResult = fldValue;
                }
                break;
            }
        } catch (Exception e) {
//            logger.error("fail to open mysql");
            e.printStackTrace();
        }
        return strResult;
    }
    private void getMapPutFromPOS(String strPOS, HashMap<String, Object> map) {
        boolean isRightPOS = false;
        if (strPOS != null) {
            if (strPOS.contains(Constants.POS_SEPERATOR_UNDERSCORE)) {
                String[] arrPOS = strPOS.split(Constants.POS_SEPERATOR_UNDERSCORE);
                if (arrPOS.length == 6) {
                    isRightPOS = true;
                    map.put("POSALL",
                            arrPOS[0].toString() + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[1].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[2].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[3].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[4].toString()
                                    + Constants.POS_SEPERATOR_UNDERSCORE + arrPOS[5].toString());
                    // map.put("POS", arrPOS[0].toString());
                    // map.put("POS2", arrPOS[1].toString());
                    // map.put("POS3", arrPOS[2].toString());
                    // map.put("POS4", arrPOS[3].toString());
                    // map.put("POS5", arrPOS[4].toString());
                    // map.put("POS6", arrPOS[5].toString());
                }
            }
        }

        if (isRightPOS == false) {
            map.put("POSALL", "");
            // map.put("POS", "");
            // map.put("POS2", "");
            // map.put("POS3", "");
            // map.put("POS4", "");
            // map.put("POS5", "");
            // map.put("POS6", "");

        }
    }
    protected String getPOSFromWord(String Word) {
        String strResult = "";
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(Constants.KEY_TBL_NAME, getTbl_DIC());
            map.put("word", Word.toLowerCase());

            List<Map<String, Object>> resultList = new ArrayList<>();// sqlSession.selectList("DalReadTextMapper.getPOSFromWord", map);
            for (Map<String, Object> resultMap : resultList) {
                strResult = (String) resultMap.get(Constants.FLD_POSALL);
                strResult = strResult.trim();
                if (strResult == "") {
                    continue;
                }
                break;
            }
        } catch (Exception e) {
//            logger.error("fail to open mysql");
            e.printStackTrace();
        }
        return strResult;
    }
//    private void addAlreadyParsedVocaToExractedWords(DTO_RUBY_ALL_WORDS mapExtractedWords, List<String> LIST_INPUT_TEXT) {
//        //이미 분석된게 있으면 그걸 받아와서 그대로 쓸려고 한다.
//        if (getAlreadyNLPParsedList(LIST_INPUT_TEXT) == false)
//            return;
//
////        Map<String, List<DTO_VOCA_POS>> mapSubtitleCorrectFormat = dto.getMAP_VOCA_ALREADY_NLP_PARSED();
////        List<Integer> listVocaIDAlreadyParsed = new ArrayList<Integer>();
////        for (String inputText : dto.getLIST_INPUT_TEXT()) {
////            if (mapSubtitleCorrectFormat.containsKey(inputText)) {
////                List<DTO_VOCA_POS> listVocaPos = mapSubtitleCorrectFormat.get(inputText);
////                listVocaIDAlreadyParsed.addAll(listVocaPos.stream().map(DTO_VOCA_POS::getVOCA_ID)
////                        .filter(id -> id > 0).collect(Collectors.toList()));
////            }
////        }
//
////        if (listVocaIDAlreadyParsed.size() == 0)
////            return;
//
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put(Constants.KEY_TBL_NAME, getTbl_DIC());
////        map.put("listVocaID", listVocaIDAlreadyParsed);
//        addFldMeaningIntoMap(map, Constants.LANGCODE_KO);
//        List<VO_DIC_COMMON> resultListAlreadyParsed = new ArrayList<>(); //sqlSession.selectList("voMapper.getVocaInfoByVocaIDList", map);
//
//        if (resultListAlreadyParsed.size() == 0)
//            return;
//
//        Map<String, WordMorpheme> mapWordMorphemeWithFrequency = mapExtractedWords.getMAP_WORD_MORPHEME_WITH_FREQUENCY();
//        for(VO_DIC_COMMON vo : resultListAlreadyParsed) {
//            String strLowcasewordWithPOS_For_Map = vo.getVOCA() + Constants.POS_SEPERATOR_UNDERSCORE;
//            if (mapWordMorphemeWithFrequency.containsKey(strLowcasewordWithPOS_For_Map)) {
//                //이미 파싱해놓은 단어가 새로 분석한 단어에 있으면 빈도수를 추가해줘야 한다.
//            } else {
//                WordMorpheme wordMorpheme = new WordMorpheme(vo.getVOCA(), vo.getPRONOUNCE(), vo.getVOCA(), vo.getPOSALL(), vo.getPOS());
//                wordMorpheme.setPartOfSpeechLevel2Ori(vo.getPOS2());
//                wordMorpheme.setPartOfSpeechLevel3Ori(vo.getPOS3());
//                wordMorpheme.setPartOfSpeechLevel4Ori(vo.getPOS4());
//                wordMorpheme.setPartOfSpeechLevel5Ori(vo.getPOS5());
//                wordMorpheme.setPartOfSpeechLevel6Ori(vo.getPOS6());
//                wordMorpheme.setWordWithConjugation(vo.getVOCA());
//                mapWordMorphemeWithFrequency.put(strLowcasewordWithPOS_For_Map, wordMorpheme);
//            }
//        }
//    }
    //이미 분석된게 있으면 그걸 받아와서 그대로 쓴다.
    protected boolean getAlreadyNLPParsedList(List<String> LIST_INPUT_TEXT) {
        List<String> listInputTextWithoutEmptyLine = LIST_INPUT_TEXT.stream().filter(e -> (!e.equals(""))).collect(Collectors.toList());
        if (listInputTextWithoutEmptyLine.size() == 0)
            return false;

        try {

            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(Constants.KEY_TBL_NAME, getTbl_NLP_PARSED());
            map.put("studylangCode", Constants.LANGCODE_EN);
            map.put("listInputText", listInputTextWithoutEmptyLine);
            List<VO_NLP_PARSED_COMMON> resultList = new ArrayList<>(); //sqlSession.selectList("voMapper.getAlreadyNLPParsedList", map);
//            Map<String, List<DTO_VOCA_POS>> mapVocaAlreadyNlPParsed = new HashMap<>();
//            for (VO_NLP_PARSED_COMMON vo : resultList) {
//                try {
////						List<DTO_VOCA_POS> myObjects = Arrays.asList(mapper.readValue(vo.getVOCA_NLP_PARSED(), DTO_VOCA_POS[].class)); //이것도 가능함.
//                    //입력할때는 부모 클래스인 DTO_VOCA_TYPE_ID_VOCA만 넣지만, 읽을때는 나중에 POS를 추가하기 위해서 DTO_VOCA_POS 타입으로 읽어들인다.
//                    List<DTO_VOCA_POS> dtoVocaAlreadyNlPParsed = mapper.readValue(vo.getVOCA_NLP_PARSED(), new TypeReference<List<DTO_VOCA_POS>>(){});
//                    mapVocaAlreadyNlPParsed.put(vo.getVOCA(), dtoVocaAlreadyNlPParsed);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//            dto.setMAP_VOCA_ALREADY_NLP_PARSED(mapVocaAlreadyNlPParsed);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    protected String getTbl_NLP_PARSED() {
        return Constants.TBL_NLP_PARSED_ENG;
    }
    private DTO_SUBTITLE_PARSED appendSubtitle2(File FILE_input2, AbstractTranslateFileService translateFile2, DTO_SUBTITLE_PARSED dtoSubtitleParsed, int subtitleLang2) {
        if (FILE_input2 == null)
            return dtoSubtitleParsed;

        if (translateFile2 instanceof AbstractMOVIE_SubtitleService) {
            cleanInputFileDTO(FILE_input2);
            DTO_SUBTITLE_PARSED dtoSubtitleParsed2 = ((AbstractMOVIE_SubtitleService)translateFile2).getDialogueInSubtitleDTO(FILE_input2, subtitleLang2);
            return combineSubtitles(dtoSubtitleParsed, dtoSubtitleParsed2);
        }

        return dtoSubtitleParsed;
    }

    private DTO_SUBTITLE_PARSED combineSubtitles(DTO_SUBTITLE_PARSED dtoSubtitleParsed, DTO_SUBTITLE_PARSED dtoSubtitleParsed2) {
        DTO_SUBTITLE_PARSED dtoResult = new DTO_SUBTITLE_PARSED.Builder().build();

        dtoResult.setHAS_STUDY_LANG_DIALOGUE(dtoSubtitleParsed.isHAS_STUDY_LANG_DIALOGUE());
        dtoResult.setNEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(true);

        //자막을 합친후에 시작시간으로 다시 정렬해준다. (아래 listDialogueStudyLang는 쓰는지 확인후 필요하면 같이 정렬 필요)
        List<DTO_DIALOGUE> listDtoDialogue = dtoSubtitleParsed.getLIST_DIALOGUE_INFO();
        listDtoDialogue.addAll(dtoSubtitleParsed2.getLIST_DIALOGUE_INFO());
        Collections.sort(listDtoDialogue, Comparator.comparingInt(DTO_DIALOGUE::getSTART_TIME));
        dtoResult.setLIST_DIALOGUE_INFO(listDtoDialogue);



        List<String> listDialogueStudyLang = dtoSubtitleParsed.getLIST_DIALOGUE_STUDY_LANG();
        listDialogueStudyLang.addAll(dtoSubtitleParsed2.getLIST_DIALOGUE_STUDY_LANG());
        dtoResult.setLIST_DIALOGUE_STUDY_LANG(listDialogueStudyLang);

        Map<String, DTO_DIALOGUE> mapDialogueInfo = dtoSubtitleParsed.getMAP_DIALOGUE_INFO();
        mapDialogueInfo.putAll(dtoSubtitleParsed2.getMAP_DIALOGUE_INFO());
        dtoResult.setMAP_DIALOGUE_INFO(mapDialogueInfo);

        Map<String, DTO_DIALOGUE> mapDialogueInfoByVocaTypeIdKey = dtoSubtitleParsed.getMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY();
        mapDialogueInfoByVocaTypeIdKey.putAll(dtoSubtitleParsed2.getMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY());
        dtoResult.setMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY(mapDialogueInfoByVocaTypeIdKey);

        return dtoResult;
    }

    protected DTO_SUBTITLE_PARSED syncSubtitlesDTO(DTO_SUBTITLE_PARSED dtoSubtitleParsed) {
        List<DTO_DIALOGUE> listDtoDialogAll = dtoSubtitleParsed.getLIST_DIALOGUE_INFO();// (List<DTO_Subtitle>)mapSubtitle.get(Constants.KEY_SUBTITLE_ALL);
        if ((listDtoDialogAll == null) || (listDtoDialogAll.size() == 0)) {
            return dtoSubtitleParsed;
        }

        Integer allowDiffStartTime = 10000;
        Integer allowDiffEndTime = 10000;
        Integer indexMotherTongueDialogUpdated = 0;
        for(Integer index = 0; index < listDtoDialogAll.size(); index++) {
            DTO_DIALOGUE dtoVoca = listDtoDialogAll.get(index);
            String voca = listDtoDialogAll.get(index).getVOCA();
            String meaningTemp = listDtoDialogAll.get(index).getMEANING();
//    		System.out.println("\n\nindex : " + index);
//    		System.out.println(dtoVoca);
//    		System.out.println("voca : " + voca);
//			System.out.println("meaningTemp : " + meaningTemp);
            if (voca.trim().equals("")) {
                continue;
            }
            if (!(meaningTemp.equals(""))) {
                continue;
            }
            Integer studyLangDialogStartTime = listDtoDialogAll.get(index).getSTART_TIME_ORIGINAL();
            Integer studyLangDialogEndTime = listDtoDialogAll.get(index).getEND_TIME_ORIGINAL();

            for(Integer indexMotherTongueDialog = indexMotherTongueDialogUpdated; indexMotherTongueDialog < listDtoDialogAll.size(); indexMotherTongueDialog++) {
                DTO_DIALOGUE dtoMeaning = listDtoDialogAll.get(indexMotherTongueDialog);
                String voca2 = listDtoDialogAll.get(indexMotherTongueDialog).getVOCA();
                String meaning = listDtoDialogAll.get(indexMotherTongueDialog).getMEANING();
//    			System.out.println("indexMotherTongueDialog : " + indexMotherTongueDialog);
//    			System.out.println(dtoMeaning);
//    			System.out.println("voca2 : " + voca2);
//    			System.out.println("meaning : " + meaning);
                Integer motherTongueDialogStartTime = listDtoDialogAll.get(indexMotherTongueDialog).getSTART_TIME_ORIGINAL();
                Integer motherTongueDialogEndTime = listDtoDialogAll.get(indexMotherTongueDialog).getEND_TIME_ORIGINAL();

                Integer startTimeDiff = studyLangDialogStartTime - motherTongueDialogStartTime;
                Integer endTimeDiff = studyLangDialogEndTime - motherTongueDialogEndTime;
                //시간이 같으면 싱크를 안 맞춘다.
                if ((startTimeDiff == 0) && (endTimeDiff == 0)) {
                    //같은 라인의 자막이 아니면...
                    if (!(index.equals(indexMotherTongueDialog))) {
                        //자막 2개를 지원할려고 하니 시간이 같은 경우가 나온다...이때는 뜻을 업데이트 해준다.
                        listDtoDialogAll.get(index).setMEANING(meaning);
                        listDtoDialogAll.get(indexMotherTongueDialog).setINSERT_IN_SQLITE(Constants.IS_NO);
                        //
                        indexMotherTongueDialogUpdated = index > indexMotherTongueDialog ? index+1 : indexMotherTongueDialog + 1;
                        break;
                    }
                }
                //모국어 자막이 학습어 자막보다 이전에 나오는건 안한다. (중첩되는것만 대상으로 할려고)
                boolean blnMotherTongueEndTimeIsLessThanStudyLangStartTime = motherTongueDialogEndTime < studyLangDialogStartTime;
                if (blnMotherTongueEndTimeIsLessThanStudyLangStartTime) {
                    continue;
                }
                if (meaning.trim().equals(""))
                    continue;
                if (!(voca2.trim().equals(""))) {
                    continue;
                }

                //모국어 자막이 학습어자막보다 뒤에 있으면 빠져나간다.
                boolean blnMotherTongueStartTimeIsGreatThanStudyLangEndTime = motherTongueDialogStartTime > studyLangDialogEndTime;
                if (blnMotherTongueStartTimeIsGreatThanStudyLangEndTime) {
                    indexMotherTongueDialogUpdated = index > indexMotherTongueDialog ? index : indexMotherTongueDialog;
                    break;
                }

                //모국어 자막의 시간이 학습어 자막의 시간보다 2초내에 차이가 날때는, 모국어 자막의 시간을 쓰지 않고, 학습어 자막의 시간을 사용한다.
                if ( ((Math.abs(startTimeDiff) <= allowDiffStartTime))  && ((Math.abs(endTimeDiff) <= allowDiffEndTime)) ) {
                    listDtoDialogAll.get(index).setMEANING(meaning);
                    listDtoDialogAll.get(indexMotherTongueDialog).setINSERT_IN_SQLITE(Constants.IS_NO);
                    indexMotherTongueDialogUpdated = index > indexMotherTongueDialog ? index+1 : indexMotherTongueDialog + 1;
                    break;
                }


            }
        }
//    	System.out.println("listDtoDialogAll.size() : " + listDtoDialogAll.size());
        List<DTO_DIALOGUE> listDialogueInfo = listDtoDialogAll.stream().filter(e->e.getINSERT_IN_SQLITE() == Constants.IS_YES).collect(Collectors.toList());
//		System.out.println("listDialogueInfo.size() : " + listDialogueInfo.size());
        Collections.sort(listDialogueInfo, Comparator.comparingInt(DTO_DIALOGUE::getSTART_TIME_ORIGINAL));

        List<String> listDialogueStudyLang = new ArrayList<String>();
        List<String> listDialogueStudyLangWithoutPunctuation = new ArrayList<String>();
        Map<String, DTO_DIALOGUE> mapDialogueInfo = new HashMap<String, DTO_DIALOGUE>();
//		Map<String, DTO_DIALOGUE> mapDialogueWithoutPunctInfo = new HashMap<String, DTO_DIALOGUE>();

        int id = 1;
        for (DTO_DIALOGUE dtoDialogue : listDialogueInfo) {
            dtoDialogue.setID(id++);
            String dialogueWithoutHtmlTagStudyLang = dtoDialogue.getVOCA();
            String dialogueWithoutHtmlTagStudyLangWithoutPunct = removePunctForSubtitleAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);


            listDialogueStudyLang.add(dialogueWithoutHtmlTagStudyLang);
            listDialogueStudyLangWithoutPunctuation.add(dialogueWithoutHtmlTagStudyLangWithoutPunct);
            if (!(dialogueWithoutHtmlTagStudyLang.equals(""))) {
                mapDialogueInfo.put(dialogueWithoutHtmlTagStudyLangWithoutPunct, dtoDialogue);
//				mapDialogueWithoutPunctInfo.put(dialogueWithoutHtmlTagStudyLangWithoutPunct, dtoDialogue);
            }
        }


        return new DTO_SUBTITLE_PARSED.Builder()
                .HAS_STUDY_LANG_DIALOGUE(true)
                .NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(true)
                .LIST_DIALOGUE_INFO(listDialogueInfo)
                .LIST_DIALOGUE_STUDY_LANG(listDialogueStudyLang)
                .LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION(listDialogueStudyLangWithoutPunctuation)
                .MAP_DIALOGUE_INFO(mapDialogueInfo)
//				.MAP_DIALOGUE_WITHOUT_PUNCT_INFO(mapDialogueWithoutPunctInfo)
                .build();

    }

    protected boolean cleanInputFileDTO(File FILE_input) {
        return true;
    }
    public DTO_SUBTITLE_PARSED getDialogueInSubtitleDTO(File file, int subtitleLang) {
        return new DTO_SUBTITLE_PARSED.Builder().build();
    }


    protected String getStudyLang(){
        return Constants.LANG_EN;
    }

    protected Integer guessSubtitleFileLang(Integer cntAllDialogue, Integer cntDialogueStudyLang, Integer cntDialogueIsNotStudyLang, Integer langStudyCode, Integer langMeaningCode) {
        Integer intSubtitleFileLang = langStudyCode;
        double ratioSubtitleStudyLang = (double) cntDialogueStudyLang / cntAllDialogue;
        double ratioSubtitleMotherTongue = (double) cntDialogueIsNotStudyLang / cntAllDialogue;
        double properRatioSubtitleLang = 0.1f;

        if (ratioSubtitleStudyLang > properRatioSubtitleLang) {
            intSubtitleFileLang = langStudyCode;
        } else {
            intSubtitleFileLang = langMeaningCode;
        }

        return intSubtitleFileLang;
    }

    protected String cleanUpText(String strText) {
        strText = strText.replaceAll("�", "'"); //0xFFFD는 Replacement문자로 문자를 못찾았을때 쓰는데, Let me in의 자막이 어포스트로피가 이걸로 표시되어서 여기서 강제로 치환해준다.
        return strText.replaceAll("\n+", "\n");
    }
    public String removePunctForSubtitleAndMakeLowercase(String strOne) {
        //TODO : 아래구문이 있으면 로컬에서는 돌아가는데 AWS에서는 안돌아간다. 아마 AWS의 자바 버전이 낮아서 그런거 같다.
//		Set<UnicodeBlock> unicodeBlocksPunctuation = new HashSet<UnicodeBlock>() {
//			{
//				add(UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
//				add(UnicodeBlock.GENERAL_PUNCTUATION);
//				add(UnicodeBlock.SUPPLEMENTAL_PUNCTUATION);
//				add(UnicodeBlock.IDEOGRAPHIC_SYMBOLS_AND_PUNCTUATION);
//			}
//		};
        strOne = strOne.replace(System.getProperty("line.separator"), " ");
        StringBuilder sb= new StringBuilder();
//		System.out.println("\n" + strOne);
        for (char c : strOne.toCharArray()) {
            String strOneChar = java.lang.Character.toString(c);
//			System.out.println("\nstrOneChar : [" + strOneChar + "]");
//			if (Character.isDigit(c)) { //숫자는 일단 특수문자로 제거 하지 말자...
            if (strOneChar.equals(" ")) {
                if (false) {//언어에 따라서 스페이스를 제거여부가 다르다. (영어는 제거 안함)
                    continue;
                }
            } else if (strOneChar.matches("[',・?!、.…()\\-\\[\\]]")) {
                continue;
//			} else if (strOneChar.matches("['・,?!、.…()\\-\\[\\]\\\n ]")) {

            } else if (strOneChar.matches("[｡？～！➡｟｠＜、`~!@#$%^&*+=;:：/?。｢「」【】<>{}・.\\\"()，\\-\\[\\] ]")) {
                continue;
            } else if (strOneChar.matches("[♪♫]")) {
//			} else if (unicodeBlocksPunctuation.contains(UnicodeBlock.of(c))) {
//				continue;
            }

            sb.append(strOneChar);
        }
//		System.out.println("\n" + strOne);
//		System.out.println("result : " + sb.toString().trim().toLowerCase());
//		return sb.toString().trim().toLowerCase();
        String result = sb.toString().trim().toLowerCase();
        result = result.trim().replaceAll("\\s+", " "); //연속된 공백은 하나로 한다.
        return result;
    }
    protected Set<Character.UnicodeBlock> getStudyLangUnicodeBlock() {
//		System.out.println("getStudyLangUnicodeBlock eng");
        Set<Character.UnicodeBlock> unicodeBlocks = new HashSet<Character.UnicodeBlock>() {
            {
                add(Character.UnicodeBlock.BASIC_LATIN);
//				strOneChar.matches("[\\u3040-\\u9FAF]")) {

//				add(UnicodeBlock.LATIN_1_SUPPLEMENT);
//				add(UnicodeBlock.LATIN_EXTENDED_A);
//				add(UnicodeBlock.GENERAL_PUNCTUATION);
            }
        };
        return unicodeBlocks;
    }
    public boolean isRightSubtitle(String subtitle) {
        if (subtitle.trim().length() == 0) {
            return false;
        }
//		if (DalString.isNumeric(subtitle)) {
//			return false;
//		}
        //학습어별로 허용되는 문자블록을 가져온다.
        Set<Character.UnicodeBlock> uUnicodeBlocksAlphabetOnly = getStudyLangUnicodeBlock();

        //TODO : 아래구문이 있으면 로컬에서는 돌아가는데 AWS에서는 안돌아간다. 아마 AWS의 자바 버전이 낮아서 그런거 같다.
//		Set<UnicodeBlock> unicodeBlocksPunctuation = new HashSet<UnicodeBlock>() {
//			{
//				add(UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
//				add(UnicodeBlock.GENERAL_PUNCTUATION);
//				add(UnicodeBlock.SUPPLEMENTAL_PUNCTUATION);
//				add(UnicodeBlock.IDEOGRAPHIC_SYMBOLS_AND_PUNCTUATION);
//			}
//		};

        List<String> allCharInSubtitle = new ArrayList<String>();
        List<String> correctCharInSubtitle = new ArrayList<String>();
        List<String> possibleCharInSubtitle = new ArrayList<String>();
        List<String> wrongCharInSubtitle = new ArrayList<String>();
        for (char c : subtitle.toCharArray()) {
            String strOneAtIndex = Character.toString(c);
//			strOneAtIndex = cleanUpText(strOneAtIndex); //자막의 대사를 처리할때도 cleanUpText를 부르는데 문자 하나하나에도 불러야 하나?

//			System.out.println("strOneAtIndex : [" + strOneAtIndex + "]");
            allCharInSubtitle.add(strOneAtIndex);

            if (strOneAtIndex.matches("[0-9'・,?!、.…()\\-\\[\\]\\\n ]")) {
                //			System.out.println("isSpecialChar 2");
                possibleCharInSubtitle.add(strOneAtIndex);
            } else if (strOneAtIndex.matches("[`~@#$%^&*+=';:：/?。「」【】<>{}・.\\\"()，\\-\\[\\] ]")) {
                //			System.out.println("isSpecialChar 3");
                possibleCharInSubtitle.add(strOneAtIndex);

            } else if (uUnicodeBlocksAlphabetOnly.contains(Character.UnicodeBlock.of(c)) || (isRightChar(strOneAtIndex))) {

                if (!(isAlphagetInEnglish(strOneAtIndex))) {
//					System.out.println("특수문자 : [" + strOneAtIndex + "]");
                    possibleCharInSubtitle.add(strOneAtIndex);
                } else {
                    correctCharInSubtitle.add(strOneAtIndex);
//					System.out.println("isRightChar");
                    possibleCharInSubtitle.add(strOneAtIndex);
                }
//			} else if (unicodeBlocksPunctuation.contains(UnicodeBlock.of(c))) {
//				System.out.println("isSpecialChar");
//				possibleCharInSubtitle.add(strOneAtIndex);
            } else {
                wrongCharInSubtitle.add(strOneAtIndex);

            }
        }

        if (correctCharInSubtitle.size() > 0) {
//			System.out.println("isRightSubtitle true");
            //해당언어의 글자가 있다고 해도, 대사내에서 비중을 봐야한다. possibleCharInSubtitle로 확인한다.
            double ratioSubtitleStudyLang = (double) possibleCharInSubtitle.size() / subtitle.length();
            double properRatioSubtitleLang = 0.5f;

            if (ratioSubtitleStudyLang > properRatioSubtitleLang) {
                return true;
            } else {
                return false;
            }
        }
//		System.out.println("isRightSubtitle false");
        return false;
//		//해당 학습어가 하나도 없으면 false
//		if (correctCharInSubtitle.size() == 0) {
//			return false;
//		}
//
//		//이상한 문자가 하나도 없으면 true
//		if (wrongCharInSubtitle.size() == 0) {
//			return true;
//		}
//
//		double percentOfRightCharInSubtitle = ((double)possibleCharInSubtitle.size()) / allCharInSubtitle.size();
//		if (percentOfRightCharInSubtitle >= 0.9) {
//			return true;
//		}
//		return false;
    }
    public static String removeBracketBoth(String strOne) {
        String strResult = strOne;
        if (strResult.startsWith("[")) {
            strResult = strResult.substring(1, strResult.length());
        }

        if (strResult.endsWith("]")) {
            strResult = strResult.substring(0, strResult.length() - 1);
        }
        return strResult;
    }

    protected void writeSMISubtitle(String strFilePath, String strToReplace, String strContents, String contentsInStyleTag, boolean isSplitSMI)
    {
        try {
            String newContents = strContents.replace("%@1", contentsInStyleTag);
            newContents = newContents.replace("%@2", strToReplace);


            if (isSplitSMI) {
                newContents = replaceHREFAndSrcPath(newContents);
            }
            Files.write(Paths.get(strFilePath), newContents.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String replaceHREFAndSrcPath(String newContents) {
        newContents = newContents.replaceAll(" href=\"", " href=\"../");
        newContents = newContents.replaceAll(" src=\"", " src=\"../");
        return newContents;
    }
}