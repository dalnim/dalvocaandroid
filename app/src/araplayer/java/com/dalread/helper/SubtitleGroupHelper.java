package com.dalread.helper;

import android.text.TextUtils;

import com.dalread.base.BasePlayerActivity;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.database.sqlite.model.SubtitleWordListModel;
import com.dalread.model.RubyTextModel;
import com.dalread.model.SubtitleHideModel;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.MergeUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.SubtitleContentUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubtitleGroupHelper {
    public enum TYPE {
        ALL_SUBTITLES,
        ALL_SUBTITLES_EXCEPT_AUTO_HIDED,
//        EXCEPTION_SUBTITELS,
        TO_DELETE_SUBTITLE,
        DIFFICULTY_SUBTITLE,
//        DIFFICULTY_WORD,
        SEARCH,
        RANGE,
    }
    private VocaKnowGroupSelect vocaKnowGroupSelect; //Will replace subtitleDifficultArray
    private List<DicModel> subtitleListTotal;
    private SubDatabase subDatabase;
    public Map<Integer, DicModel> mapSubtitleTotal; //Dalnim Add
    private SubtitleHideModel subtitleHideModel;
    public List<RubyTextModel> rubyTextModels;
    public BasePlayerActivity activity;
    private String searchValue;
    public SubtitleGroupHelper(List<DicModel> subtitleListTotal, SubDatabase subDatabase, SubtitleHideModel subtitleHideModel) {
        this.vocaKnowGroupSelect = new VocaKnowGroupSelect();
        this.subtitleListTotal = subtitleListTotal;
        this.subDatabase = subDatabase;
        this.subtitleHideModel = subtitleHideModel;
//        this.rubyTextModels = rubyTextModels;
        mapSubtitleTotal = subtitleListTotal.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
    }

    public void setSubtitleHideModel(SubtitleHideModel subtitleHideModel) {
        this.subtitleHideModel = subtitleHideModel;
    }

    public void setVocaKnowGroupSelect(VocaKnowGroupSelect vocaKnowGroupSelect) {
        this.vocaKnowGroupSelect = vocaKnowGroupSelect;
    }

    public List<DicModel> generateSubtitleDialogByType(TYPE subtitlePlayType) {
        TYPE type = subtitlePlayType;

//        if (type == TYPE.DIFFICULTY_SUBTITLE && !vocaKnowGroupSelect.isSelectedAnyVocaKnow()) {
//            type = TYPE.ALL_SUBTITLES;
//        }

        final ArrayList<DicModel> items = new ArrayList<>();
        switch (type) {
            case ALL_SUBTITLES:
                items.addAll(subtitleListTotal.stream()
                        .collect(Collectors.toList()));
                break;
            case ALL_SUBTITLES_EXCEPT_AUTO_HIDED:
                checkSubtitleToHide(subtitleListTotal, mapSubtitleTotal, subtitleHideModel);
                items.addAll(subtitleListTotal.stream()
                        .filter(e -> e.isShowUsed())
                        .collect(Collectors.toList()));
                subDatabase.updateUsedSubtitleHideAuto(subtitleListTotal);
                break;
//            case EXCEPTION_SUBTITELS:
//                checkSubtitleToHide(subtitleListTotal, mapSubtitleTotal, subtitleHideModel);
//                items.addAll(subtitleListTotal.stream()
//                        .filter(e -> !e.isShowUsed())
//                        .collect(Collectors.toList()));
//                subDatabase.updateUsedSubtitleHideAuto(subtitleListTotal);
//                break;
            case TO_DELETE_SUBTITLE:
                checkSubtitleToDelete(subtitleListTotal, mapSubtitleTotal, subtitleHideModel);
                items.addAll(subtitleListTotal.stream()
                        .filter(e -> !e.isShowUsed())
                        .collect(Collectors.toList()));
                subDatabase.updateUsedSubtitleHideAuto(subtitleListTotal);
                break;
            case DIFFICULTY_SUBTITLE:
                List<DicModel> difficultDicModelList = getSubtitleDialogListByDifficult(vocaKnowGroupSelect, subtitleListTotal);
                MergeUtil.generateMeaning(difficultDicModelList, rubyTextModels);
                items.addAll(difficultDicModelList.stream().
                        sorted(Comparator.comparingLong(DicModel::getStartTime)).
                        collect(Collectors.toList()));
                break;
//            case DIFFICULTY_WORD:
//                List<DicModel> dicModelDifficultyWords = new ArrayList<>();
//                dicModelDifficultyWords.addAll(subDatabase.getWordDialogListByDifficult(vocaKnowGroupSelect));
//                MergeUtil.generateMeaning(dicModelDifficultyWords, rubyTextModels);
//                mapRecordedPathForSubtitleList(dicModelDifficultyWords);
//                items.addAll(dicModelDifficultyWords);
//                break;
            case SEARCH:
                items.addAll(generaSearchSubtitleDialog(subtitleListTotal));
                break;
        }

        return items;
    }
    public List<DicModel> generaSearchSubtitleDialog(List<DicModel> totalData) {
        if (Utils.isEmpty(searchValue) || totalData == null || totalData.isEmpty())
            return totalData;
        final ArrayList<DicModel> temp = new ArrayList<>();
        final int searchType = SubtitleContentUtil.getSearchType(searchValue);
        final int searchIn = SubtitleContentUtil.getValueSearchIn(true, true);
        DLog.d("generaSearchSubtitleDialog", "searchType=" + searchType + " - searchIn=" + searchIn);
        for (DicModel model : totalData) {
            if (SubtitleContentUtil.checkSearchSubtitleDialog(searchType, searchIn, model, searchValue)) {
                temp.add(model);
            }
        }
        return temp;
    }

    public List<DicModel> getSubtitleDialogListByDifficult(VocaKnowGroupSelect vocaKnowGroupSelect, List<DicModel> totalData) {
        final ArrayList<DicModel> temp = new ArrayList<>();
        for (DicModel model : totalData) {
            if ((vocaKnowGroupSelect.isNotRated()) && (BaseVocaKnow.isNotRated(model))) {
                temp.add(model);
            } else if ((vocaKnowGroupSelect.isAmki1st()) && (BaseVocaKnow.isAmkiGrade1(model))) {
                temp.add(model);
            } else if ((vocaKnowGroupSelect.isAmki2nd()) && (BaseVocaKnow.isAmkiGrade2(model))) {
                temp.add(model);
            } else if ((vocaKnowGroupSelect.isUnknown()) && (BaseVocaKnow.isUnknown(model))) {
                temp.add(model);
            } else if ((vocaKnowGroupSelect.isKnown()) && (BaseVocaKnow.isKnown(model))) {
                temp.add(model);
            } else if ((vocaKnowGroupSelect.isDifficultPronunciation()) && (BaseVocaKnow.isUnknownPronounceWhenKnownWord(model))) {
                temp.add(model);
            } else if ((vocaKnowGroupSelect.isBookmarked()) && (model.isVIBookmark())){
                temp.add(model);
            }
        }
        return temp;
    }
    private void mapRecordedPathForSubtitleList(List<DicModel> subtitleList) {
        subtitleList.forEach(item-> {
            addRecordedFileInDicModel(item);
        });
    }
    private void addRecordedFileInDicModel(DicModel d1) {
        File recordedFile = Voca.getVoiceFileOnLocal(activity, activity.playerFileModel, Voca.getVoiceFolderOnLocal(activity), d1.getVIPath(), d1.getVocaId());
        if (recordedFile.exists()) {
            d1.setRecordedPath(recordedFile.getPath());
        }
    }

    //Dalnim add
    public void checkSubtitleToHide(List<DicModel> subtitleListTotal, Map<Integer, DicModel> mapSubtitleTotal, SubtitleHideModel subtitleHideModel) {
        setToHideAutoForNormalCases(subtitleListTotal, subtitleHideModel);

        setToHideAutoForShorterLongerWordsCases(mapSubtitleTotal, subtitleHideModel);

        restoreHideManualAndHowForSubtitleToHide(subtitleListTotal, subtitleHideModel);

    }
    @NotNull
    private void setToHideAutoForNormalCases(List<DicModel> subtitleListTotal, SubtitleHideModel subtitleHideModel) {
//        int minCharCount = 3;
        for(DicModel dicModel : subtitleListTotal) {
            String dialog = dicModel.getVocaDisplay();
            //first reset all HIDE_AUTO to SHOW, then check dialogs and set HIDE_AUTO by the below IF statements
//            if (dicModel.getUsed() == Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO) {
            dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.SHOW);
//            }

            if (subtitleHideModel.isSelected_known_subtitles() && Voca.isVocaKnown(dicModel.getVocaKnow())) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_all_capitals() && StringUtils.isAllCapitalWords(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_including_url() && StringUtils.hasURL(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_paired_bracket() && StringUtils.hasPairedBracketPunctuation(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_music_symbol() && StringUtils.hasMusicText(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_no_study_lang_character() && StringUtils.isNonStudyLangOnly(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            }
        }
    }
    //If I made subtitles HIDE manually then always hide them.
    //If I turn on "But, Always show these subtitles (1, 2, X)" then show them always except hided subtitles manually
    private void restoreHideManualAndHowForSubtitleToHide(List<DicModel> subtitleListTotal, SubtitleHideModel subtitleHideModel) {
        for(DicModel dicModel : subtitleListTotal) {
            if (subtitleHideModel.isSelected_show_always_unknown_subtitles() && Voca.isVocaUnknownExceptNotRated(dicModel.getVocaKnow())) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.SHOW);
            }
        }
    }

    private void setToHideAutoForShorterLongerWordsCases(Map<Integer, DicModel> mapSubtitleTotal, SubtitleHideModel subtitleHideModel) {
        Map<String, DicModel> mapDicModelWordList = subDatabase.getDicModelByKnownWord().stream().collect(Collectors.toMap(e -> e.getVocaType() + "_" + e.getVocaId(), e -> e));

        if (subtitleHideModel.isSelected_shorter_3_known_words()) {
            int wordCountInSubtile = Constant.PLAYER.DEFAULT_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NUMBER_DEFAULT;
            List<SubtitleWordListModel> subtitleWordListModelList = getSubtitleWordListModels(subDatabase.getSubtitleWordListByWordCountIsShorterThan(wordCountInSubtile));
            setToHideAutoForShorterCase(mapSubtitleTotal, mapDicModelWordList, subtitleWordListModelList);
        }

        if (subtitleHideModel.isSelected_longer_20_words()) {
            int wordCountInSubtile = Constant.PLAYER.DEFAULT_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER_DEFAULT;
            List<SubtitleWordListModel> subtitleWordListModelList = getSubtitleWordListModels(subDatabase.getSubtitleWordListByWordCountIsLongerThan(wordCountInSubtile));
            setToHideAutoForLongerCase(mapSubtitleTotal, subtitleWordListModelList);
        }
    }

    private List<SubtitleWordListModel> getSubtitleWordListModels(List<Integer> list) {
        String strSubtitleIDWhereWordCount = list.stream()
                .map(e -> Integer.toString(e))
                .collect(Collectors.joining(","));
        return subDatabase.getSubtitleWordListOrderBySubtitleID(strSubtitleIDWhereWordCount);
    }

    private void setToHideAutoForLongerCase(Map<Integer, DicModel> mapSubtitleTotal, List<SubtitleWordListModel> subtitleWordListModelList) {
        int previousSubtitleID = -1;
        for (SubtitleWordListModel subtitleWordListModel : subtitleWordListModelList) {
            if (previousSubtitleID != -1 && previousSubtitleID != subtitleWordListModel.getSubtitleId()) {
//                setHideAutoat3KnownWordsInSubtitles(mapSubtitleTotal, previousSubtitleID, true);
                previousSubtitleID = subtitleWordListModel.getSubtitleId();
            }

        }
    }

    private void setToHideAutoForShorterCase(Map<Integer, DicModel> mapSubtitleTotal, Map<String, DicModel> mapDicModelWordList, List<SubtitleWordListModel> subtitleWordListModelList) {
        int previousSubtitleID = -1;
        boolean isAllWordsKnown = false;
        for (SubtitleWordListModel subtitleWordListModel : subtitleWordListModelList) {
            String vocaTypeId = subtitleWordListModel.getVocaType() + "_" + subtitleWordListModel.getVocaId();
            if (previousSubtitleID != subtitleWordListModel.getSubtitleId()) {
//                setHideAutoat3KnownWordsInSubtitles(mapSubtitleTotal, previousSubtitleID, isAllWordsKnown);
                isAllWordsKnown = true;
                previousSubtitleID = subtitleWordListModel.getSubtitleId();
            }

            if (mapDicModelWordList.containsKey(vocaTypeId)) {
                DicModel dicModel = mapDicModelWordList.get(vocaTypeId);
                isAllWordsKnown = isAllWordsKnown && dicModel.getVocaKnow() >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN ? true : false;
            } else {
                isAllWordsKnown = false;
            }
        }
        //Do it for the last item.
//        setHideAutoat3KnownWordsInSubtitles(mapSubtitleTotal, previousSubtitleID, isAllWordsKnown);
    }

    private void checkSubtitleToDelete(List<DicModel> subtitleListTotal, Map<Integer, DicModel> mapSubtitleTotal, SubtitleHideModel subtitleHideModel) {
        setToDeleteAutoForNormalCases(subtitleListTotal, subtitleHideModel);
    }
    @NotNull
    private void setToDeleteAutoForNormalCases(List<DicModel> subtitleListTotal, SubtitleHideModel subtitleHideModel) {
        for(DicModel dicModel : subtitleListTotal) {
            String dialog = dicModel.getVocaDisplay();
            dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.SHOW);

            if (subtitleHideModel.isSelected_1_word() && StringUtils.isOneWord(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_repeated_1_word() && StringUtils.isRepeatedOneWord(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_all_capitals() && StringUtils.isAllCapitalWords(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_including_url() && StringUtils.hasURL(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_paired_bracket() && StringUtils.hasPairedBracketPunctuation(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_music_symbol() && StringUtils.hasMusicText(dialog)) {
                dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
            } else if (subtitleHideModel.isSelected_no_study_lang_character() && StringUtils.isNonStudyLangOnly(dialog)) {
                if (!TextUtils.isEmpty(dicModel.getMeaning())) {
                    dicModel.setUsed(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
                }
            }
        }
    }
}
