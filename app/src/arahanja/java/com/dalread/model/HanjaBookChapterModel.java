package com.dalread.model;

public class HanjaBookChapterModel {
    private final String TAG = "HanjaBookModel";

    private String voca;
    private String pronounce;
    private String meaning;
    private String meaningDetailed;

    public HanjaBookChapterModel(String voca, String pronounce, String meaning, String meaningDetailed) {
        this.voca = voca;
        this.pronounce = pronounce == null ? "" : pronounce;
        this.meaning = meaning == null ? "" : meaning;
        this.meaningDetailed = meaningDetailed == null ? "" : meaningDetailed;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
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

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }
}
