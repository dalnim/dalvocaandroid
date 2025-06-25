package com.dalread.widget;

import com.dalread.model.Lesson;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LessonListWidgetItem extends RealmObject {

    @PrimaryKey
    private int id;
    private long startTime;
    private long finishTime;
    private boolean started;
    private boolean finished;
    private int studyLangCode;
    private int lessonTypeApi;
    private String studentName;
    private String tutorName;
    private boolean studentJoined;
    private boolean tutorJoined;
    private int lessonType;

    public LessonListWidgetItem() {
    }

    public LessonListWidgetItem(Lesson lesson) {
        setId(lesson.getId());
        setStartTime(lesson.getLessonStartTimeTS());
        setFinishTime(lesson.getLessonFinishTimeTS());
        setStarted(lesson.getStarted() == 1);
        setFinished(lesson.getFinished() == 1);
        setStudyLangCode(lesson.getStudyLangCode());
        setLessonTypeApi(lesson.getLessonTypeApi());
        setStudentName(lesson.getStudentName());
        setTutorName(lesson.getTutorName());
        setStudentJoined(lesson.getStudentJoined() == 1);
        setTutorJoined(lesson.getTutorJoined() == 1);
        setLessonType(lesson.getLessonType());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getStudyLangCode() {
        return studyLangCode;
    }

    public void setStudyLangCode(int studyLangCode) {
        this.studyLangCode = studyLangCode;
    }

    public int getLessonTypeApi() {
        return lessonTypeApi;
    }

    public void setLessonTypeApi(int lessonTypeApi) {
        this.lessonTypeApi = lessonTypeApi;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public boolean isStudentJoined() {
        return studentJoined;
    }

    public void setStudentJoined(boolean studentJoined) {
        this.studentJoined = studentJoined;
    }

    public boolean isTutorJoined() {
        return tutorJoined;
    }

    public void setTutorJoined(boolean tutorJoined) {
        this.tutorJoined = tutorJoined;
    }

    public int getLessonType() {
        return lessonType;
    }

    public void setLessonType(int lessonType) {
        this.lessonType = lessonType;
    }
}
