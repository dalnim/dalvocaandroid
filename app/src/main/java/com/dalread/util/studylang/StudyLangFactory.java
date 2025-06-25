package com.dalread.util.studylang;

import android.content.Context;

import com.dalread.util.LanguageUtil;

public class StudyLangFactory {
    public static AbstractStudyLang create(Context context) {
        AbstractStudyLang studyLang = null;
        if (LanguageUtil.isStudyLangEnglish(context)) {
            studyLang = new StudyLangEnglish(context);
        } else if (LanguageUtil.isStudyLangKorean(context)) {
            studyLang = new StudyLangKorean(context);
        } else if (LanguageUtil.isStudyLangChinese(context)) {
            studyLang = new StudyLangChinese(context);
        } else if (LanguageUtil.isStudyLangJapanese(context)) {
            studyLang = new StudyLangJapanese(context);
        } else if (LanguageUtil.isStudyLangHanja(context)) {
            studyLang = new StudyLangHanja(context);
        } else {
            studyLang = new StudyLangEnglish(context);
        }
        return studyLang;
    }
}
