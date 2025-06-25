package com.dalread.model;

import com.google.gson.annotations.SerializedName;

public class VocaCountInBook {

    @SerializedName("VOCA_COUNT")
    private int allWordCount;
    @SerializedName("KNOWN_VOCA_COUNT")
    private int knownWordCount;

    public int getAllWordCount() {
        return allWordCount;
    }

    public void setAllWordCount(int allWordCount) {
        this.allWordCount = allWordCount;
    }

    public int getKnownWordCount() {
        return knownWordCount;
    }

    public void setKnownWordCount(int knownWordCount) {
        this.knownWordCount = knownWordCount;
    }
}
