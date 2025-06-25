package com.dalread.util.stt;

import com.dalread.util.Utils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SttModelVosk extends SttModel{
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

    public static SttModelVosk withSttResult(String json) {
        Gson gson = new Gson();
        SttModelVosk voskSttModel = gson.fromJson(json, SttModelVosk.class);
        if (!Utils.isEmpty(voskSttModel.getWordList())) {
            SttModelVosk.Word firstWord = voskSttModel.getWordList().get(0);
            SttModelVosk.Word lastWord = voskSttModel.getWordList().get(voskSttModel.getWordList().size() - 1);
            voskSttModel.setStartTime(firstWord.getStartTime());
            voskSttModel.setEndTime(lastWord.getEndTime());
        }
        return voskSttModel;
    }

    public List<Word> getWordList() {
        return wordList;
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

    public String getSentence() {
        String result = "";
        if ((text != null) && (text.trim().length() > 0)) {
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
