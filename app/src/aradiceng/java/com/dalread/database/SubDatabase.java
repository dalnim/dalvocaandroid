package com.dalread.database;

import static com.dalread.util.Constant.PLAYER.SQL.COLUMN;
import static com.dalread.util.Constant.PLAYER.SQL.QUERY;

import android.content.Context;

import com.dalread.database.sqlite.DicSentenceSubDatabase;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;
import com.dalread.util.stt.SttModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SubDatabase extends DicSentenceSubDatabase {
    private static SubDatabase instance;

    public static SubDatabase getInstance(Context context, String path) {
        if (instance == null || subPath == null || !subPath.equals(path))
            instance = new SubDatabase(context, path);
        return instance;
    }

    public SubDatabase(Context context, String path) {
        super(context, path);
    }


//    public List<IVocaFullPlayTTSItem> getDifficultWordListBySentence(String sentence) {
//        String wordListWithComma = StringUtils.splitSentenceIntoWordListWithComma(sentence);
//        if (wordListWithComma.trim().equals(""))
//            return Collections.emptyList();
//        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD
//                + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.LESS + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
//                + QUERY.AND + COLUMN.WORDLEVEL + QUERY.GREATER + "2"
//                + QUERY.AND;
//        String queryFinal = query + COLUMN.WORD + QUERY.IN_OPEN + wordListWithComma + QUERY.IN_CLOSE + QUERY.FINISH;
//        return getVocaListBySQL(TABLE_DIC_WORD, queryFinal);
//    }

    public List<IVocaFullPlayTTSItem> getDifficultWordList(SttModel model) {
        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD
                + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.LESS + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                + QUERY.AND + COLUMN.WORDLEVEL + QUERY.GREATER + "2"
                + QUERY.AND;
        return getWordListCommon(model, query);
    }

    public List<IVocaFullPlayTTSItem> getWordList(SttModel model) {
        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD
                + QUERY.WHERE;
        return getVocaListBySQL(TABLE_DIC_WORD, query);
    }

    private List<IVocaFullPlayTTSItem> getWordListCommon(SttModel model, String query) {
        List<SttModel.Word> wordList = model.SMgetWordList();
        if (Utils.isEmpty(wordList))
            return Collections.emptyList();

        String wordListWithComma = wordList.stream().map(e -> "\"" + e.getWord().replace("\"","") + "\"").collect(Collectors.joining(","));
        String queryFinal = query + COLUMN.WORD + QUERY.IN_OPEN + wordListWithComma + QUERY.IN_CLOSE + QUERY.FINISH;
        return getVocaListBySQL(TABLE_DIC_WORD, queryFinal);
    }
}
