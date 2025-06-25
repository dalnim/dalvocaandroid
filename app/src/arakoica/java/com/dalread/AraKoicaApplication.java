package com.dalread;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.network.AraKoicaApi;
import com.dalread.network.AraKoicaApiImpl;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;

import io.realm.RealmConfiguration;

public class AraKoicaApplication extends BaseApplication {
    private AraKoicaApi araKoicaApi;
    private AraKoicaApiImpl araKoicaApiImpl;
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
        araKoicaApi = retrofit.create(AraKoicaApi.class);
        araKoicaApiImpl = new AraKoicaApiImpl(this);
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
        String fileNameInAsset = LanguageUtil.getSQLFileNameForAraKoicaInAsset(this);
        String destPathWithFileName = BaseStorageUtil.getAraKoicaDBPathWithFileName(context);
        BaseStorageUtil.copyFileAssetsToExternalStorageModifyFileName(context, fileNameInAsset, destPathWithFileName);
    }

    public AraKoicaApi getAraKoicaApi() {
        return araKoicaApi;
    }

    public void setAraKoicaApi(AraKoicaApi araKoicaApi) {
        this.araKoicaApi = araKoicaApi;
    }

    public AraKoicaApiImpl getAraKoicaApiImpl() {
        return araKoicaApiImpl;
    }

    public void setAraKoicaApiImpl(AraKoicaApiImpl araKoicaApiImpl) {
        this.araKoicaApiImpl = araKoicaApiImpl;
    }
}
