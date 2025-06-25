package com.dalread.network;

import com.dalread.util.Constant;

public enum GptUserOption {
    BEGINNER(0, Constant.SIGN_UP_LEVEL_BEGINNER, 1000, 200, 0.8),
    PRE_INTERMEDIATE(1, Constant.SIGN_UP_LEVEL_PRE_INTERMEDIATE, 2000, 100, 0.8),
    INTERMEDIATE(2, Constant.SIGN_UP_LEVEL_INTERMEDIATE, 2000, 100, 0.9),
    POST_INTERMEDIATE(3, Constant.SIGN_UP_LEVEL_POST_INTERMEDIATE, 2000, 100, 0.9),
    ADVANCED(4, Constant.SIGN_UP_LEVEL_ADVANCED, 100000, 50, 0.9);

    private final int index;
    private final String studentLangLevel; //회원가입시의 나의 단어 레벨과 같은 값을 사용.
    private final int maxResponseTokens;
    private final int gptTypingSpeedInConsole;
    private final double temperature; //gpt가 생성하는 문장이 0에 가까우면 같은 표현이 나오고, 1에 가까우면 다른 표현이 나온다.

    GptUserOption(int index, String studentLangLevel, int maxResponseTokens, int gptTypingSpeedInConsole, double temperature) {
        this.index = index;
        this.studentLangLevel = studentLangLevel;
        this.maxResponseTokens = maxResponseTokens;
        this.gptTypingSpeedInConsole = gptTypingSpeedInConsole;
        this.temperature = temperature;
    }

    public int getMaxResponseTokens() {
        return maxResponseTokens;
    }

    public int getGptTypingSpeedInConsole() {
        return gptTypingSpeedInConsole;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getStudentLangLevel() {
        return studentLangLevel;
    }

    public int getStudentLangLevelIndex() {
        return index;
    }
    public static GptUserOption getGptOptionsByIndex(int index) {
        switch (index) {
            case 0:
                return BEGINNER;
            case 1:
                return PRE_INTERMEDIATE;
            case 2:
                return INTERMEDIATE;
            case 3:
                return POST_INTERMEDIATE;
            case 4:
                return ADVANCED;
        }
        return BEGINNER;
    }

    public static GptUserOption getGptOptionsByLevel(String studentLangLevel) {
        switch (studentLangLevel) {
            case Constant.SIGN_UP_LEVEL_PRE_INTERMEDIATE:
                return PRE_INTERMEDIATE;
            case Constant.SIGN_UP_LEVEL_INTERMEDIATE:
                return INTERMEDIATE;
            case Constant.SIGN_UP_LEVEL_POST_INTERMEDIATE:
                return POST_INTERMEDIATE;
            case Constant.SIGN_UP_LEVEL_ADVANCED:
                return ADVANCED;
            default:
                return BEGINNER;
        }
    }



    public boolean isBeginner() {
        return this == BEGINNER;
    }

    public boolean isPreIntermediateOrLess() {
        if ((this == BEGINNER) || (this == PRE_INTERMEDIATE)) {
            return true;
        }
        return false;
    }

    public boolean isIntermediate() {
        return this == INTERMEDIATE;
    }

    public boolean isAdvanced() {
        return this == ADVANCED;
    }

    public boolean isNotBeginner() {
        return this != BEGINNER;
    }
    public boolean isShowSuggestonInKeyboard() {
        return isPreIntermediateOrLess();
    }
}
