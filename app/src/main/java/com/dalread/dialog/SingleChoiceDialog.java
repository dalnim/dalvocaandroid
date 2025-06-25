package com.dalread.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.dalread.R;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;
import com.dalread.util.Utils;

import butterknife.BindDimen;

public class SingleChoiceDialog implements DialogInterface.OnDismissListener {
    private final AlertDialog.Builder builder;
    private Context context;
    private int pos;
    private OnClickDialogListener listener;
    @BindDimen(R.dimen.dialog_horizontal_margin)
    int horizontalMargin;
    @BindDimen(R.dimen.dialog_vertical_margin)
    int verticalMargin;
    private boolean isRightHandMode = true;

    public SingleChoiceDialog(Context context) {
        this.context = context;
        builder = new AlertDialog.Builder(context, AraThemeUtil.getSingleChoiceDialogThemeResId());
    }

    public void show(@StringRes int titleId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, final OnClickDialogListener listener) {
        show(titleId, items, checkedItem, positiveId, negativeId, false, listener);
    }

    public void show(@StringRes int titleId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, boolean useNegativeCallback, final OnClickDialogListener listener) {
        AlertDialog dialog = initDialog(titleId, items, checkedItem, positiveId, negativeId, useNegativeCallback, listener);
        dialog.show();
        if (!isRightHandMode) {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            ViewGroup parentView = (ViewGroup) positiveBtn.getParent();
            if (parentView instanceof LinearLayout) {
                if (parentView.getChildCount() > 2) {
                    // Following this link: https://chromium.googlesource.com/android_tools/+/25d57ead05d3dfef26e9c19b13ed10b0a69829cf/sdk/platforms/android-23/data/res/layout/alert_dialog_button_bar_material.xml
                    // Child view at index 1 is Space, it has 0dp width, 0dp height and the weight is 1.
                    parentView.getChildAt(1).setVisibility(View.GONE);
                }
                ((LinearLayout) parentView).setGravity(Gravity.START);
            }
        }
        Utils.setDialogSizeWider(context, dialog);
    }

    public void showWrapContentHeight(@StringRes int titleId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, final OnClickDialogListener listener) {
        AlertDialog dialog = initDialog(titleId, items, checkedItem, positiveId, negativeId, false, listener);
        dialog.show();
    }

    private AlertDialog initDialog(@StringRes int titleId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, boolean useNegativeCallback, final OnClickDialogListener listener){
        this.listener = listener;
        pos = checkedItem;

        // Create a TextView for the dialog's title and set its text and styling
        TextView titleTextView = new TextView(context);
        titleTextView.setText(titleId);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        AraThemeUtil.setTextColor(context, titleTextView, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textPrimaryWhiteColor : R.color.textPrimaryColor);
//        titleTextView.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryColor));
        titleTextView.setPadding(16, 16, 16, 16);
//        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);

//        // Add a gray divider below the title TextView
//        View dividerView = new View(context);
//        dividerView.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundGrayColor));
//        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.convertDpToPx(context, 1));
//        dividerView.setLayoutParams(dividerParams);

        // Add the title TextView and divider to a layout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(titleTextView);
//        layout.addView(dividerView);

        AlertDialog dialog = builder.setCustomTitle(layout)
                .setSingleChoiceItems(items, checkedItem, (dialog1, which) -> pos = which)
                .setPositiveButton(positiveId, (dialog2, which) -> {
                    dialog2.dismiss();
                    if (pos >= 0 && listener != null) {
                        listener.onClick(null, pos);
                    }
                })
                .setNegativeButton(negativeId, (dialog3, which) -> {
                    dialog3.dismiss();
                    if (useNegativeCallback && listener != null) {
                        listener.onClick(null, -1);
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(this);
        return dialog;
    }


    private AlertDialog initDialog1(@StringRes int titleId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, boolean useNegativeCallback, final OnClickDialogListener listener){
        this.listener = listener;
        pos = checkedItem;
        AlertDialog dialog = builder.setTitle(titleId)
                .setSingleChoiceItems(items, checkedItem, (dialog1, which) -> pos = which)
                .setPositiveButton(positiveId, (dialog2, which) -> {
                    dialog2.dismiss();
                    if (pos >= 0 && listener != null) {
                        listener.onClick(null, pos);
                    }
                })
                .setNegativeButton(negativeId, (dialog3, which) -> {
                    dialog3.dismiss();
                    if (useNegativeCallback && listener != null) {
                        listener.onClick(null, -1);
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(this);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(null, -1);
        }
    }

    public void setRightHandMode(boolean rightHandMode) {
        isRightHandMode = rightHandMode;
    }
}
