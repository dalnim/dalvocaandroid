package com.araonesoft.dalstttest;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CpuInfo {
  private static final String LOG_TAG = "CpuInfo";

  @RequiresApi(api = Build.VERSION_CODES.N)
  public static int getHighPerfCpuCount() {
    try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
      String line;
      int highPerfCpuCount = 0;
      Pattern pattern = Pattern.compile("cpu\\s+(\\d+)");
      
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("processor")) {
          Matcher matcher = pattern.matcher(line);
          if (matcher.find()) {
            int cpuId = Integer.parseInt(matcher.group(1));
            if (isHighPerfCpu(cpuId)) {
              highPerfCpuCount++;
            }
          }
        }
      }
      
      return Math.max(highPerfCpuCount, 1);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Error reading cpuinfo", e);
      return 2; // 기본값
    }
  }

  private static boolean isHighPerfCpu(int cpuId) {
    // 간단한 구현: 짝수 CPU를 고성능으로 간주
    // 실제로는 더 복잡한 로직이 필요할 수 있음
    return cpuId % 2 == 0;
  }
}
