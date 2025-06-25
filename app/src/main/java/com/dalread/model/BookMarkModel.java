package com.dalread.model;

import com.dalread.util.Constant;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by JetVHS on 2/28/2017.
 */
public class BookMarkModel extends RealmObject {
    public static final String FIELD_ID = "id";
    public static final String FIELD_URL = "url";
    public static final String FIELD_NAME = "name";

    @PrimaryKey
    private int id;
    private String url;
    private String name;

    public BookMarkModel() {
        this(Constant.BASE_BLANK, Constant.BASE_BLANK);
    }

    public BookMarkModel(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BookMarkModel{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
