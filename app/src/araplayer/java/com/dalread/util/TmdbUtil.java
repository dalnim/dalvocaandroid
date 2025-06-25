package com.dalread.util;

import com.dalread.model.VideoModel;
import com.dalread.model.VideoSeasonModel;

//Dalnim Add
public class TmdbUtil {

    private static final String TAG = "TmdbUtil";

    public static boolean isDisplayTvSeasonPoster(VideoSeasonModel videoSeasonModel) {
        if (videoSeasonModel == null)
            return false;

        String seasonPosterPath = videoSeasonModel.getTmdbSeasonPosterPath();
        if (!isHasPosterPath(seasonPosterPath))
            return false;

        String posterPath = videoSeasonModel.getTmdbPosterPath();
        if (isSamePosterAndSeasonPoster(posterPath, seasonPosterPath))
            return false;

        return true;
    }

    public static boolean isDisplayTvSeasonPoster(VideoModel videoModel) {
        if (videoModel == null)
            return false;

        String seasonPosterPath = videoModel.getTmdbSeasonPosterPath();
        if (!isHasPosterPath(seasonPosterPath))
            return false;

        String posterPath = videoModel.getTmdbPosterPath();
        if (isSamePosterAndSeasonPoster(posterPath, seasonPosterPath))
            return false;

        return true;
    }

    public static boolean isSamePosterAndSeasonPoster(String posterPath, String seasonPosterPath) {
        if (!Utils.isEmpty(posterPath) && (!Utils.isEmpty(posterPath))) {
            if (posterPath.equals(seasonPosterPath))
                return true;
        }
        return false;

    }
    public static boolean isHasPosterPath(String posterPath) {
        return !Utils.isEmpty(posterPath);
    }
}
