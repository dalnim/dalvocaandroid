package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VocaRecordListForBook {

    @SerializedName("VOCA_TOTAL")
    private int totalCount;
    @SerializedName("VOCA_RECORDED_TOTAL")
    private int recordedCount;
    @SerializedName("VOCA_LIST")
    private ArrayList<VocaInBook> vocas;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getRecordedCount() {
        return recordedCount;
    }

    public void setRecordedCount(int recordedCount) {
        this.recordedCount = recordedCount;
    }

    public ArrayList<VocaInBook> getVocas() {
        if (vocas == null) {
            vocas = new ArrayList<>();
        }
        return vocas;
    }

    public void setVocas(ArrayList<VocaInBook> vocas) {
        this.vocas = vocas;
    }
}
