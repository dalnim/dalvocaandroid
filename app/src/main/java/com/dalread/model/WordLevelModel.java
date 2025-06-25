package com.dalread.model;

import com.dalread.util.Constant;

/**
 * Created by JetVHS on 6/14/17.
 */

public class WordLevelModel {
    private String title;
    private int count;

    public WordLevelModel() {
        this(Constant.BASE_BLANK, 0);
    }

    public WordLevelModel(String title, int count) {
        this.title = title;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "WordLevelModel{" +
                "title='" + title + '\'' +
                ", count=" + count +
                '}';
    }
}
