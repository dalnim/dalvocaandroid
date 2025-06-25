package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VocaHanjaSentence extends VocaHanjaExample {

    @SerializedName("HANJA_LIST")
    private List<VocaHanja> hanjaList;

    public List<VocaHanja> getHanjaList() {
        if (hanjaList == null) {
            hanjaList = new ArrayList<>();
        }
        return hanjaList;
    }

    public void setHanjaList(List<VocaHanja> hanjaList) {
        this.hanjaList = hanjaList;
    }
}
