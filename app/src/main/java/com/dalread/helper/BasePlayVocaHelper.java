package com.dalread.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.io.File;
import java.util.Locale;

public class BasePlayVocaHelper {

    private Context context;
    private MediaPlayer mediaPlayer;
    private TextToSpeech textToSpeech;

    public BasePlayVocaHelper(Context context) {
        this.context = context;
    }

    public void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void initTTS(final Locale locale, final int speed) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(locale);
                    setTTSSpeed(speed);
                } else {
                    textToSpeech = null;
                }
            }
        });
    }

    private void setTTSSpeed(int speed) {
        if (textToSpeech != null) {
            float speechRate = speed / 50f;
            if (speechRate < .5f) {
                speechRate = .5f;
            }
            String tag = getClass().getSimpleName();
            DLog.i(tag, "speed = " + speed);
            DLog.i(tag, "speechRate = " + speechRate);
            textToSpeech.setSpeechRate(speechRate);
        }
    }

    public boolean play(File file, final MediaPlayer.OnCompletionListener listener) {
        if (mediaPlayer == null || file == null || !file.exists())
            return false;

        try {
            Uri uri = Uri.fromFile(file);
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp != null) {
                        mp.reset();
                    }
                    if (listener != null) {
                        listener.onCompletion(mp);
                    }
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void play(String text, String utteranceId, final UtteranceProgressListener listener) {
        if (textToSpeech == null) {
            if (listener != null) {
                listener.onError(utteranceId);
            }
        } else {
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    if (listener != null) {
                        listener.onStart(utteranceId);
                    }
                }

                @Override
                public void onDone(String utteranceId) {
                    if (listener != null) {
                        listener.onDone(utteranceId);
                    }
                    textToSpeech.setOnUtteranceProgressListener(null);
                }

                @Override
                public void onError(String utteranceId) {
                    if (listener != null) {
                        listener.onError(utteranceId);
                    }
                    textToSpeech.setOnUtteranceProgressListener(null);
                }
            });
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            textToSpeech.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, utteranceId);
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    public void destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }
}
