package com.dalread.util;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;

public class StringCompareUtil {

    // 단순히 .equals 하면 같은 문자인데도 바이트가 달라서 다르게 나올때가 있어서 이걸 쓴다.
    public static boolean areStringsEqualByNfc(String str1, String str2) {
        // NFC 방식으로 정규화
        String normalizedStr1NFC = Normalizer.normalize(str1, Normalizer.Form.NFC);
        String normalizedStr2NFC = Normalizer.normalize(str2, Normalizer.Form.NFC);

        // 바이트 배열로 변환 (NFC)
        byte[] str1NFCBytes = normalizedStr1NFC.getBytes(StandardCharsets.UTF_8);
        byte[] str2NFCBytes = normalizedStr2NFC.getBytes(StandardCharsets.UTF_8);
        return Arrays.equals(str1NFCBytes, str2NFCBytes);
    }
}
