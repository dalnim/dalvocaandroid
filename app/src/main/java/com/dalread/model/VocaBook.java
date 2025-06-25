package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocabooksCommon;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
//이건 SERVER_VOCABOOKS 테이블을 말하는거 같다.
public class VocaBook implements Serializable, IVocabooksCommon {

    @SerializedName("ID")
    private int id;
    @SerializedName("INDEX")
    private int index;
    @SerializedName("STUDENT_ID")
    private int studentId;
    @SerializedName("NAME")
    private String name;
    @SerializedName("WORD_COUNT")
    private int wordCount;
    @SerializedName("IMAGE_ID")
    private int imageId;
    @SerializedName("VERSION")
    private int version;
    @SerializedName("studyLang")
    private int studyLang;
    @SerializedName("langDisplay")
    private int langDisplay;
    private VocaCountInBook countInBook;
    private boolean checked;
    private boolean parentChecked;
    private int USED;
    private int recordedVocaCount;
    private String nameStudyLang;

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

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameStudyLang() {
        return nameStudyLang;
    }

    public void setNameStudyLang(String nameStudyLang) {
        this.nameStudyLang = nameStudyLang;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getStudyLang() {
        return studyLang;
    }

    public void setStudyLang(int studyLang) {
        this.studyLang = studyLang;
    }

    public int getLangDisplay() {
        return langDisplay;
    }

    public void setLangDisplay(int langDisplay) {
        this.langDisplay = langDisplay;
    }

    public VocaCountInBook getCountInBook() {
        return countInBook;
    }

    public void setCountInBook(VocaCountInBook countInBook) {
        this.countInBook = countInBook;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isParentChecked() {
        return parentChecked;
    }

    public void setParentChecked(boolean parentChecked) {
        this.parentChecked = parentChecked;
    }

    public int getUSED() {
        return USED;
    }

    public void setUSED(int USED) {
        this.USED = USED;
    }

    @Override
    public long getIBookId() {
        return getId();
    }

    @Override
    public long getIBookVocaCount() {
        return getWordCount();
    }

    @Override
    public long getIBookRecordedVocaCount() {
        return recordedVocaCount;
    }

    @Override
    public void setIBookRecordedVocaCount(int value) {
        this.recordedVocaCount = value;
    }

    @Override
    public String getIBookName(EnumLanguage enumLanguage) {
        return getName();
    }

    @Override
    public String getIBookNameEng() {
        return "";
    }

    @Override
    public String getIBookNameStudyLang() {
        return getNameStudyLang();
    }

    @Override
    public long getIBookUsed() {
        return getUSED();
    }

    @Override
    public boolean isIBookHasSubList() {
        return getWordCount() == 0 ? true : false;
    }

    @Override
    public long getIBookCountOfVocaKnow() {
        return 0;
    }


}
