package com.dalread.util;

public class NumberUtil {
    public static String formatNumber(int number) {
        return String.format("%,d", number);
    }

    public static int percentage(int min, int max) {
        if (max <= 0) return 0;
        return (int) Math.round((min / (double) max) * 100);
    }

    public static String percentageString(int min, int max) {
        return percentage(min, max) + "%";
    }

    public static int percentage(long min, long max) {
        if ((max == 0) || (min == 0))
            return 0;

        int percentage = (int)(min * 100.0 / max + 0.5);
        if (percentage == 0) // sometimes min is greater than 0, but the percentage can be 0, so AraPlayer doesn't show the watched duration.
            return 1;

        return percentage;
    }

    public static int percentageWatched(long min, long max) {
        int precentage  =  percentage(min, max);

        if (precentage > 95)
            return 100;
        else
            return precentage;
    }
}
