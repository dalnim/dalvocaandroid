package com.dalread.util.arasubtitle;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MOVIE_ASSService extends AbstractMOVIE_SRTASSSubtitleService {
    public DTO_SUBTITLE_PARSED getDialogueInSubtitleDTO(File file, int subtitleLang) {
        List<String> listDialogueStudyLang = new ArrayList<String>();
        List<String> listDialogueStudyLangWithoutPunctuation = new ArrayList<String>();
        List<DTO_DIALOGUE> listDialogueInfo = new ArrayList<DTO_DIALOGUE>();
        Map<String, DTO_DIALOGUE> mapDialogueInfo = new HashMap<String, DTO_DIALOGUE>();
//		Map<String, DTO_DIALOGUE> mapDialogueWithoutPunctInfo = new HashMap<String, DTO_DIALOGUE>();


        ASSParser parser = new ASSParser();
        File file2Digit = make3DigitTo2DigitFile(file);
        ASSSub subtitle = parser.parse(file2Digit);

        Map<String, Object> mapSubtitleFileLang = checkSubtitleFileLang(subtitle.getTimedLines());
        Integer subtitleFileLangFromDialogue = (Integer)mapSubtitleFileLang.get(Constants.KEY_SUBTITLE_FILE_LANG);
        List<Integer> listIsRightSubtitle  = (List<Integer>)mapSubtitleFileLang.get(Constants.KEY_LIST_IS_RIGHT_SUBTITLE);

        Integer indexForListIsRightSubtitle = 0;
        boolean hasStudyLangDialogue = false;
        int id = 1;
        Set<? extends TimedLine> subtitleLines = subtitle.getTimedLines();
        for(TimedLine subtitleLine : subtitleLines) {
            TimedObject timedLine = subtitleLine.getTime();
            List<String> listLine = subtitleLine.getTextLines();

            LocalTime srtTimeStart = timedLine.getStart();
            LocalTime srtTimeEnd = timedLine.getEnd();
            Integer intStartTime = getMilliseconds(srtTimeStart);
            Integer intEndTime = getMilliseconds(srtTimeEnd);

            String dialogue = String.join("\n", listLine);
            dialogue = dialogue.replaceAll("^,", "");
            dialogue = dialogue.replaceAll("\\{.*?\\}", "");
            dialogue = cleanUpText(dialogue);

            String dialogueWithoutHtmlTag = getSimpleTextWithoutHTMLTag(dialogue);
            String dialogueWithoutHtmlTagStudyLang = "";
            String dialogueWithoutHtmlTagStudyLangWithoutPunct = "";
            String dialogueWithoutHtmlTagMeaning = "";

            Integer correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_NO;

            if (subtitleLang == Constants.SUBTITLE_LANG_STUDY_LANG) {
                hasStudyLangDialogue = true;
                dialogueWithoutHtmlTagStudyLang = dialogueWithoutHtmlTag;
                dialogueWithoutHtmlTagStudyLangWithoutPunct = removePunctForSubtitleAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);
                if (listIsRightSubtitle.get(indexForListIsRightSubtitle).equals(Constants.IS_RIGHT_SUBTITLE_YES)) {
                    correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_YES;
                }
            } else if (subtitleLang == Constants.SUBTITLE_LANG_MOTHER_TONGUE) {
                dialogueWithoutHtmlTagMeaning = dialogueWithoutHtmlTag;
            } else {
                if (subtitleFileLangFromDialogue == Constants.LANGCODE_EN) {
                    hasStudyLangDialogue = true;
                    dialogueWithoutHtmlTagStudyLang = dialogueWithoutHtmlTag;
                    dialogueWithoutHtmlTagStudyLangWithoutPunct = removePunctForSubtitleAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);
                    if (listIsRightSubtitle.get(indexForListIsRightSubtitle).equals(Constants.IS_RIGHT_SUBTITLE_YES)) {
                        correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_YES;
                    }
                } else {
                    dialogueWithoutHtmlTagMeaning = dialogueWithoutHtmlTag;
                }
            }



            DTO_DIALOGUE dtoSubtitleInfo = new DTO_DIALOGUE.Builder()
                    .LANG_STUDY_CODE(Constants.LANGCODE_EN)
                    .LANG_MEANING_CODE(Constants.LANGCODE_KO)
                    .ID(id++)
                    .VOCA(dialogueWithoutHtmlTagStudyLang)
                    .VOCA_ORIGINAL(dialogueWithoutHtmlTagStudyLang)
                    .VOCA_REMOVE_PUNCT(dialogueWithoutHtmlTagStudyLangWithoutPunct)
                    .MEANING(dialogueWithoutHtmlTagMeaning)
                    .CORRECT_FORMAT_VOCA_TO_INSERT_DB(correctFormat)
                    .START_TIME(intStartTime)
                    .END_TIME(intEndTime)
                    .START_TIME_ORIGINAL(intStartTime)
                    .END_TIME_ORIGINAL(intEndTime)
                    .build();

            listDialogueStudyLang.add(dialogueWithoutHtmlTagStudyLang);
            listDialogueStudyLangWithoutPunctuation.add(dialogueWithoutHtmlTagStudyLangWithoutPunct);
            listDialogueInfo.add(dtoSubtitleInfo);
            mapDialogueInfo.put(dialogueWithoutHtmlTagStudyLangWithoutPunct, dtoSubtitleInfo);
//			mapDialogueWithoutPunctInfo.put(dialogueWithoutHtmlTagStudyLangWithoutPunct, dtoSubtitleInfo);

            indexForListIsRightSubtitle++;
        }

        return new DTO_SUBTITLE_PARSED.Builder()
                .HAS_STUDY_LANG_DIALOGUE(hasStudyLangDialogue)
                .NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(false) //ASS는 자막 싱크를 맞출필요없다.
                .LIST_DIALOGUE_INFO(listDialogueInfo)
                .LIST_DIALOGUE_STUDY_LANG(listDialogueStudyLang)
                .LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION(listDialogueStudyLangWithoutPunctuation)
                .MAP_DIALOGUE_INFO(mapDialogueInfo)
