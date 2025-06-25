package com.dalread.util;

import androidx.annotation.StringRes;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.base.EnumLanguage;

public class PlayerLanguageUtil {
    public static @StringRes
    int getStudyLanguageFolderText(int appMediaType) {
        @StringRes int resid = R.string.video_folder_en;
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            resid = R.string.video_folder_multi_player;
        } else {
            EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(BuildConfig.STUDY_LANG);
            if (appMediaType == Constant.AppMediaType.VIDEO) {
                if (studyLanguage == EnumLanguage.JAPANESE) {
                    resid = R.string.video_folder_jp;
                } else if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
                    resid = R.string.video_folder_cn;
                }
            } else if (appMediaType == Constant.AppMediaType.MUSIC) {
                if (studyLanguage == EnumLanguage.JAPANESE) {
                    resid = R.string.music_folder_jp;
                } else if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
                    resid = R.string.music_folder_cn;
                } else {
                    resid = R.string.music_folder_en;
                }
            }
        }
        return resid;
    }
}
