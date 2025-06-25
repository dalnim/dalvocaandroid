package com.dalread.helper;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import com.dalread.base.EnumLanguage;
import com.dalread.util.Constant;

import java.util.Locale;

public class StudyChatPlayVocaHelper {

    private Context context;
    private TextToSpeech motherTongueTTS;
    private UtteranceProgressListener motherTongueListener;
    private OnInitListener onInitListener;

    public StudyChatPlayVocaHelper(Context context) {
        this.context = context;
    }

    public void initMotherTongueTTS(EnumLanguage enumLanguage) {
        initMotherTongueTTS(enumLanguage.getLocale());
    }

    public void initMotherTongueTTS(final Locale locale) {
        motherTongueTTS = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                motherTongueTTS.setLanguage(locale);
                motherTongueTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        if (motherTongueListener != null) {
                            motherTongueListener.onStart(utteranceId);
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (motherTongueListener != null) {
                            motherTongueListener.onDone(utteranceId);
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        if (motherTongueListener != null) {
                            motherTongueListener.onError(utteranceId);
                        }
                    }
                });
                if (onInitListener != null) {
                    onInitListener.onMotherTongueSuccess();
                }
            } else {
                motherTongueTTS = null;
            }
        });
    }

    public void setMotherTongueListener(UtteranceProgressListener listener) {
        motherTongueListener = listener;
    }

    public void setOnInitListener(OnInitListener onInitListener) {
        this.onInitListener = onInitListener;
    }

    public void play(final String id, final String vocaMeaning) {
        if (motherTongueTTS != null) {
            if (TextUtils.isEmpty(vocaMeaning)) {
                motherTongueTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_FLUSH, id);
            } else {
                motherTongueTTS.speak(vocaMeaning, TextToSpeech.QUEUE_FLUSH, null, id);
                motherTongueTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    public void stop() {
        if (motherTongueTTS != null) {
            motherTongueTTS.stop();
        }
    }

    public void onDestroy() {
        if (motherTongueTTS != null) {
            motherTongueTTS.stop();
            motherTongueTTS.shutdown();
            motherTongueTTS = null;
        }
    }

    public interface OnInitListener {

        void onMotherTongueSuccess();
    }
}
