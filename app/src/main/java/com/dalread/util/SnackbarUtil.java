package com.dalread.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dalread.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarUtil {
    private Context context;
    private static SnackbarUtil instance;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Snackbar currentSnackbar;

    public static SnackbarUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SnackbarUtil(context);
        }
        return instance;
    }

    private SnackbarUtil(Context context) {
        this.context = context;
    }

    public void show(int resId) {
        show(context.getString(resId));
    }

    public void show(String message) {
        if (Utils.isEmpty(message)) {
            return;
        }

        handler.postDelayed(() -> {
            hide();
            Spannable centeredText = new SpannableString(message);
            centeredText.setSpan(
                    new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL),
                    0, message.length() - 1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
            );

            currentSnackbar = Snackbar.make(
                    ((Activity) context).findViewById(android.R.id.content),
                    centeredText,
                    Snackbar.LENGTH_INDEFINITE
            );
            // Set max lines
            TextView snackbarText = currentSnackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            snackbarText.setMaxLines(5);
            snackbarText.setEllipsize(TextUtils.TruncateAt.END);

            currentSnackbar.setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hide();
                }
            });
            currentSnackbar.show();
        }, 100);
    }

    private void hide() {
        if (currentSnackbar != null) {
            currentSnackbar.dismiss();
        }
        currentSnackbar = null;
    }
}

