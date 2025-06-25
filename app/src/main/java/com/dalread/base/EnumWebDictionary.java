package com.dalread.base;

import java.util.ArrayList;
import java.util.List;

public enum EnumWebDictionary {
    CHINESE_KOREAN_1("다음", "https://m.dic.daum.net/search.do?dic=ch&q=%word%", EnumLanguage.CHINESE_SIMPLIFIED.getIdApi(), EnumLanguage.KOREAN.getIdApi()),

    ENGLISH_KOREAN_1("다음", "https://m.dic.daum.net/search.do?dic=eng&q=%word%", EnumLanguage.ENGLISH.getIdApi(), EnumLanguage.KOREAN.getIdApi()),
    ENGLISH_KOREAN_2("네이버", "https://en.dict.naver.com/#/search?query=%word%&range=all", EnumLanguage.ENGLISH.getIdApi(), EnumLanguage.KOREAN.getIdApi()),
    ENGLISH_JAPANESE("辞書", "https://jisho.org/search/%word%", EnumLanguage.ENGLISH.getIdApi(), EnumLanguage.JAPANESE.getIdApi()),
    ENGLISH_CHINESE("坎布里奇", "https://dictionary.cambridge.org/dictionary/english-chinese-traditional/%word%", EnumLanguage.ENGLISH.getIdApi(), EnumLanguage.CHINESE_SIMPLIFIED.getIdApi()),

    JAPANESE_KOREAN_1("다음", "https://m.dic.daum.net/search.do?dic=jp&q=%word%", EnumLanguage.JAPANESE.getIdApi(), EnumLanguage.KOREAN.getIdApi()),


    KOREAN_CHINESE_2("Naver", "https://zh.dict.naver.com/#/search?query=%word%", EnumLanguage.KOREAN.getIdApi(), EnumLanguage.CHINESE_SIMPLIFIED.getIdApi()),
    KOREAN_ENGLISH_1("Daum", "https://korean.dict.naver.com/koendict/#/search?range=all&query=%word%", EnumLanguage.KOREAN.getIdApi(), EnumLanguage.ENGLISH.getIdApi()),
    KOREAN_JAPANESE_2("ネイバー", "https://ja.dict.naver.com/#/search?query=%word%", EnumLanguage.KOREAN.getIdApi(), EnumLanguage.JAPANESE.getIdApi()),
    KOREAN_JAPANESE_1("glosbe", "https://glosbe.com/ko/ja/%word%", EnumLanguage.KOREAN.getIdApi(), EnumLanguage.JAPANESE.getIdApi()),
    KOREAN_SPANISH_1("del Instituto Nacional de la Lengua Coreana", "https://krdict.korean.go.kr/spa/dicSearch/search?nation=spa&nationCode=9&ParaWordNo=&mainSearchWord=%word%", EnumLanguage.KOREAN.getIdApi(), EnumLanguage.SPANISH.getIdApi()),
    KOREAN_SPANISH_2("yandex", "https://translate.yandex.com/?lang=ko-es&text=%word%", EnumLanguage.KOREAN.getIdApi(), EnumLanguage.SPANISH.getIdApi()),

    ;
    private String title;
    private String url;
    private int studyLang;
    private int motherTongue;

    EnumWebDictionary(String title, String url, int studyLang, int motherTongue) {
        this.title = title;
        this.url = url;
        this.studyLang = studyLang;
        this.motherTongue = motherTongue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStudyLang() {
        return studyLang;
    }

    public void setStudyLang(int studyLang) {
        this.studyLang = studyLang;
    }

    public int getMotherTongue() {
        return motherTongue;
    }

    public void setMotherTongue(int motherTongue) {
        this.motherTongue = motherTongue;
 }

    public static List<EnumWebDictionary> getWebDictionary(int studyLanguage, int motherTongue) {
        final List<EnumWebDictionary> list = new ArrayList<>();
        for (EnumWebDictionary web : values()) {
            if (web.studyLang == studyLanguage && web.motherTongue == motherTongue) {
                list.add(web);
            }
        }
        return list;
    }

    public static EnumWebDictionary[] getAll() {
        return values();
    }

    @Override
    public String toString() {
        return "EnumWebDictionary{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", studyLang=" + studyLang +
                ", motherTongue=" + motherTongue +
                '}';
    }
}
