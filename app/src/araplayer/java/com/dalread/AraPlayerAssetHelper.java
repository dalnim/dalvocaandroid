package com.dalread;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.DicSentenceSubDatabase;
import com.dalread.database.sqlite.model.UserDicEngModel;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.StorageUtil;

import java.util.List;

public class AraPlayerAssetHelper extends AbstractAssetHelper {
    final int araPlayerNewDicDbVer = 4; //사전 Db버전. 이 값을 올려주면 기존 DB의 유저데이타는 백업받고 새로운 DB로 갈아치운다.
    public AraPlayerAssetHelper(SharedPreferencesDB sharedPreferencesDB, Context context) {
        super(sharedPreferencesDB, context);
    }

    @Override
    protected void setNewDicDbVer() {
        newDicDbVer = araPlayerNewDicDbVer;
    }

    public void initFileNames() {
        dicZipDbFileNameInAsset = Constant.PLAYER.ASSET_DATABASE_NAME_ENG_ZIP;
        dicDbDestPathWithFileName = BaseStorageUtil.getAraPlayerDicDBPath(context);
        dicZipDbDestPathWithFileName = BaseStorageUtil.getAraPlayerZipDicDBPath(context);
    }
    //압축파일을 푸는 로직도 추가해야 한다.
    protected void restoreUserDataAndCopyAsset() {
        Loading.show(context,"새로운 DB를 설치중입니다.");
        if (FileUtil.isFileExist(dicDbDestPathWithFileName)) {
            DicSentenceSubDatabase dicDb = getDicDb();
            //DB가 이전 버전이면, 기존 레코드의 값을 복사해서 변수에 담고, 기존 DB는 지운다.
            final List<IVocaFullPlayTTSItem> list = dicDb.getUserVocaKnowAndBookmarkList();
            final List<UserDicEngModel> userList = dicDb.getUserDicEngList();
            dicDb.close();
            removeOldFile();
            //asset에서 새로운 DB를 복사한다.
            copyAsset(true);
            //변수에 담은 값을 복원해준다.
            DicSentenceSubDatabase dicDbNew = getDicDb();
            dicDbNew.updateVocaKnowAndBookmarkList(list);
            dicDbNew.updateUserDicEngListInDicTableInDicDbMain(userList);
            dicDbNew.close();
        } else {
            copyAsset(true);
        }
        Loading.hide();
    }

    private DicSentenceSubDatabase getDicDb() {
        return DicSentenceSubDatabase.getInstance(context, dicDbDestPathWithFileName);
    }
    protected void copyAsset(boolean isOverwrite) {
//        BaseStorageUtil.copyFileAssetsToExternalStorageModifyFileName(context, dicDbFileNameInAsset, dicDbDestPathWithFileName);
        //Asset의 압축파일을 dest폴더에 압축파일로 복사한다.
        BaseStorageUtil.copyFileAssetsToExternalStorageOrOverwrite(context, dicZipDbFileNameInAsset, dicZipDbDestPathWithFileName, isOverwrite);
        //복사된 압축파일을 압축해제한다.
        StorageUtil.unzipFileAndDeleteZipFile(dicZipDbDestPathWithFileName);
        //새로운 DB가 들어왔으므로 버전을 올려둔다.
        incrementAndStoreDbVer();
    }
    @Override
    protected void removeOldFile() {
        BaseStorageUtil.removeFile(dicDbDestPathWithFileName);
    }
}
