package com.dalread.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.SingleChoiceWithMessageDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnYesNoClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogUtil {
  public static void showCopyTextDialog(final Context context, final String text) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, AraThemeUtil.getMaterialAlertDialogResId())
            .setMessage(text)
            .setCancelable(true)
            .setPositiveButton(context.getString(R.string.close), null)
            .setNegativeButton(context.getString(R.string.copy), (dialog, which) -> {
              CopyTextUtil.copyToClipboard(context, text, R.string.copied);
            });
//    builder.show();
    AlertDialog dialog = builder.create();
    setColor(context, dialog);
    dialog.show();
  }

  private static void setColor(Context context, AlertDialog dialog) {
    if (AppFlavorUtil.isAraMultiPlayerApp()) {
      dialog.setOnShowListener(d -> {
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        TextView messageTextView = dialog.findViewById(android.R.id.message);
        TextView titleTextView = dialog.findViewById(androidx.appcompat.R.id.alertTitle);
        if (titleTextView != null) {
          titleTextView.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryWhiteColor));
        }
        if (messageTextView != null) {
          messageTextView.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryWhiteColor));
        }
        if (positiveButton != null) {
          positiveButton.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryWhiteColor));
        }
        if (negativeButton != null) {
          negativeButton.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryWhiteColor));
        }
      });
    }
  }

  public static void showCopyTextDialog(final Context context, final String title, final String message) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, AraThemeUtil.getMaterialAlertDialogResId())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.close), null)
                .setNegativeButton(context.getString(R.string.copy), (dialog, which) -> {
                    CopyTextUtil.copyToClipboard(context, message, R.string.copied);
                });
    AlertDialog dialog = builder.create();
    setColor(context, dialog);
    dialog.show();
//        builder.show();
    }
    public static void showRepeatCountDialog(Context context, int messageId, OnClickDialogListener listener) {
        final String[] readCountValues = BaseVoca.getRepeatCountValues(context);
        int index = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.COUNT_OF_SMART_REPEAT;
        SingleChoiceWithMessageDialog singleChoiceWithMessageDialog = new SingleChoiceWithMessageDialog(context);
        singleChoiceWithMessageDialog.show(
                R.string.repeat_count_per_dialog,
                messageId,
                readCountValues,
                index,
                R.string.select,
                R.string.cancel,
                listener);
    }
    public static void askToDeleteDialogInSubtitle(Context context, OnYesNoClickListener yesNoClickListener) {
        final YesNoDialog dialog = new YesNoDialog(context, R.string.confirm, R.string.msg_delete_selected_subtitle, null, yesNoClickListener);
        dialog.show();
    }
    public static void showPositiveDialog(Context context, @StringRes int title, String message, @StringRes int positiveButtonText, Runnable onPositiveButtonClick) {
        showPositiveDialog(context, context.getString(title), message, context.getString(positiveButtonText), onPositiveButtonClick);
    }
    public static void showPositiveDialog(Context context, @StringRes int title, @StringRes int message, @StringRes int positiveButtonText, Runnable onPositiveButtonClick) {
        showPositiveDialog(context, context.getString(title), context.getString(message), context.getString(positiveButtonText), onPositiveButtonClick);
    }
    public static void showPositiveDialog(Context context, String title, String message, String positiveButtonText, Runnable onPositiveButtonClick) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, AraThemeUtil.getMaterialAlertDialogResId())
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButtonText, ((dialog, which) -> {
                    onPositiveButtonClick.run();
                }));

        if (title != null && !title.isEmpty()) {
            builder.setTitle(title);
        }
//      builder.show();
      AlertDialog dialog = builder.create();
      setColor(context, dialog);
      dialog.show();

    }


    public interface OpenGptFromHomeViewListener {
        public void selectedMenu(int menuIndex);
    }
    public static void openGptFromAraConvHomeView(Context context, OpenGptFromHomeViewListener listener) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_open_gpt_from_araconv_home_view);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.dialog_title_open_gpt_from_araconv_home_view,
                displayOptions,
                1,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int)object;
                        if (listener != null) {
                            listener.selectedMenu(which);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    public static void openGptFromAraConvConversationView(Context context, OpenGptFromHomeViewListener listener) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_open_gpt_from_araconv_conversation_view);
        int checkedItem = SharedPreferencesDB.getInstance(context).getChoiceOpenGptFromAraConvConversationView();
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.dialog_title_open_gpt_from_araconv_conversation_view,
                displayOptions,
                checkedItem,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int)object;
                        SharedPreferencesDB.getInstance(context).setChoiceOpenGptFromAraConvConversationView(which);
                        if (listener != null) {
                            listener.selectedMenu(which);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }
}
