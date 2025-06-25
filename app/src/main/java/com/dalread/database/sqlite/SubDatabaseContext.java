package com.dalread.database.sqlite;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.dalread.util.DLog;

import java.io.File;

public class SubDatabaseContext extends ContextWrapper {

    private static final String DEBUG_CONTEXT = "DatabaseContext";
    private Context context;

    public SubDatabaseContext(Context base) {
        super(base);
        this.context = base;
    }

    @Override
    public File getDatabasePath(String path)  {
//        String path = StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(context, name.toLowerCase());
//        if (!path.endsWith(Constant.PLAYER.DATABASE.SQLITE)) {
//            path += Constant.PLAYER.DATABASE.SQLITE ;
//        }

        File result = new File(path);

        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }
        DLog.d(DEBUG_CONTEXT, "getDatabasePath(" + path + ") = " + result.getAbsolutePath());

        return result;
    }

    /* this version is called for android devices >= api-11. thank to @damccull for fixing this. */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return openOrCreateDatabase(name,mode, factory);
    }

    /* this version is called for android devices < api-11 */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        DLog.d(DEBUG_CONTEXT, "openOrCreateDatabase(" + name + ",,) = " + result.getPath());
        return result;
    }
}
