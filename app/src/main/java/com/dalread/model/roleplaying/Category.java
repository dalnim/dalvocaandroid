package com.dalread.model.roleplaying;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("INDEX")
    private int index;
    @SerializedName("ID")
    private int id;
    @SerializedName("PARENT_WORDBOOK_ID")
    private int parentWordbookId;
    @SerializedName("ROLE_PLAYING_TYPE")
    private int rolePlayingType;
    @SerializedName("NAME")
    private String name;
    @SerializedName("NAME_DISPLMEANINGLANG")
    private String nameDisplayMeaningLang;
    @SerializedName("NAME_STUDYLANG")
    private String nameStudyLang;
    @SerializedName("HAS_SUB_LIST")
    private int hasSubList;
    @SerializedName("studyLang")
    private String studyLang;
    private boolean isRandom;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentWordbookId() {
        return parentWordbookId;
    }

    public void setParentWordbookId(int parentWordbookId) {
        this.parentWordbookId = parentWordbookId;
    }

    public int getRolePlayingType() {
        return rolePlayingType;
    }
    public void setRolePlayingType(int rolePlayingType) {
        this.rolePlayingType = rolePlayingType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameDisplayMeaningLang() {
        return nameDisplayMeaningLang;
    }

    public void setNameDisplayMeaningLang(String nameDisplayMeaningLang) {
        this.nameDisplayMeaningLang = nameDisplayMeaningLang;
    }

    public String getNameStudyLang() {
        return nameStudyLang;
    }

    public void setNameStudyLang(String nameStudyLang) {
        this.nameStudyLang = nameStudyLang;
    }

    public int getHasSubList() {
        return hasSubList;
    }

    public void setHasSubList(int hasSubList) {
        this.hasSubList = hasSubList;
    }

    public String getStudyLang() {
        return studyLang;
    }

    public void setStudyLang(String studyLang) {
        this.studyLang = studyLang;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }
}
