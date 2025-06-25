package com.dalread.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LanguageUtilKorean {
    private static final char[] CHOSUNG = {
            0xAC00, 0xAE4C, ' ', 0xB098, ' ', ' ', 0xB2E4, 0xB530, 0xB77C, ' ', ' ', ' ', ' ', ' ', ' ', ' ', 0xB9C8,
            0xBC14, 0xBE60, ' ', 0xC0AC, 0xC2F8, 0xC544, 0xC790, 0xC9DC, 0xCC28, 0xCE74, 0xD0C0, 0xD30C, 0xD558};

    final static String[] CHO = {"ㄱ","ㄲ","ㄴ","ㄷ","ㄸ","ㄹ","ㅁ","ㅂ","ㅃ", "ㅅ","ㅆ","ㅇ","ㅈ","ㅉ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"};
    final static String[] JOONG = {"ㅏ","ㅐ","ㅑ","ㅒ","ㅓ","ㅔ","ㅕ","ㅖ","ㅗ","ㅘ", "ㅙ","ㅚ","ㅛ","ㅜ","ㅝ","ㅞ","ㅟ","ㅠ","ㅡ","ㅢ","ㅣ"};
    final static String[] JONG = {"","ㄱ","ㄲ","ㄳ","ㄴ","ㄵ","ㄶ","ㄷ","ㄹ","ㄺ","ㄻ","ㄼ", "ㄽ","ㄾ","ㄿ","ㅀ","ㅁ","ㅂ","ㅄ","ㅅ","ㅆ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"};

    public static boolean isChoSung(String searchChar) {
        return 0x3131 <= searchChar.charAt(0) && searchChar.charAt(0) < 0x314F;
    }
    public static String getChosungFromOneCharacter(String oneCharacter) {
        //https://needjarvis.tistory.com/644
        String strCho = "";
        try {
            char uniVal = oneCharacter.charAt(0);
            uniVal = (char)(uniVal - 0xAC00);
            char cho = (char) (uniVal / 28 / 21);
            char joong = (char) ((uniVal) / 28 % 21);
            char jong = (char) (uniVal % 28); // 종성의 첫번째는 채움이기 때문에 System.out.println(CHO[cho] + JOONG[joong] + JONG[jong]);
            strCho = CHO[cho];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strCho;
    }

    public static List<String> speedHangleCheck(String searchChar, List<String> stringList) {
        // TODO : 찾으려는 초성이나 들어간 글자만 추출한다.
        ArrayList<String> arrayListResult = new ArrayList<>();
        for (String hangul : stringList) {
            boolean checkString = true;
            int i = 0, j = searchChar.trim().length() - 1;
            int stringLength = (j % 2 == 0) ? j / 2 : j / 2 + 1;
            for (; i <= stringLength; i++, j--) {
                checkString = checkString && _getCheckCollect(searchChar.charAt(i), hangul.charAt(i))
                        && _getCheckCollect(searchChar.charAt(j), hangul.charAt(j));
                if (checkString) {
                    arrayListResult.add(hangul);
                }
            }
        }
        return arrayListResult;
    }

    public static boolean speedHangleCheck(String searchChar, String hangul) {
        // TODO : 찾으려는 초성이나 문자가 들어간 문자열이 맞는지 빠르게 검색하여 확인한다.
        String deletedTrim = searchChar.trim();
        if (deletedTrim.length() > hangul.length())
            return false;
        if (hangul.matches(".*"+searchChar.toString()+".*"))
            return true;
        boolean checkString = true;
        int i = 0, j = deletedTrim.length() - 1;
        int stringLength = (j % 2 == 0) ? j / 2 : j / 2 + 1;
        for (; i <= stringLength; i++, j--) {
            checkString = checkString && _getCheckCollect(searchChar.charAt(i), hangul.charAt(i))
                    && _getCheckCollect(searchChar.charAt(j), hangul.charAt(j));
            if (!checkString) return false;
        }
        return checkString;
    }

    private static boolean _getCheckCollect(char searchChar, char oneChar) {
        // TODO : 문자하나의 범위를 이용하여 맞는지 안맞는지 확인
        boolean checkText = false; // 기본 문자도아니고 초성도아닐 때
        char range;
        if (0xAC00 <= searchChar && searchChar < 0xD7A4) { // 문자
            range = _getHangleRange(searchChar);
            checkText = (range <= oneChar && oneChar < range + 0x1c);
        } else if (0x3131 <= searchChar && searchChar < 0x314F) { // 초성일 때
            range = _getChosungRange(searchChar);
            checkText = (range <= oneChar && oneChar < range + 0x24c);
        } else if (searchChar == 0x20 && searchChar == oneChar) { // 공백
            checkText = true;
        }
        return checkText;
    }

    private static char _getHangleRange(char searchChar) {
        // TODO : 한글이 주어졌을 때 범위 찾는다 . 초성+중성 일 때 범위 searchChar <= searchCHar < searchChar+28
        return searchChar;
    }

    private static char _getChosungRange(char chosung) {
        // TODO : 초성이 주어졌을 때 한글 문자 하나의 범위를 얻는다. '가' 종류 일 때 44032 ~ 44520
        // ㄱ일 때 44032 ~ 44520
        int base = chosung - 0x3131;
        return CHOSUNG[base];
    }

    public static String normalizeFirstCharHangul(String str) {
        String result = str;
        if (!Utils.isEmpty(str)) {
            HashMap<String, String> mapNoramizeFirstCharHangul = getMapToNoramizeFirstCharHangul();
            String firstStr = str.substring(0, 1);
            String restStr = str.substring(1);
            if (mapNoramizeFirstCharHangul.containsKey(firstStr)) {
                firstStr = mapNoramizeFirstCharHangul.get(firstStr);
            }
            result = firstStr + restStr;
        }
        return result;
    }

    private static HashMap<String, String> getMapToNoramizeFirstCharHangul() {
        HashMap<String, String> mapNoramizeFirstCharHangul = new HashMap<>();
        mapNoramizeFirstCharHangul.put("라", "나");
        mapNoramizeFirstCharHangul.put("락", "낙");
        mapNoramizeFirstCharHangul.put("란", "난");
        mapNoramizeFirstCharHangul.put("랄", "날");
        mapNoramizeFirstCharHangul.put("람", "남");
        mapNoramizeFirstCharHangul.put("랍", "납");
        mapNoramizeFirstCharHangul.put("랑", "낭");
        mapNoramizeFirstCharHangul.put("래", "내");
        mapNoramizeFirstCharHangul.put("랭", "냉");
        mapNoramizeFirstCharHangul.put("략", "약");
        mapNoramizeFirstCharHangul.put("냑", "약");
        mapNoramizeFirstCharHangul.put("량", "양");
        mapNoramizeFirstCharHangul.put("냥", "양");
        mapNoramizeFirstCharHangul.put("려", "여");
        mapNoramizeFirstCharHangul.put("녀", "여");
        mapNoramizeFirstCharHangul.put("력", "역");
        mapNoramizeFirstCharHangul.put("녁", "역");
        mapNoramizeFirstCharHangul.put("련", "연");
        mapNoramizeFirstCharHangul.put("년", "연");
        mapNoramizeFirstCharHangul.put("렬", "열");
        mapNoramizeFirstCharHangul.put("녈", "열");
        mapNoramizeFirstCharHangul.put("렴", "염");
        mapNoramizeFirstCharHangul.put("념", "염");
        mapNoramizeFirstCharHangul.put("렵", "엽");
        mapNoramizeFirstCharHangul.put("녕", "영");
        mapNoramizeFirstCharHangul.put("령", "영");
        mapNoramizeFirstCharHangul.put("녜", "예");
        mapNoramizeFirstCharHangul.put("례", "예");
        mapNoramizeFirstCharHangul.put("로", "노");
        mapNoramizeFirstCharHangul.put("록", "녹");
        mapNoramizeFirstCharHangul.put("론", "논");
        mapNoramizeFirstCharHangul.put("롱", "농");
        mapNoramizeFirstCharHangul.put("뢰", "뇌");
        mapNoramizeFirstCharHangul.put("료", "요");
        mapNoramizeFirstCharHangul.put("뇨", "요");
        mapNoramizeFirstCharHangul.put("룡", "용");
        mapNoramizeFirstCharHangul.put("루", "누");
        mapNoramizeFirstCharHangul.put("류", "유");
        mapNoramizeFirstCharHangul.put("뉴", "유");
        mapNoramizeFirstCharHangul.put("륙", "육");
        mapNoramizeFirstCharHangul.put("뉵", "육");
        mapNoramizeFirstCharHangul.put("륜", "윤");
        mapNoramizeFirstCharHangul.put("률", "율");
        mapNoramizeFirstCharHangul.put("륭", "융");
        mapNoramizeFirstCharHangul.put("륵", "늑");
        mapNoramizeFirstCharHangul.put("름", "늠");
        mapNoramizeFirstCharHangul.put("릉", "능");
        mapNoramizeFirstCharHangul.put("니", "이");
        mapNoramizeFirstCharHangul.put("리", "이");
        mapNoramizeFirstCharHangul.put("린", "인");
        mapNoramizeFirstCharHangul.put("림", "임");
        mapNoramizeFirstCharHangul.put("립", "입");
        return mapNoramizeFirstCharHangul;
    }
}
