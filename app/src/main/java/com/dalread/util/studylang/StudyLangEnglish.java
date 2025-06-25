package com.dalread.util.studylang;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.util.Constant;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudyLangEnglish extends AbstractStudyLang {

    StudyLangEnglish(Context context) {
        super(context);
    }
    @Override
    protected void initEnumLanguage() {
        enumStudyLanguage = EnumLanguage.ENGLISH;
    }
    @Override
    protected void getDefaultTtsSpeed() {
        this.ttsSpeedDefault = Constant.SETTINGS.SETTING_TTS_SPEED_ENGLISH;
    }
    @Override
    public int getSmartRepeatCountByVocaLength(String voca) {
        int smartRepeatCount = 0;
        if (Utils.isEmpty(voca))
            return smartRepeatCount;

        int spaceCount = (int) voca.codePoints().filter(e -> e == ' ').count();
        if (spaceCount > 10) {
            smartRepeatCount = 3;
        } else if (spaceCount > 10) {
            smartRepeatCount = 2;
        } else {
            smartRepeatCount = 1;
        }
        return smartRepeatCount;
    }

    @Override
    public int getTTSSpeedInSetting() {
        return sharedPreferences.getSettingTTSSpeedEnglish();
    }
    @Override
    public void setTTSSpeedInSetting(int value) {
        sharedPreferences.setSettingTTSSpeed(value);
        sharedPreferences.setSettingTTSSpeedEnglish(value);
    }

    @Override
    public int getPauseTimeToRepeatTts() {
        return ENUM_PAUSE_TIME_TO_REPEAT_TTS.ENGLISH.milliTime();
    }

    @Override
    public List<String> splitSentence(String sentence) {
        return Arrays.asList(sentence.split(" "));
    }
}
