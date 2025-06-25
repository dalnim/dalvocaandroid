package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.util.Constant;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class VideoSeasonModel extends RealmObject implements Parcelable {
    //Ex) File name      : Doctor Who.S01E01~{KiNg}.mp4
    @PrimaryKey
    private String seasonNameVideoFile = ""; //VideoModel's Foreign Key
                                //Ex) : Doctor Who.
    private String tmdbVideoName = "";    // Default is same as seasonNameVideoFile, but if TMDB info is selected then use TMDB's name.
                            //Ex) Default           : Doctor Who.
                            //Ex) TMEB's data       : Doctor Who

    private String tmdbPosterPath;
    private String tmdbSeasonPosterPath;
    private int seasonNumber = -1;
    private int episodeVideoFileCount = 0;
    private int tmdbTvShowId = -1;
    private int tmdbSeasonId = -1;
    private int trash = Constant.INT_BOOLEAN.FASLE;  //If it's false, delete the record after refresh the video file list. (It's to delete the video record in the Realm for the remove video files on the phone).

    public VideoSeasonModel() {

    }

    public VideoSeasonModel(VideoSeasonModel model) {
        if (model == null) return;
        this.seasonNameVideoFile = model.getSeasonNameVideoFile();
        this.tmdbVideoName = model.getTmdbVideoName();
        this.tmdbPosterPath = model.getTmdbPosterPath();
        this.tmdbSeasonPosterPath = model.getTmdbSeasonPosterPath();
        this.seasonNumber = model.getSeasonNumber();
        this.episodeVideoFileCount = model.getEpisodeVideoFileCount();
        this.tmdbTvShowId = model.getTmdbTvShowId();
        this.tmdbSeasonId = model.getTmdbSeasonId();
        this.trash = model.getTrash();

    }

    protected VideoSeasonModel(Parcel in) {
        seasonNameVideoFile = in.readString();
        tmdbVideoName = in.readString();
        tmdbPosterPath = in.readString();
        tmdbSeasonPosterPath = in.readString();
        seasonNumber = in.readInt();
        episodeVideoFileCount = in.readInt();
        tmdbTvShowId = in.readInt();
        tmdbSeasonId = in.readInt();
        trash = in.readInt();
    }

    public static final Creator<VideoSeasonModel> CREATOR = new Creator<VideoSeasonModel>() {
        @Override
        public VideoSeasonModel createFromParcel(Parcel in) {
            return new VideoSeasonModel(in);
        }

        @Override
        public VideoSeasonModel[] newArray(int size) {
            return new VideoSeasonModel[size];
        }
    };

    public String getSeasonNameVideoFile() {
        return seasonNameVideoFile;
    }

    public void setSeasonNameVideoFile(String seasonNameVideoFile) {
        this.seasonNameVideoFile = seasonNameVideoFile;
    }

    public String getTmdbVideoName() {
        return tmdbVideoName;
    }

    public void setTmdbVideoName(String tmdbVideoName) {
        this.tmdbVideoName = tmdbVideoName;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getEpisodeVideoFileCount() {
        return episodeVideoFileCount;
    }

    public void setEpisodeVideoFileCount(int episodeVideoFileCount) {
        this.episodeVideoFileCount = episodeVideoFileCount;
    }

    public int getTmdbTvShowId() {
        return tmdbTvShowId;
    }

    public void setTmdbTvShowId(int tmdbTvShowId) {
        this.tmdbTvShowId = tmdbTvShowId;
    }

    public int getTmdbSeasonId() {
        return tmdbSeasonId;
    }

    public void setTmdbSeasonId(int tmdbSeasonId) {
        this.tmdbSeasonId = tmdbSeasonId;
    }

    public String getTmdbPosterPath() {
        return tmdbPosterPath;
    }

    public void setTmdbPosterPath(String tmdbPosterPath) {
        this.tmdbPosterPath = tmdbPosterPath;
    }

    public String getTmdbSeasonPosterPath() {
        return tmdbSeasonPosterPath;
    }

    public void setTmdbSeasonPosterPath(String tmdbSeasonPosterPath) {
        this.tmdbSeasonPosterPath = tmdbSeasonPosterPath;
    }

    public int getTrash() {
        return trash;
    }

    public void setTrash(int trash) {
        this.trash = trash;
    }

    @Override
    public String toString() {
        return "VideoModel{" +
                ", seasonNameVideoFile='" + seasonNameVideoFile + '\'' +
                ", seasonNameTmdb='" + tmdbVideoName + '\'' +
                ", tmdbPosterPath='" + tmdbPosterPath + '\'' +
                ", tmdbSeasonPosterPath='" + tmdbSeasonPosterPath + '\'' +
                ", seasonNumber='" + seasonNumber + '\'' +
                ", episodeVideoFileCount='" + episodeVideoFileCount + '\'' +
                ", tmdbSeriesId='" + tmdbTvShowId + '\'' +
                ", tmdbSeasonId='" + tmdbSeasonId + '\'' +
                ", trash='" + trash +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(seasonNameVideoFile);
        dest.writeString(tmdbVideoName);
        dest.writeString(tmdbPosterPath);
        dest.writeString(tmdbSeasonPosterPath);
        dest.writeInt(seasonNumber);
        dest.writeInt(episodeVideoFileCount);
        dest.writeInt(tmdbTvShowId);
        dest.writeInt(tmdbSeasonId);
        dest.writeInt(trash);
    }
}
