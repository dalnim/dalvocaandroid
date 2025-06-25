package com.dalread.model;

import android.content.Context;

import com.dalread.R;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VocaKnowGroupSelected<T extends IVocaBasicItem> {
    public enum Type {
        AMKI_GRADE,
        ALPAHBET,
        FREQUENCY,
        APPEARANCE,
    }
    private Context context;
    public Type type;
    List<Object> amkiGradeList = new ArrayList<>();
    WordListHeaderModel headerModelGrade1 = new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1, "");
    WordListHeaderModel headerModelGrade2 = new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2, "");
    WordListHeaderModel headerModelNotRated = new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED, "");
    WordListHeaderModel headerModelUnknown = new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN, "");
    WordListHeaderModel headerModelKnown = new WordListHeaderModel(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, "");

    List<T> allVocaList = new ArrayList<>();
    List<T> amkiGrade1List = new ArrayList<>();
    List<T> amkiGrade2List = new ArrayList<>();
    List<T> notRatedList = new ArrayList<>();
    List<T> unknownList = new ArrayList<>();
    List<T> knownList = new ArrayList<>();

    public VocaKnowGroupSelected(Context context, Type type) {
        this.context = context;
        this.type = type;
    }

    public void addItem(T item) {
         if (BaseVocaKnow.isAmkiGrade1(item)) {
            amkiGrade1List.add(item);
        } else if (BaseVocaKnow.isAmkiGrade2(item)) {
             amkiGrade2List.add(item);
         } else if (BaseVocaKnow.isNotRated(item)) {
            notRatedList.add(item);
        } else if (BaseVocaKnow.isUnknown(item)) {
            unknownList.add(item);
        } else {
            knownList.add(item);
        }
    }

    public void populateAllList() {
        if (amkiGrade1List.size() > 0) {
            headerModelGrade1.setSize(amkiGrade1List.size());
            headerModelGrade1.setName(context.getString(R.string.format_size_primary_targets, amkiGrade1List.size()));
            amkiGradeList.add(headerModelGrade1);
            amkiGradeList.addAll(amkiGrade1List);
            allVocaList.addAll(amkiGrade1List);
        }
        if (amkiGrade2List.size() > 0) {
            headerModelGrade2.setSize(amkiGrade2List.size());
            headerModelGrade2.setName(context.getString(R.string.format_size_secondary_targets, amkiGrade2List.size()));
            amkiGradeList.add(headerModelGrade2);
            amkiGradeList.addAll(amkiGrade2List);
            allVocaList.addAll(amkiGrade2List);
        }
        if (notRatedList.size() > 0) {
            headerModelNotRated.setSize(notRatedList.size());
            headerModelNotRated.setName(context.getString(R.string.format_size_not_determined, notRatedList.size()));
            amkiGradeList.add(headerModelNotRated);
            amkiGradeList.addAll(notRatedList);
            allVocaList.addAll(notRatedList);
        }
        if (unknownList.size() > 0) {
            headerModelUnknown.setSize(unknownList.size());
            headerModelUnknown.setName(context.getString(R.string.format_size_unknown_phrases, unknownList.size()));
            amkiGradeList.add(headerModelUnknown);
            amkiGradeList.addAll(unknownList);
            allVocaList.addAll(unknownList);
        }
        if (knownList.size() > 0) {
            headerModelKnown.setSize(knownList.size());
            headerModelKnown.setName(context.getString(R.string.format_size_known_phrases, knownList.size()));
            amkiGradeList.add(headerModelKnown);
            amkiGradeList.addAll(knownList);
            allVocaList.addAll(knownList);
        }
    }

    public boolean isTypeAmkiGrade() {
        return this.type == Type.AMKI_GRADE;
    }

    public boolean isTypeAlphabet() {
        return this.type == Type.ALPAHBET;
    }

    private List<T> sortAllVocaList(Type type) {
        List<T> sortedList = new ArrayList<>(allVocaList);
        switch (type) {
            case ALPAHBET:
                Collections.sort(sortedList, new Comparator<T>() {
                    @Override
                    public int compare(T item1, T item2) {
                        // Assuming there's a getName() method in your IVocaBasicItem interface
                        return item1.getVIVoca().compareToIgnoreCase(item2.getVIVoca());
                    }
                });
                break;

        }
        return sortedList;
    }

    public List<T> getVocaListByAlphabetOrder() {
        return sortAllVocaList(Type.ALPAHBET);
    }

    public List<Object> getAmkiGradeList() {
        return amkiGradeList;
    }

    public void setAmkiGradeList(List<Object> amkiGradeList) {
        this.amkiGradeList = amkiGradeList;
    }

    public WordListHeaderModel getHeaderModelGrade1() {
        return headerModelGrade1;
    }

    public void setHeaderModelGrade1(WordListHeaderModel headerModelGrade1) {
        this.headerModelGrade1 = headerModelGrade1;
    }

    public WordListHeaderModel getHeaderModelGrade2() {
        return headerModelGrade2;
    }

    public void setHeaderModelGrade2(WordListHeaderModel headerModelGrade2) {
        this.headerModelGrade2 = headerModelGrade2;
    }

    public WordListHeaderModel getHeaderModelNotRated() {
        return headerModelNotRated;
    }

    public void setHeaderModelNotRated(WordListHeaderModel headerModelNotRated) {
        this.headerModelNotRated = headerModelNotRated;
    }

    public WordListHeaderModel getHeaderModelUnknown() {
        return headerModelUnknown;
    }

    public void setHeaderModelUnknown(WordListHeaderModel headerModelUnknown) {
        this.headerModelUnknown = headerModelUnknown;
    }

    public WordListHeaderModel getHeaderModelKnown() {
        return headerModelKnown;
    }

    public void setHeaderModelKnown(WordListHeaderModel headerModelKnown) {
        this.headerModelKnown = headerModelKnown;
    }

    public List<T> getAllVocaList() {
        return allVocaList;
    }

    public void setAllVocaList(List<T> allVocaList) {
        this.allVocaList = allVocaList;
    }

    public List<T> getAmkiGrade1List() {
        return amkiGrade1List;
    }

    public void setAmkiGrade1List(List<T> amkiGrade1List) {
        this.amkiGrade1List = amkiGrade1List;
    }

    public List<T> getAmkiGrade2List() {
        return amkiGrade2List;
    }

    public void setAmkiGrade2List(List<T> amkiGrade2List) {
        this.amkiGrade2List = amkiGrade2List;
    }

    public List<T> getNotRatedList() {
        return notRatedList;
    }

    public void setNotRatedList(List<T> notRatedList) {
        this.notRatedList = notRatedList;
    }

    public List<T> getUnknownList() {
        return unknownList;
    }

    public void setUnknownList(List<T> unknownList) {
        this.unknownList = unknownList;
    }

    public List<T> getKnownList() {
        return knownList;
    }

    public void setKnownList(List<T> knownList) {
        this.knownList = knownList;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
