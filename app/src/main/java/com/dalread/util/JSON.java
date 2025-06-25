package com.dalread.util;

import android.content.Context;

import java.io.InputStream;

public class JSON {

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            DLog.i("loadJSONFromAsset", is.read(buffer) + "bytes");
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            json = "";
        }
        return json;
    }
}
