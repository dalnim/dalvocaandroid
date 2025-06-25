package com.dalread.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import com.dalread.database.SharedPreferencesDB;

import org.apache.commons.io.FilenameUtils;

import java.security.SecureRandom;

public class AraFreePointUtil {
    // 대소문자와 숫자 중 헷갈리는 글자를 제외한 문자열 상수
    private static final String LOWERCASE = "abcdefghijkmnpqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String DIGITS = "23456789";

    public static String generateRandomString(Context context) {
        int callCount = getCallCount(context);
        int stringLength = determineStringLength(callCount);
        return generateRandomStringByLength(stringLength);
    }

    private static int getCallCount(Context context) {
        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        int callCount = sharedPreferencesDB.getFreePointCallCount();
        sharedPreferencesDB.setFreePointCallCount(callCount + 1);
        return callCount;
    }

    // 난수 문자열을 생성하는 함수
    private static String generateRandomStringByLength(int length) {
        SecureRandom random = new SecureRandom();
        char[] characters = new char[length];

        for (int i = 0; i < length; i++) {
            if (i % 3 == 0) {
                characters[i] = UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
            } else if (i % 3 == 1) {
                characters[i] = DIGITS.charAt(random.nextInt(DIGITS.length()));
            } else {
                characters[i] = LOWERCASE.charAt(random.nextInt(LOWERCASE.length()));
            }
        }

        return new String(characters);
    }
    // 문자열 길이를 결정하는 함수
    private static int determineStringLength(int callCount) {
        if (callCount < 1) {
            return 1;
        } else if (callCount == 1) {
            return 3;
        } else if (callCount == 2) {
            return 6;
        } else {
            return 9;
        }
    }
//
//    public static String generateRandomString(Context context) {
//        //대소문자와 숫자중 헷갈리는 글자는 뺐다.
////        final String LOWERCASE = "abcdefghijkmnpqrstuvwxyz";
////        final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
////        final String DIGITS = "23456789";
//        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
//        int callCount = sharedPreferencesDB.getFreePointCallCount();
//
//        SecureRandom random = new SecureRandom();
//        int length;
//        if (callCount < 1) {
//            length = 1;
//        } else if (callCount == 1) {
//            length = 3;
//        } else if (callCount == 2) {
//            length = 6;
//        } else {
//            length = 9;
//        }
//
//        // 문자열 생성
//        char[] characters = new char[length];
//        for (int i = 0; i < length; i++) {
//            if (i % 3 == 0) {
//                characters[i] = UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
//            } else if (i % 3 == 1) {
//                characters[i] = DIGITS.charAt(random.nextInt(DIGITS.length()));
//            } else {
//                characters[i] = LOWERCASE.charAt(random.nextInt(LOWERCASE.length()));
//            }
//        }
////
////        characters[0] = UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
////        characters[1] = DIGITS.charAt(random.nextInt(DIGITS.length()));
////        characters[2] = LOWERCASE.charAt(random.nextInt(LOWERCASE.length()));
////        characters[3] = UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
////        characters[4] = DIGITS.charAt(random.nextInt(DIGITS.length()));
////        characters[5] = LOWERCASE.charAt(random.nextInt(LOWERCASE.length()));
////        characters[6] = UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
////        characters[7] = DIGITS.charAt(random.nextInt(DIGITS.length()));
////        characters[8] = LOWERCASE.charAt(random.nextInt(LOWERCASE.length()));
//
//        return new String(characters);
//    }
}