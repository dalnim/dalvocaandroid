package com.dalread.util.arasubtitle;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MOVIE_SMIService extends AbstractMOVIE_SubtitleService {


    public DTO_SUBTITLE_PARSED getDialogueInSubtitleDTO(File file, int subtitleLang) {
        Map<String, DTO_DIALOGUE> mapDialogueInfoByTimeKey = new HashMap<String, DTO_DIALOGUE>();

        String encodingStr = Constants.ENCODING_UTF_8;
        try {
            encodingStr = FileUtils.guessEncoding(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SamiParser parser = new SamiParser(encodingStr);
        SamiObject subtitleSmiObject = null;
        try {
            subtitleSmiObject = parser.parse(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SubtitleParsingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        List<SubtitleCue> listSubtitleCue = subtitleSmiObject.getCues();

        //SMI파일은 KRCC라고 적어놓고 ENCC가 들어갈수 있어서 자막을 읽어서 언어를 확인한다.
        Map<String, Object> mapSubtitleFileLang = checkSubtitleFileLang(listSubtitleCue, subtitleLang);
        Integer subtitleFileLangFromDialogue = (Integer)mapSubtitleFileLang.get(Constants.KEY_SUBTITLE_FILE_LANG);

        List<Integer> listIsRightSubtitle  = (List<Integer>)mapSubtitleFileLang.get(Constants.KEY_LIST_IS_RIGHT_SUBTITLE);

        Integer indexForListIsRightSubtitle = 0;
        boolean hasStudylangDialogue = false;
        boolean hasMotherTongueDialogue = false;
        for(SubtitleCue subtitleCue : listSubtitleCue) {
            String dialogue = subtitleCue.getText();
            if (dialogue.trim().equals("")){
                continue;
            }
            dialogue = cleanUpText(dialogue);

            Integer dialogueLangCode = subtitleCue.getLanguageCode();
            if (subtitleFileLangFromDialogue != Constants.LANGCODE_NONE) {
                dialogueLangCode = subtitleFileLangFromDialogue;
            }

            String dialogueWithoutHtmlTag = getSimpleTextWithoutHTMLTag(dialogue);
            String dialogueWithoutHtmlTagStudyLang = "";
            String dialogueWithoutHtmlTagStudyLangWithoutPunct = "";
            String dialogueWithoutHtmlTagMeaning = "";

            if (subtitleLang == Constants.SUBTITLE_LANG_STUDY_LANG) {
                dialogueWithoutHtmlTagStudyLang = dialogueWithoutHtmlTag;
                dialogueWithoutHtmlTagStudyLangWithoutPunct = removePunctForSubtitleAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);
                hasStudylangDialogue = true;
            } else if (subtitleLang == Constants.SUBTITLE_LANG_MOTHER_TONGUE) {
                dialogueWithoutHtmlTagMeaning = dialogueWithoutHtmlTag;
                hasMotherTongueDialogue = true;
            } else {
                if (dialogueLangCode == Constants.LANGCODE_EN) {
                    dialogueWithoutHtmlTagStudyLang = dialogueWithoutHtmlTag;
                    dialogueWithoutHtmlTagStudyLangWithoutPunct = removePunctForSubtitleAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);
                    hasStudylangDialogue = true;
                } else {
                    dialogueWithoutHtmlTagMeaning = dialogueWithoutHtmlTag;
                    hasMotherTongueDialogue = true;
                }
            }
            Integer correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_NO;
            if (listIsRightSubtitle.get(indexForListIsRightSubtitle).equals(Constants.IS_RIGHT_SUBTITLE_YES)) {
                correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_YES;
            }

            DTO_DIALOGUE dtoSubtitleInfo = new DTO_DIALOGUE.Builder()
                    .LANG_STUDY_CODE(Constants.LANGCODE_EN)
                    .LANG_MEANING_CODE(Constants.LANGCODE_KO)
//					.ID(id)
                    .VOCA(dialogueWithoutHtmlTagStudyLang)
                    .VOCA_ORIGINAL(dialogueWithoutHtmlTagStudyLang)
                    .VOCA_REMOVE_PUNCT(dialogueWithoutHtmlTagStudyLangWithoutPunct)
                    .MEANING(dialogueWithoutHtmlTagMeaning)
                    .CORRECT_FORMAT_VOCA_TO_INSERT_DB(correctFormat)
                    .START_TIME(subtitleCue.getStartTime().getTimeAsMillisecond())
                    .END_TIME(subtitleCue.getEndTime().getTimeAsMillisecond())
                    .START_TIME_ORIGINAL(subtitleCue.getStartTime().getTimeAsMillisecond())
                    .END_TIME_ORIGINAL(subtitleCue.getEndTime().getTimeAsMillisecond())
                    .build();

            String key = subtitleCue.getStartTime().getTime() + "_" + subtitleCue.getEndTime().getTime();
            if (mapDialogueInfoByTimeKey.containsKey(key)) {
                DTO_DIALOGUE dtoSubtitleInfoExisted = mapDialogueInfoByTimeKey.get(key);
                dtoSubtitleInfoExisted.setCORRECT_FORMAT_VOCA_TO_INSERT_DB(correctFormat);
                if (!(dialogueWithoutHtmlTagStudyLang.equals(""))) {
                    dtoSubtitleInfoExisted.setVOCA(dialogueWithoutHtmlTagStudyLang);
                    dtoSubtitleInfoExisted.setVOCA_REMOVE_PUNCT(dialogueWithoutHtmlTagStudyLangWithoutPunct);
                }
                if (!(dialogueWithoutHtmlTagMeaning.equals(""))) {
                    dtoSubtitleInfoExisted.setMEANING(dialogueWithoutHtmlTagMeaning);
                }
            } else {
                mapDialogueInfoByTimeKey.put(key, dtoSubtitleInfo);
            }


            indexForListIsRightSubtitle++;
        }

        List<DTO_DIALOGUE> listDialogueInfo = mapDialogueInfoByTimeKey.values().stream().collect(Collectors.toList());
//		Collections.sort(listDialogueInfo, Comparator.comparingInt(DTO_DIALOGUE::getID));
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

        DTO_SUBTITLE_PARSED dtoSubtitleParsed = new DTO_SUBTITLE_PARSED.Builder()
                .HAS_STUDY_LANG_DIALOGUE(hasStudylangDialogue)
                .NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(hasStudylangDialogue && hasMotherTongueDialogue)
                .LIST_DIALOGUE_INFO(listDialogueInfo)
                .LIST_DIALOGUE_STUDY_LANG(listDialogueStudyLang)
                .LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION(listDialogueStudyLangWithoutPunctuation)
                .MAP_DIALOGUE_INFO(mapDialogueInfo)
//				.INPUT_SUBTITLE_FORMAT(Constants.INPUT_SUBTITLE_FORMAT_SMI)
//				.MAP_DIALOGUE_WITHOUT_PUNCT_INFO(mapDialogueWithoutPunctInfo)
                .build();


        return dtoSubtitleParsed;
    }



    private Map<String, Object> checkSubtitleFileLang(List<SubtitleCue> listSubtitleCue, int subtitleLang) {
        Map<String, Object> mapResult = new HashMap<String, Object>();
        List<Integer> listIsRightSubtitle = new ArrayList<Integer>();
//		boolean blnHasMoreThanTwoSubtitleLang = false;
        Set<Integer> hashSetLanguageCodeInSubtitle = new HashSet<Integer>(); //자막의 TAG를 보고 언어를 가져온다. 근데 이걸 쓸일이 있나?
        Integer cntDialogueStudyLang = 0;
        Integer cntDialogueMotherTongue = 0;
        Integer cntDialogueAll = 0;
        for(SubtitleCue subtitleCue : listSubtitleCue) {
            String dialogue = subtitleCue.getText();
            if (dialogue.trim().equals("")){
                continue;
            }

            Integer languageCodeInSubtitle = subtitleCue.getLanguageCode();
            String dialogueWithoutHtmlTag = getSimpleTextWithoutHTMLTag(dialogue);
            if (isRightSubtitle(dialogueWithoutHtmlTag)) {
                listIsRightSubtitle.add(Constants.IS_RIGHT_SUBTITLE_YES);
                cntDialogueStudyLang++;
            } else {
                listIsRightSubtitle.add(Constants.IS_RIGHT_SUBTITLE_NO);
                cntDialogueMotherTongue++;
            }
            cntDialogueAll++;
//			if (studyLangCode != languageCodeInSubtitle) {
//				blnHasMoreThanTwoSubtitleLang = true;
//			}
            hashSetLanguageCodeInSubtitle.add(languageCodeInSubtitle);
        }
        mapResult.put(Constants.KEY_LIST_IS_RIGHT_SUBTITLE, listIsRightSubtitle);

        //만약 SMI의 자막이 한개의 언어로만 되어 있으면,자막 파일의 언어를 설정해준다.
        Integer intSubtitleFileLangCode = Constants.LANGCODE_NONE;
        if (hashSetLanguageCodeInSubtitle.size() == 1) {
            Integer subtitleFileLangFromDialogue = guessSubtitleFileLang(cntDialogueAll, cntDialogueStudyLang, cntDialogueMotherTongue, Constants.LANGCODE_EN, Constants.LANGCODE_KO);
            Integer subtitleFileLangHTMLTag = hashSetLanguageCodeInSubtitle.iterator().next();
            intSubtitleFileLangCode = subtitleFileLangFromDialogue;
        }
        mapResult.put(Constants.KEY_SUBTITLE_FILE_LANG, intSubtitleFileLangCode);
        return mapResult;
    }



//	protected String getEncoding(HttpServletRequest req){
//		String encoding = req.getParameter(Constants.KEY_ENCODING);
//		if ((encoding == null) || (encoding.trim().length() == 0)) {
//			encoding = Constants.ENCODING_UTF_8;
//		}
//
//
//
//		return encoding;
//	}

}