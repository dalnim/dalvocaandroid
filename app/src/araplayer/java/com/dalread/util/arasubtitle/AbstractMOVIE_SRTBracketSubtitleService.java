package com.dalread.util.arasubtitle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMOVIE_SRTBracketSubtitleService extends AbstractMOVIE_SRTASSSubtitleService {

    protected abstract Set<SRTLine> getSRTLines(File file);

    public DTO_SUBTITLE_PARSED getDialogueInSubtitleDTO(File file, int subtitleLang) {
//        AbstractLanguage lang = langFactory.getLanguageService(dalVocaService.convertLangCodeToLangName(dto.getLANG_STUDY_CODE()));

        List<String> listDialogueStudyLang = new ArrayList<String>();
        List<String> listDialogueStudyLangWithoutPunctuation = new ArrayList<String>();
        List<DTO_DIALOGUE> listDialogueInfo = new ArrayList<DTO_DIALOGUE>();
        Map<String, DTO_DIALOGUE> mapDialogueInfo = new HashMap<String, DTO_DIALOGUE>();
//		Map<String, DTO_DIALOGUE> mapDialogueWithoutPunctInfo = new HashMap<String, DTO_DIALOGUE>();

//		SRTParser parser = new SRTParser();
//		SRTSub subtitle = parser.parse(file);
        Set<SRTLine> srtLines = getSRTLines(file);

        Map<String, Object> mapSubtitleFileLang = checkSubtitleFileLang(srtLines, subtitleLang);
        Integer subtitleFileLangFromDialogue = (Integer)mapSubtitleFileLang.get(Constants.KEY_SUBTITLE_FILE_LANG);
        List<Integer> listIsRightSubtitle  = (List<Integer>)mapSubtitleFileLang.get(Constants.KEY_LIST_IS_RIGHT_SUBTITLE);

        Integer indexForListIsRightSubtitle = 0;
        boolean hasStudyLangDialogue = false;
        for(SRTLine srtLine : srtLines) {
            List<String> listLine = srtLine.getTextLines();
            // <i>~~</i>를 &lt;i&lg;로 바꾸어주는데... 이렇게 되면 lt, lg를 단어로 인식한다...
//			String escapedListLine= StringEscapeUtils.escapeHtml(listLine.toString());

            String dialogue = String.join(System.getProperty("line.separator"), listLine);
            dialogue = cleanUpText(dialogue);
            String dialogueWithoutHtmlTag = getSimpleTextWithoutHTMLTag(dialogue);
            String dialogueWithoutHtmlTagStudyLang = "";
            String dialogueWithoutHtmlTagStudyLangWithoutPunct = "";
            String dialogueWithoutHtmlTagMeaning = "";

            Integer correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_NO;
//			if (listIsRightSubtitle.get(indexForListIsRightSubtitle).equals(Constants.IS_RIGHT_SUBTITLE_YES)) {
//				correctFormat = Constants.CORRECT_FORMAT_DIALOGUE_TO_INSERT_DB_YES;
//				dialogueWithoutHtmlTagStudyLang = dialogueWithoutHtmlTag;
//				dialogueWithoutHtmlTagStudyLangWithoutPunct = lang.removePunctAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);
//				hasStudyLangDialogue = true;
//			} else {
//				dialogueWithoutHtmlTagMeaning = dialogueWithoutHtmlTag;
//			}

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
//                if (subtitleFileLangFromDialogue == dto.getLANG_STUDY_CODE()) {
                    hasStudyLangDialogue = true;
                    dialogueWithoutHtmlTagStudyLang = dialogueWithoutHtmlTag;
                    dialogueWithoutHtmlTagStudyLangWithoutPunct = removePunctForSubtitleAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);
                    if (listIsRightSubtitle.get(indexForListIsRightSubtitle).equals(Constants.IS_RIGHT_SUBTITLE_YES)) {
                        correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_YES;
                    }
//                } else {
//                    dialogueWithoutHtmlTagMeaning = dialogueWithoutHtmlTag;
//                }
            }

            DTO_DIALOGUE dtoSubtitleInfo = new DTO_DIALOGUE.Builder()
                    .LANG_STUDY_CODE(Constants.LANGCODE_EN)
                    .LANG_MEANING_CODE(Constants.LANGCODE_KO)
                    .ID(srtLine.getId())
                    .VOCA(dialogueWithoutHtmlTagStudyLang)
                    .VOCA_ORIGINAL(dialogueWithoutHtmlTagStudyLang)
                    .VOCA_REMOVE_PUNCT(dialogueWithoutHtmlTagStudyLangWithoutPunct)
                    .MEANING(dialogueWithoutHtmlTagMeaning)
                    .CORRECT_FORMAT_VOCA_TO_INSERT_DB(correctFormat)
                    .START_TIME(getMilliseconds(srtLine.getTime().getStart()))
                    .END_TIME(getMilliseconds(srtLine.getTime().getEnd()))
                    .START_TIME_ORIGINAL(getMilliseconds(srtLine.getTime().getStart()))
                    .END_TIME_ORIGINAL(getMilliseconds(srtLine.getTime().getEnd()))
                    .build();

            listDialogueStudyLang.add(dialogueWithoutHtmlTagStudyLang);
            listDialogueStudyLangWithoutPunctuation.add(dialogueWithoutHtmlTagStudyLangWithoutPunct);
            listDialogueInfo.add(dtoSubtitleInfo);
            mapDialogueInfo.put(dialogueWithoutHtmlTagStudyLangWithoutPunct, dtoSubtitleInfo);
//			mapDialogueWithoutPunctInfo.put(dialogueWithoutHtmlTagStudyLangWithoutPunct, dtoSubtitleInfo);

            indexForListIsRightSubtitle++;
        }

        DTO_SUBTITLE_PARSED dtoSubtitleParsed = new DTO_SUBTITLE_PARSED.Builder()
                .HAS_STUDY_LANG_DIALOGUE(hasStudyLangDialogue)
                .NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(false) // SRT는 자막 싱크를 맞출 필요가 없다.
                .LIST_DIALOGUE_INFO(listDialogueInfo)
                .LIST_DIALOGUE_STUDY_LANG(listDialogueStudyLang)
                .LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION(listDialogueStudyLangWithoutPunctuation)
                .MAP_DIALOGUE_INFO(mapDialogueInfo)
                .build();
        return dtoSubtitleParsed;
    }


    //SRT는 자막에 한 종류의 언어밖에 없고, 자막의 언어를 앱에서 지정해주면 그걸쓰고 아니면 짐작해서 쓴다.
    private Map<String, Object> checkSubtitleFileLang(Set<SRTLine> srtLines, int subtitleLang) {
        Map<String, Object> mapResult = new HashMap<String, Object>();
        List<Integer> listIsRightSubtitle = new ArrayList<Integer>();
        Integer cntDialogueStudyLang = 0;
        Integer cntDialogueMotherTongue = 0;
        Integer cntDialogueAll = 0;
        for(SRTLine srtLine : srtLines) {
            List<String> listLine = srtLine.getTextLines();
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

    // 자막이 해당 학습어에 해당되는 글자인지


    //SRT파일은 마지막에 999가 있는지 보고 있으면 그다음줄을 다 제거한다.
    protected boolean cleanInputFileDTO(File FILE_input) {
        String strSubtitleContents = getFileContents(FILE_input.toPath());
        if (!subtitleHasID9999(strSubtitleContents)) {
            return false;
        }

        deteteID9999InSRTFile(FILE_input, strSubtitleContents);
        return true;
    }
    //SRT파일은 마지막에 999가 있는지 보고 있으면 그다음줄을 다 제거한다.
    protected boolean cleanInputSRTFile(File FILE_input, String folderNew) {
        String strSubtitleContents = getFileContents(FILE_input.toPath());
        if (!subtitleHasID9999(strSubtitleContents)) {
            return false;
        }

        makeBackupOfInputSRTFile(FILE_input, folderNew);
        deteteID9999InSRTFile(FILE_input, strSubtitleContents);
        return true;
    }
    private boolean subtitleHasID9999(String strSubtitleContents) {
        String[] eachLines = strSubtitleContents.split("\r\n");
        for(String eachLine : eachLines) {
            if (eachLine.startsWith("9999")) {
                return true;
            }
        }

        return false;
    }

    private void makeBackupOfInputSRTFile(File FILE_input, String folderNew) {
        File FILE_destBackupSRTFileSmi = new File(FILE_input.getPath() + Constants.FILE_DOT + Constants.FILEEXT_backup);;
        try {
            FileUtils.copyFile(FILE_input, FILE_destBackupSRTFileSmi);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        copyFolder(FILE_input,FILE_destBackupSRTFileSmi);

    }

    private void deteteID9999InSRTFile(File FILE_input, String strSubtitleContents) {
        String[] eachLines = strSubtitleContents.split("\r\n");
        StringBuilder newLines = new StringBuilder();
        for(String eachLine : eachLines) {
            if (eachLine.startsWith("9999")) {
                break;
            }
            newLines.append(eachLine);
            newLines.append(System.getProperty("line.separator"));
        }

        try {
            Files.write(Paths.get(FILE_input.toString()), newLines.toString().getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}