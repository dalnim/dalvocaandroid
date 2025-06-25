package com.dalread.util.stt;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class SttModelGoogleCloud extends SttModel {
    @SerializedName(value = "result")
    private List<Word> wordList;
    @SerializedName(value = "text")
    private String text; //From onResult
    @SerializedName(value = "partial")
    private String partial; //From onPartial

    private String sentence;
    private String translation;
    private String difficultWordAndMeaning;
    private double startTime;
    private double endTime;

    public static SttModelGoogleCloud withSttResult(String str) {
        SttModelGoogleCloud model = new SttModelGoogleCloud();
        model.setSentence(str);
        return model;
    }


    public List<Word> getWordList() {
        return Collections.emptyList();
    }

    public void setWordList(List<Word> wordList) {
        this.wordList = wordList;
    }

//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        String result = "";
        if ((sentence != null) && (sentence.trim().length() > 0)) {
            result = sentence.trim();
        } else if ((text != null) && (text.trim().length() > 0)) {
            result = text.trim();
        } else if ((partial != null) && (partial.trim().length() > 0)) {
            result = partial.trim();
        }
        return result;
    }
    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    private String getDifficultWordAndMeaning() {
        return difficultWordAndMeaning;
    }

    private void setDifficultWordAndMeaning(String difficultWordAndMeaning) {
        this.difficultWordAndMeaning = difficultWordAndMeaning;
    }

    @Override
    public String SMgetDifficultWordAndMeaning() {
        return getDifficultWordAndMeaning();
    }

    @Override
    public void SMsetDifficultWordAndMeaning(String value) {
        setDifficultWordAndMeaning(value);
    }
    @Override
    public String SMgetSentence() {
        return getSentence();
    }

    @Override
    public String SMgetTranslation() {
        return getTranslation();
    }

    @Override
    public List<Word> SMgetWordList() {
        return getWordList();
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public String getStartEndTime() {
        String result = "";
        if ((startTime >= 0) && (endTime > 0)) {
            result = startTime + "~" + endTime;
        }
        return result;
    }
}
