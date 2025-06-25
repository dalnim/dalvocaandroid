package com.dalread.util;

import com.dalread.database.sqlite.model.DicModel;

public class VocaKnow extends BaseVocaKnow {
    public static boolean isSetColorOnSubtitle(DicModel dicModel) {
        int vocaKnow = dicModel.getVocaKnow();
        if (isKnown(vocaKnow) || isAmkiGrade1(vocaKnow) || isAmkiGrade2(vocaKnow))
            return true;
        return false;
    }

    public static boolean isNotRated(DicModel dicModel) {
        return dicModel.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
    }

//    public static boolean isKnown(DicModel dicModel) {
//        return dicModel.getVocaKnow() >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
//    }
//
//    public static boolean isUnknown(DicModel dicModel) {
//        return dicModel.getVocaKnow() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
//    }

    public static boolean isDisplayPronounceAlso(DicModel dicModel) {
        if (isUnknownPronounceWhenKnownWord(dicModel)) {
            return true;
        }
        if (!isKnown(dicModel)) {
            return true;
        }
        return false;
    }
}
