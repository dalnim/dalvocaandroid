package com.dalread.database.sqlite.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.io.Serializable;

public class DIC_WORD_MODEL extends AbstractMeanings implements Serializable, IVocaFullPlayTTSItem {
    public int ID;
    public String WORD;
    public int WORD_SAME_ID;
    public String WORDORI;
    public int WORDORI_ID;
    public int VOCA_TYPE_ORI;
    public int VOCA_ID_ORI;
    public String PRONOUNCE;
    public int PRONOUNCE_USE;
    public String POSALL;
    public String POS;
    public String POS2;
    public String POS3;
    public String POS4;
    public String POS6;
    public String POS5;
    public int WORDLEVEL;
    public String SENTENCE_ID_LIST;
    public String WORD_DISPLAY;
    public String WORD_TTS;
    public String MEANING_TTS;
    public int UID;
    public int CREATOR_TYPE;
    public long UPDATE_DATE;
    public long UPDATE_DATE_BOOKMARK;
    public long UPDATE_DATE_VOCA_KNOWPRONOUNCE;
    public long UPDATE_DATE_VOCA_KNOW;
    public int BOOKMARK;
    public int VOCA_KNOWPRONOUNCE;
    public int VOCA_KNOW;

//    public int VOCA_TYPE;
//    public int VOCA_ID; //Same as ID
    public String MEANING; //Don't use this
    public String MEANING_DETAILED; //Don't use this
    private String SEARCH_HISTORY;
    private int index;
    ///For PlaylistItem
    private boolean checked;  //PlaylistItem에서 플레이할때 선택된것만 플레이 할려고
    private boolean playing; //this voca is play by TTS or voice files
    private boolean recording;
    //    private Context context;
    private String path; //What is this for? Voice file path?. file name only.
    private int hasVoiceFile; //Has voice file is in the phone or not. Not related with path.
    private int difficultWordsCount;
    private int repeat; //Listen comprehension 2
    private int idInVocaBook;
//    private int vocaBookType;


    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getVOCA_TYPE() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
    }

//    public void setVOCA_TYPE(int VOCA_TYPE) {
//        this.VOCA_TYPE = VOCA_TYPE;
//    }

    public int getVOCA_ID() {
        return ID;
    }

