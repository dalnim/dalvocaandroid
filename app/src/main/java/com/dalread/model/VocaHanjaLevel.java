package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaHanjaLevel implements Serializable {

    @SerializedName("LEVEL")
    private int level;
    @SerializedName("LEVEL_KOREA_TYPE_1")
    private String levelKoreaType1;
    @SerializedName("LEVEL_KOREA_TYPE_2")
    private String levelKoreaType2;
    @SerializedName("LEVEL_KOREA_TYPE_3")
    private String levelKoreaType3;
    @SerializedName("COMMON_USE_KOREA")
    private String commonUseKorea;
    @SerializedName("COMMON_USE_JAPAN")
    private String commonUseJapan;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelKoreaType1() {
        return levelKoreaType1;
    }

    public void setLevelKoreaType1(String levelKoreaType1) {
        this.levelKoreaType1 = levelKoreaType1;
    }

    public String getLevelKoreaType2() {
        return levelKoreaType2;
    }

    public void setLevelKoreaType2(String levelKoreaType2) {
        this.levelKoreaType2 = levelKoreaType2;
    }

    public String getLevelKoreaType3() {
        return levelKoreaType3;
    }

    public void setLevelKoreaType3(String levelKoreaType3) {
        this.levelKoreaType3 = levelKoreaType3;
    }

    public String getCommonUseKorea() {
        return commonUseKorea;
    }

    public void setCommonUseKorea(String commonUseKorea) {
        this.commonUseKorea = commonUseKorea;
    }

    public String getCommonUseJapan() {
        return commonUseJapan;
    }

    public void setCommonUseJapan(String commonUseJapan) {
        this.commonUseJapan = commonUseJapan;
    }
}
