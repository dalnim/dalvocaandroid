package com.dalread.model;

import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

public class VocaReading extends VocaFromAllVocaBook {

    @SerializedName("READING_ID")
    private int readingId;
    @SerializedName("LESSON_READING_ID")
    private int lessonReadingId;
    @SerializedName("RUBY_TEXT")
    private String rubyText;
    @SerializedName("MEANING")
    private String meaning;

    public int getReadingId() {
        return readingId;
    }

    public void setReadingId(int readingId) {
        this.readingId = readingId;
    }

    public int getLessonReadingId() {
        return lessonReadingId;
    }

    public void setLessonReadingId(int lessonReadingId) {
        this.lessonReadingId = lessonReadingId;
    }

    public String getRubyText() {
        return rubyText;
    }

    public void setRubyText(String rubyText) {
        this.rubyText = rubyText;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public boolean updateVocaDisplayRubyText(int vocaId, String key, int newValue) {
        return Utils.updateRubyText(getRubyText(), vocaId, key, newValue, this::setRubyText);
    }
}
