package com.dalread.model;

import com.google.gson.annotations.SerializedName;

public class CurrentLesson {

    @SerializedName("LANG_STUDY")
    private String studyLang;
    @SerializedName("VOCABOOK_TYPE")
    private int bookType;
    @SerializedName("VOCABOOKS_ID")
    private int bookId;
    @SerializedName("VOCABOOKS_CELL_INDEX")
    private int cellIndex;
    @SerializedName("TOPIC_BEGIN_INDEX")
    private int topicBeginIndex;
    @SerializedName("TOPIC_REPEATED_COUNT")
    private int topicRepeatedCount;
    @SerializedName("LESSON_MODE")
    private int lessonMode;
    @SerializedName("ROLE_PLAYING_ID")
    private int rolePlayingId;
    @SerializedName("GRAMMAR_ID")
    private int grammarId;
    @SerializedName("LESSON_READING_ID")
    private int lessonReadingId;

    public String getStudyLang() {
        return studyLang;
    }

    public void setStudyLang(String studyLang) {
        this.studyLang = studyLang;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }

    public int getTopicBeginIndex() {
        return topicBeginIndex;
    }

    public void setTopicBeginIndex(int topicBeginIndex) {
        this.topicBeginIndex = topicBeginIndex;
    }

    public int getTopicRepeatedCount() {
        return topicRepeatedCount;
    }

    public void setTopicRepeatedCount(int topicRepeatedCount) {
        this.topicRepeatedCount = topicRepeatedCount;
    }

    public int getLessonMode() {
        return lessonMode;
    }

    public void setLessonMode(int lessonMode) {
        this.lessonMode = lessonMode;
    }

    public int getRolePlayingId() {
        return rolePlayingId;
    }

    public void setRolePlayingId(int rolePlayingId) {
        this.rolePlayingId = rolePlayingId;
    }

    public int getGrammarId() {
        return grammarId;
    }

    public void setGrammarId(int grammarId) {
        this.grammarId = grammarId;
    }

    public int getLessonReadingId() {
        return lessonReadingId;
    }

    public void setLessonReadingId(int lessonReadingId) {
        this.lessonReadingId = lessonReadingId;
    }
}
