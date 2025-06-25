package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class VocaFromAllVocaBook implements Serializable {

    @SerializedName("STUDY_VOCA_LIST")
    private ArrayList<VocaStudyChat> studyVocaList;
    @SerializedName("EXAM_VOCA_LIST")
    private ArrayList<VocaStudyChatExam> examVocaList;
    @SerializedName("ALL_VOCA_LIST")
    private ArrayList<VocaStudyChat> allVocaList;

    public ArrayList<VocaStudyChat> getStudyVocaList() {
        if (studyVocaList == null) {
            setStudyVocaList(new ArrayList<>());
        }
        return studyVocaList;
    }

    public void setStudyVocaList(ArrayList<VocaStudyChat> studyVocaList) {
        this.studyVocaList = studyVocaList;
    }

    public ArrayList<VocaStudyChatExam> getExamVocaList() {
        if (examVocaList == null) {
            setExamVocaList(new ArrayList<>());
        }
        return examVocaList;
    }

    public void setExamVocaList(ArrayList<VocaStudyChatExam> examVocaList) {
        this.examVocaList = examVocaList;
    }

    public ArrayList<VocaStudyChat> getAllVocaList() {
        if (allVocaList == null) {
            setAllVocaList(new ArrayList<>());
        }
        return allVocaList;
    }

    public void setAllVocaList(ArrayList<VocaStudyChat> allVocaList) {
        this.allVocaList = allVocaList;
    }
}
