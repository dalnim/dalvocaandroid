package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.util.Constant;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class DownloadModel extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String name;
    private String path;
    private int status;
    private long idServer;
    private long currentSize;
    private long size;
    private long createDate = System.currentTimeMillis();
    @Ignore
    private int position = -1;
    @Ignore
    private ServerModel serverModel;

    public DownloadModel() {
        this(Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.PLAYER.SERVER.DOWNLOAD.STATUS.WAIT, 0);
    }

    public DownloadModel(String id, String name, String path, int status, long idServer) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.path = path;
        this.idServer = idServer;
    }

    public DownloadModel(DownloadModel s) {
        this.id = s.getId();
        this.name = s.getName();
        this.status = s.getStatus();
        this.path = s.getPath();
        this.idServer = s.getIdServer();
        this.currentSize = s.getCurrentSize();
        this.position = s.getPosition();
        this.size = s.getSize();
        this.serverModel = s.getServerModel();
        this.createDate = s.getCreateDate();
    }

    protected DownloadModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        path = in.readString();
        status = in.readInt();
        idServer = in.readLong();
        currentSize = in.readLong();
        position = in.readInt();
        size = in.readLong();
        serverModel = in.readParcelable(ServerModel.class.getClassLoader());
        createDate = in.readLong();
    }

    public static final Creator<DownloadModel> CREATOR = new Creator<DownloadModel>() {
        @Override
        public DownloadModel createFromParcel(Parcel in) {
            return new DownloadModel(in);
        }

        @Override
        public DownloadModel[] newArray(int size) {
            return new DownloadModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(boolean isDownloading) {
        setStatus(isDownloading ? Constant.PLAYER.SERVER.DOWNLOAD.STATUS.WAIT : Constant.PLAYER.SERVER.DOWNLOAD.STATUS.DOWNLOAD);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getIdServer() {
        return idServer;
    }

    public void setIdServer(long idServer) {
        this.idServer = idServer;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        if (currentSize <= size) {
            this.currentSize = currentSize;
        }
    }

    public boolean isCompleted() {
        return status == Constant.PLAYER.SERVER.DOWNLOAD.STATUS.COMPLETE;
    }

    public boolean isPause() {
        return status == Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE;
    }

    public boolean isDownload() {
        return status == Constant.PLAYER.SERVER.DOWNLOAD.STATUS.DOWNLOAD;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "DownloadModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", status=" + status +
                ", idServer=" + idServer +
                ", currentSize=" + currentSize +
                ", size=" + size +
                ", createDate='" + createDate + '\'' +
                ", position=" + position +
                ", serverModel=" + serverModel +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeInt(status);
        dest.writeLong(idServer);
        dest.writeInt(position);
        dest.writeLong(currentSize);
        dest.writeLong(size);
        dest.writeParcelable(serverModel, flags);
        dest.writeLong(createDate);
    }

    public ServerModel getServerModel() {
        return serverModel;
    }

    public void setServerModel(ServerModel serverModel) {
        this.serverModel = serverModel;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
