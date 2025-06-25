package com.dalread.database.sqlite.model;

import com.dalread.util.TimeUtil;

public class SubtitleModel {
    private int id;
    private int langStudy;
    private String subtitle;
//    private String subtitleRuby;
    private String meaning;
    private long starTime;
    private long endTime;
    private int bookmark;

    public SubtitleModel() {
    }

//    public SubtitleModel(int id, int langStudy, String subtitle, String subtitleRuby, long starTime, long endTime, int bookmark) {
    public SubtitleModel(int id, int langStudy, String subtitle, long starTime, long endTime, int bookmark) {
        this.id = id;
        this.langStudy = langStudy;
        this.subtitle = subtitle;
//        this.subtitleRuby = subtitleRuby;
        this.starTime = starTime;
        this.endTime = endTime;
        this.bookmark = bookmark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLangStudy() {
        return langStudy;
    }

    public void setLangStudy(int langStudy) {
        this.langStudy = langStudy;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle.replaceAll("\n+", "\n");
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning.replaceAll("\n+", "\n");
    }

    public String getMeaning() {
        return meaning;
    }

//    public String getSubtitleRuby() {
//        return subtitleRuby;
//    }
//
//    public void setSubtitleRuby(String subtitleRuby) {
//        this.subtitleRuby = subtitleRuby;
//    }

    public long getStarTime() {
        return starTime;
    }

    public void setStarTime(long starTime) {
        this.starTime = starTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public String getDisplayFormatTimes() {
        return TimeUtil.displayTimesMillisecondsForSRT(starTime) + " --> " + TimeUtil.displayTimesMillisecondsForSRT(endTime);
    }

    public String getDisplayFormatTimesForStartTimeLyric() {
        return TimeUtil.displayTimesMillisecondsForLRC(starTime);
    }
    public String getDisplayFormatTimesForEndTimeLyric() {
        return TimeUtil.displayTimesMillisecondsForLRC(endTime);
    }

    @Override
    public String toString() {
        return "SubtitleModel{" +
                "id='" + id + '\'' +
                ", langStudy='" + langStudy + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", meaning='" + meaning + '\'' +
//                ", subtitleRuby='" + subtitleRuby + '\'' +
                ", starTime=" + starTime +
                ", endTime=" + endTime +
                ", bookmark=" + bookmark +
                '}';
    }
}
