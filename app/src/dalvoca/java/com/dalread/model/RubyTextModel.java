package com.dalread.model;

import android.text.TextUtils;

import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RubyTextModel {

    private final String TAG = "RubyTextModel";
    private int vocaId;
    private int vocaType;
    private int vocaOriId;
    private String voca;
    private String vocaDisplay;
    private String pronounce;
    private int vocaKnow;
    private int vocaKnowPronounce;
    private int amkiGrade;
    private int wordLevel;
    private int bookmark;
    private String pos;
    private String meaning;
    private String meaningTts;
    @SerializedName("INPUT_TEXT")
    private String inputText;
    @SerializedName("RUBY_TEXT")
    private String content;
    // true when studyLang is JP/CN
    private boolean isKnownPronounceMeaning;
    // using DalPlayer
    private String baseContent;
    private static final boolean DEBUG = false;
    
    public RubyTextModel() {
        this(0, 0, 0, Constant.BASE_BLANK,
                Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN,
                Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, 0, 0,
                0, Constant.BASE_BLANK, Constant.BASE_BLANK,
                Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK);
    }

    public RubyTextModel(int vocaId, int vocaType, int vocaOriId, String voca,
                         String vocaDisplay, String pronounce, int vocaKnow,
                         int vocaKnowPronounce, int amkiGrade, int wordLevel,
                         int bookmark, String pos, String meaning,
                         String meaningTts, String inputText, String content) {
        this.vocaId = vocaId;
        this.vocaType = vocaType;
        this.vocaOriId = vocaOriId;
        this.voca = voca;
        this.vocaDisplay = vocaDisplay;
        this.pronounce = pronounce;
        this.vocaKnow = vocaKnow;
        this.vocaKnowPronounce = vocaKnowPronounce;
        this.amkiGrade = amkiGrade;
        this.wordLevel = wordLevel;
        this.bookmark = bookmark;
        this.pos = pos;
        this.meaning = meaning;
        this.meaningTts = meaningTts;
        this.inputText = inputText;
        this.content = content;
    }

    public RubyTextModel(RubyTextModel rubyTextModel) {
        this.vocaId = rubyTextModel.getVocaId();
        this.vocaType = rubyTextModel.getVocaType();
        this.vocaOriId = rubyTextModel.getVocaOriId();
        this.voca = rubyTextModel.getVoca();
        this.vocaDisplay = rubyTextModel.getVocaDisplay();
        this.pronounce = rubyTextModel.getPronounce();
        this.vocaKnow = rubyTextModel.getVocaKnow();
        this.vocaKnowPronounce = rubyTextModel.getVocaKnowPronounce();
        this.amkiGrade = rubyTextModel.getAmkiGrade();
        this.wordLevel = rubyTextModel.getWordLevel();
        this.bookmark = rubyTextModel.getBookmark();
        this.pos = rubyTextModel.getPos();
        this.meaning = rubyTextModel.getMeaning();
        this.meaningTts = rubyTextModel.getMeaningTts();
    }

    public void parserFromRubyTextModel(RubyTextModel rubyTextModel) {
        this.vocaId = rubyTextModel.getVocaId();
        this.vocaType = rubyTextModel.getVocaType();
        this.vocaOriId = rubyTextModel.getVocaOriId();
        this.voca = rubyTextModel.getVoca();
        this.vocaDisplay = rubyTextModel.getVocaDisplay();
        this.pronounce = rubyTextModel.getPronounce();
        this.vocaKnow = rubyTextModel.getVocaKnow();
        this.vocaKnowPronounce = rubyTextModel.getVocaKnowPronounce();
        this.amkiGrade = rubyTextModel.getAmkiGrade();
        this.wordLevel = rubyTextModel.getWordLevel();
        this.bookmark = rubyTextModel.getBookmark();
        this.pos = rubyTextModel.getPos();
        this.meaning = rubyTextModel.getMeaning();
        this.meaningTts = rubyTextModel.getMeaningTts();
        log(toString());
    }

    public void parserHtml(String content, String html) {
        if (TextUtils.isEmpty(html)) return;
        Document doc = Jsoup.parse(html);
        Elements spans = doc.select(Constant.RUBY.KEY.SPAN);
        log("spans size=" + spans.size());
        for (Element e : spans) {
            this.vocaId = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_ID));
            this.vocaType = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_TYPE));
            this.vocaOriId = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_ORI_ID));
            this.voca = e.attr(Constant.RUBY.KEY.VOCA);
            this.vocaDisplay = e.attr(Constant.RUBY.KEY.VOCA_DISPLAY);
            this.pronounce = e.attr(Constant.RUBY.KEY.PRONOUNCE);
            if (e.hasAttr(Constant.RUBY.KEY.VOCA_KNOW)) {
                this.vocaKnow = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_KNOW));
            } else {
                this.vocaKnow = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
            }
            if (e.hasAttr(Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE)) {
                this.vocaKnowPronounce = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE));
            } else {
                this.vocaKnowPronounce = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
            }
            this.amkiGrade = Utils.parseInt(e.attr(Constant.RUBY.KEY.AMKI_GRADE));
            this.wordLevel = Utils.parseInt(e.attr(Constant.RUBY.KEY.WORD_LEVEL));
            this.bookmark = Utils.parseInt(e.attr(Constant.RUBY.KEY.BOOKMARK));
            this.pos = e.attr(Constant.RUBY.KEY.POS);
            this.meaning = e.attr(Constant.RUBY.KEY.MEANING);
            this.meaningTts = e.attr(Constant.RUBY.KEY.MEANING_TTS);
        }
        this.content = content;
        log(toString());
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

    public int getVocaOriId() {
        return vocaOriId;
    }

    public void setVocaOriId(int vocaOriId) {
        this.vocaOriId = vocaOriId;
    }

    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public String getVocaDisplay() {
        return Utils.isEmpty(vocaDisplay) ? voca : vocaDisplay;
    }

    public void setVocaDisplay(String vocaDisplay) {
        this.vocaDisplay = vocaDisplay;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public int getAmkiGrade() {
        return amkiGrade;
    }

    public void setAmkiGrade(int amkiGrade) {
        this.amkiGrade = amkiGrade;
    }

    public int getWordLevel() {
        return wordLevel;
    }

    public void setWordLevel(int wordLevel) {
        this.wordLevel = wordLevel;
    }

    public int getBookmark() {
        return bookmark;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getMeaningTts() {
        return meaningTts;
    }

    public void setMeaningTts(String meaningTts) {
        this.meaningTts = meaningTts;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
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

    public Boolean isBookmark() {
        return bookmark == Constant.RUBY.KEY.BOOKMARK_SHOW;
    }

    public void setBookmark(boolean isBookmark) {
        this.bookmark = isBookmark ?
                Constant.RUBY.KEY.BOOKMARK_SHOW :
                Constant.RUBY.KEY.BOOKMARK_HIDE;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
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
                !TextUtils.isEmpty(pronounce) :
                !TextUtils.isEmpty(meaning);
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

    public void setKnownValue(boolean isKnown) {
        this.vocaKnow = isKnown ?
                Constant.VOCA_KNOW.VOCA_KNOW_KNOWN :
                Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
    }

    public void setKnownPronounceValue(boolean isKnown) {
        this.vocaKnowPronounce = isKnown ?
                Constant.VOCA_KNOW.VOCA_KNOW_KNOWN :
                Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
    }

    public String generateKnowValue(boolean isKnown) {
        return isKnown ?
                Constant.RUBY.KEY.KNOW_STR_KNOWN :
                Constant.RUBY.KEY.KNOW_STR_UNKNOWN;
    }

    public String generateKnowValue(int value) {
        return Constant.RUBY.KEY.VOCA_KNOW + "=" + value;
    }

    public String generateKnowPronounceValue(boolean isKnown) {
        return isKnown ?
                Constant.RUBY.KEY.KNOW_PRONOUNCE_STR_KNOW :
                Constant.RUBY.KEY.KNOW_PRONOUNCE_STR_UNKNOWN;
    }

    public String generateKnowPronounceValue(int value) {
        return Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE + "=" + value;
    }

    public String generateBookmarkValue(boolean showBookmark) {
        return showBookmark ?
                Constant.RUBY.KEY.BOOKMARK_STR_SHOW :
                Constant.RUBY.KEY.BOOKMARK_STR_HIDE;
    }

    public String generateMeaning(String meaning) {
        return Constant.RUBY.KEY.MEANING + "=\"" + meaning + "\"";
    }

    public String generatePronounce(String pronounce) {
        return Constant.RUBY.KEY.PRONOUNCE + "=\"" + pronounce + "\"";
    }

    public String generateKnownPronounce(String pronounce) {
        return Constant.RUBY.KEY.SEMICOLON + pronounce;
    }

    public String generateMeaningKnownPronounce() {
        return Constant.RUBY.KEY.BRACKET_LEFT + meaning + Constant.RUBY.KEY.BRACKET_RIGHT;
    }

    public String generateMeaningKnownPronounce(String meaning) {
        return Constant.RUBY.KEY.BRACKET_LEFT + meaning + Constant.RUBY.KEY.BRACKET_RIGHT;
    }

    public void handleDoubleClick() {
        if (isKnownPronounceMeaning) {
            updateKnownPronounce();
        } else {
            updateKnownWord();
        }
    }

    public void updateKnownWord() {
        if (TextUtils.isEmpty(content)) return;
        try {
            log("content=" + content);
            final String wordId = Constant.RUBY.KEY.VOCA_ID + "=" + vocaId;
            log("wordId=" + wordId);
            int startIndex = content.indexOf(wordId);
            log("startIndex=" + startIndex);
            int endIndex = content.indexOf(Constant.RUBY.KEY.VOCA_KNOW, startIndex) + (Constant.RUBY.KEY.VOCA_KNOW.length() + 2);
            log("endIndex=" + endIndex);
            if (startIndex >= 0 && endIndex > 0) {
                while (endIndex < content.length() - 1) {
                    if (String.valueOf(content.charAt(++endIndex)).equals(" "))
                        break;
                }
                String oldString = content.substring(startIndex, endIndex);
                log("oldString=" + oldString);
                String oldValue = generateKnowValue(vocaKnow);
                log("oldValue=" + oldValue);
                String newString = oldString.replace(oldValue, generateKnowValue(!isKnown()));
                log("newString=" + newString);
                log("oldContent=" + content);
                content = content.replace(oldString, newString);
                log("newContent=" + content);
                updateBaseContent(oldString, newString);
            }
            setKnownValue(!isKnown());
        } catch (Exception ex) {
            DLog.e(TAG, ex.toString());
        }
    }

    public void updateKnownPronounce() {
        if (TextUtils.isEmpty(content)) return;
        try {
            log("content=" + content);
            final String wordId = Constant.RUBY.KEY.VOCA_ID + "=" + vocaId;
            log("wordId=" + wordId);
            int startIndex = content.indexOf(wordId);
            log("startIndex=" + startIndex);
            int endIndex = content.indexOf(Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE, startIndex) + (Constant.RUBY.KEY.VOCA_KNOWPRONOUNCE.length() + 2);
            log("endIndex=" + endIndex);
            if (startIndex >= 0 && endIndex > 0) {
                while (endIndex < content.length() - 1) {
                    if (String.valueOf(content.charAt(++endIndex)).equals(" "))
                        break;
                }
                String oldString = content.substring(startIndex, endIndex);
                String oldValue = generateKnowPronounceValue(vocaKnowPronounce);
                log("oldValue=" + oldValue);
                String newString = oldString.replace(oldValue, generateKnowPronounceValue(!isKnownPronounce()));
                log("oldString=" + oldString);
                log("newString=" + newString);
                log("oldContent=" + content);
                content = content.replace(oldString, newString);
                log("newContent=" + content);
                updateBaseContent(oldString, newString);
            }
            setKnownPronounceValue(!isKnownPronounce());
        } catch (Exception ex) {
            DLog.e(TAG, ex.toString());
        }
    }

    public void updateBookmark() {
        if (TextUtils.isEmpty(content)) return;
        try {
            log("content=" + content);
            final String wordId = Constant.RUBY.KEY.VOCA_ID + "=" + vocaId;
            log("wordId=" + wordId);
            int startIndex = content.indexOf(wordId);
            log("startIndex=" + startIndex);
            int endIndex = content.indexOf(Constant.RUBY.KEY.BOOKMARK, startIndex) + (Constant.RUBY.KEY.BOOKMARK.length() + 2);
            log("endIndex=" + endIndex);
            if (startIndex >= 0 && endIndex > 0) {
                String oldString = content.substring(startIndex, endIndex);
                String oldValue = generateBookmarkValue(isBookmark());
                log("oldValue=" + oldValue);
                String newString = oldString.replace(oldValue, generateBookmarkValue(!isBookmark()));
                log("oldString=" + oldString);
                log("newString=" + newString);
                log("oldContent=" + content);
                content = content.replace(oldString, newString);
                log("newContent=" + content);
                updateBaseContent(oldString, newString);
            }
            setBookmark(!isBookmark());
        } catch (Exception ex) {
            DLog.e(TAG, ex.toString());
        }
    }

    public void updateWordMeaning(String meaning) {
        if (TextUtils.isEmpty(content)) return;
        try {
            log("content=" + content);
            final String wordId = Constant.RUBY.KEY.VOCA_ID + "=" + vocaId;
            log("wordId=" + wordId);
            int startIndex = content.indexOf(wordId);
            log("startIndex=" + startIndex);
            String oldValue = generateMeaning(getMeaning());
            log("oldValue=" + oldValue);
            int endIndex = content.indexOf(oldValue, startIndex) + oldValue.length() + 1;
            log("endIndex=" + endIndex);
            if (startIndex >= 0 && endIndex > 0) {
                String oldString = content.substring(startIndex, endIndex);
                String newString = oldString.replace(oldValue, generateMeaning(meaning));
                log("oldString=" + oldString);
                log("newString=" + newString);
                log("oldContent=" + content);
                content = content.replace(oldString, newString);
                log("newContent=" + content);
                updateBaseContent(oldString, newString);
            }
            updateKnownWord(startIndex, meaning);
        } catch (Exception ex) {
            DLog.e(TAG, ex.toString());
        }
    }

    public void updateWordPronounce(String pronounce) {
        if (TextUtils.isEmpty(content)) return;
        try {
            log("content=" + content);
            final String wordId = Constant.RUBY.KEY.VOCA_ID + "=" + vocaId;
            log("wordId=" + wordId);
            int startIndex = content.indexOf(wordId);
            log("startIndex=" + startIndex);
            String oldValue = generatePronounce(getPronounce());
            log("oldValue=" + oldValue);
            int endIndex = content.indexOf(oldValue, startIndex) + oldValue.length() + 1;
            log("endIndex=" + endIndex);
            if (startIndex >= 0 && endIndex > 0) {
                String oldString = content.substring(startIndex, endIndex);
                String newString = oldString.replace(oldValue, generatePronounce(pronounce));
                log("oldString=" + oldString);
                log("newString=" + newString);
                log("oldContent=" + content);
                content = content.replace(oldString, newString);
                log("newContent=" + content);
                updateBaseContent(oldString, newString);
            }
            updateKnownPronounce(startIndex, pronounce);
        } catch (Exception ex) {
            DLog.e(TAG, ex.toString());
        }
    }

    public void updateKnownWord(int startIndex, String meaning) {
        if (TextUtils.isEmpty(content)) return;
        try {
            log("startIndex=" + startIndex);
            String oldValue = generateMeaningKnownPronounce(getMeaning());
            log("oldValue=" + oldValue);
            int endIndex = content.indexOf(oldValue, startIndex) + oldValue.length() + 1;
            log("endIndex=" + endIndex);
            if (startIndex >= 0 && endIndex > 0) {
                String oldString = content.substring(startIndex, endIndex);
                String newString = oldString.replace(oldValue, generateMeaningKnownPronounce(meaning));
                log("oldString=" + oldString);
                log("newString=" + newString);
                log("oldContent=" + content);
                content = content.replace(oldString, newString);
                log("newContent=" + content);
                updateBaseContent(oldString, newString);
            }
            setMeaning(meaning);
        } catch (Exception ex) {
            DLog.e(TAG, ex.toString());
        }
    }


    public void updateKnownPronounce(int startIndex, String pronounce) {
        if (TextUtils.isEmpty(content)) return;
        try {
            log("startIndex=" + startIndex);
            String oldValue = generateKnownPronounce(getPronounce());
            log("oldValue=" + oldValue);
            int endIndex = content.indexOf(oldValue, startIndex) + oldValue.length() + 1;
            log("endIndex=" + endIndex);
            if (startIndex >= 0 && endIndex > 0) {
                String oldString = content.substring(startIndex, endIndex);
                String newString = oldString.replace(oldValue, generateKnownPronounce(pronounce));
                log("oldString=" + oldString);
                log("newString=" + newString);
                log("oldContent=" + content);
                content = content.replace(oldString, newString);
                log("newContent=" + content);
                updateBaseContent(oldString, newString);
            }
            setPronounce(pronounce);
        } catch (Exception ex) {
            DLog.e(TAG, ex.toString());
        }
    }

    private void updateBaseContent(String oldString, String newString) {
        if (!Utils.isEmpty(baseContent)) {
            baseContent = baseContent.replace(oldString, newString);
        }
    }

    @Override
    public String toString() {
        return "RubyTextModel{" +
                "TAG='" + TAG + '\'' +
                ", vocaId=" + vocaId +
                ", vocaType=" + vocaType +
                ", vocaOriId=" + vocaOriId +
                ", voca='" + voca + '\'' +
                ", vocaDisplay='" + vocaDisplay + '\'' +
                ", pronounce='" + pronounce + '\'' +
                ", vocaKnow=" + vocaKnow +
                ", vocaKnowPronounce=" + vocaKnowPronounce +
                ", amkiGrade=" + amkiGrade +
                ", wordLevel=" + wordLevel +
                ", bookmark=" + bookmark +
                ", pos='" + pos + '\'' +
                ", meaning='" + meaning + '\'' +
                ", meaningTts='" + meaningTts + '\'' +
                ", inputText='" + inputText + '\'' +
                ", content='" + content + '\'' +
                ", isKnownPronounceMeaning=" + isKnownPronounceMeaning +
                ", baseContent='" + baseContent + '\'' +
                '}';
    }

    private void log(String msg) {
        if (DEBUG) {
            log(msg);
        }
    }
}
