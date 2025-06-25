package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

public class VocaSearch implements IVocaFullPlayTTSItem {

    @SerializedName("ID")
    private int id;
    @SerializedName("VOCA")
    private String voca;
    @SerializedName("VOCA_DISPLAY")
    private String vocaDisplay;
    @SerializedName("VOCA_TTS")
    private String vocaTTS;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("MEANING_ENG")
    private String meaningEnglish;
    @SerializedName("MEANING_TTS")
    private String meaningTTS;
    @SerializedName("PRONOUNCE")
    private String pronounce;
    @SerializedName("VOCA_KNOW")
    private int vocaKnow;
    @SerializedName("VOCA_KNOWPRONOUNCE")
    private int vocaKnowPronounce;
    @SerializedName("BELONG_TO_VOCABOOK")
    private int belongToBook;
    @SerializedName("HAS_VOICE_FILE")
    private int hasVoiceFile;
    @SerializedName("FILE_VERSION")
    private int fileVersion;
    private boolean checked;
    private boolean playing;
    private String path;
    private int type;
    private int index;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMeaning() {
        String result = meaning;
//        if (TextUtils.isEmpty(result) && !TextUtils.isEmpty(getMeaningEnglish())) {
//            result = "(" + getMeaningEnglish() + ")";
//        }
        return result;
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

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
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

    public boolean isBelongToBook() {
        return belongToBook == 1;
    }

    public int getBelongToBook() {
        return belongToBook;
    }

    public void setBelongToBook(boolean belongToBook) {
        this.belongToBook = belongToBook ? 1 : 0;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
        return null;
    }

    @Override
    public Integer getVIId() {
        return getId();
    }

    @Override
    public String getVIVoca() {
        return getVoca();
    }

    @Override
    public Integer getVIVocaKnow() {
        return getVocaKnow();
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return getVocaKnowPronounce();
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
        setVocaKnow(value);
    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {
        setVocaKnowPronounce(value);
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
        return getId();
    }

    @Override
    public void setVIVocaType(Integer value) {
        setType(value);
    }

    @Override
    public void setVIVocaId(Integer value) {
        setId(value);
    }
}
