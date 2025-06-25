package com.dalread.composition;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.dalread.util.LanguageUtil;
import com.dalread.util.studylang.AbstractStudyLang;
import com.dalread.util.studylang.StudyLangFactory;
//이건 AraConv에서 영어회화시에 사용한다
public class NormalTts {
    private AbstractStudyLang studyLang;
    private TextToSpeech tts;
    private boolean isReady = false;
    private Context context;
    private OnTtsCompleteListener listener;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String textToSpeak = "";
    private boolean isPlaying = false;
    public NormalTts(Context context, OnTtsCompleteListener listener) {
        this.context = context;
        this.listener = listener;
        initTts(context, listener);
        initListener(listener);
        initStudyLang();
    }

    private void initStudyLang() {
        studyLang = StudyLangFactory.create(context);
    }
    private void initListener(OnTtsCompleteListener listener) {
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                updateIsPlayingStatus(true);
                if (listener != null) {
                    listener.onTtsStart();
                }
            }

            @Override
            public void onDone(String utteranceId) {
                if (utteranceId.equals(UtteranceId.REPEAT.name())) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speak(textToSpeak, false);
                        }
                    }, 1000);
                } else {
                    updateIsPlayingStatus(false);
                    if (listener != null) {
                        listener.onTtsComplete();
                    }
                }
            }

            @Override
            public void onError(String utteranceId) {
                updateIsPlayingStatus(false);
                if (listener != null) {
                    listener.onTtsError();
                }
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                updateIsPlayingStatus(false);
            }
        });
    }

    private void initTts(Context context, OnTtsCompleteListener listener) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    updateSpeechRate();
                    setLanguageToTts();
                    isReady = true;
                    if (listener != null) {
                        listener.onTtsInit();
                    }
                }
            }
        });
    }
    //TTS의 목소리를 영어, 한국어등으로 설정해준다.
    private void setLanguageToTts() {
        tts.setLanguage(LanguageUtil.getStudyLangLocale(context));
    }

    public boolean isReady() {
        return isReady;
    }

    public boolean isPlaying() {
//        return tts.isSpeaking(); //이건 연속 재생시 1초 정도 쉴때는 playing이 아니라고 하기 때문에 쓰면 안됨.
        return isPlaying;
    }
    public void updateSpeechRate() {
        tts.setSpeechRate(studyLang.getTtsSpeechRate());
    }
    public void speak(String textToSpeak, boolean isPlayOnce) {
        this.textToSpeak = textToSpeak;
        updateSpeechRate();
        if (isReady()) {

            if (isPlayOnce) {
                tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, UtteranceId.SPEAK_ONCE.name());
            } else {
                tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, UtteranceId.REPEAT.name());
            }
        }
    }
    private void updateIsPlayingStatus(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
    public void stop() {
        if (isPlaying()) {
            updateIsPlayingStatus(false);
            tts.stop();
        }
    }

    public void resume() {
        if (isReady() && !isPlaying()) {
            isPlaying = true;
            speak(textToSpeak, false);
        }
    }

    public void shutdown() {
        tts.stop();
        tts.shutdown();
        handler.removeCallbacksAndMessages(null);
    }
    public enum UtteranceId {
        SPEAK_ONCE,
        REPEAT
    }

    public interface OnTtsCompleteListener {
        void onTtsInit();

        void onTtsStart();

        void onTtsComplete();

        void onTtsError();
    }
}
