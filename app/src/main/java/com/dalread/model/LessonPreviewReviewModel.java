package com.dalread.model;

import com.dalread.util.Constant;

public class LessonPreviewReviewModel {
    private int lessonType;
    private boolean recordLesson;

    public LessonPreviewReviewModel(int lessonType, boolean recordLesson) {
        this.lessonType = lessonType;
        this.recordLesson = recordLesson;
    }

    public int getLessonType() {
        return lessonType;
    }

    public void setLessonType(int lessonType) {
        this.lessonType = lessonType;
    }

    public boolean isRecordLesson() {
        return recordLesson;
    }

    public void setRecordLesson(boolean recordLesson) {
        this.recordLesson = recordLesson;
    }

    public boolean isPreviewTodayLesson() {
        return lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT;
    }

    @Override
    public String toString() {
        return "LessonPreviewReviewModel{" +
                "lessonType=" + lessonType +
                ", recordLesson=" + recordLesson +
                '}';
    }
}
