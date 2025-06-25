package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.network.ChatCompletionRequest;
import com.dalread.util.Constant;

import java.util.List;

//이건 IVocaFullPlayTTSItem에서 vocaType, vocaId, bookmark, vocaKnow, vocaKnowPronounce만 값을 가지고 있다.
public class ConversationModel implements IVocaFullPlayTTSItem {
    private int ID;
    private int ROOM_ID;
    private String REQUEST_RESONPSE_TYPE; //REQUEST, RESPONSE, GUIDE
    private String ROLE; //system, user, assistant
    private String CREATE_DATE; //
    private String MESSAGE_TYPE; //TEXT, PICTURE, 향후 추가될 가능성 대비
    private String MESSAGE_CONTENT; //실제 대화 내용
    private String MESSAGE_TRANSLATION; //번역(뜻)
    private String MESSAGE_PRONOUNCE; //발음 (병음이나 후리가나, 영단어 발음등)

    private String FILE_PATH; //음성등 파일 위치, 미래 대비용
    private int PROMPT_TOKENS;
    private int COMPLETION_TOKENS;
    private int TOTAL_TOKENS;
    private String RUBY; //영어 단어 루비 만들때. 미래 대비용

    //DB에는 없고, 내부적으로 쓰는 것, 가변적으로 바뀜.
    private int adapterPosition;
    private String difficultWordMeaning;
    private boolean playingTts;
    private List<IVocaFullPlayTTSItem> allWordList;
    private int vocaType = -1;
    private int vocaId = -1;
    private int bookmark;
    private int vocaKnow;
    private int vocaKnowPronounce;


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getROOM_ID() {
        return ROOM_ID;
    }

    public void setROOM_ID(int ROOM_ID) {
        this.ROOM_ID = ROOM_ID;
    }

    public String getREQUEST_RESONPSE_TYPE() {
        return REQUEST_RESONPSE_TYPE;
    }

    public void setREQUEST_RESONPSE_TYPE(String REQUEST_RESONPSE_TYPE) {
        this.REQUEST_RESONPSE_TYPE = REQUEST_RESONPSE_TYPE;
    }
    public boolean isGuide() {
        return REQUEST_RESONPSE_TYPE.equals(ChatCompletionRequest.RequestResponseType.GUIDE.name());
    }
    public boolean isRequest() {
        return REQUEST_RESONPSE_TYPE.equals(ChatCompletionRequest.RequestResponseType.REQUEST.name());
    }
    public boolean isResponse() {
        return REQUEST_RESONPSE_TYPE.equals(ChatCompletionRequest.RequestResponseType.RESPONSE.name());
    }
    public String getROLE() {
        return ROLE;
    }

    public void setROLE(String ROLE) {
        this.ROLE = ROLE;
    }

