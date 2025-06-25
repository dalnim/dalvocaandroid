package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VocaDetailInfo implements AmkiItem, IVocaFullPlayTTSItem {

    @SerializedName("VOCA_ID")
    private int vocaId;
    @SerializedName("VOCA_TYPE")
    private int vocaType;
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
    @SerializedName("MEANING_DETAILED")
    private String meaningDetailed;
    @SerializedName("MEANING_ENG_DETAILED")
    private String meaningEngDetailed;
    @SerializedName("STUDY_COUNT")
    private int studyCount;
    @SerializedName("VOCA_KNOW")
    private int vocaKnow;
    @SerializedName("VOCA_KNOWPRONOUNCE")
    private int vocaKnowPronounce;
    @SerializedName("EXAMPLE_SENTENCES")
    private List<VocaDetailInfo> exampleSentences;
    @SerializedName("HANJA_LIST")
    private List<VocaHanja> hanjaList;
    @SerializedName("HAS_VOICE_FILE")
    private int hasVoiceFile;
    @SerializedName("FILE_VERSION")
    private int fileVersion;
    @SerializedName("BOOKMARK")
    private int bookmark;
    private boolean checked;
    private boolean playing;
    private String path;
    private int index;

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public String getVocaDisplay() {
        return Utils.isEmpty(vocaDisplay) ? voca : vocaDisplay;
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

    public String getMeaningDetailed() {
        return meaningDetailed;
    }

    public void setMeaningDetailed(String meaningDetailed) {
        this.meaningDetailed = meaningDetailed;
    }

    public String getMeaningEngDetailed() {
        return meaningEngDetailed;
    }

    public void setMeaningEngDetailed(String meaningEngDetailed) {
        this.meaningEngDetailed = meaningEngDetailed;
    }

    public int getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(int studyCount) {
        this.studyCount = studyCount;
    }

    public int getVocaKnow() {
        return vocaKnow;
    }

    public void setVocaKnow(int vocaKnow) {
        this.vocaKnow = vocaKnow;
    }

    public int getVocaKnowPronounce() {
        return vocaKnowPronounce;
    }

    public void setVocaKnowPronounce(int vocaKnowPronounce) {
        this.vocaKnowPronounce = vocaKnowPronounce;
    }

    public List<VocaDetailInfo> getExampleSentences() {
        if (exampleSentences == null) {
            exampleSentences = new ArrayList<>();
        }
        return exampleSentences;
    }

    public void setExampleSentences(List<VocaDetailInfo> exampleSentences) {
        this.exampleSentences = exampleSentences;
    }

    public List<VocaHanja> getHanjaList() {
        if (hanjaList == null) {
            hanjaList = new ArrayList<>();
        }
        return hanjaList;
    }

    public void setHanjaList(List<VocaHanja> hanjaList) {
        this.hanjaList = hanjaList;
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

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isBookmark() {
        return bookmark > 0;
    }
    public void swapBookmark() {
        setBookmark(bookmark > 0 ? 0 : 1);
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int getAmkiId() {
        return getVocaId();
    }

    @Override
    public int getAmkiType() {
        return getVocaType();
    }

    @Override
    public int getAmkiKnow() {
        return getVocaKnow();
    }

    @Override
    public int getAmkiKnowPronounce() {
        return getVocaKnowPronounce();
    }

    @Override
    public String getAmkiEvaluationGrade() {
        return null;
    }

    @Override
    public String getAmki() {
        return getVoca();
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
    public Integer getVIId() {
        // This model has no Id
        return vocaId;
    }

    @Override
    public String getVIVoca() {
        return vocaDisplay == null || vocaDisplay.trim().length() == 0 ? voca : vocaDisplay;
    }

    @Override
    public Integer getVIVocaKnow() {
        return vocaKnow;
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return vocaKnowPronounce;
    }


    @Override
    public String getVIPronounce() {
        return pronounce;
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        String meaning = getMeaning();
//        if (TextUtils.isEmpty(meaning) && !TextUtils.isEmpty(getMeaningEnglish())) {
//            meaning = "(" + getMeaningEnglish() + ")";
//        }
        return meaning;
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return meaningDetailed;
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
        return getMeaningEngDetailed();
    }

    @Override
    public String getVIMeaningEngTts() {
        return getMeaningEnglish();
    }

    @Override
    public Integer getVIBookmark() {
        return bookmark;
    }

    @Override
    public boolean isVIBookmark() {
        return isBookmark();
    }

    @Override
    public void setVIId(Integer value) {
        //DO nothing, it has no id
    }

    @Override
    public void setVIVoca(String value) {
        voca = value;
        vocaDisplay = value;
    }

    @Override
    public String getVIVocaTTS() {
        return vocaTTS;
    }

    @Override
    public void setVIVocaTTS(String value) {
        vocaTTS = value;
    }

    @Override
    public void setVIPronounce(String value) {
        pronounce = value;
    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguage, String value) {
        meaning = value;
    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguage, String value) {
        meaningDetailed = value;
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
        vocaKnow = value;
    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {
        vocaKnowPronounce = value;
    }

    @Override
    public void setVIBookmark(Integer value) {
        // Do nothing.
        bookmark = value;
    }

    @Override
    public void swapVIBookmark() {
        swapBookmark();
    }

    @Override
    public Integer getVIVocaType() {
        return vocaType;
    }

    @Override
    public Integer getVIVocaId() {
        return vocaId;
    }

    @Override
    public void setVIVocaType(Integer value) {
        vocaType = value;
    }

    @Override
    public void setVIVocaId(Integer value) {
        vocaId = value;
    }

    @Override
    public Integer getVIIndex() {
        return index;
    }

    @Override
    public String getVIPosAll() {
        return "";
    }

    @Override
    public Integer getVIVocaTypeBase() {
        return -1;
    }

    @Override
    public Integer getVIVocaIdBase() {
        return -1;
    }

    @Override
    public void setVIVocaTypeBase(Integer value) {
        //Do nothing
    }

    @Override
    public void setVIVocaIdBase(Integer value) {
        //Do nothing
    }
}
