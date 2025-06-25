package com.dalread.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.dalread.activity.PopupSubtitleTranslateDialog;
import com.dalread.listener.OnClickDialogListener;

public class TranslateUtil {
    public static void openWebTranslate(Context context, String text, OnDismissPopupSubtitleTranslateDialogListener listener) {
        if (NetworkUtil.isNetworkConnetedIfNotShowWarningAsPopup(context)) {
            final PopupSubtitleTranslateDialog dialog = new PopupSubtitleTranslateDialog(context, text, new OnClickDialogListener() {
                @Override
                public void onClick(View view, Object object) {
                    // Do something on click
                }

                @Override
                public void onDismiss(View view, Object object) {
                    listener.onDismissPopupSubtitleTranslateDialog();
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    listener.onDismissPopupSubtitleTranslateDialog();
                }
            });
            dialog.show();
        }
    }

    public interface OnDismissPopupSubtitleTranslateDialogListener {
        void onDismissPopupSubtitleTranslateDialog();
    }
}
