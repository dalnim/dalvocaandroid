package com.dalread.util;

import androidx.annotation.NonNull;

import com.dalread.database.sqlite.model.DIC_ICT_TERM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UtilIctTerm {
    @NonNull
    public static List<DIC_ICT_TERM> filterList(List<DIC_ICT_TERM> list, String keyword) {
        Set<DIC_ICT_TERM> setFiltered = new LinkedHashSet<>();
        for(DIC_ICT_TERM item : list) {
            if (item.getTERM_ENG_ABBR().toLowerCase().startsWith(keyword)) {
                setFiltered.add(item);
            }
        }
        for(DIC_ICT_TERM item : list) {
            if (!setFiltered.contains(item) && item.getTERM_ENG_ABBR().toLowerCase().contains(keyword)) {
                setFiltered.add(item);
            }
        }
        for(DIC_ICT_TERM item : list) {
            if (!setFiltered.contains(item) && item.getTERM_ENG_FULL().toLowerCase().startsWith(keyword)) {
                setFiltered.add(item);
            }
        }
        for(DIC_ICT_TERM item : list) {
            if (!setFiltered.contains(item) && item.getTERM_ENG_FULL().toLowerCase().contains(keyword)) {
                setFiltered.add(item);
            }
        }
        for(DIC_ICT_TERM item : list) {
            if (!setFiltered.contains(item) && item.getTERM_KO_TITLE().toLowerCase().startsWith(keyword)) {
                setFiltered.add(item);
            }
        }
        for(DIC_ICT_TERM item : list) {
            if (!setFiltered.contains(item) && item.getTERM_KO_TITLE().toLowerCase().contains(keyword)) {
                setFiltered.add(item);
            }
        }

        return new ArrayList<>(setFiltered);
    }

    public static List<DIC_ICT_TERM> sortList(List<DIC_ICT_TERM> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        Collections.sort(list, Comparator.nullsLast(Comparator.comparingInt(DIC_ICT_TERM::getBOOKMARK).reversed()
                .thenComparing(DIC_ICT_TERM::getTERM_ENG_ABBR, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(DIC_ICT_TERM::getTERM_ENG_FULL, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(DIC_ICT_TERM::getTERM_KO_TITLE)));
        return list;
    }

    public static List<DIC_ICT_TERM> resetIndex(List<DIC_ICT_TERM> list) {
        int i = 1;
        for(DIC_ICT_TERM hanjaItem : list) {
            hanjaItem.setINDEX(i++);
        }
        return list;
    }

    public static List<DIC_ICT_TERM> updateItemInList(List<DIC_ICT_TERM> list, DIC_ICT_TERM item) {
        if (item != null) {
            for (int i = 0; i < list.size(); i++) {
                DIC_ICT_TERM hanjaItemInList = list.get(i);
                if (item.getID() == hanjaItemInList.getID()) {
                    item.setINDEX(hanjaItemInList.getINDEX());
                    list.set(i, item);
                }
            }
        }
        return list;
    }

    public static List<DIC_ICT_TERM> addItemInList(List<DIC_ICT_TERM> list, DIC_ICT_TERM item) {
        if (item != null) {
            for (int i = 0; i < list.size(); i++) {
                DIC_ICT_TERM hanjaItemInList = list.get(i);
                if (item.getID() == hanjaItemInList.getID()) {
                    item.setINDEX(hanjaItemInList.getINDEX());
                    list.set(i, item);
                }
            }
        }
        return list;
    }
}
