package com.dalread.model;

import com.dalread.database.sqlite.model.DicModel;

public class SubtitleMeaningModel {
    private int subtitleId;
    private DicModel dicModel;

    public SubtitleMeaningModel() {
    }

    public SubtitleMeaningModel(int subtitleId, DicModel dicModel) {
        this.subtitleId = subtitleId;
        this.dicModel = dicModel;
    }

    public int getSubtitleId() {
        return subtitleId;
    }

    public void setSubtitleId(int subtitleId) {
        this.subtitleId = subtitleId;
    }

    public DicModel getDicModel() {
        return dicModel;
    }

    public void setDicModel(DicModel dicModel) {
        this.dicModel = dicModel;
    }

    @Override
    public String toString() {
        return "SubtitleMeaningModel{" +
                "subtitleId=" + subtitleId +
                ", dicModel=" + dicModel +
                '}';
    }
}
