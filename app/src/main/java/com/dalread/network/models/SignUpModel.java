package com.dalread.network.models;

import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

/**
 * Created by JetVHS on 2/26/2017.
 */
public class SignUpModel {
    @SerializedName("UID")
    private String uid;
    @SerializedName("EMAIL")
    private String email;
    @SerializedName("POINT_READING")
    private String pointReading;
    @SerializedName("NAME")
    private String name;

    public SignUpModel() {
        this(Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK);
    }

    public SignUpModel(String uid, String email, String pointReading, String name) {
        this.uid = uid;
        this.email = email;
        this.pointReading = pointReading;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPointReading() {
        return pointReading;
    }

    public void setPointReading(String pointReading) {
        this.pointReading = pointReading;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SignUpModel{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", pointReading='" + pointReading + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
