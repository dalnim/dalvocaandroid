package com.dalread.util;

import android.content.Context;

public class DeviceUtil {
    // 디바이스가 폰인지 확인하는 함수
    public static boolean isPhone(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp < 600;
    }

    // 디바이스가 태블릿인지 확인하는 함수
    public static boolean isPad(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }
}
