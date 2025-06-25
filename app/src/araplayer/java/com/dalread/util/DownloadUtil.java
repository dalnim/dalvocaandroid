package com.dalread.util;

import com.dalread.model.DownloadModel;

public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    public static String getDownloadInfo(DownloadModel file) {
        try {
//        DLog.d(TAG, "getDownloadInfo - file=" + file.toString());
            if (file.getCurrentSize() <= 0 || file.getSize() <= 0 || file.getCurrentSize() > file.getSize()) {
                return Constant.BASE_BLANK;
            }
            double ratio = (file.getCurrentSize() / (double) file.getSize()) * 100;
            return String.format("%.1f", ratio) + "% (" + StorageUtil.getDynamicSpace(file.getCurrentSize()) + " / " +
                    StorageUtil.getDynamicSpace(file.getSize()) + ")";
        } catch (Exception ex) {
            return Constant.BASE_BLANK;
        }
    }
}
