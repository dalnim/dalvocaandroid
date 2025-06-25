package com.dalread.util;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil extends BaseFileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();;
    private static String fileName = "ocr.jpg";
    public static File saveBitmapToFile(Context context, Bitmap bitmap) {
        if (bitmap == null)
            return null;

        File externalCacheDir = context.getExternalCacheDir();
        File cacheDir = context.getCacheDir();
        File filesDir = context.getFilesDir();
        File imageFile = new File(cacheDir, fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            //PNG로 하면, 파일을 저장하는데 시간이 너무 오래 걸린다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
