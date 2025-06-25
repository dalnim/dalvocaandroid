package com.dalread.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dalread.BaseApplication;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_PLAY_VOICE_OR_TTS = "ACTION_PLAY_VOICE_OR_TTS";
    public static final String ACTION_FINISH_CURRENT_TTS = "ACTION_FINISH_CURRENT_TTS";
    public static final int PLAY_NEXT_TTS_REQUEST_CODE = 9001;
    public static final int FINISH_CURRENT_TTS_REQUEST_CODE = 9002;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_PLAY_VOICE_OR_TTS)) {
            BaseApplication.getInstance().getPlayTTSHelper().handlePlayVoiceOrTts();
        } else if (intent.getAction().equals(ACTION_FINISH_CURRENT_TTS)) {
            BaseApplication.getInstance().getPlayTTSHelper().handleFinishCurrentItem();
        }
    }
}
