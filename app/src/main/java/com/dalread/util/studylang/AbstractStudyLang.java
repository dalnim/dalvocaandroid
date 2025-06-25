package com.dalread.util.studylang;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.DLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractStudyLang {
    private final String TAG = this.getClass().getName();
    public abstract List<String> splitSentence(String sentence);
    public abstract int getPauseTimeToRepeatTts();
    public abstract int getTTSSpeedInSetting();
    public abstract void setTTSSpeedInSetting(int value);
    protected abstract void getDefaultTtsSpeed();
    protected abstract void initEnumLanguage();
    protected Context context;
    protected SharedPreferencesDB sharedPreferences;
    protected int ttsSpeedDefault;
    private float mediaPlaySpeed = 1.0f;
    protected EnumLanguage enumStudyLanguage;

    private final float initTtsSpeed = 50f;
    AbstractStudyLang(Context context) {
        this.context = context;
        this.sharedPreferences = SharedPreferencesDB.getInstance(context);
        initEnumLanguage();
        initPlaySpeed();
    }

    private void initPlaySpeed() {
        getDefaultTtsSpeed();
        setMediaPlaySpeed();
    }

    protected void setMediaPlaySpeed() {
        int ttsSpeedInSetting = getTTSSpeedInSetting();
        mediaPlaySpeed = (float) ttsSpeedInSetting / ttsSpeedDefault;
    }

    protected void resetMedisPlaySpeed() {
        mediaPlaySpeed = 1.0f;
    }

    public int getSmartRepeatCountByVocaLength(String voca) {
        return 0;
    }

    public String aaa() {
        return enumStudyLanguage.getLetStudy();
    }
    private EnumLanguage getEnumLanguage() {
        return EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
    }

    public Locale getLocale() {
        return getEnumLanguage().getLocale();
    }

    public String getLocaleDisplayLanguage() {
        return getLocale().getDisplayLanguage();
    }

    public float getTtsSpeechRate() {
        float speechRate = getTTSSpeedInSetting() / initTtsSpeed;
        if (speechRate < .5f) {
            speechRate = .5f;
        }
        DLog.i(TAG, "speed = " + getTTSSpeedInSetting());
        DLog.i(TAG, "speechRate = " + speechRate);

        return speechRate;
    }

    public float getMediaPlaySpeed() {
        return getTtsSpeechRate();
    }


    protected enum ENUM_PAUSE_TIME_TO_REPEAT_TTS {
        ENGLISH(200),
        CHINESE(400),
        JAPANESE(350),
        KOREAN(300),
        HANJA(200);
        private int milliTime;

        ENUM_PAUSE_TIME_TO_REPEAT_TTS(int milliTime) {
            this.milliTime = milliTime;
        }

        public int milliTime() {
            return milliTime;
        }
    }

    protected List<String> splitByCharacter(String sentence) {
        List<String> characters = new ArrayList<>();
        for (int i = 0; i < sentence.length(); i++) {
            characters.add(Character.toString(sentence.charAt(i)));
        }
        return characters;
    }
}
