package com.dalread.util;

import com.dalread.base.BasePlayerActivity;
import com.dalread.database.VideoSeasonModelQuery;
import com.dalread.database.sqlite.model.MultiPlayerVideoAbRepeatModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.SubtitleMeaningModel;
import com.dalread.model.VideoSeasonModel;

import java.util.ArrayList;
import java.util.List;

public class SortUtil {
    public static ArrayList<SubtitleMeaningModel> sortSubtitleMeaningModel(BasePlayerActivity activity, String listId) {
        final List<SubtitleMeaningModel> tmp = activity.getSubDatabase().getSubtitleMeaning(listId);
        final ArrayList<SubtitleMeaningModel> listTop = new ArrayList<>();
        final ArrayList<SubtitleMeaningModel> listBottom = new ArrayList<>();
        for(SubtitleMeaningModel model : tmp) {
            if (model.getDicModel().getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || model.getDicModel().getVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                listTop.add(model);
            } else {
                listBottom.add(model);
            }
        }
        final ArrayList<SubtitleMeaningModel> list = new ArrayList<>();
        list.addAll(listTop);
        list.addAll(listBottom);
        return list;
    }

    public static List<PlayerFileModel> sortPlayerFileModelFolderTop(List<PlayerFileModel> data) {
        if (data == null || data.isEmpty()) return new ArrayList<>();

        final List<PlayerFileModel> listTopDirectoryBlack = new ArrayList<>();
        final List<PlayerFileModel> listTopDirectory = new ArrayList<>();
        final List<PlayerFileModel> listFilesBookmarked = new ArrayList<>();
        final List<PlayerFileModel> listFilesNew = new ArrayList<>();
        final List<PlayerFileModel> listFilesNormal = new ArrayList<>();
        for(PlayerFileModel playerFileModel : data) {
            if (playerFileModel.isDirectoryBlack()) {
                listTopDirectoryBlack.add(playerFileModel);
            } else if (playerFileModel.isDirectory()) {
                listTopDirectory.add(playerFileModel);
            } else {
                if (playerFileModel.getVideoModel().isBookmark()) {
                    listFilesBookmarked.add(playerFileModel);
                } else if (playerFileModel.getVideoModel().isNewFile()) {
                    listFilesNew.add(playerFileModel);
                } else {
                    listFilesNormal.add(playerFileModel);
                }
            }
        }
        final List<PlayerFileModel> list = new ArrayList<>();
        list.addAll(listTopDirectoryBlack);
        list.addAll(listTopDirectory);
        list.addAll(listFilesBookmarked);
        list.addAll(listFilesNew);
        list.addAll(listFilesNormal);
        return list;
    }

    public static List<VideoSeasonModel> getSeriesFromVideoList(List<PlayerFileModel> listVideo) {
        return VideoSeasonModelQuery.getAllUniqueSeries(Voca.getRealm());
    }

    // AB_A 기준으로 오름차순 정렬
    public static void sortAbRepeatByAbA(List<MultiPlayerVideoAbRepeatModel> abRepeatModelList) {
        abRepeatModelList.sort((model1, model2) -> Long.compare(model1.getAB_A(), model2.getAB_A()));
    }
}
