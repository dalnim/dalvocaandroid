package com.dalread.model;

import android.util.Pair;

import java.util.ArrayList;

public class Question {

    private String question;
    private ArrayList<Pair<Boolean, String>> answers;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<Pair<Boolean, String>> getAnswers() {
        if (answers == null) {
            setAnswers(new ArrayList<Pair<Boolean, String>>());
        }
        return answers;
    }

    public void setAnswers(ArrayList<Pair<Boolean, String>> answers) {
        this.answers = answers;
    }
}
