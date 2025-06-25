package com.dalread.model;

import com.google.gson.annotations.SerializedName;

public class NativeSpeaker extends User {

    @SerializedName("NATIVE_SPEAKER_ID")
    private int nativeSpeakerId;
    @SerializedName("IS_PREFFERRED")
    private int isPreferred;
    @SerializedName("RANK")
    private int rank;
    @SerializedName("COUNT_RECORD")
    private int countRecord;
    @SerializedName("HAS_VOICE_FILE")
    private int hasVoiceFile;

    public int getNativeSpeakerId() {
        return nativeSpeakerId;
    }

    public void setNativeSpeakerId(int nativeSpeakerId) {
        this.nativeSpeakerId = nativeSpeakerId;
    }

    public boolean isPreferred() {
        return isPreferred == 1;
    }

    public void setPreferred(boolean preferred) {
        isPreferred = preferred ? 1 : 0;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCountRecord() {
        return countRecord;
    }

    public void setCountRecord(int countRecord) {
        this.countRecord = countRecord;
    }

    public int getHasVoiceFile() {
        return hasVoiceFile;
    }

    public void setHasVoiceFile(int hasVoiceFile) {
        this.hasVoiceFile = hasVoiceFile;
    }
}
