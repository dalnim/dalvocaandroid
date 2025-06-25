package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaTypeId_KnowWithOldValue extends VocaTypeId implements Serializable {

    @SerializedName("VOCA_KNOW")
    private int vocaKnow;
    @SerializedName("VOCA_KNOWPRONOUNCE")
    private int vocaKnowpronounce;

    @SerializedName("VOCA_KNOW_OLD")
    private int vocaKnowOld;
    @SerializedName("VOCA_KNOWPRONOUNCE_OLD")
    private int vocaKnowpronounceOld;

    public VocaTypeId_KnowWithOldValue(int vocaId, int vocaType, int vocaKnow, int vocaKnowpronounce, int vocaKnowOld, int vocaKnowpronounceOld) {
        super(vocaId, vocaType);
        this.vocaKnow = vocaKnow;
        this.vocaKnowpronounce = vocaKnowpronounce;
        this.vocaKnowOld = vocaKnowOld;
        this.vocaKnowpronounceOld = vocaKnowpronounceOld;
    }


    public int getVocaKnow() {
        return vocaKnow;
    }

    public void setVocaKnow(int vocaKnow) {
        this.vocaKnow = vocaKnow;
    }

    public int getVocaKnowpronounce() {
        return vocaKnowpronounce;
    }

    public void setVocaKnowpronounce(int vocaKnowpronounce) {
        this.vocaKnowpronounce = vocaKnowpronounce;
    }

    public int getVocaKnowOld() {
        return vocaKnowOld;
    }

    public void setVocaKnowOld(int vocaKnowOld) {
        this.vocaKnowOld = vocaKnowOld;
    }

    public int getVocaKnowpronounceOld() {
        return vocaKnowpronounceOld;
    }

    public void setVocaKnowpronounceOld(int vocaKnowpronounceOld) {
        this.vocaKnowpronounceOld = vocaKnowpronounceOld;
    }
}
