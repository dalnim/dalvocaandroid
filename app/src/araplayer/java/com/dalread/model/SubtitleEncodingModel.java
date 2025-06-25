package com.dalread.model;

import com.dalread.util.Constant;

public class SubtitleEncodingModel {
    private String name;
    private boolean isHeader;
    private boolean isSelect;

    public SubtitleEncodingModel() {
        this(Constant.BASE_BLANK, false, false);
    }

    public SubtitleEncodingModel(String name) {
        this(name, false, false);
    }

    public SubtitleEncodingModel(String name, String value) {
        this(name, false, value.equals(name));
    }

    public SubtitleEncodingModel(String name, boolean isHeader) {
        this(name, isHeader, false);
    }

    public SubtitleEncodingModel(String name, boolean isHeader, String value) {
        this(name, isHeader, value.equals(name));
    }

    public SubtitleEncodingModel(String name, boolean isHeader, boolean isSelect) {
        this.name = name;
        this.isHeader = isHeader;
        this.isSelect = isSelect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "SubtitleEncodingModel{" +
                "name='" + name + '\'' +
                ", isHeader=" + isHeader +
                ", isSelect=" + isSelect +
                '}';
    }
}
