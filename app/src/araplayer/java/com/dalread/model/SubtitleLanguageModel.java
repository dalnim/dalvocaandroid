package com.dalread.model;

public class SubtitleLanguageModel { //implements Parcelable {

    private int language;
    private boolean isSelected = true;
    private int index;
    private boolean hasData;

    public SubtitleLanguageModel(int index, int language, boolean isSelected, boolean hasData) {
        this.index = index;
        this.language = language;
        this.isSelected = isSelected;
        this.hasData = hasData;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    @Override
    public String toString() {
        return "SubtitleLanguageModel{" +
                ", subLanguage='" + language + '\'' +
                ", isSelect=" + isSelected +
                ", index=" + index +
                ", hasData=" + hasData +
                '}';
    }
}
