package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaConfused implements Serializable {

    @SerializedName("INDEX")
    private int index;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("MEANING_DETAILED")
    private String meaningDetailed;
    @SerializedName("MEANING_ENG")
    private String meaningEng;
    @SerializedName("MEANING_FOR_HIDE_ALL")
    private String meaningForHideAll;
    @SerializedName("MEANING_TTS")
    private String meaningTTS;
    @SerializedName("PRONOUNCE")
    private String pronounce;
    @SerializedName("VOCA")
    private String voca;
    @SerializedName("VOCAORI")
    private String vocaOri;
    @SerializedName("VOCA_DISPLAY")
    private String vocaDisplay;
    @SerializedName("VOCA_ID")
    private int vocaId;
    @SerializedName("VOCA_TTS")
    private String vocaTTS;
    @SerializedName("VOCA_TYPE")
    private int vocaType;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getMeaningDetailed() {
        return meaningDetailed;
    }

    public void setMeaningDetailed(String meaningDetailed) {
        this.meaningDetailed = meaningDetailed;
    }

    public String getMeaningEng() {
        return meaningEng;
    }

    public void setMeaningEng(String meaningEng) {
        this.meaningEng = meaningEng;
    }

    public String getMeaningForHideAll() {
        return meaningForHideAll;
    }

    public void setMeaningForHideAll(String meaningForHideAll) {
        this.meaningForHideAll = meaningForHideAll;
    }

    public String getMeaningTTS() {
        return meaningTTS;
    }

    public void setMeaningTTS(String meaningTTS) {
        this.meaningTTS = meaningTTS;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public String getVocaOri() {
        return vocaOri;
    }

    public void setVocaOri(String vocaOri) {
        this.vocaOri = vocaOri;
    }

    public String getVocaDisplay() {
        return vocaDisplay;
    }

    public void setVocaDisplay(String vocaDisplay) {
        this.vocaDisplay = vocaDisplay;
    }

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
    }

    public String getVocaTTS() {
        return vocaTTS;
    }

    public void setVocaTTS(String vocaTTS) {
        this.vocaTTS = vocaTTS;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }
}
