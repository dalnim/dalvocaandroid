package com.dalread.helper;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.dalread.activity.MultiplePlayerFragment;
import com.dalread.database.sqlite.model.MultiPlayerVideoModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MultiPlayerFileListHelper {
    public static List<String> convertFileListToFilePathList(List<PlayerFileModel> currentFilesList) {
        List<String> currentVideoFilePathList = new ArrayList<>();
        for (PlayerFileModel playerFileModel : currentFilesList) {
            if (!Utils.isEmpty(playerFileModel.getPath())) {
                currentVideoFilePathList.add(playerFileModel.getPath());
            }
        }
        return currentVideoFilePathList;
    }

    public static List<String> getCurrentVideoFilePathListFromModel(MultiPlayerVideoModel model, List<MultiplePlayerFragment> fragmentList ) {
        List<String> currentVideoFilePathList = new ArrayList<>();
        for(MultiplePlayerFragment fragment : fragmentList) {
            //원래는 선택한 화면의 비디오는 그냥 쓸려고 했는데, 가끔 화면에 로드한 비디오와 화면동기화가 안될때가 있어서 전부 다함.
            if (fragment.getModel().getSCREEN_ID() == model.getSCREEN_ID()) {
                currentVideoFilePathList = fragment.getCurrentVideoFilePathList();
            }
        }
        return currentVideoFilePathList;
    }

    public static List<String> getCurrentVideoFilesFromIntent(@NonNull Intent data) {
        List<String> currentVideoFilePathList = new ArrayList<>();
        if (data.hasExtra(Constant.BUNDLE.KEY_ALL_VIDEO_FILES_IN_LIST)) {
            currentVideoFilePathList = (ArrayList<String>) data.getSerializableExtra(Constant.BUNDLE.KEY_ALL_VIDEO_FILES_IN_LIST);
//            List<PlayerFileModel> currentFilesList = (ArrayList<PlayerFileModel>) data.getSerializableExtra(Constant.BUNDLE.KEY_ALL_VIDEO_FILES_IN_LIST);
//            currentVideoFilePathList = MultiPlayerFileListHelper.convertFileListToFilePathList(currentFilesList);
        }
        return currentVideoFilePathList;
    }
}
