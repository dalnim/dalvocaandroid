package com.dalread.util;

public enum SupportImageFormat {
    // https://exoplayer.dev/supported-formats.html
    // https://developer.android.com/guide/topics/media/media-formats#core
    PNG("png"),
    JPG("jpg")
            ;

    private String fileSuffix;

    SupportImageFormat(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public String getDotFileSuffix() {
        return "." + fileSuffix;
    }


}
