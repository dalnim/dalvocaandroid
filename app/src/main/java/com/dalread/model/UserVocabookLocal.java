package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;

public class UserVocabookLocal implements IVocaFullPlayTTSItem {
    public int ID;
    public int VOCABOOKS_ID;
    public int VOCA_ID; //여기서는 VOCA_ID는 ID와 동일하다.
    public String VOCA_DISPLAY;
    public String VOCA_TTS;
    public String PRONOUNCE;
    public int DISP_ORDER;
    public int VOCA_TYPE;
    public String PERSON_AB;
    public int BOOKMARK;
    public int VOCA_KNOWPRONOUNCE;
    public int VOCA_KNOW;
    public int HAS_VOICE_FILE;
    public int PRACTICE_COUNT;
    public int CORRECT_COUNT;
    public int WRONG_COUNT;
    public int WANT_PRACTICE_PRONOUNE;
    public String MEANING;
    public String MEANING_DETAILED;
    public String MEANING_TTS;

    //아래는 Table에는 없어도 된다.
    private boolean checked;  //PlaylistItem에서 플레이할때 선택된것만 플레이 할려고
    private boolean playing; //this voca is play by TTS or voice files

    public int getVOCABOOKS_ID() {
        return VOCABOOKS_ID;
    }

    public void setVOCABOOKS_ID(int VOCABOOKS_ID) {
        this.VOCABOOKS_ID = VOCABOOKS_ID;
    }

    public int getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(int DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }

    public int getPRACTICE_COUNT() {
        return PRACTICE_COUNT;
    }

    public void setPRACTICE_COUNT(int PRACTICE_COUNT) {
        this.PRACTICE_COUNT = PRACTICE_COUNT;
    }

    public int getCORRECT_COUNT() {
        return CORRECT_COUNT;
    }

    public void setCORRECT_COUNT(int CORRECT_COUNT) {
        this.CORRECT_COUNT = CORRECT_COUNT;
    }

    public int getWRONG_COUNT() {
        return WRONG_COUNT;
    }

    public void setWRONG_COUNT(int WRONG_COUNT) {
        this.WRONG_COUNT = WRONG_COUNT;
    }

    public int getWANT_PRACTICE_PRONOUNE() {
        return WANT_PRACTICE_PRONOUNE;
    }

    public void setWANT_PRACTICE_PRONOUNE(int WANT_PRACTICE_PRONOUNE) {
        this.WANT_PRACTICE_PRONOUNE = WANT_PRACTICE_PRONOUNE;
    }

    @Override
    public Integer getVIId() {
        return ID;
    }

    @Override
    public String getVIVoca() {
        return VOCA_DISPLAY == null ? "" : VOCA_DISPLAY;
    }

    @Override
    public Integer getVIVocaKnow() {
        return VOCA_KNOW;
    }

    @Override
    public Integer getVIVocaKnowPronounce() {
        return VOCA_KNOWPRONOUNCE;
    }

    @Override
    public String getVIVocaTTS() {
        return VOCA_TTS == null ? "" : VOCA_TTS;
    }

    @Override
    public String getVIPronounce() {
        return PRONOUNCE == null ? "" : PRONOUNCE;
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        return MEANING == null ? "" : MEANING;
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return MEANING_DETAILED == null ? "" : MEANING_DETAILED;
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguage) {
        return MEANING_TTS == null ? "" : MEANING_TTS;
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
        return BOOKMARK;
    }

    @Override
    public boolean isVIBookmark() {
        return BOOKMARK == Constant.INT_BOOLEAN.TRUE;
    }

    @Override
    public void setVIId(Integer value) {
        this.ID = value;
    }

    @Override
    public void setVIVoca(String value) {
        this.VOCA_DISPLAY = value;
    }

    @Override
    public void setVIVocaTTS(String value) {
        this.VOCA_TTS = value;
    }

    @Override
    public void setVIPronounce(String value) {
        this.PRONOUNCE = value;
    }

    @Override
    public void setVIMeaning(EnumLanguage enumLanguage, String value) {
        this.MEANING = value;
    }

    @Override
    public void setVIMeaningDetailed(EnumLanguage enumLanguage, String value) {
        this.MEANING_DETAILED = value;
    }

    @Override
    public void setVIMeaningTts(EnumLanguage enumLanguage, String value) {
        this.MEANING_TTS = value;
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
        this.VOCA_KNOW = value;
    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {
        this.VOCA_KNOWPRONOUNCE = value;
    }

    @Override
    public void setVIBookmark(Integer value) {
        this.BOOKMARK = value;
    }

    @Override
    public void swapVIBookmark() {
        if (isVIBookmark()) {
            setVIBookmark(Constant.INT_BOOLEAN.FASLE);
        } else {
            setVIBookmark(Constant.INT_BOOLEAN.TRUE);
        }
    }

    @Override
    public Integer getVIVocaTypeBase() {
        return 0;
    }

    @Override
    public Integer getVIVocaIdBase() {
        return 0;
    }

    @Override
    public void setVIVocaTypeBase(Integer value) {

    }

    @Override
    public void setVIVocaIdBase(Integer value) {

    }

    @Override
    public boolean hasVIVoiceFile() {
        return false;
    }

    @Override
    public Integer getVIVocaType() {
        return Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE;
    }

    @Override
    public Integer getVIVocaId() {
        return ID;
    }

    @Override
    public void setVIVocaType(Integer value) {

    }

    @Override
    public void setVIVocaId(Integer value) {
        this.ID = value;
    }

    @Override
    public Integer getVIIndex() {
        return 0;
    }

    @Override
    public void setVIIndex(Integer index) {

    }

    @Override
    public String getVIPosAll() {
        return null;
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
        return null;
    }

    @Override
    public void setVIPath(String path) {

    }

    @Override
    public int getVIVoiceFileVersion() {
        return 0;
    }

    @Override
    public String getPersonAB() {
        return PERSON_AB;
    }

    @Override
    public void setPersonAB(String value) {
        this.PERSON_AB = value;
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
}
