package com.dalread.util;

import android.content.Context;

import com.dalread.R;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.studylang.AbstractStudyLang;
import com.dalread.util.studylang.StudyLangFactory;

public class RepeatUtil {

    private static final String TAG = "RepeatUtil";

    public static int getRepeatCount(Context context, String count) {
        int repeatCount;
        if (count.equals(context.getString(R.string.smart_fewer, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER_PLUS))) {
            repeatCount = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER;
        } else if (count.equals(context.getString(R.string.smart_few, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW_PLUS))) {
            repeatCount = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW;
        } else if (count.equals(context.getString(R.string.smart_many, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY_PLUS))) {
            repeatCount = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY;
        } else {
            repeatCount = Utils.parseInt(count);
        }
        return repeatCount;
    }

    //Who uses this?
//    public static String getRepeatCount(Context context, int count) {
//        switch (count) {
//            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER:
//                return context.getString(R.string.smart_fewer);
//            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW:
//                return context.getString(R.string.smart_few);
//            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY:
//                return context.getString(R.string.smart_many);
//            default:
//                return String.valueOf(count);
//        }
//    }

//    public static int getRepeatValue(DicModel dicModel) {
//        if (dicModel.getVocaKnow() >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN ) { // Don't need to practice more if I know this.
//            return Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.JUST_PRACTICE_ONE_IS_ENOUGH;
//        }
//
//        int wordsCount = dicModel.getDifficultWordsCount();
//        if (dicModel.getVocaKnowPronounce() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) { // If I don't know the pronounce, I need to practice more
////            wordsCount += Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.DONT_KNOW_PRONOUNCE_SO_NEED_TO_PRACTICE_MORE; //Dalnim : At this moment, KnowPronounce value is not correct, so skip this now.
//        }
//
//        final int count = dicModel.getRepeat();
//        switch (count) {
//            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER:
//                return wordsCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER_PLUS;
//            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW:
//                return wordsCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW_PLUS;
//            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY:
//                return wordsCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY_PLUS;
//        }
//        DLog.d(TAG, "getRepeatValue - " + count);
//        return count;
//    }

    public static int getRepeatValue(IVocaFullPlayTTSItem voca) {
        if (BaseVocaKnow.isKnown(voca) ) { // Don't need to practice more if I know this.
            return Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.JUST_PRACTICE_ONE_IS_ENOUGH;
        }

        int wordsCount = voca.getVIDifficultWordsCount();
        if (BaseVocaKnow.isUnknownAndLessPronounce(voca)) { // If I don't know the pronounce, I need to practice more
//            wordsCount += Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.DONT_KNOW_PRONOUNCE_SO_NEED_TO_PRACTICE_MORE; //Dalnim : At this moment, KnowPronounce value is not correct, so skip this now.
        }

        final int count = voca.getVIRepeatCount();
        switch (count) {
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER:
                return wordsCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER_PLUS;
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW:
                return wordsCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW_PLUS;
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY:
                return wordsCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY_PLUS;
        }
        DLog.d(TAG, "getRepeatValue - " + count);
        return count;
    }

    public static int getRepeatValueWithoutDifficultWords(Context context, IVocaFullPlayTTSItem voca) {
        if (BaseVocaKnow.isKnown(voca) ) { // Don't need to practice more if I know this.
            return Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.JUST_PRACTICE_ONE_IS_ENOUGH;
        }

        AbstractStudyLang studyLang = StudyLangFactory.create(context);
        int baseCount = studyLang.getSmartRepeatCountByVocaLength(voca.getVIVoca());


        int count = voca.getVIRepeatCount();
        switch (count) {
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER:
                count = baseCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEWER_PLUS;
                break;
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW:
                count = baseCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW_PLUS;
                break;
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY:
                count = baseCount + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_MANY_PLUS;
                break;
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.ZERO:
                count = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.DEAFULT_VALUE;
                break;
        }
        DLog.d(TAG, "getRepeatValue - " + count);
        return count;
    }

    public static String getMinRepeatCountFormat(Context context, float count) {
        return context.getString(R.string.format_second, count);
    }

//    public static int getMinTimeKeepPlaySubtitle(VideoModel videoModel, int type) {
//        float value;
//        switch (type) {
//            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.BEFORE:
//                value = videoModel.getPlayBeforeSubtitle();
//                break;
//            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.AFTER:
//                value = videoModel.getPlayAfterSubtitle();
//                break;
//            default:
//                value = videoModel.getKeepPlayBetweenSubtitle();
//                break;
//        }
//        int time = getTime(value);
//        DLog.d(TAG, "getMinTimeKeepPlaySubtitle type=" + type + " - time=" + time);
//        return time;
//    }

//    private static long getPlayValueSubtitle(VideoModel videoModel, long time, int type) {
//        long value = time + getMinTimeKeepPlaySubtitle(videoModel, type) + videoModel.getDelaySubtitles();
//        if (value < 0) {
//            value = 0;
//        }
//        return value;
//    }
//    public static long getPlayBeforeSubtitle(VideoModel videoModel, long startTime) {
//        return getPlayValueSubtitle(videoModel, startTime, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.BEFORE);
//    }
//
//    public static long getPlayBeforeSubtitle(VideoModel videoModel) {
//        return getMinTimeKeepPlaySubtitle(videoModel, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.BEFORE);
//    }
//
//    public static long getPlayAfterSubtitle(VideoModel videoModel, long endTime) {
//        return getPlayValueSubtitle(videoModel, endTime, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.AFTER);
//    }
//
//    public static long getPlayAfterSubtitle(VideoModel videoModel) {
//        return getMinTimeKeepPlaySubtitle(videoModel, Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.AFTER);
//    }

    public static int getTime(float value) {
        return (int) (value * Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_VALUE);
    }
}
