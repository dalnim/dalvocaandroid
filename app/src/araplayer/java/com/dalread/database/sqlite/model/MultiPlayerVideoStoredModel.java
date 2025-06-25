package com.dalread.database.sqlite.model;

import android.os.Parcel;

public class MultiPlayerVideoStoredModel extends MultiPlayerVideoModel {
    private int STORED_ID;
    protected String LAYOUT_NAME;  // Video file path
    private int BOOKMARK;
    protected int ROTATE_LAYOUT; // 이건 개별비디오가 아닌 전체 폰의 회전 정보

    public MultiPlayerVideoStoredModel() {
        super();
    }

    public int getSTORED_ID() {
        return STORED_ID;
    }

    public void setSTORED_ID(int STORED_ID) {
        this.STORED_ID = STORED_ID;
    }

    public String getLAYOUT_NAME() {
        return LAYOUT_NAME;
    }

    public void setLAYOUT_NAME(String LAYOUT_NAME) {
        this.LAYOUT_NAME = LAYOUT_NAME;
    }

    public int getBOOKMARK() {
        return BOOKMARK;
    }

    public void setBOOKMARK(int BOOKMARK) {
        this.BOOKMARK = BOOKMARK;
    }

    public int getROTATE_LAYOUT() {
        return ROTATE_LAYOUT;
    }

    public void setROTATE_LAYOUT(int ROTATE_LAYOUT) {
        this.ROTATE_LAYOUT = ROTATE_LAYOUT;
    }

    protected MultiPlayerVideoStoredModel(Parcel in) {
        super(in);
        STORED_ID = in.readInt();
        ROTATE_LAYOUT = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(STORED_ID);
        dest.writeString(LAYOUT_NAME);
        dest.writeInt(BOOKMARK);
        dest.writeInt(ROTATE_LAYOUT);
    }

    public static final Creator<MultiPlayerVideoStoredModel> CREATOR = new Creator<MultiPlayerVideoStoredModel>() {
        @Override
        public MultiPlayerVideoStoredModel createFromParcel(Parcel in) {
            return new MultiPlayerVideoStoredModel(in);
        }

        @Override
        public MultiPlayerVideoStoredModel[] newArray(int size) {
            return new MultiPlayerVideoStoredModel[size];
        }
    };

    public void initializeFromBaseModel(MultiPlayerVideoModel baseModel, int storedId, String name, int bookmark, int rotateLayout) {
        this.setSCREEN_ID(baseModel.getSCREEN_ID());
        this.setFILE_PATH(baseModel.getFILE_PATH());
        this.setLAST_TIME(baseModel.getLAST_TIME());
        this.setAB_A(baseModel.getAB_A());
        this.setAB_B(baseModel.getAB_B());
        this.setROTATE(baseModel.getROTATE());
        this.setUSE_AB(baseModel.getUSE_AB());
        this.setVOLUME(baseModel.getVOLUME());
        this.setRESIZE_MODE(baseModel.getRESIZE_MODE());
        this.setSTORED_ID(storedId);
        this.setLAYOUT_NAME(name);
        this.setBOOKMARK(bookmark);
        this.setROTATE_LAYOUT(rotateLayout);
    }
}

