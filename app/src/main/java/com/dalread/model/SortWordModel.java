package com.dalread.model;

import java.util.Comparator;

/**
 * Created by JetVHS on 3/19/2017.
 */
public class SortWordModel implements Comparator<WordModel> {
    @Override
    public int compare(WordModel lhs, WordModel rhs) {
        return lhs.getWord().compareToIgnoreCase(rhs.getWord());
    }
}
