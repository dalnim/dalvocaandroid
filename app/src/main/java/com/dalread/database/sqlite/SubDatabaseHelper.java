package com.dalread.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dalread.BuildConfig;
//이건 현재 특빌히 하는 일이 없는 클래스이다.
public class SubDatabaseHelper extends SQLiteOpenHelper {
    protected Context context;
    protected String name;
    public SubDatabaseHelper(Context context, String name) {
        super(new SubDatabaseContext(context), name, null, BuildConfig.DB_VERSION);
        this.context = context;
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}