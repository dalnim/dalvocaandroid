package com.dalread.util.stt;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.dalread.database.SubDatabase;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.BaseVocaList;
import com.dalread.util.Utils;
import com.dalread.util.VocaListUtil;

import java.util.List;

public abstract class SttEngine {
    protected abstract SttModel makeModel(String str);
    public abstract boolean releaseSttEngine();
    public abstract void recognizeMicrophone();
    public abstract void pause(boolean checked);
    protected abstract void initStt();

    static protected final int STATE_PREPARE = 0;
    static protected final int STATE_READY = STATE_PREPARE + 1;
    static protected final int STATE_START = STATE_READY + 1;
    static protected final int STATE_END = STATE_START + 1;

    protected SttEngineFactory.STT_ENGINE_TYPE sttEngineType = SttEngineFactory.STT_ENGINE_TYPE.VOSK;
    public OnSttEngineListener onSttEngineListener;
    public OnSttEngineStatusListener onSttEngineStatusListener;

    protected Activity activity;
    protected SubDatabase subDatabase;

    public SttEngine(Activity activity, OnSttEngineListener onSttEngineListener, OnSttEngineStatusListener onSttEngineStatusListener) {
        this.activity = activity;
        this.onSttEngineListener = onSttEngineListener;
        this.onSttEngineStatusListener = onSttEngineStatusListener;
        initData();
    }

    protected void initData() {
        setUiState(STATE_PREPARE);
        initSubDatabase();
        // Check if user has given permission to record audio, init the model after permission is granted
        setPermissionsRequestRecordAudio();
    }
    protected void setPermissionsRequestRecordAudio() {
        if (BasePermissionUtils.checkRecordAudio(activity, true)) {
            if (BasePermissionUtils.checkWriteExternalStorage(activity, true)) {
                initStt();
            }
        }
    }

    public SttEngineFactory.STT_ENGINE_TYPE getSttEngineType() {
        return sttEngineType;
    }

    @NonNull
    protected SttModel getDifficultWordAndMeaning(SttModel model) {
        if (!Utils.isEmpty(model.SMgetSentence())) {
            List<IVocaFullPlayTTSItem> allWordList = BaseVocaList.getAllWordListOfFromDB(model.SMgetSentence(), subDatabase);
            List<IVocaFullPlayTTSItem> wordList = BaseVocaList.getDifficultVocaListFromList(allWordList);
//            List<IVocaFullPlayTTSItem> wordList = subDatabase.getDifficultWordListBySentence(model.SMgetSentence());
            String difficultWords = BaseVocaKnow.getOneLineDifficultWordAndMeaning(activity, wordList);
            model.SMsetDifficultWordAndMeaning(difficultWords);
        }
        return model;
    }

    private void initSubDatabase() {
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(activity);
        subDatabase = SubDatabase.getInstance(activity, destPathWithFileName);
    }

    protected void setUiState(int state) {
        switch (state) {
            case STATE_PREPARE:
                onSttEngineStatusListener.onPrepare();
                break;
            case STATE_READY:
                onSttEngineStatusListener.onReady();
                break;
            case STATE_START:
                onSttEngineStatusListener.onStart();
                break;
            case STATE_END:
                onSttEngineStatusListener.onEnd();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    public interface OnSttEngineListener {
        void onGuideText(String text);
        void onSttPartialResult(SttModel model);
        void onSttResult(SttModel model);
        void onNoClick(Object object);
        void setErrorState(String message);
    }

    public interface OnSttEngineStatusListener {
        void onPrepare();
        void onStart();
        void onReady();
        void onPause(boolean checked);
//        void onUpdateMic();
        void onEnd();
    }
}
