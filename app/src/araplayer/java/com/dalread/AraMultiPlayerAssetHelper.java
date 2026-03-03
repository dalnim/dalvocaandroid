package com.dalread;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.DicSentenceSubDatabase;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.database.sqlite.model.MultiPlayerVideoAbRepeatModel;
import com.dalread.database.sqlite.model.MultiPlayerVideoListInScreenModel;
import com.dalread.database.sqlite.model.MultiPlayerVideoModel;
import com.dalread.database.sqlite.model.MultiPlayerVideoStoredModel;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;

import java.util.List;

public class AraMultiPlayerAssetHelper extends AbstractAssetHelper {
    final int multiPlayerNewDicDbVer = 3; //사전 Db버전. 이 값을 올려주면 기존 DB의 유저데이타는 백업받고 새로운 DB로 갈아치운다.
    public AraMultiPlayerAssetHelper(SharedPreferencesDB sharedPreferencesDB, Context context) {
        super(sharedPreferencesDB, context);
    }

    @Override
    protected void setNewDicDbVer() {
        newDicDbVer = multiPlayerNewDicDbVer;
    }

    public void initFileNames() {
        dicZipDbFileNameInAsset = Constant.ARAMULTIPLAYER.ASSET_DATABASE_NAME;
        dicDbDestPathWithFileName = BaseStorageUtil.getAraMultiPlayerDBPathWithFileName(context);
//        dicZipDbDestPathWithFileName = BaseStorageUtil.getAraPlayerZipDicDBPath(context);
    }

    protected void copyAsset(boolean isOverwrite) {
//        String fileNameInAsset = Constant.ARAMULTIPLAYER.ASSET_DATABASE_NAME;
//        String destPathWithFileName = BaseStorageUtil.getAraMultiPlayerDBPathWithFileName(context);

        BaseStorageUtil.copyFileAssetsToExternalStorageModifyFileName(context, dicZipDbFileNameInAsset, dicDbDestPathWithFileName);
    }

    protected void restoreUserDataAndCopyAsset() {
        Loading.show(context);
        if (FileUtil.isFileExist(dicDbDestPathWithFileName)) {
            MultiPlayerDatabase multiPlayerDatabase = MultiPlayerDatabase.getInstance(context, dicDbDestPathWithFileName);
            // DB가 이전 버전이면, 기존 레코드의 값을 복사해서 변수에 담고, 기존 DB는 지운다.
            List<MultiPlayerVideoModel> list = multiPlayerDatabase.getCurrentScreenModels();
            List<MultiPlayerVideoModel> backupList = multiPlayerDatabase.getVideoMetaModels();
            List<MultiPlayerVideoAbRepeatModel> abRepeatList = multiPlayerDatabase.getAbRepeatFromLegacyTable();
            List<MultiPlayerVideoStoredModel> storedModelList = multiPlayerDatabase.getAllScreenStoredLayout();
            List<MultiPlayerVideoListInScreenModel> videoListInScreen = multiPlayerDatabase.getAllVideoListInScreen();
            multiPlayerDatabase.close();
            removeOldFile();

            // 앱에서 MultiPlayerDatabaseHelper로 빈 DB 생성 후 기존 데이터 복원
            MultiPlayerDatabase multiPlayerDatabaseNew = MultiPlayerDatabase.getInstance(context, dicDbDestPathWithFileName);
            multiPlayerDatabaseNew.insertRecordsIntoNewDatabase(list);
            multiPlayerDatabaseNew.insertDicScreenBackupRecordsIntoNewDatabase(backupList);
            multiPlayerDatabaseNew.insertDicScreenAbRepeatRecordsIntoNewDatabase(abRepeatList);
            multiPlayerDatabaseNew.insertDicScreenStoredLayoutRecordsIntoNewDatabase(storedModelList);
            multiPlayerDatabaseNew.insertVideoListInScreenRecords(videoListInScreen);
            multiPlayerDatabaseNew.close();
        }
        // DB 파일이 없으면 asset 복사하지 않음. 최초 getInstance() 시 MultiPlayerDatabaseHelper가 빈 DB 생성.
        Loading.hide();
    }

    private DicSentenceSubDatabase getDicDb() {
        return DicSentenceSubDatabase.getInstance(context, dicDbDestPathWithFileName);
    }

    @Override
    protected void removeOldFile() {
        BaseStorageUtil.removeFile(dicDbDestPathWithFileName);
    }

}
