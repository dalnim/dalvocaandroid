package com.dalread.model.roleplaying;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Mission implements Serializable {

    @SerializedName("MISSON_ID")
    private int missionId;
    @SerializedName("MESSAGE")
    private String message;

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
