package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaHanjaSearch implements Serializable {

    @SerializedName("HANJA_COMPONENT")
    private String hanjaComponent;
    @SerializedName("INDEX")
    private int index;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("PRONOUNCE")
    private String pronounce;
    @SerializedName("VOCA")
    private String voca;
    @SerializedName("VOCAORI")
    private String vocaOri;
    @SerializedName("VOCA_FIRST_MEANING_PRONOUNCE_FOR_HANJA")
    private String vocaFirstMeaningPronounceForHanja;
    @SerializedName("VOCA_MEANING_PRONOUNCE_FOR_HANJA")
    private String vocaMeaningPronounceForHanja;

    public String getHanjaComponent() {
        return hanjaComponent;
    }

    public void setHanjaComponent(String hanjaComponent) {
        this.hanjaComponent = hanjaComponent;
    }

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

    public String getVocaFirstMeaningPronounceForHanja() {
        return vocaFirstMeaningPronounceForHanja;
    }

    public void setVocaFirstMeaningPronounceForHanja(String vocaFirstMeaningPronounceForHanja) {
        this.vocaFirstMeaningPronounceForHanja = vocaFirstMeaningPronounceForHanja;
    }

    public String getVocaMeaningPronounceForHanja() {
        return vocaMeaningPronounceForHanja;
    }

    public void setVocaMeaningPronounceForHanja(String vocaMeaningPronounceForHanja) {
        this.vocaMeaningPronounceForHanja = vocaMeaningPronounceForHanja;
    }
}
