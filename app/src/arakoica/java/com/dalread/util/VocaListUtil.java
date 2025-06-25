package com.dalread.util;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaFullPlayTTSItem;

import java.util.List;

public class VocaListUtil extends BaseVocaList {
    public static List<IVocaFullPlayTTSItem> getAllWordListOfFromDB(String sentence, SubDatabase subDatabase) {
        String wordListWithComma = StringUtils.splitSentenceIntoWordListWithComma(sentence);
        return subDatabase.getAllWordListWordListWithComma(wordListWithComma);
    }
}
