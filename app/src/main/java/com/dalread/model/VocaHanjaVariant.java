package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaHanjaVariant implements Serializable {

    @SerializedName("HANJA_TRADITIONAL")
    private String hanjaTraditional;
    @SerializedName("HANJA_KOREA")
    private String hanjaKorea;
    @SerializedName("HANJA_JAPAN")
    private String hanjaJapan;
    @SerializedName("HANJA_SIMPLIFIED")
    private String hanjaSimplified;
    @SerializedName("HANJA_TAIWAN")
    private String hanjaTaiwan;
    @SerializedName("HANJA_SHORT_FORM")
    private String hanjaShortForm;
    @SerializedName("HANJA_VARIANT_1")
    private String hanjaVariant1;
    @SerializedName("HANJA_VARIANT_2")
    private String hanjaVariant2;
    @SerializedName("HANJA_SOKJA")
    private String hanjaSokja;

    public String getHanjaTraditional() {
        return hanjaTraditional;
    }

    public void setHanjaTraditional(String hanjaTraditional) {
        this.hanjaTraditional = hanjaTraditional;
    }

    public String getHanjaKorea() {
        return hanjaKorea;
    }

    public void setHanjaKorea(String hanjaKorea) {
        this.hanjaKorea = hanjaKorea;
    }

    public String getHanjaJapan() {
        return hanjaJapan;
    }

    public void setHanjaJapan(String hanjaJapan) {
        this.hanjaJapan = hanjaJapan;
    }

    public String getHanjaSimplified() {
        return hanjaSimplified;
    }

    public void setHanjaSimplified(String hanjaSimplified) {
        this.hanjaSimplified = hanjaSimplified;
    }

    public String getHanjaTaiwan() {
        return hanjaTaiwan;
    }

    public void setHanjaTaiwan(String hanjaTaiwan) {
        this.hanjaTaiwan = hanjaTaiwan;
    }

    public String getHanjaShortForm() {
        return hanjaShortForm;
    }

    public void setHanjaShortForm(String hanjaShortForm) {
        this.hanjaShortForm = hanjaShortForm;
    }

    public String getHanjaVariant1() {
        return hanjaVariant1;
    }

    public void setHanjaVariant1(String hanjaVariant1) {
        this.hanjaVariant1 = hanjaVariant1;
    }

    public String getHanjaVariant2() {
        return hanjaVariant2;
    }

    public void setHanjaVariant2(String hanjaVariant2) {
        this.hanjaVariant2 = hanjaVariant2;
    }

    public String getHanjaSokja() {
        return hanjaSokja;
    }

    public void setHanjaSokja(String hanjaSokja) {
        this.hanjaSokja = hanjaSokja;
    }
}
