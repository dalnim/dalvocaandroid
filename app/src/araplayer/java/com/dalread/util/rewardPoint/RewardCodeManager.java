package com.dalread.util.rewardPoint;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 앱 내 코드 검증 로직을 처리하는 클래스
 * SharedPreferences를 사용하여 코드 사용 내역을 관리합니다.
 * @author dalnimbest
 */
public class RewardCodeManager {
    public enum RewardCodeValidationResult {
        VALID,
        ALREADY_USED,
        INVALID
    }

    private static final String SECRET_SALT = "my_secret_salt_for_ara_multiplayer_point_rewards";
    public static final String PREFS_NAME = "RewardCodePrefs";
    public static final String KEY_LAST_USED_CODE = "last_used_reward_code";

    private final int validWeekRange;
    private final Supplier<LocalDateTime> nowProvider;

    public static final boolean MAKE_CODE_FOR_WEEK = true;

    /**
     * 기본 생성자. 유효 기간은 1주, 시간은 현재 시간을 사용합니다.
     */
    public RewardCodeManager() {
        this(1, LocalDateTime::now);
    }

    /**
     * 생성자
     * @param validWeekRange 유효 주차 범위
     * @param nowProvider 현재 시간을 제공하는 Supplier (테스트용)
     */
    public RewardCodeManager(int validWeekRange, Supplier<LocalDateTime> nowProvider) {
        this.validWeekRange = validWeekRange;
        this.nowProvider = nowProvider;
    }

    /**
     * 현재 날짜 기준으로 유효한 코드 목록을 생성합니다.
     * @return 유효한 코드 리스트
     */
    public List<String> getValidCodes() {
        final LocalDateTime now = nowProvider.get();
        final List<String> codes = new ArrayList<>();

        if (MAKE_CODE_FOR_WEEK) {
            final int year = now.getYear();
            final int currentWeek = weekNumber(now);
            for (int i = 0; i < validWeekRange; i++) {
                final int week = currentWeek - i;
                codes.add(generateCodeFromIdentifier(year + "-" + week, SECRET_SALT));
            }
        } else {
            final long currentMinuteId = minuteIdentifier(now);
            codes.add(generateCodeFromIdentifier(String.valueOf(currentMinuteId), SECRET_SALT));
        }
        return codes;
    }

    /**
     * 식별자를 기반으로 코드를 생성합니다.
     * @param identifier 고유 식별자 (예: "2024-30")
     * @param salt 비밀 키
     * @return 생성된 코드
     */
    public static String generateCodeFromIdentifier(String identifier, String salt) {
        final String input = identifier + "-" + salt;
        return String.valueOf(Math.abs(input.hashCode()));
    }

    /**
     * 사용자가 입력한 코드가 유효한지 확인합니다.
     * 이 메소드는 코드 사용 내역을 저장하지 않습니다.
     * 검증 성공 후 saveUsedCode()를 호출하여 코드를 사용 처리해야 합니다.
     * @param context Android Context
     * @param input 사용자가 입력한 코드
     * @return 검증 결과 (VALID, ALREADY_USED, INVALID)
     */
    public RewardCodeValidationResult validateUserCode(Context context, String input) {
        if (input == null || input.isEmpty()) {
            return RewardCodeValidationResult.INVALID;
        }
        final List<String> validCodes = getValidCodes();
        if (validCodes.stream().anyMatch(code -> code.equalsIgnoreCase(input))) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            final String lastUsedCode = prefs.getString(KEY_LAST_USED_CODE, null);
            if (input.equalsIgnoreCase(lastUsedCode)) {
                return RewardCodeValidationResult.ALREADY_USED;
            } else {
                return RewardCodeValidationResult.VALID;
            }
        }
        return RewardCodeValidationResult.INVALID;
    }

    /**
     * 사용한 코드를 SharedPreferences에 저장합니다.
     * @param context Android Context
     * @param code 저장할 코드
     */
    public void saveUsedCode(Context context, String code) {
        if (context == null || code == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LAST_USED_CODE, code.toUpperCase());
        editor.apply();
    }

    /**
     * 날짜를 주차로 변환합니다. (1년의 첫 날(1일)을 1주차의 시작으로 간주)
     * @param date 날짜
     * @return 해당 날짜의 주차 (1부터 시작)
     */
    private int weekNumber(LocalDateTime date) {
        LocalDateTime firstDayOfYear = date.withDayOfYear(1);
        long days = ChronoUnit.DAYS.between(firstDayOfYear, date);
        return (int) Math.ceil((double) (days + 1) / 7.0);
    }

    /**
     * 날짜와 시간(분까지)을 기반으로 고유한 분 단위 식별자를 생성합니다.
     * @param date 날짜
     * @return 분 단위 식별자 (long)
     */
    private long minuteIdentifier(LocalDateTime date) {
        return (long) date.getYear() * 1000000000L +
               date.getMonthValue() * 10000000L +
               date.getDayOfMonth() * 100000L +
               date.getHour() * 1000L +
               date.getMinute();
    }
}
