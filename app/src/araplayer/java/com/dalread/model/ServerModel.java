package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.util.Constant;
import com.dalread.util.NetworkUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ServerModel extends RealmObject implements Parcelable {
    @PrimaryKey
    private long id;
    private String title;
    private String host;
    private String account;
    private String password;
    private String port;
    private int type;


    public ServerModel() {
        this(System.currentTimeMillis(), Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.PLAYER.SERVER.TYPE.NONE);
    }

    public ServerModel(long id, String title, String host, String account, String password, String port, int type) {
        this.id = id;
        this.title = title;
        this.host = host;
        this.account = account;
        this.password = password;
        this.port = port;
        this.type = type;
    }

    public ServerModel(ServerModel s) {
        this.id = s.getId();
        this.title = s.getTitle();
        this.host = s.getHost();
        this.account = s.getAccount();
        this.password = s.getPassword();
        this.port = s.getPort();
        this.type = s.getType();
    }

    protected ServerModel(Parcel in) {
        id = in.readLong();
        title = in.readString();
        host = in.readString();
        account = in.readString();
        password = in.readString();
        port = in.readString();
        type = in.readInt();
    }

    public static final Creator<ServerModel> CREATOR = new Creator<ServerModel>() {
        @Override
        public ServerModel createFromParcel(Parcel in) {
            return new ServerModel(in);
        }

        @Override
        public ServerModel[] newArray(int size) {
            return new ServerModel[size];
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return NetworkUtil.getProtocol(type) + host + ":" + port;
    }

    public String getPath(String path) {
        return getPath() + path;
    }

    public String getAuthPath() {
        return NetworkUtil.getProtocol(type) + account + ":" + password + "@" + host + ":" + port;
    }

    public String getAuthPath(String path) {
        return getAuthPath() + path;
    }

    public boolean isFTP() {
        return type == Constant.PLAYER.SERVER.TYPE.FTP || type == Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD;
    }

    public boolean isWebDAV() {
        return type == Constant.PLAYER.SERVER.TYPE.WEBDAV;
    }

    public String getAccountPassword() {
        return account + ":" + password;
    }

    @Override
    public String toString() {
        return "ServerModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", host='" + host + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", port='" + port + '\'' +
                ", type=" + type +
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
        dest.writeString(host);
        dest.writeString(account);
        dest.writeString(password);
        dest.writeString(port);
        dest.writeInt(type);
    }
}
