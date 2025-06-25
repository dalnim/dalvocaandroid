package com.dalread.interfaces;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.util.LanguageUtil;

public interface IVocaBasicItem extends IVocaCoreItem {
    Integer getVIId();
    String getVIVoca();
    Integer getVIVocaKnow();
    Integer getVIVocaKnowPronounce();
    String getVIVocaTTS();
    String getVIPronounce();
    String getVIMeaning(EnumLanguage enumLanguage);
    String getVIMeaningDetailed(EnumLanguage enumLanguage);
    String getVIMeaningTts(EnumLanguage enumLanguage);
    String getVIMeaningEng();
    String getVIMeaningEngDetailed();
    String getVIMeaningEngTts();
    Integer getVIBookmark();
    boolean isVIBookmark();

    void setVIId(Integer value);
    void setVIVoca(String value);
    void setVIVocaTTS(String value);
    void setVIPronounce(String value);
    void setVIMeaning(EnumLanguage enumLanguage, String value);
    void setVIMeaningDetailed(EnumLanguage enumLanguage, String value);
    void setVIMeaningTts(EnumLanguage enumLanguage, String value);
    void setVIMeaningEng(String value);
    void setVIMeaningEngDetailed(String value);
    void setVIMeaningEngTts(String value);
    void setVIVocaKnow(Integer value);
    void setVIVocaKnowPronounce(Integer value);
    void setVIBookmark(Integer value);
    void swapVIBookmark();
    //If Subtitle has only one word, then VocaIdBase is word's ID.
    Integer getVIVocaTypeBase();
    Integer getVIVocaIdBase();
    void setVIVocaTypeBase(Integer value);
    void setVIVocaIdBase(Integer value);

    boolean hasVIVoiceFile();
    default void setVIVoiceFile(Integer value) {

    }

    default String getVIMeaning(Context context) {
        String result = getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)) + "";
        return result.trim();
    }
    default String getVIMeaningDetailed(Context context) {
        String result = getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(context)) + "";
        return result.trim();
    }
    default String getVIMeaningTts(Context context) {
        String result = getVIMeaningTts(LanguageUtil.getMotherTongueLanguage(context)) + "";
        return result.trim();
    }
    default String getVIVocaMeaning(Context context) {
        StringBuilder result = new StringBuilder();
        String voca = getVIVoca() + "";
        voca = voca.trim();
        String meaning = getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)) + "";
        meaning = meaning.trim();
        if (!voca.isEmpty()) {
            result.append(getVIVoca());
        }

        if (!voca.isEmpty() && !meaning.isEmpty()) {
            result.append("\n");
        }

        if (!meaning.isEmpty()) {
            result.append(meaning);
        }

        return result.toString();
//        return  getVIVoca() + "\n" + getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
    }
//    void swapVIHasVoiceFile();
}