//				.INPUT_SUBTITLE_FORMAT(Constants.INPUT_SUBTITLE_FORMAT_ASS)
//				.MAP_DIALOGUE_WITHOUT_PUNCT_INFO(mapDialogueWithoutPunctInfo)
                .build();
    }

    public Map<String, Object> getDialogueInSubtitle(File file) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Object> listId = new ArrayList<Object>();
        List<Object> listStartTime = new ArrayList<Object>();
        List<Object> listEndTime = new ArrayList<Object>();
        List<Object> listDialogue = new ArrayList<Object>();

        ASSParser parser = new ASSParser();
        File file2Digit = make3DigitTo2DigitFile(file);
        ASSSub subtitle = parser.parse(file2Digit);
        int id = 1;
        Set<? extends TimedLine> subtitleLines = subtitle.getTimedLines();
        for(TimedLine subtitleLine : subtitleLines) {
            TimedObject timedLine = subtitleLine.getTime();
            List<String> listInSubtitleLine = subtitleLine.getTextLines();
            subtitleLine.toString();
//			System.out.println("subtitleLine :\n" + subtitleLine.toString());

            LocalTime srtTimeStart = timedLine.getStart();
            LocalTime srtTimeEnd = timedLine.getEnd();
            Integer intStartTime = getMilliseconds(srtTimeStart);
            Integer intEndTime = getMilliseconds(srtTimeEnd);

            listId.add(id++);
            listStartTime.add(intStartTime);
            listEndTime.add(intEndTime);
            String strSubtitleLine = String.join("\n", listInSubtitleLine);
            strSubtitleLine = strSubtitleLine.replaceAll("^,", "");

            strSubtitleLine = strSubtitleLine.replaceAll("\\{.*?\\}", "");
//			String strSubtitleLine1 = strSubtitleLine.replace("\\N", "\r\n");
//			String strSubtitleLine2 = strSubtitleLine.replace("\\N", "\r");
//			String strSubtitleLine3 = strSubtitleLine.replace("\\N", "\n");
////			String strSubtitleLine4 = strSubtitleLine.replaceAll("\\N", "\r\n");
////			String strSubtitleLine5 = strSubtitleLine.replaceAll("\\N", "\r");
////			String strSubtitleLine6 = strSubtitleLine.replaceAll("\\N", "\n");
//
//			strSubtitleLine = strSubtitleLine.replace("\\N", "\n");
            strSubtitleLine  = getSimpleTextWithoutHTMLTag(strSubtitleLine);
            listDialogue.add(removeBracketBoth(strSubtitleLine));
        }

        result.put("SUBTITLE_ID", listId);
        result.put("SUBTITLE_STARTTIME", listStartTime);
        result.put("SUBTITLE_ENDTIME", listEndTime);
        result.put("SUBTITLE_DIALOGUE", listDialogue);


        return result;
    }

    //자막의 언어를 앱에서 지정해주면 그걸쓰고 아니면 짐작해서 쓴다.
    private Map<String, Object> checkSubtitleFileLang(Set<? extends TimedLine> subtitleLines) {
        Map<String, Object> mapResult = new HashMap<String, Object>();
        List<Integer> listIsRightSubtitle = new ArrayList<Integer>();
        Integer cntDialogueStudyLang = 0;
        Integer cntDialogueMotherTongue = 0;
        Integer cntDialogueAll = 0;
        for(TimedLine subtitleLine : subtitleLines) {
            List<String> listLine = subtitleLine.getTextLines();

            String strListLine = String.join(System.getProperty("line.separator"), listLine);
            if (isRightSubtitle(strListLine)) {
                listIsRightSubtitle.add(Constants.IS_RIGHT_SUBTITLE_YES);
                cntDialogueStudyLang++;
            } else {
                cntDialogueMotherTongue++;
                listIsRightSubtitle.add(Constants.IS_RIGHT_SUBTITLE_NO);
            }
            cntDialogueAll++;
        }
        mapResult.put(Constants.KEY_LIST_IS_RIGHT_SUBTITLE, listIsRightSubtitle);
        Integer subtitleFileLangFromDialogue = guessSubtitleFileLang(cntDialogueAll, cntDialogueStudyLang, cntDialogueMotherTongue, Constants.LANGCODE_EN, Constants.LANGCODE_KO);
//		Integer subtitleLangCode = guessSubtitleFileLang(srtLines.size(), cntDialogueStudyLang, cntDialogueIsNotStudyLang) == Constants.SUBTITLE_FILE_LANG_STUDYLANG ? dto.getLANG_STUDY_CODE() : dto.getLANG_MEANING_CODE();
        mapResult.put(Constants.KEY_SUBTITLE_FILE_LANG, subtitleFileLangFromDialogue);
        return mapResult;
    }

    private File make3DigitTo2DigitFile(File file) {
        String filePath = file.getAbsolutePath();
        String file2DigitPath = FilenameUtils.getFullPath(filePath) + File.separator + FilenameUtils.getBaseName(filePath) + "_2Digit";

        try {
            // BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
//			BufferedWriter bw2DigitFile = new BufferedWriter(new FileWriter(file2DigitPath));
            BufferedReader brInput = new BufferedReader(new FileReader(filePath));
            String strLine;
            StringBuilder strBuilder = new StringBuilder();
            while ((strLine = brInput.readLine()) != null) {
                String strLine2Digit = strLine.replaceAll(":(\\d)(\\d):(\\d)(\\d).(\\d)(\\d)(\\d),", ":$1$2:$3$4.$5$6,");
                strBuilder.append(strLine2Digit);
                strBuilder.append(System.getProperty("line.separator"));
//				bw2DigitFile.write(strLine2Digit + System.getProperty("line.separator"));
            }
            String strContents = strBuilder.toString();
            Files.write(Paths.get(file2DigitPath), strContents.getBytes());
//			bw2DigitFile.flush();

            // 객체 닫기
//			bw2DigitFile.close();
            brInput.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        File file2Digit = new File(file2DigitPath);
        return file2Digit;
    }
}