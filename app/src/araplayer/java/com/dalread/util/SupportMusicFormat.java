package com.dalread.util;

import com.dalread.BuildConfig;

import java.util.ArrayList;
import java.util.List;

public enum SupportMusicFormat {
    // https://exoplayer.dev/supported-formats.html
    // https://developer.android.com/guide/topics/media/media-formats#core
    NONE("none"),
    OGG("ogg"),
    MP3("mp3"),
    FLAC("flac"),
    M4A("m4a")


            ;

    private String fileSuffix;

    SupportMusicFormat(String fileSuffix) {
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
        for (SupportMusicFormat video : values()) {
            if (isSupportedFormatOnDebugModeOnly(video)) {
                continue;
            }
            videos.add(video.getFileSuffix());
        }
        videos.remove(0);
        return videos;
    }

    private static boolean isSupportedFormatOnDebugModeOnly(SupportMusicFormat format) {
        return !BuildConfig.DEBUG && isAudioFormat(format);
    }

    public static boolean isAudioFormat(SupportMusicFormat format) {
        return ((format == OGG) || (format == MP3) || (format == FLAC) || (format == M4A));
    }
}
