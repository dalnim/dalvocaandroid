package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaHanjaExample implements Serializable, AmkiItem {

    @SerializedName("VOCA")
    private String voca;
    @SerializedName("VOCA_ID")
    private int vocaId;
    @SerializedName("VOCA_KNOW")
    private int vocaKnow;
    @SerializedName("VOCA_KNOWPRONOUNCE")
    private int vocaKnowPronounce;
    @SerializedName("VOCA_TYPE")
    private int vocaType;
    @SerializedName("PRONOUNCE")
    private String pronounce;
    @SerializedName("PRONOUNCE_CH_S")
    private String pronounceChineseSimplified;
    @SerializedName("PRONOUNCE_JP")
    private String pronounceJapanese;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("MEANING_DETAILED")
    private String meaningDetailed;
    @SerializedName("WORDLEVEL")
    private int wordLevel;

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
    }

    public int getVocaKnow() {
        return vocaKnow;
    }

    public void setVocaKnow(int vocaKnow) {
        this.vocaKnow = vocaKnow;
    }

    public int getVocaKnowPronounce() {
        return vocaKnowPronounce;
    }

    public void setVocaKnowPronounce(int vocaKnowPronounce) {
        this.vocaKnowPronounce = vocaKnowPronounce;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getPronounceChineseSimplified() {
        return pronounceChineseSimplified;
    }

    public void setPronounceChineseSimplified(String pronounceChineseSimplified) {
        this.pronounceChineseSimplified = pronounceChineseSimplified;
    }

    public String getPronounceJapanese() {
        return pronounceJapanese;
    }

    public void setPronounceJapanese(String pronounceJapanese) {
        this.pronounceJapanese = pronounceJapanese;
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

    public int getWordLevel() {
        return wordLevel;
    }

    public void setWordLevel(int wordLevel) {
        this.wordLevel = wordLevel;
    }

    @Override
    public int getAmkiId() {
        return getVocaId();
    }

    @Override
    public int getAmkiType() {
        return getVocaType();
    }

    @Override
    public int getAmkiKnow() {
        return getVocaKnow();
    }

    @Override
    public int getAmkiKnowPronounce() {
        return getVocaKnowPronounce();
    }

    @Override
    public String getAmkiEvaluationGrade() {
        return "";
    }

    @Override
    public String getAmki() {
        return "";
    }
}
