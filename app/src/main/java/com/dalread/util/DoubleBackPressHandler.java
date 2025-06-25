package com.dalread.util;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.dalread.R;

public class DoubleBackPressHandler {

    private boolean backPressedOnce = false;
    private Toast backToast;
    private final Activity activity;
    private final Handler handler = new Handler();
    private String message = "";
    private final Runnable resetBackPressedOnce = new Runnable() {
        @Override
        public void run() {
            backPressedOnce = false;
        }
    };

    public DoubleBackPressHandler(Activity activity) {
        this.activity = activity;
        this.message = activity.getString(R.string.toast_double_press_back_button_to_exit_app);
    }

    public DoubleBackPressHandler(Activity activity, String message) {
        this.activity = activity;
        this.message = message;
    }

    public void onBackPressed() {
        if (backPressedOnce) {
            if (backToast != null) backToast.cancel();
            activity.finish();
            return;
        }

        this.backPressedOnce = true;
        backToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        backToast.show();

        handler.postDelayed(resetBackPressedOnce, 2000); // 2 seconds to press back again
    }
}
