package com.dalread.model;

import com.dalread.interfaces.IVocaCoreItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaTypeId implements Serializable, IVocaCoreItem {

    @SerializedName("VOCA_TYPE")
    protected int vocaType;

    @SerializedName("VOCA_ID")
    protected int vocaId;


    public VocaTypeId(int vocaId, int vocaType) {
        this.vocaId = vocaId;
        this.vocaType = vocaType;
    }
    public VocaTypeId(IVocaCoreItem item) {
        this.vocaId = item.getVIVocaId();
        this.vocaType = item.getVIVocaType();
    }

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
        return vocaType;
    }

    @Override
    public Integer getVIVocaId() {
        return vocaId;
    }

    @Override
    public void setVIVocaType(Integer value) {
        this.vocaType = value;
    }

    @Override
    public void setVIVocaId(Integer value) {
        this.vocaId = value;
    }
}
