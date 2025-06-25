package com.dalread.base;

import android.content.Context;

public enum EnumMultiplePlayer {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    SIX(6),
    EIGHT(8),
    NINE(9),
    TWELVE(12),
    FIFTEEN(15),
    SIXTEEN(16),
    EIGHTEEN(18),
    TWENTY(20),
    TWENTY_ONE(21),
    TWENTY_FOUR(24),
    TWENTY_SEVEN(27),
    TWENTY_EIGHT(28),
    THIRTY(30),
    THIRTY_TWO(32),
    THIRTY_SIX(36),
    FORTY(40);

    private int numberOfScreen;

    EnumMultiplePlayer(int numberOfScreen) {
        this.numberOfScreen = numberOfScreen;
    }

    public static EnumMultiplePlayer getEnum(int numberOfScreen) {
        for (EnumMultiplePlayer enumValue : EnumMultiplePlayer.values()) {
            if (enumValue.numberOfScreen == numberOfScreen) {
                return enumValue;
            }
        }
        return getDefaultEnum();
    }

    public static EnumMultiplePlayer getDefaultEnum() {
        return FOUR;
    }

    public static String[] getNames(Context context) {
        int count = values().length;
//        int count = DeviceUtil.isPhone(context) ? 4 : values().length;
//        if (Utils.isDebug()) {
//            count = values().length;
//        }
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = String.valueOf(values()[i].numberOfScreen);
        }
        return names;
    }

    public int getNumberOfScreen() {
        return numberOfScreen;
    }
}
