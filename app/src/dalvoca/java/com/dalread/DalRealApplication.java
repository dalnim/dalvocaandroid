package com.dalread;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.realm.RealmConfiguration;

/**
 * Created by JetVHS on 2/21/2017.
 */
public class DalRealApplication extends BaseApplication {

    @Override
    public RealmConfiguration onInitRealmConfiguration() {
        return new RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .schemaVersion(BuildConfig.DB_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    @Override
    public void initData() {

    }

    @Override
    public void buildRetrofit(String baseUrl) {

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

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
}
