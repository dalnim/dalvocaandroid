package com.dalread;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.util.BaseStorageUtil;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;

import io.realm.RealmConfiguration;

/**
 * Created by JetVHS on 2/21/2017.
 */
public class AraHanjaApplication extends BaseApplication {
    @Override
    public RealmConfiguration onInitRealmConfiguration() {
        final int currentDatabaseVersion = mSharedPref.getDatabaseVersion();
        final int newDatabaseVersion = BuildConfig.DB_VERSION;

        if (currentDatabaseVersion == 0) { // fresh install
            // copy the database file from the assets folder
            copyAssetFile(getApplicationContext());
        } else if (currentDatabaseVersion < newDatabaseVersion) { // update required
            copyAssetFile(getApplicationContext());
        }
        mSharedPref.setDatabaseVersion(newDatabaseVersion);
        return null;
    }

    @Override
    public void initData() {

    }

    @Override
    public void buildRetrofit(String baseUrl) {

    }

    @Override
    protected void createAllBaseFolder() {
        DLog.d("", "createDalPlayerFolder");
        super.createAllBaseFolder();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    private void copyAssetFile(Context context) {
        String fileNameInAsset = LanguageUtil.getSQLFileNameForAraConvInAsset(this);
        String destPathWithFileName = BaseStorageUtil.getAraKoicaDBPathWithFileName(context);
        BaseStorageUtil.copyFileAssetsToExternalStorageModifyFileName(context, fileNameInAsset, destPathWithFileName);
    }
}
