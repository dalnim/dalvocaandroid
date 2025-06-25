package com.dalread.util.stt;

import android.app.Activity;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class SttEngineFactory {
    public enum STT_ENGINE_TYPE {
        VOSK(0, "vosk"),
        GOOGLE_CLOUD(1, "google cloud");
        private int index;
        private String name;
        STT_ENGINE_TYPE(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }

    @NonNull
    @Contract("_, _, _, _ -> new")
    public static SttEngine getSttEngine(STT_ENGINE_TYPE type, Activity activity, SttEngine.OnSttEngineListener onSttEngineListener, SttEngine.OnSttEngineStatusListener onSttEngineStatusListener){
        if (type == STT_ENGINE_TYPE.GOOGLE_CLOUD){
            return new SttEngineGoogleCloud(activity, onSttEngineListener, onSttEngineStatusListener);
        }
        return new SttEngineVosk(activity, onSttEngineListener, onSttEngineStatusListener);
    }
}
