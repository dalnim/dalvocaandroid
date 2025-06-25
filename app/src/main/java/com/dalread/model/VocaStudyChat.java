package com.dalread.model;

import android.text.TextUtils;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VocaStudyChat implements IVocaFullPlayTTSItem, AmkiItem, Serializable {

    @SerializedName("ID")
    private int id;
    @SerializedName("INDEX")
    private int index;
    @SerializedName("PERSON_AB")
    private String personAB;
    @SerializedName("DISP_ORDER")
    private int displayOrder;
    @SerializedName("VOCA_ID")
    private int vocaId;
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
    @SerializedName("MEANING_FOR_HIDE_ALL")
    private String meaningForHideAll;
    @SerializedName("VOCA_TYPE")
    private int type;
    @SerializedName("VOCA_KNOW")
    private int vocaKnow;
    @SerializedName("VOCA_KNOWPRONOUNCE")
    private int vocaKnowPronounce;
    @SerializedName("STUDY_COUNT")
    private int studyCount;
    @SerializedName("NAME")
    private String tutorName;
    @SerializedName("FEEDBACK_MESSAGE")
    private String feedbackMessage;
    @SerializedName("FEEDBACK_DATE")
    private String feedbackDate;
    @SerializedName("studyLang")
    private String studyLang;
    @SerializedName("EVALUATE_VOCA_GRADE")
    private String evaluateVocaGrade;
    @SerializedName("EVALUATE_VOCA_GRADE_TUTORS")
    private String evaluateVocaGradeTutors;
    @SerializedName("VOCA_DISPLAY_RUBY_TEXT")
    private String vocaDisplayRubyText;
    @SerializedName("VOCA_DISPLAY_RUBY_VOCA_LIST")
    private List<VocaStudyChat> vocaDisplayRubyVocaList;
    @SerializedName("ID_IN_VOCABOOK")
    private int idInVocaBook;
    @SerializedName("HAS_VOICE_FILE")
    private int hasVoiceFile;
    @SerializedName("FILE_VERSION")
    private Object fileVersion;
    private boolean checked;
    private boolean playing;
    private String path;
    private Integer tblParentSection;
    private Integer tblParentRow;
    private Integer tblSection;
    private Integer tblRow;
    private int tblTypeSync;
    private int pnType;
    private String rubyVocaIds;
    private String rubyVocaTypes;
    private boolean displayEvaluation = true;
    private String hanja;
    private String jmdictMeaning; //JM Dictionary (meaning for Japanese language, usually Spanish or German)
    private String jmdictMeaningEng; //JM Dictionary (English meaning for Japanese language)
    protected int bookmark;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
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

    public String getJmdictMeaning() {
        return jmdictMeaning;
    }

    public void setJmdictMeaning(String jmdictMeaning) {
        this.jmdictMeaning = jmdictMeaning;
    }

    public String getJmdictMeaningEng() {
        return jmdictMeaningEng;
    }

    public void setJmdictMeaningEng(String jmdictMeaningEng) {
        this.jmdictMeaningEng = jmdictMeaningEng;
    }

    public String getMeaningForHideAll() {
        return meaningForHideAll;
    }

    public void setMeaningForHideAll(String meaningForHideAll) {
        this.meaningForHideAll = meaningForHideAll;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVocaKnow() {
        return vocaKnow;
    }

    public void setVocaKnow(int vocaKnow) {
        this.vocaKnow = vocaKnow;
    }

    public boolean isVocaKnow() {
        return getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public int getVocaKnowPronounce() {
        return vocaKnowPronounce;
    }

    public void setVocaKnowPronounce(int vocaKnowPronounce) {
        this.vocaKnowPronounce = vocaKnowPronounce;
    }

    public boolean isVocaKnowPronounce() {
        return getVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public int getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(int studyCount) {
        this.studyCount = studyCount;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(String feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public String getStudyLang() {
        return studyLang;
    }

    public void setStudyLang(String studyLang) {
        this.studyLang = studyLang;
    }

    public String getEvaluateVocaGrade() {
        return evaluateVocaGrade;
    }

    public void setEvaluateVocaGrade(String evaluateVocaGrade) {
        this.evaluateVocaGrade = evaluateVocaGrade;
    }

    public String getEvaluateVocaGradeTutors() {
        return evaluateVocaGradeTutors;
    }

    public void setEvaluateVocaGradeTutors(String evaluateVocaGradeTutors) {
        this.evaluateVocaGradeTutors = evaluateVocaGradeTutors;
    }

    public String getEvaluateVocaGradeValue() {
        return TextUtils.isEmpty(evaluateVocaGrade) ? evaluateVocaGradeTutors : evaluateVocaGrade;
    }

    public String getVocaDisplayRubyText() {
        return vocaDisplayRubyText;
    }

    public void setVocaDisplayRubyText(String vocaDisplayRubyText) {
        this.vocaDisplayRubyText = vocaDisplayRubyText;
    }

    public List<VocaStudyChat> getVocaDisplayRubyVocaList() {
        if (vocaDisplayRubyVocaList == null) {
            setVocaDisplayRubyVocaList(new ArrayList<>());
        }
        return vocaDisplayRubyVocaList;
    }

    public void setVocaDisplayRubyVocaList(List<VocaStudyChat> rubyVocaList) {
        this.vocaDisplayRubyVocaList = rubyVocaList;
    }

    public int getIdInVocaBook() {
        return idInVocaBook;
    }

    public void setIdInVocaBook(int idInVocaBook) {
        this.idInVocaBook = idInVocaBook;
    }

    public boolean hasVoiceFile() {
        return hasVoiceFile == 1;
    }

    public void setHasVoiceFile(boolean hasVoiceFile) {
        this.hasVoiceFile = hasVoiceFile ? 1 : 0;
    }

    public int getFileVersion() {
        try {
            return Integer.parseInt(String.valueOf(fileVersion));
        } catch (NumberFormatException e) {
        }
        return 0;
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

    public String getPersonAB() {
        return personAB;
    }

    public void setPersonAB(String personAB) {
        this.personAB = personAB;
    }

    public String getHanja() {
        return hanja;
    }

    public void setHanja(String hanja) {
        this.hanja = hanja;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isBookmark() {
        return bookmark > 0 ? true : false;
    }

    public void setBookmark(boolean isBookmark) {
        this.bookmark = isBookmark ? 1 : 0;
    }

    public void swapBookmark() {
        setBookmark(!isBookmark());
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
        return getType();
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
        return getEvaluateVocaGradeValue();
    }

    @Override
    public String getAmki() {
        return getVoca();
    }

    public Integer getTblParentSection() {
        return tblParentSection;
    }

    public void setTblParentSection(Integer tblParentSection) {
        this.tblParentSection = tblParentSection;
    }

    public Integer getTblParentRow() {
        return tblParentRow;
    }

    public void setTblParentRow(Integer tblParentRow) {
        this.tblParentRow = tblParentRow;
    }

    public Integer getTblSection() {
        return tblSection;
    }

    public void setTblSection(Integer tblSection) {
        this.tblSection = tblSection;
    }

    public Integer getTblRow() {
        return tblRow;
    }

    public void setTblRow(Integer tblRow) {
        this.tblRow = tblRow;
    }

    public int getTblTypeSync() {
        return tblTypeSync;
    }

    public void setTblTypeSync(int tblTypeSync) {
        this.tblTypeSync = tblTypeSync;
    }

    public int getPnType() {
        return pnType;
    }

    public void setPnType(int pnType) {
        this.pnType = pnType;
    }

    public String getRubyVocaIds() {
        return rubyVocaIds;
    }

    public String getRubyVocaTypes() {
        return rubyVocaTypes;
    }

    public void setRubyVocaIdsAndTypes(String rubyVocaIds, String rubyVocaTypes) {
        if (TextUtils.isEmpty(rubyVocaIds)) {
            this.rubyVocaIds = rubyVocaIds;
            this.rubyVocaTypes = rubyVocaTypes;
        } else {
            StringBuilder ids = new StringBuilder();
            StringBuilder types = new StringBuilder();
            List<String> list = new ArrayList<>();
            String[] idArray = rubyVocaIds.split(",");
            String[] typeArray = rubyVocaTypes.split(",");
            for (int i = 0; i < idArray.length; i++) {
                if (!list.contains(idArray[i])) {
                    list.add(idArray[i]);
                    ids.append(",").append(idArray[i]);
                    types.append(",").append(typeArray[i]);
                }
            }
            this.rubyVocaIds = ids.substring(1);
            this.rubyVocaTypes = types.substring(1);
        }
    }

    public boolean updateVocaDisplayRubyText(int vocaId, String key, int newValue) {
        return Utils.updateRubyText(getVocaDisplayRubyText(), vocaId, key, newValue, this::setVocaDisplayRubyText);
    }

    public boolean isDisplayEvaluation() {
        return displayEvaluation;
    }

    public void setDisplayEvaluation(boolean displayEvaluation) {
        this.displayEvaluation = displayEvaluation;
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
        return bookmark;
    }

    @Override
    public boolean isVIBookmark() {
        return isBookmark();
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
    public void setVIId(Integer value) {
        id = value;
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
        //TODO : Should have meaningDetailed variable
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
        swapBookmark();
    }

    @Override
    public Integer getVIVocaType() {
        //TODO : SHould have vocaType in VocaStudyChat
        return Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
    }

    @Override
    public Integer getVIVocaId() {
        return vocaId;
    }

    @Override
    public void setVIVocaType(Integer value) {
        //TODO : SHould have vocaType in VocaStudyChat
    }

    @Override
    public void setVIVocaId(Integer value) {
        vocaId = value;
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
