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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;

import com.dalread.R;
import com.dalread.model.ReceiveCallModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.util.Map;

public class CallKeepModule {

    private static final String TAG = "CallKeepModule";
    private static TelecomManager telecomManager;
    private Context context;
    public static PhoneAccountHandle handle;
    private static CallKeepModule instance;

    public CallKeepModule(Context context) {
        this.context = context;
        setup();
    }


    public static CallKeepModule getInstance(Context context) {
        if (instance == null) {
            instance = new CallKeepModule(context);
        }
        return instance;
    }

    private void setup() {
        DLog.d(TAG, "setup");
        if (isConnectionServiceAvailable()) {
            this.registerPhoneAccount();
        }
    }

    public void displayIncomingCall(ReceiveCallModel receiveCallModel) {
        DLog.d(TAG, "displayIncomingCall hasPhoneAccount=" + hasPhoneAccount());
        if (!isConnectionServiceAvailable()) {
            return;
        }
        if (!hasPhoneAccount()) {
            registerPhoneAccount();
        }
        DLog.d(TAG, "displayIncomingCall: " + receiveCallModel.toString());
        Bundle extras = new Bundle();
        Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, context.getString(R.string.app_name), null);
        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
        extras.putString(Constant.NOTIFICATION_KEY.CALLER_UID, receiveCallModel.getId());
        extras.putString(Constant.NOTIFICATION_KEY.CALLER_USERNAME, receiveCallModel.getName());
        extras.putString(Constant.NOTIFICATION_KEY.LESSON_ID, receiveCallModel.getLessonId());
        extras.putString(Constant.NOTIFICATION_KEY.OPPONENT_STUDY_ROLE, receiveCallModel.getStudyRole());
        extras.putInt(Constant.NOTIFICATION_KEY.CALLER_SCREEN, receiveCallModel.getScreen());
        telecomManager.addNewIncomingCall(handle, extras);
    }

    public void answerIncomingCall(String uuid) {
        DLog.d(TAG, "answerIncomingCall");
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            return;
        }

        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }

        conn.onAnswer();
    }

    public void endCall(String uuid) {
        DLog.d(TAG, "endCall called");
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            return;
        }

        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }
        conn.onDisconnect();

        DLog.d(TAG, "endCall executed");
    }

    public void endAllCalls() {
        DLog.d(TAG, "endAllCalls called");
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            return;
        }

        Map<String, VoiceConnection> currentConnections = VoiceConnectionService.currentConnections;
        for (Map.Entry<String, VoiceConnection> connectionEntry : currentConnections.entrySet()) {
            Connection connectionToEnd = connectionEntry.getValue();
            connectionToEnd.onDisconnect();
        }

        DLog.d(TAG, "endAllCalls executed");
    }

    public void setOnHold(String uuid, boolean shouldHold) {
        DLog.d(TAG, "setOnHold");
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }

        if (shouldHold == true) {
            conn.onHold();
        } else {
            conn.onUnhold();
        }
    }

    public void reportEndCallWithUUID(String uuid, int reason) {
        DLog.d(TAG, "reportEndCallWithUUID");
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            return;
        }

        VoiceConnection conn = (VoiceConnection) VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }
        conn.reportDisconnect(reason);
    }

    public void rejectCall(String uuid) {
        DLog.d(TAG, "rejectCall");
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            return;
        }

        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }

        conn.onReject();
    }

    public void setMutedCall(String uuid, boolean shouldMute) {
        DLog.d(TAG, "setMutedCall");
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }

        CallAudioState newAudioState = null;
        //if the requester wants to mute, do that. otherwise unmute
        if (shouldMute) {
            newAudioState = new CallAudioState(true, conn.getCallAudioState().getRoute(),
                    conn.getCallAudioState().getSupportedRouteMask());
        } else {
            newAudioState = new CallAudioState(false, conn.getCallAudioState().getRoute(),
                    conn.getCallAudioState().getSupportedRouteMask());
        }
        conn.onCallAudioStateChanged(newAudioState);
    }

    public void sendDTMF(String uuid, String key) {
        DLog.d(TAG, "sendDTMF");
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }
        char dtmf = key.charAt(0);
        conn.onPlayDtmfTone(dtmf);
    }

    public void updateDisplay(String uuid, String displayName, String uri) {
        DLog.d(TAG, "updateDisplay uuid=" + uuid + " - displayName=" + displayName + " - uri=" + uri);
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }

        conn.setAddress(Uri.parse(uri), TelecomManager.PRESENTATION_ALLOWED);
        conn.setCallerDisplayName(displayName, TelecomManager.PRESENTATION_ALLOWED);
    }

    public void setCurrentCallActive(String uuid) {
        DLog.d(TAG, "setCurrentCallActive");
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            return;
        }
        conn.setConnectionCapabilities(conn.getConnectionCapabilities() | Connection.CAPABILITY_HOLD);
        conn.setActive();
    }

    public void openPhoneAccounts() {
        DLog.d(TAG, "openPhoneAccounts");
        if (!isConnectionServiceAvailable()) {
            return;
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setComponent(new ComponentName("com.android.server.telecom",
                    "com.android.server.telecom.settings.EnableAccountPreferenceActivity"));

            context.startActivity(intent);
            return;
        }
        openPhoneAccountSettings();
    }

    public void openPhoneAccountSettings() {
        DLog.d(TAG, "openPhoneAccountSettings");
        if (!isConnectionServiceAvailable()) {
            return;
        }
        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }

    public static Boolean isConnectionServiceAvailable() {
        // PhoneAccount is available since api level 23
        return Build.VERSION.SDK_INT >= 23;
    }

    public void backToForeground() {
        DLog.d(TAG, "backToForeground");
        String packageName = context.getApplicationContext().getPackageName();
        Intent focusIntent = context.getPackageManager().getLaunchIntentForPackage(packageName).cloneFilter();
        focusIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(focusIntent);
    }

    public void registerPhoneAccount() {
        DLog.d(TAG, "registerPhoneAccount");
        if (!isConnectionServiceAvailable()) {
            return;
        }
        try {
            telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            ComponentName cName = new ComponentName(context, VoiceConnectionService.class);
            String appName = this.getApplicationName(context);
            handle = new PhoneAccountHandle(cName, appName);
            if (isAndroid26()) {
                telecomManager.registerPhoneAccount(createPhoneAccountAndroid26(appName));
            }
            telecomManager.registerPhoneAccount(createPhoneAccountAndroid(appName));
            if (!hasPhoneAccount()) {
                openPhoneAccounts();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isAndroid26() {
        return android.os.Build.VERSION.SDK_INT >= 26;
    }

    public void unregisterPhoneAccount() {
        if (isAndroid26()) {
            telecomManager.unregisterPhoneAccount(handle);
        }
    }

    private PhoneAccount createPhoneAccountAndroid26(String appName) {
        return new PhoneAccount.Builder(handle, appName)
                .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
                .build();
    }

    private PhoneAccount createPhoneAccountAndroid(String appName) {
        return new PhoneAccount.Builder(handle, appName)
                .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                .build();
    }

    private String getApplicationName(Context appContext) {
        ApplicationInfo applicationInfo = appContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : appContext.getString(stringId);
    }

    public boolean hasPhoneAccount() {
        try {
            return isConnectionServiceAvailable()
                    && telecomManager != null
                    && handle != null
                    && telecomManager.getPhoneAccount(handle).isEnabled();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
