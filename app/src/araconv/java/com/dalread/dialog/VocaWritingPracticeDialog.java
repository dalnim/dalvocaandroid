package com.dalread.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;

public class VocaWritingPracticeDialog extends AlertDialog {
    private Context context;
    private IVocaFullPlayTTSItem voca;
    private final com.dalread.listener.OnClickListener listener;
    private Button buttonCancel;
    private Button buttonOk;
    private EditText editText;
    private TextView tvSentenceStudyLang;
    private TextView tvSentenceMotherTongue;
    public VocaWritingPracticeDialog(@NonNull Context context, IVocaFullPlayTTSItem voca, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.context = context;
        this.voca = voca;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_voca_writing_practice, null);
        setContentView(dialogView);

        buttonCancel = dialogView.findViewById(R.id.button_cancel);
        buttonOk = dialogView.findViewById(R.id.button_ok);
        editText = dialogView.findViewById(R.id.edit_text);
        tvSentenceStudyLang = dialogView.findViewById(R.id.tvSentenceStudyLang);
        tvSentenceMotherTongue = dialogView.findViewById(R.id.tvSentenceMotherTongue);

        tvSentenceStudyLang.setText(voca.getVIVoca());
        tvSentenceMotherTongue.setText(voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onClick(v, "");
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something with the user input
                String userInput = editText.getText().toString();
                // ...
                dismiss();
                listener.onClick(v, "");
            }
        });
        tvSentenceStudyLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSentenceStudyLang.setText(voca.getVIVoca());
//                tvSentenceStudyLang.setTextColor(ContextCompat.getColor(getContext(), R.color.textPrimaryColor));
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvSentenceStudyLang.setText("글자를 볼려면 여기를 탭하세요.");
//                tvSentenceStudyLang.setTextColor(ContextCompat.getColor(getContext(), R.color.transparent));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int typedLength = s.length();
                String expectedText = voca.getVIVoca();

                if (typedLength > expectedText.length()) {
                    s.delete(typedLength - 1, typedLength);
                } else if (typedLength > 0) {
                    char lastTypedChar = s.charAt(typedLength - 1);
                    char expectedChar = expectedText.charAt(typedLength - 1);

                    if (Character.toLowerCase(lastTypedChar) == Character.toLowerCase(expectedChar)) {
                        if (lastTypedChar != expectedChar) {
                            // Temporarily remove TextWatcher to avoid infinite loop
                            editText.removeTextChangedListener(this);
                            s.replace(typedLength - 1, typedLength, String.valueOf(expectedChar));
                            // Add TextWatcher back after updating text
                            editText.addTextChangedListener(this);
                        }
                    } else if (lastTypedChar == ' ' && isSpecialCharacter(expectedChar)) {
                        s.replace(typedLength - 1, typedLength, String.valueOf(expectedChar));
                    } else {
                        DLog.d("EditText", "Incorrect character: " + lastTypedChar);
                        s.delete(typedLength - 1, typedLength);
                    }
                }
            }
        });

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public boolean isSpecialCharacter(char c) {
        return String.valueOf(c).matches(Constant.PATTERN_SPECIAL_CHARACTERS);
    }
}
