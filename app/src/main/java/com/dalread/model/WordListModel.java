package com.dalread.model;

import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JetVHS on 3/20/17.
 */
//TODO : 이건 안쓰는거 같음.
public class WordListModel {
    private final String TAG = "WordListModel";
    private List<WordModel> wordModels;
    private List<WordModel> wordModelsUse;
    private String strAddToBookmark = Constant.BASE_BLANK;
    private String strRemoveFromBookmark = Constant.BASE_BLANK;
    private String strSetToUnknownWord = Constant.BASE_BLANK;
    private String strSetToKnownWord = Constant.BASE_BLANK;
    private String strUpdateWordMeaning = Constant.BASE_BLANK;
    private String strUpdateWordPronounce = Constant.BASE_BLANK;

    public WordListModel() {
        this(new ArrayList<WordModel>(), new ArrayList<WordModel>());
    }

    public WordListModel(List<WordModel> wordModels, List<WordModel> wordModelsUse) {
        this.wordModels = wordModels;
        this.wordModelsUse = wordModelsUse;
    }

    public WordListModel(List<WordModel> wordModels) {
        this.wordModels = new ArrayList<>();
        this.wordModelsUse = new ArrayList<>();
        for (WordModel w : wordModels) {
            this.wordModels.add(new WordModel(w));
            this.wordModelsUse.add(new WordModel(w));
        }
    }

    public WordListModel(WordModel wordModel) {
        this.wordModels = new ArrayList<>();
        this.wordModelsUse = new ArrayList<>();
        this.wordModels.add(new WordModel(wordModel));
        this.wordModelsUse.add(new WordModel(wordModel));
    }

    public List<WordModel> getWordModels() {
        return wordModels;
    }

    public void setWordModels(List<WordModel> wordModels) {
        this.wordModels = wordModels;
    }

    public List<WordModel> getWordModelsUse() {
        return wordModelsUse;
    }

    public void setWordModelsUse(List<WordModel> wordModelsUse) {
        this.wordModelsUse = wordModelsUse;
    }

    public int size() {
        return wordModels == null ? 0 : wordModels.size();
    }

    public int sizeUse() {
        return wordModelsUse == null ? 0 : wordModelsUse.size();
    }

    public String getStrAddToBookmark() {
        return strAddToBookmark;
    }

    public void setStrAddToBookmark(String strAddToBookmark) {
        this.strAddToBookmark = strAddToBookmark;
    }

    public String getStrRemoveFromBookmark() {
        return strRemoveFromBookmark;
    }

    public void setStrRemoveFromBookmark(String strRemoveFromBookmark) {
        this.strRemoveFromBookmark = strRemoveFromBookmark;
    }

    public String getStrSetToUnknownWord() {
        return strSetToUnknownWord;
    }

    public void setStrSetToUnknownWord(String strSetToUnknownWord) {
        this.strSetToUnknownWord = strSetToUnknownWord;
    }

    public String getStrSetToKnownWord() {
        return strSetToKnownWord;
    }

    public void setStrSetToKnownWord(String strSetToKnownWord) {
        this.strSetToKnownWord = strSetToKnownWord;
    }

    public String getStrUpdateWordMeaning() {
        return strUpdateWordMeaning;
    }

    public void setStrUpdateWordMeaning(String strUpdateWordMeaning) {
        this.strUpdateWordMeaning = strUpdateWordMeaning;
    }

    public String getStrUpdateWordPronounce() {
        return strUpdateWordPronounce;
    }

    public void setStrUpdateWordPronounce(String strUpdateWordPronounce) {
        this.strUpdateWordPronounce = strUpdateWordPronounce;
    }

    public void checkNeedUpdateToServer() {
        if (wordModels == null)
            return;
        String wordList = Constant.BASE_BLANK;
        String wordMeaning = Constant.BASE_BLANK;
        String wordPos = Constant.BASE_BLANK;
        for (int i = 0; i < size(); i++) {
            // false - true --> add bookmark
            DLog.d(TAG, "---------" + i + "---------");
            DLog.d(TAG, "w1=" + wordModels.get(i).getWord() + " - w2=" + wordModelsUse.get(i).getWord());
            DLog.d(TAG, "b1=" + wordModels.get(i).isBookmark() + " - b2=" + wordModelsUse.get(i).isBookmark());
            if (!wordModels.get(i).isBookmark() && wordModelsUse.get(i).isBookmark()) {
                strAddToBookmark = updateStrText(strAddToBookmark, wordModelsUse.get(i).getWord());
            }
            // true - false --> remove bookmark
            if (wordModels.get(i).isBookmark() && !wordModelsUse.get(i).isBookmark()) {
                strRemoveFromBookmark = updateStrText(strRemoveFromBookmark, wordModelsUse.get(i).getWord());
            }
            // < KNOWN - >= KNOWN --> set to known word
            DLog.d(TAG, "k1=" + wordModels.get(i).getIntKnow() + " - k2=" + wordModelsUse.get(i).getIntKnow());
            if (wordModels.get(i).getIntKnow() < Integer.valueOf(Constant.WORD_KNOW_STATUS.WORD_KNOWN_KNOWN) && wordModelsUse.get(i).getIntKnow() >= Integer.valueOf(Constant.WORD_KNOW_STATUS.WORD_KNOWN_KNOWN)) {
                strSetToKnownWord = updateStrText(strSetToKnownWord, wordModelsUse.get(i).getWord());
            }
            // >= KNOWN - < KNOWN --> set to unknown word
            if (wordModels.get(i).getIntKnow() >= Integer.valueOf(Constant.WORD_KNOW_STATUS.WORD_KNOWN_KNOWN) && wordModelsUse.get(i).getIntKnow() < Integer.valueOf(Constant.WORD_KNOW_STATUS.WORD_KNOWN_KNOWN)) {
                strSetToUnknownWord = updateStrText(strSetToUnknownWord, wordModelsUse.get(i).getWord());
            }
            // Meaning 1 != Meaning 2
            DLog.d(TAG, "m1=" + wordModels.get(i).getMeaning() + " - m2=" + wordModelsUse.get(i).getMeaning());
            if (!wordModels.get(i).getMeaning().equals(wordModelsUse.get(i).getMeaning())) {
                wordList = updateStrTextMeaning(wordList, wordModelsUse.get(i).getWord());
                wordMeaning += wordModelsUse.get(i).getMeaning();
                wordPos += wordModelsUse.get(i).getAllPosCurrentWord();
            }
            // Pronounce 1 != Pronounce 2
            if (!wordModels.get(i).getPronounce().equals(wordModelsUse.get(i).getPronounce())) {
                updateStrText(strUpdateWordPronounce, wordModelsUse.get(i).getWord());
            }
        }
        if (!Utils.isEmpty(wordList)) {
            strUpdateWordMeaning = wordList + "', '" + wordMeaning + "', '" + wordPos;
        }
    }

    private String updateStrText(String value, String add) {
        if (!Utils.isEmpty(value)) {
            value += ",";
        }
        value += add;
        return value;
    }

    private String updateStrTextMeaning(String value, String add) {
        if (!Utils.isEmpty(value)) {
            value += Constant.API.JS_CALL_DALREAD;
        }
        value += add;
        return value;
    }

    @Override
    public String toString() {
        return "WordListModel{" +
                "wordModels=" + wordModels +
                ", wordModelsUse=" + wordModelsUse +
                '}';
    }
}
