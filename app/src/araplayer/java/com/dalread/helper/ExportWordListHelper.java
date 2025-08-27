package com.dalread.helper;

import android.content.Context;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.database.sqlite.model.SubtitleWordListModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.HtmlEntityDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 단어 목록 내보내기를 위한 Helper 클래스
 * 데이터 로드, 매핑, 포맷팅을 담당
 */
public class ExportWordListHelper {
    
    private static final String TAG = "ExportWordListHelper";
    
    /**
     * 선택된 단어들을 내보내기 형식으로 변환
     * 
     * @param selectedWords 선택된 단어 목록
     * @param subDatabase 데이터베이스
     * @param context 컨텍스트
     * @return 포맷된 단어 목록 텍스트
     */
    public static String exportWordList(List<DicModel> selectedWords, 
                                      SubDatabase subDatabase,
                                      Context context) {
        if (selectedWords == null || selectedWords.isEmpty()) {
            return "";
        }
        
        try {
            // 데이터 로드 및 매핑 구축
            WordListData data = prepareWordListData(selectedWords, subDatabase);
            
            // 포맷팅
            return formatWordList(selectedWords, data, context);
            
        } catch (Exception e) {
            DLog.e(TAG, "Error exporting word list: " + e.getMessage());
            return "";
        }
    }
    
    // 단어 목록 데이터 준비
    private static WordListData prepareWordListData(List<DicModel> selectedWords, 
                                                  SubDatabase subDatabase) {
        WordListData data = new WordListData();
        
        // 자막 데이터 로드 (북마크 정보 포함)
        data.allSubtitles = new ArrayList<>(subDatabase.getSubtitleDialogList());
        data.allSubtitleWordList = new ArrayList<>(subDatabase.getSubtitleWordList());
        
        // 단어-자막 매핑 구축
        data.wordToSubtitleMap = buildWordToSubtitleMap(selectedWords, data.allSubtitleWordList);
        
        return data;
    }
    
    // 단어-자막 매핑 구축 (효율적인 단일 루프 방식)
    private static Map<Integer, List<Integer>> buildWordToSubtitleMap(
            List<DicModel> words, List<SubtitleWordListModel> subtitleWordList) {
        
        Map<Integer, List<Integer>> wordToSubtitleMap = new HashMap<>();
        
        // allSubtitleWordList를 한 번만 순회
        for (SubtitleWordListModel item : subtitleWordList) {
            int vocaId = item.getVocaId();
            int subtitleId = item.getSubtitleId();
            
            // 해당 단어 ID의 자막 ID 리스트 가져오기 (없으면 새로 생성)
            wordToSubtitleMap
                .computeIfAbsent(vocaId, k -> new ArrayList<>())
                .add(subtitleId);
        }
        
        return wordToSubtitleMap;
    }
    
    // 단어 목록을 포맷된 텍스트로 변환
    private static String formatWordList(List<DicModel> selectedWords, 
                                       WordListData data,
                                       Context context) {
        
        StringBuilder wordListText = new StringBuilder();
        
        for (DicModel word : selectedWords) {
            if (wordListText.length() > 0) {
                wordListText.append("\n");
            }
            
            String formattedLine = formatWordLine(word, data, context);
            wordListText.append(formattedLine);
        }
        
        return wordListText.toString();
    }
    
    // 개별 단어 라인 포맷팅 (단어\t뜻\t발음\t\t자막)
    private static String formatWordLine(DicModel word, 
                                       WordListData data,
                                       Context context) {
        
        // 단어\t뜻\t발음\t\t자막 형식으로 구성
        String voca = word.getVocaDisplay() != null ? word.getVocaDisplay() : "";
        String meaning = word.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)) != null ? 
                       word.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)) : "";
        String pronunciation = word.getVIPronounce() != null ? word.getVIPronounce() : "";
        String subtitle = getBestSubtitleForWord(word, data);
        
        return voca + "\t" + meaning + "\t" + pronunciation + "\t\t" + subtitle;
    }
    
    // 단어에 대한 최적의 자막 선택
    private static String getBestSubtitleForWord(DicModel word, WordListData data) {
        try {
            // 메모리에서 단어-자막 매핑 찾기
            List<Integer> subtitleIds = data.wordToSubtitleMap.get(word.getVocaId());
            if (subtitleIds == null || subtitleIds.isEmpty()) {
                DLog.d(TAG, "No subtitles found for word VOCA_ID: " + word.getVocaId());
                return "";
            }
            
            // 우선순위에 따라 최적의 자막 선택
            DicModel bestSubtitle = findBestSubtitleByPriority(word, subtitleIds, data.allSubtitles);
            
            if (bestSubtitle != null) {
                String subtitleText = bestSubtitle.getVocaDisplay();
                if (subtitleText != null) {
                    // HTML 엔티티 디코딩 후 줄바꿈 문자와 탭 문자를 공백 하나로 치환
                    subtitleText = HtmlEntityDecoder.decodeHtmlEntities(subtitleText);
                    return subtitleText.replaceAll("[\\r\\n\\t]+", " ");
                }
                return "";
            }
            
            return "";
            
        } catch (Exception e) {
            DLog.e(TAG, "Error getting subtitle for word VOCA_ID " + word.getVocaId() + ": " + e.getMessage());
            return "";
        }
    }
    
    // 우선순위에 따른 최적 자막 선택
    private static DicModel findBestSubtitleByPriority(DicModel word, 
                                                     List<Integer> subtitleIds,
                                                     List<DicModel> allSubtitles) {
        DicModel bestSubtitle = null;
        int bestScore = -1;
        
        for (Integer subtitleId : subtitleIds) {
            // 해당 ID의 자막 찾기
            DicModel subtitle = findSubtitleById(subtitleId, allSubtitles);
            if (subtitle == null) continue;
            
            // 우선순위 점수 계산
            int score = calculateSubtitlePriority(word, subtitle);
            
            if (score > bestScore) {
                bestScore = score;
                bestSubtitle = subtitle;
            }
        }
        
        return bestSubtitle;
    }
    
    // ID로 자막 찾기
    private static DicModel findSubtitleById(int subtitleId, List<DicModel> allSubtitles) {
        for (DicModel subtitle : allSubtitles) {
            if (subtitle.getId() == subtitleId) {
                return subtitle;
            }
        }
        return null;
    }
    
    // 자막 우선순위 점수 계산
    private static int calculateSubtitlePriority(DicModel word, DicModel subtitle) {
        int score = 0;
        
        // 1. 북마크 우선순위 (최고)
        if (subtitle.isBookmark()) {
            score += 1000; // 북마크된 자막에 최고 점수
        }
        
        // 2. 암기 등급 우선순위
        switch (word.getVocaKnow()) {
            case Constant.VOCA_KNOW.VOCA_KNOW_KNOWN:
                score += 900;
                break;
            case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1:
                score += 800;
                break;
            case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2:
                score += 700;
                break;
            case Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN:
                score += 600;
                break;
            case Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED:
                score += 500;
                break;
            default:
                score += 400;
                break;
        }
        
        return score;
    }
    
    // 단어 목록 데이터를 담는 내부 클래스
    private static class WordListData {
        List<DicModel> allSubtitles;
        List<SubtitleWordListModel> allSubtitleWordList;
        Map<Integer, List<Integer>> wordToSubtitleMap;
    }
}