//    public void setVOCA_ID(int VOCA_ID) {
//        this.VOCA_ID = VOCA_ID;
//    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWORD() {
        return Utils.isEmpty(WORD) ? "" : WORD;
    }

    public void setWORD(String WORD) {
        this.WORD = WORD;
    }

    public String getWORD_DISPLAY() {
        return WORD_DISPLAY;
    }

    public void setWORD_DISPLAY(String WORD_DISPLAY) {
        this.WORD_DISPLAY = WORD_DISPLAY;
    }

    public String getMEANING_KO() {
        return MEANING_KO;
    }

    public void setMEANING_KO(String MEANING_KO) {
        this.MEANING_KO = MEANING_KO;
    }

    public String getMEANING_KO_TTS() {
        return MEANING_KO_TTS;
    }

    public void setMEANING_KO_TTS(String MEANING_KO_TTS) {
        this.MEANING_KO_TTS = MEANING_KO_TTS;
    }

    public int getWORD_SAME_ID() {
        return WORD_SAME_ID;
    }

    public void setWORD_SAME_ID(int WORD_SAME_ID) {
        this.WORD_SAME_ID = WORD_SAME_ID;
    }

    public int getVOCA_TYPE_ORI() {
        return VOCA_TYPE_ORI;
    }

    public void setVOCA_TYPE_ORI(int VOCA_TYPE_ORI) {
        this.VOCA_TYPE_ORI = VOCA_TYPE_ORI;
    }

    public int getVOCA_ID_ORI() {
        return VOCA_ID_ORI;
    }

    public void setVOCA_ID_ORI(int VOCA_ID_ORI) {
        this.VOCA_ID_ORI = VOCA_ID_ORI;
    }

    public String getWORD_TTS() {
        return WORD_TTS;
    }

    public void setWORD_TTS(String WORD_TTS) {
        this.WORD_TTS = WORD_TTS;
    }

    public String getPRONOUNCE() {
        return PRONOUNCE;
    }

    public void setPRONOUNCE(String PRONOUNCE) {
        this.PRONOUNCE = PRONOUNCE;
    }
    public String getSEARCH_HISTORY() {
        return SEARCH_HISTORY;
    }

    public void setSEARCH_HISTORY(String SEARCH_HISTORY) {
        this.SEARCH_HISTORY = SEARCH_HISTORY;
    }
    @Override
    public String getVISEARCH_HISTORY() {
        return getSEARCH_HISTORY();
    }

    @Override
    public void setVISEARCH_HISTORY(String SEARCH_HISTORY) {
        setSEARCH_HISTORY(SEARCH_HISTORY);
    }

    public String getWORDORI() {
        return WORDORI;
    }

    public void setWORDORI(String WORDORI) {
        this.WORDORI = WORDORI;
    }

    public int getWORDLEVEL() {
        return WORDLEVEL;
    }

    public void setWORDLEVEL(int WORDLEVEL) {
        this.WORDLEVEL = WORDLEVEL;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public int getCREATOR_TYPE() {
        return CREATOR_TYPE;
    }

    public void setCREATOR_TYPE(int CREATOR_TYPE) {
        this.CREATOR_TYPE = CREATOR_TYPE;
    }

    public long getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setUPDATE_DATE(long UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public long getUPDATE_DATE_BOOKMARK() {
        return UPDATE_DATE_BOOKMARK;
    }

    public void setUPDATE_DATE_BOOKMARK(long UPDATE_DATE_BOOKMARK) {
        this.UPDATE_DATE_BOOKMARK = UPDATE_DATE_BOOKMARK;
    }

    public long getUPDATE_DATE_VOCA_KNOWPRONOUNCE() {
        return UPDATE_DATE_VOCA_KNOWPRONOUNCE;
    }

    public void setUPDATE_DATE_VOCA_KNOWPRONOUNCE(long UPDATE_DATE_VOCA_KNOWPRONOUNCE) {
        this.UPDATE_DATE_VOCA_KNOWPRONOUNCE = UPDATE_DATE_VOCA_KNOWPRONOUNCE;
    }

    public long getUPDATE_DATE_VOCA_KNOW() {
        return UPDATE_DATE_VOCA_KNOW;
    }

    public void setUPDATE_DATE_VOCA_KNOW(long UPDATE_DATE_VOCA_KNOW) {
        this.UPDATE_DATE_VOCA_KNOW = UPDATE_DATE_VOCA_KNOW;
    }

    public int getBOOKMARK() {
        return BOOKMARK;
    }

    public void setBOOKMARK(int BOOKMARK) {
        this.BOOKMARK = BOOKMARK;
    }

    public boolean isBOOKMARK() {
        return getBOOKMARK() == Constant.INT_BOOLEAN.TRUE;
    }
    public void swapBOOKMARK() {
        if (isBOOKMARK()) {
            setBOOKMARK(Constant.INT_BOOLEAN.FASLE);
        } else {
            setBOOKMARK(Constant.INT_BOOLEAN.TRUE);
        }
    }
    public int getVOCA_KNOWPRONOUNCE() {
        return VOCA_KNOWPRONOUNCE;
    }

    public void setVOCA_KNOWPRONOUNCE(int VOCA_KNOWPRONOUNCE) {
        this.VOCA_KNOWPRONOUNCE = VOCA_KNOWPRONOUNCE;
    }

    public int getVOCA_KNOW() {
        return VOCA_KNOW;
    }

    public void setVOCA_KNOW(int VOCA_KNOW) {
        this.VOCA_KNOW = VOCA_KNOW;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
        return getID();
    }

    @Override
    public String getVIVoca() {
        String result = getWORD_DISPLAY();
        if (Utils.isEmpty(result)) {
            result = getWORD();
        }
        return result.trim();
    }

    @Override
    public Integer getVIVocaKnow() {
        return getVOCA_KNOW();
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return getVOCA_KNOWPRONOUNCE();
    }

    @Override
    public String getVIVocaTTS() {
        return getWORD_TTS();
    }

    @Override
    public String getVIPronounce() {
        return getPRONOUNCE();
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        String result = "";
        if (enumLanguage == null) {
            result = getMEANING_KO();
        } else {
            result = getMEANING(enumLanguage);
        }
        return result;
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        String result = "";
        if (enumLanguage == null) {
            result = getMEANING_KO_DETAILED();
        } else {
            result = getMEANING_DETAILED(enumLanguage);
        }
        return result;
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguage) {
        String result = "";
        if (enumLanguage == null) {
            result = getMEANING_KO_TTS();
        } else {
            result = getMEANING_TTS(enumLanguage);
        }
        return result;
    }

    @Override
    public String getVIMeaningEng() {
        return getMEANING_ENG();
    }

    @Override
    public String getVIMeaningEngDetailed() {
        return getMEANING_ENG_DETAILED();
    }

    @Override
    public String getVIMeaningEngTts() {
        return getMEANING_ENG_TTS();
    }

    @Override
    public Integer getVIBookmark() {
        return getBOOKMARK();
    }

    @Override
    public boolean isVIBookmark() {
        return isBOOKMARK();
    }

    @Override
    public void setVIId(Integer value) {
        setID(value);
    }

    @Override
    public void setVIVoca(String value) {
        setWORD(value);
    }

    @Override
    public void setVIVocaTTS(String value) {
        setWORD_TTS(value);
    }

    @Override
    public void setVIPronounce(String value) {
        setPRONOUNCE(value);
    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguage, String value) {
        setMEANING(enumLanguage, value);
    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguage, String value) {
        setMEANING_DETAILED(enumLanguage, value);
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
        setVOCA_KNOW(value);
    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {
        setVOCA_KNOWPRONOUNCE(value);
    }

    @Override
    public void setVIBookmark(Integer value) {
        setBOOKMARK(value);
    }

    @Override
    public void swapVIBookmark() {
        swapBOOKMARK();
    }

    @Override
    public Integer getVIVocaTypeBase() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
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
        return getVOCA_TYPE();
    }

    @Override
    public Integer getVIVocaId() {
        return getVOCA_ID();
    }

    @Override
    public void setVIVocaType(Integer value) {
//        setVOCA_TYPE(value);
    }

    @Override
    public void setVIVocaId(Integer value) {
        setID(value);
//        setVOCA_ID(value);
    }

    @Override
    public void setVIIndex(Integer value) {
        setIndex(value);
    }

    @Override
    public boolean isVIChecked() {
        return isChecked();
    }

    @Override
    public void setVIChecked(boolean checked) {
        setChecked(checked);
    }

    @Override
    public boolean isVIPlaying() {
        return isPlaying();
    }

    @Override
    public void setVIPlaying(boolean playing) {
        setPlaying(playing);
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
    public boolean hasVIVoiceFile() {
        return hasVoiceFile == Constant.INT_BOOLEAN.TRUE;
    }

    @Override
    public void setVIVoiceFile(Integer value) {
        this.hasVoiceFile = value;
    }

//    @Override
//    public void swapVIHasVoiceFile() {
//        if (hasVIVoiceFile()) {
//            setVIVoiceFile(Constant.INT_BOOLEAN.FASLE);
//        } else {
//            setVIVoiceFile(Constant.INT_BOOLEAN.TRUE);
//        }
//    }

    @Override
    public int getVIVoiceFileVersion() {
        return 0;
    }

    @Override
    public String getPersonAB() {
        return "";
    }

    @Override
    public int getVIRepeatCount() {
        return repeat;
    }
    @Override
    public void setVIRepeatCount(int value) {
        this.repeat = value;
    }

    @Override
    public int getVIDifficultWordsCount() {
        return difficultWordsCount;
    }
    @Override
    public void setVIDifficultWordsCount(int value) {
        this.difficultWordsCount = value;
    }

    @Override
    public int getVIIdInVocaBook() {
        return idInVocaBook;
    }
    @Override
    public void setVIIdInVocaBook(int value) {
        this.idInVocaBook = value;
    }
}
