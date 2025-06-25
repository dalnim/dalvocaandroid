package com.dalread.util;

import com.dalread.model.TimeMinuteModel;

public class TimeUtil {

    private static final int MS_HOUR = 3600000;
    private static final int MS_MINUTE = 60000;
    private static final int MS_SECOND = 1000;

    public static String getDisplay(long time) {
        return getDisplay(time, Constant.PLAYER.TIMER.SECOND);
    }

    public static String getDisplay(float time) {
        return getDisplay(time, Constant.PLAYER.TIMER.SECOND);
    }

    //Dalnim updated, when it was minus value old code doesn't work  (ex : 3:1200)
    public static String getDisplay(long time, int seconds) {
        TimeMinuteModel t = new TimeMinuteModel((int)time);

        String minusSymbol = time >= 0 ? "" : "-";
        if (t.getHour() > 0)
            return String.format("%s%d:%02d:%02d", minusSymbol, t.getHour(), t.getMinute(), t.getSecond());
        return String.format("%s%d:%02d", minusSymbol, t.getMinute(), t.getSecond());
    }

    //Dalnim update
    public static String getRepeatDisplay(float time) {
        TimeMinuteModel t = new TimeMinuteModel((int)time);

        if (t.getHour() > 0)
            return String.format("%2d:%02d:%02d.%d", t.getHour(), t.getMinute(), t.getSecond(), t.getMillisecond()/100);
        return String.format("%2d:%02d.%d", t.getMinute(), t.getSecond(), t.getMillisecond()/100);
    }

    public static String getDisplay(float time, int seconds) {
        int hour = (int) (time/MS_HOUR);
        int minute = (int) ((time-(hour*MS_HOUR))/MS_MINUTE);
        int second = (int) ((time-(hour*MS_HOUR + minute*MS_MINUTE))/seconds);

        if (hour > 0)
            return String.format("%02d:%02d:%02d", hour, minute, second);
        return String.format("%02d:%02d", minute, second);
    }

    public static String displayTimesMilliseconds(long time) {
        int hour = (int) (time / MS_HOUR);
        int minute = (int) ((time - (hour * MS_HOUR)) / MS_MINUTE);
        int second = (int) ((time - (hour * MS_HOUR + minute * MS_MINUTE)) / MS_SECOND);
        int millisecond = (int) (time - (hour * MS_HOUR + minute * MS_MINUTE + second * MS_SECOND)) / 100;
        if (hour > 0)
            return String.format("%02d:%02d:%02d.%d", hour, minute, second, millisecond);
        return String.format("%02d:%02d.%d", minute, second, millisecond);
    }

    //Dalnim added SRT format needs hour and minute always even if it's zero
    public static String displayTimesMillisecondsForSRT(long time) {
        TimeMinuteModel t = new TimeMinuteModel((int)time);
        return String.format("%02d:%02d:%02d,%03d", t.getHour(), t.getMinute(), t.getSecond(), t.getMillisecond());
    }

    //Dalnim added LRC format needs minute always even if it's zero
    public static String displayTimesMillisecondsForLRC(long time) {
        TimeMinuteModel t = new TimeMinuteModel((int)time);
        return String.format("[%02d:%02d.%02d]", t.getMinute(), t.getSecond(), t.getMillisecond()/10);
    }

    public static String displayMinuteAndMilliseconds(long time) {
        int hour = (int) (time / MS_HOUR);
        int minute = (int) ((time - (hour * MS_HOUR)) / MS_MINUTE);
        int second = (int) ((time - (hour * MS_HOUR + minute * MS_MINUTE)) / MS_SECOND);
        int millisecond = (int) (time - (hour * MS_HOUR + minute * MS_MINUTE + second * MS_SECOND));
        return String.format("%02d:%02d.%d", minute, second, millisecond / 100);
    }

    public static String getVideoTimeDisplay(long value) {
        if (value < 0) {
            value = 0;
        }
        return TimeUtil.getDisplay(value);
    }


    //Dalnim added
    public static String getVideoTimeDisplay_detailed(long time) {
        return getRepeatDisplay(time) + " (" + time + ")";
    }
}
