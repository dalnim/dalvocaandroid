package com.dalread.util.stt;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.dalread.util.DLog;
import com.dalread.util.Utils;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;

public class SttEngineVosk extends SttEngine implements RecognitionListener {


    private Model currentModel;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;


    public SttEngineVosk(Activity activity, OnSttEngineListener onSttEngineListener, OnSttEngineStatusListener onSttEngineStatusListener) {
        super(activity, onSttEngineListener, onSttEngineStatusListener);
    }

    @Override
    protected void initData() {
        sttEngineType = SttEngineFactory.STT_ENGINE_TYPE.VOSK;
        LibVosk.setLogLevel(LogLevel.INFO);

        super.initData();
    }

    @Override
    protected void initStt() {
        initModel();
    }


    private void initModel() {
        StorageService.unpack(activity, "model-en-us", "model",
                (model) -> {
                    this.currentModel = model;
                    setUiState(STATE_READY);
                },
                (exception) -> DLog.d("","Failed to unpack the model" + exception.getMessage()));
    }


    @Override
    protected SttModel makeModel(String str) {
        return SttModelVosk.withSttResult(str);
    }

    private void updateResultText(String hypothesis) {
        SttModel model = getDifficultWordAndMeaning(makeModel(hypothesis));
        if (!Utils.isEmpty(model.SMgetSentence())) {
            onSttEngineListener.onSttResult(model);
        }
    }

    @Override
    public void onPartialResult(String hypothesis) {
        SttModel model = getDifficultWordAndMeaning(makeModel(hypothesis));
        if (!Utils.isEmpty(model.SMgetSentence())) {
            onSttEngineListener.onSttPartialResult(model);
        }
    }

    @Override
    public void onResult(String hypothesis) {
        updateResultText(hypothesis);
    }

    @Override
    public void onFinalResult(String hypothesis) {
        updateResultText(hypothesis);
//        setUiState(STATE_END);
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onError(@NonNull Exception exception) {
        onSttEngineListener.setErrorState(exception.getMessage());
    }

    @Override
    public void onTimeout() {
        setUiState(STATE_END);
    }

    @Override
    public boolean releaseSttEngine() {
        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }

        if (speechStreamService != null) {
            speechStreamService.stop();
        }
        return true;
    }

    @Override
    public void recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_END);
            speechService.stop();
            speechService = null;
        } else {
            setUiState(STATE_START);
            try {
                Recognizer rec = new Recognizer(currentModel, 16000.0f);
                rec.setWords(true);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
            } catch (IOException e) {
                onSttEngineListener.setErrorState(e.getMessage());
            } catch (Exception e) {
                onSttEngineListener.setErrorState(e.getMessage());
            }
        }
    }

    @Override
    public void pause(boolean checked) {
        if (speechService != null) {
            speechService.setPause(checked);
            onSttEngineStatusListener.onPause(checked);
        }
    }
}
