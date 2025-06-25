package com.dalread.model;

import com.google.gson.annotations.SerializedName;

public class VocaBookNativeSpeaker extends VocaBook {

    @SerializedName("PARENT_WORDBOOK_ID")
    private int parentWordbookId;
    @SerializedName("VOCA_RECORDED_TOTAL")
    private int vocaRecordedTotal;

    public int getParentWordbookId() {
        return parentWordbookId;
    }

    public void setParentWordbookId(int parentWordbookId) {
        this.parentWordbookId = parentWordbookId;
    }

    public int getVocaRecordedTotal() {
        return vocaRecordedTotal;
    }

    public void setVocaRecordedTotal(int vocaRecordedTotal) {
        this.vocaRecordedTotal = vocaRecordedTotal;
    }
}
