package com.dalread.dialog;

import android.content.Context;

import com.dalread.R;
import com.dalread.listener.OnClickDialogListener;

public class SelectResizeDialog {
    private Context context;
    private OnClickDialogListener listener;

    public SelectResizeDialog(Context context, OnClickDialogListener listener) {
        this.context = context;
        this.listener = listener;
    }


    public void show() {
        String[] RESIZE_MODE = {
                context.getString(R.string.screen_resize_fit_mode),        // Fit Mode
                context.getString(R.string.screen_resize_fixed_width_mode),
                context.getString(R.string.screen_resize_fixed_height_mode),
                context.getString(R.string.screen_resize_fill_mode),
                context.getString(R.string.screen_resize_zoom_mode)
        };
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.multi_player_resize_all_videos,
                RESIZE_MODE,
                0,
                R.string.ok,
                R.string.cancel,
                listener
        );
    }
}

