package com.dalread.helper;

import android.content.Context;

import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.util.BaseStorageUtil;

public class MultiplePlayerDbHelper {
    private Context context;
    private MultiPlayerDatabase multiPlayerDatabase;
    public MultiplePlayerDbHelper(Context context) {
        this.context = context;
    }
    public MultiPlayerDatabase initSubDatabase(MultiPlayerDatabase inDb) {
        String databasePath = BaseStorageUtil.getAraMultiPlayerDBPathWithFileName(context);
        if (inDb != null) {
            inDb.close();
        }
        this.multiPlayerDatabase = MultiPlayerDatabase.getInstance(context, databasePath);
        return multiPlayerDatabase;
    }

    public void handleFileDelete(String filePath) {
        multiPlayerDatabase.handleFileDelete(filePath);
    }

    public void handleFilePathRename(String oldFilePath, String newFilePath) {
        multiPlayerDatabase.handleFilePathRename(oldFilePath, newFilePath);
    }
}
