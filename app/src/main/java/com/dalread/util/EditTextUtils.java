package com.dalread.util;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.dalread.R;

public class EditTextUtils {
    public static void updateClearButtonVisibility(EditText editText) {
        Drawable drawable = null;
        if (editText.getText().length() > 0) {
            drawable = ContextCompat.getDrawable(editText.getContext(), R.drawable.ic_circle_close);
        }
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
    }

    public static void setClearButtonOnTouchListener(final EditText editText) {
        setClearButtonOnTouchListener(editText, null);
    }

    public static void setClearButtonOnTouchListener(final EditText editText, final Runnable runnable) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Drawable[] compoundDrawables = editText.getCompoundDrawables();
                    if (compoundDrawables[2] != null && event.getRawX() >= editText.getRight() - compoundDrawables[2].getBounds().width()) {
                        editText.setText("");
                        if (runnable != null) {
                            runnable.run();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static TextWatcher createTextWatcher(EditText editText) {
        EditTextUtils.setClearButtonOnTouchListener(editText);
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditTextUtils.updateClearButtonVisibility(editText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }
    //TYPE_TEXT_VARIATION_VISIBLE_PASSWORD를 추가하니 단어 힌트는 안보여주는데, 쓰기 모드등도 안되어 버린다.
    public static void showOrHideSuggestionsInKeyboardForMultiLine(EditText editText, boolean suggestionsEnabled) {
        int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
        if (suggestionsEnabled) {
            inputType |= InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
        } else {
            inputType |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        }
        editText.setInputType(inputType);
    }
}
