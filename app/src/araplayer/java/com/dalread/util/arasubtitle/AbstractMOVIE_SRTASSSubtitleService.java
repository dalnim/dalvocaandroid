package com.dalread.util.arasubtitle;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractMOVIE_SRTASSSubtitleService extends AbstractMOVIE_SubtitleService {


    //ASS SRT는 동일함. 나중에 따로 처리할것...
    protected void saveSubtitleWithDalReadHTMLTag(Map<String, List<String>> mapTextWithMeaning, Map<String, Object> mapSubtitle, String folderNew)
    {
        List<DTO_DIALOGUE> listDtoSubtitleAll = (List<DTO_DIALOGUE>)mapSubtitle.get(Constants.KEY_SUBTITLE_ALL);

        List<String> arrContentsWithMeaning = new ArrayList<String>();
        for (String strFilePath : mapTextWithMeaning.keySet()) {
            arrContentsWithMeaning = mapTextWithMeaning.get(strFilePath);
        }


        //SRT는 translated SMI의 언어가 뭔지는 학습어에 따라서 정해준다. (영어는 EN으로...)
        //--> smi처럼 자막 파일에 언어가 정해져있지 않으면... default를 사용하자...
        //자막에 언어가 2개 있는건 정보가 없어서 1개처럼 처리하자...
//    	List<String> listLangInSMI = getLangToUseInSMIByStudyLang(req);
        String strLangInSMI = Constants.LANG_SMI_CLASS_DEFAULT;
        String contentsInStyleTag = ".DEFAULT {Name:DEFAULT; Lang:DEFAULT; SAMIType:CC;}";

        StringBuilder strAllDialogue = new StringBuilder();
        StringBuilder strAllDialogueForAllHTMLSubtitle = new StringBuilder();
        StringBuilder strAllDialogueForAllSMISubtitle = new StringBuilder();
        String folderNameHTML = "html";
        String strPathSubtitleSmiTemplate = folderNew + File.separator + subtitleSmi;
        File file =  new File(strPathSubtitleSmiTemplate);
        String strContents = "";
        try {
            strContents = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for(Integer index = 0; index < listDtoSubtitleAll.size(); index++) {
            //ID를 SRT로부터 가져오기 ID가 순서대로 안되어 있는 경우가 있더라...
            //Integer id = listId.get(index);
            Integer id = index;
            String strPathDialogue = folderNameHTML + File.separator + id + Constants.FILE_DOT +  Constants.FILEEXT_html;
            Integer startTime = listDtoSubtitleAll.get(index).getSTART_TIME();
            Integer endTime = listDtoSubtitleAll.get(index).getEND_TIME();
//    		String strSubtitle = listDtoSubtitleAll.get(index).getVOCA();
            String strDialogueTranslated = arrContentsWithMeaning.get(index);
            strDialogueTranslated = removeBracketBoth(strDialogueTranslated);
            String syncStart = "<SYNC Start=" + startTime + "><P Class=" + strLangInSMI + ">";
            String syncEnd = "<SYNC Start=" + endTime + "><P Class=" + strLangInSMI + ">&nbsp;";

            String syncStartHTML = "<span s=" + Constants.DIALOGUE_HIDE + " id=" + strLangInSMI + "_" + startTime + " timelang=" + strLangInSMI + "_" + startTime + " time=" + startTime + " lang=" + strLangInSMI + "><br/>";
            String syncEndHTML = "<span s=" + Constants.DIALOGUE_HIDE + " id=" + strLangInSMI + "_" + startTime + " timelang=" + strLangInSMI + "_" + endTime + " time=" + endTime + " lang=" + strLangInSMI + "></span>";
            String strDialogueTranslatedHTML = strDialogueTranslated + "</span>";
            strDialogueTranslatedHTML = index + ": " + strDialogueTranslatedHTML;


            strAllDialogue.append(syncStart + System.getProperty("line.separator"));
            strAllDialogue.append(strPathDialogue + System.getProperty("line.separator"));
            strAllDialogue.append(syncEnd + System.getProperty("line.separator"));
            strAllDialogue.append(System.getProperty("line.separator"));

            strAllDialogueForAllSMISubtitle.append(syncStart + System.getProperty("line.separator"));
            strAllDialogueForAllSMISubtitle.append(strDialogueTranslated + System.getProperty("line.separator"));
            strAllDialogueForAllSMISubtitle.append(syncEnd + System.getProperty("line.separator"));
            strAllDialogueForAllSMISubtitle.append(System.getProperty("line.separator"));

            strAllDialogueForAllHTMLSubtitle.append(syncStartHTML + System.getProperty("line.separator"));
            strAllDialogueForAllHTMLSubtitle.append(strDialogueTranslatedHTML + System.getProperty("line.separator"));
            strAllDialogueForAllHTMLSubtitle.append(syncEndHTML + System.getProperty("line.separator"));
            strAllDialogueForAllHTMLSubtitle.append(System.getProperty("line.separator"));


            StringBuilder strTranslatedDialogueWithTime = new StringBuilder();
            strTranslatedDialogueWithTime.append(syncStart + System.getProperty("line.separator"));
            strTranslatedDialogueWithTime.append(strDialogueTranslated + System.getProperty("line.separator"));
            strTranslatedDialogueWithTime.append(syncEnd + System.getProperty("line.separator"));
            String strFullPathDialogue=  folderNew + File.separator + strPathDialogue;
            writeSMISubtitle(strFullPathDialogue, strTranslatedDialogueWithTime.toString(), strContents, contentsInStyleTag, true);
        }

        writeSMISubtitle(file.getPath(), strAllDialogue.toString(), strContents, contentsInStyleTag, false);

        String strAllHTMLFilePath = FilenameUtils.removeExtension(file.getPath()) + Constants.FILE_SUFFIX_ALLHTML;
        writeSMISubtitle(strAllHTMLFilePath, strAllDialogueForAllHTMLSubtitle.toString(), strContents, contentsInStyleTag, false);

        String strAllSmiFilePath = FilenameUtils.removeExtension(file.getPath()) + Constants.FILE_SUFFIX_ALLSMI;
        writeSMISubtitle(strAllSmiFilePath, strAllDialogueForAllSMISubtitle.toString(), strContents, contentsInStyleTag, false);
        return;
    }
}