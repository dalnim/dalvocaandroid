// FileUtil.java
package com.araonesoft.dalstttest;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WhisperFileUtil {
  public static String copyAssetToInternalStorage(Context context, String assetPath, String outputFilename) throws IOException {
    AssetManager assetManager = context.getAssets();
    File outFile = new File(context.getFilesDir(), outputFilename);

    try (InputStream in = assetManager.open(assetPath);
         FileOutputStream out = new FileOutputStream(outFile)) {
      byte[] buffer = new byte[4096];
      int read;
      while ((read = in.read(buffer)) != -1) {
        out.write(buffer, 0, read);
      }
      out.flush();
    }

    return outFile.getAbsolutePath();
  }
}
