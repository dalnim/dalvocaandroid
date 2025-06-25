package com.dalread.util;

import com.dalread.BuildConfig;

import java.util.ArrayList;
import java.util.List;

public enum SupportSubtitleFormat {
    NONE("none"),
    LRC("lrc"),
    TXT("txt"),

    ASS("ass"),
    SMI("smi"),
    SSA("ssa"),
    SRT("srt")
    ;

    private String fileSuffix;

    SupportSubtitleFormat(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public String getDotFileSuffix() {
        return "." + fileSuffix;
    }

    public static List<String> getSubtitles() {
        ArrayList<String> subtitles = new ArrayList<>();
        for (SupportSubtitleFormat subtitle : values()) {
            if (isSubtitleFormat(subtitle))
                subtitles.add(subtitle.getFileSuffix());
        }
//        subtitles.remove(0);
        return subtitles;
    }

    public static List<String> getLyrics() {
        ArrayList<String> lyrics = new ArrayList<>();
        for (SupportSubtitleFormat lyric : values()) {
            if (isLyricFormat(lyric))
                lyrics.add(lyric.getFileSuffix());
        }
//        lyrics.remove(0);
        return lyrics;
    }

    private static boolean isSupportedFormatOnDebugModeOnly(SupportSubtitleFormat format) {
        return !BuildConfig.DEBUG && isLyricFormat(format);
    }

    public static boolean isSubtitleFormat(SupportSubtitleFormat format) {
        return ((format == ASS) || (format == SSA) || (format == SRT) || (format == SMI));
    }

    public static boolean isLyricFormat(SupportSubtitleFormat format) {
        return (format == LRC || format == TXT);
    }

    public static boolean isTxtFormat(SupportSubtitleFormat format) {
        return format == TXT;
    }
}
