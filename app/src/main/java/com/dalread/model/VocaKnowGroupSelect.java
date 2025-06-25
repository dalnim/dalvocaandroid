package com.dalread.model;

import com.dalread.util.Constant;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VocaKnowGroupSelect implements Serializable {

    private boolean notRated;
    private boolean amki1st;
    private boolean amki2nd;
    private boolean unknown;
    private boolean known;
    private boolean difficultPronunciation;
    private boolean bookmarked;

    private Set<Integer> selectedItems = new HashSet<>();
    public VocaKnowGroupSelect() {

    }

    public VocaKnowGroupSelect(boolean notRated, boolean amki1st, boolean amki2nd, boolean unknown, boolean known, boolean difficultPronunciation, boolean bookmarked) {
        this.notRated = notRated;
        this.amki1st = amki1st;
        this.amki2nd = amki2nd;
        this.unknown = unknown;
        this.known = known;
        this.difficultPronunciation = difficultPronunciation;
        this.bookmarked = bookmarked;
    }

    public void setValueToOpenBookmarkedItemsOnly() {
        this.notRated = false;
        this.amki1st = false;
        this.amki2nd = false;
        this.unknown = false;
        this.known = false;
        this.difficultPronunciation = false;
        this.bookmarked = true;
    }

    public void setValueToOpenAllItems() {
        this.notRated = true;
        this.amki1st = true;
        this.amki2nd = true;
        this.unknown = true;
        this.known = true;
        this.difficultPronunciation = true;
        this.bookmarked = true;
    }

    public boolean isNotRated() {
        return notRated;
    }

    public void setNotRated(boolean notRated) {
        this.notRated = notRated;
    }

    public boolean isAmki1st() {
        return amki1st;
    }

    public void setAmki1st(boolean amki1st) {
        this.amki1st = amki1st;
    }

    public boolean isAmki2nd() {
        return amki2nd;
    }

    public void setAmki2nd(boolean amki2nd) {
        this.amki2nd = amki2nd;
    }

    public boolean isUnknown() {
        return unknown;
    }

    public void setUnknown(boolean unknown) {
        this.unknown = unknown;
    }

    public boolean isKnown() {
        return known;
    }

    public void setKnown(boolean known) {
        this.known = known;
    }

    public boolean isDifficultPronunciation() {
        return difficultPronunciation;
    }

    public void setDifficultPronunciation(boolean difficultPronunciation) {
        this.difficultPronunciation = difficultPronunciation;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public boolean isSelectedAnyVocaKnow() {
        return notRated == true ||
                amki1st == true ||
                amki2nd == true ||
                unknown == true ||
                known == true ||
                difficultPronunciation == true ||
                bookmarked == true;
    }

    public Set<Integer> getSelectedItems() {
        selectedItems = new HashSet<>();

        if (isAmki1st())
            selectedItems.add(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);

        if (isAmki2nd())
            selectedItems.add(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);

        if (isUnknown())
            selectedItems.add(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);

        if (isKnown())
            selectedItems.add(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);

        if (isAmki1st())
            selectedItems.add(Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);

        return selectedItems;
    }
//
//    public Map<Integer, Integer> mapDifficultType() {
//        Set<Integer> setSelected = new HashSet<>();
//
//        Map<Integer, Integer> mapSelectedItem = new HashMap<>();
//        if (isAmki1st())
//            setSelected.add(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
//
//        if (isAmki2nd())
//            setSelected.add(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
//
//        if (isUnknown())
//            setSelected.add(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
//
//        if (isKnown())
//            setSelected.add(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
//
//        if (isAmki1st())
//            setSelected.add(Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);
//        return mapSelectedItem;
//    }
    public String getSelectedItemsWithComma() {

        return getSelectedItems().stream().map(e -> Integer.toString(e)).collect(Collectors.joining(","));
//        String strResult = "";
//        if (isAmki1st())
//            strResult += Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1;
//
//        if (isAmki2nd())
//            strResult += Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2;
//
//        if (isUnknown())
//            strResult += Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
//
//        if (isKnown())
//            strResult += Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
//
//        if (isAmki1st())
//            strResult += Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
//
//
//        if (strResult.endsWith(","))
//            strResult = strResult.substring(0, strResult.length() - 1);
//
//        return strResult;
    }
}
