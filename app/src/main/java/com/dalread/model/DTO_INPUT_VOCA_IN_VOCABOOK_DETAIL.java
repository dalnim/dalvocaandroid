package com.dalread.model;

import com.google.gson.annotations.SerializedName;

//updateVocaBookMeaningWithID.ajax API보낼때 이걸로 보낼려고 했는데 잘 안되네... (현재 아무곳도 안쓰임)
public class DTO_INPUT_VOCA_IN_VOCABOOK_DETAIL {
    @SerializedName("UID")
    private String uid;
    @SerializedName("VOCABOOK_TYPE_CODE")
    private int vocabookTypeCode;
    @SerializedName("ID_IN_VOCABOOK")
    private int idInVocabook;
    @SerializedName("VOCA")
    private String voca;
    @SerializedName("LANG_MEANING_CODE")
    private int langMeaningCode;
    @SerializedName("LANG_STUDY_CODE")
    private int langStudyCode;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("MEANING_DETAILED")
    private String meaningDetailed;
    @SerializedName("MEANING_TTS")
    protected String meaningTts = "";
    @SerializedName("MEANING_FOR_HIDE_ALL")
    protected String meaningForHideAll = "";

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getVocabookTypeCode() {
        return vocabookTypeCode;
    }

    public void setVocabookTypeCode(int vocabookTypeCode) {
        this.vocabookTypeCode = vocabookTypeCode;
    }

    public int getIdInVocabook() {
        return idInVocabook;
    }

    public void setIdInVocabook(int idInVocabook) {
        this.idInVocabook = idInVocabook;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public int getLangMeaningCode() {
        return langMeaningCode;
    }

    public void setLangMeaningCode(int langMeaningCode) {
        this.langMeaningCode = langMeaningCode;
    }

    public int getLangStudyCode() {
        return langStudyCode;
    }

    public void setLangStudyCode(int langStudyCode) {
        this.langStudyCode = langStudyCode;
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

    public String getMeaningTts() {
        return meaningTts;
    }

    public void setMeaningTts(String meaningTts) {
        this.meaningTts = meaningTts;
    }

    public String getMeaningForHideAll() {
        return meaningForHideAll;
    }

    public void setMeaningForHideAll(String meaningForHideAll) {
        this.meaningForHideAll = meaningForHideAll;
    }
}
