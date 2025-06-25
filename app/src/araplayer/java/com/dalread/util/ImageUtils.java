package com.dalread.util;

import androidx.annotation.DrawableRes;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.BuildConfig;

public class ImageUtils extends BaseImageUtils {

    public static @DrawableRes
    int getStudyLanguageFlag() {
        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(BuildConfig.STUDY_LANG);
        @DrawableRes int resid = R.drawable.ic_video_folder_english_flag;
        if (studyLanguage == EnumLanguage.JAPANESE) {
            resid = R.drawable.ic_video_folder_japanese_flag;
        } else if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
            resid = R.drawable.ic_video_folder_chinese_flag;
        }

        return resid;
    }
}
