package com.dalread.model;

import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LessonOption implements Serializable {

    @SerializedName(Constant.API_KEY.KEY_ENABLE_LESSON_PN)
    private int enableLessonPN;
    @SerializedName(Constant.API_KEY.KEY_PN_MINUTES_BEFORE_BEGIN_LESSON)
    private int pnMinutesBeforeBeginLesson;
    @SerializedName(Constant.API_KEY.KEY_PN_MINUTES_BEFORE_FINISH_LESSON)
    private int pnMinutesBeforeFinishLesson;
    @SerializedName(Constant.API_KEY.KEY_LEAD_LESSON)
    private int leadLesson;
    @SerializedName(Constant.API_KEY.KEY_LANGUAGE_LEVEL)
    private int languageLevel;
    @SerializedName(Constant.API_KEY.KEY_FREE_TALKING)
    private int freeTalking;
    @SerializedName(Constant.API_KEY.KEY_SMALL_TALKING)
    private int smallTalking;
    @SerializedName(Constant.API_KEY.KEY_SPEAKING_SPEED)
    private int speakingSpeed;
    @SerializedName(Constant.API_KEY.KEY_READING_SPEED)
    private int readingSpeed;
    @SerializedName(Constant.API_KEY.KEY_CORRECT_PRONUNCIATION)
    private int correctPronunciation;
    @SerializedName(Constant.API_KEY.KEY_LESSON_REPEAT_TOPICS)
    private int repeatTopics;
    @SerializedName(Constant.API_KEY.KEY_LESSON_REPEAT_COUNT)
    private int repeatCount;
    @SerializedName(Constant.API_KEY.KEY_LESSON_REPEAT_MAX_PHRASE_COUNT)
    private int repeatMaxPhraseCount;
    @SerializedName(Constant.API_KEY.KEY_USER_WORD_EXAM_TYPE)
    private int userWordExamType;
    @SerializedName(Constant.API_KEY.KEY_RECORD_LESSON)
    private int recordLesson;
    @SerializedName(Constant.API_KEY.KEY_COUNT_OF_PHRASES_TO_STUDY_AT_ONCE)
    private int countOfPhrasesToStudyAtOnce;
    @SerializedName(Constant.API_KEY.KEY_STUDY_ORDER_HIDE_PART_OF_WORDS)
    private int studyOrderHidePartOfWords;
    @SerializedName(Constant.API_KEY.KEY_STUDY_ORDER_HIDE_ALL_PHRASES)
    private int studyOrderHideAllPhrases;
    @SerializedName(Constant.API_KEY.KEY_STUDY_ORDER_RANDOM_QUESTIONS)
    private int studyOrderRandomQuestions;

    public int getEnableLessonPN() {
        return enableLessonPN;
    }

    public void setEnableLessonPN(int enableLessonPN) {
        this.enableLessonPN = enableLessonPN;
    }

    public int getPnMinutesBeforeBeginLesson() {
        return pnMinutesBeforeBeginLesson;
    }

    public void setPnMinutesBeforeBeginLesson(int pnMinutesBeforeBeginLesson) {
        this.pnMinutesBeforeBeginLesson = pnMinutesBeforeBeginLesson;
    }

    public int getPnMinutesBeforeFinishLesson() {
        return pnMinutesBeforeFinishLesson;
    }

    public void setPnMinutesBeforeFinishLesson(int pnMinutesBeforeFinishLesson) {
        this.pnMinutesBeforeFinishLesson = pnMinutesBeforeFinishLesson;
    }

    public int getLeadLesson() {
        return leadLesson;
    }

    public void setLeadLesson(int leadLesson) {
        this.leadLesson = leadLesson;
    }

    public int getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(int languageLevel) {
        this.languageLevel = languageLevel;
    }

    public int getFreeTalking() {
        return freeTalking;
    }

    public void setFreeTalking(int freeTalking) {
        this.freeTalking = freeTalking;
    }

    public int getSmallTalking() {
        return smallTalking;
    }

    public void setSmallTalking(int smallTalking) {
        this.smallTalking = smallTalking;
    }

    public int getSpeakingSpeed() {
        return speakingSpeed;
    }

    public void setSpeakingSpeed(int speakingSpeed) {
        this.speakingSpeed = speakingSpeed;
    }

    public int getReadingSpeed() {
        return readingSpeed;
    }

    public void setReadingSpeed(int readingSpeed) {
        this.readingSpeed = readingSpeed;
    }

    public int getCorrectPronunciation() {
        return correctPronunciation;
    }

    public void setCorrectPronunciation(int correctPronunciation) {
        this.correctPronunciation = correctPronunciation;
    }

    public int getRepeatTopics() {
        return repeatTopics;
    }

    public void setRepeatTopics(int repeatTopics) {
        this.repeatTopics = repeatTopics;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getRepeatMaxPhraseCount() {
        return repeatMaxPhraseCount;
    }

    public void setRepeatMaxPhraseCount(int repeatMaxPhraseCount) {
        this.repeatMaxPhraseCount = repeatMaxPhraseCount;
    }

    public int getUserWordExamType() {
        if (userWordExamType == 0) {
            userWordExamType = Constant.EXAM_TYPE_QUESTION_WORD;
        }
        return userWordExamType;
    }

    public void setUserWordExamType(int userWordExamType) {
        this.userWordExamType = userWordExamType;
    }

    public int getRecordLesson() {
        return recordLesson;
    }

    public void setRecordLesson(int recordLesson) {
        this.recordLesson = recordLesson;
    }

    public boolean isRecordLesson() {
        return recordLesson == Constant.API_VALUE.RECORD_LESSON_YES;
    }

    public int getCountOfPhrasesToStudyAtOnce() {
        return countOfPhrasesToStudyAtOnce;
    }

    public void setCountOfPhrasesToStudyAtOnce(int countOfPhrasesToStudyAtOnce) {
        this.countOfPhrasesToStudyAtOnce = countOfPhrasesToStudyAtOnce;
    }

    public int getStudyOrderHidePartOfWords() {
        return studyOrderHidePartOfWords;
    }

    public void setStudyOrderHidePartOfWords(int studyOrderHidePartOfWords) {
        this.studyOrderHidePartOfWords = studyOrderHidePartOfWords;
    }

    public boolean isStudyOrderHidePartOfWords() {
        return studyOrderHidePartOfWords == Constant.API_VALUE.IS_YES;
    }

    public int getStudyOrderHideAllPhrases() {
        return studyOrderHideAllPhrases;
    }

    public void setStudyOrderHideAllPhrases(int studyOrderHideAllPhrases) {
        this.studyOrderHideAllPhrases = studyOrderHideAllPhrases;
    }

    public boolean isStudyOrderHideAllPhrases() {
        return studyOrderHideAllPhrases == Constant.API_VALUE.IS_YES;
    }

    public int getStudyOrderRandomQuestions() {
        return studyOrderRandomQuestions;
    }

    public void setStudyOrderRandomQuestions(int studyOrderRandomQuestions) {
        this.studyOrderRandomQuestions = studyOrderRandomQuestions;
    }

    public boolean isStudyOrderRandomQuestions() {
        return studyOrderRandomQuestions == Constant.API_VALUE.IS_YES;
    }
}
