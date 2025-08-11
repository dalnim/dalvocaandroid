package com.araonesoft.dalstttest;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class WhisperUtils {
  public static boolean isArmEabiV7a() {
    return Build.SUPPORTED_ABIS[0].equals("armeabi-v7a");
  }

  public static boolean isArmEabiV8a() {
    return Build.SUPPORTED_ABIS[0].equals("arm64-v8a");
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public static String cpuInfo() {
    try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      return sb.toString();
    } catch (IOException e) {
      Log.e("WhisperUtils", "Error reading cpuinfo", e);
      return null;
    }
  }

  public static byte[] readFileToBytes(String filePath) throws IOException {
    File file = new File(filePath);
    FileInputStream fis = new FileInputStream(file);
    byte[] buffer = new byte[(int) file.length()];
    int read = fis.read(buffer);
    fis.close();
    return buffer;
  }
}