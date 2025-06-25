package com.dalread.model;

import com.dalread.interfaces.IVocaFullPlayTTSItem;

import java.util.List;

public class GPT_CHAT_MESSAGE {
    private int ID;
    private int ROOM_ID;
    private String REQUEST_RESONPSE_TYPE; //REQUEST, RESPONSE, APP
    private String ROLE; //system, user, assistant
    private String CREATE_DATE; //
    private String MESSAGE_TYPE; //TEXT, PICTURE, 향후 추가될 가능성 대비
    private String MESSAGE_CONTENT; //실제 대화 내용
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
        this.MESSAGE_CONTENT = MESSAGE_CONTENT;
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
}
