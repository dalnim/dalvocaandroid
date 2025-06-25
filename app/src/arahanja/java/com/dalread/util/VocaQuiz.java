package com.dalread.util;

import android.content.Context;

import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.HanjaItem;
import com.dalread.model.VocaStudyChatExam;

import java.util.List;

public class VocaQuiz extends BaseVocaQuiz {
    public static List<VocaStudyChatExam> generateVocaStudyChatExamFromHanjaItemList(Context context, List<HanjaItem> list) {
        int quizCount = 20;
        List<HanjaItem> unknownVocaList = VocaKnow.getUnknowAndLessVoca(list, quizCount);
        List<IVocaFullPlayTTSItem> ttsItemList;
        if (unknownVocaList.size() == 0) {
            ttsItemList = Voca.convertHanjaItemListToIVocaFullPlayTTSItemList(list, quizCount);
        } else {
            ttsItemList = Voca.convertHanjaItemListToIVocaFullPlayTTSItemList(unknownVocaList, quizCount);
        }
        return generateVocaStudyChatExam(context, ttsItemList);
    }
    public static List<VocaStudyChatExam> generateVocaStudyChatExamFromTodayCandidateList(Context context) {
        int quizCount = 20;
        List<DIC_HANJA> hanjaTodayCandidateList = Voca.getHanjaTodayCandidateList(quizCount);
        List<IVocaFullPlayTTSItem> ttsItemList = Voca.convertDicHanjaListToIVocaFullPlayTTSItemList(hanjaTodayCandidateList, quizCount);
        return generateVocaStudyChatExam(context, ttsItemList);
    }
}