package com.dalread.util;

import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.TextView;

public class TextViewUtil {

    public static void makeTextViewEndTruncate(final TextView tv, final int maxLine) {
        makeTextViewResizable(tv, maxLine, TextUtils.TruncateAt.END);
    }

    public static void makeTextViewMiddleTruncate(final TextView tv, final int maxLine) {
        makeTextViewResizable(tv, maxLine, TextUtils.TruncateAt.MIDDLE);
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, TextUtils.TruncateAt ellipsize) {
        final String expandText = ".....";
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                if (tv.getLineCount() > maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    CharSequence content = tv.getText();
                    String text = Constant.BASE_BLANK;
                    if (ellipsize == TextUtils.TruncateAt.END) {
                        text = content.subSequence(0, lineEndIndex - (expandText.length() * 2)) + expandText
                                + content.subSequence(content.length() - expandText.length(), content.length());
                    } else if (ellipsize == TextUtils.TruncateAt.MIDDLE) {
                        final int midIndex = (lineEndIndex / 2) - expandText.length();
                        text = content.subSequence(0, midIndex) + expandText
                                + content.subSequence(content.length() - midIndex,  content.length());
                    }
                    tv.setText(text);
                }
            }
        });
    }
}
