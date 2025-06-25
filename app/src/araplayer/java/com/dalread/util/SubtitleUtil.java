package com.dalread.util;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.SubtitleEncodingModel;
import com.dalread.model.SubtitleLanguageModel;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubtitleUtil {

    private static final String TAG = "SubTitleUtil";

//    public static BaseSubtitleObject parserSubTitle(PlayerFileModel playerFileModel) {
//        return parserSubTitle(playerFileModel, null);
//    }
//
//    public static BaseSubtitleObject parserSubTitle(PlayerFileModel playerFileModel, String encoding) {
//        DLog.d(TAG, "parserSubTitle - encoding=" + encoding);
//        BaseSubtitleObject subtitle = null;
//        if (!Utils.isEmpty(playerFileModel.getSubPath())) {
//            final SupportSubtitleFormat type = FileUtil.getSubtitleExtension(playerFileModel.getSubPath());
//            switch (type) {
//                case SRT:
//                    subtitle = parserSRT(playerFileModel, encoding);
//                    break;
//                case SMI:
//                    subtitle = parserSMI(playerFileModel, encoding);
//                    break;
//            }
//        }
//        return subtitle;
//    }

    public static String parserContentSubTitle(PlayerFileModel playerFileModel) {
        return parserContentSubTitle(playerFileModel, playerFileModel.getVideoModel().getSubPathIndex());
    }
    //인코딩은 나중에 고려하자.
    public static String parserContentSubTitle(PlayerFileModel playerFileModel, int subPathIndex) {
        String subPath = playerFileModel.getVideoModel().getSubPath1();
        String encoding = playerFileModel.getVideoModel().getEncoding1();
        if (subPathIndex == Constant.PLAYER.INTENT.SUBPATH_INDEX_2) {
            subPath = playerFileModel.getVideoModel().getSubPath2();
            encoding = playerFileModel.getVideoModel().getEncoding2();
        }
        DLog.d(TAG, "getContentSRT - file=" + playerFileModel.toString() + " - encoding=" + encoding);
        if (encoding.isEmpty()) { // AUTO
            encoding = guessEncoding(subPath);
        }

        File subtitleFile = new File(subPath);
        String content = FileUtil.getFileContentsFromFile(subtitleFile);
        return content;
    }

//    private static SrtObject parserSRT(PlayerFileModel file, String encoding) {
//        DLog.d(TAG, "parserSRT - file=" + file.toString() + " - encoding=" + encoding);
//        SrtParser parser = new SrtParser(encoding);
//        try {
//            if (file.isLocal()) {
//                return parser.parse(file.getSubPath());
//            } else if (file.isWebDAV()) {
//                String path = file.getServerModel().getPath(file.getSubPath());
//                return parser.parse(ServerManager.getInstance().getInputStream(path, file.getServerModel().getAccountPassword()));
//            }
//        } catch (Exception ex) {
//            DLog.e(TAG, ex.getMessage());
//        }
//        return null;
//    }
//
//    private static String getContentSRT(PlayerFileModel playerFileModel, String subPath, String encoding) {
//        DLog.d(TAG, "getContentSRT - file=" + playerFileModel.toString() + " - encoding=" + encoding);
//
//        SrtParser parser = new SrtParser(encoding);
//        try {
//            if (playerFileModel.isLocal()) {
//                return parser.getContent(subPath);
//            } else if (playerFileModel.isWebDAV()) {
////                String path = playerFileModel.getServerModel().getPath(playerFileModel.getSubPath());
//                ServerModel serverModel = playerFileModel.getServerModel();
//                return parser.getContent(ServerManager.getInstance().getInputStream(serverModel.getPath(subPath), serverModel.getAccountPassword()));
//            }
//        } catch (Exception ex) {
//            DLog.e(TAG, ex.getMessage());
//        }
//        return null;
//    }
//
//    private static SamiObject parserSMI(PlayerFileModel file, String encoding) {
//        DLog.d(TAG, "parserSMI - file=" + file.toString() + " - encoding=" + encoding);
//        SamiParser parser = new SamiParser(encoding);
//        try {
//            if (file.isLocal()) {
//                return parser.parse(file.getSubPath());
//            } else if (file.isWebDAV()) {
//                String path = file.getServerModel().getPath(file.getSubPath());
//                return parser.parse(ServerManager.getInstance().getInputStream(path, file.getServerModel().getAccountPassword()));
//            }
//        } catch (Exception ex) {
//            DLog.e(TAG, ex.getMessage());
//        }
//        return null;
//    }

    public static String guessEncoding(String path) {
        // Load input data
        long count = 0;
        int n;
        int EOF = -1;
        InputStream input;
        try {
            input = new FileInputStream(new File(path));
            byte[] buffer = new byte[4096];
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            while ((EOF != (n = input.read(buffer))) && (count <= Integer.MAX_VALUE)) {
                output.write(buffer, 0, n);
                count += n;
            }

            if (count > Integer.MAX_VALUE) {
                throw new RuntimeException("Inputstream too large.");
            }

            byte[] data = output.toByteArray();

            // Detect encoding
            CharsetDetector charsetDetector = new CharsetDetector();
            charsetDetector.setText(data);
            charsetDetector.enableInputFilter(true);
            CharsetMatch cm = charsetDetector.detect();
            return cm.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    private static String getContentSMI(PlayerFileModel playerFileModel, String subPath, String encoding) {
//        DLog.d(TAG, "getContentSMI - file=" + playerFileModel.toString() + " - encoding=" + encoding);
//        SamiParser parser = new SamiParser(encoding);
//        try {
//            if (playerFileModel.isLocal()) {
//                return parser.getContent(subPath);
//            } else if (playerFileModel.isWebDAV()) {
////                String path = file.getServerModel().getPath(file.getSubPath());
//                ServerModel serverModel = playerFileModel.getServerModel();
//                return parser.getContent(ServerManager.getInstance().getInputStream(serverModel.getPath(subPath), serverModel.getAccountPassword()));
//            }
//        } catch (Exception ex) {
//            DLog.e(TAG, ex.getMessage());
//        }
//        return null;
//    }

    public static List<SubtitleLanguageModel> getSubtitleLanguageModels(Context context, PlayerFileModel playerFileModel, SubDatabase subDatabase) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        List<SubtitleLanguageModel> subtitleLanguageModels = new ArrayList<>();
        boolean  isDisplaySubtitleLangStudyFirst = playerFileModel.getVideoModel().getIsDisplaySubtitleLangStudyFirst() == Constant.INT_BOOLEAN.TRUE ? true : false;
        if (isDisplaySubtitleLangStudyFirst) {
            if (subDatabase.isHasStudyLangSubtitle()) {
                int index = 0;
                SubtitleLanguageModel subtitleLanguage1 = new SubtitleLanguageModel(index, EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(), true,true);
                subtitleLanguageModels.add(subtitleLanguage1);
            }

            if (subDatabase.isHasMotherTongueSubtitle()) {
                int index = 1;
                SubtitleLanguageModel subtitleLanguage2 = new SubtitleLanguageModel(index, EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getIdApi(),true,true);
                subtitleLanguageModels.add(subtitleLanguage2);
            }
        } else {
            if (subDatabase.isHasMotherTongueSubtitle()) {
                int index = 0;
                SubtitleLanguageModel subtitleLanguage2 = new SubtitleLanguageModel(index, EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getIdApi(), true,true);
                subtitleLanguageModels.add(subtitleLanguage2);
            }

            if (subDatabase.isHasStudyLangSubtitle()) {
                int index = 1;
                SubtitleLanguageModel subtitleLanguage1 = new SubtitleLanguageModel(index, EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(), true,true);
                subtitleLanguageModels.add(subtitleLanguage1);
            }
        }
        return subtitleLanguageModels;
    }

    public static List<SubtitleEncodingModel> getSubtitleEncodingList(String value) {
        List<SubtitleEncodingModel> data = new ArrayList<>();
        boolean isSelected = false;
        final String[] group = Constant.PLAYER.OPTION.SUBTITLE.ENCODING.GROUP;
        final String[][] items = Constant.PLAYER.OPTION.SUBTITLE.ENCODING.ITEMS;
        for (int i = 0; i < group.length; i++) {
            data.add(new SubtitleEncodingModel(group[i], true));
            for (String item : items[i]) {
                if (isSelected) {
                    data.add(new SubtitleEncodingModel(item));
                } else {
                    isSelected = item.equals(value);
                    data.add(new SubtitleEncodingModel(item, false, isSelected));
                }
            }
        }
        return data;
    }

    public static String getLanguageName(int code) {
        return EnumLanguage.getNameByIdApi(code);
    }

    public static long getStartTimeOfFirstDialogWithBufferTime(long startTime) {
        long bufferTime = 2000;
        long startTimeWithBuffer = startTime - bufferTime;
        if (startTimeWithBuffer <= 0)
            startTimeWithBuffer = 0;
        return startTimeWithBuffer;
    }

    public static long getEndTimeOfFirstDialogWithBufferTime(long endTime, long duration) {
        long bufferTime = 500;
        long endTimeWithBuffer = endTime + bufferTime;
        if (endTimeWithBuffer > duration)
            endTimeWithBuffer = duration;
        return endTimeWithBuffer;
    }

    public static DicModel getNextDicModel(List<DicModel> list, int index) {
        DicModel nextDicModel = null;
        if (index < list.size() - 1) {
            nextDicModel = getDicModel(list, index + 1);
        }
        return nextDicModel;
    }

    public static DicModel getDicModel(List<DicModel> list, int index) {
        if (index < 0)
            return null;

        if (isHasSubtitle(list) && Utils.isIndexInsideRange(list, index)) {
            return list.get(index);
        }
        return null;
    }

    public static boolean isHasSubtitle(List<DicModel> list) {
        return !Utils.isEmptyCollection(list);
    }
}
