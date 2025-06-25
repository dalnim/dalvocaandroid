package com.dalread.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

//Speech To Text하는 유틸(AraPlayer에 있는 SpeechRecognizer도 여기로 빼야함), 실제 녹음은 VoiceRecording을 이용하자.
public class SpeechRecognizerUtil {
    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private final SpeechRecognizerUtilListener listener;

    public interface SpeechRecognizerUtilListener {
        void onTranscriptionResults(String text);
        void onError(String message);
    }

    public SpeechRecognizerUtil(Context context, SpeechRecognizerUtilListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void startTranscription() {
        setupSpeechRecognizer();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LanguageUtil.getStudyLangLocaleString(context));
        speechRecognizer.startListening(intent);
    }

    public void stopTranscription() {
        speechRecognizer.stopListening();
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        RecognitionListener recognitionListener = new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                if (results != null) {
                    String text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                    listener.onTranscriptionResults(text);
                } else {
                    listener.onError("");
                }
            }

            // Implement other required methods
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                String errorMessage;
                switch (error) {
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        errorMessage = "STT error: " + error + " Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        errorMessage = "STT error: " + error + " Network error";
                        break;
                    case SpeechRecognizer.ERROR_AUDIO:
                        errorMessage = "STT error: " + error + " Audio recording failed";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        errorMessage = "STT error: " + error + " Server error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        errorMessage = "STT error: " + error + " Client error";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        errorMessage = "STT error: " + error + " No speech input";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage = "STT error: " + error + " No recognition result matched";
                        listener.onError(errorMessage);
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        errorMessage = "STT error: " + error + " RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        errorMessage = "STT error: " + error + " Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_TOO_MANY_REQUESTS:
                        errorMessage = "STT error: " + error + " ERROR_TOO_MANY_REQUESTS";
                        break;
                    case SpeechRecognizer.ERROR_SERVER_DISCONNECTED:
                        errorMessage = "STT error: " + error + " ERROR_SERVER_DISCONNECTED";
                        break;
                    case SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED:
                        errorMessage = "STT error: " + error + " ERROR_LANGUAGE_NOT_SUPPORTED";
                        break;
                    case SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE:
                        errorMessage = "STT error: " + error + " ERROR_LANGUAGE_UNAVAILABLE";
                        break;
                    default:
                        errorMessage = "STT error: " + error + " Unknown error";
                        break;
                }
                if (Utils.isDebug()) {
//                    ToastUtil.getInstance(context).show(errorMessage);
                }
            }


            @Override
            public void onPartialResults(Bundle partialResults) {
                int i = 0;
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                int i = 0;
            }
        };
        speechRecognizer.setRecognitionListener(recognitionListener);
    }
}
