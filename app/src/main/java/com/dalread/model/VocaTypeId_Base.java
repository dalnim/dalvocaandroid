package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaTypeId_Base extends VocaKnowAndKnowpronounce implements Serializable {

    @SerializedName("VOCA_TYPE_BASE")
    protected int vocaTypeBase;

    @SerializedName("VOCA_ID_BASE")
    protected int vocaIdBase;


    public VocaTypeId_Base(int vocaId, int vocaType, int vocaKnow, int vocaKnowPronunce, int vocaIdBase, int vocaTypeBase) {
        super(vocaId, vocaType, vocaKnow, vocaKnowPronunce);
        this.vocaTypeBase = vocaTypeBase;
        this.vocaIdBase = vocaIdBase;
    }

    public int getVocaTypeBase() {
        return vocaTypeBase;
    }

    public void setVocaTypeBase(int vocaTypeBase) {
        this.vocaTypeBase = vocaTypeBase;
    }

    public int getVocaIdBase() {
        return vocaIdBase;
    }

    public void setVocaIdBase(int vocaIdBase) {
        this.vocaIdBase = vocaIdBase;
    }
}
