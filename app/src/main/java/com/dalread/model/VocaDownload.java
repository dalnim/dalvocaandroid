package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
//This is Recorded VOCE FILE info
public class VocaDownload extends RealmObject {
    @PrimaryKey
    @SerializedName("NAME")
    private String name;
    @SerializedName("VERSION")
    private int version;
    @SerializedName("FILESIZE")
    private int fileSize; //Don't trust this, it can be wrong.(서버에서 다운받을때랑 파일싸이즈 가져올때 시점이 달라서 값이 제대로 안들어갔을수도 있다)
    @Ignore
    @SerializedName("REPLACEMENT_NAME")
    private String replacementName;
    @Ignore
    @SerializedName("REPLACEMENT_VERSION")
    private int replacementVersion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getReplacementName() {
        return replacementName;
    }

    public void setReplacementName(String replacementName) {
        this.replacementName = replacementName;
    }

    public int getReplacementVersion() {
        return replacementVersion;
    }

    public void setReplacementVersion(int replacementVersion) {
        this.replacementVersion = replacementVersion;
    }
}