    public String getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATE_DATE(String CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public String getMESSAGE_TYPE() {
        return MESSAGE_TYPE;
    }

    public void setMESSAGE_TYPE(String MESSAGE_TYPE) {
        this.MESSAGE_TYPE = MESSAGE_TYPE;
    }

    public String getMESSAGE_CONTENT() {
        return MESSAGE_CONTENT;
    }

    public void setMESSAGE_CONTENT(String MESSAGE_CONTENT) {
        this.MESSAGE_CONTENT = MESSAGE_CONTENT.trim();
    }

    public String getMESSAGE_PRONOUNCE() {
        return MESSAGE_PRONOUNCE;
    }

    public void setMESSAGE_PRONOUNCE(String MESSAGE_PRONOUNCE) {
        this.MESSAGE_PRONOUNCE = MESSAGE_PRONOUNCE;
    }

    public String getMessageContentAndTranslation() {
        StringBuilder result = new StringBuilder();

        if (!MESSAGE_CONTENT.isEmpty()) {
            result.append(MESSAGE_CONTENT);
        }

        if (!MESSAGE_CONTENT.isEmpty() && !MESSAGE_TRANSLATION.isEmpty()) {
            result.append("\n");
        }

        if (!MESSAGE_TRANSLATION.isEmpty()) {
            result.append(MESSAGE_TRANSLATION);
        }

        return result.toString();
    }



    public String getFILE_PATH() {
        return FILE_PATH;
    }

    public void setFILE_PATH(String FILE_PATH) {
        this.FILE_PATH = FILE_PATH;
    }

    public int getPROMPT_TOKENS() {
        return PROMPT_TOKENS;
    }

    public void setPROMPT_TOKENS(int PROMPT_TOKENS) {
        this.PROMPT_TOKENS = PROMPT_TOKENS;
    }

    public int getCOMPLETION_TOKENS() {
        return COMPLETION_TOKENS;
    }

    public void setCOMPLETION_TOKENS(int COMPLETION_TOKENS) {
        this.COMPLETION_TOKENS = COMPLETION_TOKENS;
    }

    public int getTOTAL_TOKENS() {
        return TOTAL_TOKENS;
    }

    public void setTOTAL_TOKENS(int TOTAL_TOKENS) {
        this.TOTAL_TOKENS = TOTAL_TOKENS;
    }

    public String getRUBY() {
        return RUBY;
    }

    public void setRUBY(String RUBY) {
        this.RUBY = RUBY;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public String getDifficultWordMeaning() {
        return difficultWordMeaning;
    }

    public void setDifficultWordMeaning(String difficultWordMeaning) {
        this.difficultWordMeaning = difficultWordMeaning;
    }

    public boolean isPlayingTts() {
        return playingTts;
    }

    public void setPlayingTts(boolean playingTts) {
        this.playingTts = playingTts;
    }

    public List<IVocaFullPlayTTSItem> getAllWordList() {
        return allWordList;
    }

    public void setAllWordList(List<IVocaFullPlayTTSItem> allWordList) {
        this.allWordList = allWordList;
    }

    public String getMESSAGE_TRANSLATION() {
        return MESSAGE_TRANSLATION;
    }

    public void setMESSAGE_TRANSLATION(String MESSAGE_TRANSLATION) {
        this.MESSAGE_TRANSLATION = MESSAGE_TRANSLATION.trim();
    }

    @Override
    public Integer getVIId() {
        return null;
    }

    @Override
    public String getVIVoca() {
        return null;
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
        return null;
    }

    @Override
    public String getVIPronounce() {
        return null;
    }

    @Override
    public String getVIMeaning(EnumLanguage enumLanguage) {
        return null;
    }

    @Override
    public String getVIMeaningDetailed(EnumLanguage enumLanguage) {
        return null;
    }

    @Override
    public String getVIMeaningTts(EnumLanguage enumLanguage) {
        return null;
    }

    @Override
    public String getVIMeaningEng() {
        return null;
    }

    @Override
    public String getVIMeaningEngDetailed() {
        return null;
    }

    @Override
    public String getVIMeaningEngTts() {
        return null;
    }

    @Override
    public Integer getVIBookmark() {
        return bookmark;
    }

    @Override
    public boolean isVIBookmark() {
        return getVIBookmark() > 0;
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
        this.vocaKnow = value;
    }

    @Override
    public void setVIVocaKnowPronounce(Integer value) {
        this.vocaKnowPronounce = value;
    }

    @Override
    public void setVIBookmark(Integer value) {
        bookmark = value;
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
    public boolean hasVIVoiceFile() {
        return false;
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
        return null;
    }

    @Override
    public void setVIIndex(Integer index) {

    }

    @Override
    public String getVIPosAll() {
        return null;
    }

    @Override
    public boolean isVIChecked() {
        return false;
    }

    @Override
    public void setVIChecked(boolean checked) {

    }

    @Override
    public boolean isVIPlaying() {
        return false;
    }

    @Override
    public void setVIPlaying(boolean playing) {

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
        return null;
    }
}
