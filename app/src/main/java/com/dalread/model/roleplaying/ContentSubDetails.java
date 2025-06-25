package com.dalread.model.roleplaying;

import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

public class ContentSubDetails {

    @SerializedName("NAME")
    private String name;
    @SerializedName("NAME_RUBY_TEXT")
    private String nameRubyText;
    @SerializedName("PRICE")
    private String price;
    @SerializedName("DESC")
    private String desc;
    private boolean checked;
    private int tblSection;
    private int tblRow;
    private int countOfOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameRubyText() {
        return nameRubyText;
    }

    public void setNameRubyText(String nameRubyText) {
        this.nameRubyText = nameRubyText;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getTblSection() {
        return tblSection;
    }

    public void setTblSection(int tblSection) {
        this.tblSection = tblSection;
    }

    public int getTblRow() {
        return tblRow;
    }

    public void setTblRow(int tblRow) {
        this.tblRow = tblRow;
    }

    public int getCountOfOrder() {
        return countOfOrder;
    }

    public void setCountOfOrder(int countOfOrder) {
        this.countOfOrder = countOfOrder;
    }

    public boolean updateVocaDisplayRubyText(int vocaId, String key, int newValue) {
        return Utils.updateRubyText(getNameRubyText(), vocaId, key, newValue, this::setNameRubyText);
    }
}
