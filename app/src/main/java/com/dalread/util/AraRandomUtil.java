package com.dalread.util;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AraRandomUtil {
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null; // 리스트가 비어있으면 null 반환
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public static <T> T getRandomElementExcluding(List<T> list, List<T> existingList) {
        if (!isListValid(list)) {
            return null;
        }

        // existingList에 없는 요소들로 새로운 리스트를 만듦
        List<T> filteredList = list.stream()
                .filter(element -> !existingList.contains(element))
                .collect(Collectors.toList());

        Random random = new Random();

        if (filteredList.isEmpty()) {
            int randomIndex = random.nextInt(list.size());
            return list.get(randomIndex); // 필터링 후 비어있으면 list에서 랜덤한걸 반환. 이건 비디오들은 있고 랜덤한걸 못 찾을때는 이미 Pin되어 있는 비디오라도 보여줄려고
        } else {
            int randomIndex = random.nextInt(filteredList.size());
            return filteredList.get(randomIndex);
        }
    }

    // 리스트가 null이거나 비어있거나 모든 요소가 빈 문자열("")일 경우 false 반환하고, 빈 문자열은 제거
    public static <T> boolean isListValid(List<T> list) {
        if (list == null || list.isEmpty()) {
            return false; // 리스트가 null이거나 비어있으면 false 반환
        }
        // 빈 문자열("")을 제거
        list.removeIf(item -> item instanceof String && ((String) item).isEmpty());
        return !list.isEmpty();
    }
}
