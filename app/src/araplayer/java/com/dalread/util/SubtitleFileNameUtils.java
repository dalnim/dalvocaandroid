package com.dalread.util;

import com.dalread.model.PlayerFileModel;

import org.apache.commons.io.FilenameUtils;

import java.util.List;
import com.dalread.util.Utils;

public class SubtitleFileNameUtils {

    /**
     * 연결된 자막 파일의 인덱스를 찾습니다.
     *
     * @param linkedSubtitlePath 연결된 자막 파일의 경로.
     * @param subtitleFiles 사용 가능한 자막 파일 목록.
     * @return 연결된 자막 파일의 인덱스. 찾지 못하면 -1을 반환합니다.
     */
    public static int findLinkedSubtitleIndex(String linkedSubtitlePath, List<PlayerFileModel> subtitleFiles) {
        if (Utils.isEmpty(linkedSubtitlePath)) {
            return -1;
        }
        for (int i = 0; i < subtitleFiles.size(); i++) {
            if (subtitleFiles.get(i).getPath().equals(linkedSubtitlePath)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 주어진 비디오 파일에 가장 적합한 자막을 자막 파일 목록에서 찾습니다.
     *
     * @param videoName 비디오 파일의 이름.
     * @param subtitleFiles 사용 가능한 자막 파일 목록.
     * @return 가장 일치하는 자막 파일의 인덱스. 적합한 파일을 찾지 못하면 -1을 반환합니다.
     */
    public static int findBestSubtitleMatch(String videoName, List<PlayerFileModel> subtitleFiles) {
        String videoFilenameWithoutExt = FilenameUtils.removeExtension(videoName).toLowerCase();

        // 1. 정확히 일치하는 경우
        for (int i = 0; i < subtitleFiles.size(); i++) {
            String subtitleFilenameWithoutExt = FilenameUtils.removeExtension(subtitleFiles.get(i).getName()).toLowerCase();
            if (subtitleFilenameWithoutExt.equals(videoFilenameWithoutExt)) {
                return i;
            }
        }

        // 2. 부분적으로 일치하는 경우 (가장 긴 공통 접두사)
        int bestMatchIndex = -1;
        int bestMatchLength = 0;

        for (int i = 0; i < subtitleFiles.size(); i++) {
            String subtitleFilenameWithoutExt = FilenameUtils.removeExtension(subtitleFiles.get(i).getName()).toLowerCase();
            int commonPrefixLength = 0;
            while (commonPrefixLength < videoFilenameWithoutExt.length() &&
                   commonPrefixLength < subtitleFilenameWithoutExt.length() &&
                   videoFilenameWithoutExt.charAt(commonPrefixLength) == subtitleFilenameWithoutExt.charAt(commonPrefixLength)) {
                commonPrefixLength++;
            }

            if (commonPrefixLength > bestMatchLength) {
                bestMatchLength = commonPrefixLength;
                bestMatchIndex = i;
            }
        }

        // 의미 있는 일치(예: 5자 이상)인 경우에만 최적의 일치 항목을 반환합니다.
        if (bestMatchLength > 5) {
            return bestMatchIndex;
        }

        return -1;
    }
}