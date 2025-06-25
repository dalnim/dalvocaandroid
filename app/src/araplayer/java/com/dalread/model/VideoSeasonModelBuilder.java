package com.dalread.model;

import com.dalread.util.Constant;

public final class VideoSeasonModelBuilder {
    //Ex) File name      : Doctor Who.S01E01~{KiNg}.mp4
    private String seasonNameVideoFile = ""; //VideoModel's Foreign Key
    //Ex) : Doctor Who.
    private String tmdbVideoName = "";    // Default is same as seasonNameVideoFile, but if TMDB info is selected then use TMDB's name.
    private String tmdbPosterPath;
    private String tmdbSeasonPosterPath;
    private int seasonNumber = -1;
    private int episodeVideoFileCount = 0;
    private int tmdbTvShowId = -1;
    private int tmdbSeasonId = -1;
    private int trash = Constant.INT_BOOLEAN.FASLE;  //If it's false, delete the record after refresh the video file list. (It's to delete the video record in the Realm for the remove video files on the phone).

    private VideoSeasonModelBuilder() {
    }

    public static VideoSeasonModelBuilder aVideoSeasonModel() {
        return new VideoSeasonModelBuilder();
    }

    public VideoSeasonModelBuilder withSeasonNameVideoFile(String seasonNameVideoFile) {
        this.seasonNameVideoFile = seasonNameVideoFile;
        return this;
    }

    public VideoSeasonModelBuilder withTmdbVideoName(String tmdbVideoName) {
        this.tmdbVideoName = tmdbVideoName;
        return this;
    }

    public VideoSeasonModelBuilder withTmdbPosterPath(String tmdbPosterPath) {
        this.tmdbPosterPath = tmdbPosterPath;
        return this;
    }

    public VideoSeasonModelBuilder withTmdbSeasonPosterPath(String tmdbSeasonPosterPath) {
        this.tmdbSeasonPosterPath = tmdbSeasonPosterPath;
        return this;
    }

    public VideoSeasonModelBuilder withSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
        return this;
    }

    public VideoSeasonModelBuilder withEpisodeVideoFileCount(int episodeVideoFileCount) {
        this.episodeVideoFileCount = episodeVideoFileCount;
        return this;
    }

    public VideoSeasonModelBuilder withTmdbTvShowId(int tmdbTvShowId) {
        this.tmdbTvShowId = tmdbTvShowId;
        return this;
    }

    public VideoSeasonModelBuilder withTmdbSeasonId(int tmdbSeasonId) {
        this.tmdbSeasonId = tmdbSeasonId;
        return this;
    }

    public VideoSeasonModelBuilder withTrash(int trash) {
        this.trash = trash;
        return this;
    }

    public VideoSeasonModel build() {
        VideoSeasonModel videoSeasonModel = new VideoSeasonModel();
        videoSeasonModel.setSeasonNameVideoFile(seasonNameVideoFile);
        videoSeasonModel.setTmdbVideoName(tmdbVideoName);
        videoSeasonModel.setTmdbPosterPath(tmdbPosterPath);
        videoSeasonModel.setTmdbSeasonPosterPath(tmdbSeasonPosterPath);
        videoSeasonModel.setSeasonNumber(seasonNumber);
        videoSeasonModel.setEpisodeVideoFileCount(episodeVideoFileCount);
        videoSeasonModel.setTmdbTvShowId(tmdbTvShowId);
        videoSeasonModel.setTmdbSeasonId(tmdbSeasonId);
        videoSeasonModel.setTrash(trash);
        return videoSeasonModel;
    }
}
