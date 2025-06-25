package com.dalread.activity;

import android.view.View;

import com.dalread.R;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.database.VideoSeasonModelQuery;
import com.dalread.listener.OnClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoSeasonModel;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.List;
import java.util.stream.Collectors;

public class MainPlayerVideoFragment extends MainPlayerMediaFragment implements OnClickListener, OnAsyncTaskListener {
    protected boolean isNavTabMedia() {
        return activity.currentBottomNavigationId == R.id.nav_video;
    }
    protected int getAppMediaType() {
        return Constant.AppMediaType.VIDEO;
    }

    protected void finishLoadData(int type, Object resultData, Object data) {
        super.finishLoadData(type, resultData, data);

        removeSeasonInFileList();
        AddAdsSeasonLanguageFolderInFileList();
        addAdsBannerInFileList(fileList);
        updateVisibilityVideoListAndMessage();
        dalPlayerAdapter.setData(fileList);
    }

    protected void removeSeasonInFileList() {
        //Don't show Season video file at first in Normal video file list.
        if(sharedPreferences.getShowNormalVideoFileList()) {
            fileList = fileList.stream()
                    .filter(e -> !e.getVideoModel().isSeason())
                    .collect(Collectors.toList());
        }
    }

    protected void AddAdsSeasonLanguageFolderInFileList() {
        if (isNavTabMedia() && (sharedPreferences.getShowNormalVideoFileList())) {
            List<VideoSeasonModel> videoSeriesModelList = VideoSeasonModelQuery.getAllUniqueSeries(Voca.getRealm());
            addLanguageFolderAndSeasonInFileList(fileList, videoSeriesModelList);
            addSeasonItemInVideoFileList(fileList, videoSeriesModelList);
        }
//        addAdsBannerInFileList(fileList);
    }

    private void addLanguageFolderAndSeasonInFileList(List<PlayerFileModel> fileList, List<VideoSeasonModel> videoSeasonList) {
        if (!Utils.isEmpty(videoSeasonList))
            fileList.add(0, new PlayerFileModel(PlayerFileModel.FileType.SEASON_LIST, videoSeasonList));

        addLanguageFolderInFileList(fileList);
    }

    private void addSeasonItemInVideoFileList(List<PlayerFileModel> fileList, List<VideoSeasonModel> videoSeriesModelList) {
        //Don't delete this code. May use this later. Season item in the video file list is not finished yet.
//        int indexInFileList = 0;
//        for (int i = 0; i < fileList.size(); i++) {
//            PlayerFileModel playerFileModel = fileList.get(i);
//            if (playerFileModel.isAdsBanner() || playerFileModel.isLanguageFolder() || playerFileModel.isSeasonList())
//                continue;
//
//            String videoFileName = playerFileModel.getName().toLowerCase();
//            for (int j = indexInFileList; j < videoSeriesModelList.size(); j++) {
//                VideoSeasonModel VideoSeasonModel = videoSeriesModelList.get(j);
//                String videoFileNameInSeason = VideoSeasonModel.getSeasonNameVideoFile().toLowerCase();
//                int sortFileName = videoFileName.compareTo(videoFileNameInSeason);
//                if (sortFileName > 0) {
//                    fileList.add(i + 1, new PlayerFileModel(requireContext(), VideoSeasonModel));
//                    indexInFileList++;
//                }
//            }
//
//        }
//        return;
    }

    private void updateVisibilityVideoListAndMessage() {
        if (fileList.size() > 0) {
            binding.rvContent.setVisibility(View.VISIBLE);
            binding.tvNoDataVideoFiles.setVisibility(View.GONE);
        } else {
            binding.rvContent.setVisibility(View.GONE);
            if (isNavTabMedia()) {
                binding.tvNoDataVideoFiles.setVisibility(View.VISIBLE);
                updateNoVideoFileListMessageForNormalOrHiddenVideos();
            } else {
                binding.tvNoDataVideoFiles.setText(R.string.cant_connect_the_server);
            }
        }
    }

    private void updateNoVideoFileListMessageForNormalOrHiddenVideos() {
        if (sharedPreferences.getShowNormalVideoFileList()) {
            binding.tvNoDataVideoFiles.setText(R.string.no_data_video_files);
        } else {
            binding.tvNoDataVideoFiles.setText(R.string.no_data_hidden_video_files);
        }
    }
}