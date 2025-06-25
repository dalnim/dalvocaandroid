package com.dalread.util;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaStudyChatExam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseVocaQuiz {
    public static int generateMultipleChoiceQuestionType(Context context) {
        int type;
        switch (SharedPreferencesDB.getInstance(context).getPlayerQuizType()) {
            case 0:
                type = Constant.MULTIPLE_CHOICE_QUESTION_TYPE.VOCA;
                break;
            case 1:
                type = Constant.MULTIPLE_CHOICE_QUESTION_TYPE.MEANING;
                break;
            default:
                type = Constant.MULTIPLE_CHOICE_QUESTION_TYPE.RANDOM;
                break;
        }
        return type;
    }

    public static List<VocaStudyChatExam> generateVocaStudyChatExam(Context context, List<IVocaFullPlayTTSItem> hanjaTodayCandidateList) {
        List<VocaStudyChatExam> items = new ArrayList<>();
        if (Utils.isEmpty(hanjaTodayCandidateList)) {
            return items;
        }

        int multipleChoiceQuestionType = generateMultipleChoiceQuestionType(context);
        for (int i = 0; i < hanjaTodayCandidateList.size(); i++) {
            IVocaFullPlayTTSItem d = hanjaTodayCandidateList.get(i);
            final VocaStudyChatExam item = new VocaStudyChatExam();
            item.setType(d.getVIVocaType());
            item.setVocaId(d.getVIId());
            item.setVoca(d.getVIVoca());
            item.setVocaDisplay(d.getVIVoca());
            item.setVocaTTS(d.getVIVocaTTS());
            item.setMeaning(d.getVIMeaning(context));
            item.setPronounce(d.getVIPronounce());
            item.setVocaKnow(d.getVIVocaKnow());
            item.setVocaKnowPronounce(d.getVIVocaKnowPronounce());
            item.setPath("");
            item.setBookmark(d.getVIBookmark().intValue());

            getOneQuestionFourAnswers(context, hanjaTodayCandidateList, i, d, item, multipleChoiceQuestionType);
            items.add(item);
        }
        return items;
    }
    private static void getOneQuestionFourAnswers(Context context, List<IVocaFullPlayTTSItem> hanjaTodayCandidateList, int i, IVocaFullPlayTTSItem d, VocaStudyChatExam item, int multipleChoiceQuestionType) {
        List<Integer> index = Voca.getRandom(hanjaTodayCandidateList.size(), Constant.PLAYER.QUIZ.QUIZ_ANSWERS, i);
        Collections.shuffle(index);
        if (multipleChoiceQuestionType == Constant.MULTIPLE_CHOICE_QUESTION_TYPE.VOCA) {
            item.setQuestion(d.getVIVoca());


            Set<String> setWrongAnswers = new HashSet<>();
            String correctAnswer = d.getVIMeaning(context).trim();
            setWrongAnswers.add(correctAnswer);
            for(IVocaFullPlayTTSItem dicHanja : hanjaTodayCandidateList) {
                if (setWrongAnswers.size() >= Constant.PLAYER.QUIZ.QUIZ_ANSWERS) {
                    break;
                }
                setWrongAnswers.add(dicHanja.getVIMeaning(context).trim());
            }
            List<String> wrongAnswers = new ArrayList<>(setWrongAnswers);

            if (wrongAnswers.size() == Constant.PLAYER.QUIZ.QUIZ_ANSWERS) {
                Collections.shuffle(wrongAnswers);
                int correctAnswerIndex = 0;
                for (String answer : wrongAnswers) {
                    if (answer.equals(correctAnswer)) {
                        break;
                    }
                    correctAnswerIndex++;
                }
                item.setCorrectAnswerNumber(correctAnswerIndex+1);
                item.setAnswer1(wrongAnswers.get(0));
                item.setAnswer2(wrongAnswers.get(1));
                item.setAnswer3(wrongAnswers.get(2));
                item.setAnswer4(wrongAnswers.get(3));
            }
        } else {
            item.setQuestion(d.getVIVocaMeaning(context));

            Set<String> setWrongAnswers = new HashSet<>();
            String correctAnswer = d.getVIVoca().trim();
            setWrongAnswers.add(correctAnswer);
            for(IVocaFullPlayTTSItem dicHanja : hanjaTodayCandidateList) {
                if (setWrongAnswers.size() >= Constant.PLAYER.QUIZ.QUIZ_ANSWERS) {
                    break;
                }
                setWrongAnswers.add(dicHanja.getVIVoca().trim());
            }
            List<String> wrongAnswers = new ArrayList<>(setWrongAnswers);
            if (wrongAnswers.size() == Constant.PLAYER.QUIZ.QUIZ_ANSWERS) {
                Collections.shuffle(wrongAnswers);
                int correctAnswerIndex = 0;
                for (String answer : wrongAnswers) {
                    if (answer.equals(correctAnswer)) {
                        break;
                    }
                    correctAnswerIndex++;
                }
                item.setCorrectAnswerNumber(correctAnswerIndex + 1);
                item.setAnswer1(wrongAnswers.get(0));
                item.setAnswer2(wrongAnswers.get(1));
                item.setAnswer3(wrongAnswers.get(2));
                item.setAnswer4(wrongAnswers.get(3));
            }
        }

    }
}
