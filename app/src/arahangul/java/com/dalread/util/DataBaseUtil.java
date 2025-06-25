package com.dalread.util;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullItem;

public class DataBaseUtil {
    public static String getMeaning(Context context, IVocaFullItem voca) {
        String result = "";
        EnumLanguage motherTongueLanguage = EnumLanguage.getMotherTongueLanguage(context);
        if (voca instanceof DIC_SENTENCE_MODEL) {
            result = ((DIC_SENTENCE_MODEL) voca).getMEANING(motherTongueLanguage);
        }
        return result;
    }
    public static String getMeaningTts(Context context, IVocaFullItem voca) {
        String result = "";
        EnumLanguage motherTongueLanguage = EnumLanguage.getMotherTongueLanguage(context);
        if (voca instanceof DIC_SENTENCE_MODEL) {
            result = ((DIC_SENTENCE_MODEL) voca).getMEANING_TTS(motherTongueLanguage);
        }
        return result;
    }
    public static String getMeaningDetailed(Context context, IVocaFullItem voca) {
        String result = "";
        EnumLanguage motherTongueLanguage = EnumLanguage.getMotherTongueLanguage(context);
        if (voca instanceof DIC_SENTENCE_MODEL) {
            result = ((DIC_SENTENCE_MODEL) voca).getMEANING_DETAILED(motherTongueLanguage);
        }
        return result;
    }
}
