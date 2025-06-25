package com.dalread;

import android.app.Activity;
import android.content.Context;

import com.dalread.model.VocaDownload;
import com.dalread.network.AraConvApi;
import com.dalread.network.AraConvApiImpl;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.FileUtil;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;


import java.io.File;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by Dalnim on 2/2/2022.
 */
public class AraHangulApplication extends BaseApplication {
    private AraConvApi araHanjaApi;
    private AraConvApiImpl araHanjaApiImpl;
    //AraConv doesn't use Realm but SQLite.
    @Override
    public RealmConfiguration onInitRealmConfiguration() {
        final int currentDatabaseVersion = mSharedPref.getDatabaseVersion();
        final int newDatabaseVersion = BuildConfig.DB_VERSION;

        final RealmConfiguration newConfig = buildRealmConfiguration(newDatabaseVersion);
        if (currentDatabaseVersion == 0) { // fresh install
            // copy the database file from the assets folder
            copyAssetFile(getApplicationContext());
        } else if (currentDatabaseVersion < newDatabaseVersion) { // update required
            copyAssetFile(getApplicationContext());
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

//    @Override
//    protected void createAllBaseFolder() {
//        DLog.d("", "createDalPlayerFolder");
//        super.createAllBaseFolder();
//        BaseStorageUtil.createAllBaseFolder(this);
//    }

    @Override
    public void buildRetrofit(String baseUrl) {
        araHanjaApi = retrofit.create(AraConvApi.class);
        araHanjaApiImpl = new AraConvApiImpl(this);
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

    private RealmConfiguration buildRealmConfiguration(int version) {
        return new RealmConfiguration.Builder()
                .name(Constant.ARAHANJA.ASSET_DATABASE_NAME)
                .schemaVersion(version)
                .allowWritesOnUiThread(true)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
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
                    }
                })
                .build();
    }

    private void copyAssetFile(Context context) {
        Utils.initStudyLanguage(context);
        copyAssetDatabase(context);
        copyAssetVoiceFiles(context);
    }

    private void copyAssetDatabase(Context context) {
        String fileNameInAsset = LanguageUtil.getSQLFileNameForAraConvInAsset(this);
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(context);
        BaseStorageUtil.copyFileAssetsToExternalStorageModifyFileName(context, fileNameInAsset, destPathWithFileName);
    }

    private void copyAssetVoiceFiles(Context context) {
        String fileNameInAsset = LanguageUtil.getVoiceFileNameForAraConvInAsset(this);
        File voiceFolderInApp = BaseVoca.getVoiceFolderInApp(context);
        String destPathWithFileName = voiceFolderInApp.getPath() + File.separator + fileNameInAsset;
        BaseStorageUtil.copyFileAssetsToExternalStorageModifyFileName(context, fileNameInAsset, destPathWithFileName);
        BaseStorageUtil.unzip(destPathWithFileName, voiceFolderInApp.getPath());
    }

    public AraConvApi getAraHanjaApi() {
        return araHanjaApi;
    }

    public void setAraHanjaApi(AraConvApi araHanjaApi) {
        this.araHanjaApi = araHanjaApi;
    }

    public AraConvApiImpl getAraHanjaApiImpl() {
        return araHanjaApiImpl;
    }

    public void setAraHanjaApiImpl(AraConvApiImpl araHanjaApiImpl) {
        this.araHanjaApiImpl = araHanjaApiImpl;
    }

    protected void initVoiceRecordingFileListInRealm() {
        Realm realm = Realm.getDefaultInstance();
        final VocaDownload vocaDownload = realm.where(VocaDownload.class).findFirst();
        if (vocaDownload != null) return;
        String fileNameInAsset = FileUtil.getVoiceDownloadListFileNameInAsset(this);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<VocaDownload> vocaDownloadList = FileUtil.getVoiceDownloadListInAsset(getBaseContext(), fileNameInAsset);
                for (VocaDownload item : vocaDownloadList) {
                    createVocaDownloadItem(realm, item.getName(), item.getVersion(), item.getFileSize());
                }
            }
        });
        realm.close();
    }

    private void createVocaDownloadItem(Realm realm, final String name, final int version, final int fileSize) {
        VocaDownload vocaDownload = new VocaDownload();// realm.createObject(VocaDownload.class);
        vocaDownload.setName(name);
        vocaDownload.setVersion(version);
        vocaDownload.setFileSize(fileSize);
        realm.copyToRealmOrUpdate(vocaDownload);
    }

}
