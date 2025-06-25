package com.dalread.model;

import com.dalread.interfaces.IVocaCoreItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaBookmark implements Serializable, IVocaCoreItem {

    @SerializedName("VOCA_ID")
    private int vocaId;
    @SerializedName("VOCA_TYPE")
    private int vocaType;

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    @Override
    public Integer getVIVocaType() {
        return getVocaType();
    }

    @Override
    public Integer getVIVocaId() {
        return getVocaId();
    }

    @Override
    public void setVIVocaType(Integer value) {
        setVocaType(value);
    }

    @Override
    public void setVIVocaId(Integer value) {
        setVocaId(value);
    }
}
