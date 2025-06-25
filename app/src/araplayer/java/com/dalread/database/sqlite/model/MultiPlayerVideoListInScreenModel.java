package com.dalread.database.sqlite.model;

public class MultiPlayerVideoListInScreenModel {
    protected int ID;        // 자동 증가하는 ID
    protected int SCREEN_ID;        // 스크린 ID
    protected String FILE_PATH;     // 비디오 파일 경로

    // 기본 생성자
    public MultiPlayerVideoListInScreenModel() {
        this.FILE_PATH = "";
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSCREEN_ID() {
        return SCREEN_ID;
    }

    public void setSCREEN_ID(int SCREEN_ID) {
        this.SCREEN_ID = SCREEN_ID;
    }


    public String getFILE_PATH() {
        return FILE_PATH == null ? "" : FILE_PATH; // null인 경우 빈 문자열 리턴
    }

    public void setFILE_PATH(String filePath) {
        this.FILE_PATH = filePath != null ? filePath : ""; // null인 경우 빈 문자열 설정
    }
}
