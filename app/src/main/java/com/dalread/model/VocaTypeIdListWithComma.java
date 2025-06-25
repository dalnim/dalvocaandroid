package com.dalread.model;

import java.io.Serializable;

public class VocaTypeIdListWithComma implements Serializable {

    protected String vocaTypeListWithComma;
    protected String vocaIdListWithComma;


    public VocaTypeIdListWithComma(String vocaTypeListWithComma, String vocaIdListWithComma) {
        this.vocaTypeListWithComma = vocaTypeListWithComma;
        this.vocaIdListWithComma = vocaIdListWithComma;
    }

    public String getVocaTypeListWithComma() {
        return vocaTypeListWithComma;
    }

    public String getVocaIdListWithComma() {
        return vocaIdListWithComma;
    }
}
