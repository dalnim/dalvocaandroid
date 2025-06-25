/*
 * Copyright (c) 2016-2019 The CallKeep Authors (see the AUTHORS file)
 * SPDX-License-Identifier: ISC, MIT
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.dalread.service.callkeep;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.TelecomManager;
import android.text.TextUtils;

import com.dalread.model.Lesson;
import com.dalread.model.ReceiveCallModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.M)
public class VoiceConnection extends Connection {
    private boolean isMuted = false;
    private HashMap<String, String> handle;
    private Context context;
    private static final String TAG = "VoiceConnection";
    private Lesson lesson;
    private ReceiveCallModel receiveCallModel;
    private Application application;
    private final String ADDRESS_UNKNOWN = "unknown";
    private final String ADDRESS_DEFAULT = "Dalnimbest";

    VoiceConnection(Application application, Context context, HashMap<String, String> handle) {
        super();
        this.handle = handle;
        this.application = application;
        this.context = context;

        String number = handle.get(Constant.NOTIFICATION_KEY.CALLER_NUMBER);
        String name = handle.get(Constant.NOTIFICATION_KEY.CALLER_USERNAME);
        if (number != null && !Uri.parse(number).toString().toLowerCase().contains(ADDRESS_UNKNOWN)) {
            setAddress(Uri.parse(number), TelecomManager.PRESENTATION_ALLOWED);
        } else {
            setAddress(Uri.parse(ADDRESS_DEFAULT), TelecomManager.PRESENTATION_ALLOWED);
        }
        if (!TextUtils.isEmpty(name)) {
            setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED);
        }
        receiveCallModel = new ReceiveCallModel(handle);
        lesson = new Lesson().createLesson(context, receiveCallModel);
    }

    @Override
    public void onExtrasChanged(Bundle extras) {
        super.onExtrasChanged(extras);
        HashMap attributeMap = (HashMap<String, String>)extras.getSerializable("attributeMap");
        if (attributeMap != null) {
            handle = attributeMap;
        }
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState state) {
        if (state.isMuted() == this.isMuted) {
            return;
        }
        this.isMuted = state.isMuted(); }

    @Override
    public void onAnswer() {
        super.onAnswer();
        DLog.d(TAG, "onAnswer called");
        setConnectionCapabilities(getConnectionCapabilities() | Connection.CAPABILITY_HOLD);
        setAudioModeIsVoip(true);
        openLessonListScreen();
    }

    @Override
    public void onPlayDtmfTone(char dtmf) {
        try {
            handle.put("DTMF", Character.toString(dtmf));
        } catch (Throwable exception) {
            DLog.e(TAG, "Handle map error", exception);
        }
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));
        DLog.d(TAG, "onDisconnect executed");
        try {
            ((VoiceConnectionService) context).deinitConnection(handle.get(Constant.NOTIFICATION_KEY.CALLER_UID));
        } catch(Throwable exception) {
            DLog.e(TAG, "Handle map error", exception);
        }
        destroy();
    }

    public void reportDisconnect(int reason) {
        super.onDisconnect();
        switch (reason) {
            case 1:
                setDisconnected(new DisconnectCause(DisconnectCause.ERROR));
                break;
            case 2:
                setDisconnected(new DisconnectCause(DisconnectCause.REMOTE));
                break;
            case 3:
                setDisconnected(new DisconnectCause(DisconnectCause.BUSY));
                break;
            default:
                break;
        }
        ((VoiceConnectionService)context).deinitConnection(handle.get(Constant.NOTIFICATION_KEY.CALLER_UID));
        destroy();
    }

    @Override
    public void onAbort() {
        super.onAbort();
        setDisconnected(new DisconnectCause(DisconnectCause.REJECTED));
        DLog.d(TAG, "onAbort executed");
        try {
            ((VoiceConnectionService) context).deinitConnection(handle.get(Constant.NOTIFICATION_KEY.CALLER_UID));
        } catch(Throwable exception) {
            DLog.e(TAG, "Handle map error", exception);
        }
        destroy();
    }

    @Override
    public void onHold() {
        super.onHold();
        this.setOnHold();
    }

    @Override
    public void onUnhold() {
        super.onUnhold();
        setActive();
    }

    @Override
    public void onReject() {
        super.onReject();
        CallKeepModule.getInstance(context).unregisterPhoneAccount();
        setDisconnected(new DisconnectCause(DisconnectCause.REJECTED));
        DLog.d(TAG, "onReject executed");
        try {
            ((VoiceConnectionService) context).deinitConnection(handle.get(Constant.NOTIFICATION_KEY.CALLER_UID));
        } catch (Throwable exception) {
            DLog.e(TAG, "Handle map error", exception);
        }
        VoiceUtils.replyCallFromOpponent(application,
                lesson,
                receiveCallModel,
                Constant.JITSI.REPLY_CALL_TYPE.DECLINE);
        destroy();
    }

    private void openLessonListScreen() {
        onAbort();
        CallKeepModule.getInstance(context).unregisterPhoneAccount();
        VoiceUtils.replyCallFromOpponent(application,
                lesson,
                receiveCallModel,
                Constant.JITSI.REPLY_CALL_TYPE.ACCEPT);
        final int screen = Utils.parseInt(handle.get(Constant.NOTIFICATION_KEY.CALLER_SCREEN));
        VoiceUtils.openScreen(application, lesson, receiveCallModel, screen);
    }
}
