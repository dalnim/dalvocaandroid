package com.dalread.util;

import org.apache.commons.io.FilenameUtils;

public class AraFileNameUtil {
    public static boolean isHiddenFile(String filePath) {
        return startsWithDot(FilenameUtils.getName(filePath));
    }

    public static boolean isFileInHiddenFolder(String filePath) {
        return startsWithDot(getParentDirectoryName(filePath));
    }

    private static boolean startsWithDot(String str) {
        return str != null && str.startsWith(".");
    }

    public static String getParentDirectoryName(String filePath) {
        String basePath = FilenameUtils.getFullPath(filePath);

        // basePath에서 마지막 '/' 이전의 부분을 디렉토리명으로 추출
        if (basePath != null && basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1); // 마지막 '/' 제거
        }
        int lastSlashIndex = basePath.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return basePath.substring(lastSlashIndex + 1); // 마지막 '/' 이후의 문자열 반환
        } else {
            return basePath; // '/' 가 없는 경우 basePath 반환
        }
    }

}
