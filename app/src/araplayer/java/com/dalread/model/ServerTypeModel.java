package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.util.Constant;

import java.io.Serializable;

public class ServerTypeModel implements Parcelable {
    private String name;
    private int type;

    public ServerTypeModel() {
        this(Constant.BASE_BLANK, Constant.PLAYER.SERVER.TYPE.NONE);
    }

    public ServerTypeModel(String name, int type) {
        this.name = name;
        this.type = type;
    }

    protected ServerTypeModel(Parcel in) {
        name = in.readString();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServerTypeModel> CREATOR = new Creator<ServerTypeModel>() {
        @Override
        public ServerTypeModel createFromParcel(Parcel in) {
            return new ServerTypeModel(in);
        }

        @Override
        public ServerTypeModel[] newArray(int size) {
            return new ServerTypeModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ServerModel{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
