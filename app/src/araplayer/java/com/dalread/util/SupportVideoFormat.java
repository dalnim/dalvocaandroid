package com.dalread.util;

import com.dalread.BuildConfig;

import java.util.ArrayList;
import java.util.List;

public enum SupportVideoFormat {
    // https://exoplayer.dev/supported-formats.html
    // https://developer.android.com/guide/topics/media/media-formats#core
    NONE("none"),
    F4V("f4v"),
    M4V("m4v"),
    MKV("mkv"),

    OGG("ogg"),
    MP3("mp3"),
    FLAC("flac"),
    M4A("m4a"),

    MP4("mp4"),
    MOV("mov"),
    TS("ts"),
    WEBM("webm")

//    OGG("ogg"), //NG
//    WMV("wmv"),//NG (Screen capture, but can't play
//    ASF("asf"), //NG
//    HEVC("hevc"),//NG
//    M2TS("m2ts"), //NG
//    M2V("m2v"), //NG
//    MPEG("mpeg"),//NG
//    MPG("mpg"),//NG
//    MTS("mts"),//NG(Black screen)
//    OGV("ogv"),//NG
//    RM("rm"),//NG
//    TS("ts"),//NG(Black screen)
//    VOB("vob"),//NG(Black screen)
//    WTV("wtv") //NG

            ;

    private String fileSuffix;

    SupportVideoFormat(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public String getDotFileSuffix() {
        return "." + fileSuffix;
    }

    public static List<String> getVideos() {
        ArrayList<String> videos = new ArrayList<>();
        for (SupportVideoFormat media : values()) {
            if (isVideoFormat(media)) {
                videos.add(media.getFileSuffix());
            }
        }
        return videos;
    }

    public static List<String> getMusics() {
        ArrayList<String> musics = new ArrayList<>();
        for (SupportVideoFormat media : values()) {
            if (isAudioFormat(media) || isVideoFormat(media)) {
                musics.add(media.getFileSuffix());
            }
        }
        return musics;
    }

    public static boolean isAudioFormat(SupportVideoFormat format) {
        return ((format == OGG) || (format == MP3) || (format == FLAC) || (format == M4A));
    }

    public static boolean isVideoFormat(SupportVideoFormat format) {
        return ((format == F4V) || (format == M4V) || (format == MKV) || (format == MP4) || (format == MOV) || (format == TS) || (format == WEBM));
    }
}
