package com.dalread.util.studylang;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.util.Constant;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudyLangKorean extends AbstractStudyLang {

    StudyLangKorean(Context context) {
        super(context);
    }

    @Override
    protected void initEnumLanguage() {
        enumStudyLanguage = EnumLanguage.KOREAN;
    }
    @Override
    protected void getDefaultTtsSpeed() {
        this.ttsSpeedDefault = Constant.SETTINGS.SETTING_TTS_SPEED_KOREAN;
    }
    public int getSmartRepeatCountByVocaLength(String voca) {
        int smartRepeatCount = 0;
        if (Utils.isEmpty(voca))
            return smartRepeatCount;

        int vocaLength = voca.length();
        if (vocaLength > 12) {
            smartRepeatCount = 3;
        } else if (vocaLength > 6) {
            smartRepeatCount = 2;
        } else {
            smartRepeatCount = 1;
        }
        return smartRepeatCount;
    }
    @Override
    public int getTTSSpeedInSetting() {
        return sharedPreferences.getSettingTTSSpeedKorean();
    }
    @Override
    public void setTTSSpeedInSetting(int value) {
        sharedPreferences.setSettingTTSSpeed(value);
        sharedPreferences.setSettingTTSSpeedKorean(value);
    }
    @Override
    public int getPauseTimeToRepeatTts() {
        return ENUM_PAUSE_TIME_TO_REPEAT_TTS.KOREAN.milliTime();
    }

    @Override
    public List<String> splitSentence(String sentence) {
        return splitByCharacter(StringUtils.getOnlyKorean(sentence));
    }
}
