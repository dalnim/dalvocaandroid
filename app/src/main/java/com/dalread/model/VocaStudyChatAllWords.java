package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VocaStudyChatAllWords implements Serializable {

    @SerializedName("TITLE")
    private String title;
    @SerializedName("VOCA_LIST")
    private List<VocaStudyChat> vocaList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<VocaStudyChat> getVocaList() {
        if (vocaList == null) {
            setVocaList(new ArrayList<>());
        }
        return vocaList;
    }

    public void setVocaList(List<VocaStudyChat> vocaList) {
        this.vocaList = vocaList;
    }

    public boolean updateVocaDisplayRubyText(int vocaId, String key, int newValue) {
        boolean result = false;
        for (VocaStudyChat voca : getVocaList()) {
            result |= voca.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        return result;
    }
}
