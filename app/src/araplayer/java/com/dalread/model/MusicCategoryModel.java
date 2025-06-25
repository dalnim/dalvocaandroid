package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicCategoryModel implements Parcelable {
    private long id;
    private String title;



    public MusicCategoryModel(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public MusicCategoryModel(MusicCategoryModel s) {
        this.id = s.getId();
        this.title = s.getTitle();
    }

    protected MusicCategoryModel(Parcel in) {
        id = in.readLong();
        title = in.readString();
    }

    public static final Creator<MusicCategoryModel> CREATOR = new Creator<MusicCategoryModel>() {
        @Override
        public MusicCategoryModel createFromParcel(Parcel in) {
            return new MusicCategoryModel(in);
        }

        @Override
        public MusicCategoryModel[] newArray(int size) {
            return new MusicCategoryModel[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    @Override
    public String toString() {
        return "ServerModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
    }

    public boolean isPlaylist() {
        return id == 1;
    }

    public boolean isSongs() {
        return id == 2;
    }

    public boolean isVideos() {
        return id == 3;
    }

    public boolean isArtists() {
        return id == 4;
    }

    public boolean isAlbums() {
        return id == 5;
    }

    public boolean isGenres() {
        return id == 6;
    }

    public boolean isFolders() {
        return id == 7;
    }

    public boolean isTestList() {
        return id == 99;
    }

}
