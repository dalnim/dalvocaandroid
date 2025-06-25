package com.dalread.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessageResponse {

    @SerializedName("VOCA_IDs")
    private String vocaIds;

    public String getVocaIds() {
        return vocaIds;
    }

    public void setVocaIds(String vocaIds) {
        this.vocaIds = vocaIds;
    }
}
