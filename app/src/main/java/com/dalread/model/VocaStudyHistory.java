package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class VocaStudyHistory extends RealmObject implements IVocaFullPlayTTSItem {

    @PrimaryKey
    @SerializedName("ID")
    private int id;
    @SerializedName("VOCA_ID")
    private int vocaId;
    @SerializedName("VOCA_TYPE")
    private int type;
    @SerializedName("VOCA")
    private String voca;
    @SerializedName("VOCA_DISPLAY")
    private String vocaDisplay;
    @SerializedName("VOCA_TTS")
    private String vocaTTS;
    @SerializedName("PRONOUNCE")
    private String pronounce;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("MEANING_ENG")
    private String meaningEnglish;
    @SerializedName("MEANING_TTS")
    private String meaningTTS;
    @SerializedName("STUDENT_ID")
    private int studentId;
    @SerializedName("TUTOR_ID")
    private int tutorId;
    @SerializedName("TUTOR_NAME")
    private String tutorName;
    @SerializedName("STUDY_DATE_TS")
    private long studyDateTS;
    @SerializedName("STUDY_COUNT")
    private int studyCount;
    @SerializedName("studyLang")
    private String studyLang;
    @Ignore
    @SerializedName("HAS_VOICE_FILE")
    private int hasVoiceFile;
    @Ignore
    @SerializedName("FILE_VERSION")
    private int fileVersion;
    @Ignore
    private boolean checked;
    @Ignore
    private boolean playing;
    @Ignore
    private String path;
    @Ignore
    private int index;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public String getVocaDisplay() {
        return vocaDisplay;
    }

    public void setVocaDisplay(String vocaDisplay) {
        this.vocaDisplay = vocaDisplay;
    }

    public String getVocaTTS() {
        return vocaTTS;
    }

    public void setVocaTTS(String vocaTTS) {
        this.vocaTTS = vocaTTS;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getMeaningEnglish() {
        return meaningEnglish;
    }

    public void setMeaningEnglish(String meaningEnglish) {
        this.meaningEnglish = meaningEnglish;
    }

    public String getMeaningTTS() {
        return meaningTTS;
    }

    public void setMeaningTTS(String meaningTTS) {
        this.meaningTTS = meaningTTS;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public long getStudyDateTS() {
        return studyDateTS;
    }

    public void setStudyDateTS(long studyDate) {
        this.studyDateTS = studyDateTS;
    }

    public int getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(int studyCount) {
        this.studyCount = studyCount;
    }

    public String getStudyLang() {
        return studyLang;
    }

    public void setStudyLang(String studyLang) {
        this.studyLang = studyLang;
    }

    public boolean hasVoiceFile() {
        return hasVoiceFile == 1;
    }

    public void setHasVoiceFile(boolean hasVoiceFile) {
        this.hasVoiceFile = hasVoiceFile ? 1 : 0;
    }

    public int getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(int fileVersion) {
        this.fileVersion = fileVersion;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPath(String path) {
        this.path = path;
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
        return path;
    }

    @Override
    public void setVIPath(String path) {
        this.path = path;
    }

    @Override
    public void setVIIndex(Integer index) {
        setIndex(index);
    }

    @Override
    public boolean hasVIVoiceFile() {
        return hasVoiceFile();
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
        return "";
    }

    @Override
    public Integer getVIId() {
        return getId();
    }

    @Override
    public String getVIVoca() {
        return getVocaDisplay();
    }

    @Override
    public Integer getVIVocaKnow() {
        return Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
    }

    @Override
    public String getVIVocaTTS() {
        return getVocaTTS();
    }

    @Override
    public String getVIPronounce() {
        return getPronounce();
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        return getMeaning();
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return "";
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguage) {
        return getMeaningTTS();
    }

    @Override
    public String getVIMeaningEng() {
        return getMeaningEnglish();
    }

    @Override
    public String getVIMeaningEngDetailed() {
        return "";
    }

    @Override
    public String getVIMeaningEngTts() {
        return getMeaningEnglish();
    }

    @Override
    public Integer getVIBookmark() {
        return Constant.INT_BOOLEAN.FASLE;
    }

    @Override
    public boolean isVIBookmark() {
        return false;
    }

    @Override
    public void setVIId(Integer value) {
        setId(value);
    }

    @Override
    public void setVIVoca(String value) {
        setVoca(value);
        setVocaDisplay(value);
    }

    @Override
    public void setVIVocaTTS(String value) {
        setVocaTTS(value);
    }

    @Override
    public void setVIPronounce(String value) {
        setPronounce(value);
    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguage, String value) {
        setMeaning(value);
    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguage, String value) {

    }

    @Override
    public void setVIMeaningTts(EnumLanguage enumLanguage, String value) {
        setMeaningTTS(value);
    }

    @Override
    public void setVIMeaningEng(String value) {
        setMeaningEnglish(value);
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
        return getType();
    }

    @Override
    public Integer getVIVocaId() {
        return getVocaId();
    }

    @Override
    public void setVIVocaType(Integer value) {
        setType(value);
    }

    @Override
    public void setVIVocaId(Integer value) {
        setVocaId(value);
    }
}
