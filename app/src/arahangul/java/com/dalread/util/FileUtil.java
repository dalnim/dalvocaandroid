package com.dalread.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.dalread.model.VocaDownload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil extends BaseFileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();;

    public static String getVoiceDownloadListFileNameInAsset(Context context) {
        if (LanguageUtil.isStudyLangChinese(context))
            return Constant.ARACONV.ENG.ASSET_VOICE_FILE_LIST_TEXT;
        else if (LanguageUtil.isStudyLangJapanese(context))
            return Constant.ARACONV.ENG.ASSET_VOICE_FILE_LIST_TEXT;
        else if (LanguageUtil.isStudyLangKorean(context))
            return Constant.ARACONV.ENG.ASSET_VOICE_FILE_LIST_TEXT;
        return Constant.ARACONV.ENG.ASSET_VOICE_FILE_LIST_TEXT;
    }

    public static List<VocaDownload> getVoiceDownloadListInAsset(Context context, String fileNameInAsset) {
        List<VocaDownload> vocaDownloadList = new ArrayList<>();
        InputStream in;
        try {
            AssetManager assetManager = context.getAssets();
            in = assetManager.open(fileNameInAsset);

            String fileContent = BaseStorageUtil.convertStreamToString(in);
            List<String> lineList = Arrays.asList(fileContent.split("[\r\n]+"));
            for (int i = 1; i < lineList.size(); i++) {
                String lineContent = lineList.get(i);
                List<String> itemList = Arrays.asList(lineContent.split("[\t]+"));
                if (itemList.size() == 3) {
                    VocaDownload vocaDownload = new VocaDownload();
                    vocaDownload.setName(itemList.get(0));
                    vocaDownload.setVersion(Integer.parseInt(itemList.get(1)));
                    vocaDownload.setFileSize(Integer.parseInt(itemList.get(2)));
                    vocaDownloadList.add(vocaDownload);
                }
            }
            in.close();
        } catch (Exception e) {
            DLog.e(TAG, "Exception", e);
        }
        return vocaDownloadList;
    }
}
