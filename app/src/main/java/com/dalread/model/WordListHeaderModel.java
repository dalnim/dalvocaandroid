package com.dalread.model;

import com.dalread.util.Constant;

import java.io.Serializable;

public class WordListHeaderModel implements Serializable {
    private int know; //TODO: vocaKnow로 바꿀것
//    private int grade;
    private String name;
    private int size;

    public WordListHeaderModel(String name) {
        this(Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED, name, 0);
    }

//    public WordListHeaderModel(int grade, String name) {
//        this(Constant.AMKI_GRADE.VALUE_NONE, grade, name, 0);
//    }

    public WordListHeaderModel(int know, String name) {
        this(know, name, 0);
    }

    public WordListHeaderModel(int know, String name, int size) {
        this.know = know;
        this.name = name;
        this.size = size;
    }

    public int getKnow() {
        return know;
    }

    public void setKnow(int know) {
        this.know = know;
    }

//    public int getGrade() {
//        return grade;
//    }
//
//    public void setGrade(int grade) {
//        this.grade = grade;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isHideType() {
        return know == Constant.VOCA_KNOW.VOCA_KNOW_NULL;
    }

    @Override
    public String toString() {
        return "WordListHeaderModel{" +
                "know=" + know +
                ", name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}
