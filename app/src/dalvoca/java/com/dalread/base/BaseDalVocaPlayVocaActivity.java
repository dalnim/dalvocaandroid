package com.dalread.base;

import android.content.DialogInterface;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.model.Lesson;
import com.dalread.model.ReceiveCallModel;
import com.dalread.service.callkeep.VoiceUtils;
import com.dalread.util.DLog;

public abstract class BaseDalVocaPlayVocaActivity extends PlayVocaActivity {

    public void showAdminDialog(BaseApplication application,
                                Lesson lesson,
                                ReceiveCallModel receiveCallModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createDialogAdminReceived(application, lesson, receiveCallModel);
            }
        });
    }

    public void createDialogAdminReceived(BaseApplication application,
                                          Lesson lesson,
                                          ReceiveCallModel receiveCallModel) {
        DLog.d(getLogTag(), "createDialogAdminReceived - " + receiveCallModel.toString());
        try {
            if (adminDialog != null && adminDialog.isShowing()) {
                adminDialog.dismiss();
            }
            adminDialog = new ConfirmationDialog(this, R.string.voice_call_observer_receive_calling_title,
                    application.getCurrentActivity().getString(R.string.voice_call_observer_receive_calling_msg, receiveCallModel.getBody()),
                    R.string.voice_call_observer_receive_calling_btn_no,
                    R.string.voice_call_observer_receive_calling_btn_yes,
                    new ConfirmationDialog.OnDialogClickListener() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(DialogInterface dialog) {
                            VoiceUtils.openScreen(application, lesson, receiveCallModel, receiveCallModel.getScreen());
                            dialog.dismiss();
                        }
                    });
            if (adminDialog != null) {
                adminDialog.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}