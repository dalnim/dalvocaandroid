package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VocaKnowAndBookmarkList implements Serializable {

    @SerializedName("VOCA_BOOKMARK_LIST")
    private List<VocaBookmark> vocaBookmarkList;
    @SerializedName("VOCA_KNOW_KNOWPRONOUNCE_LIST")
    private List<VocaKnowAndKnowpronounce> vocaKnowKnowpronounceList;

    public List<VocaBookmark> getVocaBookmarkList() {
        return vocaBookmarkList;
    }

    public void setVocaBookmarkList(List<VocaBookmark> vocaBookmarkList) {
        this.vocaBookmarkList = vocaBookmarkList;
    }

    public List<VocaKnowAndKnowpronounce> getVocaKnowKnowpronounceList() {
        return vocaKnowKnowpronounceList;
    }

    public void setVocaKnowKnowpronounceList(List<VocaKnowAndKnowpronounce> vocaKnowKnowpronounceList) {
        this.vocaKnowKnowpronounceList = vocaKnowKnowpronounceList;
    }

}
