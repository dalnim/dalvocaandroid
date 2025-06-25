package com.dalread.database.sqlite.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MultiPlayerVideoAbRepeatModel implements Cloneable, Parcelable {
    protected int ID; // id in the multiple screens.
    protected String FILE_PATH;  // Video file path
    protected long AB_A; // AB repeat's A time
    protected long AB_B; // AB repeat's B time

    public MultiPlayerVideoAbRepeatModel() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    @Override
    public MultiPlayerVideoAbRepeatModel clone() {
        try {
            return (MultiPlayerVideoAbRepeatModel) super.clone();
        } catch (CloneNotSupportedException e) {
            // CloneNotSupportedException 처리
            e.printStackTrace();
            return null;
        }
    }
    protected MultiPlayerVideoAbRepeatModel(Parcel in) {
        ID = in.readInt();
        FILE_PATH = in.readString();
        AB_A = in.readLong();
        AB_B = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(FILE_PATH);
        dest.writeLong(AB_A);
        dest.writeLong(AB_B);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MultiPlayerVideoAbRepeatModel> CREATOR = new Creator<MultiPlayerVideoAbRepeatModel>() {
        @Override
        public MultiPlayerVideoAbRepeatModel createFromParcel(Parcel in) {
            return new MultiPlayerVideoAbRepeatModel(in);
        }

        @Override
        public MultiPlayerVideoAbRepeatModel[] newArray(int size) {
            return new MultiPlayerVideoAbRepeatModel[size];
        }
    };
}
