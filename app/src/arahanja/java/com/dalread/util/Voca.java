package com.dalread.util;

import android.content.Context;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaBookChapterModel;
import com.dalread.model.HanjaItem;
import com.dalread.model.RubyTextModel;
import com.dalread.model.VOCABOOKS_HANJA;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;
import com.dalread.model.VOCABOOK_HANJA;
import com.dalread.model.VOCABOOK_HANJA_CLASSICS;
import com.dalread.model.VocaBookmark;
import com.dalread.model.VocaKnowAndBookmarkList;
import com.dalread.model.VocaKnowAndKnowpronounce;
import com.dalread.model.VocaTypeId;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class Voca extends BaseVoca {

    private static final String TAG = "VocaQuery";
//    @BindColor(R.color.color_hanja_keyword)
//    static int clHanjaKeyword;

    public static RealmQuery<DIC_HANJA> searchHanjaByAllMethods(Realm realm, String keyword) {
        String keywordWithoutSpace = keyword.replace(" ", "");
        return realm.where(DIC_HANJA.class)
                .contains(Constant.REALMDB.KEY_VOCA, keyword)
                .or()
                .contains(Constant.REALMDB.KEY_PRONOUNCE1_FIRST, keyword)
                .or()
                .contains(Constant.REALMDB.KEY_PRONOUNCE1, keyword)
                .or()
                .contains(Constant.REALMDB.KEY_MEANING1, keyword)
                .or()
                .contains(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST, keywordWithoutSpace)
                .or()
                .contains(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1, keywordWithoutSpace);
    }

    public static Set<HanjaItem> searchHanjaByAllMethodsSorted(Realm realm, String keyword) {
        Set<HanjaItem> result = new LinkedHashSet<>();
        seachHanjaOneWord(realm, keyword, result);
        searchHanjaSentence(realm, keyword, result);

        return result;
    }

    private static void searchHanjaSentence(Realm realm, String keyword, Set<HanjaItem> result) {
        if (StringUtils.isOnlyChinese(keyword)) {
            RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByVocaEqualToResults = searchHanjaSentenceByVocaEqualTo(realm, keyword);
            result.addAll(realm.copyFromRealm(searchHanjaSentenceByVocaEqualToResults));
            RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByVocaBeginsWithResults = searchHanjaSentenceByVocaBeginsWith(realm, keyword);
            result.addAll(realm.copyFromRealm(searchHanjaSentenceByVocaBeginsWithResults));
            RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByVocaContainsResults = searchHanjaSentenceByVocaContains(realm, keyword);
            result.addAll(realm.copyFromRealm(searchHanjaSentenceByVocaContainsResults));
        } else {
            RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByPronounceResultsEqualTo = searchHanjaSentenceByPronounceEqualTo(realm, keyword);
            RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByPronounceResultsBeginWith = searchHanjaSentenceByPronounceBeginWith(realm, keyword);
            RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByPronounceResultsContains = searchHanjaSentenceByPronounceContains(realm, keyword);
//            RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByMeaningResults = searchHanjaSentenceByMeaning(realm, keyword);


            result.addAll(realm.copyFromRealm(searchHanjaSentenceByPronounceResultsEqualTo));
            result.addAll(realm.copyFromRealm(searchHanjaSentenceByPronounceResultsBeginWith));
            result.addAll(realm.copyFromRealm(searchHanjaSentenceByPronounceResultsContains));
//            result.addAll(realm.copyFromRealm(searchHanjaSentenceByMeaningResults));
        }
    }

    private static void seachHanjaOneWord(Realm realm, String keyword, Set<HanjaItem> result) {
        // "𠂉"의 경우에는 한글자인데 length가 2로 나온다. getBytes()로보면 4바이트임. 그래서 codePoints().count()로 추가로 검사한다.
        boolean isKeywordChineseOnly = StringUtils.isOnlyChinese(keyword);
        if ((keyword.length() == 1) || (keyword.codePoints().count() == 1)) {
            if (isKeywordChineseOnly) {
                RealmResults<DIC_HANJA> searchHanjaByVocaResults = searchHanjaByVocaEqualTo(realm, keyword);
                result.addAll(realm.copyFromRealm(searchHanjaByVocaResults));
            } else {
                RealmResults<DIC_HANJA> searchHanjaByPronounceResults = searchHanjaByPronounceEqualTo(realm, keyword);
                result.addAll(realm.copyFromRealm(searchHanjaByPronounceResults));
            }
        }

        if (!isKeywordChineseOnly) {
            RealmResults<DIC_HANJA> searchHanjaByMeaningPronounceFirst = searchHanjaByMeaningPronounceFirst(realm, keyword);
            RealmResults<DIC_HANJA> searchHanjaByMeaningPronounceFirstWithSpace = searchHanjaByMeaningPronounceFirstWithSpace(realm, keyword);
            RealmResults<DIC_HANJA> searchHanjaByMeaningPronounceFirstResultsBeginsWith = searchHanjaByMeaningPronounceFirstBeginsWith(realm, keyword);
            RealmResults<DIC_HANJA> searchHanjaByMeaningPronounceFirstResultsContains = searchHanjaByMeaningPronounceFirstContains(realm, keyword);

            result.addAll(realm.copyFromRealm(searchHanjaByMeaningPronounceFirst));
            result.addAll(realm.copyFromRealm(searchHanjaByMeaningPronounceFirstWithSpace));
            result.addAll(realm.copyFromRealm(searchHanjaByMeaningPronounceFirstResultsBeginsWith));
            result.addAll(realm.copyFromRealm(searchHanjaByMeaningPronounceFirstResultsContains));
        }
    }

    public static DIC_HANJA searchHanjaWordByVocaID(Realm realm, long vocaID) {
        return realm.where(DIC_HANJA.class)
                .equalTo(Constant.REALMDB.KEY_ID, vocaID)
                .findFirst();
    }

    public static RealmResults<DIC_HANJA> searchHanjaByVocaEqualTo(Realm realm, String keyword) {
        return realm.where(DIC_HANJA.class)
                .equalTo(Constant.REALMDB.KEY_VOCA, keyword)
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE, Sort.ASCENDING);
    }

    public static RealmResults<DIC_HANJA> searchHanjaByPronounceEqualTo(Realm realm, String keyword) {
        return realm.where(DIC_HANJA.class)
                .equalTo(Constant.REALMDB.KEY_PRONOUNCE1_FIRST, keyword)
                .or()
                .equalTo(Constant.REALMDB.KEY_PRONOUNCE1_FIRST, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
//                .or()
//                .equalTo(Constant.REALMDB.KEY_PRONOUNCE1, keyword)
//                .equalTo(Constant.REALMDB.KEY_PRONOUNCE1, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE1_FIRST)
                .sort(Constant.REALMDB.KEY_MEANING1)
                ;
    }

    public static DIC_HANJA_SENTENCE searchHanjaSentenceByID(Long id) {
        List<DIC_HANJA_SENTENCE> dicHanjaSentences = new ArrayList<>();
        executeRealmTransaction(realm -> {
            DIC_HANJA_SENTENCE dicHanjaSentence = realm.where(DIC_HANJA_SENTENCE.class)
                    .equalTo(Constant.REALMDB.KEY_ID, id)
                    .findFirst();
            dicHanjaSentences.add(realm.copyFromRealm(dicHanjaSentence));
        });

        if (dicHanjaSentences.size() > 0) {
            return dicHanjaSentences.get(0);
        }
        return null;
    }

    public static DIC_HANJA_BOOK searchHanjaBookByID(Long id) {
        List<DIC_HANJA_BOOK> listResult = new ArrayList<>();
        executeRealmTransaction(realm -> {
            DIC_HANJA_BOOK dic = realm.where(DIC_HANJA_BOOK.class)
                    .equalTo(Constant.REALMDB.KEY_ID, id)
                    .findFirst();
            listResult.add(realm.copyFromRealm(dic));
        });

        if (listResult.size() > 0) {
            return listResult.get(0);
        }
        return null;
    }

    public static List<DIC_HANJA_SENTENCE> searchHanjaSentenceByPronounceContains(String text) {
        List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            List<DIC_HANJA_SENTENCE> dicHanjaSentenceListInRealm = realm.where(DIC_HANJA_SENTENCE.class)
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE, text)
                    .findAll();
            dicHanjaSentenceList.addAll(realm.copyFromRealm(dicHanjaSentenceListInRealm));
        });
        return dicHanjaSentenceList;
    }

    public static List<DIC_HANJA_SENTENCE> searchHanjaSentenceByVoca(String text) {
        List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            List<DIC_HANJA_SENTENCE> dicHanjaSentenceListInRealm = realm.where(DIC_HANJA_SENTENCE.class)
                    .equalTo(Constant.REALMDB.KEY_VOCA, text)
                    .findAll();
            dicHanjaSentenceList.addAll(realm.copyFromRealm(dicHanjaSentenceListInRealm));
        });
        return dicHanjaSentenceList;
    }

    public static DIC_HANJA searchHanjaWordByID(Long id) {
        List<DIC_HANJA> dicHanjas = new ArrayList<>();
        executeRealmTransaction(realm -> {
            DIC_HANJA dicHanja = realm.where(DIC_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_ID, id)
                    .findFirst();
            dicHanjas.add(realm.copyFromRealm(dicHanja));
        });

        if (dicHanjas.size() > 0) {
            return dicHanjas.get(0);
        }
        return null;
    }

    public static List<DIC_HANJA> searchHanjaWordByText(String text) {
        List<DIC_HANJA> dicHanjaList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            List<DIC_HANJA> dicHanjas = realm.where(DIC_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE1_FIRST, text)
                    .or()
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE1, text)
                    .or()
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE2_FIRST, text)
                    .or()
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE2, text)
                    .or()
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE3_FIRST, text)
                    .or()
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE3, text)
                    .or()
                    .findAll();
            dicHanjaList.addAll(realm.copyFromRealm(dicHanjas));
        });

        return dicHanjaList;
    }

    public static DIC_HANJA searchHanjaWordByWord(String hanja) {
        List<DIC_HANJA> dicHanjas = new ArrayList<>();
        executeRealmTransaction(realm -> {
            DIC_HANJA dicHanja = realm.where(DIC_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_VOCA, hanja)
                    .findFirst();
            dicHanjas.add(realm.copyFromRealm(dicHanja));
        });

        if (dicHanjas.size() > 0) {
            return dicHanjas.get(0);
        }
        return null;
    }

    public static RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByVocaEqualTo(Realm realm, String keyword) {
        return realm.where(DIC_HANJA_SENTENCE.class)
                .equalTo(Constant.REALMDB.KEY_VOCA, keyword)
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE, Sort.ASCENDING);
    }

    public static RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByVocaBeginsWith(Realm realm, String keyword) {
        return realm.where(DIC_HANJA_SENTENCE.class)
                .beginsWith(Constant.REALMDB.KEY_VOCA, keyword)
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE, Sort.ASCENDING);
    }

    public static RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByVocaContains(Realm realm, String keyword) {
        return realm.where(DIC_HANJA_SENTENCE.class)
                .contains(Constant.REALMDB.KEY_VOCA, keyword)
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE, Sort.ASCENDING);
    }


    public static RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByPronounceEqualTo(Realm realm, String keyword) {
        return realm.where(DIC_HANJA_SENTENCE.class)
                .equalTo(Constant.REALMDB.KEY_PRONOUNCE, keyword)
                .or()
                .equalTo(Constant.REALMDB.KEY_PRONOUNCE, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE)
//                .sort(Constant.REALMDB.KEY_MEANING_KO)
                ;
    }

    public static RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByPronounceBeginWith(Realm realm, String keyword) {
        return realm.where(DIC_HANJA_SENTENCE.class)
                .beginsWith(Constant.REALMDB.KEY_PRONOUNCE, keyword)
                .or()
                .beginsWith(Constant.REALMDB.KEY_PRONOUNCE, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE)
//                .sort(Constant.REALMDB.KEY_MEANING_KO)
                ;
    }

    public static RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByPronounceContains(Realm realm, String keyword) {
        return realm.where(DIC_HANJA_SENTENCE.class)
                .contains(Constant.REALMDB.KEY_PRONOUNCE, keyword)
                .or()
                .contains(Constant.REALMDB.KEY_PRONOUNCE, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE)
//                .sort(Constant.REALMDB.KEY_MEANING_KO)
                ;
    }

    public static RealmResults<DIC_HANJA_SENTENCE> searchHanjaSentenceByMeaning(Realm realm, String keyword) {
        return realm.where(DIC_HANJA_SENTENCE.class)
                .contains(Constant.REALMDB.KEY_MEANING_KO, keyword)
                .contains(Constant.REALMDB.KEY_MEANING_KO, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_PRONOUNCE)
                .sort(Constant.REALMDB.KEY_MEANING_KO)
                ;
    }

    public static RealmResults<DIC_HANJA> searchHanjaByMeaningBeginWith(Realm realm, String keyword) {
        return realm.where(DIC_HANJA.class)
                .beginsWith(Constant.REALMDB.KEY_MEANING1, keyword)
                .beginsWith(Constant.REALMDB.KEY_MEANING1, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_MEANING1)
                .sort(Constant.REALMDB.KEY_PRONOUNCE1_FIRST)
                ;
    }

    public static RealmResults<DIC_HANJA> searchHanjaByMeaningContains(Realm realm, String keyword) {
        return realm.where(DIC_HANJA.class)
                .contains(Constant.REALMDB.KEY_MEANING1, keyword)
                .findAll()
                .sort(Constant.REALMDB.KEY_MEANING1)
                .sort(Constant.REALMDB.KEY_PRONOUNCE1_FIRST)
                ;
    }

    public static RealmResults<DIC_HANJA> searchHanjaByMeaningPronounceFirst(Realm realm, String keyword) {
        String keywordWithoutSpace = keyword.replace(" ", "");
        return realm.where(DIC_HANJA.class)
                .equalTo(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST, keywordWithoutSpace)
                .or()
                .equalTo(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST)
                ;
    }

    //https://www.mongodb.com/docs/realm-legacy/docs/java/latest/#logical-operators
    public static RealmResults<DIC_HANJA> searchHanjaByMeaningPronounceFirstWithSpace(Realm realm, String keyword) {
        String[] keywordWithoutSpace = keyword.split(" ");
        RealmResults<DIC_HANJA> results = null;
        if (keywordWithoutSpace.length == 2) {
            String meaning = keywordWithoutSpace[0];
            String pronounce = keywordWithoutSpace[1];

            results = realm.where(DIC_HANJA.class)
                    .beginGroup()
                    .equalTo(Constant.REALMDB.KEY_MEANING1, meaning)
                    .or()
                    .equalTo(Constant.REALMDB.KEY_MEANING1, LanguageUtilKorean.normalizeFirstCharHangul(meaning))
                    .endGroup()
                    .and()
                    .beginGroup()
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE1_FIRST, pronounce)
                    .or()
                    .equalTo(Constant.REALMDB.KEY_PRONOUNCE1_FIRST, LanguageUtilKorean.normalizeFirstCharHangul(pronounce))
                    .endGroup()
                    .findAll()
                    .sort(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST)
            ;
        }
        return results;
    }

    public static RealmResults<DIC_HANJA> searchHanjaByMeaningPronounceFirstBeginsWith(Realm realm, String keyword) {
        String keywordWithoutSpace = keyword.replace(" ", "");
        return realm.where(DIC_HANJA.class)
                .beginsWith(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST, keywordWithoutSpace)
                .or()
                .beginsWith(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST)
                ;
    }

    public static RealmResults<DIC_HANJA> searchHanjaByMeaningPronounceFirstContains(Realm realm, String keyword) {
        String keywordWithoutSpace = keyword.replace(" ", "");
        return realm.where(DIC_HANJA.class)
                .contains(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST, keywordWithoutSpace)
                .or()
                .contains(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST, LanguageUtilKorean.normalizeFirstCharHangul(keyword))
                .findAll()
                .sort(Constant.REALMDB.KEY_MEANING1_PRONOUNCE1_FIRST)
                ;
    }

    public static void searchAlternativeMeaningPronounceFirst(DIC_HANJA dicHanja) {
        executeRealmTransaction(realm -> {
            searchAlternativeMeaningPronounceFirst(realm, dicHanja);
        });
    }

    public static void searchAlternativeMeaningPronounceFirst(Realm realm, DIC_HANJA dicHanja) {
        if (needToGetVocaOriMeaningOrPronounce(dicHanja)) {
            String keyword = dicHanja.getVOCAORI();
            if (!TextUtils.isEmpty(keyword)) {
                DIC_HANJA dicHanja1 = Voca.searchHanjaByAllMethods(realm, keyword).findFirst();
                String ko = dicHanja1.getMEANING_KO();
                String ko_Detailed = dicHanja1.getMEANING_KO_DETAILED();
                if (dicHanja1 != null) {
                    if (TextUtils.isEmpty(dicHanja.getMEANING1().trim())) {
                        dicHanja.setMEANING1(dicHanja1.getMEANING1());
                    }
                    if (TextUtils.isEmpty(dicHanja.getPRONOUNCE1_FIRST().trim())) {
                        dicHanja.setPRONOUNCE1_FIRST(dicHanja1.getPRONOUNCE1_FIRST());
                    }
                    if (TextUtils.isEmpty(dicHanja.getMEANING_KO().trim())) {
                        dicHanja.setMEANING_KO(dicHanja1.getMEANING_KO());
                    }
                    if (TextUtils.isEmpty(dicHanja.getMEANING_KO_DETAILED().trim())) {
                        dicHanja.setMEANING_KO_DETAILED(dicHanja1.getMEANING_KO_DETAILED());
                    }
                }
            }
        }
    }

    private static boolean needToGetVocaOriMeaningOrPronounce(DIC_HANJA dicHanja) {
        if (dicHanja == null)
            return false;

        boolean blnResult = false;
        boolean hasVocaOri = false;
        if (!dicHanja.getVOCA().equals(dicHanja.getVOCAORI()))
            hasVocaOri = true;

        if ((dicHanja.getMEANING1() == null || dicHanja.getPRONOUNCE1_FIRST() == null)
            && hasVocaOri) {
            blnResult = true;
        } else if (TextUtils.isEmpty(dicHanja.getMEANING1()) || TextUtils.isEmpty(dicHanja.getPRONOUNCE1_FIRST())
                && hasVocaOri) {
            blnResult = true;
        }
        return blnResult;
    }


    public static RealmResults<DIC_HANJA_BOOK> temp(Realm realm) {
        DIC_HANJA dicTemp1 = realm.where(DIC_HANJA.class)
                .findFirst();
        DIC_HANJA_BOOK dicTemp2 = realm.where(DIC_HANJA_BOOK.class)
                .findFirst();
        VOCABOOKS_HANJA_CLASSICS dicTemp3 = realm.where(VOCABOOKS_HANJA_CLASSICS.class)
                .findFirst();
        VOCABOOK_HANJA_CLASSICS dicTemp4 = realm.where(VOCABOOK_HANJA_CLASSICS.class)
                .findFirst();
        RealmResults<DIC_HANJA_BOOK> dicTemp = realm.where(DIC_HANJA_BOOK.class)
                .findAll();
        return dicTemp;

    }

    public static List<HanjaBookChapterModel> getHanjaBookChapterList1(Realm realm, long id) {
        Long[] chapterIDList = getHanjaBookContentIDList(realm, id);
        List<DIC_HANJA_BOOK> dicHanjaBooks = realm.where(DIC_HANJA_BOOK.class)
                .in(Constant.REALMDB.KEY_ID, chapterIDList)
                .findAll();

        List<HanjaBookChapterModel> hanjaBookChapterModelList = new ArrayList<>();
        dicHanjaBooks.stream().forEach(e -> hanjaBookChapterModelList.add(new HanjaBookChapterModel(e.getVOCA(), e.getPRONOUNCE(), e.getMEANING_KO(), e.getMEANING_KO_DETAILED())));
        return hanjaBookChapterModelList;

    }

    public static Map<String, HanjaItem> searchHanjaByBookHanja(Realm realm, String content) {
        Set<HanjaItem> result = new LinkedHashSet<>();
        RealmResults<DIC_HANJA> searchHanjaByVocaResults = realm.where(DIC_HANJA.class)
                .in(Constant.REALMDB.KEY_VOCA, content.split(""))
                .findAll();
        result.addAll(realm.copyFromRealm(searchHanjaByVocaResults));
        Map<String, HanjaItem> mapResult = new HashMap<String, HanjaItem>();
        for (HanjaItem hanja : result) {
            mapResult.put(hanja.getHI_VOCA(), hanja);
        }
        return mapResult;
    }

    public static List<DIC_HANJA_BOOK> getHanjaBookContentListByBookIdAndKeyword(long bookId, String keyword) {
        List<DIC_HANJA_BOOK> result = new ArrayList<>();
        executeRealmTransaction(realm -> {
            Long[] bookIdList = getBookIdListWhenItHasSubBookIdList(realm, bookId);
            Long[] bookContentIdList = getHanjaBookContentIDList(realm, bookIdList);
//            Long[] bookContentIdList = getHanjaBookContentIDList(realm, bookId);
            RealmResults<DIC_HANJA_BOOK> searchHanjaBookChatpersResult = searchHanjaBookContents(realm, bookContentIdList);
            List<DIC_HANJA_BOOK> dicHanjaBookList = realm.copyFromRealm(searchHanjaBookChatpersResult);
            Long index = 1L;
            for (DIC_HANJA_BOOK dicHanjaBook : dicHanjaBookList) {
                dicHanjaBook.setHI_INDEX(index++);
                if (dicHanjaBook.getVOCA().contains(keyword)) {
                    result.add(dicHanjaBook);
                } else if (dicHanjaBook.getPRONOUNCE().contains(keyword)) {
                    result.add(dicHanjaBook);
                } else if (dicHanjaBook.getMEANING_KO().contains(keyword)) {
                    result.add(dicHanjaBook);
                }

            }
        });

        return result;

    }

    public static List<DIC_HANJA_BOOK> getHanjaItemListFromBookContent(Realm realm, long id) {
        List<DIC_HANJA_BOOK> result = new ArrayList<>();

        Long[] bookIdList = getBookIdListWhenItHasSubBookIdList(realm, id);
        Long[] bookContentIdList = getHanjaBookContentIDList(realm, bookIdList);
        RealmResults<DIC_HANJA_BOOK> searchHanjaBookChatpersResult = searchHanjaBookContents(realm, bookContentIdList);
        result.addAll(realm.copyFromRealm(searchHanjaBookChatpersResult));
        return result;

    }

    private static RealmResults<DIC_HANJA_BOOK> searchHanjaBookContents(Realm realm, Long[] chapterIDList) {
        return realm.where(DIC_HANJA_BOOK.class)
                .in(Constant.REALMDB.KEY_ID, chapterIDList)
                .findAll();
    }

    @NotNull
    private static Long[] getHanjaBookContentIDList(Realm realm, long id) {
        List<VOCABOOK_HANJA_CLASSICS> vocabookHanjaClassicsList = realm.where(VOCABOOK_HANJA_CLASSICS.class)
                .equalTo(Constant.REALMDB.KEY_VOCABOOKS_ID, id)
                .findAll();

        List<Long> chapterIDListTemp = vocabookHanjaClassicsList.stream().map(e -> e.getVOCA_ID()).collect(Collectors.toList());
        Long[] chapterIDList = convertToArrayFromListLong(chapterIDListTemp);
        return chapterIDList;
    }

    @NotNull
    private static Long[] getHanjaBookContentIDList(Realm realm, Long[] bookConentIdList) {
        List<VOCABOOK_HANJA_CLASSICS> vocabookHanjaClassicsList = realm.where(VOCABOOK_HANJA_CLASSICS.class)
                .in(Constant.REALMDB.KEY_VOCABOOKS_ID, bookConentIdList)
                .findAll();

        List<Long> chapterIDListTemp = vocabookHanjaClassicsList.stream().map(e -> e.getVOCA_ID()).collect(Collectors.toList());
        Long[] chapterIDList = convertToArrayFromListLong(chapterIDListTemp);
        return chapterIDList;
    }

    @NotNull
    private static Long[] getBookIdListWhenItHasSubBookIdList(Realm realm, long id) {
        VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics = realm.where(VOCABOOKS_HANJA_CLASSICS.class)
                .equalTo(Constant.REALMDB.KEY_ID, id)
                .findFirst();

        List<Long> chapterIDListTemp = new ArrayList<>();
        String subIdList = vocabooksHanjaClassics.getID_LIST();
        if (Utils.isEmpty(subIdList)) {
            chapterIDListTemp.add(vocabooksHanjaClassics.getID());
        } else {
            subIdList = StringUtils.removeSpaces(subIdList);
            chapterIDListTemp = Stream
                    .of(subIdList.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }
        Long[] chapterIDList = convertToArrayFromListLong(chapterIDListTemp);
        return chapterIDList;
    }


    @NotNull
    public static void saveLastReadBookPageNo(long id, long lastReadBookRowNo) {
        Voca.executeRealmTransaction(realm -> {
            VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics = realm.where(VOCABOOKS_HANJA_CLASSICS.class)
                    .equalTo(Constant.REALMDB.KEY_ID, id)
                    .findFirst();
            if (vocabooksHanjaClassics != null) {
                vocabooksHanjaClassics.setIMAGE_ID(lastReadBookRowNo);
                realm.insertOrUpdate(vocabooksHanjaClassics); //Need this?
            }
        });
    }

    public static int getLastReadBookPageNo(Realm realm, long id) {
        VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics = realm.where(VOCABOOKS_HANJA_CLASSICS.class)
                .equalTo(Constant.REALMDB.KEY_ID, id)
                .findFirst();

        int result = 0;
        if (vocabooksHanjaClassics != null)
            result = vocabooksHanjaClassics.getIMAGE_ID().intValue();
        return result;
    }

    public static VOCABOOKS_HANJA_CLASSICS getVocaBooksHanjaClassics(Realm realm, long id) {
        List<VOCABOOKS_HANJA_CLASSICS> resultList = new ArrayList<>();
        VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics = realm.where(VOCABOOKS_HANJA_CLASSICS.class)
                .equalTo(Constant.REALMDB.KEY_ID, id)
                .findFirst();
        resultList.add(realm.copyFromRealm(vocabooksHanjaClassics));
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }

    public static Long[] convertToArrayFromListLong(List<Long> listLong) {
        Long[] arrayLong = new Long[listLong.size()];
        for (int i = 0; i < listLong.size(); i++) {
            Long chapterID = listLong.get(i);
            arrayLong[i] = chapterID;
        }
        return arrayLong;
    }

    public static List<String> getRubyTextAndUnknownWord(String content, String pronounces) {
        List<String> listResult = new ArrayList<>();
        if (content.trim().length() == 0) {
            listResult.add("");
            listResult.add("");
            return listResult;
        }

        StringBuilder unknownWordText = new StringBuilder();
        StringBuilder sbRubyText = new StringBuilder();
        if (pronounces == null)
            pronounces = "";
        //한자에 해당되는 Ruby를 가지고 있다.
        Map<String, RubyTextModel> mapRubyTextModel = getMapRubyTextModel(content);
        //한자의 인덱스에 해당되는 발음들을 가지고 있다.
        Map<Integer, Map<String, String>> mapVocaWithPronounce = getMapOnlyHanjaWithPronounce(content, pronounces);
        Map<RubyTextModel, String> mapRubyTextModelWithoutDuplication = new HashMap<>();

        //한자 및 특수문자를 포함한 content원본을 각 글자들의 리스트로 만듬.
        List<String> listContent = getHanjaBookContentAsList(content, pronounces);
        int indexForHanja = 0;
        for (int i = 0; i < listContent.size(); i++) {
            String oneChar = listContent.get(i);
            String rubyText = oneChar;
            //한자이면 루비텍스트를 만들어준다.
            if (mapRubyTextModel.containsKey(oneChar)) {
                RubyTextModel rubyTextModel = mapRubyTextModel.get(oneChar);
                String pronounce = rubyTextModel.getPronounce();
                int vocaKnow = rubyTextModel.getVocaKnow();
                int vocaKnowPronounce = rubyTextModel.getVocaKnowPronounce();

                //Use book's pronounce for the voca has pronounce in the book
                //책에 있는 발음을 가져오는데, 한자문장과 발음이 정확하게 매칭되면 i를 쓰고 아니면, indexForHanja를 써야한다.
                int indexInMapVocaWithPronounce = i;
                if (mapVocaWithPronounce.size() != listContent.size()) {
                    indexInMapVocaWithPronounce = indexForHanja;
                }
                if (mapVocaWithPronounce.containsKey(indexInMapVocaWithPronounce)) {
                    Map<String, String> mapVoca = mapVocaWithPronounce.get(indexInMapVocaWithPronounce);
                    if (mapVoca.containsKey(oneChar)) {
                        pronounce = mapVoca.get(oneChar);
                    }
                }

                //Display unknown word's meaning and pronounce
                if ((vocaKnow != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) || (vocaKnowPronounce != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)) {
                    if (!(mapRubyTextModelWithoutDuplication.containsKey(rubyTextModel))) {
                        if ((!Utils.isEmpty(rubyTextModel.getMeaning())) && (!Utils.isEmpty(rubyTextModel.getPronounce()))) {
                            mapRubyTextModelWithoutDuplication.put(rubyTextModel, "");
                            String vocaWithVocaOri = rubyTextModel.getVoca().equals(rubyTextModel.getVocaOri()) ? rubyTextModel.getVoca() : rubyTextModel.getVoca() + "(" + rubyTextModel.getVocaOri() + ")";
                            unknownWordText.append(vocaWithVocaOri + "[" + rubyTextModel.getMeaning() + " " + rubyTextModel.getPronounce() + "] ");
                        }
                    }
                }

                rubyText = "<span VOCA_TYPE=" + rubyTextModel.getVocaType() + " VOCA_ID=" + rubyTextModel.getVocaId() + " PRONOUNCE=" + pronounce +
                        " MEANING=" + rubyTextModel.getMeaning() + " VOCA_KNOW=" + vocaKnow + " VOCA_KNOWPRONOUNCE=" + vocaKnowPronounce +
                        "><ruby><rb>" + rubyTextModel.getVoca() + "</rb><rt>" + pronounce + "</rt></ruby></span>";
                indexForHanja++;
            }
            sbRubyText.append(rubyText);
        }
        listResult.add(sbRubyText.toString());
        listResult.add(unknownWordText.toString());
        return listResult;
    }

    //Same Voca can have different pronounce by the position in the sentence, so use Integer as index and key.
    private static Map<Integer, Map<String, String>> getMapVocaWithPronounce(String content, String pronounces) {
        Map<Integer, Map<String, String>> mapResult = new HashMap<>();
        if (content.length() == pronounces.length()) {
            getMapContentAndPronounce(mapResult, content, pronounces);
        } else {
            String contentChineseWithoutNumbers = StringUtils.removeNumbers(content);
            String contentKoreanWithoutNumbers = StringUtils.removeNumbers(pronounces);
            if (contentChineseWithoutNumbers.trim().length() == contentKoreanWithoutNumbers.trim().length()) {
                getMapContentAndPronounce(mapResult, contentChineseWithoutNumbers, contentKoreanWithoutNumbers);
            } else {
                String contentChineseOnly = StringUtils.getOnlyChinese(content);
                String pronouncesKoreanOnly = StringUtils.getOnlyKorean(pronounces);
                if (contentChineseOnly.trim().length() == pronouncesKoreanOnly.trim().length()) {
                    getMapContentAndPronounce(mapResult, contentChineseOnly, pronouncesKoreanOnly);
                }
            }
        }
        return mapResult;
    }

    //두음법칙이나 특별한 발음등일때를 위해서, 고전에는 한자문장과 발음을 같이 제공해준다. 한자문장과 발음의 갯수가 일치하지 않으면 특수문자들을 먼저 제거하고 한자문장으로 For문을 돌고, 없는 발음은 비워둔다.
    //고전을 만들때 한자문장과 발음의 갯수를 일치 치켜야한다.
    //리턴값 인덱스<한자,발음>
    private static Map<Integer, Map<String, String>> getMapOnlyHanjaWithPronounce(String content, String pronounces) {
        Map<Integer, Map<String, String>> mapResult = new HashMap<>();
        List<String> listContent = new ArrayList<>();
        List<String> listPronounce = new ArrayList<>();
        if (content.length() == pronounces.length()) {
            listContent = Arrays.asList(content.trim().split(""));
            listPronounce = Arrays.asList(pronounces.split(""));
            // 이게 있으니 양혜왕에서 "우록탁탁이어늘 백조"의 발음이 "우록탁탁하고 백조"처럼 갯수가 안맞으니까 "우록탁탁하고 하고"로 보이게 된다.
//        } else {
//            String contentChineseOnly = StringUtils.getOnlyChinese(content);
//            String pronouncesKoreanOnly = StringUtils.getOnlyKorean(pronounces);
//
//            listContent = Arrays.asList(contentChineseOnly.split(""));
//            listPronounce = Arrays.asList(pronouncesKoreanOnly.split(""));
        }
        for (int i = 0; i < listContent.size(); i++) {
            String voca = listContent.get(i);
            String pronounce = (i < listPronounce.size()) ? listPronounce.get(i) : "";
            Map<String, String> mapVoca = new HashMap<>();
            mapVoca.put(voca, pronounce);
            mapResult.put(i, mapVoca);
        }
        return mapResult;
    }

    private static List<String> getHanjaBookContentAsList(String content, String pronounces) {
        return Arrays.asList(content.trim().split(""));
        //원래는 발음이 있는 한자만을 따로 뽑았으나 이러면 고전에서 특수문자가 안보이는 버그가 있어서, 그냥 본문을 리스트로만 변환해서 리턴한다.
//        List<String> listContent = Arrays.asList(content.trim().split(""));
//        if (content.length() != pronounces.length()) {
//            String contentChineseWithoutNumbers = StringUtils.removeNumbers(content);
//            String contentKoreanWithoutNumbers = StringUtils.removeNumbers(pronounces);
//            if (contentChineseWithoutNumbers.trim().length() == contentKoreanWithoutNumbers.trim().length()) {
//                listContent = Arrays.asList(contentChineseWithoutNumbers.trim().split(""));
//            } else {
//                String contentChineseOnly = StringUtils.getOnlyChinese(content);
//                String pronouncesKoreanOnly = StringUtils.getOnlyKorean(pronounces);
//                if ((!Utils.isEmpty(contentChineseOnly)
//                        && (contentChineseOnly.trim().length() == pronouncesKoreanOnly.trim().length()))) {
//                    listContent = Arrays.asList(contentChineseOnly.trim().split(""));
//                }
//            }
//        }
//        return listContent;
    }

    private static void getMapContentAndPronounce(Map<Integer, Map<String, String>> mapResult, String contentChineseOnly, String pronouncesKoreanOnly) {
        List<String> listContent = Arrays.asList(contentChineseOnly.trim().split(""));
        List<String> listPronounce = Arrays.asList(pronouncesKoreanOnly.trim().split(""));
        for (int i = 0; i < listContent.size(); i++) {
            String voca = listContent.get(i);
            String pronounce = listPronounce.get(i);
            Map<String, String> mapVoca = new HashMap<>();
            mapVoca.put(voca, pronounce);
            mapResult.put(i, mapVoca);
        }
    }

    private static Map<String, RubyTextModel> getMapRubyTextModel(String content) {
        //Can get voca's KNOW and want to use it to hide pronouce if I know the voca and set the color to red if I don't know the voca.
        Map<String, RubyTextModel> mapRubyTextModel = new HashMap<>();

        executeRealmTransaction(realm -> {
            Map<String, HanjaItem> mapHanjaInBook = Voca.searchHanjaByBookHanja(realm, content);

            for (HanjaItem hanja : mapHanjaInBook.values()) {
                if (hanja instanceof DIC_HANJA) {
                    DIC_HANJA dicHanja = (DIC_HANJA) hanja;
                    searchAlternativeMeaningPronounceFirst(realm, dicHanja);
                    RubyTextModel rubyTextModel = parserToRubyTextModel(dicHanja);
                    mapRubyTextModel.put(rubyTextModel.getVoca(), rubyTextModel);
                }
            }

        });


        return mapRubyTextModel;
    }

    private static RubyTextModel parserToRubyTextModel(DIC_HANJA dicHanja) {
        RubyTextModel model = new RubyTextModel();
        model.setVocaId(dicHanja.getHI_ID().intValue());
        model.setVocaType(dicHanja.getHI_VOCA_TYPE());
        model.setVoca(dicHanja.getVOCA());
        model.setPronounce(dicHanja.getHI_PRONOUNCE1_FIRST());
        model.setVocaOriId(dicHanja.getVOCAORI_ID().intValue());
        model.setVocaOri(dicHanja.getVOCAORI());
        model.setVocaKnow(dicHanja.getHI_VOCA_KNOW().intValue());
        model.setVocaKnowPronounce(dicHanja.getHI_VOCA_KNOWPRONOUNCE().intValue());
        model.setMeaning(dicHanja.getMEANING1());
        model.setMeaningTts(model.getMeaning());
        return model;
    }

    public static boolean hasHanjaWordItemListFromContent(String contents) {
        return getHanjaWordItemListFromContent(contents, false).size() > 0;
    }
    public static List<HanjaItem> getHanjaWordItemListFromContent(String contents, boolean isFindSentence) {
        List<HanjaItem> hanjaItemList = new ArrayList<>();

        if (isFindSentence) {
            List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = searchHanjaSentenceByVocaRecursive(contents);
            if (dicHanjaSentenceList.size() > 0) {
                hanjaItemList.add(dicHanjaSentenceList.get(0));
                contents = contents.substring(dicHanjaSentenceList.get(0).getVOCA().length() - 1, contents.length() - 1);
            }
        }

        if (!Utils.isEmpty(contents)) {
            //remove duplicate char then get only Hanja information.
            Set<HanjaItem> hanjas = new LinkedHashSet<>();
            String[] vocas = contents.split("(?!^)");

            executeRealmTransaction(realm -> {
                RealmResults<DIC_HANJA> results = realm.where(DIC_HANJA.class)
                        .in(Constant.REALMDB.KEY_VOCA, vocas)
                        .findAll();
                Map<String, DIC_HANJA> hanjaMap = new HashMap<>();
                DIC_HANJA dicHanja;
                for (DIC_HANJA dh : results) {
                    dicHanja = realm.copyFromRealm(dh);
                    hanjaMap.put(dicHanja.getVOCA(), dicHanja);
                }
                for (String voca : vocas) {
                    dicHanja = hanjaMap.get(voca);
                    if (dicHanja != null) {
                        Voca.searchAlternativeMeaningPronounceFirst(realm, dicHanja);
                        hanjas.add(dicHanja);
                    }
                }
            });
            if (!hanjas.isEmpty()) {
                hanjaItemList.addAll(hanjas);
            }
        }
        return hanjaItemList;
    }

    private static List<DIC_HANJA_SENTENCE> searchHanjaSentenceByVocaRecursive(String text) {
        List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = Voca.searchHanjaSentenceByVoca(text);
        if ((dicHanjaSentenceList == null) || (dicHanjaSentenceList.isEmpty())) {
            if (text.length() > 2) {
                text = text.substring(0, text.length() - 1);
                dicHanjaSentenceList = searchHanjaSentenceByVocaRecursive(text);
            }
        }
        return dicHanjaSentenceList;
    }

    public static List<HanjaItem> getHanjaWordItemListFromVocaTypeIdList(List<VocaTypeId> vocaTypeIdList) {
        Long[] hanjaWordIDArray = new Long[vocaTypeIdList.size()];
        Long[] hanjaSentenceIDArray = new Long[vocaTypeIdList.size()];
        Long[] vocaIDArray = new Long[vocaTypeIdList.size()];
        Long[] vocaTypeArray = new Long[vocaTypeIdList.size()];
        for (int i = 0; i < vocaTypeIdList.size(); i++) {
            VocaTypeId vocaTypeId = vocaTypeIdList.get(i);
            if (vocaTypeId.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) {
                hanjaWordIDArray[i] = Long.valueOf(vocaTypeId.getVocaId());
            } else if (vocaTypeId.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE) {
                hanjaSentenceIDArray[i] = Long.valueOf(vocaTypeId.getVocaId());
            }
            vocaIDArray[i] = Long.valueOf(vocaTypeId.getVocaId());
            vocaTypeArray[i] = Long.valueOf(vocaTypeId.getVocaType());
        }

        List<HanjaItem> hanjaItemListUnsorted = Voca.getHanjaListInDicHanjaByVocaIDList(hanjaWordIDArray);
        hanjaItemListUnsorted.addAll(Voca.getHanjaListInDicHnajaSentenceByVocaIDList(hanjaSentenceIDArray));
        return Voca.sortHanjaListByDispOrderOfVocaIDList(hanjaItemListUnsorted, vocaTypeArray, vocaIDArray);
    }


    public static String getColoredKeywordInMeaningAndPronounce(HanjaItem hanja, String keyword, Context context) {
        String meaning = hanja.getHI_MEANING1() == null ? "" : hanja.getHI_MEANING1();
        String pronounce = hanja.getHI_PRONOUNCE1_FIRST() == null ? "" : hanja.getHI_PRONOUNCE1_FIRST();
        String keywordWithTag = getColoredHtml(keyword, context);

        if (pronounce.contains(keyword)) {
            pronounce = pronounce.replaceAll(keyword, keywordWithTag);
        } else if (meaning.contains(keyword)) {
            meaning = meaning.replaceAll(keyword, keywordWithTag);
        }
        return meaning + " " + pronounce;
    }


    public static String getColoredPronounceWithMeaning(HanjaItem hanja, Context context) {
        String result = "";
        String pronounce = hanja.getHI_PRONOUNCE1_FIRST() == null ? "" : hanja.getHI_PRONOUNCE1_FIRST();
        if (VocaKnow.isVocaTypeSentence(hanja)) {
            result = getColoredHtml(pronounce, context);
        } else {
            String meaning = hanja.getHI_MEANING1() == null ? "" : hanja.getHI_MEANING1();
            result = meaning + " " + getColoredHtml(pronounce, context);
        }
        return result;
    }

    private static String getColoredHtml(String keyword, Context context) {
        int color = ContextCompat.getColor(context, R.color.color_hanja_keyword);
        String keywordColor = String.format("#%06X", (0xFFFFFF & color));
        return "<font color=" + keywordColor + ">" + "<b>" + keyword + "</b>" + "</font>";
    }

    public static void updateItemInDic(HanjaItem hanjaItem) {
        if (hanjaItem instanceof DIC_HANJA) {
        } else if (hanjaItem instanceof DIC_HANJA_SENTENCE) {
        } else if (hanjaItem instanceof DIC_HANJA_BOOK) {
            updateItemInDicHanjaBook((DIC_HANJA_BOOK) hanjaItem);
        }
    }

    private static void updateItemInDicHanjaBook(DIC_HANJA_BOOK dicHanjaBook) {
        executeRealmTransaction(realm -> {
            DIC_HANJA_BOOK dicHanjaBook1 = realm.where(DIC_HANJA_BOOK.class).equalTo(Constant.REALMDB.KEY_ID, dicHanjaBook.getID()).findFirst();
            if (dicHanjaBook1 != null) {
                dicHanjaBook1.setBOOKMARK(dicHanjaBook.getBOOKMARK());
            }
        });
    }

    public static List<VOCABOOK_HANJA> getVocabookHanjaListByID(long bookId) {
        List<VOCABOOK_HANJA> vocabookHanjaList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            RealmResults<VOCABOOK_HANJA> realmResults = realm.where(VOCABOOK_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_VOCABOOKS_ID, bookId)
                    .sort(Constant.REALMDB.KEY_DISP_ORDER)
                    .findAll();
            vocabookHanjaList.addAll(realm.copyFromRealm(realmResults));
        });
        return vocabookHanjaList;
    }

    public static List<HanjaItem> getHanjaListInDicHanjaByVocaIDList(Long[] vocaIDList) {
        List<HanjaItem> hanjaItemList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            List<DIC_HANJA> dicHanjaList = realm.where(DIC_HANJA.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDList)
                    .findAll();
            hanjaItemList.addAll(realm.copyFromRealm(dicHanjaList));
        });
        return hanjaItemList;
    }

    public static List<HanjaItem> getHanjaListInDicHnajaSentenceByVocaIDList(Long[] vocaIDList) {
        List<HanjaItem> hanjaItemList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            List<DIC_HANJA_SENTENCE> dicHanjaList = realm.where(DIC_HANJA_SENTENCE.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDList)
                    .findAll();
            hanjaItemList.addAll(realm.copyFromRealm(dicHanjaList));
        });
        return hanjaItemList;
    }

    //Dalnim : Need to update logic to sort faster
    public static List<HanjaItem> sortHanjaListByDispOrderOfVocaIDList(List<HanjaItem> hanjaItemListUnsorted, Long[] vocaTypeArray, Long[] vocaIDList) {
        List<HanjaItem> hanjaItemList = new ArrayList<>();
        if (vocaTypeArray.length != vocaIDList.length)
            return  hanjaItemListUnsorted;

        Map<String, HanjaItem> mapHanjaItem = new HashMap<>(); //hanjaItemListUnsorted.stream().collect(Collectors.toMap(HanjaItem::getHI_VOCA_TYPE + "_" + HanjaItem::getHI_ID, e -> e ));
        for(HanjaItem hanjaItem : hanjaItemListUnsorted) {
            String vocaTypeID = hanjaItem.getHI_VOCA_TYPE() + "_" + hanjaItem.getHI_ID();
            mapHanjaItem.put(vocaTypeID, hanjaItem);
        }
        for (Integer i = 0; i < vocaTypeArray.length; i++) {
            Long vocaType = vocaTypeArray[i];
            Long vocaID = vocaIDList[i];
//            Log.d("dal", "Array vocaType : " + vocaType + ", vocaID : " + vocaID);
            String vocaTypeID = vocaType + "_" + vocaID;
            if (mapHanjaItem.containsKey(vocaTypeID)) {
                hanjaItemList.add(mapHanjaItem.get(vocaTypeID));
            }
        }

        return hanjaItemList;
    }


    //Dalnim : Need to update logic to sort faster
    public static List<HanjaItem> sortHanjaListByDispOrderOfVocaIDList1(List<HanjaItem> hanjaItemListUnsorted, Long[] vocaTypeArray, Long[] vocaIDList) {
        List<HanjaItem> hanjaItemList = new ArrayList<>();
        if (vocaTypeArray.length != vocaIDList.length)
            return  hanjaItemListUnsorted;

        hanjaItemListUnsorted = hanjaItemListUnsorted.stream().sorted(Comparator.comparingLong(HanjaItem::getHI_ID)).collect(Collectors.toList());
        List<HanjaItem> hanjaItemWordList = new ArrayList<>();
        List<HanjaItem> hanjaItemSentenceList = new ArrayList<>();
        for(HanjaItem hanjaItem : hanjaItemListUnsorted) {
            if (hanjaItem.getHI_VOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) {
                hanjaItemWordList.add(hanjaItem);
            } else if (hanjaItem.getHI_VOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE) {
                hanjaItemSentenceList.add(hanjaItem);
            }
        }




        for (Integer i = 0; i < vocaTypeArray.length; i++) {
            Long vocaType = vocaTypeArray[i];
            Long vocaID = vocaIDList[i];
//            Log.d("dal", "Array vocaType : " + vocaType + ", vocaID : " + vocaID);
            int j = 0;

            if (vocaType == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) {
                hanjaItemList.add(getHanjaItemByID(hanjaItemWordList, vocaID));
            } else if (vocaType == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE) {
                hanjaItemList.add(getHanjaItemByID(hanjaItemSentenceList, vocaID));
            }

//            Iterator<HanjaItem> it = hanjaItemListUnsorted.iterator();
//            while (it.hasNext()) {
//                HanjaItem hanjaItem = it.next();
//                Log.d("dal", "HanjaItem " + hanjaItemListUnsorted.size() + ", j : " + j++ + ", " + "vocaType : " + hanjaItem.getHI_VOCA_TYPE() + ", vocaID : " + hanjaItem.getHI_ID() + ", " + hanjaItem.getHI_VOCA());
//                if ((hanjaItem.getHI_VOCA_TYPE() == vocaType) && (hanjaItem.getHI_ID().equals(vocaID))) {
//                    hanjaItemList.add(hanjaItem);
//                    it.remove();
//                    break;
//                }
//            }
        }

        return hanjaItemList;
    }

    private static HanjaItem getHanjaItemByID(List<HanjaItem> hanjaItemList, long vocaID) {
        int mid = 0;
        int left = 0;
        int right = hanjaItemList.size() - 1;

        while (right >= left) {
            mid = (right + left) / 2;

            if (vocaID == hanjaItemList.get(mid).getHI_ID()) {
                break;
            }

            if (vocaID < hanjaItemList.get(mid).getHI_ID()) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }

        }
        return hanjaItemList.get(mid);
    }

    //When RealmDB is updated, backup this data and restore them by using updateVocaKnowAndVocaKnowpronounceAndBookmark
    public static VocaKnowAndBookmarkList getVocaKnowAndVocaKnowpronounceAndBookmark(Realm realm) {
        VocaKnowAndBookmarkList vocaKnowAndBookmarkList = new VocaKnowAndBookmarkList();
        vocaKnowAndBookmarkList.setVocaBookmarkList(getVocaBookmarkList(realm));
        vocaKnowAndBookmarkList.setVocaKnowKnowpronounceList(getVocaKnowKnowpronounceList(realm));
        return vocaKnowAndBookmarkList;
    }

    public static List<HanjaItem> getVocaBookmarkList() {
        List<HanjaItem> hanjaItemList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            getVocaBookmarkListFromWord(hanjaItemList, realm);

            getVocaBookmarkListFromSentence(hanjaItemList, realm);

            getVocaBookmarkListFromBookContent(hanjaItemList, realm);
        });
        return hanjaItemList;
    }

    public static List<HanjaItem> getUnicodeHanjaList() {
        List<HanjaItem> hanjaItemList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            List<DIC_HANJA> dicHanjaListRealm = realm.where(DIC_HANJA.class)
                    .findAll()
                    .sort(Constant.REALMDB.KEY_UNICODE_DEC);
            List<DIC_HANJA> dicHanjaList = realm.copyFromRealm(dicHanjaListRealm);
            hanjaItemList.addAll(dicHanjaList);

        });
        return hanjaItemList;
    }

    private static void getVocaBookmarkListFromBookContent(List<HanjaItem> hanjaItemList, Realm realm) {
        List<DIC_HANJA_BOOK> dicHanjaBookListRealm = realm.where(DIC_HANJA_BOOK.class)
                .equalTo(Constant.REALMDB.KEY_BOOKMARK, Constant.INT_BOOLEAN.TRUE)
                .findAll();
        List<DIC_HANJA_BOOK> dicHanjaBookList = realm.copyFromRealm(dicHanjaBookListRealm);
        Collections.sort(dicHanjaBookList, Comparator.nullsLast(Comparator.comparing(DIC_HANJA_BOOK::getPRONOUNCE)));
        hanjaItemList.addAll(dicHanjaBookList);
    }

    private static void getVocaBookmarkListFromSentence(List<HanjaItem> hanjaItemList, Realm realm) {
        List<DIC_HANJA_SENTENCE> dicHanjaSentenceListRealm = realm.where(DIC_HANJA_SENTENCE.class)
                .equalTo(Constant.REALMDB.KEY_BOOKMARK, Constant.INT_BOOLEAN.TRUE)
                .findAll();
        List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = realm.copyFromRealm(dicHanjaSentenceListRealm);
        Collections.sort(dicHanjaSentenceList, Comparator.nullsLast(Comparator.comparing(DIC_HANJA_SENTENCE::getPRONOUNCEOrEmptyString)));
        hanjaItemList.addAll(dicHanjaSentenceList);
    }

    private static void getVocaBookmarkListFromWord(List<HanjaItem> hanjaItemList, Realm realm) {
        List<DIC_HANJA> dicHanjaListRealm = realm.where(DIC_HANJA.class)
                .equalTo(Constant.REALMDB.KEY_BOOKMARK, Constant.INT_BOOLEAN.TRUE)
                .findAll();
        List<DIC_HANJA> dicHanjaList = realm.copyFromRealm(dicHanjaListRealm);
        Collections.sort(dicHanjaList, Comparator.nullsLast(Comparator.comparing(DIC_HANJA::getPRONOUNCE1_FIRST)
                .thenComparing(DIC_HANJA::getMEANING_KO)
                .thenComparingLong(DIC_HANJA::getSTROKES)));
        hanjaItemList.addAll(dicHanjaList);
    }

    public static List<VocaBookmark> getVocaBookmarkList(Realm realm) {
        List<VocaBookmark> vocaBookmarkList = new ArrayList<>();
        realm.executeTransaction(realm1 -> {
            List<DIC_HANJA> dicHanjaList = realm1.where(DIC_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_BOOKMARK, Constant.INT_BOOLEAN.TRUE)
                    .findAll();
            for(DIC_HANJA dicHanja : dicHanjaList) {
                VocaBookmark vocaBookmark = new VocaBookmark();
                vocaBookmark.setVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_WORD);
                vocaBookmark.setVocaId(dicHanja.getID().intValue());
                vocaBookmarkList.add(vocaBookmark);
            }
            List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = realm1.where(DIC_HANJA_SENTENCE.class)
                    .equalTo(Constant.REALMDB.KEY_BOOKMARK, Constant.INT_BOOLEAN.TRUE)
                    .findAll();
            for(DIC_HANJA_SENTENCE dicHanja : dicHanjaSentenceList) {
                VocaBookmark vocaBookmark = new VocaBookmark();
                vocaBookmark.setVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
                vocaBookmark.setVocaId(dicHanja.getID().intValue());
                vocaBookmarkList.add(vocaBookmark);
            }
            List<DIC_HANJA_BOOK> dicHanjaBookList = realm1.where(DIC_HANJA_BOOK.class)
                    .equalTo(Constant.REALMDB.KEY_BOOKMARK, Constant.INT_BOOLEAN.TRUE)
                    .findAll();
            for(DIC_HANJA_BOOK dicHanja : dicHanjaBookList) {
                VocaBookmark vocaBookmark = new VocaBookmark();
                vocaBookmark.setVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_BOOK);
                vocaBookmark.setVocaId(dicHanja.getID().intValue());
                vocaBookmarkList.add(vocaBookmark);
            }
        });
        return vocaBookmarkList;
    }
    public static List<VocaKnowAndKnowpronounce> getVocaKnowKnowpronounceList(Realm realm) {
        List<VocaKnowAndKnowpronounce> vocaKnowAndKnowpronounceList = new ArrayList<>();
        realm.executeTransaction(realm1 -> {
            List<DIC_HANJA> dicHanjaList = realm1.where(DIC_HANJA.class)
                    .notEqualTo(Constant.REALMDB.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .or()
                    .notEqualTo(Constant.REALMDB.KEY_VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .findAll();
            for(DIC_HANJA dicHanja : dicHanjaList) {
                VocaKnowAndKnowpronounce vocaKnowAndKnowpronounce = new VocaKnowAndKnowpronounce(dicHanja.getID().intValue(),
                        Constant.API_VALUE.VALUE_VOCA_TYPE_WORD,
                        dicHanja.getVOCA_KNOW().intValue(),
                        dicHanja.getVOCA_KNOWPRONOUNCE().intValue());
                vocaKnowAndKnowpronounceList.add(vocaKnowAndKnowpronounce);
            }
            List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = realm1.where(DIC_HANJA_SENTENCE.class)
                    .notEqualTo(Constant.REALMDB.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .or()
                    .notEqualTo(Constant.REALMDB.KEY_VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .findAll();
            for(DIC_HANJA_SENTENCE dicHanja : dicHanjaSentenceList) {
                VocaKnowAndKnowpronounce vocaKnowAndKnowpronounce = new VocaKnowAndKnowpronounce(dicHanja.getID().intValue(),
                        Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE,
                        dicHanja.getVOCA_KNOW().intValue(),
                        dicHanja.getVOCA_KNOWPRONOUNCE().intValue());
                vocaKnowAndKnowpronounceList.add(vocaKnowAndKnowpronounce);
            }
            List<DIC_HANJA_BOOK> dicHanjaBookList = realm1.where(DIC_HANJA_BOOK.class)
                    .notEqualTo(Constant.REALMDB.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .or()
                    .notEqualTo(Constant.REALMDB.KEY_VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .findAll();
            for(DIC_HANJA_BOOK dicHanja : dicHanjaBookList) {
                VocaKnowAndKnowpronounce vocaKnowAndKnowpronounce = new VocaKnowAndKnowpronounce(dicHanja.getID().intValue(),
                        Constant.API_VALUE.VALUE_VOCA_TYPE_BOOK,
                        dicHanja.getVOCA_KNOW().intValue(),
                        dicHanja.getVOCA_KNOWPRONOUNCE().intValue());
                vocaKnowAndKnowpronounceList.add(vocaKnowAndKnowpronounce);
            }
        });
        return vocaKnowAndKnowpronounceList;
    }

    //Dalnim
    public static void updateVocaKnowAndVocaKnowpronounceAndBookmark(Realm realm, VocaKnowAndBookmarkList vocaKnowAndBookmarkList) {
        List<VocaBookmark> vocaBookmarkList = vocaKnowAndBookmarkList.getVocaBookmarkList();
        List<VocaKnowAndKnowpronounce> vocaKnowKnowpronounceList = vocaKnowAndBookmarkList.getVocaKnowKnowpronounceList();

        updateVocaKnowAndVocaKnowpronounce(realm, vocaKnowKnowpronounceList);
        updateVocaBookmark(realm, vocaBookmarkList);
    }

    private static void updateVocaBookmark(Realm realm, List<VocaBookmark> vocaBookmarkList) {
        Map<Long, List<Integer>> mapBookmark = new HashMap<>();
        Long[] vocaIDListInWord = new Long[vocaBookmarkList.size()];
        Long[] vocaIDListInSentence = new Long[vocaBookmarkList.size()];
        Long[] vocaIDListInBook = new Long[vocaBookmarkList.size()];

        for(int i = 0; i < vocaBookmarkList.size(); i++) {
            VocaBookmark vocaBookmark = vocaBookmarkList.get(i);
            if (vocaBookmark.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) {
                vocaIDListInWord[i] = Long.valueOf(vocaBookmark.getVocaId());
            } else if (vocaBookmark.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE) {
                vocaIDListInSentence[i] = Long.valueOf(vocaBookmark.getVocaId());
            } else if (vocaBookmark.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_BOOK) {
                vocaIDListInBook[i] = Long.valueOf(vocaBookmark.getVocaId());
            }
        }
        //TODO : need to reset all bookmark first before update .
        updateVocaBookmarkForWords(realm, vocaIDListInWord);
        updateVocaBookmarkForSentences(realm, vocaIDListInSentence);
        updateVocaBookmarkForBooks(realm,vocaIDListInBook);
    }

    private static void updateVocaBookmarkForWords(Realm realm1, Long[] vocaIDList) {
        realm1.executeTransaction(realm -> {
            RealmResults<DIC_HANJA> dicHanjaList = realm.where(DIC_HANJA.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDList)
                    .findAll();
            for(DIC_HANJA dicHanja : dicHanjaList) {
                dicHanja.setBOOKMARK((long) Constant.INT_BOOLEAN.TRUE);
                realm.insertOrUpdate(dicHanja);
            }
        });
    }

    private static void updateVocaBookmarkForSentences(Realm realm1, Long[] vocaIDList) {
        realm1.executeTransaction(realm -> {
            RealmResults<DIC_HANJA_SENTENCE> dicHanjaList = realm.where(DIC_HANJA_SENTENCE.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDList)
                    .findAll();
            for(DIC_HANJA_SENTENCE dicHanja : dicHanjaList) {
                dicHanja.setBOOKMARK((long) Constant.INT_BOOLEAN.TRUE);
                realm.insertOrUpdate(dicHanja);
            }
        });
    }

    private static void updateVocaBookmarkForBooks(Realm realm1, Long[] vocaIDList) {
        realm1.executeTransaction(realm -> {
            RealmResults<DIC_HANJA_BOOK> dicHanjaList = realm.where(DIC_HANJA_BOOK.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDList)
                    .findAll();
            for(DIC_HANJA_BOOK dicHanja : dicHanjaList) {
                dicHanja.setBOOKMARK((long) Constant.INT_BOOLEAN.TRUE);
                realm.insertOrUpdate(dicHanja);
            }
        });
    }

    public static void updateVocaKnowAndVocaKnowpronounce(Realm realm, List<VocaKnowAndKnowpronounce> vocaKnowKnowpronounceList) {
        Map<Long, List<Integer>> mapWordIDAndVocaKnow = new HashMap<>();
        Map<Long, List<Integer>> mapSentenceIDAndVocaKnow = new HashMap<>();
        Map<Long, List<Integer>> mapBookIDAndVocaKnow = new HashMap<>();

        Long[] vocaIDListInWord = new Long[vocaKnowKnowpronounceList.size()];
        Long[] vocaIDListInSentence = new Long[vocaKnowKnowpronounceList.size()];
        Long[] vocaIDListInBook = new Long[vocaKnowKnowpronounceList.size()];
        for(int i = 0; i < vocaKnowKnowpronounceList.size(); i++) {
            VocaKnowAndKnowpronounce vocaKnowValue = vocaKnowKnowpronounceList.get(i);
            List<Integer> listVocaKnowAndKnowpronounce = new ArrayList<>();
            listVocaKnowAndKnowpronounce.add(vocaKnowValue.getVocaKnow());
            listVocaKnowAndKnowpronounce.add(vocaKnowValue.getVocaKnowPronounce());
            if (vocaKnowValue.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) {
                vocaIDListInWord[i] = Long.valueOf(vocaKnowValue.getVocaId());
                mapWordIDAndVocaKnow.put(vocaIDListInWord[i], listVocaKnowAndKnowpronounce);
            } else if (vocaKnowValue.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE) {
                vocaIDListInSentence[i] = Long.valueOf(vocaKnowValue.getVocaId());
                mapSentenceIDAndVocaKnow.put(vocaIDListInSentence[i], listVocaKnowAndKnowpronounce);
            } else if (vocaKnowValue.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_BOOK) {
                vocaIDListInBook[i] = Long.valueOf(vocaKnowValue.getVocaId());
                mapBookIDAndVocaKnow.put(vocaIDListInBook[i], listVocaKnowAndKnowpronounce);
            }
        }

        updateVocaKnowAndVocaKnowpronounceForWords(realm, mapWordIDAndVocaKnow, vocaIDListInWord);
        updateVocaKnowAndVocaKnowpronounceForSentences(realm, mapSentenceIDAndVocaKnow, vocaIDListInSentence);
        updateVocaKnowAndVocaKnowpronounceForBooks(realm, mapBookIDAndVocaKnow, vocaIDListInBook);

    }

    private static void updateVocaKnowAndVocaKnowpronounceForWords(Realm realm1, Map<Long, List<Integer>> mapWordIDAndVocaKnow, Long[] vocaIDListInWord) {
        realm1.executeTransaction(realm -> {
            RealmResults<DIC_HANJA> dicHanjaList = realm.where(DIC_HANJA.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDListInWord)
                    .findAll();

            for(DIC_HANJA dicHanja : dicHanjaList) {
                if (mapWordIDAndVocaKnow.containsKey(dicHanja.getID())) {
                    List<Integer> listVocaKnowAndKnowpronounce = mapWordIDAndVocaKnow.get(dicHanja.getID());
                    dicHanja.setVOCA_KNOW((long) listVocaKnowAndKnowpronounce.get(0));
                    dicHanja.setVOCA_KNOWPRONOUNCE((long) listVocaKnowAndKnowpronounce.get(1));
                    realm.insertOrUpdate(dicHanja);
                }
            }
        });
    }

    private static void updateVocaKnowAndVocaKnowpronounceForSentences(Realm realm1,Map<Long, List<Integer>> mapSentenceIDAndVocaKnow, Long[] vocaIDListInSentence) {
        realm1.executeTransaction(realm -> {
            RealmResults<DIC_HANJA_SENTENCE> dicHanjaSentenceList = realm.where(DIC_HANJA_SENTENCE.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDListInSentence)
                    .findAll();
            for(DIC_HANJA_SENTENCE dicHanjaSentence : dicHanjaSentenceList) {
                if (mapSentenceIDAndVocaKnow.containsKey(dicHanjaSentence.getID())) {
                    List<Integer> listVocaKnowAndKnowpronounce = mapSentenceIDAndVocaKnow.get(dicHanjaSentence.getID());
                    dicHanjaSentence.setVOCA_KNOW((long) listVocaKnowAndKnowpronounce.get(0));
                    dicHanjaSentence.setVOCA_KNOWPRONOUNCE((long) listVocaKnowAndKnowpronounce.get(1));
                    realm.insertOrUpdate(dicHanjaSentence);
                }
            }
        });
    }

    private static void updateVocaKnowAndVocaKnowpronounceForBooks(Realm realm1,Map<Long, List<Integer>> mapSentenceIDAndVocaKnow, Long[] vocaIDList) {
        realm1.executeTransaction(realm -> {
            RealmResults<DIC_HANJA_BOOK> dicList = realm.where(DIC_HANJA_BOOK.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDList)
                    .findAll();
            for(DIC_HANJA_BOOK dicOne : dicList) {
                if (mapSentenceIDAndVocaKnow.containsKey(dicOne.getID())) {
                    List<Integer> listVocaKnowAndKnowpronounce = mapSentenceIDAndVocaKnow.get(dicOne.getID());
                    dicOne.setVOCA_KNOW((long) listVocaKnowAndKnowpronounce.get(0));
                    dicOne.setVOCA_KNOWPRONOUNCE((long) listVocaKnowAndKnowpronounce.get(1));
                    realm.insertOrUpdate(dicOne);
                }
            }
        });
    }

    public static HanjaItem getHanjaToday(HanjaItem currentHanjaToday) {
        int limitCountOfVoca = Constant.COUNT_OF_CANDIDATE_FOR_TODAY_HANJA;
        List<DIC_HANJA> hanjaTodayCandidateList = getHanjaTodayCandidateList(limitCountOfVoca);

        return chooseOneRandomHanjaTodayFromCandidates(hanjaTodayCandidateList, currentHanjaToday);
    }

    public static List<DIC_HANJA> getHanjaTodayCandidateList(int limitCountOfVoca) {
        List<DIC_HANJA> hanjaTodayCandidateList = getHanjaTodayCandidates1st(limitCountOfVoca);
        if (hanjaTodayCandidateList.size() < limitCountOfVoca) {
            hanjaTodayCandidateList = getHanjaTodayCandidates2nd(hanjaTodayCandidateList, limitCountOfVoca);
        }
        if (hanjaTodayCandidateList.size() < limitCountOfVoca) {
            hanjaTodayCandidateList = getHanjaTodayCandidates3rd(hanjaTodayCandidateList, limitCountOfVoca);
        }
        if (hanjaTodayCandidateList.size() < limitCountOfVoca) {
            hanjaTodayCandidateList = getHanjaTodayCandidates4th(hanjaTodayCandidateList, limitCountOfVoca);
        }
        return hanjaTodayCandidateList;
    }

    private static HanjaItem chooseOneRandomHanjaTodayFromCandidates(List<DIC_HANJA> hanjaList, HanjaItem currentHanjaToday) {
        HanjaItem hanjaToday = new DIC_HANJA();
        if (hanjaList.size() > 0) {
            Random random = new Random();
            hanjaToday = hanjaList.get(random.nextInt(hanjaList.size()));
            if (currentHanjaToday == null)
                return hanjaToday;

            if ((hanjaToday.getHI_VOCA_TYPE() != currentHanjaToday.getHI_VOCA_TYPE()) || (hanjaToday.getHI_ID() != currentHanjaToday.getHI_ID()))
                return hanjaToday;


            for (int i = 0; i<hanjaList.size(); i++) {
                hanjaToday = hanjaList.get(i);
                if ((hanjaToday.getHI_VOCA_TYPE() != currentHanjaToday.getHI_VOCA_TYPE()) || (hanjaToday.getHI_ID() != currentHanjaToday.getHI_ID()))
                    break;
            }
        }
        return hanjaToday;
    }

//    private static List<DIC_HANJA> get100VocasOrderByVocaLevel(List<DIC_HANJA> hanjaList) {
//        return hanjaList.stream().sorted(Comparator.comparing(DIC_HANJA::getVOCA_LEVEL)).collect(Collectors.toList());
//    }

    public static List<DIC_HANJA> removeSimplifiedChineseInList(List<DIC_HANJA> hanjas) {
        if (isShowSimplifiedChinese())
            return hanjas;

        Iterator<DIC_HANJA> it = hanjas.iterator();
        while (it.hasNext()) {
            HanjaItem hanjaItem = it.next();
            if (hanjaItem instanceof DIC_HANJA) {
                DIC_HANJA dicHanja = (DIC_HANJA) hanjaItem;
                if (isSimplifiedChinese(dicHanja)) {
                    it.remove();
                }
            }
        }


        return hanjas;
    }
    private static List<DIC_HANJA> getHanjaTodayCandidates1st(int limitCountOfVoca) {
        List<DIC_HANJA> hanjaList = new ArrayList<>();
        executeRealmTransaction(realm -> {
            RealmResults<DIC_HANJA> results = realm.where(DIC_HANJA.class)
                    .lessThan(Constant.REALMDB.KEY_VOCA_LEVEL, Constant.VOCA_LEVEL_INDIC_MAX)
                    .lessThan(Constant.REALMDB.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                    .greaterThan(Constant.REALMDB.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .sort(Constant.REALMDB.KEY_VOCA_LEVEL)
                    .limit(limitCountOfVoca)
                    .findAll();
            hanjaList.addAll(realm.copyFromRealm(results));
        });
        return removeSimplifiedChineseInList(hanjaList);
    }

    private static List<DIC_HANJA> getHanjaTodayCandidates2nd(List<DIC_HANJA> hanjaList, int limitCountOfVoca) {
        executeRealmTransaction(realm -> {
            RealmResults<DIC_HANJA> results = realm.where(DIC_HANJA.class)
                    .lessThan(Constant.REALMDB.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                    .greaterThan(Constant.REALMDB.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .sort(Constant.REALMDB.KEY_VOCA_LEVEL)
                    .limit(limitCountOfVoca)
                    .findAll();
            hanjaList.addAll(realm.copyFromRealm(results));
        });
        return removeSimplifiedChineseInList(hanjaList);
    }

    private static List<DIC_HANJA> getHanjaTodayCandidates3rd(List<DIC_HANJA> hanjaList, int limitCountOfVoca) {
        executeRealmTransaction(realm -> {
            RealmResults<DIC_HANJA> results = realm.where(DIC_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)
                    .sort(Constant.REALMDB.KEY_VOCA_LEVEL)
                    .limit(limitCountOfVoca)
                    .findAll();
            hanjaList.addAll(realm.copyFromRealm(results));
        });
        return removeSimplifiedChineseInList(hanjaList);
    }

    private static List<DIC_HANJA> getHanjaTodayCandidates4th(List<DIC_HANJA> hanjaList, int limitCountOfVoca) {
        executeRealmTransaction(realm -> {
            RealmResults<DIC_HANJA> results = realm.where(DIC_HANJA.class)
                    .sort(Constant.REALMDB.KEY_VOCA_LEVEL)
                    .limit(limitCountOfVoca)
                    .findAll();
            hanjaList.addAll(realm.copyFromRealm(results));
        });
        return removeSimplifiedChineseInList(hanjaList);
    }


    public static void updateHanjaSentence(DIC_HANJA_SENTENCE dicHanjaSentence) {
        executeRealmTransaction(realm -> {
            realm.insertOrUpdate(dicHanjaSentence);
        });
    }

    public static void updateHanjaBook(DIC_HANJA_BOOK dicHanjaBook) {
        executeRealmTransaction(realm -> {
            realm.insertOrUpdate(dicHanjaBook);
        });
    }

    public static void addHanjaSentence(DIC_HANJA_SENTENCE dicHanjaSentence) {
        executeRealmTransaction(realm -> {
            realm.insertOrUpdate(dicHanjaSentence);
        });
    }

    public static void updateHanjaWord(DIC_HANJA dicHanja) {
        executeRealmTransaction(realm -> {
            realm.insertOrUpdate(dicHanja);
        });
    }

    public static List<VOCABOOKS_HANJA> getWorkbooksInfo(VOCABOOKS_HANJA parentBook, boolean isGetCountOfVocaKnow, int vocabooksUsedByUserType) {
        List<VOCABOOKS_HANJA> vocabooksHanjaList = getSubWorkbookListFromBookID(parentBook == null ? 0 : parentBook.getID(), vocabooksUsedByUserType);
        if (isGetCountOfVocaKnow)
            getVocaKnowOfWorkbooks(vocabooksHanjaList);
        return vocabooksHanjaList;
    }

    private static void getVocaKnowOfWorkbooks(List<VOCABOOKS_HANJA> vocabooksHanjaList) {
        for (VOCABOOKS_HANJA vocabooksHanja : vocabooksHanjaList) {
            List<VOCABOOK_HANJA> vocabookHanjaList = Voca.getVocabookHanjaListByID(vocabooksHanja.getID());

            Long[] vocaIDArray = vocabookHanjaList.stream()
                    .filter(e -> e.getVOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD)
                    .map(e -> e.getVOCA_ID())
                    .toArray(Long[]::new);
            List<HanjaItem> hanjaItemListUnsorted = Voca.getHanjaListInDicHanjaByVocaIDList(vocaIDArray);
            Long countOfKnownWords = hanjaItemListUnsorted.stream().
                    filter(e -> e.getHI_VOCA_KNOW() >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                    .count();
            vocabooksHanja.setCOUNT_OF_VOCA_KNOW(countOfKnownWords);
        }

    }

//    @NotNull
//    private static List<VOCABOOKS_HANJA> getWorkbooksFromParentbook(VOCABOOKS_HANJA parentBook) {
//        List<VOCABOOKS_HANJA> books = new ArrayList<>();
//        BaseVoca.executeRealmTransaction(realm -> {
//            RealmResults<VOCABOOKS_HANJA> realmResults = realm.where(VOCABOOKS_HANJA.class)
//                    .equalTo(Constant.REALMDB.KEY_PARENT_ID, parentBook == null ? 0 : parentBook.getID())
//                    .equalTo(Constant.REALMDB.KEY_USED, 1)
//                    .sort(Constant.REALMDB.KEY_DISP_ORDER)
//                    .findAll();
//            books.addAll(realm.copyFromRealm(realmResults));
//        });
//        return books;
//    }

    @NotNull
    public static List<VOCABOOKS_HANJA> getSubWorkbookListFromBookID(long parentID, int vocabooksUsedByUserType) {
        List<VOCABOOKS_HANJA> books = new ArrayList<>();
        BaseVoca.executeRealmTransaction(realm -> {
            RealmResults<VOCABOOKS_HANJA> realmResults = realm.where(VOCABOOKS_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_PARENT_ID, parentID)
                    .greaterThanOrEqualTo(Constant.REALMDB.KEY_USED, vocabooksUsedByUserType)
                    .sort(Constant.REALMDB.KEY_DISP_ORDER)
                    .findAll();
            books.addAll(realm.copyFromRealm(realmResults));
        });
        return books;
    }

    @NotNull
    public static VOCABOOKS_HANJA getVocabooksHanjaByBookId(long bookId) {
        List<VOCABOOKS_HANJA> books = new ArrayList<>();
        BaseVoca.executeRealmTransaction(realm -> {
            RealmResults<VOCABOOKS_HANJA> realmResults = realm.where(VOCABOOKS_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_ID, bookId)
                    .findAll();
            books.addAll(realm.copyFromRealm(realmResults));
        });

        if (books.size() > 0) {
            return books.get(0);
        }
        return null;
    }

    public static String getVocabooksHanjaNameByBookId(long bookId, Context context) {
        String name = "";
        VOCABOOKS_HANJA vocabooksHanja = getVocabooksHanjaByBookId(bookId);
        if (vocabooksHanja != null) {
            name = vocabooksHanja.getName(context);
        }
        return name;
    }

    public static  List<HanjaItem> getSortedHanjaItemsByDispOrder(List<VOCABOOK_HANJA> vocabookHanjaList) {
        Long[] hanjaWordIDArray = new Long[vocabookHanjaList.size()];
        Long[] hanjaSentenceIDArray = new Long[vocabookHanjaList.size()];
        Long[] vocaIDArray = new Long[vocabookHanjaList.size()];
        Long[] vocaTypeArray = new Long[vocabookHanjaList.size()];
        for (int i = 0; i < vocabookHanjaList.size(); i++) {
            VOCABOOK_HANJA vocabookHanja = vocabookHanjaList.get(i);
            if (vocabookHanja.getVOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) {
                hanjaWordIDArray[i] = vocabookHanja.getVOCA_ID();
            } else if (vocabookHanja.getVOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE) {
                hanjaSentenceIDArray[i] = vocabookHanja.getVOCA_ID();
            }
            vocaIDArray[i] = vocabookHanja.getVOCA_ID();
            vocaTypeArray[i] = vocabookHanja.getVOCA_TYPE();
        }
//        Long[] vocaIDArray = vocabookHanjaList.stream().map(e -> e.getVOCA_ID()).toArray(Long[]::new); //.collect(Collectors.toList());
//        Long[] vocaTypeArray = vocabookHanjaList.stream().map(e -> e.getVOCA_TYPE()).toArray(Long[]::new) ;//.collect(Collectors.toList());
        List<HanjaItem> hanjaItemListUnsorted = Voca.getHanjaListInDicHanjaByVocaIDList(hanjaWordIDArray);
        hanjaItemListUnsorted.addAll(Voca.getHanjaListInDicHnajaSentenceByVocaIDList(hanjaSentenceIDArray));
        return Voca.sortHanjaListByDispOrderOfVocaIDList(hanjaItemListUnsorted, vocaTypeArray, vocaIDArray);
    }

//    public static void updateVocaKnow(HanjaItem hanjaItem, int vocaKnow, int vocaKnowPronounce) {
//        Long[] idList = new Long[] {hanjaItem.getHI_ID()};
//        if (BaseVocaKnow.isVocaTypeWord(hanjaItem)) {
//            updateMultipleVocaKnowWord(idList, vocaKnow, vocaKnowPronounce);
//        } else if (BaseVocaKnow.isVocaTypeSentence(hanjaItem)) {
//            updateMultipleVocaKnowSentence(idList, vocaKnow, vocaKnowPronounce);
//        }
//    }

    public static void updateVocaKnow(IVocaBasicItem item, int vocaKnow, int vocaKnowPronounce) {
        Long[] idList = new Long[] {Long.valueOf(item.getVIVocaId())};
        if (BaseVocaKnow.isVocaTypeWord(item)) {
            updateMultipleVocaKnowWord(idList, vocaKnow, vocaKnowPronounce);
        } else if (BaseVocaKnow.isVocaTypeSentence(item)) {
            updateMultipleVocaKnowSentence(idList, vocaKnow, vocaKnowPronounce);
        }
    }

    public static void updateMultipleVocaKnow(List<HanjaItem> hanjaItemList, int vocaKnow) {
        Long[] hanjaWordIDList = hanjaItemList.stream()
                .filter(e -> e.getHI_VOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD)
                .map(e -> e.getHI_ID())
                .toArray(Long[]::new);
        Long[] hanjaSentenceIDList = hanjaItemList.stream()
                .filter(e -> e.getHI_VOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE)
                .map(e -> e.getHI_ID())
                .toArray(Long[]::new);

        updateMultipleVocaKnowWord(hanjaWordIDList, vocaKnow, vocaKnow);
        updateMultipleVocaKnowSentence(hanjaSentenceIDList, vocaKnow, vocaKnow);
    }

    private static void updateMultipleVocaKnowWord(Long[] vocaIDList, long vocaKnow, long vocaKnowPronounce) {
        if (vocaIDList.length == 0)
            return;

        executeRealmTransaction(realm -> {
            RealmResults<DIC_HANJA> dicHanjaList = realm.where(DIC_HANJA.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDList)
                    .findAll();
            for(DIC_HANJA dicHanja : dicHanjaList) {
                dicHanja.setVOCA_KNOW(vocaKnow);
                dicHanja.setVOCA_KNOWPRONOUNCE(vocaKnowPronounce);
//                realm.insertOrUpdate(dicHanja); //Don't need to call this, because the RealmResults is updated in the realm transaction, it's also saved in the RealmDB too.
            }
        });
    }

    private static void updateMultipleVocaKnowSentence(Long[] vocaIDList, long vocaKnow, long vocaKnowPronounce) {
        if (vocaIDList.length == 0)
            return;

        executeRealmTransaction(realm -> {
            RealmResults<DIC_HANJA_SENTENCE> dicHanjaList = realm.where(DIC_HANJA_SENTENCE.class)
                    .in(Constant.REALMDB.KEY_ID, vocaIDList)
                    .findAll();
            for(DIC_HANJA_SENTENCE dicHanja : dicHanjaList) {
                dicHanja.setVOCA_KNOW(vocaKnow);
                dicHanja.setVOCA_KNOWPRONOUNCE(vocaKnowPronounce);
//                realm.insertOrUpdate(dicHanja);
            }
        });
    }

    public static boolean isShowSimplifiedChinese() {
        return SharedPreferencesDB.getInstance(BaseApplication.getInstance().getApplicationContext()).getShowSimplifiedChinese();
    }

    public static boolean isShowBeyondLevelHanja() {
        return SharedPreferencesDB.getInstance(BaseApplication.getInstance().getApplicationContext()).getShowBeyondLevelHanja();
    }

    public static boolean isSimplifiedChinese(DIC_HANJA dicHanja) {
        if (dicHanja == null)
            return false;

        String strSimplifiedChinese = "간체자";
        if (dicHanja.getHANJA_TYPE().trim().equals(strSimplifiedChinese)) {
            return true;
        }
        return false;
    }

    public static boolean isBeyondLevelHanja(DIC_HANJA dicHanja) {
        if (dicHanja == null)
            return false;

        if ((dicHanja.getVOCA_LEVEL() < Constant.VOCA_LEVEL_INDIC_MAX)
            || (!Utils.isEmpty(dicHanja.getCOMMON_USE_KOREA()))
                || (!Utils.isEmpty(dicHanja.getCOMMON_USE_KOREA()))
                || (!Utils.isEmpty(dicHanja.getLEVEL_KOREA_TYPE_1()))
                || (!Utils.isEmpty(dicHanja.getLEVEL_KOREA_TYPE_2()))
                || (!Utils.isEmpty(dicHanja.getLEVEL_KOREA_TYPE_3()))
        ){
            return false;
        }
        return true;
    }

    public static List<DIC_HANJA> prepareHanjaListToDisplay(Realm realm, List<DIC_HANJA> hanjas, DIC_HANJA dicHanjaSelected) {
        boolean isShowSimplifiedChinese = isShowSimplifiedChinese();
        boolean isShowBeyondLevelHanja = isShowBeyondLevelHanja();
        Iterator<DIC_HANJA> it = hanjas.iterator();
        while (it.hasNext()) {
            HanjaItem hanjaItem = it.next();
            if (hanjaItem instanceof DIC_HANJA) {
                DIC_HANJA dicHanja = (DIC_HANJA) hanjaItem;
                if (!isShowSimplifiedChinese && isSimplifiedChinese(dicHanja) && !isSameVoca(dicHanjaSelected, dicHanja)) {
                    it.remove();
                } else if (!isShowBeyondLevelHanja && isBeyondLevelHanja(dicHanja) && !isSameVoca(dicHanjaSelected, dicHanja)) {
                    it.remove();
                } else {
                    if ((dicHanja.getPRONOUNCE1_FIRST().equals("")) || (dicHanja.getMEANING1().equals(""))) {
                        Voca.searchAlternativeMeaningPronounceFirst(realm, dicHanja);
                        // Don't display if it has no pronounce or meaning
//                    if ((dicHanja.getPRONOUNCE1_FIRST().equals("")) || (dicHanja.getMEANING1().equals(""))) {
//                        it.remove();
//                    }
                    }
                }
            }
        }

        Collections.sort(hanjas, Comparator.nullsLast(Comparator.comparingLong(HanjaItem::getHI_VOCA_LEVEL)
                .thenComparing(HanjaItem::getHI_PRONOUNCE1_FIRST)
                .thenComparing(HanjaItem::getHI_MEANING1)));

        return hanjas;
    }


//    public static void openCopyDialog(Context context, IVocaBasicItem vocaBasicItem) {
//        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_word_pronounce);
//        if (VocaKnow.isVocaTypeWord(vocaBasicItem)) {
//            displayOptions = Arrays.stream(displayOptions).limit(displayOptions.length - 2).toArray(String[]::new);
//        }
//        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
//        singleChoiceDialog.showWrapContentHeight(
//            R.string.menu_to_copy,
//            displayOptions,
//            0,
//            R.string.ok,
//            R.string.cancel,
//            new OnClickDialogListener() {
//                @Override
//                public void onClick(View view, Object object) {
//                    final int which = (int) object;
//                    String strToCopy = "";
//                    switch (which) {
//                        case 0:
//                            strToCopy = vocaBasicItem.getVIVoca();
//                            break;
//                        case 1:
//                            strToCopy = "(" + vocaBasicItem.getVIVoca() + ")";
//                            break;
//                        case 2:
//                            strToCopy = vocaBasicItem.getVIVoca() + "(" + vocaBasicItem.getVIPronounce() + ")";
//                            break;
//                        case 3:
//                            strToCopy = vocaBasicItem.getVIPronounce() + "(" + vocaBasicItem.getVIVoca() + ")";
//                            break;
//                        case 4:
//                        case 5:
//                            String meaningWithNewLine = Utils.isEmpty(vocaBasicItem.getVIMeaning()) ? "" : "\n" + vocaBasicItem.getVIMeaning();
//                            if (which == 4) {
//                                strToCopy = vocaBasicItem.getVIVoca() + "(" + vocaBasicItem.getVIPronounce() + ")" + meaningWithNewLine;
//                            } else {
//                                strToCopy = vocaBasicItem.getVIPronounce() + "(" + vocaBasicItem.getVIVoca() + ")" + meaningWithNewLine;
//                            }
//                            break;
//                    }
//
//                    String messageInToastToShow = getMessageInToastToShow(strToCopy);
//                    Utils.copyToClipboard(context, strToCopy, context.getString(R.string.copied) + " " + messageInToastToShow);
//                }
//
//
//
//                @Override
//                public void onDismiss(View view, Object object) {
//
//                }
//            });
//    }
//
//    public static String getMessageInToastToShow(String strToCopy) {
//        int maxStringLength = 20;
//        return StringUtils.getSubstringWithMoreText(strToCopy, maxStringLength);
////                    String messageInToastToShow = strToCopy;
////
////                    if (messageInToastToShow.length() >= maxStringLength) {
////                        messageInToastToShow = messageInToastToShow.substring(0, maxStringLength) + "...";
////                    }
////                    return messageInToastToShow;
//    }

//    public static boolean isVocaWord(HanjaItem voca) {
//        if (voca == null)
//            return false;
//        return voca.getHI_VOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
//    }
//
//    public static boolean isVocaSentence(HanjaItem voca) {
//        if (voca == null)
//            return false;
//        return voca.getHI_VOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE;
//    }
//
//    public static boolean isVocaBook(HanjaItem voca) {
//        if (voca == null)
//            return false;
//        return voca.getHI_VOCA_TYPE() == Constant.API_VALUE.VALUE_VOCA_TYPE_BOOK;
//    }
//
//    public static boolean isSameVoca(HanjaItem voca1, HanjaItem voca2) {
//        if ((voca1 == null) || (voca2 == null))
//            return false;
//        return (voca1.getHI_VOCA_TYPE() == voca2.getHI_VOCA_TYPE()) && voca1.getHI_ID().equals(voca2.getHI_ID());
//    }
//
//    public static boolean isSameVoca(DIC_HANJA voca1, DIC_HANJA voca2) {
//        if ((voca1 == null) || (voca2 == null))
//            return false;
//        return (voca1.getHI_VOCA_TYPE() == voca2.getHI_VOCA_TYPE()) && voca1.getHI_ID().equals(voca2.getHI_ID());
//    }

    public static void showToastIfNoHanja(Context context, int hanjaSizeBefore, int hanjaSizeAfter) {
        if ((hanjaSizeBefore > 0) && (hanjaSizeAfter == 0))
            ToastUtil.getInstance(context).show(R.string.msg_no_hanja_in_list);
    }

    public static List<IVocaFullPlayTTSItem> convertHanjaItemListToIVocaFullPlayTTSItemList(List<HanjaItem> list, int limit) {
        return list.stream()
                .filter(item -> item instanceof IVocaFullPlayTTSItem)
                .map(item -> (IVocaFullPlayTTSItem) item)
                .limit(limit)
                .collect(Collectors.toList());
    }

        public static List<IVocaFullPlayTTSItem> convertDicHanjaListToIVocaFullPlayTTSItemList(List<DIC_HANJA> list, int limit) {
        return list.stream()
                .filter(item -> item instanceof IVocaFullPlayTTSItem)
                .map(item -> (IVocaFullPlayTTSItem) item)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
