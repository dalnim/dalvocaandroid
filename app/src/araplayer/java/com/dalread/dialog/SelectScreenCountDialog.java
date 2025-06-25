package com.dalread.dialog;

import android.content.Context;

import com.dalread.R;
import com.dalread.base.EnumMultiplePlayer;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.DialogUtil;

import java.util.Arrays;

public class SelectScreenCountDialog {
    private Context context;
    private OnClickDialogListener listener;
    private String[] displayNumberOfScreens;
    private SharedPreferencesDB sharedPreferencesDB;
    public SelectScreenCountDialog(Context context, String[] displayNumberOfScreens, OnClickDialogListener listener) {
        this.context = context;
        this.displayNumberOfScreens = displayNumberOfScreens;
        this.listener = listener;
        this.sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
    }

    public void show() {
        if (sharedPreferencesDB.isFirstShowAlertSelectManyScreenCount()) {
            sharedPreferencesDB.setFirstShowAlertSelectManyScreenCount();
            DialogUtil.showPositiveDialog(context, context.getString(R.string.info), context.getString(R.string.dialog_message_select_too_many_screen_count), context.getString(R.string.ok), () -> showDialog());
        } else {
            showDialog();
        }

    }
    private void showDialog() {
        // displayNumberOfScreens와 selectedNumberOfScreenPos를 내부에서 설정
        EnumMultiplePlayer enumMultiplePlayer = EnumMultiplePlayer.getEnum(sharedPreferencesDB.getMultiPlayerScreenCount());
        final int selectedNumberOfScreenPos = Arrays.asList(displayNumberOfScreens).indexOf(String.valueOf(enumMultiplePlayer.getNumberOfScreen()));
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.choose_number_of_screens,
                displayNumberOfScreens,
                selectedNumberOfScreenPos,
                R.string.ok,
                R.string.cancel,
                listener
        );
    }
}
