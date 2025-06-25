package com.dalread.util;

import android.content.Context;

import com.dalread.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    private static final String DATE_FULL_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FULL_NO_SECOND_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String DATE_ONLY_PATTERN = "yyyy/MM/dd";
    private static final String DATE_ONLY_WITH_DAY_NAME_PATTERN = "yyyy/MM/dd (EEE)";
    private static final String DATE_STRING_PATTERN = "yyyy_MM_dd";
    private static final String TIME_TO_MINUTE_PATTERN = "mm:ss";
    private static final String TIME_TO_HOUR_PATTERN = "HH:mm";
    private static final String TIME_WIDGET_PATTERN = "HH:mm";
    private static final String DATE_DAY_WIDGET_PATTERN = "(d EEE)";
    private static final String DATE_FULL_NO_SPACE = "yyyyMMddHHmmss";
    private static final String DATE_DAY_TIME_NO_SPACE = "ddHHmmss";

    public static DateFormat getDateFullFormat() {
        return new SimpleDateFormat(DATE_FULL_PATTERN, Locale.getDefault());
    }

    public static DateFormat getDateFullNoSecondFormat() {
        return new SimpleDateFormat(DATE_FULL_NO_SECOND_PATTERN, Locale.getDefault());
    }

    public static DateFormat getDateOnlyFormat() {
        return new SimpleDateFormat(DATE_ONLY_PATTERN, Locale.getDefault());
    }

    public static DateFormat getDateOnlyWithDayNameFormat() {
        return new SimpleDateFormat(DATE_ONLY_WITH_DAY_NAME_PATTERN, Locale.getDefault());
    }

    public static DateFormat getDateStringFormat() {
        return new SimpleDateFormat(DATE_STRING_PATTERN, Locale.getDefault());
    }

    public static DateFormat getTimeToMinuteFormat() {
        return new SimpleDateFormat(TIME_TO_MINUTE_PATTERN, Locale.getDefault());
    }

    public static DateFormat getTimeToHourFormat() {
        return new SimpleDateFormat(TIME_TO_HOUR_PATTERN, Locale.getDefault());
    }

    public static DateFormat getTimeWidgetFormat() {
        return new SimpleDateFormat(TIME_WIDGET_PATTERN, Locale.getDefault());
    }

    public static DateFormat getDateDayWidgetFormat() {
        return new SimpleDateFormat(DATE_DAY_WIDGET_PATTERN, Locale.getDefault());
    }

    public static DateFormat getDateFullNoSpaceFormat() {
        return new SimpleDateFormat(DATE_FULL_NO_SPACE, Locale.getDefault());
    }

    public static DateFormat getDateDayTImeNoSpaceFormat() {
        return new SimpleDateFormat(DATE_DAY_TIME_NO_SPACE, Locale.getDefault());
    }


    public static boolean isPast(Date date) {
        Calendar c = Calendar.getInstance();
        return c.getTime().after(date);
    }

    public static boolean isSameDate(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isToday(Date date) {
        Calendar c = Calendar.getInstance();
        return isSameDate(c.getTime(), date);
    }

    public static boolean isTodayOrFuture(Date date) {
        Calendar c = Calendar.getInstance();
        return isSameDate(c.getTime(), date) || c.getTime().before(date);
    }

    public static boolean isYesterday(Date date) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 1);
        return isSameDate(c.getTime(), date);
    }

    public static boolean isTomorrow(Date date) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
        return isSameDate(c.getTime(), date);
    }

    public static boolean isThisWeek(Date date) {
        Calendar c1 = Calendar.getInstance(); // now
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isNextWeek(Date date) {
        Calendar c1 = Calendar.getInstance(); // now
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date);
        boolean case1 = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR) - 1;
        boolean case2 = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) - 1
                && c2.get(Calendar.WEEK_OF_YEAR) == 1;
        return case1 || case2;
    }

    public static boolean isSameWeek(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR);
    }

    public static String getFirstDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return getDateOnlyFormat().format(c.getTime());
    }

    public static String getLastDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.setTimeInMillis(c.getTimeInMillis() + TimeUnit.DAYS.toMillis(6));
        return getDateOnlyFormat().format(c.getTime());
    }

    public static String getDateVoca(Context context, Date date) {
        if (isToday(date))
            return context.getString(R.string.today);
        if (isYesterday(date))
            return context.getString(R.string.yesterday);
        return getDateOnlyFormat().format(date);
    }

    public static String getDateChatRoom(String strDate) {
        try {
            Date date = getDateFullFormat().parse(strDate);
            if (isToday(date))
                return getTimeToHourFormat().format(date);
            return getDateOnlyFormat().format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getCurrentDateTimeFullFormat() {
        return getDateFullFormat().format(new Date());
    }

    public static long secondsToMillis(long seconds) {
        return seconds * 1000;
    }

    public static long millisToSeconds(long millis) {
        return millis / 1000;
    }

    public static String getDateFullNoSpace(Date date) {
        return getDateFullNoSpaceFormat().format(date);
    }
    public static String getDateDayTimeNoSpace(Date date) {
        return getDateDayTImeNoSpaceFormat().format(date);
    }

    public static String convertLongToFullPatternDateString(Long value) {
        return getDateFullFormat().format(new Date(value));

    }

}
