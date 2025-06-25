package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.google.gson.annotations.SerializedName;

public class StudentVoice extends NativeSpeaker implements IVocaFullPlayTTSItem {

    @SerializedName("STUDENT_ID")
    private int studentId;
    @SerializedName("FILE_NAME")
    private String fileName;
    @SerializedName("VERSION")
    private int version;
    @SerializedName("FILE_VERSION")
    private int fileVersion;
    private VocaDetailInfo voca;
    private boolean checked;
    private boolean playing;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public VocaDetailInfo getVoca() {
        return voca;
    }

    public void setVoca(VocaDetailInfo voca) {
        this.voca = voca;
    }

    public int getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(int fileVersion) {
        this.fileVersion = fileVersion;
    }

    @Override
    public boolean isVIChecked() {
        return checked;
    }

    @Override
    public void setVIChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean isVIPlaying() {
        return playing;
    }

    @Override
    public void setVIPlaying(boolean playing) {
        this.playing = playing;
    }

    @Override
    public boolean isVIRecording() {
        return false;
    }

    @Override
    public void setVIRecording(boolean recording) {

    }

    @Override
    public String getVIPath() {
        return fileName;
    }

    @Override
    public void setVIPath(String path) {
        this.fileName = path;
    }

    @Override
    public void setVIIndex(Integer index) {
        setIndex(index);
    }

    @Override
    public boolean hasVIVoiceFile() {
        return getHasVoiceFile() == 1;
    }

    @Override
    public int getVIVoiceFileVersion() {
        return getFileVersion();
    }

    @Override
    public String getPersonAB() {
        return "";
    }

    @Override
    public Integer getVIIndex() {
        return getIndex();
    }

    @Override
    public String getVIPosAll() {
        return null;
    }

    @Override
    public Integer getVIId() {
        return getStudentId();
    }

    @Override
    public String getVIVoca() {
        return null;
    }

    @Override
    public Integer getVIVocaKnow() {
        return null;
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return null;
    }

    @Override
    public String getVIVocaTTS() {
        return null;
    }

    @Override
    public String getVIPronounce() {
        return null;
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        return "";
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return null;
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguage) {
        return "";
    }

    @Override
    public String getVIMeaningEng() {
        return "";
    }

    @Override
    public String getVIMeaningEngDetailed() {
        return "";
    }

    @Override
    public String getVIMeaningEngTts() {
        return "";
    }


    @Override
    public Integer getVIBookmark() {
        return null;
    }

    @Override
    public boolean isVIBookmark() {
        return false;
    }

    @Override
    public void setVIId(Integer value) {

    }

    @Override
    public void setVIVoca(String value) {

    }

    @Override
    public void setVIVocaTTS(String value) {

    }

    @Override
    public void setVIPronounce(String value) {

    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguage, String value) {

    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguage, String value) {

    }

    @Override
    public void setVIMeaningTts(EnumLanguage enumLanguage, String value) {

    }

    @Override
    public void setVIMeaningEng(String value) {

    }

    @Override
    public void setVIMeaningEngDetailed(String value) {

    }

    @Override
    public void setVIMeaningEngTts(String value) {

    }

    @Override
    public void setVIVocaKnow(Integer value) {

    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {

    }

    @Override
    public void setVIBookmark(Integer value) {

    }

    @Override
    public void swapVIBookmark() {

    }

    @Override
    public Integer getVIVocaTypeBase() {
        return null;
    }

    @Override
    public Integer getVIVocaIdBase() {
        return null;
    }

    @Override
    public void setVIVocaTypeBase(Integer value) {

    }

    @Override
    public void setVIVocaIdBase(Integer value) {

    }

    @Override
    public Integer getVIVocaType() {
        return null;
    }

    @Override
    public Integer getVIVocaId() {
        return null;
    }

    @Override
    public void setVIVocaType(Integer value) {

    }

    @Override
    public void setVIVocaId(Integer value) {

    }
}
