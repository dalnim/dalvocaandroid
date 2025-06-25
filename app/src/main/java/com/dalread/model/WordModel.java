package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

import io.realm.annotations.Ignore;

/**
 * Created by JetVHS on 3/16/17.
 */

/**
 * Example:
 * "prosecution":{"ALL_POS_CURRENT_WORD":"_____",
 * "ALL_POS_CURRENT_WORD_BASEFORM":"","BOOKMARK":"false","KNOW":"1","KNOWPRONOUNCE":"1",
 * "MEANING":"enjuiciar","PRONOUNCE":"prὰsikjúːʃən","WORD":"prosecution","WORDLEVEL":"999",
 * "WORDLIST_KEY":"prosecution","WORDORI":"prosecute"}
 */

public class WordModel implements Parcelable {
    @SerializedName("ALL_POS_CURRENT_WORD")
    private String allPosCurrentWord;
    @SerializedName("ALL_POS_CURRENT_WORD_BASEFORM")
    private String allPosCurrentWordBaseFrom;
    @SerializedName("BOOKMARK")
    private boolean bookmark;
    @SerializedName("KNOW")
    private String know;
    @SerializedName("KNOWPRONOUNCE")
    private String knowPronounce;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("MEANING_DETAILED")
    private String meaningDetailed;
    @SerializedName("PRONOUNCE")
    private String pronounce;
    @SerializedName("WORD")
    private String word;
    @SerializedName("WORDLEVEL")
    private String wordLevel;
    @SerializedName("WORDLIST_KEY")
    private String wordListKey;
    @SerializedName("WORDORI")
    private String wordDori;
    @SerializedName("VOCA_ID")
    private String vocaId;
    @SerializedName("VOCA")
    private String voca;
    @SerializedName("VOCA_TYPE")
    private int vocaType;

    @Ignore
    private boolean isSelect = false;
    @Ignore
    private boolean isShow = true;
    @Ignore
    /*
        0: OFF
        1: Single
        2: All
     */
    private int speak = Constant.WORD_SPEAK.SPEAK_OFF;

    public WordModel() {
        this(Constant.BASE_BLANK, Constant.BASE_BLANK, false, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, 0);
    }

    public WordModel(String allPosCurrentWord, String allPosCurrentWordBaseFrom, boolean bookmark, String know, String knowPronounce, String meaning, String meaningDetailed, String pronounce, String word, String wordLevel, String wordListKey, String wordDori, String vocaId, String voca, int vocaType) {
        this.allPosCurrentWord = allPosCurrentWord;
        this.allPosCurrentWordBaseFrom = allPosCurrentWordBaseFrom;
        this.bookmark = bookmark;
        this.know = know;
        this.knowPronounce = knowPronounce;
        this.meaning = meaning;
        this.meaningDetailed = meaningDetailed;
        this.pronounce = pronounce;
        this.word = word;
        this.wordLevel = wordLevel;
        this.wordListKey = wordListKey;
        this.wordDori = wordDori;
        this.vocaId = vocaId;
        this.voca = voca;
        this.vocaType = vocaType;
    }

    public WordModel(WordModel wordModel) {
        this.allPosCurrentWord = wordModel.getAllPosCurrentWord();
        this.allPosCurrentWordBaseFrom = wordModel.getAllPosCurrentWordBaseFrom();
        this.bookmark = wordModel.isBookmark();
        this.know = wordModel.getKnow();
        this.knowPronounce = wordModel.getKnowPronounce();
        this.meaning = wordModel.getMeaning();
        this.meaningDetailed = wordModel.getMeaningDetailed();
        this.pronounce = wordModel.getPronounce();
        this.word = wordModel.getWord();
        this.wordLevel = wordModel.getWordLevel();
        this.wordListKey = wordModel.getWordListKey();
        this.wordDori = wordModel.getWordDori();
        this.vocaId = wordModel.getVocaId();
        this.voca = wordModel.getVoca();
        this.vocaType = wordModel.getVocaType();
        this.isSelect = wordModel.isSelect();
        this.isShow = wordModel.isShow();
        this.speak = wordModel.getSpeak();
    }

    public String getAllPosCurrentWord() {
        return allPosCurrentWord;
    }

    public void setAllPosCurrentWord(String allPosCurrentWord) {
        this.allPosCurrentWord = allPosCurrentWord;
    }

