package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VocaHanja implements Serializable, AmkiItem, HanjaQuizItem {

    // from server
    @SerializedName("COMPONENTS")
    private String components;
    @SerializedName("CONFUSED_VOCA_LIST")
    private List<VocaConfused> confusedVocaList;
    @SerializedName("EXAMPLE_SENTENCES")
    private List<VocaHanjaExample> exampleSentences;
    @SerializedName("HANJA_COMPONENT")
    private String hanjaComponent;
    @SerializedName("HANJA_COMPONENT_MAP")
    private Map<String, VocaHanja> hanjaComponentMap;
    @SerializedName("HANJA_LEVEL")
    private VocaHanjaLevel hanjaLevel;
    @SerializedName("HANJA_VARIANTS")
    private VocaHanjaVariant hanjaVariants;
    @SerializedName("HANJA_TYPE")
    private String hanjaType;
    @SerializedName("INDEX")
    private int index;
    @SerializedName("INDEX_HANJA")
    private int indexHanja;
    @SerializedName("KUNYOMI")
    private String kunYomi;
    @SerializedName("MEANING")
    private String meaning;
    @SerializedName("MEANING_DETAILED")
    private String meaningDetailed;
    @SerializedName("MEANING_ENG")
    private String meaningEng;
    @SerializedName("MEANING_ENG_DETAILED")
    private String meaningEngDetailed;
    @SerializedName("MEANING_WITH_PRONOUNCE_FOR_HANJA")
    private String meaningWithPronounceForHanja;
    @SerializedName("MEANING_WITH_PRONOUNCE_FOR_RADICAL")
    private String meaningWithPronounceForRadical;
    @SerializedName("ONYOMI")
    private String onYomi;
    @SerializedName("PINYIN")
    private String pinyin;
    @SerializedName("PRONOUNCE")
    private String pronounce;
    @SerializedName("RADICAL")
    private String radical;
    @SerializedName("STROKES")
    private int strokes;
    @SerializedName("VOCA")
    private String voca;
    @SerializedName("VOCAORI")
    private String vocaOri;
    @SerializedName("VOCAORI_ID")
    private int vocaOriId;
    @SerializedName("VOCA_ID")
    private int vocaId;
    @SerializedName("VOCA_KNOW")
    private int vocaKnow;
    @SerializedName("VOCA_KNOWPRONOUNCE")
    private int vocaKnowPronounce;
    @SerializedName("VOCA_MEANING_PRONOUNCE_FOR_HANJA")
    private String vocaMeaningPronounceForHanja;
    @SerializedName("VOCA_TYPE")
    private int vocaType;

    // client only
    private String parentVoca;

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    public List<VocaConfused> getConfusedVocaList() {
        if (confusedVocaList == null) {
            confusedVocaList = new ArrayList<>();
        }
        return confusedVocaList;
    }

    public void setConfusedVocaList(List<VocaConfused> confusedVocaList) {
        this.confusedVocaList = confusedVocaList;
    }

    public List<VocaHanjaExample> getExampleSentences() {
        if (exampleSentences == null) {
            exampleSentences = new ArrayList<>();
        }
        return exampleSentences;
    }

    public void setExampleSentences(List<VocaHanjaExample> exampleSentences) {
        this.exampleSentences = exampleSentences;
    }

    public String getHanjaComponent() {
        return hanjaComponent;
    }

    public void setHanjaComponent(String hanjaComponent) {
        this.hanjaComponent = hanjaComponent;
    }

    public Map<String, VocaHanja> getHanjaComponentMap() {
        if (hanjaComponentMap == null) {
            hanjaComponentMap = new HashMap<>();
        }
        return hanjaComponentMap;
    }

    public void setHanjaComponentMap(Map<String, VocaHanja> hanjaComponentMap) {
        this.hanjaComponentMap = hanjaComponentMap;
    }

    public VocaHanjaLevel getHanjaLevel() {
        return hanjaLevel;
    }

    public void setHanjaLevel(VocaHanjaLevel hanjaLevel) {
        this.hanjaLevel = hanjaLevel;
    }

    public VocaHanjaVariant getHanjaVariants() {
        return hanjaVariants;
    }

    public void setHanjaVariants(VocaHanjaVariant hanjaVariants) {
        this.hanjaVariants = hanjaVariants;
    }

    public String getHanjaType() {
        return hanjaType;
    }

    public void setHanjaType(String hanjaType) {
        this.hanjaType = hanjaType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndexHanja() {
        return indexHanja;
    }

    public void setIndexHanja(int indexHanja) {
        this.indexHanja = indexHanja;
    }

    public String getKunYomi() {
        return kunYomi;
    }

    public void setKunYomi(String kunYomi) {
        this.kunYomi = kunYomi;
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

    public String getMeaningEng() {
        return meaningEng;
    }

    public void setMeaningEng(String meaningEng) {
        this.meaningEng = meaningEng;
    }

    public String getMeaningEngDetailed() {
        return meaningEngDetailed;
    }

    public void setMeaningEngDetailed(String meaningEngDetailed) {
        this.meaningEngDetailed = meaningEngDetailed;
    }

    public String getMeaningWithPronounceForHanja() {
        return meaningWithPronounceForHanja;
    }

    public void setMeaningWithPronounceForHanja(String meaningWithPronounceForHanja) {
        this.meaningWithPronounceForHanja = meaningWithPronounceForHanja;
    }

    public String getMeaningWithPronounceForRadical() {
        return meaningWithPronounceForRadical;
    }

    public void setMeaningWithPronounceForRadical(String meaningWithPronounceForRadical) {
        this.meaningWithPronounceForRadical = meaningWithPronounceForRadical;
    }

    public String getOnYomi() {
        return onYomi;
    }

    public void setOnYomi(String onYomi) {
        this.onYomi = onYomi;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getRadical() {
        return radical;
    }

    public void setRadical(String radical) {
        this.radical = radical;
    }

    public int getStrokes() {
        return strokes;
    }

    public void setStrokes(int strokes) {
        this.strokes = strokes;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public String getVocaOri() {
        return vocaOri;
    }

    public void setVocaOri(String vocaOri) {
        this.vocaOri = vocaOri;
    }

    public int getVocaOriId() {
        return vocaOriId;
    }

    public void setVocaOriId(int vocaOriId) {
        this.vocaOriId = vocaOriId;
    }

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
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

    public String getVocaMeaningPronounceForHanja() {
        return vocaMeaningPronounceForHanja;
    }

    public void setVocaMeaningPronounceForHanja(String vocaMeaningPronounceForHanja) {
        this.vocaMeaningPronounceForHanja = vocaMeaningPronounceForHanja;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public String getParentVoca() {
        return parentVoca;
    }

    public void setParentVoca(String parentVoca) {
        this.parentVoca = parentVoca;
    }

    @Override
    public int getAmkiId() {
        return getVocaId();
    }

    @Override
    public int getAmkiType() {
        return getVocaType();
    }

    @Override
    public int getAmkiKnow() {
        return getVocaKnow();
    }

    @Override
    public int getAmkiKnowPronounce() {
        return getVocaKnowPronounce();
    }

    @Override
    public String getAmkiEvaluationGrade() {
        return "";
    }

    @Override
    public String getAmki() {
        return "";
    }

    @Override
    public String getHQIVoca() {
        return getVoca();
    }

    @Override
    public String getHQIMeaningWithPronounceForHanja() {
        return getMeaningWithPronounceForHanja();
    }

    @Override
    public String getHQIParentVoca() {
        return getParentVoca();
    }

    @Override
    public void setHQIParentVoca(String parentVoca) {
        setParentVoca(parentVoca);
    }

    @Override
    public int getHQIIndexHanja() {
        return getIndexHanja();
    }

    @Override
    public void setHQIIndexHanja(int indexHanja) {
        setIndexHanja(indexHanja);
    }
}
