package com.dalread.composition;

import android.app.Activity;

public class PlayTTS extends AbstractPlayTTS{
    public PlayTTS(Activity activity) {
        super(activity);
        playTTSHelper.isPlayTTSAtFirst = false;
    }
}