    public String getAllPosCurrentWordBaseFrom() {
        return allPosCurrentWordBaseFrom;
    }

    public void setAllPosCurrentWordBaseFrom(String allPosCurrentWordBaseFrom) {
        this.allPosCurrentWordBaseFrom = allPosCurrentWordBaseFrom;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    public void setBookmark(int value) {
        this.bookmark = value == 1;
    }

    public String getKnow() {
        return know;
    }

    public int getIntKnow() {
        return Utils.parseInt(know);
    }

    public void setKnow(String know) {
        this.know = know;
    }

    public String getKnowPronounce() {
        return knowPronounce;
    }

    public void setKnowPronounce(String knowPronounce) {
        this.knowPronounce = knowPronounce;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getMeaningDetailed() {
        return meaningDetailed;
    }

    public void setMeaningDetailed(String meaningDetailed) {
        this.meaningDetailed = meaningDetailed;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordLevel() {
        return wordLevel;
    }

    public void setWordLevel(String wordLevel) {
        this.wordLevel = wordLevel;
    }

    public String getWordListKey() {
        return wordListKey;
    }

    public void setWordListKey(String wordListKey) {
        this.wordListKey = wordListKey;
    }

    public String getWordDori() {
        return wordDori;
    }

    public void setWordDori(String wordDori) {
        this.wordDori = wordDori;
    }

    public String getVocaId() {
        return vocaId;
    }

    public void setVocaId(String vocaId) {
        this.vocaId = vocaId;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getSpeak() {
        return speak;
    }

    public void setSpeak(int speak) {
        this.speak = speak;
    }

    @Override
    public String toString() {
        return "WordModel{" +
                "allPosCurrentWord='" + allPosCurrentWord + '\'' +
                ", allPosCurrentWordBaseFrom='" + allPosCurrentWordBaseFrom + '\'' +
                ", bookmark=" + bookmark +
                ", know='" + know + '\'' +
                ", knowPronounce='" + knowPronounce + '\'' +
                ", meaning='" + meaning + '\'' +
                ", meaningDetailed='" + meaningDetailed + '\'' +
                ", pronounce='" + pronounce + '\'' +
                ", word='" + word + '\'' +
                ", wordLevel='" + wordLevel + '\'' +
                ", wordListKey='" + wordListKey + '\'' +
                ", wordDori='" + wordDori + '\'' +
                ", vocaId='" + vocaId + '\'' +
                ", voca='" + voca + '\'' +
                ", vocaType='" + vocaType + '\'' +
                ", isSelect=" + isSelect +
                ", isShow=" + isShow +
                ", speak=" + speak +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(allPosCurrentWord);
        dest.writeString(allPosCurrentWordBaseFrom);
        dest.writeByte((byte) (bookmark ? 1 : 0));
        dest.writeString(know);
        dest.writeString(knowPronounce);
        dest.writeString(meaning);
        dest.writeString(meaningDetailed);
        dest.writeString(pronounce);
        dest.writeString(word);
        dest.writeString(wordLevel);
        dest.writeString(wordListKey);
        dest.writeString(wordDori);
        dest.writeString(vocaId);
        dest.writeString(voca);
        dest.writeInt(vocaType);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeByte((byte) (isShow ? 1 : 0));
        dest.writeInt(speak);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<WordModel> CREATOR = new Parcelable.Creator<WordModel>() {
        public WordModel createFromParcel(Parcel in) {
            return new WordModel(in);
        }

        public WordModel[] newArray(int size) {
            return new WordModel[size];
        }
    };

    private WordModel(Parcel in) {
        this.allPosCurrentWord = in.readString();
        this.allPosCurrentWordBaseFrom = in.readString();
        this.bookmark = in.readByte() != 0;
        this.know = in.readString();
        this.knowPronounce = in.readString();
        this.meaning = in.readString();
        this.meaningDetailed = in.readString();
        this.pronounce = in.readString();
        this.word = in.readString();
        this.wordLevel = in.readString();
        this.wordListKey = in.readString();
        this.wordDori = in.readString();
        this.vocaId = in.readString();
        this.voca = in.readString();
        this.vocaType = in.readInt();
        this.isSelect = in.readByte() != 0;
        this.isShow = in.readByte() != 0;
        this.speak = in.readInt();
    }
}
