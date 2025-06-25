package com.dalread.network.models;

import com.dalread.model.VocaPractice;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VocaPracticeListResponse {

    @SerializedName("VOCA_COUNT")
    private int vocaCount;
    @SerializedName("KNOWN_VOCA_COUNT")
    private int knownVocaCount;
    @SerializedName("VOCA_LIST")
    private ArrayList<VocaPractice> vocas;

    public int getVocaCount() {
        return vocaCount;
    }

    public void setVocaCount(int vocaCount) {
        this.vocaCount = vocaCount;
    }

    public int getKnownVocaCount() {
        return knownVocaCount;
    }

    public void setKnownVocaCount(int knownVocaCount) {
        this.knownVocaCount = knownVocaCount;
    }

    public ArrayList<VocaPractice> getVocas() {
        return vocas;
    }

    public void setVocas(ArrayList<VocaPractice> vocas) {
        this.vocas = vocas;
    }
}
