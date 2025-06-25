package com.dalread.database.sqlite.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

public class MultiPlayerVideoModel implements Cloneable, Parcelable {
    protected int SCREEN_ID; // id in the multiple screens.
    protected String FILE_PATH;  // Video file path
    protected long LAST_TIME; // Last watched time for the video(File Path)
    protected long AB_A; // AB repeat's A time
    protected long AB_B; // AB repeat's B time
    protected int ROTATE; // 개별 비디오 스크린의 회전정보
    protected int USE_AB; // Use AB or not
    protected int VOLUME = -1; // 0~100, -1 Not use
    protected int RESIZE_MODE; // 화면을 Fit,

    public MultiPlayerVideoModel() {
        this.FILE_PATH = "";
        this.RESIZE_MODE = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
    }

    public int getSCREEN_ID() {
        return SCREEN_ID;
    }

    public void setSCREEN_ID(int SCREEN_ID) {
        this.SCREEN_ID = SCREEN_ID;
    }

    public String getFILE_PATH() {
        return FILE_PATH == null ? "" : FILE_PATH;
    }
    public boolean isFilePathEmpty() {
        return FILE_PATH == null || FILE_PATH.isEmpty();
    }
    public void setFILE_PATH(String FILE_PATH) {
        this.FILE_PATH = FILE_PATH;
    }

    public long getLAST_TIME() {
        return LAST_TIME;
    }

    public void setLAST_TIME(long LAST_TIME) {
        this.LAST_TIME = LAST_TIME;
    }

    public long getAB_A() {
        return AB_A;
    }

    public void setAB_A(long AB_A) {
        this.AB_A = AB_A;
    }

    public long getAB_B() {
        return AB_B;
    }

    public void setAB_B(long AB_B) {
        this.AB_B = AB_B;
    }

    public int getROTATE() {
        return ROTATE;
    }

    public void setROTATE(int ROTATE) {
        this.ROTATE = ROTATE;
    }

    public int getUSE_AB() {
        return USE_AB;
    }

    public void setUSE_AB(int USE_AB) {
        this.USE_AB = USE_AB;
    }

    public int getVOLUME() {
        return VOLUME;
    }

    public void setVOLUME(int VOLUME) {
        this.VOLUME = VOLUME;
    }
    public void setVOLUMEMuted() {
        this.VOLUME = -1;
    }
    public boolean isMuted() { //실제 비디오의 소리가 0인거와 model의 mute와는 다르다. Mute이면 비디오의 소리가 조절이 안됨. Mute가 아니면 비디오의 소리가 0이라도 크게 조절 가능함.
        return VOLUME < 0;
    }
    public int getRESIZE_MODE() {
        return RESIZE_MODE;
    }

    public void setRESIZE_MODE(int RESIZE_MODE) {
        this.RESIZE_MODE = RESIZE_MODE;
    }

    @Override
    public MultiPlayerVideoModel clone() {
        try {
            return (MultiPlayerVideoModel) super.clone();
        } catch (CloneNotSupportedException e) {
            // CloneNotSupportedException 처리
            e.printStackTrace();
            return null;
        }
    }
    protected MultiPlayerVideoModel(Parcel in) {
        SCREEN_ID = in.readInt();
        FILE_PATH = in.readString();
        LAST_TIME = in.readLong();
        AB_A = in.readLong();
        AB_B = in.readLong();
        ROTATE = in.readInt();
        USE_AB = in.readInt();
        VOLUME = in.readInt();
        RESIZE_MODE = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(SCREEN_ID);
        dest.writeString(FILE_PATH);
        dest.writeLong(LAST_TIME);
        dest.writeLong(AB_A);
        dest.writeLong(AB_B);
        dest.writeInt(ROTATE);
        dest.writeInt(USE_AB);
        dest.writeInt(VOLUME);
        dest.writeInt(RESIZE_MODE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MultiPlayerVideoModel> CREATOR = new Creator<MultiPlayerVideoModel>() {
        @Override
        public MultiPlayerVideoModel createFromParcel(Parcel in) {
            return new MultiPlayerVideoModel(in);
        }

        @Override
        public MultiPlayerVideoModel[] newArray(int size) {
            return new MultiPlayerVideoModel[size];
        }
    };

    public void clearModelExceptId() {
        this.FILE_PATH = "";
        this.LAST_TIME = 0;
        this.AB_A = 0;
        this.AB_B = 0;
        this.ROTATE = 0;
        this.USE_AB = 0;
        this.VOLUME = -1;
        this.RESIZE_MODE = 0;
    }
    public void initializeFromStoredModel(MultiPlayerVideoStoredModel storedModel) {
        if (storedModel != null) {
            this.SCREEN_ID = storedModel.getSCREEN_ID();
            this.FILE_PATH = storedModel.getFILE_PATH();
            this.LAST_TIME = storedModel.getLAST_TIME();
            this.AB_A = storedModel.getAB_A();
            this.AB_B = storedModel.getAB_B();
            this.ROTATE = storedModel.getROTATE();
            this.USE_AB = storedModel.getUSE_AB();
            this.VOLUME = storedModel.getVOLUME();
            this.RESIZE_MODE = storedModel.getRESIZE_MODE();
        }
    }
}
