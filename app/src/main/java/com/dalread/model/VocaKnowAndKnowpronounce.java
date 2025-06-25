package com.dalread.model;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaBasicItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaKnowAndKnowpronounce extends VocaTypeId implements Serializable, IVocaBasicItem {

    @SerializedName("VOCA_KNOW")
    private int vocaKnow;
    @SerializedName("VOCA_KNOWPRONOUNCE")
    private int vocaKnowPronounce;

    public VocaKnowAndKnowpronounce(int vocaId, int vocaType, int vocaKnow, int vocaKnowPronounce) {
        super(vocaId, vocaType);
        this.vocaKnow = vocaKnow;
        this.vocaKnowPronounce = vocaKnowPronounce;
    }

    public VocaKnowAndKnowpronounce(IVocaBasicItem item) {
        super(item);
        this.vocaKnow = item.getVIVocaKnow();
        this.vocaKnowPronounce = item.getVIVocaKnowPronounce();
    }

    public int getVocaKnow() {
        return vocaKnow;
    }

    public void setVocaKnow(int vocaKnow) {
        this.vocaKnow = vocaKnow;
    }

    public int getVocaKnowPronounce() {
        return vocaKnowPronounce;
    }

    public void setVocaKnowPronounce(int vocaKnowPronounce) {
        this.vocaKnowPronounce = vocaKnowPronounce;
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
        return null;
    }

    @Override
    public boolean isVIBookmark() {
        return false;
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

    }

    @Override
    public void swapVIBookmark() {

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
}
