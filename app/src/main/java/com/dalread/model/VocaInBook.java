package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;
//이건 SERVER_VOCABOOK 이다.
public class VocaInBook implements AmkiItem, IVocaFullPlayTTSItem {

    @SerializedName("ID")
    private int id;
    @SerializedName("ID_IN_SERVER_VOCABOOK")
    private int idInServerVocabook;
    @SerializedName("USE_SERVER_VOCABOOK_DATA")
    private int useServerVocabookData;
    @SerializedName("INDEX")
    private int index;
    @SerializedName("PERSON_AB")
    private String personAB;
    @SerializedName("VOCABOOKS_ID")
    private int bookId;
    @SerializedName("VOCA_ID")
    private int vocaId;
    @SerializedName("VOCA")
    private String voca;
    @SerializedName("VOCA_DISPLAY")
    private String vocaDisplay;
    @SerializedName("VOCA_TTS")
    private String vocaTTS;
    @SerializedName("DISP_ORDER")
    private int displayOrder;
    @SerializedName("VOCA_TYPE")
    private int vocaType;
    @SerializedName("studyLang")
    private int studyLang;
    @SerializedName("PRONOUNCE")
    private String pronounce;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("MEANING_ENG")
    private String meaningEnglish;
    @SerializedName("MEANING_TTS")
    private String meaningTTS;
    @SerializedName("VOCA_KNOW")
    private int vocaKnow;
    @SerializedName("VOCA_KNOWPRONOUNCE")
    private int vocaKnowPronounce;
    @SerializedName("EVALUATE_VOCA_GRADE")
    private String evaluateVocaGrade;
    @SerializedName("HAS_VOICE_FILE")
    private int hasVoiceFile;
    @SerializedName("FILE_VERSION")
    private int fileVersion;
    private boolean checked;
    private boolean playing;
    private boolean recording;
    private String path;
    private int bookmark;
    private String meaningDetailed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdInServerVocabook() {
        return idInServerVocabook;
    }

    public void setIdInServerVocabook(int idInServerVocabook) {
        this.idInServerVocabook = idInServerVocabook;
    }

    public int getUseServerVocabookData() {
        return useServerVocabookData;
    }

    public void setUseServerVocabookData(int useServerVocabookData) {
        this.useServerVocabookData = useServerVocabookData;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
        return recording;
    }

    @Override
    public void setVIRecording(boolean recording) {
        this.recording = recording;
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
        return hasVoiceFile == Constant.INT_BOOLEAN.TRUE;
    }

    @Override
    public void setVIVoiceFile(Integer value) {
        this.hasVoiceFile = value;
    }

    @Override
    public int getVIVoiceFileVersion() {
        return getFileVersion();
    }

    public String getPersonAB() {
        return personAB;
    }

    public void setPersonAB(String personAB) {
        this.personAB = personAB;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
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

    private String getVocaTTS() {
        return vocaTTS;
    }

    private void setVocaTTS(String vocaTTS) {
        this.vocaTTS = vocaTTS;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public int getStudyLang() {
        return studyLang;
    }

    public void setStudyLang(int studyLang) {
        this.studyLang = studyLang;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getMeaning() {
        String result = meaning;
//        if (TextUtils.isEmpty(result) && !TextUtils.isEmpty(getMeaningEnglish())) {
//            result = "(" + getMeaningEnglish() + ")";
//        }
        return result;
    }

    public String getMeaningDetailed() {
        return meaningDetailed;
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
        return Utils.isEmpty(meaningTTS) ? getMeaning() : meaningTTS;
    }

    public void setMeaningTTS(String meaningTTS) {
        this.meaningTTS = meaningTTS;
    }

    public void setMeaningDetailed(String meaningDetailed) {
        this.meaningDetailed = meaningDetailed;
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

    public String getEvaluateVocaGrade() {
        return evaluateVocaGrade;
    }

    public void setEvaluateVocaGrade(String evaluateVocaGrade) {
        this.evaluateVocaGrade = evaluateVocaGrade;
    }

    public boolean hasVoiceFile() {
        return hasVoiceFile == Constant.INT_BOOLEAN.TRUE;
    }

    public void setHasVoiceFile(boolean hasVoiceFile) {
        this.hasVoiceFile = hasVoiceFile ? Constant.INT_BOOLEAN.TRUE : Constant.INT_BOOLEAN.FASLE;
    }

    public int getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(int fileVersion) {
        this.fileVersion = fileVersion;
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
        return getEvaluateVocaGrade();
    }

    @Override
    public String getAmki() {
        return getVoca();
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
    public Integer getVIId() {
        return id;
    }

    @Override
    public String getVIVoca() {
        return vocaDisplay;
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
    public String getVIVocaTTS() {
        return getVocaTTS();
    }

    @Override
    public String getVIPronounce() {
        return pronounce;
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        return meaning;
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return getMeaningDetailed();
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
        return bookmark;
    }

    @Override
    public boolean isVIBookmark() {
        return bookmark > 0 ? true : false;
    }

    @Override
    public void setVIId(Integer value) {
        id = value;
    }

    @Override
    public void setVIVoca(String value) {
        voca = value;
        vocaDisplay = value;
    }

    @Override
    public void setVIVocaTTS(String value) {
        setVocaTTS(value);
//        vocaTTS = value;
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
        setMeaningDetailed(value);
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
        bookmark = value;
    }

    @Override
    public void swapVIBookmark() {
        if (isVIBookmark()) {
            setVIBookmark(0);
        } else {
            setVIBookmark(1);
        }
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

    }

    @Override
    public void setVIVocaIdBase(Integer value) {

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
}
