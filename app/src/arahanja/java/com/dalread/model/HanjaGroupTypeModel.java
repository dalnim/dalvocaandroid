package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HanjaGroupTypeModel implements Parcelable {
    private final DIC_HANJA hanja;
    private HanjaGroupType hanjaGroupType;
    private boolean isFindAllHanjasThatContainThisHanja;
    public HanjaGroupTypeModel(DIC_HANJA hanja, HanjaGroupType hanjaGroupType) {
        this.hanja = hanja;
        this.hanjaGroupType = hanjaGroupType;
    }

    protected HanjaGroupTypeModel(Parcel in) {
        hanja = (DIC_HANJA) in.readSerializable();
        hanjaGroupType = (HanjaGroupType) in.readSerializable();
        isFindAllHanjasThatContainThisHanja = in.readByte() != 0;
    }

    public static final Creator<HanjaGroupTypeModel> CREATOR = new Creator<HanjaGroupTypeModel>() {
        @Override
        public HanjaGroupTypeModel createFromParcel(Parcel in) {
            return new HanjaGroupTypeModel(in);
        }

        @Override
        public HanjaGroupTypeModel[] newArray(int size) {
            return new HanjaGroupTypeModel[size];
        }
    };

    public DIC_HANJA getHanja() {
        return hanja;
    }

    public HanjaGroupType getHanjaGroupType() {
        return hanjaGroupType;
    }

    public void setHanjaGroupType(HanjaGroupType hanjaGroupType) {
        this.hanjaGroupType = hanjaGroupType;
    }

    public void setIsFindAllHanjasThatContainThisHanja(boolean value) {
        this.isFindAllHanjasThatContainThisHanja = value;
    }

    public boolean getIsFindAllHanjasThatContainThisHanja() {
        return isFindAllHanjasThatContainThisHanja;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(hanja);
        parcel.writeSerializable(hanjaGroupType);
        parcel.writeByte((byte) (isFindAllHanjasThatContainThisHanja ? 1 : 0));
    }
}
