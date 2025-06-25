package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PlaylistSongModel extends RealmObject implements Parcelable {
    @PrimaryKey
    private long id;
    private long playListId;
    private String path;

    public PlaylistSongModel() {
    }

    public PlaylistSongModel(long id, long playListId, String path) {
        this.id = id;
        this.playListId = playListId;
        this.path = path;
    }

    protected PlaylistSongModel(Parcel in) {
        id = in.readLong();
        playListId = in.readLong();
        path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(playListId);
        dest.writeString(path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaylistSongModel> CREATOR = new Creator<PlaylistSongModel>() {
        @Override
        public PlaylistSongModel createFromParcel(Parcel in) {
            return new PlaylistSongModel(in);
        }

        @Override
        public PlaylistSongModel[] newArray(int size) {
            return new PlaylistSongModel[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPlayListId() {
        return playListId;
    }

    public void setPlayListId(long playListId) {
        this.playListId = playListId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
