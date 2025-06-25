package com.dalread.model;

import com.google.gson.annotations.SerializedName;

public class MultipleWordMeaningWithResultModel {
    @SerializedName("NEW_VOCA_ID_LIST")
    private MultipleWordMeaningWithIDModel.InfoListModel item;

    public MultipleWordMeaningWithResultModel() {
        item = new MultipleWordMeaningWithIDModel.InfoListModel();
    }

    public MultipleWordMeaningWithResultModel(MultipleWordMeaningWithIDModel.InfoListModel item) {
        this.item = item;
    }

    public MultipleWordMeaningWithIDModel.InfoListModel getItem() {
        return item;
    }

    public void setItem(MultipleWordMeaningWithIDModel.InfoListModel item) {
        this.item = item;
    }
}
