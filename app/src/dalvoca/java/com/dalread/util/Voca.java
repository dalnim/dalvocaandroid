package com.dalread.util;

import android.content.Context;
import android.util.Pair;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.model.GRAMMAR;
import com.dalread.model.Question;
import com.dalread.model.READING;
import com.dalread.model.SERVER_VOCABOOKS;
import com.dalread.model.VocaStudy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

public class Voca extends BaseVoca {

    public static ArrayList<Question> createQuestions(Context context) {
        ArrayList<Question> questions = new ArrayList<>();
        final ArrayList<VocaStudy> vocaStudies = new ArrayList<>();
        executeRealmTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                RealmResults<VocaStudy> results = realm.where(VocaStudy.class)
                        .findAll();
                vocaStudies.addAll(realm.copyFromRealm(results));
            }
        });
        int wordCount = vocaStudies.size();
        if (wordCount >= Constant.ANSWER_SIZE) {
            Random random = new Random();
            Collections.shuffle(vocaStudies, random);
            int questionCount = wordCount < Constant.QUESTION_SIZE ? wordCount : Constant.QUESTION_SIZE;
            for (int i = 0; i < questionCount; i++) {
                VocaStudy vocaStudy = vocaStudies.get(i);
                Question question = new Question();
                int questionType = random.nextInt(Constant.QUESTION_TYPE_COUNT);
                question.setQuestion(questionType == Constant.QUESTION_TYPE_NAME ? getVocaDisplay(vocaStudy) : vocaStudy.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
                question.getAnswers().add(new Pair<>(true, questionType == Constant.QUESTION_TYPE_NAME ? vocaStudy.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)) : getVocaDisplay(vocaStudy)));
                ArrayList<Integer> answerPositions = new ArrayList<>();
                answerPositions.add(i);
                for (int j = 1; j < Constant.ANSWER_SIZE; j++) {
                    int pos;
                    do {
                        pos = random.nextInt(wordCount);
                    } while (answerPositions.contains(pos));
                    VocaStudy w = vocaStudies.get(pos);
                    question.getAnswers().add(new Pair<>(false, questionType == Constant.QUESTION_TYPE_NAME ? w.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)) : getVocaDisplay(w)));
                    answerPositions.add(pos);
                }
                Collections.shuffle(question.getAnswers());
                questions.add(question);
            }
        }
        return questions;
    }

    public static void splitVocaStudyChinese(String voca, List<String> splitList, List<Integer> posList) {
        Set<Character.UnicodeBlock> chineseUnicodeBlocks = getChineseUnicodeBlocks();
        char[] vocaArray = voca.toCharArray();
        int count = vocaArray.length;
        for (int i = 0; i < count; i++) {
            char c = vocaArray[i];
            if (chineseUnicodeBlocks.contains(Character.UnicodeBlock.of(c))) {
                splitList.add(String.valueOf(c));
                posList.add(i);
            }
        }
    }

    public static String getBookName(final String id, final EnumLanguage enumDisplayLanguage) {
        final String[] name = new String[1];
        executeRealmTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                SERVER_VOCABOOKS book = realm.where(SERVER_VOCABOOKS.class)
                        .equalTo("ID", id)
                        .findFirst();
                if (book != null) {
                    name[0] = book.getName(enumDisplayLanguage);
                }
            }
        });
        return name[0];
    }

    public static String getBookNameCombined(final String id, final EnumLanguage enumDisplayLanguage, final EnumLanguage enumStudyLanguage) {
        final String[] name = new String[1];
        executeRealmTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                SERVER_VOCABOOKS book = realm.where(SERVER_VOCABOOKS.class)
                        .equalTo("ID", id)
                        .findFirst();
                if (book != null) {
                    String n = "";
                    SERVER_VOCABOOKS parentBook = realm.where(SERVER_VOCABOOKS.class)
                            .equalTo("ID", book.getPARENT_ID())
                            .findFirst();
                    if (parentBook != null) {
                        n += parentBook.getName(enumDisplayLanguage);
                        if (enumDisplayLanguage != enumStudyLanguage) {
                            n += " (" + parentBook.getName(enumStudyLanguage) + ")";
                        }
                        n += " : ";
                    }
                    n += book.getName(enumDisplayLanguage);
                    if (enumDisplayLanguage != enumStudyLanguage) {
                        n += " (" + book.getName(enumStudyLanguage) + ")";
                    }
                    name[0] = n;
                }
            }
        });
        return name[0];
    }

    public static ArrayList<Integer> getParentBookIds(int bookId) {
        ArrayList<Integer> parentBookIds = new ArrayList<>();
        parentBookIds.add(bookId);
        executeRealmTransaction(realm -> {
            SERVER_VOCABOOKS book = realm.where(SERVER_VOCABOOKS.class)
                    .equalTo("ID", String.valueOf(bookId))
                    .findFirst();
            if (book != null) {
                String strParentId = book.getPARENT_ID();
                int parentId = Utils.parseInt(strParentId);
                while (parentId > 0) {
                    parentBookIds.add(parentId);
                    SERVER_VOCABOOKS parentBook = realm.where(SERVER_VOCABOOKS.class)
                            .equalTo("ID", strParentId)
                            .findFirst();
                    if (parentBook == null) {
                        parentId = 0;
                    } else {
                        strParentId = parentBook.getPARENT_ID();
                        parentId = Utils.parseInt(strParentId);
                    }
                }
            }
        });
        return parentBookIds;
    }

    public static int getLessonBackgroundColor(long startTimeSeconds, long finishTimeSeconds, boolean isStarted, boolean isFinished) {
        long now = System.currentTimeMillis();
        long startDateMillis = DateUtils.secondsToMillis(startTimeSeconds);
        long finishDateMillis = DateUtils.secondsToMillis(finishTimeSeconds);
        if (now > finishDateMillis || isFinished)
            return R.color.colorBackgroundMenu; // past lessons
        if (now > startDateMillis)
            return isStarted
                    ? R.color.colorHeader // studying
                    : R.color.colorRed; // not started on time
        return R.color.colorWhite; // normal case
    }

    public static String[] getDuplicateLessonDays() {
        List<String> days = new ArrayList<>();
        for (int i = Constant.DUPLICATE_LESSON_DAY_MIN; i <= Constant.DUPLICATE_LESSON_DAY_MAX; i++) {
            days.add(String.valueOf(i));
        }
        return days.toArray(new String[0]);
    }

    public static GRAMMAR getGrammar(int grammarId, EnumLanguage enumDisplayLanguage) {
        GRAMMAR[] grammars = new GRAMMAR[1];
        executeRealmTransaction(realm -> {
            GRAMMAR grammar = realm.copyFromRealm(realm.where(GRAMMAR.class)
                    .equalTo("ID", grammarId)
                    .findFirst());
            grammar.setDISPLAY_LANG(enumDisplayLanguage.getIdApi());
            grammars[0] = grammar;
        });
        return grammars[0];
    }

    public static READING getReading(int readingId, EnumLanguage enumDisplayLanguage) {
        READING[] readings = new READING[1];
        executeRealmTransaction(realm -> {
            READING reading = realm.copyFromRealm(realm.where(READING.class)
                    .equalTo("ID", readingId)
                    .findFirst());
            reading.setDISPLAY_LANG(enumDisplayLanguage.getIdApi());
            readings[0] = reading;
        });
        return readings[0];
    }
}
