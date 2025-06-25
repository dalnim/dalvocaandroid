package com.dalread.database.sqlite;

import android.content.Context;

import com.dalread.util.BaseStorageUtil;

//컴파일 에러 방지용
public class SubDatabase extends DicSentenceSubDatabase {

    private static SubDatabase instance;
    public SubDatabase(Context context, String path) {
        super(context, path);
    }

    public static SubDatabase getInstance(Context contextTemp, String path) {
        context = contextTemp;
        if (instance == null || subPath == null || !subPath.equals(path))
            instance = new SubDatabase(context, path);
        return instance;
    }

    public static SubDatabase getDicDatabaseInstance(Context context) {
        String destPathWithFileName = BaseStorageUtil.getAraHanjaDBPath(context);
        return getInstance(context, destPathWithFileName);
    }
}
