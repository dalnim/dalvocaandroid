package com.dalread.interfaces;

import java.io.Serializable;

public interface IVocaCoreItem extends Serializable {
    Integer getVIVocaType();

    Integer getVIVocaId();

    void setVIVocaType(Integer value);

    void setVIVocaId(Integer value);

    default String getVIVocaTypeId() {
        return getVIVocaType() + "_" + getVIVocaId();
    }

}
