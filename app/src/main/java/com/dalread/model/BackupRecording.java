package com.dalread.model;

public class BackupRecording {

    // Firestore properties
    private String DATE_STRING;
    private String FILE_NAME;
    private String ID;
    private String VERSION;
    private String VOCA_FILENAME;
    // Local properties
    private boolean playing;
    private boolean checked;
    private int index;

    public String getDATE_STRING() {
        return DATE_STRING;
    }

    public void setDATE_STRING(String DATE_STRING) {
        this.DATE_STRING = DATE_STRING;
    }

    public String getFILE_NAME() {
        return FILE_NAME;
    }

    public void setFILE_NAME(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getVERSION() {
        return VERSION;
    }

    public void setVERSION(String VERSION) {
        this.VERSION = VERSION;
    }

    public String getVOCA_FILENAME() {
        return VOCA_FILENAME;
    }

    public void setVOCA_FILENAME(String VOCA_FILENAME) {
        this.VOCA_FILENAME = VOCA_FILENAME;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
