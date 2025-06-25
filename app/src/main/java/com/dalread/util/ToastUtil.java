package com.dalread.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.widget.Toast;

import com.dalread.R;

public class ToastUtil {

    private Context context;
    private static ToastUtil instance;
    private Toast toast;
    Handler handler = new Handler(Looper.getMainLooper());

    public static ToastUtil getInstance(Context context) {
        if (instance == null)
            instance = new ToastUtil(context);
        return instance;
    }

    public ToastUtil(Context context) {
        this.context = context;
    }

    public void show(int id) {
        show(context.getString(id));
    }

    public void show(String message) {
        if (Utils.isEmpty(message)) {
            return;
        }
        handler.postDelayed(() -> {
            hide();
            Spannable centeredText = new SpannableString(message);
            centeredText.setSpan(
                    new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                    0, message.length() - 1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
            );

            toast = Toast.makeText(context, centeredText, Toast.LENGTH_SHORT);
            toast.show();
        }, 100);
    }

    private void hide() {
        if (toast != null) {
            toast.cancel();
        }
    }

    public void showErrorNetwork() {
        show(R.string.msg_no_internet);
    }

    public void clearInstance() {
        instance = null;
    }
}
