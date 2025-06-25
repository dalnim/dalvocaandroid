package com.dalread.model;

//Dalnim add
public class TimeMinuteModel {
    private final int MS_HOUR = 24;
    private final int MS_MINUTE = 60;
    private final int MS_MILLI = 1000;

    private int hour;
    private int minute;
    private int second;
    private int millisecond;

    //Dalnim added
    public TimeMinuteModel(int time) {
        this.hour = Math.abs((time / (MS_MILLI * MS_MINUTE * MS_MINUTE)) % MS_HOUR);
        this.minute = Math.abs(((time / MS_MILLI) / MS_MINUTE) % MS_MINUTE);
        this.second = Math.abs((time / MS_MILLI) % MS_MINUTE);
        this.millisecond = Math.abs(time % MS_MILLI);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMillisecond() {
        return millisecond;
    }

}
