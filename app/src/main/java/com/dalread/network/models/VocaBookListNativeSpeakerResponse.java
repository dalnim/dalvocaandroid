package com.dalread.network.models;

import com.dalread.model.VocaBookNativeSpeaker;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VocaBookListNativeSpeakerResponse {

    @SerializedName("VOCA_RECORDED_TOTAL")
    private int vocaRecordedTotal;
    @SerializedName("VOCA_LIST")
    private ArrayList<VocaBookNativeSpeaker> vocas;

    public int getVocaRecordedTotal() {
        return vocaRecordedTotal;
    }

    public void setVocaRecordedTotal(int vocaRecordedTotal) {
        this.vocaRecordedTotal = vocaRecordedTotal;
    }

    public ArrayList<VocaBookNativeSpeaker> getVocas() {
        return vocas;
    }

    public void setVocas(ArrayList<VocaBookNativeSpeaker> vocas) {
        this.vocas = vocas;
    }
}
