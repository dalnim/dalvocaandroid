package com.dalread;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.dalread.base.EnumFlavor;
import com.dalread.network.AraPlayerApi;
import com.dalread.network.AraPlayerApiImpl;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.StorageUtil;

import io.realm.RealmConfiguration;

public class AraPlayerApplication extends BaseApplication implements LifecycleObserver {

    private AraPlayerApi araPlayerApi;
    private AraPlayerApiImpl araPlayerApiImpl;
    private boolean isBackground = false;
//    public static int darkLightTheme;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this); //어디서 이걸 쓰지? 아마 아라플레이어가 백그라운드일때도 계속 플레이되게 하는거 같은데...
    }

    @Override
    public RealmConfiguration onInitRealmConfiguration() {
        return new RealmConfiguration.Builder()
                .name(EnumFlavor.ARAPLAYER.getName())
                .schemaVersion(BuildConfig.DB_VERSION)
                .migration(new AraPlayerRealmMigration()) // 마이그레이션을 처리하는 MyMigration 클래스를 설정합니다.
                .allowWritesOnUiThread(true)
                .build();
    }

//    @Override
//    public void initData() {
//        araPlayerApi = retrofit.create(AraPlayerApi.class);
//        araPlayerApiImpl = new AraPlayerApiImpl(this);
//    }

    @Override
    public void initData() {
    }

    @Override
    protected void createAllBaseFolder() {
//        super.createAllBaseFolder(); //Why didn't call this?
        AbstractAssetHelper helper;
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            helper = new AraMultiPlayerAssetHelper(mSharedPref, this);
            helper.manageAsset();
        } else {
            helper = new AraPlayerAssetHelper(mSharedPref, this);
            helper.manageAsset();
            StorageUtil.deleteBaseAndRelatedFolder(this);
        }
        StorageUtil.createAppRelatedFolder(this); //여기서 안해도 설치시 다시 불리는데?
    }

    @Override
    public void buildRetrofit(String baseUrl) {
        araPlayerApi = retrofit.create(AraPlayerApi.class);
        araPlayerApiImpl = new AraPlayerApiImpl(this);
    }


    public AraPlayerApi getAraPlayerApi() {
        return araPlayerApi;
    }

    public void setAraPlayerApi(AraPlayerApi araPlayerApi) {
        this.araPlayerApi = araPlayerApi;
    }

    public AraPlayerApiImpl getAraPlayerApiImpl() {
        return araPlayerApiImpl;
    }

    public void setAraPlayerApiImpl(AraPlayerApiImpl araPlayerApiImpl) {
        this.araPlayerApiImpl = araPlayerApiImpl;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        isBackground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        if (isBackground) {
            mSharedPref.setShowNormalVideoFileList(true);
            isBackground = false;
        }
    }

//    public static boolean setDarkLightTheme(int selectedDarkLightTheme) {
//        boolean isChange = false;
//        if (darkLightTheme != selectedDarkLightTheme) {
//            isChange = true;
//            darkLightTheme = selectedDarkLightTheme;
//            AppCompatDelegate.setDefaultNightMode(selectedDarkLightTheme);
//        }
//        return isChange;
//    }

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
