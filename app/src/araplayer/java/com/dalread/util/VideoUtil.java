package com.dalread.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.VideoModelQuery;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wseemann.media.FFmpegMediaMetadataRetriever;

//Dalnim Add
public class VideoUtil {

    private static final String TAG = "VideoUtil";

    //Dalnim Add
    public static long setVideoDurationWhenZero(VideoModel videoModel) {
        String videoPath = videoModel.getPath();
        long duration = videoModel.getDuration();
        if (duration == 0) {
            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            long durationRefreshed = StorageUtil.getDuration(mmr, videoPath);
            if (durationRefreshed > 0) {
                videoModel.setDuration(durationRefreshed);
                VideoModelQuery.updateVideoDuration(Voca.getRealm(), videoPath, durationRefreshed);
                duration = durationRefreshed;
            }
            mmr.release();
        }
        return duration;
    }

    public static long setVideoDurationFromMediaMetadataRetriever(VideoModel videoModel, MediaMetadataRetriever mmr) {
        String videoPath = videoModel.getPath();
        long duration = videoModel.getDuration();
        if (duration == 0) {
            long durationRefreshed = StorageUtil.getDuration(mmr);
            if (durationRefreshed > 0) {
                videoModel.setDuration(durationRefreshed);
                VideoModelQuery.updateVideoDuration(Voca.getRealm(), videoPath, durationRefreshed);
                duration = durationRefreshed;
            }
        }
        return duration;
    }

    public static String getLastPlayedPath(Context context) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);

        String lastPath = "";
        if (sharedPreferences.getShowNormalVideoFileList()) {
            lastPath = sharedPreferences.getPlayerLastPlayed();
        } else {
            lastPath = sharedPreferences.getPlayerLastPlayedHidedVideo();
        }
        if (Utils.isEmpty(lastPath))
            lastPath = "";

        return lastPath;
    }

    public static void setLastPlayedPath(Context context, PlayerFileModel playerFileModel) {
        String path = playerFileModel.getPath();
        if (Utils.isEmpty(path) || playerFileModel.getServerModel() != null)
            return;

        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        if (sharedPreferences.getShowNormalVideoFileList()) {
            sharedPreferences.setPlayerLastPlayed(path);
        } else {
            sharedPreferences.setPlayerLastPlayedHidedVideo(path);
        }
    }

    public static List<PlayerFileModel> setVideoDurationWhenZero(List<PlayerFileModel> list) {
        return list.stream().map(e -> {
            if (VideoUtil.isNeedToSetVideoDurationWhenZero(e)) {
                VideoUtil.setVideoDurationWhenZero(e.getVideoModel());
            }
            return e;
        }).collect(Collectors.toList());
    }
    public static boolean isNeedToSetVideoDurationWhenZero(PlayerFileModel e) {
        return (e.isVideo() || e.isMusic()) && e.getDuration() <= 0;
    }

    public static MediaMetadataRetriever getMediaMetadataRetrieverFromNetworkVideo(PlayerFileModel playerFileModel) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String videoPath = playerFileModel.getServerModel().getPath(playerFileModel.getPath());
        String auth = NetworkUtil.generateBasicAuth(playerFileModel.getServerModel().getAccountPassword());
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", NetworkUtil.getDefaultUserAgent());
        headers.put("Authorization", auth);
        mmr.setDataSource(videoPath, headers);
        return mmr;
    }
}
