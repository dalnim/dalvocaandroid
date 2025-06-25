package com.dalread.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.dalread.BaseApplication;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;

public class MediaButtonIntentReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaButtonIntentReceiver";
    public MediaButtonIntentReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        ToastUtil.getInstance(context).show("MediaButtonIntentReceiver is called");
        // earphone, headphone, ear pod, ear phone
        if(Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                BaseApplication.getInstance().getEventBus().post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_BUTTON_PAUSE_BRROADCAST, true));
//                ToastUtil.getInstance(context).show("MEDIA_BUTTON_PAUSE_BRROADCAST");
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                BaseApplication.getInstance().getEventBus().post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_BUTTON_PLAY_BRROADCAST, true));
//                ToastUtil.getInstance(context).show("MEDIA_BUTTON_PLAY_BRROADCAST");
            }
        }
    }
}