package com.dalread.util;

import com.dalread.database.VideoModelQuery;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;

import java.util.List;

public class PlayerFileModelUtil {
    public static void setNewFileToFalse(List<PlayerFileModel> list) {
        for(PlayerFileModel playerFileModel : list) {
            setNewFileToFalse(playerFileModel);
        }
    }
    public static void setNewFileToFalse(PlayerFileModel playerFileModel) {
        playerFileModel.getVideoModel().setNewFile(Constant.INT_BOOLEAN.FASLE);
        updateVideoModel(playerFileModel);
    }

    public static void updateVideoModel(PlayerFileModel playerFileModel) {
        if (playerFileModel != null) {
            VideoModel videoModel = playerFileModel.getVideoModel();
            if (videoModel != null) {
                updateVideoModel(videoModel);
            }
        }
    }

    private static void updateVideoModel(VideoModel videoModel) {
        VideoModelQuery.update(Voca.getRealm(), videoModel);
    }
}
