package com.dalread.database.sqlite.model;

public class SubtitleWordListModel {
    private int id;
    private int subtitleId;
//    private String vocaDisplay;
    private int vocaType;
    private int vocaId;

    public SubtitleWordListModel() {
    }

    public SubtitleWordListModel(int id, int subtitleId, int vocaType, int vocaId) {
        this.id = id;
        this.subtitleId = subtitleId;
        this.vocaType = vocaType;
//        this.vocaDisplay = vocaDisplay;
        this.vocaId = vocaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubtitleId() {
        return subtitleId;
    }

    public void setSubtitleId(int subtitleId) {
        this.subtitleId = subtitleId;
    }

//    public String getVocaDisplay() {
//        return vocaDisplay;
//    }
//
//    public void setVocaDisplay(String vocaDisplay) {
//        this.vocaDisplay = vocaDisplay;
//    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
    }

    @Override
    public String toString() {
        return "SubtitleWordListModel{" +
                "id='" + id + '\'' +
                ", subtitleId='" + subtitleId + '\'' +
//                ", vocaDisplay='" + vocaDisplay + '\'' +
                ", vocaType='" + vocaType + '\'' +
                ", vocaId='" + vocaId + '\'' +
                '}';
    }
}
