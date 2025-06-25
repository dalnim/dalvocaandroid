package com.dalread.helper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dalread.util.StringUtils;
import com.dalread.util.studylang.AbstractStudyLang;
import com.dalread.util.studylang.StudyLangFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChooseWordHelper {
    private static final int BATCH_SIZE = 8;
    private Context context;
    private String sentence;
    private List<String> sentenceWords;
    private List<String> randomizedWords;
    private int currentWordIndex = 0;
    private int currentBatch = 0;

    public ChooseWordHelper(Context context, String sentence) {
        this.context = context;
        this.sentence = sentence;
        AbstractStudyLang studyLang = StudyLangFactory.create(context);
        sentenceWords = studyLang.splitSentence(sentence);
        randomizedWords = new ArrayList<>();
        // Randomize and combine the batches of words
        int batches = (int) Math.ceil((double) sentenceWords.size() / BATCH_SIZE);
        for (int i = 0; i < batches; i++) {
            List<String> batchWords = getNextBatchOfRandomizedWords(i);
            randomizedWords.addAll(batchWords);
        }
    }
    public String getNextWord() {
        if (currentWordIndex < sentenceWords.size()) {
            return sentenceWords.get(currentWordIndex);
        }
        return null;
    }

    public List<String> getNextBatchOfRandomizedWords(int currentBatch) {
        int start = currentBatch * BATCH_SIZE;
        int end = Math.min((currentBatch + 1) * BATCH_SIZE, sentenceWords.size());
        List<String> batchWords = new ArrayList<>(sentenceWords.subList(start, end));
        Collections.shuffle(batchWords);
        return switchConsecutiveWords(start, batchWords);
    }

    @NonNull
    private List<String> switchConsecutiveWords(int start, List<String> batchWords) {
        for (int i = 0; i < batchWords.size() - 2; i++) {
            String currentWord = batchWords.get(i);
            String nextWord = batchWords.get(i + 1);
            String currentWordInSentence = sentenceWords.get(start + i);
            String nextWordInSentence = sentenceWords.get(start + i + 1);

            if (currentWord.equals(currentWordInSentence) && (nextWord.equals(nextWordInSentence))) {
                batchWords.set(i, nextWord);
                batchWords.set(i+1, currentWord);
            }
        }
        return batchWords;
    }

    public List<String> getSentenceWords() {
        return sentenceWords;
    }

    public List<String> getRandomizedWords() {
        return randomizedWords;
    }

    public boolean isCorrectWord(String word) {
        if (word.equals(sentenceWords.get(currentWordIndex))) {
            currentWordIndex++;
            return true;
        }
        return false;
    }

    public boolean isCompleted() {
        return currentWordIndex == sentenceWords.size();
    }

    private void refreshDictationData() {
        Collections.shuffle(randomizedWords = new ArrayList<>(sentenceWords));
    }

    public String makeSentenceFromWord(String prevSentence, String word) {
        String remainSentence = sentence.replace(prevSentence, "");

        String textBeforeWord = StringUtils.getTextBeforeCharacter(remainSentence, word);
        return prevSentence + textBeforeWord + word;
    }
}
