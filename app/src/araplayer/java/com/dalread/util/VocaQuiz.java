package com.dalread.util;

import android.content.Context;

import com.dalread.database.sqlite.model.DicModel;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaStudyChatExam;

import java.util.List;

public class VocaQuiz extends BaseVocaQuiz {
    public static List<VocaStudyChatExam> generateVocaStudyChatExamFromDicModelList(Context context, List<DicModel> modelListQuiz) {
        int quizCount = 20;
        List<IVocaFullPlayTTSItem> ttsItemList = Voca.convertDicModelListToIVocaFullPlayTTSItemList(modelListQuiz, quizCount);
        return generateVocaStudyChatExam(context, ttsItemList);
    }
}