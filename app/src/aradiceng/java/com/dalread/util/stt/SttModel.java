package com.dalread.util.stt;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public abstract class SttModel {
    public abstract String SMgetDifficultWordAndMeaning();
    public abstract void SMsetDifficultWordAndMeaning(String value);

    public abstract String SMgetSentence();
    public abstract String SMgetTranslation();

    public abstract List<Word> SMgetWordList();

    public class Word {
        @SerializedName(value = "conf")
        private double confidence;
        @SerializedName(value = "start")
        private double startTime;
        @SerializedName(value = "end")
        private double endTime;
        private String word;

        public double getConfidence() {
            return confidence;
        }

        public double getStartTime() {
            return startTime;
        }

        public double getEndTime() {
            return endTime;
        }

        public String getWord() {
            return word;
        }
    }
}
