package com.dalread.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MediaFolderUtil {
    private static final String MICRO_SD_CARD = "SD Card";
    private static final String MICRO_SD_DOWNLOADS = MICRO_SD_CARD + " (" + Environment.DIRECTORY_DOWNLOADS + ")";
    private static final String MICRO_SD_MOVIES = MICRO_SD_CARD + " (" + Environment.DIRECTORY_MOVIES + ")";
    private static final String MICRO_SD_DOCUMENTS = MICRO_SD_CARD + " (" + Environment.DIRECTORY_DOCUMENTS + ")";
    private static final String KEY_SELECTED_MEDIA_FOLDERS = "KEY_SELECTED_MEDIA_FOLDERS";
    private static final String SET_KEY_SELECTED_MEDIA_FOLDERS = "SET_KEY_SELECTED_MEDIA_FOLDERS";

    private static final Set<String> DEFAULT_EXCLUDED_FOLDERS = new HashSet<>();
    private static final Set<String> DEFAULT_EXCLUDED_FOLDERS_IN_MICRO_SD = new HashSet<>();

    static {
        DEFAULT_EXCLUDED_FOLDERS.add(Environment.DIRECTORY_DCIM);
        DEFAULT_EXCLUDED_FOLDERS.add(Environment.DIRECTORY_MUSIC);
        DEFAULT_EXCLUDED_FOLDERS.add(Environment.DIRECTORY_PODCASTS);
        DEFAULT_EXCLUDED_FOLDERS.add(Environment.DIRECTORY_DOCUMENTS);

        DEFAULT_EXCLUDED_FOLDERS_IN_MICRO_SD.add(MICRO_SD_DOWNLOADS);
        DEFAULT_EXCLUDED_FOLDERS_IN_MICRO_SD.add(MICRO_SD_MOVIES);
        DEFAULT_EXCLUDED_FOLDERS_IN_MICRO_SD.add(MICRO_SD_DOCUMENTS);
    }


    private static final String[] ALL_MEDIA_FOLDER_NAMES = {
            Environment.DIRECTORY_DOWNLOADS,
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_MOVIES,
            Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_PODCASTS
    };

    public static String[] getAllMediaFolders(Context context) {
        // ALL_MEDIA_FOLDER_NAMES 배열을 리스트로 변환하여 초기화
        List<String> allMediaFolders = new ArrayList<>(Arrays.asList(ALL_MEDIA_FOLDER_NAMES));

        //MicroSD 카드가 있으면 다운로드 폴더등 추가
        if (!getMicroSdPath(context).isEmpty()) {
            allMediaFolders.add(MICRO_SD_DOWNLOADS);
            allMediaFolders.add(MICRO_SD_MOVIES);
            allMediaFolders.add(MICRO_SD_DOCUMENTS);
        }

        return allMediaFolders.toArray(new String[0]);
    }

    private static String getMicroSdPath(Context context) {
        String result = "";
        File[] externalStorageVolumes = ContextCompat.getExternalCacheDirs(context);
        if (externalStorageVolumes.length > 1) {  // 외장 스토리지 (MicroSD 카드)가 있는 경우
            File cachePath = externalStorageVolumes[1];
            if (cachePath != null && cachePath.exists()) {
                File microSdPath = cachePath.getParentFile().getParentFile().getParentFile().getParentFile();
                if (microSdPath != null && microSdPath.exists()) {
                    result = microSdPath.getAbsolutePath();
//                    File downloadPathInMicroSd = new File(microSdPath, Environment.DIRECTORY_DOWNLOADS);
//                    if (downloadPathInMicroSd != null && downloadPathInMicroSd.exists()) {
//                        result = downloadPathInMicroSd.getAbsolutePath();
//                    }
                }
            }
        }
        return result;
    }
    public static boolean isTooSmallSizeMediaFile(File file) {
        int minFileSize = 1024 * 10;
        return file.length() <= minFileSize;
    }

    public static void saveSelectedFolders(Context context, List<String> selectedFolders) {
        SharedPreferences prefs = context.getSharedPreferences(KEY_SELECTED_MEDIA_FOLDERS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>(selectedFolders);
        editor.putStringSet(SET_KEY_SELECTED_MEDIA_FOLDERS, set);
        editor.apply();
    }

    private static List<String> getSelectedFolders(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(KEY_SELECTED_MEDIA_FOLDERS, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(SET_KEY_SELECTED_MEDIA_FOLDERS, new HashSet<>());
        return new ArrayList<>(set);
    }

    private static List<String> getMediaFolderForAraMedia(Context context) {
        List<String> selectedFolders = getSelectedFolders(context);
        List<String> results = new ArrayList<>();
        for (String folderName : getAllMediaFolders(context)) {
            if (selectedFolders.size() == 0) {
                if (!DEFAULT_EXCLUDED_FOLDERS.contains(folderName)) {
                    boolean isMicroSdAvailable = !getMicroSdPath(context).isEmpty();
                    if (isMicroSdAvailable || !DEFAULT_EXCLUDED_FOLDERS_IN_MICRO_SD.contains(folderName)) {
                        results.add(folderName);
                    }
                }
            } else {
                if (selectedFolders.contains(folderName)) {
                    results.add(folderName);
                }
            }
        }
        return results;
    }

    public static List<String> getMediaFolderPathForAraMedia(Context context) {
        List<String> selectedFolders = getMediaFolderForAraMedia(context);
        List<String> results = new ArrayList<>();
        String microSdPath = getMicroSdPath(context);
        for (String folderName : selectedFolders) {
            if (folderName.equals(MICRO_SD_DOWNLOADS)) {
                results.add(microSdPath + "/" + Environment.DIRECTORY_DOWNLOADS);
            } else if (folderName.equals(MICRO_SD_MOVIES)) {
                results.add(microSdPath + "/" + Environment.DIRECTORY_MOVIES);
            } else if (folderName.equals(MICRO_SD_DOCUMENTS)) {
                results.add(microSdPath + "/" + Environment.DIRECTORY_DOCUMENTS);
            } else {
                results.add(Environment.getExternalStoragePublicDirectory(folderName).getPath());
            }
        }
        return results;
    }

    public static boolean[] getCheckedItems(Context context) {
        String[] allItems = getAllMediaFolders(context);
        String[] items = getMediaFolderForAraMedia(context).toArray(new String[0]);
        boolean[] checkedItems = new boolean[allItems.length];
        Set<String> itemsSet = new HashSet<>(Arrays.asList(items));

        for (int i = 0; i < allItems.length; i++) {
            checkedItems[i] = itemsSet.contains(allItems[i]);
        }

        return checkedItems;
    }
}