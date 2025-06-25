package com.dalread.util;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//BaseStorageUtil과 비슷한데 여기는 File에 관련된 내용을 주로 담는다. BaseStorageUtil에도 비슷한 것들이 있긴 있음. 나중에는 하나로 합치던지 해야 할거 같음. 아님 이건 File자체에만 해당되는 코드만 둔다던지...
public class BaseFileUtil {
    private static final String TAG = "BaseFileUtil";
    public static String getFileExtension(String fileName) {
        if (Utils.isEmpty(fileName))
            return "";
        int i = fileName.lastIndexOf('.');
        if (i > 0)
            return fileName.substring(i + 1);
        return "";
    }
    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static boolean isFileExist(String filePath) {
        File f = new File(filePath);
        if(f.exists()) {
            return true;
        }
        return false;
    }

    public static File getVoiceFileOnLocal(File voiceFolder, String fileName) {
        if (voiceFolder != null) {
            File folder = new File(voiceFolder, BaseFileUtil.getVoiceSubFolderPath(fileName));// (voiceFolder.getPath() + File.separator + getVoiceSubFolderPath(fileName));
            if (folder.exists() || folder.mkdirs()) {
                return new File(folder, fileName);
            }
        }
        return null;
    }

    public static boolean isFileExist(String filePath, String fileName) {
        File f = new File(filePath, fileName);
        if(f.exists()) {
            return true;
        }
        return false;
    }
    public static String getVoiceFileWithPathInVoiceFolder(Context context, String fileName) {
        return BaseVoca.getVoiceFolderInApp(context) + File.separator + BaseFileUtil.getVoiceSubFolderPath(fileName) + fileName;
    }
    public static boolean isVoiceFileExistInVoiceFolder(Context context, String fileName) {
        return BaseFileUtil.isFileExist(getVoiceFileWithPathInVoiceFolder(context, fileName));
    }
    public static boolean isVoiceFileExist(String filePath, String fileName) {
        String voiceFileWithParentPath = getVoiceSubFolderPath(fileName);
        File f = new File(filePath, voiceFileWithParentPath + fileName);
        if(f.exists()) {
            return true;
        }
        return false;
    }

    public static boolean saveTextToFile(String filePath, String text) {
        File file = new File(filePath);
        boolean isWritable = file.exists() ? file.canWrite() : file.getParentFile().canWrite();
        if (!isWritable) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

//
//    public static boolean saveTextToFile(String filePath, String text) {
//        boolean result = true;
//        File file = new File(filePath);
//        if (file.exists()) {
//            if (!file.canWrite()) {
//                result = false;
//            }
//        } else {
//            try {
//                if (!file.createNewFile()) {
//                    result = false;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                result = false;
//            }
//        }
//
//        if (result) {
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//                writer.write(text);
//            } catch (IOException e) {
//                e.printStackTrace();
//                result = false;
//            }
//        }
//        return result;
//    }

    public static String getVoiceSubFolderPath(String fileName) {
        try {
            String[] split = fileName
                    .split("\\.")[0]
                    .split("_");
            return split[0] + "/" + split[3] + "/";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getVocaType_IdFromFileName(String fileName) {
        try {
            String[] split = fileName
                    .split("\\.")[0]
                    .split("_");
            return split[1] + "_" + split[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static File getTempVoiceRecordFileInCacheDir(Context context) {
        return new File(BaseStorageUtil.getTempRecordFilePathInCacheDir(context));
    }

    public static File getInternalCacheDir(Context context) {
        return context.getCacheDir() ;
    }

    public static File getRecordInternalCacheDir(Context context) {
        return new File(getInternalCacheDir(context), Constant.FOLDER_ARAONE.TEMP);
    }




}
