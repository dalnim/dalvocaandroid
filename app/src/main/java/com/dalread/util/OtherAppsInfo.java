package com.dalread.util;

import com.dalread.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 드로어 등에서 "다른 앱 다운받기" 목록을 제공하는 단일 소스.
 * 플러터 all_apps_info.dart와 앱 ID/URL 매핑을 맞춤. 아라한자·아라멀티플레이어 등 여러 앱에서 재사용.
 */
public final class OtherAppsInfo {

    /** 드로어 목록에서 제외할 앱 ID (예: Helpee) */
    private static final Set<Integer> EXCLUDED_APP_IDS = new HashSet<>(Arrays.asList(3)); // Helpee

    public static final class Entry {
        public final int appId;
        public final int nameResId;
        public final int descriptionResId;
        public final String playStoreUrl;
        public final String appStoreUrl;

        public Entry(int appId, int nameResId, int descriptionResId, String playStoreUrl, String appStoreUrl) {
            this.appId = appId;
            this.nameResId = nameResId;
            this.descriptionResId = descriptionResId;
            this.playStoreUrl = playStoreUrl;
            this.appStoreUrl = appStoreUrl;
        }
    }

    /** 앱 ID: 맥 멀티 플레이어(맨 위 고정), 플러터 AraApp 0~8 */
    private static final int APP_ID_MAC_MULTI_PLAYER = 9;

    private static final List<Entry> ALL_APPS = Arrays.asList(
            new Entry(APP_ID_MAC_MULTI_PLAYER, R.string.app_name_MacMultiPlayer, R.string.app_description_MacMultiPlayer, "", "https://apps.apple.com/us/app/aramultivideoplayer/id6752670863?l=ko&mt=12"),
            new Entry(0, R.string.app_name_AraJangbogi, R.string.app_description_AraJangbogi, "https://tinyurl.com/AndJangbogi", "https://tinyurl.com/iosjangbogi"),
            new Entry(1, R.string.app_name_AraTravel, R.string.app_description_AraTravel, "https://tinyurl.com/AndPacking", "https://tinyurl.com/iosPacking"),
            new Entry(2, R.string.app_name_VocabWave, R.string.app_description_VocabWave, "https://play.google.com/store/apps/details?id=com.araonesoft.vocabwave", "https://apps.apple.com/app/vocab-wave/id6747004176"),
            new Entry(3, R.string.app_name_Helpee, R.string.app_description_Helpee, "https://play.google.com/store/apps/details?id=com.araonesoft.helpee", "https://apps.apple.com/app/helpee-words/id6473635697"),
            new Entry(4, R.string.app_name_AraMultiplayer, R.string.app_description_AraMultiplayer, "https://play.google.com/store/apps/details?id=com.araonesoft.aramultiplayer", "https://apps.apple.com/app/aramultiplayer/id1629544322"),
            new Entry(5, R.string.app_name_AraHanja, R.string.app_description_AraHanja, "https://play.google.com/store/apps/details?id=com.dalnimsoft.arahanja", "https://apps.apple.com/app/id1620470799"),
            new Entry(6, R.string.app_name_AraThousand, R.string.app_description_AraThousand, "https://play.google.com/store/apps/details?id=com.araonesoft.arathousand", "https://apps.apple.com/kr/app/id6748657069"),
            new Entry(7, R.string.app_name_AraJesa, R.string.app_description_AraJesa, "https://tinyurl.com/AndAraJesa", "https://tinyurl.com/iOSAraJesa"),
            new Entry(8, R.string.app_name_AraGyeongjosa, R.string.app_description_AraGyeongjosa, "https://tinyurl.com/AndGyeongjosa", "https://tinyurl.com/iosGyeongjosa")
    );

    /**
     * 앱 ID에 해당하는 Entry 반환 (현재 앱 이름 등 표시용).
     */
    public static Entry getEntry(int appId) {
        for (Entry e : ALL_APPS) {
            if (e.appId == appId) return e;
        }
        return null;
    }

    /**
     * 현재 앱을 제외한 "다른 앱" 목록 반환 (제외 ID 적용, appId 순 정렬).
     *
     * @param currentAppId 현재 앱의 AraApp appId (예: 아라멀티플레이어 4, 아라한자 5)
     */
    public static List<Entry> getOtherApps(int currentAppId) {
        List<Entry> result = new ArrayList<>();
        for (Entry e : ALL_APPS) {
            if (e.appId != currentAppId && !EXCLUDED_APP_IDS.contains(e.appId)) {
                result.add(e);
            }
        }
        // 맥 멀티 플레이어(9)는 맨 위, 나머지는 appId 순
        Collections.sort(result, (a, b) -> {
            if (a.appId == APP_ID_MAC_MULTI_PLAYER) return -1;
            if (b.appId == APP_ID_MAC_MULTI_PLAYER) return 1;
            return Integer.compare(a.appId, b.appId);
        });
        return result;
    }

    private OtherAppsInfo() {
    }
}
