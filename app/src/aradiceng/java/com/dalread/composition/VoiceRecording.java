package com.dalread.composition;

import android.app.Activity;
import android.widget.ImageView;

public class VoiceRecording extends AbstractVoiceRecording {
    public VoiceRecording(Activity activity) {
        super(activity);
    }
    public VoiceRecording(Activity activity, ImageView ivMic, ImageView ivSpeaker) {
        super(activity, ivMic, ivSpeaker);
    }

}
