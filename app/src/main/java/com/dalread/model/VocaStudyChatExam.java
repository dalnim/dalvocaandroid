package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VocaStudyChatExam extends VocaStudyChat implements Serializable {

    @SerializedName("QUESTION")
    private String question;
    @SerializedName("CORRECT_ANSWER_NUMBER")
    private int correctAnswerNumber;
    @SerializedName("ANSWER_1")
    private String answer1;
    @SerializedName("ANSWER_2")
    private String answer2;
    @SerializedName("ANSWER_3")
    private String answer3;
    @SerializedName("ANSWER_4")
    private String answer4;
    @SerializedName("EXAM_TYPE")
    private int examType;
    @SerializedName("HANJA_LIST")
    private List<VocaHanja> hanjaList;
    private boolean answer1Selected;
    private boolean answer2Selected;
    private boolean answer3Selected;
    private boolean answer4Selected;
    private int lastSelectedAnswer;
    private boolean hasWrongAnswer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getCorrectAnswerNumber() {
        return correctAnswerNumber;
    }

    public void setCorrectAnswerNumber(int correctAnswerNumber) {
        this.correctAnswerNumber = correctAnswerNumber;
    }

    public String getCorrectAnswer() {
        String correctAnswer = "";
        switch (correctAnswerNumber) {
            case 1:
                correctAnswer = answer1;
                break;
            case 2:
                correctAnswer = answer2;
                break;
            case 3:
                correctAnswer = answer3;
                break;
            case 4:
                correctAnswer = answer4;
                break;
        }
        return correctAnswer;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public int getExamType() {
        return examType;
    }

    public void setExamType(int examType) {
        this.examType = examType;
    }

    public List<VocaHanja> getHanjaList() {
        if (hanjaList == null) {
            setHanjaList(new ArrayList<>());
        }
        return hanjaList;
    }

    public void setHanjaList(List<VocaHanja> hanjaList) {
        this.hanjaList = hanjaList;
    }

    public boolean isAnswer1Selected() {
        return answer1Selected;
    }

    public void setAnswer1Selected(boolean answer1Selected) {
        this.answer1Selected = answer1Selected;
    }

    public boolean isAnswer2Selected() {
        return answer2Selected;
    }

    public void setAnswer2Selected(boolean answer2Selected) {
        this.answer2Selected = answer2Selected;
    }

    public boolean isAnswer3Selected() {
        return answer3Selected;
    }

    public void setAnswer3Selected(boolean answer3Selected) {
        this.answer3Selected = answer3Selected;
    }

    public boolean isAnswer4Selected() {
        return answer4Selected;
    }

    public void setAnswer4Selected(boolean answer4Selected) {
        this.answer4Selected = answer4Selected;
    }

    public int getLastSelectedAnswer() {
        return lastSelectedAnswer;
    }

    public void setLastSelectedAnswer(int lastSelectedAnswer) {
        this.lastSelectedAnswer = lastSelectedAnswer;
    }

    public boolean hasWrongAnswer() {
        return hasWrongAnswer;
    }

    public void setHasWrongAnswer(boolean hasWrongAnswer) {
        this.hasWrongAnswer = hasWrongAnswer;
    }

    public boolean isBookmark() {
        return getBookmark() == 1;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public void setBookmark(boolean isBookmark) {
        this.bookmark = isBookmark ? 1 : 0;
    }


}
