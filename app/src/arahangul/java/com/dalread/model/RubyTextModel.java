package com.dalread.model;

import android.text.TextUtils;

import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.dalread.util.VocaKnow;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RubyTextModel {

    private final String TAG = "RubyTextModel";
    private int id;
    private int vocaId;
    private int vocaType;
    private String voca;
    private String pronounce;
    private int vocaKnow = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    private int vocaKnowPronounce = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    private String meaning;
    private String meaningTTS;
    private String meaningEng;
    private String jmdictMeaning; //JM Dictionary (meaning for Japanese language, usually Spanish or German)
    private String jmdictMeaningEng; //JM Dictionary (English meaning for Japanese language)
    private int bookmark;
    @SerializedName("INPUT_TEXT")
    private String inputText;
    @SerializedName("RUBY_TEXT")
    private String content;
    // true when studyLang is JP/CN
    private boolean isKnownPronounceMeaning; //한글 : 이건 영어는 뜻을 일본어/중국어는 발음을 후리가나로 보여주라는건가?
    // using DalPlayer
    private String baseContent;
    private static final boolean DEBUG = false;

    public RubyTextModel() {
    }

    public RubyTextModel(RubyTextModel item) {
        this.id = item.getId();
        this.vocaId = item.getVocaId();
        this.vocaType = item.getVocaType();
        this.voca = item.getVoca();
        this.pronounce = item.getPronounce();
        this.vocaKnow = item.getVocaKnow();
        this.vocaKnowPronounce = item.getVocaKnowPronounce();
        this.meaning = item.getMeaning();
        this.meaningTTS = item.getMeaningTTS();
        this.meaningEng = item.getMeaningEng();
        this.jmdictMeaning = item.getJmdictMeaning();
        this.jmdictMeaningEng = item.getJmdictMeaningEng();
    }

    public void parserFromRubyTextModel(RubyTextModel item) {
        this.id = item.getId();
        this.vocaId = item.getVocaId();
        this.vocaType = item.getVocaType();
        this.voca = item.getVoca();
        this.pronounce = item.getPronounce();
        this.vocaKnow = item.getVocaKnow();
        this.vocaKnowPronounce = item.getVocaKnowPronounce();
        this.meaning = item.getMeaning();
        this.meaningTTS = item.getMeaningTTS();
        this.meaningEng = item.getMeaningEng();
        this.jmdictMeaning = item.getJmdictMeaning();
        this.jmdictMeaningEng = item.getJmdictMeaningEng();
        log(toString());
    }

    public void parserHtml(String content, String html) {
        if (TextUtils.isEmpty(html)) return;
        Document doc = Jsoup.parse(html);
        Elements spans = doc.select(Constant.RUBY.KEY.SPAN);
        log("spans size=" + spans.size());
        for (Element e : spans) {
            vocaId = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_ID));
            vocaType = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_TYPE));
        }
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVocaId() {
        return vocaId;
    }

    public void setVocaId(int vocaId) {
        this.vocaId = vocaId;
    }

    public int getVocaType() {
        return vocaType;
    }

    public void setVocaType(int vocaType) {
        this.vocaType = vocaType;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
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

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getMeaningTTS() {
        return meaningTTS;
    }

    public void setMeaningTTS(String meaningTTS) {
        this.meaningTTS = meaningTTS;
    }

    public String getMeaningEng() {
        return meaningEng;
    }

    public void setMeaningEng(String meaningEng) {
        this.meaningEng = meaningEng;
    }

    public String getJmdictMeaning() {
        return jmdictMeaning;
    }

    public void setJmdictMeaning(String jmdictMeaning) {
        this.jmdictMeaning = jmdictMeaning;
    }

    public String getJmdictMeaningEng() {
        return jmdictMeaningEng;
    }

    public void setJmdictMeaningEng(String jmdictMeaningEng) {
        this.jmdictMeaningEng = jmdictMeaningEng;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public Boolean isBookmark() {
        return bookmark == Constant.RUBY.KEY.BOOKMARK_SHOW;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBaseContent() {
        return baseContent;
    }

    public void setBaseContent(String baseContent) {
        this.baseContent = baseContent;
    }

    public boolean isKnownPronounceMeaning() {
        return isKnownPronounceMeaning;
    }

    public void setKnownPronounceMeaning(boolean knownPronounceMeaning) {
        isKnownPronounceMeaning = knownPronounceMeaning;
    }

    public boolean isHasMeaning() {
        return isKnownPronounceMeaning ?
                !TextUtils.isEmpty(getPronounce()) :
                !TextUtils.isEmpty(getMeaning());
    }

    public boolean isKnown() {
        return vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public boolean isKnownPronounce() {
        return vocaKnowPronounce == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public boolean isShowMeaning() {
        return !(isKnownPronounceMeaning ?
                isKnownPronounce() :
                isKnown());
    }

    public String generateMeaningKnownPronounce() {
        return Constant.RUBY.KEY.BRACKET_LEFT + getMeaning() + Constant.RUBY.KEY.BRACKET_RIGHT;
    }

    public String generateMeaningKnownPronounce(String meaning) {
        return Constant.RUBY.KEY.BRACKET_LEFT + meaning + Constant.RUBY.KEY.BRACKET_RIGHT;
    }
    public void handleDoubleClick() {
        if (isKnownPronounceMeaning) {
            vocaKnowPronounce = VocaKnow.switchVocaKnow(getVocaKnowPronounce());
        } else {
            vocaKnow = VocaKnow.switchVocaKnow(getVocaKnow());
        }
    }

    private void log(String msg) {
        if (DEBUG) {
            DLog.d(TAG, msg);
        }
    }

    @Override
    public String toString() {
        return "RubyTextModel{" +
                "TAG='" + TAG + '\'' +
                ", id=" + id +
                ", vocaId=" + vocaId +
                ", vocaType=" + vocaType +
                ", voca='" + voca + '\'' +
                ", pronounce='" + pronounce + '\'' +
                ", vocaKnow=" + vocaKnow +
                ", vocaKnowPronounce=" + vocaKnowPronounce +
                ", meaning='" + meaning + '\'' +
                ", meaningTSS='" + meaningTTS + '\'' +
                ", meaningEng='" + meaningEng + '\'' +
                ", jmdictMeaning='" + jmdictMeaning + '\'' +
                ", jmdictMeaningEng='" + jmdictMeaningEng + '\'' +
                ", bookmark=" + bookmark +
                ", inputText='" + inputText + '\'' +
                ", content='" + content + '\'' +
                ", isKnownPronounceMeaning=" + isKnownPronounceMeaning +
                ", baseContent='" + baseContent + '\'' +
                '}';
    }
}
