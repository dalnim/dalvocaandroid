package com.dalread;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.model.DIC_HANJA;
import com.dalread.model.VocaKnowAndBookmarkList;
import com.dalread.network.AraHanjaApi;
import com.dalread.network.AraHanjaApiImpl;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Voca;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by JetVHS on 2/21/2017.
 */
public class AraHanjaApplication extends BaseApplication {
    private AraHanjaApi araHanjaApi;
    private AraHanjaApiImpl araHanjaApiImpl;
    @Override
    public RealmConfiguration onInitRealmConfiguration() {
        final int currentDatabaseVersion = mSharedPref.getDatabaseVersion();
        final int newDatabaseVersion = BuildConfig.DB_VERSION;

        final RealmConfiguration newConfig = buildRealmConfiguration(newDatabaseVersion);
//        final RealmConfiguration newConfig = buildRealmConfiguration(newDatabaseVersion, currentDatabaseVersion);
        if (currentDatabaseVersion == 0) { // fresh install
            // copy the realm file from the assets folder
            copyAssetFile(getApplicationContext(), newConfig);
        } else if (currentDatabaseVersion < newDatabaseVersion) { // update required
            restoreDataAndCopyAssetFile(currentDatabaseVersion, newConfig);
        }
        mSharedPref.setDatabaseVersion(newDatabaseVersion);
        return newConfig;
    }

//    @Override
//    public void initData() {
//        araHanjaApi = retrofit.create(AraHanjaApi.class);
//        araHanjaApiImpl = new AraHanjaApiImpl(this);
//    }

    @Override
    public void initData() {

    }

    @Override
    protected void createAllBaseFolder() {
        DLog.d("", "createDalPlayerFolder");
        super.createAllBaseFolder();
    }

    @Override
    public void buildRetrofit(String baseUrl) {
        araHanjaApi = retrofit.create(AraHanjaApi.class);
        araHanjaApiImpl = new AraHanjaApiImpl(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
//        ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
//        if(cm.hasPrimaryClip() == false){
//            return;
//        }
//
//        ClipData clip = cm.getPrimaryClip();
//        ClipData.Item item = clip.getItemAt(0);
//        if (item != null) {
//            //@Huy. Could you add a dialog to ask user to open HanjaConvertActivity or not.
//            //Title : "There are Chinese characters in the clipboard. Do you want to parse them?"
//            String str = item.getText().toString();
//            if (Voca.containChineseCharacters(str)) {
//                Intent intent = new Intent(this, HanjaConvertActivity.class);
//                intent.putExtra(Constant.BUNDLE.KEY_HANJA_DATA, str);
//                startActivity(intent);
//            }
//        }
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

    private RealmConfiguration buildRealmConfiguration(int version) {
        return new RealmConfiguration.Builder()
                .name(Constant.ARAHANJA.ASSET_DATABASE_NAME)
                .schemaVersion(version)
                .allowWritesOnUiThread(true)
                .build();
    }

    //Don't use this yet. (Can't restore old bookmark and copy new Realm with this code)
    private RealmConfiguration buildRealmConfiguration(int newDatabaseVersion, int currentDatabaseVersion) {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder()
                .name(Constant.ARAHANJA.ASSET_DATABASE_NAME)
                .schemaVersion(newDatabaseVersion)
                .allowWritesOnUiThread(true);
        if (currentDatabaseVersion == 0) {
            builder.deleteRealmIfMigrationNeeded();
        } else if (currentDatabaseVersion < newDatabaseVersion) {
            builder.migration((realm, oldVersion, newVersion) -> {
                RealmSchema schema = realm.getSchema();
                RealmObjectSchema webDictionaryModel = schema.get("WebDictionaryModel");
                if (webDictionaryModel == null) {
                    webDictionaryModel = schema.create("WebDictionaryModel");
                    webDictionaryModel.addField("id", long.class, FieldAttribute.PRIMARY_KEY);
                    webDictionaryModel.addField("title", String.class);
                    webDictionaryModel.addField("url", String.class);
                    webDictionaryModel.addField("studyLanguage", int.class);
                    webDictionaryModel.addField("motherTongue", int.class);
                    webDictionaryModel.addField("index", int.class);
                    oldVersion++;
                }
//                callBackup(newDatabaseVersion, currentDatabaseVersion);
            });
        }
        return builder.build();
    }

    private void restoreDataAndCopyAssetFile(int currentDatabaseVersion, RealmConfiguration newConfig) {
        final RealmConfiguration oldConfig = buildRealmConfiguration(currentDatabaseVersion);
//            final RealmConfiguration oldConfig = buildRealmConfiguration(currentDatabaseVersion, currentDatabaseVersion);
        // backup data from the old realm file
        VocaKnowAndBookmarkList vocaKnowAndBookmarkList = backupData(Realm.getInstance(oldConfig));
        // delete the old realm file
        Realm.deleteRealm(oldConfig);

        // copy the new one from the assets folder
        copyAssetFile(getApplicationContext(), newConfig);
        // restore data to the new realm file
        restoreData(Realm.getInstance(newConfig), vocaKnowAndBookmarkList);
    }

    private void copyAssetFile(Context context, RealmConfiguration config) {
        IOException exceptionWhenClose = null;
        File realmFile = new File(config.getRealmDirectory(), config.getRealmFileName());
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = context.getAssets().open(config.getRealmFileName());
            if (inputStream == null) {
                throw new IOException("Could not open asset file: " + config.getRealmFileName());
            }
            outputStream = new FileOutputStream(realmFile);
            byte[] buf = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > -1) {
                outputStream.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            // Handle IO exceptions somehow
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    exceptionWhenClose = e;
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Ignores this one if there was an exception when close inputStream.
                    if (exceptionWhenClose == null) {
                        exceptionWhenClose = e;
                    }
                }
            }
        }

        // No other exception has been thrown, only the exception when close. So, throw it.
        if (exceptionWhenClose != null) {
            throw new RuntimeException(exceptionWhenClose);
        }
    }

    private VocaKnowAndBookmarkList backupData(Realm realm) {
        DLog.i("AraHanjaApplication", "DIC_HANJA old size = " + realm.where(DIC_HANJA.class).count());
        VocaKnowAndBookmarkList vocaKnowAndBookmarkList = Voca.getVocaKnowAndVocaKnowpronounceAndBookmark(realm);
        realm.close();
        return vocaKnowAndBookmarkList;
    }

    private void restoreData(Realm realm, VocaKnowAndBookmarkList vocaKnowAndBookmarkList) {
        DLog.i("AraHanjaApplication", "DIC_HANJA new size = " + realm.where(DIC_HANJA.class).count());
        Voca.updateVocaKnowAndVocaKnowpronounceAndBookmark(realm, vocaKnowAndBookmarkList);
//        realm.close();
    }

    public AraHanjaApi getAraHanjaApi() {
        return araHanjaApi;
    }

    public void setAraHanjaApi(AraHanjaApi araHanjaApi) {
        this.araHanjaApi = araHanjaApi;
    }

    public AraHanjaApiImpl getAraHanjaApiImpl() {
        return araHanjaApiImpl;
    }

    public void setAraHanjaApiImpl(AraHanjaApiImpl araHanjaApiImpl) {
        this.araHanjaApiImpl = araHanjaApiImpl;
    }
}
