package com.dalread.model;

import com.dalread.util.Constant;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WebDictionaryModel extends RealmObject {

    @PrimaryKey
    private long id;
    private String title;
    private String url;
    private int studyLanguage;
    private int motherTongue;
    private int index;

    public WebDictionaryModel() {
        this(System.currentTimeMillis(), Constant.BASE_BLANK, Constant.BASE_BLANK, 0, 0, 0);
    }

    public WebDictionaryModel(long id, String title, String url, int studyLanguage, int motherTongue, int index) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.studyLanguage = studyLanguage;
        this.motherTongue = motherTongue;
        this.index = index;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMotherTongue() {
        return motherTongue;
    }

    public void setMotherTongue(int motherTongue) {
        this.motherTongue = motherTongue;
    }

    public int getStudyLanguage() {
        return studyLanguage;
    }

    public void setStudyLanguage(int studyLanguage) {
        this.studyLanguage = studyLanguage;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "WebDictionaryModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", motherTongue=" + motherTongue +
                ", studyLanguage=" + studyLanguage +
                ", index=" + index +
                '}';
    }
}
