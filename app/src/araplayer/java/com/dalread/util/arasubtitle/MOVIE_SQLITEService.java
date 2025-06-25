package com.dalread.util.arasubtitle;

import com.dalread.database.sqlite.model.DicModel;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MOVIE_SQLITEService extends AbstractMOVIE_SRTASSSubtitleService {
    protected ISubtitleParser subtitleParser = new SubtitleParser_SRT();

    public DTO_SUBTITLE_PARSED getDialogueInSubtitleDTO(File file, int subtitleLang) {
        List<String> listDialogueStudyLang = new ArrayList<String>();
        List<String> listDialogueStudyLangWithoutPunctuation = new ArrayList<String>();
        List<DTO_DIALOGUE> listDialogueInfo = new ArrayList<>();
        Map<String, DTO_DIALOGUE> mapDialogueInfo = new HashMap<>();

        List<DicModel> subtitleList = subDatabase.getSubtitleDialogList();
        for (DicModel dicModel : subtitleList) {
            String dialogue = dicModel.getVIVoca();
            dialogue = cleanUpText(dialogue);

            String strListLine = dicModel.getVIVoca();
            strListLine = cleanUpText(strListLine);
            Integer correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_NO;
            if (isRightSubtitle(strListLine)) {
                correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_YES;
            }
            String dialogueWithoutHtmlTagStudyLang = strListLine;
            String dialogueWithoutHtmlTagStudyLangWithoutPunct = removePunctForSubtitleAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);
            Long startTime = dicModel.getStartTime();
            Long endTime = dicModel.getEndTime();
            Long startTimeOriginal = dicModel.getStartTimeOriginal();
            Long endTimeOriginal = dicModel.getEndTimeOriginal();
            DTO_DIALOGUE dtoSubtitleInfo = new DTO_DIALOGUE.Builder()
                    .ID(dicModel.getVIId())
                    .LANG_STUDY_CODE(dicModel.getLangStudy())
                    .VOCA(dialogueWithoutHtmlTagStudyLang)
                    .VOCA_ORIGINAL(dialogueWithoutHtmlTagStudyLang)
                    .VOCA_REMOVE_PUNCT(dialogueWithoutHtmlTagStudyLangWithoutPunct)
                    .MEANING(dicModel.getMeaning())
                    .CORRECT_FORMAT_VOCA_TO_INSERT_DB(correctFormat)
                    .START_TIME(startTime.intValue())
                    .END_TIME(endTime.intValue())
                    .START_TIME_ORIGINAL(startTimeOriginal.intValue())
                    .END_TIME_ORIGINAL(endTimeOriginal.intValue())
                    .build();


            listDialogueStudyLang.add(dialogueWithoutHtmlTagStudyLang);
            listDialogueStudyLangWithoutPunctuation.add(dialogueWithoutHtmlTagStudyLangWithoutPunct);
            listDialogueInfo.add(dtoSubtitleInfo);
            mapDialogueInfo.put(dialogueWithoutHtmlTagStudyLangWithoutPunct, dtoSubtitleInfo);
        }

        return new DTO_SUBTITLE_PARSED.Builder()
                .HAS_STUDY_LANG_DIALOGUE(true)
                .NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(false) //SQLite는 자막 싱크를 맞출필요없다.
                .LIST_DIALOGUE_INFO(listDialogueInfo)
                .LIST_DIALOGUE_STUDY_LANG(listDialogueStudyLang)
                .LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION(listDialogueStudyLangWithoutPunctuation)
                .MAP_DIALOGUE_INFO(mapDialogueInfo)
//				.INPUT_SUBTITLE_FORMAT(Constants.INPUT_SUBTITLE_FORMAT_SQLITE)
//				.MAP_DIALOGUE_WITHOUT_PUNCT_INFO(mapDialogueWithoutPunctInfo)
                .build();
    }

    public DTO_SUBTITLE_PARSED getDialogueInSubtitleDTO1(File file, int subtitleLang) {
        List<String> listDialogueStudyLang = new ArrayList<String>();
        List<String> listDialogueStudyLangWithoutPunctuation = new ArrayList<String>();
        List<DTO_DIALOGUE> listDialogueInfo = new ArrayList<DTO_DIALOGUE>();
        Map<String, DTO_DIALOGUE> mapDialogueInfo = new HashMap<String, DTO_DIALOGUE>();
//		Map<String, DTO_DIALOGUE> mapDialogueWithoutPunctInfo = new HashMap<String, DTO_DIALOGUE>();
        List<DicModel> subtitleList = subDatabase.getSubtitleDialogList();
        try {
            System.out.println("file.getPath() : " + file.getPath());
            String driver = "org.sqlite.JDBC";
            Class.forName(driver);
            String filePath = "/Users/dalnimbest/epubresult/DalPlayer_Ruby.sqlite";
            String dbUrl = "jdbc:sqlite:" + file.getPath();
            Connection conn;

            conn = DriverManager.getConnection(dbUrl);

            String sql = "SELECT * FROM SUBTITLE ORDER BY START_TIME;";
//
//            JDBC4PreparedStatement pstmt  = (JDBC4PreparedStatement) conn.prepareStatement(sql);
//
//            ResultSet rs  = pstmt.executeQuery();
//
//            // loop through the result set
//            while (rs.next()) {
//                String strListLine = String.join(System.getProperty("line.separator"), rs.getString("VOCA"));
//                strListLine = lang.cleanUpText(strListLine);
//                Integer correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_NO;
//                if (lang.isRightSubtitle(strListLine)) {
//                    correctFormat = Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_YES;
//                }
//                String dialogueWithoutHtmlTagStudyLang = rs.getString("VOCA");
//                String dialogueWithoutHtmlTagStudyLangWithoutPunct = lang.removePunctForSubtitleAndMakeLowercase(dialogueWithoutHtmlTagStudyLang);
////   				String vocaWithoutPunct = lang.removePunctAndMakeLowercase(rs.getString("VOCA"));
//                DTO_DIALOGUE dtoSubtitleInfo = DTO_DIALOGUE.builder()
//                        .LANG_STUDY_CODE(rs.getInt("LANG_STUDY"))
//                        .LANG_MEANING_CODE(dto.getLANG_MEANING_CODE())
//                        .ID(rs.getInt("ID"))
//                        .VOCA(dialogueWithoutHtmlTagStudyLang)
//                        .VOCA_REMOVE_PUNCT(dialogueWithoutHtmlTagStudyLangWithoutPunct)
//                        .VOCA_TYPE(rs.getInt("VOCA_TYPE"))
//                        .VOCA_ID(rs.getInt("VOCA_ID"))
//                        .VOCA_ID_TO_SEND_SERVER(rs.getInt("VOCA_ID_TO_SEND_SERVER"))
//                        .VOCA_KNOW(rs.getInt("VOCA_KNOW"))
//                        .VOCA_KNOWPRONOUNCE(rs.getInt("VOCA_KNOWPRONOUNCE"))
//                        .MEANING(rs.getString("MEANING"))
//                        .VOCA_ORIGINAL(rs.getString("VOCA_ORIGINAL"))
//                        .CORRECT_FORMAT_VOCA_TO_INSERT_DB(correctFormat)
//                        .START_TIME(rs.getInt("START_TIME"))
//                        .END_TIME(rs.getInt("END_TIME"))
//                        .START_TIME_ORIGINAL(rs.getInt("START_TIME_ORIGINAL"))
//                        .END_TIME_ORIGINAL(rs.getInt("END_TIME_ORIGINAL"))
//                        .USED(rs.getInt("USED"))
//                        .MEMO(rs.getString("MEMO"))
//                        .BOOKMARK(rs.getInt("BOOKMARK"))
//                        .REPEAT(rs.getInt("REPEAT"))
//                        .RECORDING_PATH(rs.getString("RECORDING_PATH"))
//                        .build();
//
//
//
//                listDialogueStudyLang.add(dialogueWithoutHtmlTagStudyLang);
//                listDialogueStudyLangWithoutPunctuation.add(dialogueWithoutHtmlTagStudyLangWithoutPunct);
//                listDialogueInfo.add(dtoSubtitleInfo);
//                mapDialogueInfo.put(dialogueWithoutHtmlTagStudyLangWithoutPunct, dtoSubtitleInfo);
////				mapDialogueWithoutPunctInfo.put(vocaWithoutPunct, dtoSubtitleInfo);
//            }
//            rs.close();
//            pstmt.close();
//            conn.close();



        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {

        }
        return new DTO_SUBTITLE_PARSED.Builder()
                .HAS_STUDY_LANG_DIALOGUE(true)
                .NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(false) //SQLite는 자막 싱크를 맞출필요없다.
                .LIST_DIALOGUE_INFO(listDialogueInfo)
                .LIST_DIALOGUE_STUDY_LANG(listDialogueStudyLang)
                .LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION(listDialogueStudyLangWithoutPunctuation)
                .MAP_DIALOGUE_INFO(mapDialogueInfo)
//				.INPUT_SUBTITLE_FORMAT(Constants.INPUT_SUBTITLE_FORMAT_SQLITE)
//				.MAP_DIALOGUE_WITHOUT_PUNCT_INFO(mapDialogueWithoutPunctInfo)
                .build();
    }
}