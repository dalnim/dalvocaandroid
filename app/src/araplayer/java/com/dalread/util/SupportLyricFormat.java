package com.dalread.util;

import java.util.ArrayList;
import java.util.List;

public enum SupportLyricFormat {
    NONE("none"),
    LRC("lrc"),

    ASS("ass"),
    SMI("smi"),
    SSA("ssa"),
    SRT("srt")
    ;

    private String fileSuffix;

    SupportLyricFormat(String fileSuffix) {
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
        for (SupportLyricFormat subtitle : values()) {
            subtitles.add(subtitle.getFileSuffix());
        }
        subtitles.remove(0);
        return subtitles;
    }
}
