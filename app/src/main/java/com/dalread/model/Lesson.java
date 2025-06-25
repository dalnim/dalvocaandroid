package com.dalread.model;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Lesson implements Serializable {

    @SerializedName("ID")
    private int id;
    @SerializedName("INDEX")
    private int index;
    @SerializedName("LESSON_TIME_LENGTH")
    private int lessonTimeLength;
    @SerializedName("LESSON_START_TIME_TS")
    private long lessonStartTimeTS;
    @SerializedName("LESSON_FINISH_TIME_TS")
    private long lessonFinishTimeTS;
    @SerializedName("STUDENT_ID")
    private int studentId;
    @SerializedName("STUDENT_NAME")
    private String studentName;
    @SerializedName("STUDENT_langNative")
    private String studentLangNative;
    @SerializedName("STUDENT_JOINED")
    private int studentJoined;
    @SerializedName("TUTOR_ID")
    private int tutorId;
    @SerializedName("TUTOR_NAME")
    private String tutorName;
    @SerializedName("TUTOR_langNative")
    private String tutorLangNative;
    @SerializedName("TUTOR_JOINED")
    private int tutorJoined;
    @SerializedName("STARTED")
    private int started;
    @SerializedName("FINISHED")
    private int finished;
    @SerializedName("STUDYLANG_CODE_IN_LESSONS_CHEDULE")
    private int studyLangCode;
    @SerializedName("STUDYLANG_IN_LESSONS_CHEDULE")
    private String studyLang;
    @SerializedName("LESSON_TYPE")
    private int lessonTypeApi;
    @SerializedName("VOCABOOKS_ID")
    private int bookId;
    private String bookName;
    private String bookNameCombined;
    private int lessonType;
    private boolean hasNextLessonInARow;

    public static Lesson createLesson(Context context, ReceiveCallModel receiveCallModel) {
        Lesson lesson = new Lesson();
        lesson.setId(Utils.parseInt(receiveCallModel.getLessonId()));
        final int type = getLessonTypeFromStudyRole(Utils.parseInt(receiveCallModel.getStudyRole()));
        final int myUid = SharedPreferencesDB.getInstance(context).getRealUid();
        final int uid = Utils.parseInt(receiveCallModel.getId());
        lesson.setLessonType(type);
        if (type == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
            lesson.setStudentId(myUid);
            lesson.setTutorId(uid);
        } else {
            lesson.setStudentId(uid);
            lesson.setTutorId(myUid);
        }
        return lesson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLessonTimeLength() {
        return lessonTimeLength;
    }

    public void setLessonTimeLength(int lessonTimeLength) {
        this.lessonTimeLength = lessonTimeLength;
    }

    public long getLessonStartTimeTS() {
        return lessonStartTimeTS;
    }

    public void setLessonStartTimeTS(long lessonStartTimeTS) {
        this.lessonStartTimeTS = lessonStartTimeTS;
    }

    public long getLessonFinishTimeTS() {
        return lessonFinishTimeTS;
    }

    public void setLessonFinishTimeTS(long lessonFinishTimeTS) {
        this.lessonFinishTimeTS = lessonFinishTimeTS;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentLangNative() {
        return studentLangNative;
    }

    public void setStudentLangNative(String studentLangNative) {
        this.studentLangNative = studentLangNative;
    }

    public int getStudentJoined() {
        return studentJoined;
    }

    public void setStudentJoined(int studentJoined) {
        this.studentJoined = studentJoined;
    }

    public int getTutorId() {
        return tutorId;
    }

    public void setTutorId(int tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getTutorLangNative() {
        return tutorLangNative;
    }

    public void setTutorLangNative(String tutorLangNative) {
        this.tutorLangNative = tutorLangNative;
    }

    public int getTutorJoined() {
        return tutorJoined;
    }

    public void setTutorJoined(int tutorJoined) {
        this.tutorJoined = tutorJoined;
    }

    public int getStarted() {
        return started;
    }

    public void setStarted(int started) {
        this.started = started;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getStudyLangCode() {
        return studyLangCode;
    }

    public void setStudyLangCode(int studyLangCode) {
        this.studyLangCode = studyLangCode;
    }

    public String getStudyLang() {
        return studyLang;
    }

    public void setStudyLang(String studyLang) {
        this.studyLang = studyLang;
    }

    public int getLessonTypeApi() {
        return lessonTypeApi;
    }

    public void setLessonTypeApi(int lessonTypeApi) {
        this.lessonTypeApi = lessonTypeApi;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookNameCombined() {
        return bookNameCombined;
    }

    public void setBookNameCombined(String bookNameCombined) {
        this.bookNameCombined = bookNameCombined;
    }

    public int getLessonType() {
        return lessonType;
    }

    public void setLessonType(int lessonType) {
        this.lessonType = lessonType;
    }

    public boolean hasNextLessonInARow() {
        return hasNextLessonInARow;
    }

    public void setHasNextLessonInARow(boolean hasNextLessonInARow) {
        this.hasNextLessonInARow = hasNextLessonInARow;
    }

    /**
     * get Lesson type from studyRole
     * studyRole from caller >
     * STUDENT Role is TUTOR Lesson Type and else
     *
     * @param studyRole
     * @return
     */
    private static int getLessonTypeFromStudyRole(int studyRole) {
        if (studyRole == Constant.STUDY_ROLE_STUDENT)
            return Constant.API_VALUE.LIST_LESSON_FOR_STUDENT;
        if (studyRole == Constant.STUDY_ROLE_TUTOR)
            return Constant.API_VALUE.LIST_LESSON_FOR_TUTOR;
        return Constant.API_VALUE.LIST_LESSON_FOR_ADMIN;
    }

    public boolean isAdmin() {
        return lessonType == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN;
    }

    public boolean isTutor() {
        return lessonType == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR;
    }

    public boolean isStudent() {
        return lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", index=" + index +
                ", lessonTimeLength=" + lessonTimeLength +
                ", lessonStartTimeTS=" + lessonStartTimeTS +
                ", lessonFinishTimeTS=" + lessonFinishTimeTS +
                ", studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", studentJoined=" + studentJoined +
                ", tutorId=" + tutorId +
                ", tutorName='" + tutorName + '\'' +
                ", tutorJoined=" + tutorJoined +
                ", started=" + started +
                ", finished=" + finished +
                ", studyLangCode=" + studyLangCode +
                ", studyLang='" + studyLang + '\'' +
                ", bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", bookNameCombined='" + bookNameCombined + '\'' +
                ", lessonType=" + lessonType +
                ", hasNextLessonInARow=" + hasNextLessonInARow +
                '}';
    }
}
